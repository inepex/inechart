package com.inepex.inechart.chartwidget.piechart;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;

public class Slice {
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
		Slice.colorSet = colorSet;
	}

	private static int autoNameNo = 0;
	String name;
	ShapeProperties lookOut;
	double data;
	double percentage;
	
//	public Slice(DataSet dataSet){
//		this(dataSet.getName(), dataSet.getColor(), dataSet.getValues().get(0));
//	}
		
	public Slice(double data) {
		this("Slice No.: "+ ++autoNameNo,data);
	}

	public Slice(String name, double data) {
		this(name,  colorSet.getNextColor(), data);
	}
	
	public Slice(String name, Color color, double data) {
		lookOut = Defaults.pie();
		lookOut.getFillColor().setColor(color.getColor());
		lookOut.getLineProperties().getLineColor().setColor(color.getColor());
	}

	public Slice(String name, ShapeProperties lookOut, double data) {
		this.name = name;
		this.lookOut = lookOut;
		this.data = data;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the lookOut
	 */
	public ShapeProperties getLookOut() {
		return lookOut;
	}

	/**
	 * @param lookOut the lookOut to set
	 */
	public void setLookOut(ShapeProperties lookOut) {
		this.lookOut = lookOut;
	}

	/**
	 * @return the data
	 */
	public double getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(double data) {
		this.data = data;
	}

	/**
	 * @return the percentage
	 */
	public double getPercentage() {
		return percentage;
	}
}
