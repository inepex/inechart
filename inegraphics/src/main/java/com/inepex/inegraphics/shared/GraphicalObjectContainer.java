package com.inepex.inegraphics.shared;

import java.util.ArrayList;
import java.util.List;

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
	
	public void addAllGraphicalObject(List<GraphicalObject> gos){
		for(GraphicalObject go : gos)
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
	public void moveBasePoints(double dx, double dy){
		for(GraphicalObject go:graphicalObjects){
			go.setBasePointX(go.getBasePointX()+dx);
			go.setBasePointY(go.getBasePointY()+dy);
		}
	}
	
	public static void dropGraphicalObjectsOutsideRectangle(GraphicalObjectContainer goc, double x, double y, double width, double height){
		ArrayList<GraphicalObject> toDrop = new ArrayList<GraphicalObject>();
		for(GraphicalObject go : goc.graphicalObjects){
			if(go.getBasePointX() < x ||
				go.getBasePointY() < y ||
				go.getBasePointX() > x + width ||
				go.getBasePointY() > y + height)
				toDrop.add(go);
		}
		for(GraphicalObject go : toDrop){
			goc.removeGraphicalObject(go);
		}
	}
}
