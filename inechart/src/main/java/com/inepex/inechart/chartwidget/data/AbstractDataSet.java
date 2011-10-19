package com.inepex.inechart.chartwidget.data;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventBus;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.misc.HasTitle;

public abstract class AbstractDataSet implements HasTitle{
	protected static ColorSet colorSet = new ColorSet();
	/**
	 * @return the colorSet
	 */
	public static ColorSet getColorSet() {
		return colorSet;
	}

	/**
	 * @param colorSet the colorSet to set
	 */
	public static void setColorSet(ColorSet colorSet) {
		AbstractDataSet.colorSet = colorSet;
	}
	protected static int autotitleMaxIndex = 0;
	
	protected String title;
	protected String description;
	
	protected boolean attached = false;
	
	protected EventBus eventBus;
	
	protected ArrayList<DataEntry> dataEntries;
	
	protected AbstractDataSet() {
		this(Defaults.name + ++autotitleMaxIndex);
	}
	
	protected AbstractDataSet(String title){
		this(title, "");
	}
	
	protected AbstractDataSet(String title, String description) {
		this.title = title;
		this.description = description;
		this.dataEntries = new ArrayList<DataEntry>();
	}

	protected DataEntry getEntryByKey(double key){
		for(DataEntry de : dataEntries){
			if(Double.compare(key, de.key) == 0){
				return de;
			}
		}
		return null;
	}
	
	protected DataEntry getEntryByTitle(String title){
		for(DataEntry de : dataEntries){
			if(title.equals(de.title)){
				return de;
			}
		}
		return null;
	}
	
	protected DataEntry getEntryByValue(double value){
		for(DataEntry de : dataEntries){
			if(Double.compare(value, de.value) == 0){
				return de;
			}
		}
		return null;
	}
	
	@Override
	public void setTitle(String title) {
		this.title = title;
		fireDataSetChangeEvent();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		fireDataSetChangeEvent();
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	protected void fireDataSetChangeEvent(){
		if (attached && eventBus != null) {
			eventBus.fireEvent(new DataSetChangeEvent(this));
		}
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
}
