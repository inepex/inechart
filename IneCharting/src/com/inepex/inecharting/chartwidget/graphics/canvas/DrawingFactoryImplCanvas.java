package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.ui.AbsolutePanel;
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
	private Axes axes;
	private Curves curves;
	private Marks marks;
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
		//marks
		marks.setViewport(viewportMin, viewportMax);
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
		xAxisCanvas.setPixelSize(properties.getChartCanvasWidth(), ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
		xAxisCanvas.setCoordinateSpaceHeight( ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
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
//		EventManager.get().setReadyForEvents(false);
//		int x, width = point.getPointDrawingInfo(prev).getWidth(); 
//		if(width < point.getActualPointDrawingInfo().getWidth())
//			width = point.getActualPointDrawingInfo().getWidth();
//		if(width == 0)
//			return;
//		else{
//			x = point.getxPos() - modelManager.getViewportMinInPx() - width / 2 - 1;
//			width += 2;
//			if(x < 0){
//				width += x;
//				x = 0;
//			}
//			else if(x > modelManager.getChartCanvasWidth()){
//				width -= x - modelManager.getChartCanvasWidth(); 
//				x = modelManager.getChartCanvasWidth();
//			}
//			else if(x+width > modelManager.getViewportMaxInPx()){
//				width = modelManager.getViewportMaxInPx() - x;
//			}
//			
//		}
//		//creating backbuffercanvas
//		Canvas canvas = Canvas.createIfSupported();
//		canvas.setCoordinateSpaceHeight(properties.getChartCanvasHeight());
//		canvas.setCoordinateSpaceWidth(width);
//		Context2d backBuffer = canvas.getContext2d();
//		
//		axes.drawGridLines(x, x + width, backBuffer);
//		curves.drawLinesAndShapes(x, x + width, backBuffer);
//		axes.drawAxes(x, x + width, backBuffer);
//		
//		//copy
////		
//		try{
//			curveCanvasCtx.save();
//			curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
//			curveCanvasCtx.fillRect(x, 0, width, properties.getChartCanvasHeight());
//			curveCanvasCtx.restore();
//			curveCanvasCtx.drawImage(backBuffer.getCanvas(), x, 0);
//		}
//		catch (JavaScriptException e) {
//			e.getDescription();
//		}
//		EventManager.get().setReadyForEvents(true);			
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
				
				
				