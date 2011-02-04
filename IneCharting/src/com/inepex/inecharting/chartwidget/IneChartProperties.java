package com.inepex.inecharting.chartwidget;

import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.model.Point.State;
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
	private int chartCanvasWidth;
	private int chartCanvasHeight;
	private int chartCanvasTopPaddingPercentage;
	private TreeMap<State,PointDrawingInfo> defaultPointDrawingInfo = null;
	private TreeMap<Double, TreeMap<State, PointDrawingInfo>> customPointDrawingInfos = null;
	private double defaultViewportMin;
	private double defaultViewportMax;
	private HorizontalAxisDrawingInfo XAxisDrawingInfo;
	private VerticalAxisDrawingInfo YAxisDrawingInfo;
	private VerticalAxisDrawingInfo Y2AxisDrawingInfo = null;
	
	public IneChartProperties() {
		defaultPointDrawingInfo = new TreeMap<State, PointDrawingInfo>();
		customPointDrawingInfos = new TreeMap<Double, TreeMap<State,PointDrawingInfo>>();
		defaultPointDrawingInfo.put(State.ACTIVE, PointDrawingInfo.getDefaultPointDrawingInfo());
		defaultPointDrawingInfo.put(State.FOCUSED, PointDrawingInfo.getDefaultPointDrawingInfo());
		defaultPointDrawingInfo.put(State.VISIBLE, PointDrawingInfo.getDefaultPointDrawingInfo());
		XAxisDrawingInfo = HorizontalAxisDrawingInfo.getDefaultHorizontalAxisDrawingInfo();
//		Y2AxisDrawingInfo = 
		YAxisDrawingInfo = VerticalAxisDrawingInfo.getDefaultVerticalAxisDrawingInfo();
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
	/**
	 * Defines how to a draw a point in a {@link State} by default.
	 * Needless to say there is no need to set a State.INVISIBLE mapping.
	 * @param state
	 * @param info
	 */
	public void addDefaultPointDrawingInfo(State state, PointDrawingInfo info){
		defaultPointDrawingInfo.put(state, info);
	}
	/**
	 * Customizes the look of a single point in datamap. 
	 * @param data
	 * @param state
	 * @param info
	 */
	public void addCustomPointDrawingInfo(Double data, State state, PointDrawingInfo info) {
		TreeMap<State, PointDrawingInfo> pinfo = customPointDrawingInfos.get(data);
		if(pinfo.equals(null)){
			pinfo = new TreeMap<State, PointDrawingInfo>();
		}
		pinfo.put(state, info);
	}
	public TreeMap<State, PointDrawingInfo> getDefaultPointDrawingInfo() {
		return defaultPointDrawingInfo;
	}
	/**
	 * Gets a drawing info for a point. If there is not info it returns the default per state.
	 * @param data
	 * @param state
	 * @return
	 */
	public PointDrawingInfo getPointDrawingInfo(double data, State state){
		if(customPointDrawingInfos.get(data) == null || customPointDrawingInfos.get(data).get(state) ==  null)
				return defaultPointDrawingInfo.get(state);
		else
			return customPointDrawingInfos.get(data).get(state);
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
}
