package com.inepex.inegraphics.shared;

import java.util.ArrayList;

import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class GraphicalObjectContainer {
	
	protected ArrayList<GraphicalObject> graphicalObjects;
	
	/**
	 * @return the graphicalObjects
	 */
	public ArrayList<GraphicalObject> getGraphicalObjects() {
		return graphicalObjects;
	}

	public void addAllGraphicalObject(GraphicalObjectContainer goc){
		for(GraphicalObject go : goc.graphicalObjects)
			graphicalObjects.add(go);
	}

	public GraphicalObjectContainer() {
		graphicalObjects = new ArrayList<GraphicalObject>();
	}
	
	public boolean addGraphicalObject(GraphicalObject graphicalObject){
		if(graphicalObject == null)
			return false;
		this.graphicalObjects.add(graphicalObject);
		return true;
	}
	
	public void removeGraphicalObject(GraphicalObject graphicalObject){
		this.graphicalObjects.remove(graphicalObject);
	}
	
	public void removeAllGraphicalObject(){
		this.graphicalObjects.clear();
	}
	
	/**
	 * Sets all the contained {@link GraphicalObject}'s zIndex to the given value
	 * @param zIndex
	 */
	public void setzIndices(int zIndex){
		for(GraphicalObject go:graphicalObjects){
			go.setzIndex(zIndex);
		}
	}
	
	/**
	 * Translates all the contained {@link GraphicalObject}'s basePoint with the given value
	 * @param dx
	 * @param dy
	 */
	public void moveBasePoints(int dx, int dy){
		for(GraphicalObject go:graphicalObjects){
			go.setBasePointX(go.getBasePointX()+dx);
			go.setBasePointY(go.getBasePointY()+dy);
		}
	}
	
	
	
}
