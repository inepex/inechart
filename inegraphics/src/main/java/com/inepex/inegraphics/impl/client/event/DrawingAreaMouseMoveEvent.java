package com.inepex.inegraphics.impl.client.event;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class DrawingAreaMouseMoveEvent extends GwtEvent<DrawingAreaMouseEventHandler> {
	public static final Type<DrawingAreaMouseEventHandler> TYPE = new Type<DrawingAreaMouseEventHandler>();

	ArrayList<GraphicalObject> over;
	ArrayList<GraphicalObject> out;

	public DrawingAreaMouseMoveEvent(ArrayList<GraphicalObject> over,
			ArrayList<GraphicalObject> out) {
		this.over = over;
		this.out = out;
	}

	@Override
	protected void dispatch(DrawingAreaMouseEventHandler handler) {
		handler.onMouseMove(over, out);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DrawingAreaMouseEventHandler> getAssociatedType() {
		return TYPE;
	}

}
