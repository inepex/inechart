package com.inepex.inechart.chartwidget.data;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.properties.Color;


public abstract class AbstractXYDataSet extends AbstractDataSet {

	protected Color color;
		
	protected AbstractXYDataSet() {
		this(Defaults.name + ++autotitleMaxIndex);
	}
	
	protected AbstractXYDataSet(String title){
		this(title, "");
	}
	
	protected AbstractXYDataSet(String title, String description) {
		this.title = title;
		this.description = description;
		this.color = colorSet.getNextColor();
	}
	
	protected AbstractXYDataSet(String title, String description, Color color) {
		this.color = color;
		this.title = title;
		this.description = description;
	}


	public abstract double getxMax();

	public abstract double getyMax();
	
	public abstract double getxMin();
	
	public abstract double getyMin();
	
	protected abstract double getXForEntry(AbstractDataEntry child);
	
	public abstract boolean isSortable();
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		fireDataSetChangeEvent();
	}
	
	public abstract boolean containsXYDataEntry(XYDataEntry entry);
	
	public abstract ArrayList<XYDataEntry> getXYDataEntries();

}
