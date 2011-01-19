package com.inepex.inecharting.chartwidget.model;

/**
 * 
 *@author Miklós Süveges / Inepex Ltd
 */
public final class Point implements Comparable<Point>{
	
	/**
	 * View state of a point
	 *
	 */
	public static enum State {
		/**
		 * is not on the viewport
		 */
		INVISIBLE,
		/**
		 * is on the viewport
		 */
		VISIBLE,
		/**
		 * selected, mouseover, etc
		 */
		ACTIVE,
		/**
		 * clicked
		 */
		FOCUSED
	}
	
	/**
	 * the curve this point belongs to
	 */
	private Curve parent; 
	/**
	 * the point's x position in the canvas' coordinate system. Should be updated when viewport's length changes
	 */
	private int xPos;
	/**
	 * the point's y position in the canvas' coordinate system
	 */
	private int yPos;
	private State state;

	/**
	 * tells to a drawing/calculating method that this point does not have a proper data.
	 * most likely it was created from 2 or more overlapping points, so you will not find it in the curve's calculatedPoints container
	 */
	private boolean imaginaryPoint;
	
	
	public Point(int xPos, int yPos, boolean imaginaryPoint, Curve parent) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.imaginaryPoint = imaginaryPoint;
		this.state = State.INVISIBLE;
		this.parent = parent;
	}
	
	
	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public State getState() {
		return state;
	}
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	public void setState(State state) {
		this.state = state;
	}
	public boolean isImaginaryPoint() {
		return imaginaryPoint;
	}

	public void setParent(Curve parent) {
		this.parent = parent;
	}

	public Curve getParent() {
		return parent;
	}


	@Override
	public int compareTo(Point o) {
		return xPos-o.xPos;
	}
}

