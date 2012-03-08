package com.inepex.inechart.chartwidget.data;

import java.util.ArrayList;

public class AnnotationDataSet extends AbstractDataSet {
	//TODO
	ArrayList<AnnotationDataEntry> entries;
	
	public AnnotationDataSet() {
		super();
		entries = new ArrayList<AnnotationDataEntry>();
	}
	
	public AnnotationDataSet(String title){
		super(title);
		entries = new ArrayList<AnnotationDataEntry>();
	}
	
	public AnnotationDataSet(String title, String description) {
		super(title, description);
		entries = new ArrayList<AnnotationDataEntry>();
	}
	
	@Override
	public void clear() {
		entries.clear();
	}

	

}
