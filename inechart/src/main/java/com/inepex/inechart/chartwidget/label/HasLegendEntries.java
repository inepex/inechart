package com.inepex.inechart.chartwidget.label;

import java.util.TreeMap;

import com.inepex.inechart.chartwidget.properties.Color;

/**
 * 
 * Every module which wants to display entries in the legend must implement this interface,
 * and the chart must register it to {@link LabelFactoryBase} when created.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public interface HasLegendEntries {

	/**
	 * 
	 * @return mapping: key: name, value: {@link Color}
	 */
	TreeMap<String, Color> getLegendEntries();
	/**
	 * sets if this module should display its entries or not
	 */
	void setDisplayEntries(boolean displayEntries);
	/**
	 * 
	 * @return true if this module displayes entries in legend
	 */
	boolean isDisplayEntries();
	/**
	 * 
	 * @param legendEntries mapping: key: name, value: {@link Color}
	 */
	void setLegendEntries(TreeMap<String, Color> legendEntries);
}
