package com.inepex.inechart.chartwidget.data;

import java.util.List;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.properties.Color;


public abstract class AbstractXYDataSet extends AbstractDataSet {

	protected Color color;
	
	protected boolean allowXDuplicates = true;
	protected boolean sortable = true;
		
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
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		fireDataSetChangeEvent();
	}
	
	public abstract boolean containsXYDataEntry(XYDataEntry entry);
	
	public abstract XYDataEntry getEntry(double x);
	
	public abstract XYDataEntry getClosestEntry(double x);
	
	public abstract XYDataEntry getEntry(double x, double y);
	
	public abstract List<? extends XYDataEntry> getXYDataEntries();
	
	public abstract List<? extends XYDataEntry> getXYDataEntries(double fromX, double toX);
	
	/**
	 * Returns the {@link AbstractDataEntry} related to the given {@link XYDataEntry}
	 * @param xyDataEntry
	 * @return null if theres no such element
	 */
	public abstract AbstractDataEntry getAbstractDataEntry(XYDataEntry xyDataEntry);
	
	public boolean isAllowXDuplicates() {
		return allowXDuplicates;
	}

	public void setAllowXDuplicates(boolean allowXDuplicates) {
		this.allowXDuplicates = allowXDuplicates;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

}
