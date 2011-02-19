package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.event.EventManager;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo.PointType;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;

public class DrawingFactoryImplCanvas extends DrawingFactory {
	private Axes axes;
	private Curves curves;
	private Context2d curveCanvasCtx;
	private Canvas xAxisCanvas;
	private Canvas yAxisCanvas;
	private Canvas y2AxisCanvas;

	public DrawingFactoryImplCanvas(AbsolutePanel chartMainPanel,
			IneChartProperties properties, ModelManager modelManager,
			Axis xAxis, Axis yAxis, Axis y2Axis) {
		super(chartMainPanel, properties, modelManager, xAxis, yAxis, y2Axis);
		
	}

	@Override
	public void moveViewport(double dx) {
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		EventManager.get().setReadyForEvents(false);
		//bg color
		curveCanvasCtx.save();
		curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
		curveCanvasCtx.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		curveCanvasCtx.restore();
		//gridlines
		axes.drawGridLines();
		//curves
		curves.setViewport(viewportMin, viewportMax);
		//axes
		axes.setViewport(viewportMin, viewportMax);
		EventManager.get().setReadyForEvents(true);
	}

	@Override
	protected void init(Axis xAxis, Axis yAxis, Axis y2Axis) {
		chartCanvas = Canvas.createIfSupported();
		chartCanvas.setPixelSize(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		((Canvas) chartCanvas).setCoordinateSpaceHeight(properties.getChartCanvasHeight());
		((Canvas) chartCanvas).setCoordinateSpaceWidth(properties.getChartCanvasWidth());
		curveCanvasCtx = ((Canvas) chartCanvas).getContext2d();
		curveCanvasCtx.save();
		curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
		curveCanvasCtx.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		curveCanvasCtx.restore();
		axes = new Axes(curveCanvasCtx, modelManager, properties);
	
		if(xAxis != null)
		xAxisCanvas = Canvas.createIfSupported();
		xAxisCanvas.setPixelSize(properties.getChartCanvasWidth(), ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getTickPanelHeight());
		xAxisCanvas.setCoordinateSpaceHeight( ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getTickPanelHeight());
		xAxisCanvas.setCoordinateSpaceWidth(properties.getChartCanvasWidth());
		
		axes.addXAxis(xAxis, xAxisCanvas.getContext2d());
		
		if(y2Axis != null && ((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
			y2AxisCanvas = Canvas.createIfSupported();
			y2AxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth(),  properties.getChartCanvasHeight());
			y2AxisCanvas.setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
			y2AxisCanvas.setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth());
			
			axes.addY2Axis(y2Axis, y2AxisCanvas.getContext2d());
		}
		else
			axes.addY2Axis(y2Axis, null);
		
		if(yAxis != null &&((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
			yAxisCanvas = Canvas.createIfSupported();
			yAxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth(), properties.getChartCanvasHeight());
			yAxisCanvas.setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
			yAxisCanvas.setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth());
		
			axes.addYAxis(yAxis, yAxisCanvas.getContext2d());
		}
		else
			axes.addYAxis(yAxis,null);
	}

	@Override
	public void assembleLayout() {
		// TODO 
		chartMainPanel.setPixelSize(properties.getWidgetWidth(), properties.getWidgetHeight());
		int dx = 0,dy = 0;
		chartMainPanel.add(chartCanvas,0,0);
		chartMainPanel.add(xAxisCanvas,0,properties.getChartCanvasHeight());
		
	}

	@Override
	public void addCurve(Curve curve) {
		EventManager.get().setReadyForEvents(false);
		//bg color
		curveCanvasCtx.save();
		curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
		curveCanvasCtx.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		curveCanvasCtx.restore();
		//gridlines
		axes.drawGridLines();
		//curves
		if(curves == null)
			curves = new Curves(curveCanvasCtx, curve, modelManager, properties);
		else
			curves.addCurve(curve);
		//axes
		axes.setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
		EventManager.get().setReadyForEvents(true);
	}

	@Override
	public void removeCurve(Curve curve) {
		EventManager.get().setReadyForEvents(false);
		//bg color
		curveCanvasCtx.save();
		curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
		curveCanvasCtx.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		curveCanvasCtx.restore();
		//gridlines
		axes.drawGridLines();
		//curves
		curves.removeCurve(curve);
		//axes
		axes.setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
		EventManager.get().setReadyForEvents(true);
	}

	@Override
	public void drawPoints(ArrayList<Point> points) {
//		int x,width;
//		for(Point point:points)		{
//			x = point.getxPos();
//			if(!point.getPointDrawingInfo().getType().equals(PointType.NO_SHAPE)){
//				width = point.getPointDrawingInfo().getWidth();
//				curveCanvasCtx.save();
//				curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
//				curveCanvasCtx.fillRect(x-ModelManager.get().getViewportMinInPx()-width/2-1, 0, width + 2 , properties.getChartCanvasHeight());
//				curveCanvasCtx.restore();
//				curves.setViewport(point.getUnderlyingData().get(0),point.getUnderlyingData().get(0)+ModelManager.get().calculateDistance(width+2));
//			}
//		}
		
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
		
	}
}
				
				
				