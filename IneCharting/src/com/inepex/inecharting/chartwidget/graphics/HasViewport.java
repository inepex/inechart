package com.inepex.inecharting.chartwidget.graphics;

/**
 * A Visualizer which support moving, resizing viewport should implement this interface.
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public interface HasViewport {
	/**
	 * Slides the viewport over the curve with the given distance
	 * @param dx distance in dimension of underlying data
	 */
	public void moveViewport(double dx);
	
	/**
	 * Sets the new size of the viewport
	 * @param viewportMin
	 * @param viewportMax
	 */
	public void setViewPort(double viewportMin, double viewportMax);
}