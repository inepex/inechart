package com.inepex.inecharting.chartwidget.graphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.HorizontalAxisVisualizer;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.LineCurveVisualizer;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.PointCurveVisualizer;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.VerticalAxisVisualizer;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo.AxisLocation;

/**
 * 
 * A class for drawing the chart's model to the canvas.
 * The complete view of the IneChart belongs here.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class DrawingFactory implements HasViewport{

	public static enum DrawingTool{
		VAADIN_GWT_GRAPHICS
	}
	
	/**
	 * The tool with 
	 */
	private DrawingTool drawingTool;
	private IneChartProperties properties;
	private ModelManager modelManager;
	private TreeMap<String, ArrayList<CurveVisualizer>> curveVisualizers;
	private Widget chartCanvas;
	private HasViewport xAxisVisualizer;
	private AxisVisualizer yAxisVisualizer;
	private AxisVisualizer y2AxisVisualizer;
	private AbsolutePanel chartMainPanel;
	
	/**
	 * Creates an instance with the defined drawing toolkit,
	 * and initializes the related objects (e.g.: the 'canvas')
	 * @param drawingTool
	 */
	public DrawingFactory(AbsolutePanel chartMainPanel, DrawingTool drawingTool, IneChartProperties properties, ModelManager mm, Axis xAxis, Axis yAxis, Axis y2Axis) {
		this.chartMainPanel = chartMainPanel;
		this.drawingTool = drawingTool;
		this.properties = properties;
		this.modelManager = mm;
		curveVisualizers = new TreeMap<String, ArrayList<CurveVisualizer>>();
		switch (drawingTool) {
		case VAADIN_GWT_GRAPHICS:
			initGwtGraphicsFields(xAxis,yAxis,y2Axis);
		default:
			return;
		}
	}
	/**
	 * Initializes the layout (the canvases hierarchy, etc)
	 */
	public void assembleLayout(){
		switch (drawingTool) {
		case VAADIN_GWT_GRAPHICS:
			int x=0,y=0;
			chartMainPanel.setPixelSize(1000, 600);
			if(yAxisVisualizer != null){
				if(properties.getXAxisDrawingInfo().getAxisLocation().equals(AxisLocation.TOP))
					y +=  properties.getXAxisDrawingInfo().getTickPanelHeight();
				chartMainPanel.add(yAxisVisualizer.getCanvas(), x, y);
				chartMainPanel.add(((VerticalAxisVisualizer)yAxisVisualizer).getTextPositionerAbsolutePanel(), x, y); //TODO pos
				DOM.setElementAttribute(((VerticalAxisVisualizer)yAxisVisualizer).getTextPositionerAbsolutePanel().getElement(),"zIndex", "1");
				DOM.setElementAttribute(((VerticalAxisVisualizer)yAxisVisualizer).getCanvas().getElement(), "zIndex", "-1");
				x += properties.getYAxisDrawingInfo().getOffChartCanvasWidth();
			}
			if(xAxisVisualizer != null){
				switch (properties.getXAxisDrawingInfo().getAxisLocation()) {
				case BOTTOM:
					chartMainPanel.add(((HorizontalAxisVisualizer)xAxisVisualizer).getCanvas(),
							x,
							y + properties.getChartCanvasHeight() - properties.getXAxisDrawingInfo().getTickLengthInside());
					chartMainPanel.add(((HorizontalAxisVisualizer)xAxisVisualizer).getTextPositionerAbsolutePanel(),
							x,
							y + properties.getChartCanvasHeight());
					break;
				case TOP:
					chartMainPanel.add(((HorizontalAxisVisualizer)xAxisVisualizer).getCanvas(),
							x,
							0);
					chartMainPanel.add(((HorizontalAxisVisualizer)xAxisVisualizer).getTextPositionerAbsolutePanel(),
							x,
							0);
					break;
				}
				
				DOM.setElementAttribute(((HorizontalAxisVisualizer)xAxisVisualizer).getTextPositionerAbsolutePanel().getElement(),"zIndex", "1");
				DOM.setElementAttribute(((HorizontalAxisVisualizer)xAxisVisualizer).getCanvas().getElement(), "zIndex", "-1");
			}
			if(y2AxisVisualizer != null){
//	TODO			chartMainPanel.add(y2AxisVisualizer.getCanvas(), x , y);
//				chartMainPanel.add(((VerticalAxisVisualizer)y2AxisVisualizer).getTextPositionerAbsolutePanel(), x, y); //TODO pos
//				DOM.setElementAttribute(((VerticalAxisVisualizer)y2AxisVisualizer).getTextPositionerAbsolutePanel().getElement(),"zIndex", "-4");
//				DOM.setElementAttribute(((VerticalAxisVisualizer)y2AxisVisualizer).getCanvas().getElement(), "zIndex", "-5");
			}
			chartMainPanel.add(chartCanvas, x, y);
		default:
			return;
		}
	}
	
	private void initTexts(){
		
	}
	
	public void displayAxes(){
		if(yAxisVisualizer != null){
			yAxisVisualizer.display();
		}
		if(xAxisVisualizer != null){
			((HorizontalAxisVisualizer)xAxisVisualizer).display();
		}
		if(y2AxisVisualizer != null){
			y2AxisVisualizer.display();
		}
	}
	
	private void initGwtGraphicsFields(Axis xAxis, Axis yAxis, Axis y2Axis){
		this.chartCanvas = new DrawingArea(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		Rectangle border = new Rectangle(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		border.setFillOpacity(0);
		((DrawingArea)chartCanvas).add(border);
		
		if(xAxis != null){
			DrawingArea xCanvas = new DrawingArea(
					properties.getChartCanvasWidth(),
					properties.getXAxisDrawingInfo().getTickPanelHeight() + properties.getXAxisDrawingInfo().getTickLengthInside());
			AbsolutePanel ap =  new AbsolutePanel();
			ap.setPixelSize(
					properties.getChartCanvasWidth(), 
					properties.getXAxisDrawingInfo().getTickPanelHeight());
			xAxisVisualizer = new HorizontalAxisVisualizer(xCanvas,ap, xAxis, modelManager);
		}
		if(yAxis != null){
			DrawingArea yCanvas = new DrawingArea(
					properties.getYAxisDrawingInfo().getTickLength(), 
					properties.getChartCanvasHeight());
			AbsolutePanel ap =  new AbsolutePanel();
			ap.setPixelSize(
					100,  //TODO
					properties.getChartCanvasHeight());
		}
		if(y2Axis != null){
			//TODO 
			DrawingArea y2Canvas = new DrawingArea(
					properties.getY2AxisDrawingInfo().getTickLength(),
					properties.getChartCanvasHeight());
			AbsolutePanel ap =  new AbsolutePanel();
			ap.setPixelSize(
					100,  //TODO
					properties.getChartCanvasHeight());
		}
		
	}
	
 	/**
 	 * draws a curve on canvas
 	 * @param curve
 	 */
 	public void addCurve(Curve curve){
 		switch (drawingTool) {
		case VAADIN_GWT_GRAPHICS:
			ArrayList<CurveVisualizer> visualizers = new ArrayList<CurveVisualizer>();
			if(curve.hasLine()){
				LineCurveVisualizer lcv = new LineCurveVisualizer(chartCanvas, curve, modelManager);
				lcv.drawCurve(modelManager.getViewportMin(), modelManager.getViewportMax());
				visualizers.add(lcv);
			}
			if(curve.hasPoints()){
				PointCurveVisualizer pcv = new PointCurveVisualizer(chartCanvas, curve, modelManager, properties);
				pcv.drawCurve(modelManager.getViewportMin(), modelManager.getViewportMax());
				visualizers.add(pcv);
			}
			curveVisualizers.put(curve.getName(), visualizers);
			((DrawingArea)chartCanvas).bringToFront(((DrawingArea)chartCanvas).getVectorObject(0));
			break;
		default:
			return;
		}
 		
 	}
 
 	/**
 	 * removes a curve from canvas
 	 * @param index
 	 */
 	public void removeCurve(String name){
 		for(CurveVisualizer cv:curveVisualizers.get(name)){
 			cv.removeFromCanvas();
 		}
 		curveVisualizers.remove(name);
 	}
 	
 	public Widget getChartCanvas(){
 		return chartCanvas;		
 	}
	
 	@Override
	public void moveViewport(double dx) {
		xAxisVisualizer.moveViewport(dx);
		for(String curveName : curveVisualizers.keySet())
			for(CurveVisualizer visualizer: curveVisualizers.get(curveName))
				visualizer.moveViewport(dx);		
		((DrawingArea)chartCanvas).bringToFront(((DrawingArea)chartCanvas).getVectorObject(0));
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		xAxisVisualizer.setViewPort(viewportMin, viewportMax);
 		for(String curveName : curveVisualizers.keySet())
			for(CurveVisualizer visualizer: curveVisualizers.get(curveName))
				visualizer.setViewPort(viewportMin, viewportMax);
 		((DrawingArea)chartCanvas).bringToFront(((DrawingArea)chartCanvas).getVectorObject(0));
	}
	
}
