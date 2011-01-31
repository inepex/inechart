package com.inepex.inecharting.chartwidget.properties;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;

public class AxisDrawingInfo {
	public static enum TickLocation{
		INSIDE,
		OUTSIDE,
		OVER
	}
	
	public static final int AUTO_TICK_LENGTH = -1;
	
	protected String axisColor;
	protected String backgrounColor;
	protected TickLocation tickLocation;
	protected String tickColor;
	protected int tickLength;
	protected String tickTextColor;
	protected String tickTextBackgroundColor;
	protected String tickTextBackgroundOpacity;
	protected Style.FontStyle tickTextFontStyle;
	protected Style.FontWeight tickTextFontWeight;
	protected String tickTextFontFamily;
	protected String tickTextFormat;

	public String getTickTextFormat() {
		return tickTextFormat;
	}

	public void setTickTextFormat(String tickTextFormat) {
		this.tickTextFormat = tickTextFormat;
	}
	
	public String getAxisColor() {
		return axisColor;
	}
	public String getBackgrounColor() {
		return backgrounColor;
	}
	public TickLocation getTickLocation() {
		return tickLocation;
	}
	public String getTickColor() {
		return tickColor;
	}
	public int getTickLength() {
		return tickLength;
	}
	public String getTickTextColor() {
		return tickTextColor;
	}
	public String getTickTextBackgroundColor() {
		return tickTextBackgroundColor;
	}
	public String getTickTextBackgroundOpacity() {
		return tickTextBackgroundOpacity;
	}
	public Style.FontStyle getTickTextFontStyle() {
		return tickTextFontStyle;
	}
	public Style.FontWeight getTickTextFontWeight() {
		return tickTextFontWeight;
	}
	public String getTickTextFontFamily() {
		return tickTextFontFamily;
	}
	public void setAxisColor(String axisColor) {
		this.axisColor = axisColor;
	}
	public void setBackgrounColor(String backgrounColor) {
		this.backgrounColor = backgrounColor;
	}
	public void setTickLocation(TickLocation tickLocation) {
		this.tickLocation = tickLocation;
	}
	public void setTickColor(String tickColor) {
		this.tickColor = tickColor;
	}
	public void setTickLength(int tickLength) {
		this.tickLength = tickLength;
	}
	public void setTickTextColor(String tickTextColor) {
		this.tickTextColor = tickTextColor;
	}
	public void setTickTextBackgroundColor(String tickTextBackgroundColor) {
		this.tickTextBackgroundColor = tickTextBackgroundColor;
	}
	public void setTickTextBackgroundOpacity(String tickTextBackgroundOpacity) {
		this.tickTextBackgroundOpacity = tickTextBackgroundOpacity;
	}
	public void setTickTextFontStyle(Style.FontStyle tickTextFontStyle) {
		this.tickTextFontStyle = tickTextFontStyle;
	}
	public void setTickTextFontWeight(Style.FontWeight tickTextFontWeight) {
		this.tickTextFontWeight = tickTextFontWeight;
	}
	public void setTickTextFontFamily(String tickTextFontFamily) {
		this.tickTextFontFamily = tickTextFontFamily;
	}
}
