package com.inepex.inecharting.chartwidget.graphics;

import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.model.Curve;

/**
 * Base class for drawing curves on canvas.
 * @author Miklós Süveges / Inepex Ltd
 */
public abstract class CurveVisualizer implements HasViewport, HasDrawingJob{
	protected Widget canvas;
	protected Curve curve;
	
	/**
	 * Creates a visualizer
	 * @param canvas to draw on
	 * @param curve to draw
	 */
	public CurveVisualizer(Widget canvas, Curve curve) {
		this.canvas = canvas;
		this.curve = curve;
	}

	/**
	 * Drops any previous graphical objects (if exist), and draws curve
	 * @param viewportMin
	 * @param viewportMax
	 */
	public abstract void drawCurve(double viewportMin, double viewportMax);
	
	/**
	 * Removes all graphical objects from canvas
	 */
	public abstract void removeFromCanvas();
	

}
