package com.inepex.inechart.chartwidget;

import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;

/**
 * 
 * An interface for the {@link IneChartModul}. The axes should be created
 * automatically, even if they not present, or they are invisible by default.
 * The directions of the coordinate system are set by the {@link AxisDirection}
 * of the two axes.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public interface HasCoordinateSystem {

	/**
	 * Sets the X axis
	 * 
	 * @param xAxis
	 *            an {@link Axis} perpendicular to Y axis
	 */
	void setXAxis(Axis xAxis);

	/**
	 * 
	 * @return the X axis
	 */
	Axis getXAxis();

	/**
	 * Sets the Y axis
	 * 
	 * @param yAxis
	 *            an {@link Axis} perpendicular to X axis
	 */
	void setYAxis(Axis yAxis);

	/**
	 * 
	 * @return the Y axis
	 */
	Axis getYAxis();

	/**
	 * Defines a new {@link Viewport} on this coordinate system
	 * 
	 * @param viewport
	 */
	void setViewport(Viewport viewport);

	/**
	 * 
	 * @return the viewport
	 */
	Viewport getViewport();

	/**
	 * Defines the priority of the viewport to the implementing class. If true
	 * the viewport sets the axes If false the axes set the viewport
	 * 
	 * @param useViewport
	 */
	void setUseViewport(boolean useViewport);

	/**
	 * 
	 * @return true if the {@link Viewport} controls the view
	 */
	boolean useViewport();
}
