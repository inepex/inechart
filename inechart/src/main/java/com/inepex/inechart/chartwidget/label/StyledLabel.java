package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;

/**
 * A simple label class with padding.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class StyledLabel extends TextContainer{
	
	String text;
	
	protected static final int DEFAULT_PADDING_H = 4;
	protected static final int DEFAULT_PADDING_V = 2;
	protected int topPadding = DEFAULT_PADDING_V,
			leftPadding = DEFAULT_PADDING_H,
			bottomPadding = DEFAULT_PADDING_V,
			rightPadding = DEFAULT_PADDING_H;

	public StyledLabel(String text) {
		super();
		this.text = text;
	}
	
	public StyledLabel(String text, TextProperties textProperties) {
		super();
		this.text = text;
		this.textProperties = textProperties;
	}
	
	public StyledLabel(String text, TextProperties textProperties, int padding) {
		super();
		this.text = text;
		this.textProperties = textProperties;
		setPadding(padding);
	}
	
	public StyledLabel(String text, TextProperties textProperties, int padding, ShapeProperties background) {
		super();
		this.text = text;
		this.textProperties = textProperties;
		this.background = background;
		setPadding(padding);
	}
	
	public void setPadding(int padding){
		topPadding = bottomPadding = rightPadding = leftPadding = padding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getTopPadding() {
		return topPadding;
	}

	public void setTopPadding(int topPadding) {
		this.topPadding = topPadding;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
	}

	public int getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
	}

	public int getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
	}
	
	
}
