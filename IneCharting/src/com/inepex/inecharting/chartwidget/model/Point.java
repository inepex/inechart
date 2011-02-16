package com.inepex.inecharting.chartwidget.model;

import com.inepex.inecharting.chartwidget.model.State;
/**
 * 
 *@author Miklós Süveges / Inepex Ltd
 */
public final class Point extends GraphicalObject
	implements Comparable<Point>, HasState{
	
	/**
	 * 
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
	/**
	 * the points z position/index
	 * highest value means top layer
	 */
	private int zIndex;
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
		this.zIndex = parent.getzIndex();
	}
	
	
	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}
	@Override
	public State getState() {
		return state;
	}
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	@Override
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
	@Override
	public int getzIndex() {
		return zIndex;
	}
	@Override
	public void setzIndex(int zIndex) {
		this.zIndex = zIndex;
	}
}

