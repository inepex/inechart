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
		return new VerticalAxisDrawingInfo("black",
				"black", 
				"blue", 
				1, 
				Style.FontStyle.NORMAL, 
				Style.FontWeight.NORMAL,
				"verdana", 
				"#.#",
				AxisType.NUMBER,
				"black", 
				TickLocation.INSIDE);
	}
	
	private String axisColor;
	private TickLocation tickLocation;
	

	public VerticalAxisDrawingInfo(
			String tickColor,
			String tickTextColor,
			String tickTextBackgroundColor,
			double tickTextBackgroundOpacity,
			FontStyle tickTextFontStyle,
			FontWeight tickTextFontWeight,
			String tickTextFontFamily,
			String tickTextFormat,
			AxisType type,
			String axisColor,
			TickLocation tickLocation) {
		super(tickColor, tickTextColor, tickTextBackgroundColor,
				tickTextBackgroundOpacity, tickTextFontStyle,
				tickTextFontWeight, tickTextFontFamily, tickTextFormat, type);
		this.axisColor = axisColor;
		this.tickLocation = tickLocation;
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
	
	
}
