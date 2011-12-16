package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface PointSelectionHandler extends EventHandler {
	
	void onSelect(PointInteractionEvent event);
	
	void onDeselect(PointInteractionEvent event);

	void onTouch(PointInteractionEvent event);
}
