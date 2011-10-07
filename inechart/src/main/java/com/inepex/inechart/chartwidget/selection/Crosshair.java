package com.inepex.inechart.chartwidget.selection;

import com.inepex.inechart.chartwidget.IneChartEventManager;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public class Crosshair extends SelectionBase{

	protected Crosshair(IneChartEventManager eventManager, DrawingAreaGWT canvas) {
		super(eventManager, canvas);
		
	}

	@Override
	public void setModulToSelectFrom(IneChartModule2D modulToSelectFrom) {
		if(modulToSelectFrom instanceof LineChart)
			super.setModulToSelectFrom(modulToSelectFrom);
	}
	
	
}
