package com.inepex.inecharting.chartwidget.properties;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;

public class VerticalAxisDrawingInfo extends AxisDrawingInfo {

	public static enum TickLocation{
		INSIDE,
		OUTSIDE,
		OVER
	}
	
	public static VerticalAxisDrawingInfo getDefaultVerticalAxisDrawingInfo(){
		return new VerticalAxisDrawingInfo(
				"black",
				2,
				true,
				"lightgrey",
				1,
				"black", 
				"blue", 
				1, 
				Style.FontStyle.NORMAL, 
				Style.FontWeight.NORMAL,
				"verdana", 
				"#.#",
				AxisType.NUMBER,
				"black", 
				TickLocation.INSIDE,
				20);
	}
	
	private String axisColor;
	private TickLocation tickLocation;
	private int tickLength;

	public VerticalAxisDrawingInfo(
			String tickColor,
			int tickLineWidth,
			boolean hasGridLines,
			String gridLineColor,
			int gridLineWidth,
			String tickTextColor,
			String tickTextBackgroundColor,
			double tickTextBackgroundOpacity,
			FontStyle tickTextFontStyle,
			FontWeight tickTextFontWeight,
			String tickTextFontFamily,
			String tickTextFormat,
			AxisType type,
			String axisColor,
			TickLocation tickLocation,
			int tickLength) {
		super(tickColor, tickLineWidth, hasGridLines,
					gridLineColor, gridLineWidth, tickTextColor,
					tickTextBackgroundColor, tickTextBackgroundOpacity,
					tickTextFontStyle, tickTextFontWeight, tickTextFontFamily, tickTextFormat, type);
		this.axisColor = axisColor;
		this.tickLocation = tickLocation;
		this.tickLength = tickLength;
	}
	public String getAxisColor() {
		return axisColor;
	}
	public TickLocation getTickLocation() {
		return tickLocation;
	}
	public void setAxisColor(String axisColor) {
		this.axisColor = axisColor;
	}
	public void setTickLocation(TickLocation tickLocation) {
		this.tickLocation = tickLocation;
	}
	public int getTickLength() {
		return tickLength;
	}
	public void setTickLength(int tickLength) {
		this.tickLength = tickLength;
	}
	public int getOffChartCanvasWidth(){
		switch (tickLocation) {
		case INSIDE:
			return 0;
		case OUTSIDE:
			return tickLength;
		case OVER:
			return tickLength/2;
		}
		return 0;
	}
}
