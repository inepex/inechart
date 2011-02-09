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
		drawingFactory = new DrawingFactory(mainPanel, DrawingTool.VAADIN_GWT_GRAPHICS, properties, modelManager, xAxis, yAxis, y2Axis);
		drawingFactory.assembleLayout();
	}
	


	/**
	 * Adds a curve to the chart
	 * @param name
	 * @param dataMap
	 */
	public void addCurve(Curve curve){
		curves.put(curve.getName(), curve);
		//setting the Horizontal min and max
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
		
		//setting the Vertical min and max
		if(curve.getCurveAxis().equals(com.inepex.inecharting.chartwidget.model.Curve.Axis.Y)){
			if(modelManager.getyMin() == null || modelManager.getyMin()  > curve.getMinValue())
				modelManager.setyMin(curve.getMinValue());
			if(modelManager.getyMax() == null || modelManager.getyMax()  < curve.getMaxValue())
				modelManager.setyMax(curve.getMaxValue());
			modelManager.getAxisCalculator().setVerticalAxis(yAxis,Curve.Axis.Y);
		}
		else if(curve.getCurveAxis().equals(com.inepex.inecharting.chartwidget.model.Curve.Axis.Y2)){
			if(modelManager.getY2Min() == null || modelManager.getY2Min()  > curve.getMinValue())
				modelManager.setY2Min(curve.getMinValue());
			if(modelManager.getY2Max() == null || modelManager.getY2Max()  < curve.getMaxValue())
				modelManager.setY2Max(curve.getMaxValue());
			modelManager.getAxisCalculator().setVerticalAxis(yAxis,Curve.Axis.Y2);
		}
		//calculate points
		getPointsForCurve(curve);
		//first time when curve added (axes calculations, drawing needed)s
		if(curves.size() == 1){
			if(xAxis != null)
				modelManager.getAxisCalculator().setHorizontalAxis(xAxis);
			switch (curve.getCurveAxis()) {
			case NO_AXIS:
				break;
			case Y:
				if(yAxis != null)
					modelManager.getAxisCalculator().setVerticalAxis(yAxis,curve.getCurveAxis());
				break;
			case Y2:
				if(y2Axis != null)
					modelManager.getAxisCalculator().setVerticalAxis(y2Axis,curve.getCurveAxis());
				break;
			default:
				break;
			}
			
		}
		//first curve on Y
		if(yAxis != null && yAxis.getTickDistance() == 0 && curve.getCurveAxis().equals(Curve.Axis.Y)){
			modelManager.getAxisCalculator().setVerticalAxis(yAxis,Curve.Axis.Y);
		}
		//first curve on Y2 
		if(y2Axis != null && y2Axis.getTickDistance() == 0 && curve.getCurveAxis().equals(Curve.Axis.Y2)){
			modelManager.getAxisCalculator().setVerticalAxis(y2Axis,Curve.Axis.Y2);
		}
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
		if(xAxis != null)
			modelManager.getAxisCalculator().setHorizontalAxis(xAxis);
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
