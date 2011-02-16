package com.inepex.inecharting.chartwidget.model;

import java.util.Comparator;

public abstract class GraphicalObject implements HaszIndex{
	
	/**
	 * Comparator based on z indices
	 * @return 
	 */
	public static Comparator<GraphicalObject> getzIndexComparator(){
		return new Comparator<GraphicalObject>() {
			
			@Override
			public int compare(GraphicalObject o1, GraphicalObject o2) {
				return o1.getzIndex() - o2.getzIndex();
			}
		};
	}
	
	/**
	 * go's z position/index
	 * highest value means top layer
	 */
	private int zIndex;
	
	public int getzIndex() {
		return zIndex;
	}

	public void setzIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	
	

}
