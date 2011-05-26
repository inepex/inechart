package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Line extends GraphicalObject {

	protected double endPointX;
	protected double endPointY;
	public Line(double basePointX, double basePointY, double endPointX, double endPointY, int zIndex, Context context) {
		super(basePointX, basePointY, zIndex, context, true, true);
		this.endPointX = endPointX;
		this.endPointY = endPointY;
	}
	/**
	 * @return the endPointX
	 */
	public double getEndPointX() {
		return endPointX;
	}
	/**
	 * @param endPointX the endPointX to set
	 */
	public void setEndPointX(double endPointX) {
		this.endPointX = endPointX;
	}
	/**
	 * @return the endPointY
	 */
	public double getEndPointY() {
		return endPointY;
	}
	/**
	 * @param endPointY the endPointY to set
	 */
	public void setEndPointY(double endPointY) {
		this.endPointY = endPointY;
	}
	
}
