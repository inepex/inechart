package com.inepex.inecharting.chartwidget;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inecharting.chartwidget.event.EventManager;
import com.inepex.inecharting.chartwidget.event.ExtremesChangeEvent;
import com.inepex.inecharting.chartwidget.event.ExtremesChangeHandler;
import com.inepex.inecharting.chartwidget.event.StateChangeEvent;
import com.inepex.inecharting.chartwidget.event.StateChangeHandler;
import com.inepex.inecharting.chartwidget.event.ViewportChangeEvent;
import com.inepex.inecharting.chartwidget.event.ViewportChangeHandler;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.graphics.canvas.DrawingFactoryImplCanvas;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.DrawingFactoryImplGwtGraphics;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.Mark;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis;
import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo.AxisType;
import com.inepex.inecharting.chartwidget.properties.HorizontalTimeAxisDrawingInfo;

/**
 * A chart widget.
 * Use addCurve to add new curve,
 * use add...Handler methods to register an EventHandler.
 * use  moveViewport  
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class IneChart extends Composite implements HasViewport{
	//data fields
	private ModelManager modelManager;
	private DrawingFactory drawingFactory;
	private EventManager eventManager;
	private IneChartProperties properties;
	private ArrayList<Curve> curves;
	private Axis xAxis = null;
	private Axis yAxis = null;
	private Axis y2Axis = null;
	//ui fields
	private AbsolutePanel mainPanel;
	
	/**
	 * Constructs a chart widget with the given parameters 
	 * @param properties the parameter container
	 */
	public IneChart(IneChartProperties properties){
		this.properties = properties;
		init();
		initWidget(mainPanel); 
	}
	
	/**
	 * Initializes all managers, factories, other data fields
	 */
	private void init(){
		mainPanel = new AbsolutePanel();
		curves = new ArrayList<Curve>();

		//init model
		modelManager = ModelManager.create(properties);
		modelManager.setViewport(properties.getDefaultViewportMin(), properties.getDefaultViewportMax());
		
		//creating axes
		if(properties.getXAxisDrawingInfo() != null){
			if(properties.getXAxisDrawingInfo().getType().equals(AxisType.TIME))
				xAxis = new HorizontalTimeAxis((HorizontalTimeAxisDrawingInfo) properties.getXAxisDrawingInfo());
			else
				xAxis = new Axis(properties.getXAxisDrawingInfo());					
		}
		if(properties.getYAxisDrawingInfo() != null){
			yAxis = new Axis(properties.getYAxisDrawingInfo());
		}
		if(properties.getY2AxisDrawingInfo() != null){
			y2Axis = new Axis(properties.getY2AxisDrawingInfo());		
		}
	
		//init UI
//		drawingFactory  = DrawingFactory.create();
		drawingFactory = GWT.create(DrawingFactory.class);
		drawingFactory.init(mainPanel, properties, modelManager, xAxis, yAxis, y2Axis);
		//create canvas, panel hierarchy
		drawingFactory.assembleLayout();
		//init events
		eventManager = EventManager.create(properties, this, drawingFactory, curves);
	}
	
	
	DrawingFactory getDrawingFactory() {
		return drawingFactory;
	}
	
	
/* PUBLC METHODS */
	/**
	 * Adds a curve to the chart
	 * @param curve
	 */
	public void addCurve(Curve curve){
		curves.add(curve);
		//calculate extremes of curves and set axes
		modelManager.getAxisCalculator().calculateAxes(curves, xAxis, yAxis, y2Axis);
		//calculate points
		modelManager.getPointsForCurve(curve, false);
		//display curve
		drawingFactory.addCurve(curve);
	}

	/**
	 * Removes a curve from chart
	 * @param curve
	 */
	public void removeCurve(Curve curve){
		for(Curve actCurve : curves){
			if(actCurve.equals(curve)){
				curves.remove(curve);
				break;
			}
		}
		//calculate extremes of curves and set axes
		modelManager.getAxisCalculator().calculateAxes(curves, xAxis, yAxis, y2Axis);
	}
	
	/**
	 * Removes curve(s) with the given name from chart
	 * @param curveName
	 */
	public void removeCurve(String curveName){
		ArrayList<Curve> toRemove = new ArrayList<Curve>();
		for(Curve actCurve : curves){
			if (actCurve.getCurveDrawingInfo().getName().equals(curveName)) {
				toRemove.add(actCurve);
			}
		}
		curves.removeAll(toRemove);
		//calculate extremes of curves and set axes
		modelManager.getAxisCalculator().calculateAxes(curves, xAxis, yAxis, y2Axis);
	}
		
	public void addMarks(ArrayList<Mark> marks){
		modelManager.getMarkContainer().addMarks(marks);
		moveViewport(0);
	}
	
	public void addMark(Mark mark){
		ModelManager.get().getMarkContainer().addMark(mark);
		drawingFactory.addMark(mark);
	}
	
	public void removeMark(Mark mark){
		ModelManager.get().getMarkContainer().removeMark(mark);
		drawingFactory.removeMark(mark);
	}
	
	@Override
	public void moveViewport(double dx) {
		modelManager.setViewport(modelManager.getViewportMin() + dx, modelManager.getViewportMax() + dx);
		//curves
		for(Curve actCurve : curves){
			modelManager.getPointsForCurve(actCurve, false);	
		}
		//marks
		ModelManager.get().getMarkContainer().moveViewport(dx);
		
		drawingFactory.moveViewport(dx);
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		modelManager.setViewport(viewportMin, viewportMax);
		
		//curves
		for(Curve actualCurve : curves){
			modelManager.getPointsForCurve(actualCurve, true);
		}
		//marks
		ModelManager.get().getMarkContainer().setViewport(viewportMin, viewportMax);
		drawingFactory.setViewport(viewportMin, viewportMax);
	}
	
	/**
	 * Register a handler for receiving {@link ExtremesChangeEvent}s
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addExtremesChangeHandler(ExtremesChangeHandler handler) {
		return eventManager.addExtremesChangeHandler(handler);
	}

	/**
	 * Register a handler for receiving {@link StateChangeEvent}s
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addStateChangeHandler(StateChangeHandler handler) {
		return eventManager.addStateChangeHandler(handler);
	}
	
	/**
	 * Register a handler for receiving {@link ViewportChangeEvent}s
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addViewportChangeHandler(ViewportChangeHandler handler) {
		return eventManager.addViewportChangeHandler(handler);
	}
}
