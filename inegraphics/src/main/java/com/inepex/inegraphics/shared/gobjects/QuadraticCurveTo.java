package com.inepex.inegraphics.shared.gobjects;

public class QuadraticCurveTo extends MoveTo {
	
	private int controlPointX, controlPointY;

	public QuadraticCurveTo(int endPointX, int endPointY, int controlPointX, int controlPointY) {
		super(endPointX, endPointY);
		this.controlPointX = controlPointX;
		this.controlPointY = controlPointY;
	}

	/**
	 * @return the controlPointX
	 */
	public int getControlPointX() {
		return controlPointX;
	}

	/**
	 * @param controlPointX the controlPointX to set
	 */
	public void setControlPointX(int controlPointX) {
		this.controlPointX = controlPointX;
	}

	/**
	 * @return the controlPointY
	 */
	public int getControlPointY() {
		return controlPointY;
	}

	/**
	 * @param controlPointY the controlPointY to set
	 */
	public void setControlPointY(int controlPointY) {
		this.controlPointY = controlPointY;
	}

}
