package com.inepex.inecharting.chartwidget.properties;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;

public class HorizontalAxisDrawingInfo extends AxisDrawingInfo {

	public static enum AxisLocation {
		TOP,
		BOTTOM
	}
	
	public static HorizontalAxisDrawingInfo getDefaultHorizontalAxisDrawingInfo(){
		return new HorizontalAxisDrawingInfo(
				"black",
				2,
				true,
				"lightgrey",
				2,
				"black", 
				"lightblue", 
				1, 
				Style.FontStyle.NORMAL, 
				Style.FontWeight.NORMAL,
				"verdana", 
				"#.#",
				AxisType.NUMBER,
				"lightblue",
				AxisLocation.BOTTOM,
				10,
				20);
	}
	
	private String backgroundColor;
	private AxisLocation axisLocation;
	private int tickLengthInside;
	private int tickPanelHeight;

	public HorizontalAxisDrawingInfo(
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
			String backgroundColor,
			AxisLocation axisLocation,
			int tickLengthInside,
			int tickPanelHeight) {
		super(tickColor, tickLineWidth, hasGridLines,
				gridLineColor, gridLineWidth, tickTextColor,
				tickTextBackgroundColor, tickTextBackgroundOpacity,
				tickTextFontStyle, tickTextFontWeight, tickTextFontFamily, tickTextFormat, type);
		this.backgroundColor = backgroundColor;
		this.axisLocation = axisLocation;
		this.tickLengthInside = tickLengthInside;
		this.tickPanelHeight = tickPanelHeight;
	}

	public AxisLocation getAxisLocation() {
		return axisLocation;
	}

	public void setAxisLocation(AxisLocation axisLocation) {
		this.axisLocation = axisLocation;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setTickLengthInside(int tickLengthInside) {
		this.tickLengthInside = tickLengthInside;
	}
	
	public int getTickLengthInside() {
		return tickLengthInside;
	}

	public int getTickPanelHeight() {
		return tickPanelHeight;
	}

	public void setTickPanelHeight(int tickPanelHeight) {
		this.tickPanelHeight = tickPanelHeight;
	}
	
}
