package com.inepex.inegraphics.impl.client;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class GraphicalObjectEvent extends GwtEvent<GraphicalObjectEventHandler> {

	public static final  Type<GraphicalObjectEventHandler> TYPE = new Type<GraphicalObjectEventHandler>();
	
	public static enum GraphicalObjectEventType{
		MOUSE_MOVE,
		MOUSE_CLICK,
	}
	
	protected ArrayList<GraphicalObject> c1;
	protected ArrayList<GraphicalObject> c2;
	protected GraphicalObjectEventType goEventType;
	
	
	public GraphicalObjectEvent(ArrayList<GraphicalObject> sourceGOs) {
		this.c1 = sourceGOs;
		this.goEventType = GraphicalObjectEventType.MOUSE_CLICK;
	}

	public GraphicalObjectEvent(ArrayList<GraphicalObject> over, ArrayList<GraphicalObject> out) {
		this.c1 = over;
		this.c2 = out;
		this.goEventType = GraphicalObjectEventType.MOUSE_MOVE;
	}
	@Override
	public Type<GraphicalObjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GraphicalObjectEventHandler handler) {
		switch (goEventType) {
		case MOUSE_CLICK:
			handler.onMouseClick(c1);
			break;
		case MOUSE_MOVE:
			handler.onMouseMove(c1, c2);
			break;
		}
	}


	/**
	 * @return the goEventType
	 */
	public GraphicalObjectEventType getGraphicalObjectEventType() {
		return goEventType;
	}
	
}
