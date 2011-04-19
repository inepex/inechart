package com.inepex.inecharting.chartwidget.properties;

public class ShapeDrawingInfo {

	public static ShapeDrawingInfo getDefaultShapeDrawingInfo(){
		return new ShapeDrawingInfo("#000066", 1, "#0066CC", 0.5, true);
	}
	
	protected String borderColor;
	protected int borderWidth;
	protected String fillColor;
	protected double fillOpacity;
	protected boolean hasFill;
	
	public ShapeDrawingInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public ShapeDrawingInfo(String borderColor, int borderWidth,
			String fillColor, double fillOpacity, boolean hasFill) {
		
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		this.fillColor = fillColor;
		this.fillOpacity = fillOpacity;
		this.hasFill = hasFill;
	}



	public String getborderColor() {
		return borderColor;
	}
	public int getborderWidth() {
		return borderWidth;
	}
	public String getFillColor() {
		return fillColor;
	}
	public void setborderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public void setborderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
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
