package com.inepex.inegraphics.impl.client.event;

import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class GOMouseOverEvent extends GraphicalObjectEvent {

	public GOMouseOverEvent(GraphicalObject go) {
		super(go);
	}

	@Override
	protected void dispatch(GraphicalObjectEventHandler handler) {
		handler.onMouseOver(this);
	}

}
