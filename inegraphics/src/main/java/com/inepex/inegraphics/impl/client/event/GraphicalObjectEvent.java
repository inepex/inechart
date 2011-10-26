package com.inepex.inegraphics.impl.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public abstract class GraphicalObjectEvent extends GwtEvent<GraphicalObjectEventHandler> {
	public static final Type<GraphicalObjectEventHandler> TYPE = new Type<GraphicalObjectEventHandler>();
	
	protected GraphicalObject go;
	
	protected GraphicalObjectEvent(GraphicalObject go) {
		super();
		this.go = go;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GraphicalObjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	public GraphicalObject getGraphicalObject() {
		return go;
	}
}