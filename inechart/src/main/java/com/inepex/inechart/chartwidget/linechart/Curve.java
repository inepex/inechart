package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.data.AbstractDataEntry;
import com.inepex.inechart.chartwidget.data.AbstractXYDataSet;
import com.inepex.inechart.chartwidget.data.KeyValueDataSet;
import com.inepex.inechart.chartwidget.data.XYDataEntry;
import com.inepex.inechart.chartwidget.misc.HasShadow;
import com.inepex.inechart.chartwidget.misc.HasZIndex;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.shape.Shape;

/**
 * 
 * Represents a Line- or a Point- (or both) curve's model.
 * Stores the information about how it should be displayed.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public class Curve implements HasZIndex, HasShadow, Comparable<Curve>{
	// comparison helper fields
	private static int highestComparableNo = 0;
	private int comparableNo;

	private LineChart parent;
	/**
	 * Underlying data, curve must not change it
	 */
	AbstractXYDataSet dataSet;

	/**
	 * {@link DataPoint}s created from {@link AbstractDataEntry}s
	 */
	ArrayList<DataPoint> dataPoints;
	ArrayList<DataPoint> visibleDataPoints;
	TreeMap<XYDataEntry, DataPoint> entryPointMap;
	ArrayList<XYDataEntry> unfilterableEntries;


	// Fills
	TreeMap<Curve, Color> toCurveFills;
	TreeMap<Double, Color> toYFills;

	// line
	boolean hasLine = true;
	LineProperties lineProperties;
	boolean autoFill = false;
	ArrayList<DataPoint> discontinuitiesAsPoint;
	ArrayList<XYDataEntry> discontinuities;

	// points
	boolean hasPoint = false;
	Shape pointShape;
	ArrayList<XYDataEntry> selectedEntries;

	// shadow
	Color shadowColor;
	double shadowOffsetX, shadowOffsetY;
	boolean hasShadow = true;
	int zIndex = Integer.MIN_VALUE;
	boolean visible = true;

	public Curve(){
		this(new KeyValueDataSet());
	}

	public Curve(AbstractXYDataSet dataSet){
		comparableNo = highestComparableNo++;
		dataPoints = new ArrayList<DataPoint>();
		visibleDataPoints = new ArrayList<DataPoint>();
		selectedEntries = new ArrayList<XYDataEntry>();
		unfilterableEntries = new ArrayList<XYDataEntry>();
		discontinuities = new ArrayList<XYDataEntry>();
		discontinuitiesAsPoint = new ArrayList<DataPoint>();
		entryPointMap = new TreeMap<XYDataEntry, DataPoint>();
		setShadowOffsetY(Defaults.shadowOffsetX);
		setShadowOffsetX(Defaults.shadowOffsetY);
		setShadowColor(Defaults.shadowColor());
		this.dataSet = dataSet;
	}

	public void setDataSet(AbstractXYDataSet dataSet){
		this.dataSet = dataSet;
		if(parent != null && parent.getModuleAssist().isClientSide()){
			dataSet.setAttached(true);
			dataSet.setEventManager(parent.getModuleAssist().getEventManager());
		}
	}

	/**
	 * @return the dataSet
	 */
	public AbstractXYDataSet getDataSet() {
		return dataSet;
	}

	/**
	 * Fills the area between this and the given curve
	 * 
	 * @param curve
	 * @param color
	 *            {@link Color}
	 */
	public void addFill(Curve curve, Color color) {
		if (this.toCurveFills == null)
			toCurveFills = new TreeMap<Curve, Color>();
		toCurveFills.put(curve, color);
	}

	/**
	 * Fills an area between the curve and a horizontal line at the given y
	 * value
	 * 
	 * @param y
	 *            y position of the line
	 * @param color
	 *            {@link Color}
	 */
	public void addFill(double y, Color color) {
		if (this.toYFills == null)
			toYFills = new TreeMap<Double, Color>();
		toYFills.put(y, color);
	}

	/**
	 * Fills an area between a tick and the curve Same as
	 * addFill(tick.getPosition(), color);
	 * 
	 * @param tick
	 * @param color
	 */
	public void addFill(Tick tick, Color color) {
		if (this.toYFills == null)
			toYFills = new TreeMap<Double, Color>();
		toYFills.put(tick.getPosition(), color);
	}

	/**
	 * Fills the area between this and the given curve
	 * 
	 * @param curve
	 * @param color
	 *            {@link Color}
	 */
	public void removeFill(Curve curve) {
		toCurveFills.remove(curve);
	}

	/**
	 * Fills the area between the curve and a horizontal line at the given y
	 * value
	 * 
	 * @param y
	 *            y position of the line
	 * @param color
	 *            {@link Color}
	 */
	public void removeFill(double y) {
		toYFills.remove(y);
	}

	/**
	 * @return the lineProperties
	 */
	public LineProperties getLineProperties() {
		return lineProperties;
	}

	/**
	 * @param lineProperties
	 *            the line of the linechart
	 */
	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}

	@Override
	public void setShadowOffsetX(double offsetX) {
		this.shadowOffsetX = offsetX;
	}

	@Override
	public void setShadowOffsetY(double offsetY) {
		this.shadowOffsetY = offsetY;
	}

	@Override
	public double getShadowOffsetX() {
		return shadowOffsetX;
	}

	@Override
	public double getShadowOffsetY() {
		return shadowOffsetY;
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public int getZIndex() {
		return this.zIndex;
	}

	@Override
	public Color getShadowColor() {
		return shadowColor;
	}

	@Override
	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}

	public boolean isAutoFill() {
		return autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}

	public boolean isHasShadow() {
		return hasShadow;
	}

	public void setHasShadow(boolean hasShadow) {
		this.hasShadow = hasShadow;
	}

	@Override
	public int compareTo(Curve o) {
		return comparableNo - o.comparableNo;
	}

	/**
	 * @return the hasLine
	 */
	public boolean hasLine() {
		return hasLine;
	}

	/**
	 * @param hasLine the hasLine to set
	 */
	public void setHasLine(boolean hasLine) {
		this.hasLine = hasLine;
	}

	/**
	 * @return the hasPoint
	 */
	public boolean hasPoint() {
		return hasPoint;
	}

	/**
	 * @param hasPoint the hasPoint to set
	 */
	public void setHasPoints(boolean hasPoint) {
		this.hasPoint = hasPoint;
	}

	public void addDiscontinuity(XYDataEntry discontinuity){
		if(dataSet.containsXYDataEntry(discontinuity)){
			discontinuities.add(discontinuity);
		}
		else if(dataSet.getEntry(discontinuity.getX(), discontinuity.getY()) != null){
			discontinuities.add(dataSet.getEntry(discontinuity.getX(), discontinuity.getY()));
		}
	}

	protected void select(DataPoint dp){
		if(dp.containsHiddenData()){
			for(DataPoint dp2 : dp.filteredPoints){
				select(dp2);
			}
		}
		else{
			select(dp.data);
		}
	}

	protected ArrayList<DataPoint> singleSelect(DataPoint dp){
		ArrayList<DataPoint> deselected = getSelectedPoints();
		selectedEntries.clear();
		if(dp.containsHiddenData()){
			select(dp.filteredPoints.get(0).data);
		}
		else{
			select(dp.data);
		}
		return deselected;
	}

	protected void deselect(DataPoint dp){
		if(dp.containsHiddenData()){
			for(DataPoint dp2 : dp.filteredPoints){
				deselect(dp2);
			}
		}
		else{
			deselect(dp.data);
		}
	}

	protected void select(XYDataEntry entry){
		if(!selectedEntries.contains(entry)){
			selectedEntries.add(entry);
		}
		//		if(dataSet.containsXYDataEntry(entry)){
		//			dataSet.fireDataEntrySelectionEvent((AbstractDataEntry) entry, true);
		//		}
	}

	protected void deselect(XYDataEntry entry){
		selectedEntries.remove(entry);
		//		if(dataSet.containsXYDataEntry(entry)){
		//			dataSet.fireDataEntrySelectionEvent((AbstractDataEntry) entry, true);
		//		}
	}

	protected boolean isPointSelected(DataPoint dataPoint){
		if(dataPoint.containsHiddenData()){
			for(DataPoint dp : dataPoint.filteredPoints){
				if(isPointSelected(dp)){
					return true;
				}
			}
		}
		else{
			for(XYDataEntry entry:selectedEntries){
				if(entry.compareTo(dataPoint.data) == 0){
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<DataPoint> getSelectedPoints(){
		ArrayList<DataPoint> selectedPoints = new ArrayList<DataPoint>();
		for(XYDataEntry e : selectedEntries){
			if(entryPointMap.containsKey(e) && !selectedPoints.contains(entryPointMap.get(e))){
				selectedPoints.add(entryPointMap.get(e));
			}
		}
		return selectedPoints;
	}

	public DataPoint getPointBeforeX(double x){
		int index = binarySearchOnDataPoints(x, dataPoints, DataPointSearchParameter.lower);
		return index != -1 ? dataPoints.get(index) : null;
	}

	public DataPoint getPointAfterX(double x){
		int index = binarySearchOnDataPoints(x, dataPoints, DataPointSearchParameter.higher);
		return index != -1 ? dataPoints.get(index) : null;
	}
	
	public DataPoint getPointClosestToX(double x){
		int index = binarySearchOnDataPoints(x, dataPoints,DataPointSearchParameter.closest);
		return index != -1 ? dataPoints.get(index) : null;
	}

	public void addUnfilterableEntry(XYDataEntry entry){
		if(dataSet.containsXYDataEntry(entry)){
			unfilterableEntries.add(entry);
		}
		else if(dataSet.getEntry(entry.getX(), entry.getY()) != null){
			discontinuities.add(dataSet.getEntry(entry.getX(), entry.getY()));
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns a list of the currently calculated (visible) {@link DataPoint}s 
	 * @param from >= xAxis.min
	 * @param to <= xAxis.max
	 * @return
	 */
	public List<DataPoint> getDataPoints(double from, double to){
		int i = binarySearchOnDataPoints(from, visibleDataPoints, DataPointSearchParameter.higher);
		int j = binarySearchOnDataPoints(to, visibleDataPoints, DataPointSearchParameter.lower);
		if(i == -1){
			i = 0;
		}
		if(j == -1){
			j = visibleDataPoints.size() - 1;
		}
		return visibleDataPoints.subList(i, j);
	}
	
	public static enum DataPointSearchParameter{
		exact,
		lower,
		higher,
		closest
	}

	public static int binarySearchOnDataPoints(double x, List<DataPoint> toSearchIn, DataPointSearchParameter dataPointSearchParameter){
		if(toSearchIn == null || toSearchIn.size() == 0){
			return -1;
		}
		int intervalLeft = 0;
		int intervalRight = toSearchIn.size() - 1;
		int intervalMid = 0;
		double midValue = Double.NaN;
		while (intervalRight >= intervalLeft){
			intervalMid = (intervalLeft + intervalRight) / 2;
			midValue = toSearchIn.get(intervalMid).getData().getX();
			if (midValue < x){
				intervalLeft = intervalMid + 1;
			}
			else if (midValue > x){
				intervalRight = intervalMid - 1;
			}
			else{ //midvalue = x;
				return intervalMid;
			}
		}
		if(dataPointSearchParameter == DataPointSearchParameter.exact){
			return -1;
		}
		else{
			int lowerIndex;
			int higherIndex;
			if(midValue > x){
				lowerIndex = intervalMid - 1;
				higherIndex = intervalMid;
			}
			else{
				lowerIndex = intervalMid;
				higherIndex = intervalMid + 1;
			}
			if(dataPointSearchParameter == DataPointSearchParameter.higher){
				return higherIndex < toSearchIn.size() ? higherIndex : - 1;
			}
			else if(dataPointSearchParameter == DataPointSearchParameter.lower){
				return lowerIndex >= 0 ? lowerIndex : -1;
			}
			else { //closest
				Double lowerValue = lowerIndex >= 0 ? toSearchIn.get(lowerIndex).getData().getX() : Double.NEGATIVE_INFINITY;
				Double higherValue = higherIndex < toSearchIn.size()  ? toSearchIn.get(higherIndex).getData().getX()  : Double.POSITIVE_INFINITY;
				return Math.abs(lowerValue - x) < Math.abs(higherValue - x) ? lowerIndex : higherIndex;
			}
		}
	}
}