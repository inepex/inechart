package com.inepex.inechart.chartwidget.properties;

public class ShapeProperties {

	public static ShapeProperties defaultShapeProperties() {
		return new ShapeProperties(LineProperties.getDefaultSolidLine(),
				new Color("#FFFFFF", 0.8));
	}

	private LineProperties lineProperties;
	private Color fillColor;

	/**
	 * A shape without line (border), filled with default Color (black)
	 */
	public ShapeProperties(){
		this(new Color());
	}
	/**
	 * 
	 * @param lineProperties
	 */
	public ShapeProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
		this.fillColor = null;
	}

	public ShapeProperties(LineProperties lineProperties, Color fillColor) {
		this.lineProperties = lineProperties;
		this.fillColor = fillColor;
	}

	/**
	 * A shape with fillColor and without line (border)
	 * @param fillColor
	 */
	public ShapeProperties(Color fillColor){
		this(null, fillColor) ;
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

	/**
	 * @return the fillColor or null if the shape has no fill
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * @param fillColor
	 *            the fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

}
