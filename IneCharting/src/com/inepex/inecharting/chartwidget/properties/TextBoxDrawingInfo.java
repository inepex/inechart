package com.inepex.inecharting.chartwidget.properties;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;

public class TextBoxDrawingInfo extends ShapeDrawingInfo {
	protected String textColor;
	protected Style.FontStyle textFontStyle;
	protected Style.FontWeight textFontWeight;
	protected String textFontFamily;
	protected int fontSizeInCssPx;
	
	public TextBoxDrawingInfo(
			String borderColor,
			int borderWidth,
			String fillColor,
			double fillOpacity, 
			boolean hasFill,
			String textColor,
			FontStyle textFontStyle,
			FontWeight textFontWeight,
			String textFontFamily,
			int fontSizeInCssPx) {
		super(borderColor, borderWidth, fillColor, fillOpacity, hasFill);
		this.textColor = textColor;
		this.textFontStyle = textFontStyle;
		this.textFontWeight = textFontWeight;
		this.textFontFamily = textFontFamily;
		this.fontSizeInCssPx = fontSizeInCssPx;
	}
	/**
	 * @return the fontSizeInCssPx
	 */
	public int getFontSizeInCssPx() {
		return fontSizeInCssPx;
	}
	/**
	 * @param fontSizeInCssPx the fontSizeInCssPx to set
	 */
	public void setFontSizeInCssPx(int fontSizeInCssPx) {
		this.fontSizeInCssPx = fontSizeInCssPx;
	}
	/**
	 * @return the textColor
	 */
	public String getTextColor() {
		return textColor;
	}
	/**
	 * @param textColor the textColor to set
	 */
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	/**
	 * @return the textFontStyle
	 */
	public Style.FontStyle getTextFontStyle() {
		return textFontStyle;
	}
	/**
	 * @param textFontStyle the textFontStyle to set
	 */
	public void setTextFontStyle(Style.FontStyle textFontStyle) {
		this.textFontStyle = textFontStyle;
	}
	/**
	 * @return the textFontWeight
	 */
	public Style.FontWeight getTextFontWeight() {
		return textFontWeight;
	}
	/**
	 * @param textFontWeight the textFontWeight to set
	 */
	public void setTextFontWeight(Style.FontWeight textFontWeight) {
		this.textFontWeight = textFontWeight;
	}
	/**
	 * @return the textFontFamily
	 */
	public String getTextFontFamily() {
		return textFontFamily;
	}
	/**
	 * @param textFontFamily the textFontFamily to set
	 */
	public void setTextFontFamily(String textFontFamily) {
		this.textFontFamily = textFontFamily;
	}
	

}
