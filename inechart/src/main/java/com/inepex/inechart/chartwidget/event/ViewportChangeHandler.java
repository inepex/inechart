package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.EventHandler;
import com.inepex.inechart.chartwidget.Viewport;

public interface ViewportChangeHandler extends EventHandler {

	void onViewportChange(Viewport viewport);
}
