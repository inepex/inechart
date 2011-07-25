package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.properties.TextProperties;

public class Text {

	protected TextProperties textProperties;
	protected String text;
	
	public Text(String text) {
		this( text, Defaults.textProperties());
	}
	public Text(String text, TextProperties textProperties) {
		this.textProperties = textProperties;
		this.text = text;
	}
	public TextProperties getTextProperties() {
		return textProperties;
	}
	public void setTextProperties(TextProperties textProperties) {
		this.textProperties = textProperties;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
