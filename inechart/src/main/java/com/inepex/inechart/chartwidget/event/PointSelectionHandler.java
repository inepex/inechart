package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface PointSelectionHandler extends EventHandler {
	
	void onSelect(PointSelectionEvent event);
	
	void onDeselect(PointSelectionEvent event);

}
