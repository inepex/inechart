package com.inepex.inegraphics.impl.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface GraphicalObjectEventHandler extends EventHandler {
	
	void onMouseOver(GOMouseOverEvent event);
	
	void onClick(GOClickEvent event);
	
	void onMouseOut(GOMouseOutEvent event);
	
}
