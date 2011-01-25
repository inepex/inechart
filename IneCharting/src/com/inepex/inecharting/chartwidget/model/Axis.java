package com.inepex.inecharting.chartwidget.model;

public class Axis {
	public static enum DataType{
		TIME,
		NUMBER
	}
	
	protected DataType dataType;

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
}
