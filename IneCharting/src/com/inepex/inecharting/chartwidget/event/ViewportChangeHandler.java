package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface ViewportChangeHandler extends EventHandler {
	
	public void onViewportScrolled(double dx);
	
	public void onViewportResized(double viewportMin, double viewportMax);
	
}
