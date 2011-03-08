package com.inepex.inecharting.chartwidget.properties;

import java.util.TreeMap;

import com.google.gwt.user.client.Random;
import com.inepex.inecharting.chartwidget.model.State;

/**
 * Information about how should the curves be drawn:
 * 	
 * 	- The name /setName(String name)/ identifies the curve,
 * so you can use it at representing a non-continuous (line)curve
 * 
 *  - Some fields are drawing tool dependent (based on graphic implementation),
 *  setting a value on a not supported property is simply ignored. 
 * 
 *  
 * @author Miklós Süveges / Inepex Ltd.
 */
public class CurveDrawingInfo {
	
	/**
	 * An imaginary point's y-value
	 * 
	 * @author  Miklós Süveges / Inepex Ltd.
	 */
	public static enum ImaginaryPointValuePolicy{
		/**
		 * the lowest data
		 */
		FIRST_POINT,
		/**
		 * the highest data
		 */
		LAST_POINT,
		/**
		 * the lowest value
		 */
		LOWER,
		/**
		 * the highest value
		 */
		HIGHER,
		/**
		 * average value of all overlapping points
		 */
		AVERAGE
	}

	public static CurveDrawingInfo getDefaultCurveDrawingInfo(){
		return new CurveDrawingInfo(generateRandomName(),
				true,
				true,
				true,
				true,
				false, 30, 
				DEFAULT_SQUARE_OVERLAPPING_SIZE, DEFAULT_OVERLAPPING_POLICY, DEFAULT_X_OVERLAPPING_WIDTH, true, 
				getHighestzIndex(),
				true, true);
	}
	
	/**
	 * Generates a random name (contains 32 0-9 digits) for this curve
	 * @return the generated name
	 */
	public static String generateRandomName(){
		String name = "";
		for(int i = 0; i < 32; i++)
			name += Random.nextInt(10);
		return name;
	}

	private static int highestzIndex = 0;
	
	public static int getHighestzIndex(){
		return highestzIndex;
	}
	
	private static void setHighestzIndex(int newValue){
		highestzIndex = newValue;
	}
	
	/**
	 * Name of the curve, helps identifying at event-handling which curve (or a curve's point) was selected
	 * also can group curves by it
	 */
	private String name;
	private boolean hasLine;
	private boolean hasPoints;
	private boolean preDrawLines;
	private boolean preDrawPoints;
	private boolean preCalculatePoints;
	private boolean keepInvisibleGraphicalObjects;
	private boolean drawPointByPoint;
	private long delayBetweenDrawingPoints;
	private int defaultzIndex;
	private TreeMap<Double, TreeMap<State, PointDrawingInfo>> customPointDrawingInfos = null;
	private TreeMap<State, ShapeDrawingInfo> lineDrawingInfo = null;
	/* model-to-pixel policy */
	public static final ImaginaryPointValuePolicy DEFAULT_OVERLAPPING_POLICY = ImaginaryPointValuePolicy.AVERAGE;
	public static final int DEFAULT_X_OVERLAPPING_WIDTH = 5;
	public static final int DEFAULT_SQUARE_OVERLAPPING_SIZE = 30;
	private int overlapFilterSquareSize ;
	private ImaginaryPointValuePolicy overlapFilterPolicy;
	private int overlapFilterXWidth;
	private boolean mathematicalRounding;
	/**
	 * default appearance of the points
	 */
	private TreeMap<State,PointDrawingInfo> defaultPointDrawingInfo = null;
		
	/**
	 * (Use it in case of GwtGraphics)
	 * Creates a {@link CurveDrawingInfo} with the given properties.
	 * @param name identifying name of this curve
	 * @param preDrawLines (GwtGraphics) lines should be drawn all along the dataset, regardless of actual viewport
	 * @param preDrawPoints (GwtGraphics) all points contained by the dataset should be drawn, regardless of actual viewport
	 * @param preCalculatePoints all points contained by the dataset should be calculated, regardless of actual viewport
	 * @param keepInvisibleGraphicalObjects (GwtGraphics) the chart should keep all drawn objects outside the viewport
	 * @param drawPointByPoint (GwtGraphics) draws the curve point-by-point (it can speed up response time when adding a curve, or changing the viewport)
	 * @param delayBetweenDrawingPoints (GwtGraphics) delay between drawing a new point in ms
	 * @param overlapFilterSquareSize two or more points overlap if they are in a square with the given size in px
	 * @param overlapFilterPolicy tells which y-value to plot
	 * @param overlapFilterXWidth two or more points overlap if their x-value(data) is in a same range in px
	 * @param mathematicalRounding when converting to the chart's coordinate system mathematical rounding should be used, or the values should be floored 
	 * @param zIndex the curves default position in the third dimension
	 * @param hasLine draw lines between points (if you set false, no lines will be drawn regardless of this curve's {@link State})
	 * @param hasPoints draw shapes on the points (if you set false, no points will be shown regardless of their {@link State}) 
	 */
	@Deprecated
	public CurveDrawingInfo(
			String name,
			boolean preDrawLines, 
			boolean preDrawPoints,
			boolean preCalculatePoints, 
			boolean keepInvisibleGraphicalObjects,
			boolean drawPointByPoint, long delayBetweenDrawingPoints,
			int overlapFilterSquareSize,
			ImaginaryPointValuePolicy overlapFilterPolicy,
			int overlapFilterXWidth, 
			boolean mathematicalRounding,
			int zIndex,
			boolean hasLine,
			boolean hasPoints) {
		this.name = name;
		this.preDrawLines = preDrawLines;
		this.preDrawPoints = preDrawPoints;
		this.keepInvisibleGraphicalObjects = keepInvisibleGraphicalObjects;
		this.drawPointByPoint = drawPointByPoint;
		this.delayBetweenDrawingPoints = delayBetweenDrawingPoints;
		this.overlapFilterSquareSize = overlapFilterSquareSize;
		this.overlapFilterPolicy = overlapFilterPolicy;
		this.overlapFilterXWidth = overlapFilterXWidth;
		this.mathematicalRounding = mathematicalRounding;
		setPreCalculatePoints(preCalculatePoints);
		customPointDrawingInfos = null;
		this.hasLine = hasLine;
		this.hasPoints = hasPoints;
		setDefaultzIndex(zIndex);
		
		this.lineDrawingInfo = new TreeMap<State, ShapeDrawingInfo>();
		defaultPointDrawingInfo = new TreeMap<State, PointDrawingInfo>();
		defaultPointDrawingInfo.put(State.ACTIVE, PointDrawingInfo.getDefaultPointDrawingInfo());
		defaultPointDrawingInfo.put(State.FOCUSED, PointDrawingInfo.getDefaultPointDrawingInfo());
		defaultPointDrawingInfo.put(State.NORMAL, PointDrawingInfo.NO_SHAPE_POINT_INFO);
		lineDrawingInfo.put(State.NORMAL, ShapeDrawingInfo.getDefaultShapeDrawingInfo());
		lineDrawingInfo.put(State.ACTIVE, ShapeDrawingInfo.getDefaultShapeDrawingInfo());
		lineDrawingInfo.put(State.FOCUSED, ShapeDrawingInfo.getDefaultShapeDrawingInfo());
	}
	
	/**
	 * (Use it in case of Canvas)
	 * Creates a {@link CurveDrawingInfo} with the given properties.
	 * @param name identifying name of this curve
	 * @param preCalculatePoints all points contained by the dataset should be calculated, regardless of actual viewport
	 * @param overlapFilterSquareSize two or more points overlap if they are in a square with the given size in px
	 * @param overlapFilterPolicy tells which y-value to plot
	 * @param overlapFilterXWidth two or more points overlap if their x-value(data) is in a same range in px
	 * @param mathematicalRounding when converting to the chart's coordinate system mathematical rounding should be used, or the values should be floored 
	 * @param zIndex the curves default position in the third dimension
	 * @param hasLine draw lines between points (if you set false, no lines will be drawn regardless of this curve's {@link State})
	 * @param hasPoints draw shapes on the points (if you set false, no points will be shown regardless of their {@link State}) 
	 */
	public CurveDrawingInfo(
			String name,
			boolean preCalculatePoints,
			int overlapFilterSquareSize,
			ImaginaryPointValuePolicy overlapFilterPolicy,
			int overlapFilterXWidth, 
			boolean mathematicalRounding,
			int zIndex,
			boolean hasLine,
			boolean hasPoints) {
		this.name = name;
		this.drawPointByPoint = this.keepInvisibleGraphicalObjects = this.preDrawPoints = this.preDrawLines = false;
		this.delayBetweenDrawingPoints = 0;
		this.overlapFilterSquareSize = overlapFilterSquareSize;
		this.overlapFilterPolicy = overlapFilterPolicy;
		this.overlapFilterXWidth = overlapFilterXWidth;
		this.mathematicalRounding = mathematicalRounding;
		this.preCalculatePoints = preCalculatePoints;
		customPointDrawingInfos = null;
		this.hasLine = hasLine;
		this.hasPoints = hasPoints;
		setDefaultzIndex(zIndex);
		
		this.lineDrawingInfo = new TreeMap<State, ShapeDrawingInfo>();
		defaultPointDrawingInfo = new TreeMap<State, PointDrawingInfo>();
		defaultPointDrawingInfo.put(State.ACTIVE, PointDrawingInfo.NO_SHAPE_POINT_INFO);
		defaultPointDrawingInfo.put(State.FOCUSED, PointDrawingInfo.NO_SHAPE_POINT_INFO);
		defaultPointDrawingInfo.put(State.NORMAL, PointDrawingInfo.NO_SHAPE_POINT_INFO);
		lineDrawingInfo.put(State.NORMAL, ShapeDrawingInfo.getDefaultShapeDrawingInfo());
		lineDrawingInfo.put(State.ACTIVE, ShapeDrawingInfo.getDefaultShapeDrawingInfo());
		lineDrawingInfo.put(State.FOCUSED, ShapeDrawingInfo.getDefaultShapeDrawingInfo());
	}

	/**
	 * Customizes the look of the curve. 
	 * @param state
	 * @param info
	 */
	public void addLineDrawingInfo(State state, ShapeDrawingInfo info) {
		if(lineDrawingInfo == null)
			lineDrawingInfo = new TreeMap<State, ShapeDrawingInfo>();
		lineDrawingInfo.put(state, info);
	}
	
	public ShapeDrawingInfo getLineDrawingInfo(State state) {
		return lineDrawingInfo.get(state);
	}	
	
	/**
	 * Customizes the look of a single point in this curve's datamap. 
	 * @param data
	 * @param state
	 * @param info
	 */
	public void addCustomPointDrawingInfo(Double data, State state, PointDrawingInfo info) {
		if(customPointDrawingInfos == null)
			customPointDrawingInfos = new TreeMap<Double, TreeMap<State,PointDrawingInfo>>();
		TreeMap<State, PointDrawingInfo> pinfo = customPointDrawingInfos.get(data);
		if(pinfo == null){
			pinfo = new TreeMap<State, PointDrawingInfo>();
		}
		pinfo.put(state, info);
		customPointDrawingInfos.put(data, pinfo);
	}
	
	/**
	 * Defines how to a draw a point in a {@link State} by default.
	 * @param state
	 * @param info
	 */
	public void addDefaultPointDrawingInfo(State state, PointDrawingInfo info){
		defaultPointDrawingInfo.put(state, info);
	}
	
	public PointDrawingInfo getDefaultPointDrawingInfo(State state){
		return defaultPointDrawingInfo.get(state);
	}
	
	/**
	 * Gets a drawing info for a point. If there is not then returns null.
	 * @param data
	 * @param state
	 * @return
	 */
	public PointDrawingInfo getPointDrawingInfo(double data, State state){
		if(customPointDrawingInfos == null || customPointDrawingInfos.get(data) == null)
			return null;
		return customPointDrawingInfos.get(data).get(state);
	}

	/**
	 * Sets a policy for overlapping X-value points.
	 * @param policy one policy from the class' static final fields 
	 * @param width the width
	 */
	public void defineOverlapXPolicy(ImaginaryPointValuePolicy policy, int overlapFilterXWidth, int overlapFilterSquareSize){
		this.overlapFilterPolicy = policy;
		this.overlapFilterXWidth = overlapFilterXWidth;
		this.overlapFilterSquareSize = overlapFilterSquareSize;
	}
	
	/**
	 * Defines the model-to-pixel function should use mathematical rounding or should use double to integer casting. 
	 * @param mathematicalRounding true if mathematical rounding should being used.
	 */
	public void setMathematicalRounding(boolean mathematicalRounding) {
		this.mathematicalRounding = mathematicalRounding;
	}
	
	public int getOverlapFilterSquareSize() {
		return overlapFilterSquareSize;
	}
	
	public ImaginaryPointValuePolicy getOverlapFilerPolicy() {
		return overlapFilterPolicy;
	}
	
	public int getOverlapFilterXWidth() {
		return overlapFilterXWidth;
	}
	
	public boolean isMathematicalRounding() {
		return mathematicalRounding;
	}

	public boolean isPreDrawLines() {
		return preDrawLines;
	}

	public boolean isPreDrawPoints() {
		return preDrawPoints;
	}

	public boolean isPreCalculatePoints() {
		return preCalculatePoints;
	}

	public boolean isKeepInvisibleGraphicalObjects() {
		return keepInvisibleGraphicalObjects;
	}

	public void setPreDrawLines(boolean preDrawLines) {
		this.preDrawLines = preDrawLines;
		if(preDrawLines)
			preCalculatePoints = true;
	}

	public void setPreDrawPoints(boolean preDrawPoints) {
		this.preDrawPoints = preDrawPoints;
		if(preDrawPoints)
			preCalculatePoints = true;
	}

	public void setPreCalculatePoints(boolean preCalculatePoints) {
		if(!preCalculatePoints && !preDrawLines && !preDrawPoints)
			this.preCalculatePoints = preCalculatePoints;
		else
			this.preCalculatePoints = true;
		
	}

	public void setKeepInvisibleGraphicalObjects(
			boolean keepInvisibleGraphicalObjects) {
		this.keepInvisibleGraphicalObjects = keepInvisibleGraphicalObjects;
	}

	public boolean isDrawPointByPoint() {
		return drawPointByPoint;
	}

	public long getDelayBetweenDrawingPoints() {
		return delayBetweenDrawingPoints;
	}

	public void setDrawPointByPoint(boolean drawPointByPoint) {
		this.drawPointByPoint = drawPointByPoint;
	}

	public void setDelayBetweenDrawingPoints(long delayBetweenDrawingPoints) {
		this.delayBetweenDrawingPoints = delayBetweenDrawingPoints;
	}
	
	public void setDefaultzIndex(int zIndex){
		if(zIndex > getHighestzIndex())
			setHighestzIndex( zIndex);
		this.defaultzIndex = zIndex;
	}
	
	public int getDefaultzIndex(){
		return defaultzIndex;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
			this.name = name;
		}
	
	public boolean hasLine() {
		return hasLine;
	}
	
	public void hasLine(boolean hasLine) {
		this.hasLine = hasLine;
	}
	
	public boolean hasPoints() {
		return hasPoints;
	}
	
	public void setHasPoints(boolean hasPoints) {
		this.hasPoints = hasPoints;
	}
	
}
