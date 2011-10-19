package com.inepex.inechart.chartwidget.data;

import java.util.Comparator;

import com.inepex.inechart.chartwidget.misc.HasTitle;
import com.inepex.inechart.chartwidget.properties.Color;

public class DataEntry implements HasTitle{
	
	public static Comparator<DataEntry> titleComparator(){
		return new Comparator<DataEntry>() {

			@Override
			public int compare(DataEntry arg0, DataEntry arg1) {
				if(arg0.title == null || arg1 == null){
					return 0;
				}
				else {
					return arg0.title.compareTo(arg1.title);
				}
			}
		};
	}

	public static Comparator<DataEntry> keyComparator(){
		return new Comparator<DataEntry>() {

			@Override
			public int compare(DataEntry arg0, DataEntry arg1) {
				return ((Double)arg0.key).compareTo(arg1.key);
			}
		};
	}

	public static Comparator<DataEntry> valueComparator(){
		return new Comparator<DataEntry>() {

			@Override
			public int compare(DataEntry arg0, DataEntry arg1) {
				return ((Double)arg0.value).compareTo(arg1.value);
			}
		};
	}
	
	protected AbstractDataSet container;

	protected double value;
	protected double key;
	protected Color color;
	protected String title;
	protected String description;
	protected boolean titleIsKey;

	public DataEntry(double key, double value) {
		this.value = value;
		this.key = key;
		titleIsKey = false;
	}

	public DataEntry(String title, double value) {
		this.value = value;
		this.title = title;
		titleIsKey = true;
	}
	
	protected void fireChangeEvent(){
		if(container != null){
			container.fireDataSetChangeEvent();
		}
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		fireChangeEvent();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		fireChangeEvent();
	}

	@Override
	public String getDescription() {
		return description;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		fireChangeEvent();
	}

	public double getKey() {
		return key;
	}

	public void setKey(double key) {
		this.key = key;
		fireChangeEvent();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		fireChangeEvent();		
	}

	public boolean isTitleIsKey() {
		return titleIsKey;
	}
	
	
}
