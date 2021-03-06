package com.inepex.inechart.chartwidget.axes;

import java.text.SimpleDateFormat;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.properties.LineProperties;

public class Tick implements Comparable<Tick> {

	public static enum TickPosition {
		Cross, To_Higher_Values, To_Lower_Values;
	}

	TickPosition tickPosition;
	
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
	Text text;
	/**
	 * the box around text
	 */
	TextContainer textContainer;
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
		this(position, null, Defaults.solidLine(), Defaults.tickLength, "");
	}

	/**
	 * Constructs a tick with default look
	 * 
	 * @param position
	 * @param tickText
	 */
	public Tick(double position, String tickText) {
		this(position, null, Defaults.solidLine(), Defaults.tickLength, tickText);
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
		this.tickPosition = TickPosition.Cross;
		this.textContainer = Defaults.tickTextContainer();
		this.text = new Text(tickText, Defaults.tickTextProperties());
	}
	
	public Tick(double position, LineProperties gridLine,
			LineProperties tickLine, int tickLength, Text tickText,
			TextContainer textContainer) {
		super();
		this.position = position;
		this.gridLine = gridLine;
		this.tickLine = tickLine;
		this.tickLength = tickLength;
		this.tickPosition = TickPosition.Cross;
		this.textContainer = textContainer;
		this.text = tickText;
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

	/**
	 * @return the formatString
	 */
	public String getFormatString() {
		return formatString;
	}

	/**
	 * The ticks number or date value (depends on the {@link AxisDataType} of containing axis)
	 * will be formatted by 
	 *  - GWT's {@link DateTimeFormat} or {@link NumberFormat} on client
	 *  - jre's {@link SimpleDateFormat} or {@link java.text.NumberFormat} on 'server' side.
	 *  If you are using this chart on both implementations be careful to define a format that
	 *  matches both formatters 'language'. (Most formats are common.) 
	 * @param formatString the formatString to set
	 */
	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	/**
	 * @return the text
	 */
	public Text getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(Text text) {
		this.text = text;
	}

	/**
	 * @return the textContainer
	 */
	public TextContainer getTextContainer() {
		return textContainer;
	}

	/**
	 * @param textContainer the textContainer to set
	 */
	public void setTextContainer(TextContainer textContainer) {
		this.textContainer = textContainer;
	}

	/**
	 * @return the tickPosition
	 */
	public TickPosition getTickPosition() {
		return tickPosition;
	}

	/**
	 * @param tickPosition the tickPosition to set
	 */
	public void setTickPosition(TickPosition tickPosition) {
		this.tickPosition = tickPosition;
	}

	
}
