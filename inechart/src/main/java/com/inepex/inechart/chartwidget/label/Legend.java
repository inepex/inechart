package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;

/**
 * This class holds information about how to display a legend,
 * without knowing its entries.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class Legend extends TextContainer{
	public enum LegendEntryLayout{
		/**
		 * Entries displayed along a horizontal line (default)
		 */
		ROW,
		/**
		 * Entries displayed along a vertical line
		 */
		COLUMN,
		/**
		 * Entries displayed in
		
		 */
		AUTO
	}
	LegendEntryLayout legendEntryLayout;
	
	Rectangle legendSymbol;
	int paddingBetweenTextAndSymbol;
	int paddingBetweenEntries;
	TextProperties textProperties;
	
	int fixedX=-1, fixedY=-1;

	public Legend(){
		this(Defaults.legendSymbol, Defaults.textContainerText);
	}

	public Legend(Rectangle legendSymbol, TextProperties textProperties) {
		super();
		this.legendSymbol = legendSymbol;
		this.textProperties = textProperties;
		
		verticalPosition = VerticalPosition.Top; 
		horizontalPosition = HorizontalPosition.Right;
		//defaults
		paddingBetweenEntries = 5;
		paddingBetweenTextAndSymbol = 3;
		includeInPadding = true;
		legendEntryLayout = LegendEntryLayout.AUTO;
	}

	public Rectangle getLegendSymbol() {
		return legendSymbol;
	}

	public void setLegendSymbol(Rectangle legendSymbol) {
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

	/**
	 * Absolute position of the legend
	 * {@link HorizontalPosition} and {@link VerticalPosition} will be ignored,
	 * and includeInPadding is set false
	 * if one of the arguments is -1 it disables absolute positioning 
	 * @param fixedX
	 * @param fixedY
	 */
	public void setFixedPosition(int fixedX, int fixedY) {
		if(fixedX < 0 || fixedY <0){
			this.fixedX = -1;
			this.fixedY = -1;
		}
		this.fixedX = fixedX;
		this.fixedY = fixedY;
	}	

}
