package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;

public interface DataSetChangeHandler extends EventHandler {
	
	void onDataSetChange(DataSetChangeEvent event);

}
