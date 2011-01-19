package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.shape.Ellipse;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.event.PointStateChangeListener;
import com.inepex.inecharting.chartwidget.graphics.CurveVisualizer;
import com.inepex.inecharting.chartwidget.graphics.DrawingJobScheduler;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.model.Point.State;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo;

/**
 *  Point curve implementation using gwt-graphics 
 * @author Miklós Süveges / Inepex Ltd
 */
public final class PointCurveVisualizer extends CurveVisualizer implements PointStateChangeListener {

	private TreeMap<Point, Shape> drawnShapes;
	private ModelManager modelManager;
	private ArrayList<Point> actualDrawingJob;
	private DrawingJobScheduler scheduler;
	private int actualDrawingJobDxInPx = 0;
	private IneChartProperties properties;
	
	public PointCurveVisualizer(Widget canvas, Curve curve, ModelManager modelManager, IneChartProperties properties) {
		super(canvas, curve);
		this.modelManager = modelManager;
		this.properties = properties;
		this.drawnShapes = new TreeMap<Point, Shape>();
	}

	@Override
	public void drawCurve(double viewportMin, double viewportMax) {
		setViewPort(viewportMin, viewportMax);
	}

	@Override
	public void removeFromCanvas() {
		for(Point point : drawnShapes.keySet())
			((DrawingArea)canvas).remove(drawnShapes.get(point));
		drawnShapes.clear();
	}

	@Override
	public void moveViewport(double dx) {
		moveShapes(-dx);
		if(curve.getPolicy().isPreDrawPoints())
			return;
		else{
			actualDrawingJobDxInPx = drawnShapes.get(drawnShapes.firstKey()).getX() - drawnShapes.firstKey().getxPos();
			dropShapesOutsideViewPort();
			createActualDrawingJob(modelManager.getViewportMin(), modelManager.getViewportMax());
			if(curve.getPolicy().isDrawPointByPoint()){
				scheduler = new DrawingJobScheduler(this, curve.getPolicy().getDelayBetweenDrawingPoints());
				scheduler.start();
			}
			else{
				for(Point point : actualDrawingJob){
					drawPoint(point, State.VISIBLE, actualDrawingJobDxInPx);
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
			for(Point point : actualDrawingJob){
				drawPoint(point, State.VISIBLE, 0);
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
			drawPoint(start, State.VISIBLE, actualDrawingJobDxInPx);
		
	}
	
	private void drawPoint(Point point, State state, int distanceFromPointsX){
		removePoint(point);
		PointDrawingInfo info = properties.getPointDrawingInfo(modelManager.getDataForPoint(point).get(0), state);  //TODO in case of overlapping points, and custom pointdrawinginfos, the results may not be satisfying
		Shape shape = null;
		switch(info.getType()){
		case ELLIPSE:
			shape = new Ellipse(
					point.getxPos() + distanceFromPointsX,
					point.getyPos(), 
					info.getWidth() / 2,
					info.getHeight() / 2);
		case RECTANGLE:
			shape = new Rectangle(
					point.getxPos() - info.getWidth() / 2 + distanceFromPointsX,
					point.getyPos() - info.getHeight() / 2,
					info.getWidth(),
					info.getHeight());
		case NO_SHAPE:
			return;
		}
		drawnShapes.put(point, shape);		
	}
	
	private void removePoint(Point point){
		drawnShapes.remove(point);
	}
	
	/**
	 * 
	 * @return null if all the points from actual job has been drawn
	 */
	private Point getFirstUndrawnPointFromActualJob(){
		for(Point point:actualDrawingJob){
			if(actualDrawingJob.indexOf(point) == actualDrawingJob.size() - 1)
				break;
			if(drawnShapes.containsKey(point))
				continue;
			else
				return point;
		}
		return null;
	}
	
	private void dropShapesOutsideViewPort(){
			for(Double x : curve.getPointsToDraw().keySet()){
			if(x < modelManager.getViewportMin() || x > modelManager.getViewportMax()){
				drawnShapes.remove(curve.getPointsToDraw().get(x));
			}
		}
	}
	
	private ArrayList<Point> createActualDrawingJob(double viewportMin, double viewportMax){
		//get the points which over the line will be drawn
 		actualDrawingJob = new  ArrayList<Point>();
		for(Double x:curve.getPointsToDraw().keySet()){
			//in the case of not predrawn lines we only need visible points
			if(!curve.getPolicy().isPreDrawPoints() && (x < viewportMin || x > viewportMax))
					continue;
			//check if we have added this point before (in case of multiple imaginary points in a row)
			if(actualDrawingJob.indexOf(curve.getPointsToDraw().get(x)) == -1){
				actualDrawingJob.add(curve.getPointsToDraw().get(x));
			}
		}
		return actualDrawingJob;
	}

	private void moveShapes(double dx){
		if(drawnShapes.size() == 0)
			return;
		int dxInPx = modelManager.calculateDistance(dx);
		for(Point point:drawnShapes.keySet()){
			drawnShapes.get(point).setX(drawnShapes.get(point).getX() + dxInPx);
		}			
	}

	@Override
	public void pointStateChanged(Point point) {
		
		drawPoint(point, point.getState(), drawnShapes.get(drawnShapes.firstKey()).getX() - drawnShapes.firstKey().getxPos());
		
	}
}