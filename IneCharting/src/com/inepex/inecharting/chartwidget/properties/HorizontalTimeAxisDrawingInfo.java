package com.inepex.inecharting.chartwidget.properties;

import java.util.TreeMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis.Resolution;

public class HorizontalTimeAxisDrawingInfo extends HorizontalAxisDrawingInfo {

	private TreeMap<HorizontalTimeAxis.Resolution, String> dateTimeFormats;
	
	public static HorizontalTimeAxisDrawingInfo getDefaultTimeAxisDrawingInfo(){
		HorizontalTimeAxisDrawingInfo defaultTADI = new HorizontalTimeAxisDrawingInfo(
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
				"lightblue",
				AxisLocation.BOTTOM,
				10,
				20,
				new TreeMap<HorizontalTimeAxis.Resolution, String>());
		defaultTADI.addDateTimeFormat(Resolution.SECOND, "mm:ss");
		defaultTADI.addDateTimeFormat(Resolution.MINUTE, "HH:mm");
		defaultTADI.addDateTimeFormat(Resolution.HOUR, "EEE HH:mm");
		defaultTADI.addDateTimeFormat(Resolution.DAY, "MMM dd");
		defaultTADI.addDateTimeFormat(Resolution.DATE, "dd/MM/yyyy");
		return defaultTADI;
	}
	
	public HorizontalTimeAxisDrawingInfo(
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
			String backgroundColor,
			AxisLocation axisLocation,
			int tickLengthInside,
			int tickPanelHeight,
			TreeMap<HorizontalTimeAxis.Resolution, String> dateTimeFormats) {
		super(tickColor, tickLineWidth, hasGridLines,
				gridLineColor, gridLineWidth, tickTextColor,
				tickTextBackgroundColor, tickTextBackgroundOpacity,
				tickTextFontStyle, tickTextFontWeight, tickTextFontFamily,
				tickTextFontFamily,AxisType.TIME, backgroundColor, axisLocation, tickLengthInside, tickPanelHeight);
		this.dateTimeFormats = dateTimeFormats;
	}
	
		
	public void addDateTimeFormat(HorizontalTimeAxis.Resolution resolution, String format){
		dateTimeFormats.put(resolution, format);
	}
	
	public String getDateTimeFormat(HorizontalTimeAxis.Resolution resolution){
		return dateTimeFormats.get(resolution);
	}

}

