package com.inepex.inecharting.chartwidget;

import java.util.TreeMap;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inecharting.chartwidget.graphics.CurveVisualizer;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory.DrawingTool;
import com.inepex.inecharting.chartwidget.graphics.HasViewport;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;

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
	//ui fields
	private AbsolutePanel mainPanel;
	
	/**
	 * Constructs a chart widget with the given parameters 
	 * @param properties the parameter container
	 */
	public IneChart(IneChartProperties properties){
		this.properties = properties;
		init();
		initLayout();
	}
	
	/**
	 * Initializes all managers, factories
	 */
	private void init(){
		modelManager = new ModelManager(properties);
		modelManager.setViewport(properties.getDefaultViewportMin(), properties.getDefaultViewportMax());
		drawingFactory = new DrawingFactory(DrawingTool.VAADIN_GWT_GRAPHICS, properties, modelManager);
		curves = new TreeMap<String, Curve>();
	}
	
	/**
	 * Initializes the widget's structure and other UI fields
	 */
	private void initLayout(){
		mainPanel = new AbsolutePanel();
		mainPanel.add(drawingFactory.getChartCanvas());
		
		
		initWidget(mainPanel); 
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
		if(xMin  == null){
			xMin = curve.getDataMap().firstKey();
		}
		else{
			for(String n : curves.keySet()){
				if(curves.get(n).getDataMap().firstKey() < xMin)
					xMin = curves.get(n).getDataMap().firstKey();
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
