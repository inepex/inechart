package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface StateChangeHandler extends EventHandler {
	
	public void onStateChange(StateChangeEvent event);
	
}
