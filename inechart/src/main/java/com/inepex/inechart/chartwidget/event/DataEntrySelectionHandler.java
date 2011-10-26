package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface DataEntrySelectionHandler extends EventHandler{
	
	void onSelect(DataEntrySelectionEvent event);
	
	void onDeselect(DataEntrySelectionEvent event);

}
