package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.properties.CurveDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.ShapeDrawingInfo;


/**
 * 
 *@author Miklós Süveges / Inepex Ltd
 */
public final class Curve extends GraphicalObject 
implements Comparable<Curve>, HasState{
	
	
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
	 * X means 'vertical axis independent curve'
	 */
	private Axes curveAxis;
	/**
	 * a collection for calculated points per data
	 * points inside are unfiltered.
	 * should be updated when viewport's width changes
	 */
	private TreeMap<Double, Point> calculatedPoints;
	
	/**
	 * 
	 * contains overlap-filtered points
	 * should be cleared when resolution changes
	 * if no overlapping points have been found, this map equals with calculatedPoints, otherwise
	 * imaginary points replaced the problematic ones -> note that multiple keys can map the same imaginary point
	 */
	private TreeMap<Double, Point> pointsToDraw;
	
	/**
	 * drawing methods should access this collection
	 * the ModelManager puts the 2 invisible points closest to viewport (if there is any) for easiest curve-drawing 
	 */
	private ArrayList<Point> visiblePoints;
	
	private CurveDrawingInfo curveDrawingInfo;
		
	public Curve(CurveDrawingInfo curveDrawingInfo, TreeMap<Double, Double> dataMap) {
		this.curveDrawingInfo = curveDrawingInfo;
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
		setzIndex(curveDrawingInfo.getDefaultzIndex());
		calculatedPoints = new TreeMap<Double,Point>();
		pointsToDraw = new TreeMap<Double, Point>();
		visiblePoints = new ArrayList<Point>();
		state = State.NORMAL;
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
	
	public ArrayList<Point> getVisiblePoints() {
		return visiblePoints;
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

	public Axes getCurveAxis() {
		return curveAxis;
	}

	public void setCurveAxis(Axes curveAxis) {
		this.curveAxis = curveAxis;
	}

	boolean hasLine() {
		return curveDrawingInfo.hasLine();
	}

	boolean hasPoints() {
		return curveDrawingInfo.hasPoints();
	}

	public Double getLastInvisiblePointBeforeViewport(double viewportMin){
		double lastX = dataMap.firstKey();
		if(lastX >= viewportMin)
			return null;
		for (Double x:dataMap.keySet()){
			if(x > viewportMin)
				return lastX;
			lastX = x;
		}
		return null;
	}

	public Double getFirstInvisiblePointAfterViewport(double viewportMax){
		for (Double x:dataMap.keySet()){
			if(x > viewportMax)
				return x;
		}
		return null;	
	}

	public TreeMap<Double, Point> getCalculatedPoints() {
		return calculatedPoints;
	}
	
	public TreeMap<Double, Point> getPointsToDraw() {
		return pointsToDraw;
	}

	public ShapeDrawingInfo getLineDrawInfo() {
		return curveDrawingInfo.getLineDrawingInfo(state);
	}

	public CurveDrawingInfo getCurveDrawingInfo() {
		if(curveDrawingInfo == null)
			curveDrawingInfo = CurveDrawingInfo.getDefaultCurveDrawingInfo();
		return curveDrawingInfo;
	}
	
	public void setCurveDrawingInfo(CurveDrawingInfo curveDrawingInfo) {
		this.curveDrawingInfo = curveDrawingInfo;
	}


	@Override
	public int compareTo(Curve o) {
		return curveDrawingInfo.getName().compareTo(o.getCurveDrawingInfo().getName());
	}
}
