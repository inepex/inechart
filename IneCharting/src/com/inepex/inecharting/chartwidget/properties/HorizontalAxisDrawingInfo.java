package com.inepex.inecharting.chartwidget.properties;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;

public class HorizontalAxisDrawingInfo extends AxisDrawingInfo {

	public static enum AxisLocation {
		TOP,
//		TOP_OVER_CURVES,
		BOTTOM,
//		BOTTOM_OVER_CURVES,
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
				Style.FontWeight.BOLD,
				"Calibri", 
				"#.#",
				AxisType.NUMBER,
				ShapeDrawingInfo.getDefaultShapeDrawingInfo(),
				AxisLocation.BOTTOM,
				10,
				20);
	}
	
//	private String backgroundColor;
	private ShapeDrawingInfo axisPanelDrawingInfo;
	private AxisLocation axisLocation;
	private int tickLengthOutsideAxisPanel;
	private int axisPanelHeight;

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
			ShapeDrawingInfo axisPanelDrawingInfo,
			AxisLocation axisLocation,
			int tickLengthInside,
			int tickPanelHeight) {
		super(tickColor, tickLineWidth, hasGridLines,
				gridLineColor, gridLineWidth, tickTextColor,
				tickTextBackgroundColor, tickTextBackgroundOpacity,
				tickTextFontStyle, tickTextFontWeight, tickTextFontFamily, tickTextFormat, type);
		this.axisPanelDrawingInfo = axisPanelDrawingInfo;
		this.axisLocation = axisLocation;
		this.tickLengthOutsideAxisPanel = tickLengthInside;
		this.axisPanelHeight = tickPanelHeight;
	}

	public AxisLocation getAxisLocation() {
		return axisLocation;
	}

	public void setAxisLocation(AxisLocation axisLocation) {
		this.axisLocation = axisLocation;
	}

	public ShapeDrawingInfo getAxisPanelDrawingInfo() {
		return axisPanelDrawingInfo;
	}
	
	public void setAxisPanelDrawingInfo(ShapeDrawingInfo axisPanelDrawingInfo) {
		this.axisPanelDrawingInfo = axisPanelDrawingInfo;
	}

	public void setTickLengthOutsideAxisPanel(int tickLengthInside) {
		this.tickLengthOutsideAxisPanel = tickLengthInside;
	}
	
	public int getTickLengthOutsideAxisPanel() {
		return tickLengthOutsideAxisPanel;
	}

	public int getAxisPanelHeight() {
		return axisPanelHeight;
	}

	public void setAxisPanelHeight(int tickPanelHeight) {
		this.axisPanelHeight = tickPanelHeight;
	}
	
}
