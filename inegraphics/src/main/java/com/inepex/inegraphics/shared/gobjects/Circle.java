package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Circle extends GraphicalObject {
	
	protected double radius;

	public Circle(double basePointX, double basePointY, int zIndex, Context context,
			boolean stroke, boolean fill, double radius) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.radius = radius;
	}

	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

}
