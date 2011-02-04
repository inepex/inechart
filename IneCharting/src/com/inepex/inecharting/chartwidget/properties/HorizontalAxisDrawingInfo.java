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
				"black", 
				"blue", 
				1, 
				Style.FontStyle.NORMAL, 
				Style.FontWeight.NORMAL,
				"verdana", 
				"MM/dd HH:mm",
				AxisType.TIME,
				"blue",
				AxisLocation.BOTTOM);
	}
	
	private String backgroundColor;
	private AxisLocation axisLocation;
	
	public HorizontalAxisDrawingInfo(
			String tickColor,
			String tickTextColor,
			String tickTextBackgroundColor,
			double tickTextBackgroundOpacity,
			FontStyle tickTextFontStyle, 
			FontWeight tickTextFontWeight,
			String tickTextFontFamily,
			String tickTextFormat,
			AxisType type,
			String backgroundColor,
			AxisLocation axisLocation) {
		super(tickColor, tickTextColor, tickTextBackgroundColor,
				tickTextBackgroundOpacity, tickTextFontStyle,
				tickTextFontWeight, tickTextFontFamily, tickTextFormat, type);
		this.backgroundColor = backgroundColor;
		this.axisLocation = axisLocation;
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
	
}
