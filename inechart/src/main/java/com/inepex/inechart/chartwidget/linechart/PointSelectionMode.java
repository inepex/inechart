package com.inepex.inechart.chartwidget.linechart;

/**
 * 
 * An enum which defines the select/deselect mode of Points in {@link LineChart}
 * 
 */
public enum PointSelectionMode {
	/**
	 * The closest point to the cursor
	 *  (if mouse is over modul a point will be selected always)
	 */
	Closest_To_Cursor,
	/**
	 * The clicked point
	 */
	On_Click,
	/**
	 * The mouse overed point
	 */
	On_Over,
	/**
	 * The right clicked point
	 */
	On_Right_Click
}
