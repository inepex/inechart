package com.inepex.inegraphics.shared;

import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

/**
 * 
 * Holds information about how should the {@link GraphicalObject} be drawn. 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class Context {
	 
	public static Context getDefaultContext(){
		return new Context(1d, "black", 1, "white", 0d, 0d, 0.8, "grey");
	}
	
	protected double alpha;
	protected String strokeColor;
	protected double strokeWidth;
	protected String fillColor;
	protected double shadowOffsetX;
	protected double shadowOffsetY;
	protected double shadowAlpha;
	protected String shadowColor;
//	protected double scaleX, scaleY;
	
	
	
	
	public Context(double alpha, String strokeColor, double strokeWidth,
			String fillColor, double shadowOffsetX, double shadowOffsetY,
			double shadowAlpha, String shadowColor) {
		
		this.alpha = alpha;
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
		this.fillColor = fillColor;
		this.shadowOffsetX = shadowOffsetX;
		this.shadowOffsetY = shadowOffsetY;
		this.shadowAlpha = shadowAlpha;
		this.shadowColor = shadowColor;
	}

	/**
	 * Creates a context without defining shadows
	 * @param alpha
	 * @param strokeColor
	 * @param strokeWidth
	 * @param fillColor
	 */
	public Context(double alpha, String strokeColor, double strokeWidth, String fillColor) {
		this(alpha, strokeColor, strokeWidth, fillColor, 0, 0, 0, strokeColor);
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}
	/**
	 * @return the strokeColor
	 */
	public String getStrokeColor() {
		return strokeColor;
	}
	/**
	 * @return the strokeWidth
	 */
	public double getStrokeWidth() {
		return strokeWidth;
	}
	/**
	 * @return the fillColor
	 */
	public String getFillColor() {
		return fillColor;
	}
	/**
	 * @return the shadowOffsetX
	 */
	public double getShadowOffsetX() {
		return shadowOffsetX;
	}
	/**
	 * @return the shadowOffsetY
	 */
	public double getShadowOffsetY() {
		return shadowOffsetY;
	}
	/**
	 * @return the shadowAlpha
	 */
	public double getShadowAlpha() {
		return shadowAlpha;
	}
	/**
	 * @return the shadowColor
	 */
	public String getShadowColor() {
		return shadowColor;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @param strokeColor the strokeColor to set
	 */
	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	/**
	 * @param strokeWidth the strokeWidth to set
	 */
	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * @param shadowOffsetX the shadowOffsetX to set
	 */
	public void setShadowOffsetX(double shadowOffsetX) {
		this.shadowOffsetX = shadowOffsetX;
	}

	/**
	 * @param shadowOffsetY the shadowOffsetY to set
	 */
	public void setShadowOffsetY(double shadowOffsetY) {
		this.shadowOffsetY = shadowOffsetY;
	}

	/**
	 * @param shadowAlpha the shadowAlpha to set
	 */
	public void setShadowAlpha(double shadowAlpha) {
		this.shadowAlpha = shadowAlpha;
	}

	/**
	 * @param shadowColor the shadowColor to set
	 */
	public void setShadowColor(String shadowColor) {
		this.shadowColor = shadowColor;
	}
	
}
