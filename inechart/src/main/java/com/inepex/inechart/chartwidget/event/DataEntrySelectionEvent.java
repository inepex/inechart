package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.data.AbstractDataEntry;

public class DataEntrySelectionEvent extends GwtEvent<DataEntrySelectionHandler> {
	
	public static final Type<DataEntrySelectionHandler> TYPE = new Type<DataEntrySelectionHandler>();
	
	protected AbstractDataEntry dataEntry;
	protected IneChartModule2D sourceModule;
	protected boolean select;
	
	public DataEntrySelectionEvent(AbstractDataEntry dataEntry, boolean select){
		this(dataEntry, null, select);
	}

	public DataEntrySelectionEvent(AbstractDataEntry dataEntry,
			IneChartModule2D sourceModule, boolean select) {
		this.dataEntry = dataEntry;
		this.sourceModule = sourceModule;
		this.select = select;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DataEntrySelectionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataEntrySelectionHandler handler) {
		if(select){
			handler.onSelect(this);
		}
		else{
			handler.onDeselect(this);
		}
	}

	public AbstractDataEntry getDataEntry() {
		return dataEntry;
	}

	public IneChartModule2D getSourceModule() {
		return sourceModule;
	}

	public boolean isSelect() {
		return select;
	}

}
