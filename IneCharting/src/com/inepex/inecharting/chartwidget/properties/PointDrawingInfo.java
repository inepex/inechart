package com.inepex.inecharting.chartwidget.properties;

public class PointDrawingInfo extends ShapeDrawingInfo {
	public static enum PointType{
		ELLIPSE,
		RECTANGLE,
		NO_SHAPE
	}
	
	public static final int RECTANGLE_HEIGHT_TO_X_AXIS = -1;
	public static final int RECTANGLE_WIDTH_TO_NEXT_POINT = -1;
	
	public static PointDrawingInfo getDefaultPointDrawingInfo(){
		return new PointDrawingInfo("black", 2, "red", 0.5, true, PointType.ELLIPSE, 8, 8);
	}
	
	
	private PointType type;
	private int width;
	private int height ;
	
	public PointDrawingInfo() {
		
	}
	
	public PointDrawingInfo(String strokeColor, int strokeWidth,
			String fillColor, double fillOpacity, boolean hasFill,
			PointType type, int width, int height) {
		super(strokeColor, strokeWidth, fillColor, fillOpacity, hasFill);
		this.type = type;
		this.width = width;
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public PointType getType() {
		return type;
	}
	public void setType(PointType type) {
		this.type = type;
	}
	
}
