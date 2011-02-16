package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.CurveVisualizer;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.GraphicalObject;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.model.State;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo;

public class Curves implements HasViewport{

	private ArrayList<Curve> curves;
	private ModelManager mm;
	private boolean preDrawCurves;
	private Context2d canvas;
	private IneChartProperties prop;
	
	public Curves(Context2d canvas, Curve curve, ModelManager mm, IneChartProperties prop) {
		this.mm = mm;
		curves = new ArrayList<Curve>();
		this.prop = prop;
		this.canvas = canvas;
		if(curve.getCurveDrawingInfo().isPreDrawLines() || curve.getCurveDrawingInfo().isPreDrawPoints())
			this.preDrawCurves = true;
		else
			this.preDrawCurves = false;
		addCurve(curve);
	}
	
	public void addCurve(Curve curve){
		curves.add(curve);
		setViewPort(mm.getViewportMin(), mm.getViewportMax());
	}
	
	public void removeCurve(Curve curve){
		curves.remove(curve);
	}

	@Override
	public void moveViewport(double dx) {
		//TODO 
		if(preDrawCurves){
			setViewPort(mm.getViewportMin(), mm.getViewportMax());
		}
		else{
			setViewPort(mm.getViewportMin(), mm.getViewportMax());
		}
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		Collections.sort(curves, GraphicalObject.getzIndexComparator());
		for(Curve curve:curves){
			if(curve.hasLine()){
				drawLinesBetween(curve, viewportMin, viewportMax);
			}
			if(curve.hasPoints())
				drawPointsBetween(curve, viewportMin, viewportMax);
		}
	}
	
	
	
	private void drawLinesBetween(Curve curve, double min, double max){
		Double lineStart =  curve.getLastInvisiblePointBeforeViewport(min),
		lineStop = curve.getFirstInvisiblePointAfterViewport(max);
		ArrayList<Point> toDraw = new ArrayList<Point>();
		if(lineStart == null){
			lineStart = min;
		}
		if(lineStop == null){
			lineStop = max;
		}
		
		for(Double x : curve.getPointsToDraw().keySet()){
			if(x > lineStop)
				break;
			if(x >= lineStart){
				if(!toDraw.contains(curve.getPointsToDraw().get(x)))
					toDraw.add(curve.getPointsToDraw().get(x));
			}
		}
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
		//TODO info / state
	
		canvas.save();
		canvas.setStrokeStyle(curve.getLineDrawInfo().getborderColor());
		canvas.setLineWidth(curve.getLineDrawInfo().getborderWidth());
		canvas.setLineJoin("round");
		canvas.stroke();
		canvas.restore();
		if(curve.getLineDrawInfo().hasFill()){
			canvas.lineTo(toDraw.get(toDraw.size()-1).getxPos() + dx, prop.getChartCanvasHeight() + dy);
			canvas.lineTo(toDraw.get(0).getxPos() + dx, prop.getChartCanvasHeight() + dy);
			canvas.save();
			canvas.setLineWidth(0);
			canvas.setFillStyle(curve.getLineDrawInfo().getFillColor());
			canvas.setGlobalAlpha(curve.getLineDrawInfo().getFillOpacity());
			canvas.fill();
			canvas.restore();
		}
		canvas.closePath();
	}
	
	
	
	private void drawPointsBetween(Curve curve, double min, double max){
		int lastX = 0;
		for(Double x : curve.getPointsToDraw().keySet()){
			if(x > max)
				break;
			if(x >= min){
				Point point = curve.getPointsToDraw().get(x);
				if(point.isImaginaryPoint()){
					if(lastX == point.getxPos()){
						continue;
					}
					else
						lastX = point.getxPos();
				}
				drawPoint(point, x);
			}
		}
		
	}
	
	private void drawPoint(Point point, double data){
		if(point.getState().equals(State.INVISIBLE))
			point.setState(State.VISIBLE);
		PointDrawingInfo info = point.getParent().getCurveDrawingInfo().getPointDrawingInfo(data, point.getState());
		if(info == null)
			info = prop.getDefaultPointDrawingInfo(point.getState());
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
