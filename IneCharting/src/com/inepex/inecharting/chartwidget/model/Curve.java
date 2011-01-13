package com.inepex.inecharting.chartwidget.model;

import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.properties.ShapeDrawingInfo;



public final class Curve {
	/**
	 * Curve's axis
	 */
	public static enum Axis{
		Y,
		Y2,
		NO_AXIS
	}
	
	public static enum ImaginaryPointValuePolicy{
		FIRST_POINT,
		LAST_POINT,
		LOWER,
		HIGHER,
		AVERAGE
	}
	//data fields
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

	/* model-to-pixel policy */
	public static final ImaginaryPointValuePolicy DEFAULT_OVERLAPPING_POLICY = ImaginaryPointValuePolicy.AVERAGE;
	public static final int DEFAULT_X_OVERLAPPING_WIDTH = 3;
	public static final int DEFAULT_SQUARE_OVERLAPPING_SIZE = 10;

	private int overlapFilterSquareSize ;
	private ImaginaryPointValuePolicy overlapFilterPolicy;
	private int overlapFilterXWidth;
	private boolean mathematicalRounding;
	
	/* model handling, drawing policy */
	private boolean preDrawLines;
	private boolean preDrawPoints;
	private boolean preCalculatePoints;
	private boolean keepInvisibleGraphicalObjects;
	
	private Curve(TreeMap<Double, Double> dataMap) {
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
		defineOverlapXPolicy(DEFAULT_OVERLAPPING_POLICY, DEFAULT_X_OVERLAPPING_WIDTH, DEFAULT_SQUARE_OVERLAPPING_SIZE);
		mathematicalRounding = true;
		hasLine = true;
		hasPoints = false;
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

	/**
	 * Sets a policy for overlapping X-value points.
	 * @param policy one policy from the class' static final fields 
	 * @param width the width
	 */
	public void defineOverlapXPolicy(ImaginaryPointValuePolicy policy, int overlapFilterXWidth, int overlapFilterSquareSize){
		this.overlapFilterPolicy = policy;
		this.overlapFilterXWidth = overlapFilterXWidth;
		this.overlapFilterSquareSize = overlapFilterSquareSize;
	}
	
	/**
	 * Defines the model-to-pixel function should use mathematical rounding or should use double to integer casting. 
	 * @param mathematicalRounding true if mathematical rounding should being used.
	 */
	public void setMathematicalRounding(boolean mathematicalRounding) {
		this.mathematicalRounding = mathematicalRounding;
	}
	
	public int getOverlapFilterSquareSize() {
		return overlapFilterSquareSize;
	}
	
	public ImaginaryPointValuePolicy getOverlapFilerPolicy() {
		return overlapFilterPolicy;
	}
	
	public int getOverlapFilterXWidth() {
		return overlapFilterXWidth;
	}
	
	public boolean isMathematicalRounding() {
		return mathematicalRounding;
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

	public boolean isPreDrawLines() {
		return preDrawLines;
	}

	public boolean isPreDrawPoints() {
		return preDrawPoints;
	}

	public boolean isPreCalculatePoints() {
		return preCalculatePoints;
	}

	public boolean isKeepInvisibleGraphicalObjects() {
		return keepInvisibleGraphicalObjects;
	}

	public void setLineDrawInfo(ShapeDrawingInfo lineDrawInfo) {
		this.lineDrawInfo = lineDrawInfo;
	}

	public void setPreDrawLines(boolean preDrawLines) {
		this.preDrawLines = preDrawLines;
	}

	public void setPreDrawPoints(boolean preDrawPoints) {
		this.preDrawPoints = preDrawPoints;
	}

	public void setPreCalculatePoints(boolean preCalculatePoints) {
		this.preCalculatePoints = preCalculatePoints;
	}

	public void setKeepInvisibleGraphicalObjects(
			boolean keepInvisibleGraphicalObjects) {
		this.keepInvisibleGraphicalObjects = keepInvisibleGraphicalObjects;
	}
	
}
