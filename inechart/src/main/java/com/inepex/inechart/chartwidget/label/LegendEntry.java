package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.properties.Color;

public class LegendEntry {
	Text text;
	Color color;
	
	public LegendEntry(Text text, Color color) {
		this.text = text;
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	/**
	 * @return the text
	 */
	public Text getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(Text text) {
		this.text = text;
	}
}
