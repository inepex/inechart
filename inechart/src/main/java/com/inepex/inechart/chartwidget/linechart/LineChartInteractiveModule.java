package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.inepex.inechart.chartwidget.ModuleAssist;

public abstract class LineChartInteractiveModule implements MouseMoveHandler, MouseOutHandler, MouseDownHandler, 
	MouseOverHandler, MouseUpHandler, ClickHandler{

	protected ModuleAssist moduleAssist;
	protected LineChart2 lineChart;
	protected boolean visible;
	protected boolean canHandleEvents;

	public LineChartInteractiveModule() {
		visible = true;
	}

	public LineChartInteractiveModule(ModuleAssist moduleAssist,
			LineChart2 lineChart) {
		this();
		attach(moduleAssist, lineChart);
	}

	public void attach(ModuleAssist moduleAssist,
			LineChart2 lineChart) {
		this.moduleAssist = moduleAssist;
		this.lineChart = lineChart;
	}
	
	protected abstract void update();

	protected abstract void pointSelection(TreeMap<Curve2, ArrayList<DataPoint2>> selectedPoints, TreeMap<Curve2, ArrayList<DataPoint2>> deselectedPoints);
	

}
