package com.inepex.inecharting.chartwidget.graphics;

import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.model.Axis;

/**
 * A base class for axis visualizations. 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public abstract class AxisVisualizer extends Visualizer {
	/**
	 * The axis to be shown
	 */
	protected Axis axis;
	/**
	 * false if this axis has not yet been shown
	 */
	protected boolean hasShown;
	/**
	 * Constructs an axis visualizer.
	 * @param canvas to draw on
	 * @param axis to be visualized
	 */
	public AxisVisualizer(Widget canvas, Axis axis) {
		super(canvas);
		this.axis = axis;
		hasShown = false;
	}
	
	/**
	 * Displays the axis. Implementations must set hasShown to true in this method
	 */
	public abstract void display();

	public boolean hasShown() {
		return hasShown;
	}
}
