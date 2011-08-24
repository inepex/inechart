package com.inepex.inechart.chartwidget.intervalchart;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;

public class IntervalDataSet {
	private static int autoNameMaxIndex = 0;
	private static ColorSet colorSet = new ColorSet();
	/**
	 * @return the colorSet
	 */
	public static ColorSet getColorSet() {
		return colorSet;
	}

	/**
	 * @param colorSet the colorSet to set
	 */
	public static void setColorSet(ColorSet colorSet) {
		IntervalDataSet.colorSet = colorSet;
	}

	private ArrayList<double[]> intervals;
	private double yValue;

	private String name;
	private Color color;
	
	private ShapeProperties shapeProperties;
	private double roundedCornerRadius;
	
	public IntervalDataSet() {
		this(new ArrayList<double[]>());
	}
	
	public IntervalDataSet(ArrayList<double[]> intervals) {
		this(intervals, Defaults.yValue, Defaults.name + ++autoNameMaxIndex, colorSet.getNextColor());
	}

	public IntervalDataSet(ArrayList<double[]> intervals, double yValue,
			String name, Color color) {
		this.intervals = intervals;
		this.yValue = yValue;
		this.name = name;
		this.color = color;
		this.shapeProperties = Defaults.intervalShapeProperties();
		shapeProperties.setFillColor(color);
		shapeProperties.getLineProperties().setLineColor(color);
		this.roundedCornerRadius = Defaults.intervalRoundedCornerRadius;
	}
	
	public IntervalDataSet(ArrayList<double[]> intervals, double yValue,
			String name, ShapeProperties shapeProperties,
			int roundedCornerRadius) {
		this.intervals = intervals;
		this.yValue = yValue;
		this.name = name;
		this.shapeProperties = shapeProperties;
		this.roundedCornerRadius = roundedCornerRadius;
	}
	
	public void addInterval(double[] interval){
		if(intervals == null){
			intervals = new ArrayList<double[]>();
		}
		intervals.add(interval);	
	}
	
	public void addInterval(double start, double end){
		addInterval(new double[]{start, end});
	}

	public ArrayList<double[]> getIntervals() {
		return intervals;
	}

	public void setIntervals(ArrayList<double[]> intervals) {
		this.intervals = intervals;
	}

	public double getyValue() {
		return yValue;
	}

	public void setyValue(double yValue) {
		this.yValue = yValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		if(shapeProperties == null){
			shapeProperties = Defaults.intervalShapeProperties();
		}
		shapeProperties.setFillColor(color);
		shapeProperties.getLineProperties().setLineColor(color);
	}

	public ShapeProperties getShapeProperties() {
		return shapeProperties;
	}

	public void setShapeProperties(ShapeProperties shapeProperties) {
		this.shapeProperties = shapeProperties;
		this.color = shapeProperties.getFillColor() == null ? 
				(shapeProperties.getLineProperties().getLineColor() == null ? colorSet.getNextColor() : shapeProperties.getLineProperties().getLineColor())
				: shapeProperties.getFillColor();
	}

	public double getRoundedCornerRadius() {
		return roundedCornerRadius;
	}

	public void setRoundedCornerRadius(double roundedCornerRadius) {
		this.roundedCornerRadius = roundedCornerRadius;
	}
	
}
