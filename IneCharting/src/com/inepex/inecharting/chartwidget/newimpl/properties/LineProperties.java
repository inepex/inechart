package com.inepex.inecharting.chartwidget.newimpl.properties;

public class LineProperties {
	/**
	 * Drawing style of the line 
	 */
	public enum LineStyle{
		/**
		 * simple line
		 */
		SOLID,
		/**
		 * dashed line, may not be supported
		 * in all case of use
		 */
		DASHED
	}
	
	public static LineProperties getDefaultSolidLine(){
		return new LineProperties(1, new Color("#ffffff"));
	}

	public static LineProperties getDefaultDashedLine(){
		return new LineProperties(1, new Color("#ffffff"),DEFAULT_DASH_STROKE_LENGTH,DEFAULT_DASH_DISTANCE);
	}

	public static final double DEFAULT_DASH_STROKE_LENGTH = 3.5;
	public static final double DEFAULT_DASH_DISTANCE = 2.2;
	private int lineWidth;
	private Color  lineColor;
	private LineStyle style;
	private double dashStrokeLength, dashDistance;
	
	public LineProperties(int lineWidth, Color lineColor) {
		this.lineWidth = lineWidth;
		this.lineColor = lineColor;
		this.dashStrokeLength = 0;
		this.dashDistance = 0;
		this.style = LineStyle.SOLID;
	}

	public LineProperties(int lineWidth, Color lineColor,
			double dashStrokeLength, double dashDistance) {
		this.lineWidth = lineWidth;
		this.lineColor = lineColor;
		this.dashStrokeLength = dashStrokeLength;
		this.dashDistance = dashDistance;
		this.style = LineStyle.DASHED;
	}

	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param lineWidth the lineWidth to set
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the lineColor
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * @param lineColor the lineColor to set
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * @return the style
	 */
	public LineStyle getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(LineStyle style) {
		this.style = style;
	}

	/**
	 * @return the dashStrokeLength
	 */
	public double getDashStrokeLength() {
		return dashStrokeLength;
	}

	/**
	 * @param dashStrokeLength the dashStrokeLength to set
	 */
	public void setDashStrokeLength(double dashStrokeLength) {
		this.dashStrokeLength = dashStrokeLength;
	}

	/**
	 * @return the dashDistance
	 */
	public double getDashDistance() {
		return dashDistance;
	}

	/**
	 * @param dashDistance the dashDistance to set
	 */
	public void setDashDistance(double dashDistance) {
		this.dashDistance = dashDistance;
	}
	
	
	
}
