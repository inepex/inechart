package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.VectorObject;
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
	private TreeMap<Point, VectorObject> drawnLines;
	private TreeMap<Point, Path> drawnFills;
	private ArrayList<Point> actualDrawingJob;
	private ModelManager modelManager; 
	private DrawingJobScheduler scheduler;
	private double totalDX = 0;
	
	public LineCurveVisualizer(Widget canvas, Curve curve, ModelManager modelManager) {
		super(canvas, curve);
		this.modelManager = modelManager;
		this.drawnFills = new TreeMap<Point, Path>();
		this.drawnLines = new TreeMap<Point, VectorObject>();
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
		if(curve.getPolicy().isPreDrawLines()){
			return;
		}
		else{
			if(!curve.getPolicy().isKeepInvisibleGraphicalObjects())
				dropShapesOutsideViewPort();
			createActualDrawingJob(modelManager.getViewportMin(), modelManager.getViewportMax());
			if(curve.getPolicy().isDrawPointByPoint()){
				scheduler = new DrawingJobScheduler(this, curve.getPolicy().getDelayBetweenDrawingPoints());
				scheduler.start();
			}
			else{
				for(int i=0; i<actualDrawingJob.size()-1; i++){
					drawLines(actualDrawingJob.get(i), actualDrawingJob.get(i+1), modelManager.calculateDistance(totalDX));
				}
			}			
		}
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		totalDX = 0;
		removeFromCanvas();
		if(!curve.getPolicy().isPreDrawLines()){
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
		}
		else{
			drawEntireCurve();
		}
		moveShapes(modelManager.getxMin() - viewportMin);	
	}

	private void drawEntireCurve(){
		Path line = null;
		Path fill = null;
		Point previous = curve.getPointsToDraw().get(curve.getPointsToDraw().firstKey());
		if(curve.getLineDrawInfo().hasFill()){
			fill = new Path(
					previous.getxPos(),
					modelManager.getChartCanvasHeight());
		}
		
		for(double x : curve.getPointsToDraw().keySet()){
			Point actual = curve.getPointsToDraw().get(x);
			if(x == curve.getPointsToDraw().firstKey()){
				line = new Path(
						actual.getxPos(),
						actual.getyPos());
				line.setStrokeColor(curve.getLineDrawInfo().getStrokeColor());
				line.setStrokeWidth(curve.getLineDrawInfo().getStrokeWidth());
				if(curve.getLineDrawInfo().hasFill()){
					fill.lineRelativelyTo(
							0, 
							actual.getyPos() - modelManager.getChartCanvasHeight());
					fill.setFillColor(curve.getLineDrawInfo().getFillColor());
					fill.setFillOpacity(curve.getLineDrawInfo().getFillOpacity());
					fill.setStrokeWidth(0);
					fill.setStrokeColor(curve.getLineDrawInfo().getFillColor());
					fill.setStrokeOpacity(0);
				}
			}
			else{
				line.lineRelativelyTo(
						actual.getxPos() - previous.getxPos(),
						actual.getyPos() - previous.getyPos());
				if(curve.getLineDrawInfo().hasFill()){
					fill.lineRelativelyTo(
							actual.getxPos() - previous.getxPos(),
							actual.getyPos() - previous.getyPos());
				}
			}
			previous = actual;
		}
		((DrawingArea)canvas).add(line);
		drawnLines.put(curve.getPointsToDraw().get(curve.getPointsToDraw().firstKey()), line);
		if(curve.getLineDrawInfo().hasFill()){
			fill.lineRelativelyTo(
					0,
					modelManager.getChartCanvasHeight() - previous.getyPos());
			fill.close();
			((DrawingArea)canvas).add(fill);
			drawnFills.put(curve.getPointsToDraw().get(curve.getPointsToDraw().firstKey()), fill);
		}
	}
	
	@Override
	public void drawNextPoint() {
		Point start = getFirstUndrawnPointFromActualJob();
		if(start == null)
			scheduler.stop();
		else
			drawLines(start, actualDrawingJob.get(actualDrawingJob.indexOf(start)+1), modelManager.calculateDistance(totalDX));	
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
			path.setFillColor(curve.getLineDrawInfo().getFillColor());
			path.setFillOpacity(curve.getLineDrawInfo().getFillOpacity());
			path.setStrokeWidth(0);
			path.setStrokeColor(curve.getLineDrawInfo().getFillColor());
			path.setStrokeOpacity(0);
			path.lineRelativelyTo(0,
					startPoint.getyPos() - modelManager.getChartCanvasHeight());
			path.lineRelativelyTo(endPoint.getxPos() - startPoint.getxPos(),
					endPoint.getyPos() - startPoint.getyPos());
			path.lineRelativelyTo(0,
					modelManager.getChartCanvasHeight() - endPoint.getyPos());
			path.close();
			
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
			Double start = curve.getLastInvisiblePointBeforeViewport(viewportMin);
			Double end = curve.getFirstInvisiblePointAfterViewport(viewportMax);
			//has point before viewport
			if(start != null){
				actualDrawingJob.add(0, curve.getPointsToDraw().get(start));
			}
			if(end != null){
				actualDrawingJob.add(curve.getPointsToDraw().get(end));
			}
		}
		return actualDrawingJob;
	}

	private void moveShapes(double dx){
		totalDX += dx;
		if(drawnLines.size() == 0)
			return;
		int dxInPx = modelManager.calculateDistance(totalDX);
		for(Point point:drawnLines.keySet()){
			if(drawnLines.get(point) instanceof Line){
				Line l = (Line) drawnLines.get(point);
				int d = l.getX1() - point.getxPos();
				l.setX1(point.getxPos() + dxInPx);
				l.setX2(l.getX2() - d + dxInPx);
			}
			else{
				((Path)drawnLines.get(point)).setX(point.getxPos() + dxInPx);
			}
		}
		if(drawnFills.size() == 0)
			return;
		for(Point point:drawnFills.keySet()){
			drawnFills.get(point).setX(point.getxPos() + dxInPx);
		}
			
	}

	private void dropShapesOutsideViewPort(){
		Double firstToKeep = curve.getLastInvisiblePointBeforeViewport(modelManager.getViewportMin());
		if(firstToKeep == null)
			firstToKeep = modelManager.getViewportMin();
		Double lastToKeep = modelManager.getViewportMax();
		for(Double x : curve.getPointsToDraw().keySet()){
			if(x < firstToKeep || x > lastToKeep){
				Line line = (Line) drawnLines.get(curve.getPointsToDraw().get(x));
				Path path = drawnFills.get(curve.getPointsToDraw().get(x));
				if(line != null){
					((DrawingArea)canvas).remove(line);
					drawnLines.remove(curve.getPointsToDraw().get(x));
				}
				if(path != null){
					((DrawingArea)canvas).remove(path);
					drawnFills.remove(curve.getPointsToDraw().get(x));
				}
			}
		}
	}



}
