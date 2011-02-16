package com.inepex.inecharting.chartwidget.properties;

import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.model.State;


public class CurveDrawingInfo {
	public static enum ImaginaryPointValuePolicy{
		FIRST_POINT,
		LAST_POINT,
		LOWER,
		HIGHER,
		AVERAGE
	}
	public static CurveDrawingInfo getDefaultCurveDrawingPolicy(){
		return new CurveDrawingInfo(true, true, true, true, false, 30, DEFAULT_SQUARE_OVERLAPPING_SIZE, DEFAULT_OVERLAPPING_POLICY, DEFAULT_X_OVERLAPPING_WIDTH, true, getHighestzIndex());
	}
	
	private static int highestzIndex = 0;
	
	public static int getHighestzIndex(){
		return highestzIndex;
	}
	private static void setHighestzIndex(int newValue){
		highestzIndex = newValue;
	}
	
	
	private boolean preDrawLines;
	private boolean preDrawPoints;
	private boolean preCalculatePoints;
	private boolean keepInvisibleGraphicalObjects;
	private boolean drawPointByPoint;
	private long delayBetweenDrawingPoints;
	private int defaultzIndex;
	private TreeMap<Double, TreeMap<State, PointDrawingInfo>> customPointDrawingInfos = null;
	

	/* model-to-pixel policy */
	public static final ImaginaryPointValuePolicy DEFAULT_OVERLAPPING_POLICY = ImaginaryPointValuePolicy.AVERAGE;
	public static final int DEFAULT_X_OVERLAPPING_WIDTH = 3;
	public static final int DEFAULT_SQUARE_OVERLAPPING_SIZE = 10;

	private int overlapFilterSquareSize ;
	private ImaginaryPointValuePolicy overlapFilterPolicy;
	private int overlapFilterXWidth;
	private boolean mathematicalRounding;
	
	
	public CurveDrawingInfo(
			boolean preDrawLines, 
			boolean preDrawPoints,
			boolean preCalculatePoints, 
			boolean keepInvisibleGraphicalObjects,
			boolean drawPointByPoint, long delayBetweenDrawingPoints,
			int overlapFilterSquareSize,
			ImaginaryPointValuePolicy overlapFilterPolicy,
			int overlapFilterXWidth, 
			boolean mathematicalRounding,
			int zIndex) {
		this.preDrawLines = preDrawLines;
		this.preDrawPoints = preDrawPoints;
		this.keepInvisibleGraphicalObjects = keepInvisibleGraphicalObjects;
		this.drawPointByPoint = drawPointByPoint;
		this.delayBetweenDrawingPoints = delayBetweenDrawingPoints;
		this.overlapFilterSquareSize = overlapFilterSquareSize;
		this.overlapFilterPolicy = overlapFilterPolicy;
		this.overlapFilterXWidth = overlapFilterXWidth;
		this.mathematicalRounding = mathematicalRounding;
		setPreCalculatePoints(true);
		customPointDrawingInfos = null;
	}
	
	/**
	 * Customizes the look of a single point in datamap. 
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
}
