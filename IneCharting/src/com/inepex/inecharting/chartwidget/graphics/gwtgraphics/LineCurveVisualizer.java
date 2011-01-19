package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Path;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.graphics.CurveVisualizer;
import com.inepex.inecharting.chartwidget.graphics.DrawingJobScheduler;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;

/**
 * Line curve implementation using gwt-graphics 
 * @author Miklós Süveges / Inepex Ltd
 */
public final class LineCurveVisualizer extends CurveVisualizer {
	private TreeMap<Point, Line> drawnLines;
	private TreeMap<Point, Path> drawnFills;
	private ArrayList<Point> actualDrawingJob;
	private ModelManager modelManager; 
	private DrawingJobScheduler scheduler;
	private int actualDrawingJobDxInPx = 0;
	
	public LineCurveVisualizer(Widget canvas, Curve curve, ModelManager modelManager) {
		super(canvas, curve);
		this.modelManager = modelManager;
		this.drawnFills = new TreeMap<Point, Path>();
		this.drawnLines = new TreeMap<Point, Line>();
	}

	@Override
	public void drawCurve(double viewportMin, double viewportMax) {
		setViewPort(viewportMin, viewportMax);
	}

	@Override
	public void removeFromCanvas() {
		for(Point point : drawnLines.keySet())
			((DrawingArea)canvas).remove(drawnLines.get(point));
		for(Point point : drawnFills.keySet())
			((DrawingArea)canvas).remove(drawnFills.get(point));
		drawnFills.clear();
		drawnLines.clear();
	}

	@Override
	public void moveViewport(double dx) {
		moveShapes(-dx);
		if(curve.getPolicy().isPreDrawLines())
			return;
		else{
			actualDrawingJobDxInPx = drawnFills.get(drawnFills.firstKey()).getX() - drawnFills.firstKey().getxPos();
			dropShapesOutsideViewPort();
			createActualDrawingJob(modelManager.getViewportMin(), modelManager.getViewportMax());
			if(curve.getPolicy().isDrawPointByPoint()){
				scheduler = new DrawingJobScheduler(this, curve.getPolicy().getDelayBetweenDrawingPoints());
				scheduler.start();
			}
			else{
				for(int i=0; i<actualDrawingJob.size()-1; i++){
					drawLines(actualDrawingJob.get(i), actualDrawingJob.get(i+1), actualDrawingJobDxInPx);
				}
			}			
		}
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		removeFromCanvas();
		createActualDrawingJob(viewportMin, viewportMax);
		if(curve.getPolicy().isDrawPointByPoint()){
			scheduler = new DrawingJobScheduler(this, curve.getPolicy().getDelayBetweenDrawingPoints());
			scheduler.start();
		}
		else{
			for(int i=0; i<actualDrawingJob.size()-1; i++){
				drawLines(actualDrawingJob.get(i), actualDrawingJob.get(i+1), 0);
			}
		}
		moveShapes(modelManager.getxMin() - viewportMin);	
	}

	@Override
	public void drawNextPoint() {
		Point start = getFirstUndrawnPointFromActualJob();
		if(start.equals(null))
			scheduler.stop();
		else
			drawLines(start, actualDrawingJob.get(actualDrawingJob.indexOf(start)+1),actualDrawingJobDxInPx);	
	}
	
	/**
	 * Draws shapes from one point to another, and puts them to the related collections.
	 * If there are already drawn shapes, it takes no action.
	 * @param startPoint
	 * @param endPoint
	 */
	private void drawLines(Point startPoint, Point endPoint, int distanceFromPointsX) {
		if(drawnFills.containsKey(startPoint) || drawnLines.containsKey(startPoint))
			return;
		/* LineCurve */
		Line line = new Line(
				startPoint.getxPos() + distanceFromPointsX, startPoint.getyPos(),
				endPoint.getxPos()+ distanceFromPointsX, endPoint.getyPos());
		line.setStrokeColor(curve.getLineDrawInfo().getStrokeColor());
		line.setStrokeWidth(curve.getLineDrawInfo().getStrokeWidth());
		drawnLines.put(startPoint, line);
		((DrawingArea)canvas).add(line);
		if(curve.getLineDrawInfo().hasFill()){
			/* FillCurve */
			Path path =  new Path(startPoint.getxPos() + distanceFromPointsX,
					modelManager.getChartCanvasHeight());
			path.lineRelativelyTo(0,
					startPoint.getyPos() - modelManager.getChartCanvasHeight());
			path.lineRelativelyTo(endPoint.getxPos() - startPoint.getxPos(),
					endPoint.getyPos() - startPoint.getyPos());
			path.lineRelativelyTo(0,
					modelManager.getChartCanvasHeight() - endPoint.getyPos());
			path.close();
			path.setFillColor(curve.getLineDrawInfo().getFillColor());
			path.setFillOpacity(curve.getLineDrawInfo().getFillOpacity());
			path.setStrokeWidth(0);
			((DrawingArea)canvas).add(path);
			drawnFills.put(startPoint, path);
		}
	}
	/**
	 * 
	 * @return null if all the points from actual job has been drawn
	 */
	private Point getFirstUndrawnPointFromActualJob(){
		for(Point point:actualDrawingJob){
			if(actualDrawingJob.indexOf(point) == actualDrawingJob.size() - 1)
				break;
			if(drawnFills.containsKey(point) || drawnLines.containsKey(point))
				continue;
			else
				return point;
		}
		return null;
	}

	private ArrayList<Point> createActualDrawingJob(double viewportMin, double viewportMax){
		//get the points which over the line will be drawn
 		actualDrawingJob = new  ArrayList<Point>();
		for(Double x:curve.getPointsToDraw().keySet()){
			//in the case of not predrawn lines we only need visible points
			if(!curve.getPolicy().isPreDrawLines() && (x < viewportMin || x > viewportMax))
					continue;
			//check if we have added this point before (in case of multiple imaginary points in a row)
			if(actualDrawingJob.indexOf(curve.getPointsToDraw().get(x)) == -1){
				actualDrawingJob.add(curve.getPointsToDraw().get(x));
			}
		}
		if(!curve.getPolicy().isPreDrawLines()){
			//we need to get invisible points closest to viewport to draw lines 
			double start = curve.getLastInvisiblePointBeforeViewport(viewportMin);
			double end = curve.getFirstInvisiblePointAfterViewport(viewportMax);
			//has point before viewport
			if(!Double.isNaN(start)){
				actualDrawingJob.add(0, new Point(
						(int) modelManager.calculateX(start),
						(int) modelManager.calculateY(curve.getDataMap().get(start), curve.getMinValue(), curve.getMaxValue()),
						true,curve));
			}
			if(!Double.isNaN(end)){
				actualDrawingJob.add(new Point(
						(int) modelManager.calculateX(end),
						(int) modelManager.calculateY(curve.getDataMap().get(end), curve.getMinValue(), curve.getMaxValue()),
						true,curve));
			}
		}
		return actualDrawingJob;
	}

	private void moveShapes(double dx){
		if(drawnLines.size() == 0)
			return;
		int dxInPx = modelManager.calculateDistance(dx);
		
		for(Point point:drawnLines.keySet()){
			Line l = drawnLines.get(point);
			l.setX1(l.getX1() + dxInPx);
			l.setX2(l.getX2() + dxInPx);
		}
		if(drawnFills.size() == 0)
			return;
		for(Point point:drawnFills.keySet()){
			drawnFills.get(point).setX(drawnFills.get(point).getX() + dxInPx);
		}
			
	}

	private void dropShapesOutsideViewPort(){
		Double firstToKeep = curve.getLastInvisiblePointBeforeViewport(modelManager.getViewportMin());
		if(firstToKeep.equals(Double.NaN))
			firstToKeep = modelManager.getViewportMin();
		Double lastToKeep = curve.getFirstInvisiblePointAfterViewport(modelManager.getViewportMax());
		if(lastToKeep.equals(Double.NaN))
			lastToKeep = modelManager.getViewportMax();
		for(Double x : curve.getPointsToDraw().keySet()){
			if(x < firstToKeep || x > lastToKeep){
				drawnFills.remove(curve.getPointsToDraw().get(x));
				drawnLines.remove(curve.getPointsToDraw().get(x));
			}
		}
	}
}
