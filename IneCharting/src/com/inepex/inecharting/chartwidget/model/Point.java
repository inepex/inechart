package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;

import com.inepex.inecharting.chartwidget.model.State;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo;
/**
 * 
 *@author Miklós Süveges / Inepex Ltd
 */
public final class Point extends GraphicalObject implements Comparable<Point>{
	
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
	 * tells to a drawing/calculating method that this point does not have a proper data.
	 * most likely it was created from 2 or more overlapping points, so you will not find it in the curve's calculatedPoints container
	 */
	private boolean imaginaryPoint;
	
	
	public Point(int xPos, int yPos, boolean imaginaryPoint, Curve parent) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.imaginaryPoint = imaginaryPoint;
		this.state = State.NORMAL;
		this.parent = parent;
		this.zIndex = parent.getzIndex();
	}
	
	
	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}
	
	void setxPos(int xPos) {
		this.xPos = xPos;
	}
	void setyPos(int yPos) {
		this.yPos = yPos;
	}
	
	public boolean isImaginaryPoint() {
		return imaginaryPoint;
	}

	public Curve getParent() {
		return parent;
	}
	@Override
	public int compareTo(Point o) {
		return xPos-o.xPos;
	}
	
	public ArrayList<Double> getUnderlyingData(){
		return ModelManager.get().getDataForPoint(this);
	}
	
	public PointDrawingInfo getActualPointDrawingInfo(){
		return getPointDrawingInfo(state);
	}
	
	public PointDrawingInfo getPointDrawingInfo(State state){
		PointDrawingInfo info = null;
		ArrayList<Double> vls = getUnderlyingData();
		//look for custom info
		for(int i = 0; i < vls.size(); i++){
			info = getParent().getCurveDrawingInfo().getPointDrawingInfo(getUnderlyingData().get(i),state);
			if(info != null)
				return info;
		}
		//look for default;
		if(info == null)
			info = getParent().getCurveDrawingInfo().getDefaultPointDrawingInfo(state);
		return info;
	}
	
	public int getShapeMaxWidth(){
		int maxWidth = 0;
		for(State state : State.values()){
			int actWidth = getPointDrawingInfo(state).getWidth();
			if(actWidth > maxWidth)
				maxWidth = actWidth;
		}
		return maxWidth;
	}
}

