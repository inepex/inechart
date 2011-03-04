package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.event.ExtremesChangeEvent;
import com.inepex.inecharting.chartwidget.event.StateChangeEvent;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.model.Axes;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.Mark;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo.AxisLocation;

/**
 * 
 * A class for drawing the chart's model to the canvas.
 *
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class DrawingFactoryImplGwtGraphics extends DrawingFactory implements HasViewport{
	
	/**
	 * The tool with 
	 */
	private TreeMap<Curve, ArrayList<CurveVisualizer>> curveVisualizers;
	private HorizontalAxisVisualizer xAxisVisualizer;
	private VerticalAxisVisualizer yAxisVisualizer;
	private VerticalAxisVisualizer y2AxisVisualizer;
	private AbsolutePanel chartMainPanel;
	
	
	/**
	 * Creates an instance with the defined drawing toolkit,
	 * and initializes the related objects (e.g.: the 'canvas')
	 * @param drawingTool
	 */
	public DrawingFactoryImplGwtGraphics(AbsolutePanel chartMainPanel, IneChartProperties properties, ModelManager mm, Axis xAxis, Axis yAxis, Axis y2Axis) {
		super(chartMainPanel, properties, mm, xAxis, yAxis, y2Axis);
		curveVisualizers = new TreeMap<Curve, ArrayList<CurveVisualizer>>();
	}
	
	/**
	 * Initializes the layout (the canvases hierarchy, etc)
	 */
	public void assembleLayout(){
		int x=0,y=0;
		chartMainPanel.setPixelSize(properties.getWidgetWidth(), properties.getWidgetHeight());
		if(yAxisVisualizer != null){
			if(properties.getXAxisDrawingInfo().getAxisLocation().equals(AxisLocation.TOP))
				y +=  properties.getXAxisDrawingInfo().getAxisPanelHeight();

			chartMainPanel.add(yAxisVisualizer.getCanvas(), x, y);
			x += properties.getYAxisDrawingInfo().getOffChartCanvasWidth();
			chartMainPanel.add(((VerticalAxisVisualizer)yAxisVisualizer).getTextPositionerAbsolutePanel(), x, y); 
			DOM.setElementAttribute(((VerticalAxisVisualizer)yAxisVisualizer).getTextPositionerAbsolutePanel().getElement(),"zIndex", "1");
			DOM.setElementAttribute(((VerticalAxisVisualizer)yAxisVisualizer).getCanvas().getElement(), "zIndex", "-1");

		}
		if(xAxisVisualizer != null){
			switch (properties.getXAxisDrawingInfo().getAxisLocation()) {
			case BOTTOM:
				chartMainPanel.add(((HorizontalAxisVisualizer)xAxisVisualizer).getCanvas(),
						x,
						y + properties.getChartCanvasHeight() - properties.getXAxisDrawingInfo().getTickLengthOutsideAxisPanel());
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
			chartMainPanel.add(y2AxisVisualizer.getCanvas(),
					x + properties.getChartCanvasWidth() - (properties.getY2AxisDrawingInfo().getTickLength() - properties.getY2AxisDrawingInfo().getOffChartCanvasWidth()),
					y);
			chartMainPanel.add(((VerticalAxisVisualizer)y2AxisVisualizer).getTextPositionerAbsolutePanel(),
					x + properties.getChartCanvasWidth() - 100,
					y); //TODO pos
			DOM.setElementAttribute(((VerticalAxisVisualizer)y2AxisVisualizer).getTextPositionerAbsolutePanel().getElement(),"zIndex", "1");
			DOM.setElementAttribute(((VerticalAxisVisualizer)y2AxisVisualizer).getCanvas().getElement(), "zIndex", "-1");
		}
		chartMainPanel.add(chartCanvas, x, y);
		DOM.setElementAttribute(chartCanvas.getElement(), "zIndex", "-2");
	}
	
	public void displayAxes(){
		if(yAxisVisualizer != null &&  modelManager.getyMin() != null){
			yAxisVisualizer.display();
		}
		if(xAxisVisualizer != null  && modelManager.getxMin() != null){
			((HorizontalAxisVisualizer)xAxisVisualizer).display();
		}
		if(y2AxisVisualizer != null &&  modelManager.getY2Min() != null){
			y2AxisVisualizer.display();
		}
	}
	
	protected void init(Axis xAxis, Axis yAxis, Axis y2Axis){
		this.chartCanvas = new DrawingArea(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		Rectangle border = new Rectangle(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		border.setFillOpacity(0);
		((DrawingArea)chartCanvas).add(border);
		
		if(xAxis != null){
			DrawingArea xCanvas = new DrawingArea(
					properties.getChartCanvasWidth(),
					properties.getXAxisDrawingInfo().getAxisPanelHeight() + properties.getXAxisDrawingInfo().getTickLengthOutsideAxisPanel());
			AbsolutePanel ap =  new AbsolutePanel();
			ap.setPixelSize(
					properties.getChartCanvasWidth(), 
					properties.getXAxisDrawingInfo().getAxisPanelHeight());
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
			yAxisVisualizer = new VerticalAxisVisualizer(yCanvas, ap, yAxis, modelManager, Axes.Y);
		}
		if(y2Axis != null){
			DrawingArea y2Canvas = new DrawingArea(
					properties.getY2AxisDrawingInfo().getTickLength(),
					properties.getChartCanvasHeight());
			AbsolutePanel ap =  new AbsolutePanel();
			ap.setPixelSize(
					100,  //TODO
					properties.getChartCanvasHeight());
			y2AxisVisualizer = new VerticalAxisVisualizer(y2Canvas, ap, y2Axis, modelManager, Axes.Y2);
		}
		
	}
	
	/**
 	 * draws a curve on canvas
 	 * @param curve
 	 */
	public void addCurve(Curve curve){
		ArrayList<CurveVisualizer> vs = new ArrayList<CurveVisualizer>();
		if(curve.getCurveDrawingInfo().hasLine()){
			LineCurveVisualizer lcv = new LineCurveVisualizer(chartCanvas, curve, modelManager);
			lcv.drawCurve(modelManager.getViewportMin(), modelManager.getViewportMax());
			vs.add(lcv);
		}
		if(curve.getCurveDrawingInfo().hasPoints()){
			PointCurveVisualizer pcv = new PointCurveVisualizer(chartCanvas, curve, modelManager, properties);
			pcv.drawCurve(modelManager.getViewportMin(), modelManager.getViewportMax());
			vs.add(pcv);
		}
		curveVisualizers.put(curve, vs);
		updateHierarchy();
		displayAxes();
	}
 
 	/**
 	 * removes a curve from canvas
 	 * @param index
 	 */
 	public void removeCurve(Curve curve){
 		for(CurveVisualizer cv:curveVisualizers.get(curve)){
 			cv.removeFromCanvas();
 		}
 		curveVisualizers.remove(curve);
 	}
 	
 	public Widget getChartCanvas(){
 		return chartCanvas;		
 	}
	
 	@Override
	public void moveViewport(double dx) {
		xAxisVisualizer.moveViewport(dx);
		for(Curve curve : curveVisualizers.keySet())
			for(CurveVisualizer visualizer: curveVisualizers.get(curve))
				visualizer.moveViewport(dx);		

		updateHierarchy();
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		xAxisVisualizer.setViewport(viewportMin, viewportMax);
 		for(Curve curve : curveVisualizers.keySet())
			for(CurveVisualizer visualizer: curveVisualizers.get(curve))
				visualizer.setViewport(viewportMin, viewportMax);

 		updateHierarchy();
	}
	
	private void updateHierarchy(){
		//border
		((DrawingArea)chartCanvas).bringToFront(((DrawingArea)chartCanvas).getVectorObject(0));
		if(yAxisVisualizer != null){
			chartMainPanel.getElement().appendChild(((VerticalAxisVisualizer)yAxisVisualizer).getCanvas().getElement());
			chartMainPanel.getElement().appendChild(((VerticalAxisVisualizer)yAxisVisualizer).getTextPositionerAbsolutePanel().getElement());
		}
		if(y2AxisVisualizer != null){
			chartMainPanel.getElement().appendChild(((VerticalAxisVisualizer)y2AxisVisualizer).getCanvas().getElement());
			chartMainPanel.getElement().appendChild(((VerticalAxisVisualizer)y2AxisVisualizer).getTextPositionerAbsolutePanel().getElement());
		}
		if(xAxisVisualizer != null){
			chartMainPanel.getElement().appendChild(((HorizontalAxisVisualizer)xAxisVisualizer).getCanvas().getElement());
			chartMainPanel.getElement().appendChild(((HorizontalAxisVisualizer)xAxisVisualizer).getTextPositionerAbsolutePanel().getElement());
		}
	}


	@Override
	public void addMark(Mark mark) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeMark(Mark mark) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TreeMap<Mark, int[]> getMarkBoundingBoxes() {
		// TODO Auto-generated method stub
		return null;
	}


}

