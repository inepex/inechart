package com.inepex.inecharting.chartwidget;

import java.util.TreeMap;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory.DrawingTool;
import com.inepex.inecharting.chartwidget.graphics.HasViewport;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.TimeAxis;
import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo.AxisType;

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
	private TreeMap<String, Curve> curves;
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
		modelManager = new ModelManager(properties);
		modelManager.setViewport(properties.getDefaultViewportMin(), properties.getDefaultViewportMax());
		curves = new TreeMap<String, Curve>();
		
		//creating axes
		if(properties.getXAxisDrawingInfo() != null){
			if(properties.getXAxisDrawingInfo().getType().equals(AxisType.TIME))
				xAxis = new TimeAxis(properties.getXAxisDrawingInfo());
			else
				xAxis = new Axis(properties.getXAxisDrawingInfo());					
		}
		if(properties.getYAxisDrawingInfo() != null){
			if(properties.getYAxisDrawingInfo().getType().equals(AxisType.TIME))
				yAxis = new TimeAxis(properties.getYAxisDrawingInfo());
			else
				yAxis = new Axis(properties.getYAxisDrawingInfo());		
		}
		if(properties.getY2AxisDrawingInfo() != null){
			if(properties.getY2AxisDrawingInfo().getType().equals(AxisType.TIME))
				y2Axis = new TimeAxis(properties.getY2AxisDrawingInfo());
			else
				y2Axis = new Axis(properties.getY2AxisDrawingInfo());		
		}
		drawingFactory = new DrawingFactory(mainPanel,DrawingTool.VAADIN_GWT_GRAPHICS, properties, modelManager,xAxis,yAxis,y2Axis);
		drawingFactory.assembleLayout();
	}
	


	/**
	 * Adds a curve to the chart
	 * @param name
	 * @param dataMap
	 */
	public void addCurve(Curve curve){
		curves.put(curve.getName(), curve);
		//let's check if the new curve had a lower value on x than the actual minimum
		Double xMin = modelManager.getxMin();
		Double xMax = modelManager.getxMax();
		if(xMin  == null){
			xMin = curve.getDataMap().firstKey();
			xMax = curve.getDataMap().lastKey();
		}
		else{
			for(String n : curves.keySet()){
				if(curves.get(n).getDataMap().firstKey() < xMin)
					xMin = curves.get(n).getDataMap().firstKey();
				if(curves.get(n).getDataMap().lastKey() > xMax)
					xMax = curves.get(n).getDataMap().lastKey();
			}
		}
		if(modelManager.getxMin() != xMin){
			//if there is a new minimum on x and there are previously added curves, we simply shift their points  
			if(curves.size() > 1){
				double dx = modelManager.getxMin() - xMin;
				for(String n : curves.keySet()){
					modelManager.addDistanceToAllPoints(curves.get(n), dx);
				}
			}
			modelManager.setxMin(xMin);
		}
		if(modelManager.getxMax() != xMax)
			modelManager.setxMax(xMax);
		getPointsForCurve(curve);
		//display curve
		drawingFactory.addCurve(curve);
	}

	
	@Override
	public void moveViewport(double dx) {
		modelManager.setViewport(modelManager.getViewportMin() + dx, modelManager.getViewportMax()+dx);
		for(String curveName : curves.keySet()){
			Curve actualCurve = curves.get(curveName);
			getPointsForCurve(actualCurve);
			
		}
		drawingFactory.moveViewport(dx);
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		double shrinkRatio = (modelManager.getViewportMax()  -modelManager.getViewportMin()) / (viewportMax - viewportMin);
		modelManager.setViewport(viewportMin, viewportMax);
		for(String curveName : curves.keySet()){
			Curve actualCurve = curves.get(curveName);
			modelManager.setXPositionForCalculatedPoints(actualCurve, shrinkRatio);
			actualCurve.getPointsToDraw().clear();
			getPointsForCurve(actualCurve);
			
		}
		drawingFactory.setViewPort(viewportMin, viewportMax);
	}
	
	private void getPointsForCurve(Curve curve){
		Double start, stop;
		if(curve.getPolicy().isPreCalculatePoints()){
			start = curve.getDataMap().firstKey();
			stop = curve.getDataMap().lastKey();
		}
		else{
			start = curve.getLastInvisiblePointBeforeViewport(modelManager.getViewportMin());
			stop = curve.getFirstInvisiblePointAfterViewport(modelManager.getViewportMax());
			if(start == null)
				start = curve.getDataMap().firstKey();
			if(stop == null)
				stop = curve.getDataMap().lastKey();
		}
		modelManager.calculateAndSetPointsForInterval(curve, start, stop);
		modelManager.filterOverlappingPoints(curve, start, stop);
	}
	
}
