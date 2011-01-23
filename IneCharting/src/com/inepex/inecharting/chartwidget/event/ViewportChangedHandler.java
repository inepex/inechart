package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface ViewportChangedHandler extends EventHandler {
	public void onViewportScrolled();
	
	public void onViewportResized();
	
}
