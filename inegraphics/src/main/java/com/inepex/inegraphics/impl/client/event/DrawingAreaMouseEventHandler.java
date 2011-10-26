package com.inepex.inegraphics.impl.client.event;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventHandler;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public interface DrawingAreaMouseEventHandler extends EventHandler {

	void onMouseMove(ArrayList<GraphicalObject> overedGOs, ArrayList<GraphicalObject> outGOs);

	void onClick(ArrayList<GraphicalObject> clickedGOs);

}
