package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.axes.TickFactoryGWT;

public class AnnotationHelper {

	public static final String xValue = " #xValue#";
	public static final String yValue = "#yValue#";
	public static final String name = "#name#";

	public static String replaceXYValues(String annotation, double x, double y, String xFormat, String yFormat, AxisDataType xAxisDataType, AxisDataType yAxisDataType){
		String ret = annotation.toString();

		if(ret.contains(xValue)){
			String formatted = TickFactoryGWT.formatValue(xAxisDataType, x, xFormat);
			ret = ret.replace(xValue, formatted);
		}
		if(ret.contains(yValue)){
			String formatted = TickFactoryGWT.formatValue(yAxisDataType, y, yFormat);
			ret = ret.replace(yValue, formatted);
		}
		return ret;
	}
	
	public static String replaceName(String annotation, String name){
		String ret = annotation.toString();
		if(ret.contains(AnnotationHelper.name)){
			ret.replaceAll(AnnotationHelper.name, name);
		}
		return ret;
	}

}
