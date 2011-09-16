package com.inepex.inechart.chartwidget.event;

import java.util.TreeMap;

import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.DataPoint;

public interface PointHoverListener {

	void onPointHover(TreeMap<Curve, DataPoint> hoveredPoints);
	
}
