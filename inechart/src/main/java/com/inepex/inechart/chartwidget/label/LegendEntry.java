package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.properties.Color;

public class LegendEntry {
	HasTitle title;
	Color color;
	public LegendEntry(HasTitle title, Color color) {
		this.title = title;
		this.color = color;
	}
	public HasTitle getTitle() {
		return title;
	}
	public void setTitle(HasTitle title) {
		this.title = title;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
