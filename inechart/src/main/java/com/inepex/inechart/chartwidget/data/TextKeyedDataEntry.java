package com.inepex.inechart.chartwidget.data;

public class TextKeyedDataEntry extends ValueDataEntry{

	protected String key;
	
	public TextKeyedDataEntry(String key, double value) {
		super(value);
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		fireChangeEvent();
	}

	

}
