package com.inepex.inecharting.chartwidget.newimpl.linechart;

public class LineChartProperties {
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
	
	public static LineChartProperties getDefaultLineChartProperties(){
		return new LineChartProperties(false, false, 0, true, 15, PointSelectionMode.Closest_To_Cursor);
	}
	
	boolean autoCreateAxes;
	boolean bringSelectedCurveToFront;
	int overlapFilterDistance;
	boolean precalculatePoints;
	int topPadding;
	PointSelectionMode pointSelectionMode;
	
	
	public LineChartProperties(boolean autoCreateAxes,
			boolean bringSelectedCurveToFront, int overlapFilterDistance,
			boolean precalculatePoints, int topPadding,
			PointSelectionMode pointSelectionMode) {
		this.autoCreateAxes = autoCreateAxes;
		this.bringSelectedCurveToFront = bringSelectedCurveToFront;
		this.overlapFilterDistance = overlapFilterDistance;
		this.precalculatePoints = precalculatePoints;
		this.topPadding = topPadding;
		this.pointSelectionMode = pointSelectionMode;
	}
	
}
