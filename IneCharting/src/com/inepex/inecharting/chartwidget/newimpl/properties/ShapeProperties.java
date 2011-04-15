package com.inepex.inecharting.chartwidget.newimpl.properties;

public class ShapeProperties {

	private LineProperties lineProperties;
	private Color fillColor;
	
	
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
	 * @return the lineProperties
	 */
	public LineProperties getLineProperties() {
		return lineProperties;
	}
	/**
	 * @param lineProperties the lineProperties to set
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
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	
	
}
