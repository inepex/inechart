package com.inepex.inechart.chartwidget.properties;

import com.inepex.inechart.chartwidget.Defaults;

public class Color {

	private String color;
	private double alpha;

	public Color(){
		this(Defaults.colorString);
	}
	
	public Color(String color) {
		this.color = color;
		this.alpha = Defaults.alpha;
	}

	public Color(String color, double alpha) {
		this.color = color;
		this.alpha = alpha;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha
	 *            the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

}
