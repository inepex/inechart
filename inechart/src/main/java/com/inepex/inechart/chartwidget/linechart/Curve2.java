package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.data.AbstractDataEntry;
import com.inepex.inechart.chartwidget.data.AbstractXYDataSet;
import com.inepex.inechart.chartwidget.misc.HasShadow;
import com.inepex.inechart.chartwidget.misc.HasZIndex;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.shape.Shape;

/**
 * 
 * Represents a Line- or a Point- (or both) curve's model.
 * Stores the information about how it should be displayed.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public class Curve2 implements HasZIndex, HasShadow, Comparable<Curve2>{
	// comparison helper fields
	private static int highestComparableNo = 0;
	private int comparableNo;

	/**
	 * Underlying data, curve must not change it
	 */
	AbstractXYDataSet dataSet;
	
	/**
	 * {@link DataPoint2}s created from {@link AbstractDataEntry}s
	 */
	ArrayList<DataPoint2> dataPoints;
	
	
	// Fills
	TreeMap<Curve2, Color> toCurveFills;
	TreeMap<Double, Color> toYFills;
	
	// line
	boolean hasLine = true;
	LineProperties lineProperties;
	boolean autoFill = false;
	ArrayList<DataPoint2> discontinuitiesAsPoint;
	ArrayList<Double> discontinuities;

	// points
	boolean hasPoint = false;
	Shape pointShape;
	ArrayList<DataPoint2> selectedPoints;

	// shadow
	Color shadowColor;
	double shadowOffsetX, shadowOffsetY;
	boolean hasShadow = true;
	int zIndex = Integer.MIN_VALUE;

	public Curve2(){
		comparableNo = highestComparableNo++;
		dataPoints = new ArrayList<DataPoint2>();
		selectedPoints = new ArrayList<DataPoint2>();
//		pointShape = Defaults.normalPoint();
	}

	
	public Curve2(AbstractXYDataSet dataSet){
		this();
		this.dataSet = dataSet;
	}
	
	/**
	 * @return the dataSet
	 */
	public AbstractXYDataSet getDataSet() {
		return dataSet;
	}
	
	/**
	 * Fills the area between this and the given curve
	 * 
	 * @param curve
	 * @param color
	 *            {@link Color}
	 */
	public void addFill(Curve2 curve, Color color) {
		if (this.toCurveFills == null)
			toCurveFills = new TreeMap<Curve2, Color>();
		toCurveFills.put(curve, color);
	}

	/**
	 * Fills an area between the curve and a horizontal line at the given y
	 * value
	 * 
	 * @param y
	 *            y position of the line
	 * @param color
	 *            {@link Color}
	 */
	public void addFill(double y, Color color) {
		if (this.toYFills == null)
			toYFills = new TreeMap<Double, Color>();
		toYFills.put(y, color);
	}

	/**
	 * Fills an area between a tick and the curve Same as
	 * addFill(tick.getPosition(), color);
	 * 
	 * @param tick
	 * @param color
	 */
	public void addFill(Tick tick, Color color) {
		if (this.toYFills == null)
			toYFills = new TreeMap<Double, Color>();
		toYFills.put(tick.getPosition(), color);
	}


	/**
	 * Fills the area between this and the given curve
	 * 
	 * @param curve
	 * @param color
	 *            {@link Color}
	 */
	public void removeFill(Curve curve) {
		toCurveFills.remove(curve);
	}

	/**
	 * Fills the area between the curve and a horizontal line at the given y
	 * value
	 * 
	 * @param y
	 *            y position of the line
	 * @param color
	 *            {@link Color}
	 */
	public void removeFill(double y) {
		toYFills.remove(y);
	}


	/**
	 * @return the lineProperties
	 */
	public LineProperties getLineProperties() {
		return lineProperties;
	}

	/**
	 * @param lineProperties
	 *            the line of the linechart
	 */
	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}


	@Override
	public void setShadowOffsetX(double offsetX) {
		this.shadowOffsetX = offsetX;
	}

	@Override
	public void setShadowOffsetY(double offsetY) {
		this.shadowOffsetY = offsetY;
	}

	@Override
	public double getShadowOffsetX() {
		return shadowOffsetX;
	}

	@Override
	public double getShadowOffsetY() {
		return shadowOffsetY;
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public int getZIndex() {
		return this.zIndex;
	}

	@Override
	public Color getShadowColor() {
		return shadowColor;
	}

	@Override
	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}


	public boolean isAutoFill() {
		return autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}

	public boolean isHasShadow() {
		return hasShadow;
	}

	public void setHasShadow(boolean hasShadow) {
		this.hasShadow = hasShadow;
	}

	@Override
	public int compareTo(Curve2 o) {
		return comparableNo - o.comparableNo;
	}


	/**
	 * @return the hasLine
	 */
	public boolean isHasLine() {
		return hasLine;
	}


	/**
	 * @param hasLine the hasLine to set
	 */
	public void setHasLine(boolean hasLine) {
		this.hasLine = hasLine;
	}


	/**
	 * @return the hasPoint
	 */
	public boolean isHasPoint() {
		return hasPoint;
	}


	/**
	 * @param hasPoint the hasPoint to set
	 */
	public void setHasPoints(boolean hasPoint) {
		this.hasPoint = hasPoint;
	}

	
	public void addDiscontinuity(double discontinuity){
		discontinuities.add(discontinuity);
	}
	
	public boolean isPointSelected(DataPoint2 dp){
		for(DataPoint2 selected : selectedPoints){
			if(selected.compareTo(dp) == 0){
				return true;
			}
		}
		return false;
	}
	
	protected void selectPoint(DataPoint2 dp){
		for(DataPoint2 selected : selectedPoints){
			if(selected.compareTo(dp) == 0){
				return;
			}
		}
		selectedPoints.add(dp);
	}
	
	protected void deselectPoint(DataPoint2 dp){
		DataPoint2 toRemove = null;
		for(DataPoint2 selected : selectedPoints){
			if(selected.compareTo(dp) == 0){
				toRemove = selected;
				break;
			}
		}
		if(toRemove != null){
			selectedPoints.remove(toRemove);
		}
	}
	
}
