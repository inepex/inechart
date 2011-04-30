package com.inepex.inegraphics.shared.gobjects;

import java.util.Comparator;

import com.inepex.inegraphics.shared.Context;


public abstract class GraphicalObject {
	
	/**
	 * Comparator based on z indices
	 * @return 
	 */
	public static Comparator<GraphicalObject> getzIndexComparator(){
		return new Comparator<GraphicalObject>() {
			
			@Override
			public int compare(GraphicalObject o1, GraphicalObject o2) {
				return o1.zIndex - o2.zIndex;
			}
		};
	}
	
	/**
	 * Comparator based on x position
	 * @return 
	 */
	public static Comparator<GraphicalObject> getBasePointXComparator(){
		return new Comparator<GraphicalObject>() {
			
			@Override
			public int compare(GraphicalObject o1, GraphicalObject o2) {
				return o1.basePointX - o2.basePointX;
			}
		};
	}
	
	/**
	 * A zIndex, and basePointX comparator: if zIndex equals then compare the objects based on their x position
	 * @return
	 */
	public static Comparator<GraphicalObject> getZXComparator(){
		return new Comparator<GraphicalObject>() {
			
			@Override
			public int compare(GraphicalObject o1, GraphicalObject o2) {
				int zDiff = o1.zIndex - o2.zIndex;
				if(zDiff == 0)
					return o1.basePointX - o2.basePointX;
				return zDiff;
			}
		};
	}

	protected int basePointX;
	protected int basePointY;
	protected int zIndex;
	protected Context context;
	protected boolean stroke;
	protected boolean fill;
	
	protected GraphicalObject(int basePointX, int basePointY, int zIndex, Context context, boolean stroke, boolean fill) {
		this.basePointX = basePointX;
		this.basePointY = basePointY;
		this.zIndex = zIndex;
		this.stroke = stroke;
		this.fill = fill;
		this.context = context;
	}
	/**
	 * @return the basePointX
	 */
	public int getBasePointX() {
		return basePointX;
	}
	/**
	 * @param basePointX the basePointX to set
	 */
	public void setBasePointX(int basePointX) {
		this.basePointX = basePointX;
	}
	/**
	 * @return the basePointY
	 */
	public int getBasePointY() {
		return basePointY;
	}
	/**
	 * @param basePointY the basePointY to set
	 */
	public void setBasePointY(int basePointY) {
		this.basePointY = basePointY;
	}
	/**
	 * @return the zIndex
	 */
	public int getzIndex() {
		return zIndex;
	}
	/**
	 * @param zIndex the zIndex to set
	 */
	public void setzIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	/**
	 * @return the contextNo
	 */
	public Context getContext() {
		return context;
	}
	/**
	 * @param contextNo the contextNo to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	/**
	 * @return the stroke
	 */
	public boolean hasStroke() {
		return stroke;
	}
	/**
	 * @param stroke the stroke to set
	 */
	public void setStroke(boolean stroke) {
		this.stroke = stroke;
	}
	/**
	 * @return the fill
	 */
	public boolean hasFill() {
		return fill;
	}
	/**
	 * @param fill the fill to set
	 */
	public void setFill(boolean fill) {
		this.fill = fill;
	}

	@Override
	public String toString() {
		return "GraphicalObject [basePointX=" + basePointX + ", basePointY=" + basePointY + ", zIndex=" + zIndex + ", context="
				+ context + ", stroke=" + stroke + ", fill=" + fill + "]";
	}
	
	
	
}
