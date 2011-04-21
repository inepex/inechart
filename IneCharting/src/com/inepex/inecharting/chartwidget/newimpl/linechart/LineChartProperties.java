package com.inepex.inecharting.chartwidget.newimpl.linechart;

public class LineChartProperties {
	public static LineChartProperties getDefaultLineChartProperties(){
		return new LineChartProperties();
	}
	boolean autoCreateAxes;
	boolean bringSelectedCurveToFront;
	boolean drawSelectedPointOverNormal;
	int overlapFilterDistance = 0;
	boolean precalculatePoints;
	int topPadding = 20;
	
	public enum PointSelectionMode{
		/**
		 * The closest point to the cursor will be selected
		 */
		Closest_To_Cursor,
		/**
		 * The clicked point will be selected
		 */
		On_Point_Click,
		/**
		 * The mouse overed point will be selected
		 */
		On_Point_Over
	}
	
	PointSelectionMode pointSelectionMode;
	
	
}
