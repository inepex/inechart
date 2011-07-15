package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;

public abstract class TextContainer implements Comparable<TextContainer>{
	private static int highestComparatorID = Integer.MIN_VALUE;
	private final int comparatorID;
	static final TextProperties DEFAULT_TEXT_PROPERTIES = new TextProperties("Arial, sans-serif", 10);
	static final ShapeProperties DEFAULT_BACKGROUND = new ShapeProperties(new LineProperties(1), new Color("#ffffff", 0.8));
	VerticalPosition verticalPosition;
	HorizontalPosition horizontalPosition;
	TextProperties textProperties;
	int positionX=0, positionY=0;
	int width = 0, height = 0;
	ShapeProperties background;
	int backgroundRoundedCornerRadius = 0;
	
	protected TextContainer(){
		this(VerticalPosition.Auto, HorizontalPosition.Auto, DEFAULT_TEXT_PROPERTIES, DEFAULT_BACKGROUND);
	}
	
	protected TextContainer(VerticalPosition verticalPosition,
			HorizontalPosition horizontalPosition,
			TextProperties textProperties, ShapeProperties background) {
		this.verticalPosition = verticalPosition;
		this.horizontalPosition = horizontalPosition;
		this.textProperties = textProperties;
		this.background = background;
		comparatorID = ++highestComparatorID;
	}
	public VerticalPosition getVerticalPosition() {
		return verticalPosition;
	}
	public void setVerticalPosition(VerticalPosition verticalPosition) {
		this.verticalPosition = verticalPosition;
	}
	public HorizontalPosition getHorizontalPosition() {
		return horizontalPosition;
	}
	public void setHorizontalPosition(HorizontalPosition horizontalPosition) {
		this.horizontalPosition = horizontalPosition;
	}
	public TextProperties getTextProperties() {
		return textProperties;
	}
	public void setTextProperties(TextProperties textProperties) {
		this.textProperties = textProperties;
	}
	public ShapeProperties getBackground() {
		return background;
	}
	public void setBackground(ShapeProperties background) {
		this.background = background;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

	@Override
	public int compareTo(TextContainer o) {
		return this.comparatorID - o.comparatorID;
	}

	public int getBackgroundRoundedCornerRadius() {
		return backgroundRoundedCornerRadius;
	}

	public void setBackgroundRoundedCornerRadius(int backgroundRoundedCornerRadius) {
		this.backgroundRoundedCornerRadius = backgroundRoundedCornerRadius;
	}
	
	
}
