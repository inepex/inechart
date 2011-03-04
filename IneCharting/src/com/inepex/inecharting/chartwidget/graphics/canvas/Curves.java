package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.GraphicalObject;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo.PointType;
import com.inepex.inecharting.chartwidget.properties.ShapeDrawingInfo;

public class Curves implements HasViewport{

	private ArrayList<Curve> curves;
	private ModelManager mm;
	private Context2d canvas;
	private IneChartProperties prop;
	
	public Curves(Context2d canvas, Curve curve, ModelManager mm, IneChartProperties prop) {
		this.mm = mm;
		curves = new ArrayList<Curve>();
		this.prop = prop;
		this.canvas = canvas;
		addCurve(curve);
	}
	
	public void addCurve(Curve curve){
		curves.add(curve);
		setViewport(mm.getViewportMin(), mm.getViewportMax());
	}
	
	public void removeCurve(Curve curve){
		curves.remove(curve);
		setViewport(mm.getViewportMin(), mm.getViewportMax());
	}

	@Override
	public void moveViewport(double dx) {
		setViewport(mm.getViewportMin(), mm.getViewportMax());
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		Collections.sort(curves, GraphicalObject.getzIndexComparator());
		for(Curve curve:curves){
			if(curve.getCurveDrawingInfo().hasLine()){
				drawLinesOverPoints(curve.getLineDrawInfo(),curve.getVisiblePoints(),null,0);
			}
			if(curve.getCurveDrawingInfo().hasPoints()){
				drawShapesOverPoints(curve.getVisiblePoints());
			}
		}
	}
	
	private void drawLinesOverPoints(ShapeDrawingInfo info,ArrayList<Point> toDraw, Context2d backBuffer,int canvasX){
		Context2d context;
		int dx, dy;
		if(toDraw.size() < 2)
			return;
		if(backBuffer != null){
			context = backBuffer;
			dx = - (mm.getViewportMinInPx() + canvasX);
			dy = 0;
		}
		else{
			context = canvas;
			dx = - mm.getViewportMinInPx();
			dy = 0;
		}
		
		Iterator<Point> iPoint = toDraw.iterator();
		context.beginPath();
		context.moveTo(toDraw.get(0).getxPos() + dx, toDraw.get(0).getyPos() + dy);
		while (iPoint.hasNext()){
			Point point = iPoint.next();
			context.lineTo(point.getxPos() + dx, point.getyPos() + dy);
		}
		
		context.save();
		context.setStrokeStyle(info.getborderColor());
		context.setLineWidth(info.getborderWidth());
		context.setLineJoin("round");
		context.stroke();
		context.restore();
		if(info.hasFill()){
			context.lineTo(toDraw.get(toDraw.size()-1).getxPos() + dx, prop.getChartCanvasHeight() + dy);
			context.lineTo(toDraw.get(0).getxPos() + dx, prop.getChartCanvasHeight() + dy);
			context.save();
			context.setLineWidth(0);
			context.setFillStyle(info.getFillColor());
			context.setGlobalAlpha(info.getFillOpacity());
			context.fill();
			context.restore();
		}
		context.closePath();	
	}
	
	private void drawShapesOverPoints(ArrayList<Point> toDraw){
		int dx =  - mm.getViewportMinInPx(), dy = 0;
	
		for(Point point : toDraw){
			drawShape(canvas, point, dx, dy);
		}	
	}
	
	private static void drawShape(Context2d canvas, Point point, int dx, int dy){
		PointDrawingInfo info = point.getActualPointDrawingInfo();
		if(info == null || info.getType().equals(PointType.NO_SHAPE))
			return;
		canvas.save();
		canvas.setStrokeStyle(info.getborderColor());
		canvas.setLineWidth(info.getborderWidth());
		canvas.setFillStyle(info.getFillColor());
		canvas.setGlobalAlpha(info.getFillOpacity());
		canvas.beginPath();
		int width = info.getWidth(), height = info.getHeight();
		switch (info.getType()) {
		case ELLIPSE:
			double scaleX = 1, scaleY=1;
			if(width > height){
				scaleY = width / height;
				width = height;
			}
			else{
				scaleX = height / width;
				height = width;
			}
			canvas.scale(scaleX, scaleY);
			canvas.arc(point.getxPos() + dx, point.getyPos() + dy, height / 2, 0, Math.PI*2);
			break;
		case RECTANGLE:
			canvas.rect(point.getxPos() + dx - width / 2, point.getyPos() + dy - height / 2, width, height);
			break;
		default:
			break;
		}
		canvas.stroke();
		canvas.fill();
		canvas.restore();
		canvas.closePath();
	}

	/**
	 * Redraws the canvas at the given x interval.
	 * @param min px in the canvas/viewport's dimension
	 * @param max px in the canvas/viewport's dimension
	 */
	public void drawLinesAndShapes(int min, int max, Context2d backBuffer){
		Collections.sort(curves, GraphicalObject.getzIndexComparator());
		for(Curve curve : curves){
			Point prev = null;
			for(Point point : curve.getVisiblePoints()){
				if(prev == null)
					prev = point;
				else if(curve.getCurveDrawingInfo().hasLine() && isLineInInterval(prev, point, min, max)){
					ArrayList<Point> toDraw = new ArrayList<Point>();
					toDraw.add(prev);
					toDraw.add(point);
					drawLinesOverPoints(curve.getLineDrawInfo(), toDraw,backBuffer, min);
				}
				prev = point;
			}
			for(Point point : curve.getVisiblePoints()){
				if(curve.getCurveDrawingInfo().hasPoints() && isShapeInInterval(point, min, max)) {
					drawShape(backBuffer,point,  - (mm.getViewportMinInPx() + min), 0);
				}
			}
		}
	}
	
	private boolean isShapeInInterval(Point point,int min, int max){
		PointDrawingInfo info = point.getActualPointDrawingInfo();
		if(info == null || info.getType().equals(PointType.NO_SHAPE))
			return false;
		int shapesLeft =  mm.getCanvasX(point)- info.getWidth() / 2;
		int shapesRight =  mm.getCanvasX(point)+ info.getWidth() / 2;
		if(shapesRight < min  ||  shapesLeft > max) 
			return false;
		else
			return true;
	}
	
	private boolean isLineInInterval(Point point1,Point point2,int min, int max){
		if( (mm.getCanvasX(point1) < min && mm.getCanvasX(point2) < min) ||  (mm.getCanvasX(point1) > max && mm.getCanvasX(point2) > max))
			return false;
		else
			return true;
	}
	
		
	
	
	
	
}
