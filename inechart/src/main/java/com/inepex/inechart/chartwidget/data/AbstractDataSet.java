package com.inepex.inechart.chartwidget.data;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartEventManager;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
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
	
	protected IneChartEventManager eventManager;
		
	protected AbstractDataSet() {
		this(Defaults.name + ++autotitleMaxIndex);
	}
	
	protected AbstractDataSet(String title){
		this(title, "");
	}
	
	protected AbstractDataSet(String title, String description) {
		this.title = title;
		this.description = description;
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
	
	public void fireDataSetChangeEvent(){
		if (attached && eventManager != null) {
			eventManager.fireEvent(new DataSetChangeEvent(this));
		}
	}
	
	public void fireDataEntrySelectionEvent(AbstractDataEntry entry, boolean selected){
		if (attached && eventManager != null) {
			eventManager.fireEvent(new DataEntrySelectionEvent(entry, selected));
		}
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	public void setEventManager(IneChartEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public abstract void clear();
	
	public boolean contains(AbstractDataEntry dataEntry){
		return dataEntry.container == this;
	}
}
