package com.inepex.inecharting.chartwidget;

import java.util.TreeMap;

import com.google.gwt.event.shared.EventBus;
import com.inepex.inecharting.chartwidget.event.EventManager;
import com.inepex.inecharting.chartwidget.model.State;
import com.inepex.inecharting.chartwidget.properties.EventManagerProperties;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.PointDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;

/**
 * Class for holding (all) information about a chart:
 * 		- sizes
 * 		- drawing points,
 * 		- model / drawing policies, etc.
 * 		- axes
 * @author Miklós Süveges / Inepex Ltd.
 */
public final class IneChartProperties {
	public enum DrawingTool {
		VAADIN_GWT_GRAPHICS,
		CANVAS
	}
	//sizes
	private int chartCanvasWidth;
	private int chartCanvasHeight;
	private int markCanvasHeight;
	private int chartCanvasTopPaddingPercentage;
	//default vp position
	private double defaultViewportMin;
	private double defaultViewportMax;
	//axes
	private HorizontalAxisDrawingInfo XAxisDrawingInfo;
	private VerticalAxisDrawingInfo YAxisDrawingInfo;
	private VerticalAxisDrawingInfo Y2AxisDrawingInfo = null;
	//styles
	private String chartCanvasBackgroundColor;
	/**
	 * drawing toolkit
	 */
	private DrawingTool drawingTool;
	//events
	private EventManagerProperties eventManagerProperties;
	
	public IneChartProperties() {
		
		XAxisDrawingInfo = HorizontalAxisDrawingInfo.getDefaultHorizontalAxisDrawingInfo();
		YAxisDrawingInfo = VerticalAxisDrawingInfo.getDefaultVerticalAxisDrawingInfo();
		drawingTool = DrawingTool.CANVAS;
		chartCanvasBackgroundColor = "white";
		eventManagerProperties = new EventManagerProperties();
	}
	public DrawingTool getDrawingTool() {
		return drawingTool;
	}

	public void setDrawingTool(DrawingTool drawingTool) {
		this.drawingTool = drawingTool;
	}
	public int getChartCanvasWidth() {
		return chartCanvasWidth;
	}
	public int getChartCanvasHeight() {
		return chartCanvasHeight;
	}
	public void setChartCanvasWidth(int chartCanvasWidth) {
		this.chartCanvasWidth = chartCanvasWidth;
	}
	public void setChartCanvasHeight(int chartCanvasHeight) {
		this.chartCanvasHeight = chartCanvasHeight;
	}

	public int getChartCanvasTopPaddingPercentage() {
		return chartCanvasTopPaddingPercentage;
	}
	public void setChartCanvasTopPaddingPercentage(
			int chartCanvasTopPaddingPercentage) {
		this.chartCanvasTopPaddingPercentage = chartCanvasTopPaddingPercentage;
	}
	public void setDefaultViewportPosition(double viewportMin, double viewportMax){
		this.defaultViewportMax = viewportMax;
		this.defaultViewportMin = viewportMin;
	}
	public double getDefaultViewportMax() {
		return defaultViewportMax;
	}
	public double getDefaultViewportMin() {
		return defaultViewportMin;
	}
	public HorizontalAxisDrawingInfo getXAxisDrawingInfo() {
		return XAxisDrawingInfo;
	}
	public VerticalAxisDrawingInfo getYAxisDrawingInfo() {
		return YAxisDrawingInfo;
	}
	public VerticalAxisDrawingInfo getY2AxisDrawingInfo() {
		return Y2AxisDrawingInfo;
	}
	/**
	 * 
	 * @param xAxisDrawingInfo if set null x axis will not be displayed
	 */
	public void setXAxisDrawingInfo(HorizontalAxisDrawingInfo xAxisDrawingInfo) {
		XAxisDrawingInfo = xAxisDrawingInfo;
	}
	/**
	 * 
	 * @param yAxisDrawingInfo  if set null y axis will not be displayed
	 */
	public void setYAxisDrawingInfo(VerticalAxisDrawingInfo yAxisDrawingInfo) {
		YAxisDrawingInfo = yAxisDrawingInfo;
	}
	/**
	 * Default value: null (y2 axis not displayed)
	 * @param y2AxisDrawingInfo  if set null y2 axis will not be displayed
	 */
	public void setY2AxisDrawingInfo(VerticalAxisDrawingInfo y2AxisDrawingInfo) {
		Y2AxisDrawingInfo = y2AxisDrawingInfo;
	}
	public int getWidgetHeight(){
		int height = chartCanvasHeight;
		if(XAxisDrawingInfo != null)
			height += XAxisDrawingInfo.getTickPanelHeight();
		return height;
	}
	public int getWidgetWidth(){
		int width = chartCanvasWidth;
		if(YAxisDrawingInfo != null)
			width += YAxisDrawingInfo.getOffChartCanvasWidth();
		if(Y2AxisDrawingInfo != null)
			width += Y2AxisDrawingInfo.getOffChartCanvasWidth();
		return width;
	}
	public void setChartCanvasBackgroundColor(String chartCanvasBackgroundColor) {
		this.chartCanvasBackgroundColor = chartCanvasBackgroundColor;
	}
	public String getChartCanvasBackgroundColor() {
		return chartCanvasBackgroundColor;
	}
	public int getMarkCanvasHeight() {
		return markCanvasHeight;
	}
	public void setMarkCanvasHeight(int markCanvasHeight) {
		this.markCanvasHeight = markCanvasHeight;
	}
	public EventManagerProperties getEventManagerProperties() {
		return eventManagerProperties;
	}
	public void setEventManagerProperties(
			EventManagerProperties eventManagerProperties) {
		this.eventManagerProperties = eventManagerProperties;
	}
	
}
