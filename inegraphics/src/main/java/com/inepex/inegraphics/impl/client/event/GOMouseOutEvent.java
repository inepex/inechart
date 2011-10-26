package com.inepex.inegraphics.impl.client.event;

import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class GOMouseOutEvent extends GraphicalObjectEvent {

	public GOMouseOutEvent(GraphicalObject go) {
		super(go);
	}

	@Override
	protected void dispatch(GraphicalObjectEventHandler handler) {
		handler.onMouseOut(this);
	}

}
