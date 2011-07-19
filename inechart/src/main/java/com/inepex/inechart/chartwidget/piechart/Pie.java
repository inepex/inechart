package com.inepex.inechart.chartwidget.piechart;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.label.HasTitle;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.properties.Color;

public class Pie{

	public class Slice implements HasTitle{
		StyledLabel name, description;
		Color c;
		protected Slice(StyledLabel name, StyledLabel description, Color c) {
			this.name = name;
			this.description = description;
			this.c = c;
		}

		@Override
		public void setName(StyledLabel name) {
			this.name = name;
		}

		@Override
		public StyledLabel getName() {
			return name;
		}

		@Override
		public void setDescription(StyledLabel description) {
			this.description = description;
		}

		@Override
		public StyledLabel getDescription() {
			return description;
		}

		@Override
		public void setName(String name) {
			setName(new StyledLabel(name));
			
		}

		@Override
		public void setDescription(String description) {
			setDescription(new StyledLabel(description));
		}
		
	}
	
	ArrayList<String> keys = new ArrayList<String>();
	SortedMap<String, Double> dataMap = new TreeMap<String, Double>();
	SortedMap<String, String> colorMap = new TreeMap<String, String>();
	SortedMap<String, Slice> sliceMap = new TreeMap<String, Pie.Slice>();
	SortedMap<String, Double> percentages = new TreeMap<String, Double>();
	ColorSet colors = new ColorSet();

	/**
	 * using auto colors, and sort by name
	 * 
	 * @param data
	 */
	public void setData(SortedMap<String, Double> data) {
		this.dataMap = data;
		for (String key : dataMap.keySet()) {
			keys.add(key);
			Color c = colors.getNextColor();
			colorMap.put(key, c.getColor());
			sliceMap.put(key, new Slice(new StyledLabel(key), null,c));
		}
		calculatePercentages();
		setLegendEntries();
	}

	public void addData(String name, Double value, String color) {
		keys.add(name);
		dataMap.put(name, value);
		if (color == null){
			Color c = colors.getNextColor();
			colorMap.put(name, c.getColor());
			sliceMap.put(name, new Slice(new StyledLabel(name), null,c));
		}
		else{
			colorMap.put(name, color);
			sliceMap.put(name, new Slice(new StyledLabel(name), null,new Color(color)));
		}
		calculatePercentages();
		setLegendEntries();
	}

	public SortedMap<String, Double> getDataMap() {
		return dataMap;
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

	public SortedMap<String, Slice> getSliceMap() {
		return sliceMap;
	}

	protected void calculatePercentages(){
		percentages = new TreeMap<String, Double>();
		// calculate percentages
		Double sum = 0.0;
		for (Double value : dataMap.values()) {
			sum += value;
		}

		for (String key : dataMap.keySet()) {
			Double value = dataMap.get(key);
			Double pctg = value / sum * 100.0;
			percentages.put(key, pctg);
		}

	}
	
	protected void setLegendEntries(){
		for(String key : sliceMap.keySet()){
			Slice s = sliceMap.get(key);
			s.setName(new StyledLabel(s.getName().getText() + " " + dataMap.get(key) + " " + Math.round(percentages.get(key)) + "%"));
		}
	}
}
