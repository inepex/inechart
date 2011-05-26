package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Rectangle extends GraphicalObject {

	protected double roundedCornerRadius;
	protected double width, height;
	
	public Rectangle(double basePointX, double basePointY, double width, double height, double roundedCornerRadius, int zIndex, Context context,
			boolean stroke, boolean fill) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.roundedCornerRadius = roundedCornerRadius;
		this.width =  width;
		this.height = height;
	}

	/**
	 * @return the roundedCornerRadius
	 */
	public double getRoundedCornerRadius() {
		return roundedCornerRadius;
	}

	/**
	 * @param roundedCornerRadius the roundedCornerRadius to set
	 */
	public void setRoundedCornerRadius(double roundedCornerRadius) {
		this.roundedCornerRadius = roundedCornerRadius;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

}
