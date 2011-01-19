package com.inepex.inecharting.chartwidget.model;

import java.util.TreeMap;

import com.google.gwt.user.client.Random;
import com.inepex.inecharting.chartwidget.properties.CurveDrawingPolicy;
import com.inepex.inecharting.chartwidget.properties.ShapeDrawingInfo;


/**
 * 
 *@author Miklós Süveges / Inepex Ltd
 */
public final class Curve {
	/**
	 * Curve's axis
	 */
	public static enum Axis{
		Y,
		Y2,
		NO_AXIS
	}
	
	
	//data fields
	/**
	 * Unique name of the curve, helps identifying at event-handling which curve (or a curve's point) was selected
	 */
	private String name;
	/**
	 * the underlying data
	 */
	private TreeMap<Double, Double> dataMap;
	/**
	 * the highest value in dataMap
	 */
	private double maxValue;
	/**
	 * the lowest value in dataMap
	 */
	private double minValue;
	/**
	 * curve's axis
	 */
	private Axis curveAxis;
	/**
	 * a collection for calculated points per data
	 * points inside are unfiltered.
	 * should be updated when viewport's width changes
	 */
	private TreeMap<Double, Point> calculatedPoints;
	/**
	 * drawing methods should access this collection
	 * contains overlap filtered points
	 * should be cleared when resolution changes
	 * if no overlapping points have been found, this map equals with calculatedPoints, otherwise
	 * imaginary points replaced the problematic ones -> note that multiple keys can map the same imaginary point
	 */
	private TreeMap<Double, Point> pointsToDraw;
	
	//curve's UI fields
	private boolean hasLine;
	private boolean hasPoints;
	private ShapeDrawingInfo lineDrawInfo;
	private CurveDrawingPolicy policy;
		
	public Curve(String name, TreeMap<Double, Double> dataMap) {
		this.name = name;
		this.dataMap = dataMap;
		minValue = maxValue = dataMap.get(dataMap.firstKey());
		for(Double time:dataMap.keySet()){
			double actual = dataMap.get(time);
			if(actual > maxValue)
				maxValue = actual;
			else if (actual < minValue)
				minValue = actual;
		}
		
		//default values
		
		hasLine = true;
		hasPoints = true;
		curveAxis = Axis.Y;
		calculatedPoints = new TreeMap<Double,Point>();
		pointsToDraw = new TreeMap<Double, Point>();
		lineDrawInfo = ShapeDrawingInfo.getDefaultShapeDrawingInfo();
	}
	
	
/* GETTERS AND SETTERS */
	public TreeMap<Double, Double> getDataMap() {
		return dataMap;
	}
	/**
	 * 
	 * @param viewportMin
	 * @param viewportMax
	 * @return the visible part of the datamap
	 */
	public TreeMap<Double, Double> getVisibleDataMap(double viewportMin, double viewportMax){
		TreeMap<Double, Double> visible = new TreeMap<Double, Double>();
		for (Double x:dataMap.keySet()){
			if(x > viewportMin && x < viewportMax)
				visible.put(x, dataMap.get(x));
		}
		return visible;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public Axis getCurveAxis() {
		return curveAxis;
	}

	public void setCurveAxis(Axis curveAxis) {
		this.curveAxis = curveAxis;
	}

	public boolean hasLine() {
		return hasLine;
	}

	public void setHasLine(boolean drawLine) {
		this.hasLine = drawLine;
	}

	public boolean hasPoints() {
		return hasPoints;
	}

	public void setHasPoints(boolean hasPoints) {
		this.hasPoints = hasPoints;
	}

	public double getLastInvisiblePointBeforeViewport(double viewportMin){
		double lastX = dataMap.firstKey();
		if(lastX >= viewportMin)
			return Double.NaN;
		for (Double x:dataMap.keySet()){
			if(x > viewportMin)
				return lastX;
			lastX = x;
		}
		return Double.NaN;
	}

	public double getFirstInvisiblePointAfterViewport(double viewportMax){
		for (Double x:dataMap.keySet()){
			if(x > viewportMax)
				return x;
		}
		return Double.NaN;	
	}

	public TreeMap<Double, Point> getCalculatedPoints() {
		return calculatedPoints;
	}

	public TreeMap<Double, Point> getPointsToDraw() {
		return pointsToDraw;
	}

	public ShapeDrawingInfo getLineDrawInfo() {
		return lineDrawInfo;
	}

	public void setLineDrawInfo(ShapeDrawingInfo lineDrawInfo) {
		this.lineDrawInfo = lineDrawInfo;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Generates a random name (contains 12 0-9 digits) for this curve
	 * @return the generated name
	 */
	public String generateRandomName(){
		String name = "";
		for(int i = 0; i < 12; i++)
			name += Random.nextInt(10);
		this.name = name;
		return name;
	}

	public CurveDrawingPolicy getPolicy() {
		if(policy == null)
			policy = CurveDrawingPolicy.getDefaultCurveDrawingPolicy();
		return policy;
	}
	
	public void setPolicy(CurveDrawingPolicy policy) {
		this.policy = policy;
	}
}
