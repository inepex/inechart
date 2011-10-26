package com.inepex.inegraphics.impl.client.event;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class DrawingAreaClickEvent extends
		GwtEvent<DrawingAreaMouseEventHandler> {
	public static final Type<DrawingAreaMouseEventHandler> TYPE = new Type<DrawingAreaMouseEventHandler>();
	
	ArrayList<GraphicalObject> clicked;
	
	public DrawingAreaClickEvent(ArrayList<GraphicalObject> clicked) {
		super();
		this.clicked = clicked;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DrawingAreaMouseEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DrawingAreaMouseEventHandler handler) {
		handler.onClick(clicked);
	}

}
