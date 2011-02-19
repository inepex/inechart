package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.shape.Ellipse;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.DrawingJobScheduler;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.model.State;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo;

/**
 *  Point curve implementation using gwt-graphics 
 * @author Miklós Süveges / Inepex Ltd
 */
public final class PointCurveVisualizer extends CurveVisualizer  {

	private TreeMap<Point, Shape> drawnShapes;
	private ModelManager modelManager;
	private ArrayList<Point> actualDrawingJob;
	private DrawingJobScheduler scheduler;
	private double totalDX = 0;
	private IneChartProperties properties;
	
	public PointCurveVisualizer(Widget canvas, Curve curve, ModelManager modelManager, IneChartProperties properties) {
		super(canvas, curve);
		this.modelManager = modelManager;
		this.properties = properties;
		this.drawnShapes = new TreeMap<Point, Shape>();
	}

	@Override
	public void drawCurve(double viewportMin, double viewportMax) {
		setViewport(viewportMin, viewportMax);
	}

	@Override
	public void removeFromCanvas() {
		ArrayList<Shape> toRemove = new ArrayList<Shape>();
		for(int i=0;i<((DrawingArea)canvas).getVectorObjectCount();i++){
			if(drawnShapes.containsValue(((DrawingArea)canvas).getVectorObject(i)))
				toRemove.add( (Shape) ((DrawingArea)canvas).getVectorObject(i) );
			
		}
		for(Shape s : toRemove)
			((DrawingArea)canvas).remove(s);
		drawnShapes.clear();
//		for(Point point : drawnShapes.keySet()){
//			Shape shape = drawnShapes.get(point);
//			if(shape != null){
//				((DrawingArea)canvas).remove(shape);
//			}
//			else{
//				continue;
//			}
//		}
//		drawnShapes.clear();	
	}

	@Override
	public void moveViewport(double dx) {
		moveShapes(-dx);
		if(curve.getCurveDrawingInfo().isPreDrawPoints()){
			return;
		}
		else{
			if(!curve.getCurveDrawingInfo().isKeepInvisibleGraphicalObjects())
				dropShapesOutsideViewPort();
			createActualDrawingJob(modelManager.getViewportMin(), modelManager.getViewportMax());
			if(curve.getCurveDrawingInfo().isDrawPointByPoint()){
				scheduler = new DrawingJobScheduler(this, curve.getCurveDrawingInfo().getDelayBetweenDrawingPoints());
				scheduler.start();
			}
			else{
				for(Point point : actualDrawingJob){
					drawPoint(point, State.NORMAL, modelManager.calculateDistance(totalDX));
				}
			}			
		}

	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		totalDX = 0;
		removeFromCanvas();
		if(curve.getCurveDrawingInfo().isPreDrawPoints()){
			actualDrawingJob = new ArrayList<Point>();
			for(double x:curve.getPointsToDraw().keySet())
				actualDrawingJob.add(curve.getPointsToDraw().get(x));
		}
		else{
			createActualDrawingJob(viewportMin, viewportMax);
		}
		if(curve.getCurveDrawingInfo().isDrawPointByPoint()){
			scheduler = new DrawingJobScheduler(this, curve.getCurveDrawingInfo().getDelayBetweenDrawingPoints());
			scheduler.start();
		}
		else{
			for(Point point : actualDrawingJob){
				drawPoint(point, State.NORMAL, 0);
			}
		}
		moveShapes(modelManager.getxMin() - viewportMin);	

	}

	@Override
	public void drawNextPoint() {
		Point start = getFirstUndrawnPointFromActualJob();
		if(start == null){
			scheduler.stop();
			bringPointsToFront();
		}
		else
			drawPoint(start, State.NORMAL, modelManager.calculateDistance(totalDX));
		
	}
	
	private void drawPoint(Point point, State state, int distanceFromPointsX){
		removePoint(point);
		ArrayList<Double> datasForPoint = modelManager.getDataForPoint(point);
		double data = datasForPoint.get(0);
	
		PointDrawingInfo info = curve.getCurveDrawingInfo().getPointDrawingInfo(data, state); 
		if(info == null)
			info = curve.getCurveDrawingInfo().getDefaultPointDrawingInfo(state);
		Shape shape = null;
		switch(info.getType()){
		case ELLIPSE:
			shape = new Ellipse(
					point.getxPos() + distanceFromPointsX,
					point.getyPos(), 
					info.getWidth() / 2,
					info.getHeight() / 2);
			break;
		case RECTANGLE:
			shape = new Rectangle(
					point.getxPos() - info.getWidth() / 2 + distanceFromPointsX,
					point.getyPos() - info.getHeight() / 2,
					info.getWidth(),
					info.getHeight());
			break;
		case NO_SHAPE:
			return;
		}
		if(info.hasFill()){
			shape.setFillColor(info.getFillColor());
			shape.setFillOpacity(info.getFillOpacity());
		}
		else
			shape.setFillOpacity(0d);
		shape.setStrokeColor(info.getborderColor());
		shape.setStrokeWidth(info.getborderWidth());
		
		((DrawingArea)canvas).add(shape);
		drawnShapes.put(point, shape);		
	}
	
	private void removePoint(Point point){
		if(drawnShapes.containsKey(point)){
			((DrawingArea)canvas).remove(drawnShapes.get(point));
			drawnShapes.remove(point);
		}
	}
	
	/**
	 * 
	 * @return null if all the points from actual job has been drawn
	 */
	private Point getFirstUndrawnPointFromActualJob(){
		for(Point point:actualDrawingJob){
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
				Shape shape = drawnShapes.get(curve.getPointsToDraw().get(x));
				if(shape != null){
					((DrawingArea)canvas).remove(shape);
					drawnShapes.remove(curve.getPointsToDraw().get(x));
				}
			}
		}
	}
	
	
	private ArrayList<Point> createActualDrawingJob(double viewportMin, double viewportMax){
		//get the points which over the line will be drawn
 		actualDrawingJob = new  ArrayList<Point>();
		for(Double x:curve.getPointsToDraw().keySet()){
			//in the case of not predrawn lines we only need NORMAL points
			if(!curve.getCurveDrawingInfo().isPreDrawPoints() && (x < viewportMin || x > viewportMax))
					continue;
			//check if we have added this point before (in case of multiple imaginary points in a row)
			if(actualDrawingJob.indexOf(curve.getPointsToDraw().get(x)) == -1){
				actualDrawingJob.add(curve.getPointsToDraw().get(x));
			}
		}
		return actualDrawingJob;
	}

	private void moveShapes(double dx){
		totalDX += dx;
		if(drawnShapes.size() == 0)
			return;
		int dxInPx = modelManager.calculateDistance(totalDX);
		for(Point point:drawnShapes.keySet()){
			if( drawnShapes.get(point) != null)
				drawnShapes.get(point).setX(point.getxPos() + dxInPx);
		}			
	}

	
	public void pointStateChanged(Point point) {
		
		drawPoint(point, point.getState(), drawnShapes.get(drawnShapes.firstKey()).getX() - drawnShapes.firstKey().getxPos());
		
	}

	private void bringPointsToFront(){
		for(Point point : drawnShapes.keySet()){
			((DrawingArea)canvas).bringToFront(drawnShapes.get(point));
		}
	}

}
