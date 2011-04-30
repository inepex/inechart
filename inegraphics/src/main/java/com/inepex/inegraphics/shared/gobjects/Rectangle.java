package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Rectangle extends GraphicalObject {

	protected int roundedCornerRadius;
	protected int width, height;
	
	public Rectangle(int basePointX, int basePointY, int width, int height, int roundedCornerRadius, int zIndex, Context context,
			boolean stroke, boolean fill) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.roundedCornerRadius = roundedCornerRadius;
		this.width =  width;
		this.height = height;
	}

	/**
	 * @return the roundedCornerRadius
	 */
	public int getRoundedCornerRadius() {
		return roundedCornerRadius;
	}

	/**
	 * @param roundedCornerRadius the roundedCornerRadius to set
	 */
	public void setRoundedCornerRadius(int roundedCornerRadius) {
		this.roundedCornerRadius = roundedCornerRadius;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
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
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

}
