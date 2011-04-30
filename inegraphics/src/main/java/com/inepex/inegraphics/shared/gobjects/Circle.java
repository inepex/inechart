package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Circle extends GraphicalObject {
	
	protected int radius;

	public Circle(int basePointX, int basePointY, int zIndex, Context context,
			boolean stroke, boolean fill, int radius) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.radius = radius;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

}
