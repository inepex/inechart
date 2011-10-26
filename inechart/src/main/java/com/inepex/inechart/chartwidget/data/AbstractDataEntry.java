package com.inepex.inechart.chartwidget.data;


public abstract class AbstractDataEntry {
	
	protected AbstractDataSet container;
	
	protected void fireChangeEvent(){
		if(container != null){
			container.fireDataSetChangeEvent();
		}
	}	
	
	public AbstractDataSet getContainer() {
		return container;
	}
}
