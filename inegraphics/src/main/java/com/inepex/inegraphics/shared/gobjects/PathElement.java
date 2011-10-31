package com.inepex.inegraphics.shared.gobjects;

public abstract class PathElement {

	protected double endPointX, endPointY;
	

	protected PathElement( double endPointX,double endPointY) {
		this.endPointX = endPointX;
		this.endPointY = endPointY;
	}
	
	protected PathElement(PathElement copy){
		this(copy.endPointX, copy.endPointY);
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
