package com.inepex.inechart.chartwidget.piechart;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.misc.ColorSet;

public class Pie {

	ArrayList<String> keys = new ArrayList<String>();
	SortedMap<String, Double> dataMap = new TreeMap<String, Double>();
	SortedMap<String, String> colorMap = new TreeMap<String, String>();
	
	ColorSet colors = new ColorSet();
	
	/**
	 * using auto colors, and sort by name
	 * @param data
	 */
	public void setData(SortedMap<String, Double> data){
		this.dataMap = data;
		for (String key : dataMap.keySet()){
			keys.add(key);
			colorMap.put(key, colors.getNextColor());
		}
	}
	
	public void addData(String name, Double value, String color){
		keys.add(name);
		dataMap.put(name, value);
		if (color == null) colorMap.put(name, colors.getNextColor());
		else colorMap.put(name, color);
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

	public ArrayList<String> getKeys() {
		return keys;
	}
	
	
	
	
}
