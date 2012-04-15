package com.inepex.inechart.chartwidget.axes;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.misc.HasZIndex;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;

public class Axis implements Comparable<Axis>, HasZIndex{

	// comparison helper fields
	private static int highestComparableNo = 0;
	private int comparableNo;

	/**
	 * Defines the direction of an {@link Axis}
	 */
	public static enum AxisDirection {
		Horizontal_Ascending_To_Right(1, 1), Horizontal_Ascending_To_Left(1, -1), Vertical_Ascending_To_Top(
				-1, 1), Vertical_Ascending_To_Bottom(-1, -1);
		private final int ascending;
		private final int direction;

		private AxisDirection(int direction, int ascending) {
			this.ascending = ascending;
			this.direction = direction;
		}

		public int getAscending() {
			return ascending;
		}

		public int getDirection() {
			return direction;
		}

		/**
		 * Determines whether the given axes are perpendicular
		 * 
		 * @param axis1
		 * @param axis2
		 * @return
		 */
		public static boolean isPerpendicular(Axis axis1, Axis axis2) {
			if (axis1.axisDirection.direction + axis2.axisDirection.direction == 0)
				return true;
			return false;
		}
	}

	/**
	 * Defines the position of the axis related to perpendicular main axis as:
	 * its line and its {@link Tick}s with their labels will be aligned, but the
	 * grids are unaffected (they will run through the modul's drawing area).
	 * 
	 */
	public static enum AxisPosition {
		/**
		 * in case of horizontal axis -> at the bottom '' vertical axis -> at
		 * the left
		 */
		Minimum,
		/**
		 * at the middle of the drawing area
		 */
		Middle,
		/**
		 * in case of horizontal axis -> at the top '' vertical axis -> at the
		 * right
		 */
		Maximum,
		/**
		 * Display axis at a fix position
		 */
		Fixed,
		/**
		 * Display axis at a fix position, but if it would be invisible dock to
		 * Min/Max, which is closer to its fix position
		 */
		Fixed_Dock_If_Not_Visible
	}

	public static enum AxisDataType {
		Number, Time
	}

	protected AxisDataType axisDataType;
	/**
	 * The axis will be aligned to this modul *
	 */
	protected IneChartModule2D modulToAlign;

	protected int zIndex;
	protected ArrayList<Tick> ticks;
	protected ArrayList<Object[]> gridFills;

	/**
	 * min and max values defines the visible part of this axis
	 */
	protected double min, max;
	
	protected AxisDirection axisDirection;
	protected AxisPosition axisPosition;
	/**
	 * only used when {@link AxisPosition#Fixed} or {@link AxisPosition#Fixed_Dock_If_Not_Visible}s
	 */
	protected double fixedPosition;
	/**
	 * if false the axis and all its objects will NOT displayed
	 */
	protected boolean isVisible;
	/**
	 * If you want to display this axis as a finite line, set these values, and
	 * the axis (and its ticks and their grids) will be displayed between these
	 * values
	 */
	protected double lowerEnd, upperEnd;
	protected boolean filterFrequentTicks = false;
	protected boolean autoCreateTicks = true;
	protected boolean autoCreateGrids = false;
	/**
	 * lookout of the axis' line
	 */
	protected LineProperties lineProperties;
	/**
	 * default tick
	 */
	protected Tick defaultTick;
	protected boolean displayFirstTick = true;
	
	protected StyledLabel axisLabel;
	
	public Axis() {
		this(null);
	}

	public Axis(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
		ticks = new ArrayList<Tick>();
		gridFills = new ArrayList<Object[]>();
		comparableNo = Axis.highestComparableNo++;

		// defaults
		axisPosition = AxisPosition.Minimum;
		axisDirection = AxisDirection.Horizontal_Ascending_To_Right;
		axisDataType = AxisDataType.Number;
		lowerEnd = -Double.MAX_VALUE;
		upperEnd = Double.MAX_VALUE;
		isVisible = true;
		axisLabel = Defaults.axisLabel();
	}

	public void addTick(Tick tick) {
		ticks.add(tick);
		
	}

	public void clearTicks() {
		ticks.clear();
		
	}

	public void fillBetween(Tick tick1, Tick tick2, Color color) {
		if (color == null)
			return;
		switch (tick1.compareTo(tick2)) {
		case 0:
			return;
		case 1: //tick1 > tick2
			gridFills.add(new Object[] { tick2, tick1, color });
			break;
		case -1:
			gridFills.add(new Object[] { tick1, tick2, color });
			break;
		}
		
	}

	public void fillBetween(double value1, double value2, Color color){
		fillBetween(new Tick(value1), new Tick(value2), color);
	}

	public void removeFill(Tick tick1, Tick tick2) {
		Tick lower = null, upper = null;
		switch (tick1.compareTo(tick2)) {
		case 0:
			return;
		case 1:
			lower = tick2;
			upper = tick1;
			break;
		case -1:
			lower = tick1;
			upper = tick2;
			break;
		}
		Object[] toRemove = null;
		for (Object[] triple : gridFills) {
			if (((Tick) triple[0]).position == lower.position
					&& ((Tick) triple[1]).position == upper.position) {
				toRemove = triple;
				break;
			}
		}
		if (toRemove != null)
			gridFills.remove(toRemove);
		
	}

	public void removeFill(double value1, double value2){
		removeFill(new Tick(value1), new Tick(value2));
	}
	
	/**
	 * @return the lineProperties
	 */
	public LineProperties getLineProperties() {
		return lineProperties;
	}

	/**
	 * @param lineProperties
	 *            the lineProperties to set
	 */
	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
		
	}

	public ArrayList<Tick> getVisibleTicks() {
		java.util.Collections.sort(ticks);
		ArrayList<Tick> vTicks = new ArrayList<Tick>();
		for (Tick tick : ticks) {
			if (tick.position > max || tick.position > upperEnd)
				break;
			else if (tick.position >= min && tick.position >= lowerEnd)
				vTicks.add(tick);
		}
		return vTicks;
	}

	/**
	 * @return the max
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMax(double max) {
		this.max = max;
		
	}

	/**
	 * @return the min
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(double min) {
		this.min = min;
		
	}

	@Override
	public int compareTo(Axis o) {
		return comparableNo - o.comparableNo;
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		
	}

	@Override
	public int getZIndex() {
		return zIndex;
	}

	/**
	 * @return the ticks
	 */
	public ArrayList<Tick> getTicks() {
		return ticks;
	}

	/**
	 * @param ticks
	 *            the ticks to set
	 */
	public void setTicks(ArrayList<Tick> ticks) {
		if (this.ticks != null){
			this.ticks.clear();
		}
		this.ticks = ticks;
		
	}

	/**
	 * @return the axisOrientation
	 */
	public AxisDirection getAxisDirection() {
		return axisDirection;

	}

	/**
	 * @param axisOrientation
	 *            the axisOrientation to set
	 */
	public void setAxisDirection(AxisDirection axisDirection) {
		this.axisDirection = axisDirection;
		
	}

	/**
	 * @return the axisPosition
	 */
	public AxisPosition getAxisPosition() {
		return axisPosition;
	}

	/**
	 * @param axisPosition
	 *            the axisPosition to set
	 */
	public void setAxisPosition(AxisPosition axisPosition) {
		this.axisPosition = axisPosition;
		
	}

	/**
	 * @return the lowerEnd
	 */
	public double getLowerEnd() {
		return lowerEnd;
	}

	/**
	 * @param lowerEnd
	 *            the lowerEnd to set
	 */
	public void setLowerEnd(double lowerEnd) {
		this.lowerEnd = lowerEnd;
		
	}

	/**
	 * @return the upperEnd
	 */
	public double getUpperEnd() {
		return upperEnd;
	}

	/**
	 * @param upperEnd
	 *            the upperEnd to set
	 */
	public void setUpperEnd(double upperEnd) {
		this.upperEnd = upperEnd;
		
	}

	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param isVisible
	 *            the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		
	}

	public boolean isHorizontal() {
		if (axisDirection.getDirection() == 1)
			return true;
		else
			return false;
	}

	/**
	 * @return the modulToAlign
	 */
	public IneChartModule2D getModulToAlign() {
		return modulToAlign;
	}

	/**
	 * Do NOT use this method, it will be automatically set whenever you set a
	 * {@link IneChartModule2D}'s axis
	 * 
	 * @param modulToAlign
	 *            the modulToAlign to set
	 */
	public void setModulToAlign(IneChartModule2D modulToAlign) {
		this.modulToAlign = modulToAlign;
		
	}
	
	/**
	 * 
	 * @return the related modules main axis which is perpendicular to this axis
	 */
	public Axis getPerpendicularAxis(){
		if(modulToAlign == null){
			return null;
		}
		if (AxisDirection.isPerpendicular(this, modulToAlign.getYAxis())){
			return modulToAlign.getYAxis();
		}
		else {
			return modulToAlign.getXAxis();
		}
	}
	
	
	
	public boolean isFilterFrequentTicks() {
		return filterFrequentTicks;
	}

	public void setFilterFrequentTicks(boolean filterFrequentTicks) {
		this.filterFrequentTicks = filterFrequentTicks;
	}

	public boolean isAutoCreateTicks() {
		return autoCreateTicks;
	}
	
	public void setAutoCreateTicks(boolean autoCreateTicks) {
		this.autoCreateTicks = autoCreateTicks;
	}

	public boolean isAutoCreateGrids() {
		return autoCreateGrids;
	}

	public void setAutoCreateGrids(boolean autoCreateGrids) {
		this.autoCreateGrids = autoCreateGrids;
	}

	/**
	 * @return the defaultTick
	 */
	public Tick getDefaultTick() {
		return defaultTick;
	}

	/**
	 * If set all of the ticks in this axis will have the same
	 *  - textFormat
	 *  - tickLine
	 *  - gridLine
	 *  as this tick.
	 * @param defaultTick
	 */
	public void setDefaultTick(Tick defaultTick) {
		this.defaultTick = defaultTick;
	}

	/**
	 * @return the axisDataType
	 */
	public AxisDataType getAxisDataType() {
		return axisDataType;
	}

	/**
	 * Specify that this axis has a {@link AxisDataType#Number} or {@link AxisDataType#Time} domain.
	 * @param axisDataType the axisDataType to set
	 */
	public void setAxisDataType(AxisDataType axisDataType) {
		this.axisDataType = axisDataType;
	}

	/**
	 * @return the fixedPosition
	 */
	public double getFixedPosition() {
		return fixedPosition;
	}

	/**
	 * @param fixedPosition the fixedPosition to set
	 */
	public void setFixedPosition(double fixedPosition) {
		this.fixedPosition = fixedPosition;
	}
	
	public StyledLabel getAxisLabel() {
		return axisLabel;
	}
	
	public void setAxisLabel(StyledLabel axisLabel) {
		this.axisLabel = axisLabel;
	}
	
	public void setAxisLabel(String axisLabel) {
		this.axisLabel.getText().setText(axisLabel);
	}

	public boolean isDisplayFirstTick() {
		return displayFirstTick;
	}

	public void setDisplayFirstTick(boolean displayFirstTick) {
		this.displayFirstTick = displayFirstTick;
	}
}
