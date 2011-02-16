package com.inepex.inecharting.chartwidget;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.graphics.canvas.DrawingFactoryImplCanvas;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.DrawingFactoryImplGwtGraphics;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis;
import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo.AxisType;
import com.inepex.inecharting.chartwidget.properties.HorizontalTimeAxisDrawingInfo;

/**
 * 
 * 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class IneChart extends Composite implements HasViewport{
	//data fields
	private ModelManager modelManager;
	private DrawingFactory drawingFactory;
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
		modelManager = new ModelManager(properties);
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
		
		//init ui
		switch (properties.getDrawingTool()) {
		case VAADIN_GWT_GRAPHICS:
			drawingFactory = new DrawingFactoryImplGwtGraphics(mainPanel, properties, modelManager, xAxis, yAxis, y2Axis);
			break;
		case INECANVAS:
			drawingFactory = new DrawingFactoryImplCanvas(mainPanel, properties, modelManager, xAxis, yAxis, y2Axis);
			break;
		default:
			break;
		}
		
		
		//create canvas, panel hierarchy
		drawingFactory.assembleLayout();
	}
	

	/**
	 * Adds a curve to the chart
	 * @param curve
	 */
	public void addCurve(Curve curve){
		curves.add(curve);

		//calculate extremes of curves and set axes
		modelManager.getAxisCalculator().calculateAxes(curves, xAxis, yAxis, y2Axis);
		
		//calculate points
		modelManager.getPointsForCurve(curve);
		
		//display curve
		drawingFactory.addCurve(curve);
	}

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
	
	public void removeCurves(String curveName){
		ArrayList<Curve> toRemove = new ArrayList<Curve>();
		for(Curve actCurve : curves){
			if (actCurve.getName().equals(curveName)) {
				toRemove.add(actCurve);
			}
		}
		curves.removeAll(toRemove);
		
		//calculate extremes of curves and set axes
		modelManager.getAxisCalculator().calculateAxes(curves, xAxis, yAxis, y2Axis);
	}
		
	@Override
	public void moveViewport(double dx) {
		modelManager.setViewport(modelManager.getViewportMin() + dx, modelManager.getViewportMax()+dx);
		for(Curve actCurve : curves){
			modelManager.getPointsForCurve(actCurve);	
		}
		drawingFactory.moveViewport(dx);
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		double shrinkRatio = (modelManager.getViewportMax() - modelManager.getViewportMin()) / (viewportMax - viewportMin);
		modelManager.setViewport(viewportMin, viewportMax);
		//rescale xAxis model
//		if(xAxis != null)
//			modelManager.getAxisCalculator().setHorizontalAxis(xAxis);
		for(Curve actualCurve : curves){
			modelManager.setXPositionForCalculatedPoints(actualCurve, shrinkRatio);
			actualCurve.getPointsToDraw().clear();
			modelManager.getPointsForCurve(actualCurve);
		}
		drawingFactory.setViewPort(viewportMin, viewportMax);
	}
	
	
	
}
