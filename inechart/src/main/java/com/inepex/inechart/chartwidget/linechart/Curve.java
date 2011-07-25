package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.misc.HasShadow;
import com.inepex.inechart.chartwidget.misc.HasZIndex;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.shape.Shape;

/**
 * 
 * Represents a Line- or a Point- (or both) curve's model.
 * Stores the {@link DataSet} and information about how it should be displayed.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public class Curve implements HasZIndex, HasShadow, Comparable<Curve>{
	// comparison helper fields
	private static int highestComparableNo = 0;
	private int comparableNo;

	/**
	 * data to display
	 * curve must not change it.
	 */
	DataSet dataSet;

	/**
	 * @return the dataSet
	 */
	public DataSet getDataSet() {
		return dataSet;
	}


	/* Lookout */
	// Fills
	TreeMap<Curve, Color> toCurveFills;
	TreeMap<Integer, Color> toCanvasYFills;
	TreeMap<Double, Color> toYFills;
	
	// line
	boolean hasLine = true;
	LineProperties lineProperties;
	boolean autoFill = false;
	ArrayList<double[]> discontinuities;

	// points
	boolean hasPoint = false;
	Shape normalPoint;
	Shape selectedPoint;
	boolean applyCurveShadowForPoint = true;
	boolean useCurveLineForNormalPoint = true;
	ArrayList<double[]> selectedPoints;

	// shadow
	Color shadowColor;
	double shadowOffsetX, shadowOffsetY;
	boolean hasShadow = true;
	int zIndex = Integer.MIN_VALUE;

	/**
	 * Creates an empty curve
	 */
	public Curve() {
		selectedPoints = new ArrayList<double[]>();
		discontinuities = new ArrayList<double[]>();
		setShadowOffsetY(Defaults.shadowOffsetX);
		setShadowOffsetX(Defaults.shadowOffsetY);
		setShadowColor(Defaults.shadowColor());
		comparableNo = highestComparableNo++;
	}


	/**
	 * Creates a curve from the given dataSet
	 * 
	 * @param dataSet
	 */
	public Curve(DataSet dataSet) {
		this();
		setdDataSet(dataSet);
	}
	
	
	/**
	 * Sets a new dataSet
	 * 
	 * @param dataSet
	 */
	public void setdDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * Fills the area between this and the given curve
	 * 
	 * @param curve
	 * @param color
	 *            {@link Color}
	 */
	public void addFill(Curve curve, Color color) {
		if (this.toCurveFills == null)
			toCurveFills = new TreeMap<Curve, Color>();
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
	 * Fills the area between the curve and a horizontal line at a fix position
	 * of the chart's canvas
	 * 
	 * @param y
	 *            in px (independent from axis scaling)
	 */
	public void addFill(int y, Color color) {
		if (this.toCanvasYFills == null)
			toCanvasYFills = new TreeMap<Integer, Color>();
		toCanvasYFills.put(y, color);
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
	 * Fills the area between the curve and a horizontal line at a fix position
	 * of the chart's canvas
	 * 
	 * @param y
	 *            in px (independent from axis scaling)
	 */
	public void removeFill(int y, Color color) {
		toCanvasYFills.remove(y);
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

	/**
	 * @return the normalPointShape
	 */
	public Shape getNormalPointShape() {
		return normalPoint;
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
	public int compareTo(Curve o) {
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


	/**
	 * @return the normalPoint
	 */
	public Shape getNormalPoint() {
		return normalPoint;
	}


	/**
	 * @param normalPoint the normalPoint to set
	 */
	public void setNormalPoint(Shape normalPoint) {
		this.normalPoint = normalPoint;
	}


	/**
	 * @return the selectedPoint
	 */
	public Shape getSelectedPoint() {
		return selectedPoint;
	}


	/**
	 * @param selectedPoint the selectedPoint to set
	 */
	public void setSelectedPoint(Shape selectedPoint) {
		this.selectedPoint = selectedPoint;
	}


	/**
	 * @return the applyCurveShadowForPoint
	 */
	public boolean isApplyCurveShadowForPoint() {
		return applyCurveShadowForPoint;
	}


	/**
	 * @param applyCurveShadowForPoint the applyCurveShadowForPoint to set
	 */
	public void setApplyCurveShadowForPoint(boolean applyCurveShadowForPoint) {
		this.applyCurveShadowForPoint = applyCurveShadowForPoint;
	}


	/**
	 * @return the useCurveLinePropertiesForShape
	 */
	public boolean isUseCurveLinePropertiesForShape() {
		return useCurveLineForNormalPoint;
	}


	/**
	 * @param useCurveLinePropertiesForShape the useCurveLinePropertiesForShape to set
	 */
	public void setUseCurveLinePropertiesForShape(
			boolean useCurveLinePropertiesForShape) {
		this.useCurveLineForNormalPoint = useCurveLinePropertiesForShape;
	}

}
