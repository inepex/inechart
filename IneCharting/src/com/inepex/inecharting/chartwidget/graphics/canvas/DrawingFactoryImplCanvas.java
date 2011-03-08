package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.event.EventManager;
import com.inepex.inecharting.chartwidget.event.ExtremesChangeEvent;
import com.inepex.inecharting.chartwidget.event.StateChangeEvent;
import com.inepex.inecharting.chartwidget.event.StateChangeHandler;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.Mark;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.model.State;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;

public class DrawingFactoryImplCanvas extends DrawingFactory implements StateChangeHandler{
	protected Axes axes;
	protected Curves curves;
	protected Marks marks;
	protected Context2d curveCanvasCtx;
	protected Widget xAxisCanvas;
	protected Widget yAxisCanvas;
	protected Widget y2AxisCanvas;

	

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
//		axes.drawGridLines();
		//curves
		curves.setViewport(viewportMin, viewportMax);
		//axes
//		axes.setViewport(viewportMin, viewportMax);
		//marks
//		marks.setViewport(viewportMin, viewportMax);
		EventManager.get().setReadyForEvents(true);
	}

	@Override
	public void init(AbsolutePanel chartMainPanel, IneChartProperties properties, ModelManager modelManager, Axis xAxis, Axis yAxis, Axis y2Axis) {
		this.properties = properties;
		this.chartMainPanel = chartMainPanel;
		this.modelManager = modelManager;
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
	
		if(xAxis != null){
			xAxisCanvas = Canvas.createIfSupported();
			xAxisCanvas.setPixelSize(properties.getChartCanvasWidth(), ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
			((Canvas) xAxisCanvas).setCoordinateSpaceHeight( ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
			((Canvas) xAxisCanvas).setCoordinateSpaceWidth(properties.getChartCanvasWidth());
			
			axes.addXAxis(xAxis, ((Canvas) xAxisCanvas).getContext2d());
		}
		if(y2Axis != null && ((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
			y2AxisCanvas = Canvas.createIfSupported();
			y2AxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth(),  properties.getChartCanvasHeight());
			((Canvas) y2AxisCanvas).setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
			((Canvas) y2AxisCanvas).setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth());
			
			axes.addY2Axis(y2Axis, ((Canvas) y2AxisCanvas).getContext2d());
		}
		else
			axes.addY2Axis(y2Axis, null);
		
		if(yAxis != null &&((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
			yAxisCanvas = Canvas.createIfSupported();
			yAxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth(), properties.getChartCanvasHeight());
			((Canvas) yAxisCanvas).setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
			((Canvas) yAxisCanvas).setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth());
		
			axes.addYAxis(yAxis, ((Canvas) yAxisCanvas).getContext2d());
		}
		else
			axes.addYAxis(yAxis,null);
		marks = new Marks(curveCanvasCtx, (HorizontalAxisDrawingInfo) xAxis.getDrawingInfo());
		
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
		//marks
		marks.setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
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
		//marks
		marks.setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
		EventManager.get().setReadyForEvents(true);
	}

	public void updateShapes(ArrayList<Point> point){
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
	}
	
	private void updateShape(Point point, State prev){	
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
	}

	/**
	 * Updating through this method is slow in most cases
	 */
	@Override
	public void onStateChange(StateChangeEvent event) {
		if (event.getSourceObject() instanceof Point) {
			updateShape((Point) event.getSourceObject(), event.getPreviousState());
		}
	}

	public void addMark(Mark mark){
		marks.addMark(mark);
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
	}
	
	public void removeMark(Mark mark){
		marks.removeMark(mark);
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
	}

	@Override
	public TreeMap<Mark, int[]> getMarkBoundingBoxes() {
		return marks.getBoundingBoxes();
	}

	

}
				
				
				