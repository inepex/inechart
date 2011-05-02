package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Text extends GraphicalObject implements Comparable<Text>{
	//comparing helper fields
	private static int highestComparatorID = Integer.MIN_VALUE;
	private int comparatorID;
	//TODO font style, size, etc
	protected String text;
	protected String fontFamily;
	protected int fontSize;
	protected String fontStyle;
	protected String fontWeight;
	protected int width = 0;
	protected int height = 0;
	protected BasePointXPosition basePointXPosition;
	protected BasePointYPosition basePointYPosition;
	protected int[] padding = new int[]{0,0,0,0};
	/**
	 * 
	 * Vertical Positioning
	 */
	public enum BasePointYPosition{
		TOP,
		MIDDLE,
		BOTTOM
	}
	/**
	 * 
	 * Horizontal Positioning
	 */
	public enum BasePointXPosition{
		LEFT,
		MIDDLE,
		RIGHT
	}
	//font color is stored in context color
	protected final static Context DEFAULT_FONT_CONTEXT = new Context(1, "#ffffff", 1, "#ffffff", 0, 0, 0,  "#ffffff");
	protected final static String DEFAULT_FONT_FAMILY = "Calibri, Verdana, Arial, sans-serif";
	protected final static int DEFAULT_FONT_SIZE = 11;
	protected final static String DEFAULT_FONT_STYLE = "normal";
	protected final static String DEFAULT_FONT_WEIGHT = "normal";
	
	public Text(String text,int basePointX, int basePointY){
		this(basePointX, basePointY, text, DEFAULT_FONT_FAMILY, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE, DEFAULT_FONT_WEIGHT, BasePointXPosition.LEFT, BasePointYPosition.TOP);
	}
	
	
	public Text(String text,int basePointX, int basePointY, BasePointXPosition basePointXPosition,	BasePointYPosition basePointYPosition){
		this(basePointX, basePointY, text, DEFAULT_FONT_FAMILY, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE, DEFAULT_FONT_WEIGHT, basePointXPosition, basePointYPosition);
	}

	/**
	 * @param basePointX
	 * @param basePointY
	 * @param text
	 * @param fontFamily CSS
	 * @param fontSize CSS
	 * @param fontStyle CSS
	 * @param fontWeight CSS
	 * @param basePointXPosition {@link BasePointXPosition}
	 * @param basePointYPosition {@link BasePointYPosition}
	 */
	public Text(int basePointX, int basePointY, String text,
			String fontFamily,int fontSize, String fontStyle, String fontWeight, 
			BasePointXPosition basePointXPosition,	BasePointYPosition basePointYPosition) {
		super(basePointX, basePointY, 0, DEFAULT_FONT_CONTEXT, false, false);
		this.comparatorID = highestComparatorID++;
		this.text = text;
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontWeight = fontWeight;
		this.basePointXPosition = basePointXPosition;
		this.basePointYPosition = basePointYPosition;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	
	@Override
	public int compareTo(Text other) {
		return comparatorID - other.comparatorID;
	}


	/**
	 * @return the fontFamily
	 */
	public String getFontFamily() {
		return fontFamily;
	}


	/**
	 * @param fontFamily the fontFamily to set
	 */
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}


	/**
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}


	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}


	/**
	 * @return the fontStyle
	 */
	public String getFontStyle() {
		return fontStyle;
	}


	/**
	 * @param fontStyle the fontStyle to set
	 */
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}


	/**
	 * @return the fontWeight
	 */
	public String getFontWeight() {
		return fontWeight;
	}


	/**
	 * @param fontWeight the fontWeight to set
	 */
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}


	/**
	 * The height of the displayed text.
	 * @return the width 0 if it has not been displayed yet
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * Do NOT use this method.
	 * The text positioner uses it to properly position the text.
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}


	/**
	 * The height of the displayed text.
	 * @return the height 0 if it has not been displayed yet
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * Do NOT use this method.
	 * The text positioner modul uses it to properly position the text.
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}


	/**
	 * @return the basePointXPosition
	 */
	public BasePointXPosition getBasePointXPosition() {
		return basePointXPosition;
	}


	/**
	 * @param basePointXPosition the {@link BasePointXPosition} to set
	 */
	public void setBasePointXPosition(BasePointXPosition basePointXPosition) {
		this.basePointXPosition = basePointXPosition;
	}


	/**
	 * @return the basePointYPosition
	 */
	public BasePointYPosition getBasePointYPosition() {
		return basePointYPosition;
	}


	/**
	 * @param basePointYPosition the {@link BasePointYPosition} to set
	 */
	public void setBasePointYPosition(BasePointYPosition basePointYPosition) {
		this.basePointYPosition = basePointYPosition;
	}
	
	
	public String getColor(){
		return context.getFillColor();
	}
	
	
	public void setColor(String color){
		context.setFillColor(color);
		context.setStrokeColor(color);
		context.setShadowColor(color);
	}
	
	public void setPadding(int top, int right, int bottom, int left){
		this.padding[0] = top;
		this.padding[1] = right;
		this.padding[2] = bottom;
		this.padding[3] = left;
	}
	
	public void setTopPadding(int top){
		this.padding[0] = top;
	}
	
	public void setRightPadding(int right){
		this.padding[1] = right;
	}
	
	public void setBottomPadding(int bottom){
		this.padding[2] = bottom;
	}
	
	public void setLeftPadding(int left){
		this.padding[3] = left;
	}
	
	public int getTopPadding(){
		return this.padding[0];
	}
	
	public int getRightPadding(){
		return this.padding[1];
	}
	
	public int getBottomPadding(){
		return this.padding[2] ;
	}
	
	public int getLeftPadding(){
		return this.padding[3];
	}

}
