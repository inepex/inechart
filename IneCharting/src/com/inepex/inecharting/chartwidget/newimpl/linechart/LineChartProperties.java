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


	public boolean isAutoCreateAxes() {
		return autoCreateAxes;
	}


	public void setAutoCreateAxes(boolean autoCreateAxes) {
		this.autoCreateAxes = autoCreateAxes;
	}


	public boolean isBringSelectedCurveToFront() {
		return bringSelectedCurveToFront;
	}


	public void setBringSelectedCurveToFront(boolean bringSelectedCurveToFront) {
		this.bringSelectedCurveToFront = bringSelectedCurveToFront;
	}


	public int getOverlapFilterDistance() {
		return overlapFilterDistance;
	}


	public void setOverlapFilterDistance(int overlapFilterDistance) {
		this.overlapFilterDistance = overlapFilterDistance;
	}


	public boolean isPrecalculatePoints() {
		return precalculatePoints;
	}


	public void setPrecalculatePoints(boolean precalculatePoints) {
		this.precalculatePoints = precalculatePoints;
	}


	public int getTopPadding() {
		return topPadding;
	}


	public void setTopPadding(int topPadding) {
		this.topPadding = topPadding;
	}


	public PointSelectionMode getPointSelectionMode() {
		return pointSelectionMode;
	}


	public void setPointSelectionMode(PointSelectionMode pointSelectionMode) {
		this.pointSelectionMode = pointSelectionMode;
	}
	
	
	
}
