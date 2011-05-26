package com.inepex.inegraphics.shared.gobjects;

import java.util.Comparator;

import com.inepex.inegraphics.shared.Context;


public abstract class GraphicalObject implements Comparable<GraphicalObject>{
	//comparing helper fields
	private static int highestComparatorID = Integer.MIN_VALUE;
	private int comparatorID;
	/**
	 * Comparator based on z indices
	 * @return 
	 */
	public static Comparator<GraphicalObject> getzIndexComparator(){
		return new Comparator<GraphicalObject>() {
			
			@Override
			public int compare(GraphicalObject o1, GraphicalObject o2) {
				if(o1.zIndex > o2.zIndex)
					return 1;
				else if(o1.zIndex == o2.zIndex)
					return 0;
				else 
					return -1;
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
				double diff = o1.basePointX - o2.basePointX;
				if(diff > 0)
					return 1;
				else if(diff < 0)
					return -1;
				else 
					return 0;
			}
		};
	}
	
	public static Comparator<GraphicalObject> getZComparator(){
		return new Comparator<GraphicalObject>() {
			
			@Override
			public int compare(GraphicalObject o1, GraphicalObject o2) {
				double diff = o1.zIndex - o2.zIndex;
				if(o1.zIndex > o2.zIndex)
					return 1;
				else if(o1.zIndex < o2.zIndex)
					return -1;
				else 
					return 0;
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
				int zDiff = getZComparator().compare(o1, o2);
				if(zDiff == 0)
					zDiff = getBasePointXComparator().compare(o1, o2);
				return zDiff;
			}
		};
	}

	protected double basePointX;
	protected double basePointY;
	protected int zIndex;
	protected Context context;
	protected boolean stroke;
	protected boolean fill;
	
	protected GraphicalObject(double basePointX, double basePointY, int zIndex, Context context, boolean stroke, boolean fill) {
		this.basePointX = basePointX;
		this.basePointY = basePointY;
		this.zIndex = zIndex;
		this.stroke = stroke;
		this.fill = fill;
		this.context = context;
		this.comparatorID = highestComparatorID++;
	}
	/**
	 * @return the basePointX
	 */
	public double getBasePointX() {
		return basePointX;
	}
	/**
	 * @param basePointX the basePointX to set
	 */
	public void setBasePointX(double basePointX) {
		this.basePointX = basePointX;
	}
	/**
	 * @return the basePointY
	 */
	public double getBasePointY() {
		return basePointY;
	}
	/**
	 * @param basePointY the basePointY to set
	 */
	public void setBasePointY(double basePointY) {
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

	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GraphicalObject other) {
		return comparatorID - other.comparatorID;
	}
	
	
	
}
