package com.inepex.inecharting.chartwidget.newimpl.linechart;

public class LineChartProperties {
	public static LineChartProperties getDefaultLineChartProperties(){
		return new LineChartProperties();
	}
	boolean autoCreateAxes;
	boolean bringSelectedCurveToFront;
	int overlapFilterDistance = 0;
	boolean precalculatePoints;
	
	int topPadding = 20;
	
}
