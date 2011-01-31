package com.inepex.inecharting.chartwidget.model;

import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo;

public class TimeAxis extends Axis {
	public static enum Resolution{
		SECOND,
		MINUTE,
		HOUR,
		DAY,
		DATE,
		DATE_W_YEAR
	}

	private Resolution resolution;
	
	public TimeAxis(AxisDrawingInfo drawingInfo) {
		super(drawingInfo);
		
	}

	public Resolution getResolution() {
		return resolution;
	}
	
	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}
}
