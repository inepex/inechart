package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


import com.google.gwt.canvas.dom.client.Context2d;
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
				drawLinesOverVisiblePoints(curve);
			}
			if(curve.getCurveDrawingInfo().hasPoints())
				drawShapesOverVisiblePoints(curve);
		}
	}
	
	private void drawLinesOverVisiblePoints(Curve curve){
		ShapeDrawingInfo info = curve.getLineDrawInfo();
		ArrayList<Point> toDraw = curve.getVisiblePoints();
		if(toDraw.size() < 2)
			return;
		int dx = - mm.getViewportMinInPx();
		int dy = 0;
		Iterator<Point> iPoint = toDraw.iterator();
		canvas.beginPath();
		canvas.moveTo(toDraw.get(0).getxPos() + dx, toDraw.get(0).getyPos() + dy);
		while (iPoint.hasNext()){
			Point point = iPoint.next();
			canvas.lineTo(point.getxPos() + dx, point.getyPos() + dy);
		}
		
		canvas.save();
		canvas.setStrokeStyle(info.getborderColor());
		canvas.setLineWidth(info.getborderWidth());
		canvas.setLineJoin("round");
		canvas.stroke();
		canvas.restore();
		if(info.hasFill()){
			canvas.lineTo(toDraw.get(toDraw.size()-1).getxPos() + dx, prop.getChartCanvasHeight() + dy);
			canvas.lineTo(toDraw.get(0).getxPos() + dx, prop.getChartCanvasHeight() + dy);
			canvas.save();
			canvas.setLineWidth(0);
			canvas.setFillStyle(info.getFillColor());
			canvas.setGlobalAlpha(info.getFillOpacity());
			canvas.fill();
			canvas.restore();
		}
		canvas.closePath();
	}
	
	private void drawShapesOverVisiblePoints(Curve curve){
		for(Point point : curve.getVisiblePoints()){
			drawShape(point);
		}	
	}
	
	private void drawShape(Point point){
		PointDrawingInfo info = point.getPointDrawingInfo();
		if(info == null || info.getType().equals(PointType.NO_SHAPE))
			return;
		canvas.save();
		canvas.setStrokeStyle(info.getborderColor());
		canvas.setLineWidth(info.getborderWidth());
		canvas.setFillStyle(info.getFillColor());
		canvas.setGlobalAlpha(info.getFillOpacity());
		canvas.beginPath();
		int width = info.getWidth(), height = info.getHeight();
		int dx = - mm.getViewportMinInPx();
		int dy = 0;
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

	
}
