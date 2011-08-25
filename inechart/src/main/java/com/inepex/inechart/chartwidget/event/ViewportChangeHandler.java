package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface ViewportChangeHandler extends EventHandler {
	
	void onMove(ViewportChangeEvent event, double dx, double dy);
	
	void onMoveAlongX(ViewportChangeEvent event, double dx);
	
	void onMoveAlongY(ViewportChangeEvent event, double dy);
	
	void onSet(ViewportChangeEvent event, double xMin, double yMin, double xMax, double yMax);
	
	void onSetX(ViewportChangeEvent event, double xMin, double xMax);
	
	void onSetY(ViewportChangeEvent event, double yMin, double yMax);
	
}
