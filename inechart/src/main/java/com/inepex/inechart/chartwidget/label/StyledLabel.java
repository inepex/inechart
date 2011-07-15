package com.inepex.inechart.chartwidget.label;


public class StyledLabel extends TextContainer{
	
	String text;
	protected static final int DEFAULT_PADDING_H = 8;
	protected static final int DEFAULT_PADDING_V = 8;
	protected int topPadding = DEFAULT_PADDING_V,
			leftPadding = DEFAULT_PADDING_H,
			bottomPadding = DEFAULT_PADDING_V,
			rightPadding = DEFAULT_PADDING_H;

	public StyledLabel(String text) {
		super();
		this.text = text;
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
