package com.inepex.inechart.chartwidget.data;

public class KeyValueDataEntry extends ValueDataEntry{

	protected double key;

	public KeyValueDataEntry(double key, double value) {
		super(value);

		this.key = key;
	}

	public double getKey() {
		return key;
	}

	public void setKey(double key) {
		this.key = key;
		fireChangeEvent();
	}



	@Override
	public double getX() {
		return key;
	}
}
