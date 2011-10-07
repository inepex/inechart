package com.inepex.inechart.chartwidget.label;

import java.util.TreeMap;

import com.inepex.inechart.chartwidget.properties.Color;

public interface HasLegendEntries {
	
	TreeMap<String, Color> getLegendEntries();
	
	boolean isDisplayEntries();
	
	void setDisplayLegendEntries(boolean displayEntries);
	
	void setLegendEntries(TreeMap<String, Color> legendEntries);
}
