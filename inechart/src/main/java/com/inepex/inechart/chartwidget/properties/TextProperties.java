package com.inepex.inechart.chartwidget.properties;

public class TextProperties {
	protected String fontFamily;
	protected int fontSize;
	protected String fontStyle;
	protected String fontWeight;
	
	public TextProperties(String fontFamily, int fontSize){
		this(fontFamily, fontSize, "normal", "normal");
	}
	
	public TextProperties(String fontFamily, int fontSize, String fontStyle, String fontWeight) {
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontWeight = fontWeight;
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
	
	
}
