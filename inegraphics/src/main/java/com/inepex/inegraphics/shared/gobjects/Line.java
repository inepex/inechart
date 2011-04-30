package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Line extends GraphicalObject {

	protected int endPointX;
	protected int endPointY;
	public Line(int basePointX, int basePointY, int endPointX, int endPointY, int zIndex, Context context) {
		super(basePointX, basePointY, zIndex, context, true, true);
		this.endPointX = endPointX;
		this.endPointY = endPointY;
	}
	/**
	 * @return the endPointX
	 */
	public int getEndPointX() {
		return endPointX;
	}
	/**
	 * @param endPointX the endPointX to set
	 */
	public void setEndPointX(int endPointX) {
		this.endPointX = endPointX;
	}
	/**
	 * @return the endPointY
	 */
	public int getEndPointY() {
		return endPointY;
	}
	/**
	 * @param endPointY the endPointY to set
	 */
	public void setEndPointY(int endPointY) {
		this.endPointY = endPointY;
	}
	
}
