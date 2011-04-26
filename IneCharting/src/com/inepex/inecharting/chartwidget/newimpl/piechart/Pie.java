package com.inepex.inecharting.chartwidget.newimpl.piechart;

import java.util.SortedMap;
import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.newimpl.misc.ColorSet;

public class Pie {

	SortedMap<String, Double> dataMap = new TreeMap<String, Double>();
	SortedMap<String, String> colorMap = new TreeMap<String, String>();
	
	ColorSet colors = new ColorSet();
	
	/**
	 * using auto colors
	 * @param data
	 */
	public void setData(SortedMap<String, Double> data){
		this.dataMap = data;
		for (String key : dataMap.keySet()){
			colorMap.put(key, colors.getNextColor());
		}
	}
	
	public void addData(String name, Double value, String color){
		dataMap.put(name, value);
		colorMap.put(name, color);
	}

	public SortedMap<String, Double> getDataMap() {
		return dataMap;
	}

	public void setDataMap(SortedMap<String, Double> dataMap) {
		this.dataMap = dataMap;
	}

	public SortedMap<String, String> getColorMap() {
		return colorMap;
	}

	public void setColorMap(SortedMap<String, String> colorMap) {
		this.colorMap = colorMap;
	}
	
	
}
