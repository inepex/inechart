package com.inepex.inegraphics.impl.client;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventHandler;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public interface GraphicalObjectEventHandler extends EventHandler {

	/**
	 * Mouse clicked over the passed graphical objects
	 * @param sourceGOs
	 */
	void onMouseClick(ArrayList<GraphicalObject> sourceGOs);
	
	/**
	 * Mouse over or out
	 * @param mouseOver
	 * @param mouseOut
	 */
	void onMouseMove(ArrayList<GraphicalObject> mouseOver, ArrayList<GraphicalObject> mouseOut);
}
