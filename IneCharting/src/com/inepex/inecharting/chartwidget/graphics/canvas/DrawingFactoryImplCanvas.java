package com.inepex.inecharting.chartwidget.graphics.canvas;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;

public class DrawingFactoryImplCanvas extends DrawingFactory {
	private Axes axes;
	private Curves curves;
	private Canvas curveCanvas;
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
		setViewPort(modelManager.getViewportMin(), modelManager.getViewportMax());
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		Context2d c = curveCanvas.getContext2d();
		c.save();
		c.setFillStyle(properties.getChartCanvasBackgroundColor());
		c.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		c.restore();
		axes.drawGridLines();
		curves.setViewPort(viewportMin, viewportMax);
		axes.setViewPort(viewportMin, viewportMax);
	}

	@Override
	protected void init(Axis xAxis, Axis yAxis, Axis y2Axis) {
		curveCanvas = Canvas.createIfSupported();
		curveCanvas.setPixelSize(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		curveCanvas.setCoordinateSpaceHeight(properties.getChartCanvasHeight());
		curveCanvas.setCoordinateSpaceWidth(properties.getChartCanvasWidth());
		Context2d c = curveCanvas.getContext2d();
		c.save();
		c.setFillStyle(properties.getChartCanvasBackgroundColor());
		c.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		c.restore();
		axes = new Axes(c, modelManager, properties);
	
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
		chartMainPanel.add(curveCanvas,0,0);
		chartMainPanel.add(xAxisCanvas,0,properties.getChartCanvasHeight());
		
	}

	@Override
	public void addCurve(Curve curve) {
		axes.drawGridLines();
		if(curves == null)
			curves = new Curves(curveCanvas.getContext2d(), curve, modelManager, properties);
		else
			curves.addCurve(curve);
		axes.setViewPort(modelManager.getViewportMin(), modelManager.getViewportMax());
	}

	@Override
	public void removeCurve(Curve curve) {
		curves.removeCurve(curve);
	}

}
