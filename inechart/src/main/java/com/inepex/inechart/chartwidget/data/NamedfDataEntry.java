package com.inepex.inechart.chartwidget.data;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.properties.Color;


public class NamedfDataEntry extends AbstractDataEntry{
	
	protected String descrition;
	protected Color color;
	
	public NamedfDataEntry(double value){
		this(Defaults.uniqueEntryName(), value, AbstractDataSet.colorSet.getNextColor());
	}
	
	public NamedfDataEntry(double value, Color color){
		this(Defaults.uniqueEntryName(), value, color);
	}

	public NamedfDataEntry(String key, double value, String description) {
		this(key, value, description, AbstractDataSet.colorSet.getNextColor());
	}
	
	public NamedfDataEntry(String key, double value, Color color) {
		this(key, value, "", color);
	}
	
	public NamedfDataEntry(String key, double value,
			String descrition, Color color) {
//		super(key, value);
		this.descrition = descrition;
		this.color = color;
	}

	public String getDescrition() {
		return descrition;
	}

	public void setDescrition(String descrition) {
		this.descrition = descrition;
		fireChangeEvent();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		fireChangeEvent();
	}
	
	

}
