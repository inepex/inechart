package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface ViewportChangeHandler extends EventHandler {
	void onMove(ViewportChangeEvent event, double dx, double dy);
	void onSet(ViewportChangeEvent event, double xMin, double yMin, double xMax, double yMax);
}
