package com.inepex.inechart.chartwidget.axes;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;

public class Tick implements Comparable<Tick> {
	public static final int DEFAULT_TICK_LENGTH = 7;

	public static enum TickPosition {
		Cross, To_Upper_Values, To_Lower_Values;
	}

	TickPosition tickPosition;
	HorizontalPosition tickTextHorizontalPosition;
	VerticalPosition tickTextVerticalPosition;

	/**
	 * the position of this tick on the parent axis
	 */
	double position;
	/**
	 * the grid's look, or null if has none
	 */
	LineProperties gridLine;
	/**
	 * the lookout of the tick, or null if none
	 */
	LineProperties tickLine;
	/**
	 * the length of the tick's line
	 */
	int tickLength;
	/**
	 * the tick's label
	 */
	String tickText;
	/**
	 * the 'style' of the tick's text
	 */
	TextProperties textProperties;
	/**
	 * the number -> string
	 */
	String formatString;
	/**
	 * if set to true filterFrequentTicks won't filter out this tick
	 */
	boolean unfiltereble = false;
	
	public Tick(){
		this(0);
	}

	/**
	 * Constructs a tick with default look, without label
	 * 
	 * @param position
	 */
	public Tick(double position) {
		this(position, null, Defaults.solidLine(), DEFAULT_TICK_LENGTH, "");
	}

	/**
	 * Constructs a tick with default look
	 * 
	 * @param position
	 * @param tickText
	 */
	public Tick(double position, String tickText) {
		this(position, null, Defaults.solidLine(), DEFAULT_TICK_LENGTH, tickText);
	}

	/**
	 * Contstructs a tick with the given parameters
	 * 
	 * @param position
	 * @param gridLine
	 *            null if has no grid
	 * @param tickLine
	 *            null if has no tick
	 * @param tickLength
	 *            length of the tick
	 * @param tickText
	 *            the label of the tick
	 */
	public Tick(double position, LineProperties gridLine,
			LineProperties tickLine, int tickLength, String tickText) {
		this.position = position;
		this.gridLine = gridLine;
		this.tickLine = tickLine;
		this.tickLength = tickLength;
		this.tickText = tickText;
		this.tickPosition = TickPosition.Cross;
		tickTextHorizontalPosition =  HorizontalPosition.Auto;
		tickTextVerticalPosition = VerticalPosition.Auto;
		this.textProperties = Defaults.tickTextProperties();
	}
	
	public Tick(double position, LineProperties gridLine,
			LineProperties tickLine, int tickLength, String tickText,
			TextProperties textProperties) {
		super();
		this.position = position;
		this.gridLine = gridLine;
		this.tickLine = tickLine;
		this.tickLength = tickLength;
		this.tickText = tickText;
		this.textProperties = textProperties;
		this.tickPosition = TickPosition.Cross;
		tickTextHorizontalPosition =  HorizontalPosition.Auto;
		tickTextVerticalPosition = VerticalPosition.Auto;
	}

	/**
	 * @return the position
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(double position) {
		this.position = position;
	}

	/**
	 * @return the gridLine
	 */
	public LineProperties getGridLine() {
		return gridLine;
	}

	/**
	 * @param gridLine
	 *            the gridLine to set
	 */
	public void setGridLine(LineProperties gridLine) {
		this.gridLine = gridLine;
	}

	/**
	 * @return the tickLine
	 */
	public LineProperties getTickLine() {
		return tickLine;
	}

	/**
	 * @param tickLine
	 *            the tickLine to set
	 */
	public void setTickLine(LineProperties tickLine) {
		this.tickLine = tickLine;
	}

	/**
	 * @return the tickLength
	 */
	public int getTickLength() {
		return tickLength;
	}

	/**
	 * @param tickLength
	 *            the tickLength to set
	 */
	public void setTickLength(int tickLength) {
		this.tickLength = tickLength;
	}

	/**
	 * @return the tickText
	 */
	public String getTickText() {
		return tickText;
	}

	/**
	 * @param tickText
	 *            the tickText to set
	 */
	public void setTickText(String tickText) {
		this.tickText = tickText;
	}

	public boolean isUnfiltereble() {
		return unfiltereble;
	}

	public Tick setUnfiltereble(boolean unfiltereble) {
		this.unfiltereble = unfiltereble;
		return this;
	}

	@Override
	public int compareTo(Tick arg0) {
		double diff = position - arg0.position;
		if (diff < 0)
			return -1;
		if (diff > 0)
			return 1;
		else
			return 0;
	}

	public TextProperties getTextProperties() {
		return textProperties;
	}

	public void setTextProperties(TextProperties textProperties) {
		this.textProperties = textProperties;
	}

	/**
	 * @return the formatString
	 */
	public String getFormatString() {
		return formatString;
	}

	/**
	 * @param formatString the formatString to set
	 */
	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	
}
