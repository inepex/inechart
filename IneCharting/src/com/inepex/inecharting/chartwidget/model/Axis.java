package com.inepex.inecharting.chartwidget.model;


import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo;


public class Axis {
		
	protected AxisDrawingInfo drawingInfo;
	/**
	 * a tick's absolute position, the other ticks' positions can be measured from it. 
	 */
	protected double fixTick;
	/**
	 * distance between ticks
	 */
	protected double tickDistance;
	
	public Axis(AxisDrawingInfo drawingInfo) {
	
		this.drawingInfo = drawingInfo;
	}

	public AxisDrawingInfo getDrawingInfo() {
		return drawingInfo;
	}

	public void setDrawingInfo(AxisDrawingInfo drawingInfo) {
		this.drawingInfo = drawingInfo;
	}

	public double getFixTick() {
		return fixTick;
	}

	public double getTickDistance() {
		return tickDistance;
	}

	public void setFixTick(double fixTick) {
		this.fixTick = fixTick;
	}

	public void setTickDistance(double tickDistance) {
		this.tickDistance = tickDistance;
	}

}
