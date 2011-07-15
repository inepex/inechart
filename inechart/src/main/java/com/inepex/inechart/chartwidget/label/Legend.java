package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;

public class Legend extends TextContainer{
	public enum LegendEntryLayout{
		ROW,
		COLUMN,
		AUTO
	}
	LegendEntryLayout legendEntryLayout;
	private static final Shape DEFAULT_LEGEND_SYMBOL = new Rectangle(28, 7);
	Shape legendSymbol;
	int paddingBetweenTextAndSymbol;
	int paddingBetweenEntries;
	boolean includeInPadding;
	
	int fixedX=-1, fixedY=-1, maxWidth=-1, maxHeight=-1;

	public Legend(){
		this(DEFAULT_LEGEND_SYMBOL, DEFAULT_TEXT_PROPERTIES);
	}

	public Legend(Shape legendSymbol, TextProperties textProperties) {
		super();
		this.legendSymbol = legendSymbol;
		this.textProperties = textProperties;
		
		verticalPosition = VerticalPosition.Top;
		horizontalPosition = HorizontalPosition.Right;
		//defaults
		paddingBetweenEntries = 5;
		paddingBetweenTextAndSymbol = 3;
		includeInPadding = true;
		legendEntryLayout = LegendEntryLayout.ROW;
	}

	public Shape getLegendSymbol() {
		return legendSymbol;
	}

	public void setLegendSymbol(Shape legendSymbol) {
		this.legendSymbol = legendSymbol;
	}

	public int getPaddingBetweenTextAndSymbol() {
		return paddingBetweenTextAndSymbol;
	}

	public void setPaddingBetweenTextAndSymbol(int paddingBetweenTextAndSymbol) {
		this.paddingBetweenTextAndSymbol = paddingBetweenTextAndSymbol;
	}

	public int getPaddingBetweenEntries() {
		return paddingBetweenEntries;
	}

	public void setPaddingBetweenEntries(int paddingBetweenEntries) {
		this.paddingBetweenEntries = paddingBetweenEntries;
	}

	public LegendEntryLayout getLegendEntryLayout() {
		return legendEntryLayout;
	}

	public void setLegendEntryLayout(LegendEntryLayout legendEntryLayout) {
		this.legendEntryLayout = legendEntryLayout;
	}

	public boolean isIncludeInPadding() {
		return includeInPadding;
	}

	public void setIncludeInPadding(boolean includeInPadding) {
		this.includeInPadding = includeInPadding;
	}


	public void setFixedX(int fixedX) {
		this.fixedX = fixedX;
	}
	

	public void setFixedY(int fixedY) {
		this.fixedY = fixedY;
	}
	
	public int getWidth() {
		return width;
	}

	/**
	 * Maximum width for {@link LegendEntryLayout#AUTO}
	 * if {@link #setMaxHeight(int)} was set too, this method overrides it.
	 * @param width -1 for auto (default)
	 */
	public void setMaxWidth(int width) {
		this.maxWidth = width;
		this.maxHeight = -1;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Maximum height for {@link LegendEntryLayout#AUTO}
	 * if {@link #setMaxWidth(int)} was set too, this method overrides it.
	 * @param height -1 for auto (default)
	 */
	public void setMaxHeight(int height) {
		this.maxHeight = height;
		this.maxWidth = -1;
	}
}
