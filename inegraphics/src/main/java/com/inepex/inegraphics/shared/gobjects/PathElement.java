package com.inepex.inegraphics.shared.gobjects;

public abstract class PathElement {

	protected int endPointX, endPointY;
	

	protected PathElement( int endPointX,int endPointY) {
		
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
