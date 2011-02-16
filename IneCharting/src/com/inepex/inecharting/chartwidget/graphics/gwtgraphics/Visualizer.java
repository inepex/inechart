package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for visualizations.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class Visualizer {
	protected Widget canvas;

	public Visualizer(Widget canvas) {
		this.canvas = canvas;
	}
	
	public Widget getCanvas() {
		return canvas;
	}
	
	
}
