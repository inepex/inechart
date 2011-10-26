package com.inepex.inegraphics.impl.client.event;

import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class GOClickEvent extends GraphicalObjectEvent {

	public GOClickEvent(GraphicalObject go) {
		super(go);
	}

	@Override
	protected void dispatch(GraphicalObjectEventHandler handler) {
		handler.onClick(this);
	}

}
