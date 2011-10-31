package com.inepex.inegraphics.shared.gobjects;

public class QuadraticCurveTo extends MoveTo {
	
	private double controlPointX, controlPointY;

	public QuadraticCurveTo(double endPointX, double endPointY, double controlPointX, double controlPointY) {
		super(endPointX, endPointY);
		this.controlPointX = controlPointX;
		this.controlPointY = controlPointY;
	}
	
	public QuadraticCurveTo(QuadraticCurveTo copy){
		super(copy);
		this.controlPointX = copy.controlPointX;
		this.controlPointY = copy.controlPointY;
	}

	/**
	 * @return the controlPointX
	 */
	public double getControlPointX() {
		return controlPointX;
	}

	/**
	 * @param controlPointX the controlPointX to set
	 */
	public void setControlPointX(double controlPointX) {
		this.controlPointX = controlPointX;
	}

	/**
	 * @return the controlPointY
	 */
	public double getControlPointY() {
		return controlPointY;
	}

	/**
	 * @param controlPointY the controlPointY to set
	 */
	public void setControlPointY(double controlPointY) {
		this.controlPointY = controlPointY;
	}

}
