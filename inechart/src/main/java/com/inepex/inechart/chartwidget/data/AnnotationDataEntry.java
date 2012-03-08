package com.inepex.inechart.chartwidget.data;

import com.inepex.inechart.chartwidget.misc.HasTitle;

public class AnnotationDataEntry extends AbstractDataEntry implements HasTitle {

	private String title;
	private String description;
	private double key;

	public AnnotationDataEntry(double key, String title) {
		super();
		this.title = title;
		this.key = key;
	}

	public AnnotationDataEntry(double key, String title, String description) {
		super();
		this.title = title;
		this.key = key;
		this.description = description;
	}

	
	public double getKey() {
		return key;
	}

	public void setKey(double key) {
		this.key = key;
		fireChangeEvent();
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		fireChangeEvent();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		fireChangeEvent();
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	
}

