package com.inepex.inecharting.chartwidget.event;

import com.inepex.inecharting.chartwidget.graphics.CurveVisualizer;
import com.inepex.inecharting.chartwidget.model.Point;

/**
 * Typically {@link CurveVisualizer} instances implement this interface
 *  to be notified when a point's graphical object needs a redraw
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public interface PointStateChangeListener {
	public void pointStateChanged(Point point);
}
