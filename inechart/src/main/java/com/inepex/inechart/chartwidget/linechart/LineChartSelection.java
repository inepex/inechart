package com.inepex.inechart.chartwidget.linechart;

import com.inepex.inegraphics.shared.DrawingArea;

public interface LineChartSelection {
	
	void setLineChart(LineChart lineChart);
	
	void setDrawingArea(DrawingArea drawingArea);
	
//	boolean isDrawingAreaSet();
	
	void selectPoint(Curve c, DataPoint dp);
	
	void deselectPoint(Curve c, DataPoint dp);
	
	void update();
}
