package com.inepex.inechart.chartwidget.properties;

public class Color {

	public static final double DEFAULT_ALPHA = 1.0;
	public static final String DEFAULT_COLOR = "#000000";
	private String color;
	private double alpha;

	public Color(String color) {
		this.color = color;
		this.alpha = DEFAULT_ALPHA;
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
