package com.inepex.inecharting.chartwidget.newimpl.axes;

import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties;

public class Tick implements Comparable<Tick>{
	public static final int DEFAULT_TICK_LENGTH = 7;
	
	/**
	 * the position of this tick on the parent axis
	 */
	protected double position;
	/**
	 * the grid's look, or null if has none 
	 */
	protected LineProperties gridLine;
	/**
	 * the lookout of the tick, or null if invisible
	 */
	protected LineProperties tickLine;
	/**
	 * the length of the tick's line
	 */
	protected int tickLength;
	/**
	 * the tick's label
	 */
	protected String tickText;
	
	/**
	 * Constructs a tick with default look, without label
	 * @param position
	 */
	public Tick(double position) {
		this(position, null, LineProperties.getDefaultSolidLine(), DEFAULT_TICK_LENGTH,  "");
	}

	/**
	 * Constructs a tick with default look
	 * @param position
	 * @param tickText
	 */
	public Tick(double position, String tickText) {
		this(position, null, LineProperties.getDefaultSolidLine(), DEFAULT_TICK_LENGTH,  tickText);
	}

	/**
	 * Contstructs a tick with the given parameters
	 * @param position
	 * @param gridLine null if has no grid
	 * @param tickLine null if has no tick
	 * @param tickLength length of the tick
	 * @param tickText the label of the tick
	 */
	public Tick(double position, LineProperties gridLine, LineProperties tickLine, int tickLength, String tickText) {
		this.position = position;
		this.gridLine = gridLine;
		this.tickLine = tickLine;
		this.tickLength = tickLength;
		this.tickText = tickText;
	}

	/**
	 * @return the position
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
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
	 * @param gridLine the gridLine to set
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
	 * @param tickLine the tickLine to set
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
	 * @param tickLength the tickLength to set
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
	 * @param tickText the tickText to set
	 */
	public void setTickText(String tickText) {
		this.tickText = tickText;
	}

	@Override
	public int compareTo(Tick arg0) {
		double diff = position - arg0.position;
		if(diff < 0)
			return -1;
		if(diff > 0)
			return 1;
		else return 0;
	}
	
}
