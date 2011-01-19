package com.inepex.inecharting.chartwidget.properties;

public class ShapeDrawingInfo {

	public static ShapeDrawingInfo getDefaultShapeDrawingInfo(){
		return new ShapeDrawingInfo("blue", 1, "lightblue", 0.5, false);
	}
	
	protected String strokeColor;
	protected int strokeWidth;
	protected String fillColor;
	protected double fillOpacity;
	protected boolean hasFill;
	
	public ShapeDrawingInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public ShapeDrawingInfo(String strokeColor, int strokeWidth,
			String fillColor, double fillOpacity, boolean hasFill) {
		
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
		this.fillColor = fillColor;
		this.fillOpacity = fillOpacity;
		this.hasFill = hasFill;
	}



	public String getStrokeColor() {
		return strokeColor;
	}
	public int getStrokeWidth() {
		return strokeWidth;
	}
	public String getFillColor() {
		return fillColor;
	}
	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}
	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public double getFillOpacity() {
		return fillOpacity;
	}
	public void setFillOpacity(double d) {
		this.fillOpacity = d;
	}
	public void setHasFill(boolean hasFill) {
		this.hasFill = hasFill;
	}
	public boolean hasFill() {
		return hasFill;
	}
}
