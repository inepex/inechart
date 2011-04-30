package com.inepex.inechart.chartwidget.misc;

public class ColorSet {

	String[] colors = {"#edc240", "#afd8f8", "#cb4b4b", "#4da74d", "#9440ed", "#9440ed", "#9440ed", "#9440ed"};
	
	int actual = 0;
	
	public ColorSet() {
	}
	
	public String getNextColor(){
		return colors[actual++];
	}
	
}
