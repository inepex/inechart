package com.inepex.inecharting.chartwidget.model;


import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo;


public class Axis extends GraphicalObject{
	
	protected AxisDrawingInfo drawingInfo;
	/**
	 * a tick's absolute position, the other ticks' positions can be measured from it. 
	 */
	protected double fixTick = 0;
	/**
	 * distance between ticks
	 */
	protected double tickDistance = 0;
	
	protected Axes axis;
	
	

	protected Axis() {
		this.zIndex = HaszIndex.alwaysOnTop;
	}
	
	public Axis(AxisDrawingInfo drawingInfo) {
		this.drawingInfo = drawingInfo;
		this.zIndex = HaszIndex.alwaysOnTop;
	}

	public AxisDrawingInfo getDrawingInfo() {
		return drawingInfo;
	}

	public void setDrawingInfo(AxisDrawingInfo drawingInfo) {
		this.drawingInfo = drawingInfo;
	}

	public Axes getAxis() {
		return axis;
	}

	void setAxis(Axes axis) {
		this.axis = axis;
	}
	
	public double getFixTick() {
		return fixTick;
	}

	public double getTickDistance() {
		return tickDistance;
	}

	void setFixTick(double fixTick) {
		this.fixTick = fixTick;
	}

	void setTickDistance(double tickDistance) {
		this.tickDistance = tickDistance;
	}

}
