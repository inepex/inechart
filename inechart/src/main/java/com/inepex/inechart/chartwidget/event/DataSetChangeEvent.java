package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inechart.chartwidget.data.AbstractDataSet;

public class DataSetChangeEvent extends GwtEvent<DataSetChangeHandler> {
	
	public static final Type<DataSetChangeHandler> TYPE = new Type<DataSetChangeHandler>(); 
	
	protected AbstractDataSet dataSet;
	
	public DataSetChangeEvent(AbstractDataSet dataSet) {
		this.dataSet = dataSet;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DataSetChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataSetChangeHandler handler) {
		handler.onDataSetChange(this);
	}

}
