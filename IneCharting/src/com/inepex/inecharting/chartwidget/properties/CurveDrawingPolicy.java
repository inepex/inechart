package com.inepex.inecharting.chartwidget.properties;


public class CurveDrawingPolicy {
	public static enum ImaginaryPointValuePolicy{
		FIRST_POINT,
		LAST_POINT,
		LOWER,
		HIGHER,
		AVERAGE
	}
	public static CurveDrawingPolicy getDefaultCurveDrawingPolicy(){
		return new CurveDrawingPolicy(false, false, true, false, true, 290, DEFAULT_SQUARE_OVERLAPPING_SIZE, DEFAULT_OVERLAPPING_POLICY, DEFAULT_X_OVERLAPPING_WIDTH, true);
	}
	
	private boolean preDrawLines;
	private boolean preDrawPoints;
	private boolean preCalculatePoints;
	private boolean keepInvisibleGraphicalObjects;
	private boolean drawPointByPoint;
	private long delayBetweenDrawingPoints; 

	/* model-to-pixel policy */
	public static final ImaginaryPointValuePolicy DEFAULT_OVERLAPPING_POLICY = ImaginaryPointValuePolicy.AVERAGE;
	public static final int DEFAULT_X_OVERLAPPING_WIDTH = 3;
	public static final int DEFAULT_SQUARE_OVERLAPPING_SIZE = 10;

	private int overlapFilterSquareSize ;
	private ImaginaryPointValuePolicy overlapFilterPolicy;
	private int overlapFilterXWidth;
	private boolean mathematicalRounding;
	
	
	public CurveDrawingPolicy(boolean preDrawLines, boolean preDrawPoints,
			boolean preCalculatePoints, boolean keepInvisibleGraphicalObjects,
			boolean drawPointByPoint, long delayBetweenDrawingPoints,
			int overlapFilterSquareSize,
			ImaginaryPointValuePolicy overlapFilterPolicy,
			int overlapFilterXWidth, boolean mathematicalRounding) {
			this.preDrawLines = preDrawLines;
		this.preDrawPoints = preDrawPoints;
		this.preCalculatePoints = preCalculatePoints;
		this.keepInvisibleGraphicalObjects = keepInvisibleGraphicalObjects;
		this.drawPointByPoint = drawPointByPoint;
		this.delayBetweenDrawingPoints = delayBetweenDrawingPoints;
		this.overlapFilterSquareSize = overlapFilterSquareSize;
		this.overlapFilterPolicy = overlapFilterPolicy;
		this.overlapFilterXWidth = overlapFilterXWidth;
		this.mathematicalRounding = mathematicalRounding;
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
	}

	public void setPreDrawPoints(boolean preDrawPoints) {
		this.preDrawPoints = preDrawPoints;
	}

	public void setPreCalculatePoints(boolean preCalculatePoints) {
		this.preCalculatePoints = preCalculatePoints;
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
	
}
