package com.inepex.inecharting.chartwidget.properties;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;

public class AxisDrawingInfo {
	
	public static enum AxisType{
		TIME,
		NUMBER
	}

	public static AxisDrawingInfo getDefaultNumberAxisDrawingInfo(){
		return new AxisDrawingInfo(
				"black",
				"black", 
				"blue", 
				1, 
				Style.FontStyle.NORMAL, 
				Style.FontWeight.NORMAL,
				"verdana", 
				"#.#",
				AxisType.NUMBER);
	}
	
	public static AxisDrawingInfo getDefaultTimeAxisDrawingInfo(){
		return new AxisDrawingInfo(
				"black",
				"black", 
				"blue", 
				1, 
				Style.FontStyle.NORMAL, 
				Style.FontWeight.NORMAL,
				"verdana", 
				"MM/dd HH:mm",
				AxisType.TIME);
	}
	
	
	protected String tickColor;
	protected String tickTextColor;
	protected String tickTextBackgroundColor;
	protected double tickTextBackgroundOpacity;
	protected Style.FontStyle tickTextFontStyle;
	protected Style.FontWeight tickTextFontWeight;
	protected String tickTextFontFamily;
	protected String tickTextFormat;
	protected AxisType type;
	
	protected AxisDrawingInfo(
			String tickColor,
			String tickTextColor,
			String tickTextBackgroundColor,
			double tickTextBackgroundOpacity,
			FontStyle tickTextFontStyle,
			FontWeight tickTextFontWeight,
			String tickTextFontFamily,
			String tickTextFormat,
			AxisType type) {
		this.tickColor = tickColor;
		this.tickTextColor = tickTextColor;
		this.tickTextBackgroundColor = tickTextBackgroundColor;
		this.tickTextBackgroundOpacity = tickTextBackgroundOpacity;
		this.tickTextFontStyle = tickTextFontStyle;
		this.tickTextFontWeight = tickTextFontWeight;
		this.tickTextFontFamily = tickTextFontFamily;
		this.tickTextFormat = tickTextFormat;
		this.type = type;
	}

	public AxisType getType() {
		return type;
	}
	public void setType(AxisType type) {
		this.type = type;
	}
	public String getTickTextFormat() {
		return tickTextFormat;
	}
	public void setTickTextFormat(String tickTextFormat) {
		this.tickTextFormat = tickTextFormat;
	}
	public String getTickColor() {
		return tickColor;
	}
	public String getTickTextColor() {
		return tickTextColor;
	}
	public String getTickTextBackgroundColor() {
		return tickTextBackgroundColor;
	}
	public double getTickTextBackgroundOpacity() {
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
	public void setTickColor(String tickColor) {
		this.tickColor = tickColor;
	}
	public void setTickTextColor(String tickTextColor) {
		this.tickTextColor = tickTextColor;
	}
	public void setTickTextBackgroundColor(String tickTextBackgroundColor) {
		this.tickTextBackgroundColor = tickTextBackgroundColor;
	}
	public void setTickTextBackgroundOpacity(double tickTextBackgroundOpacity) {
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
