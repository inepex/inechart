package com.inepex.inechart.chartwidget.properties;

import com.inepex.inechart.chartwidget.Defaults;

public class TextProperties {
	protected String fontFamily;
	protected int fontSize;
	protected String fontStyle;
	protected String fontWeight;
	protected Color color;
	
	public TextProperties(String fontFamily, int fontSize){
		this(fontFamily, fontSize, "normal", "normal");
	}
	
	public TextProperties(String fontFamily, int fontSize, String fontStyle, String fontWeight) {
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontWeight = fontWeight;
		this.color = Defaults.color();
	}

	public TextProperties(String fontFamily, int fontSize, String fontStyle, String fontWeight, Color color) {
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontWeight = fontWeight;
		this.color = color;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
