package com.inepex.inecharting.chartwidget.model;

import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.properties.MarkInfo;
import com.inepex.inecharting.chartwidget.properties.ShapeDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.TextBoxDrawingInfo;

public class Mark extends GraphicalObject implements Comparable<Mark>{

	private int xPosition;
	private double xValue;
	private TreeMap<State, String> textPerState;
	private MarkInfo info;
	private boolean isImaginaryMark = false;;
	
	
	public Mark(double xValue) {
		this.xValue = xValue;
		info = MarkInfo.getDefaultMarkInfo();
		this.state = State.NORMAL;
		this.zIndex = 0;
	}
	public Mark(double xValue, MarkInfo info){
		this.xValue = xValue;
		this.info = info;
		this.state = State.NORMAL;
		this.zIndex = 0;
	}
	public Mark(double xValue, MarkInfo info, String text){
		this.xValue = xValue;
		this.info = info;
		setText(State.NORMAL, text);
		this.state = State.NORMAL;
		this.zIndex = 0;
	}
	public Mark(double xValue, MarkInfo info, String normalText, String activeText, String focusedText){
		this.xValue = xValue;
		this.info = info;
		textPerState = new TreeMap<State, String>();
		textPerState.put(State.ACTIVE, normalText);
		textPerState.put(State.NORMAL, activeText);
		textPerState.put(State.FOCUSED, focusedText);
		this.state = State.NORMAL;
		this.zIndex = 0;
	}
	

	
	public void setText(State state, String text){
		if(this.textPerState == null){
			textPerState = new TreeMap<State, String>();
			textPerState.put(State.ACTIVE, text);
			textPerState.put(State.NORMAL, text);
			textPerState.put(State.FOCUSED, text);
		}
		else{
			textPerState.put(state, text);
		}
	}
	
	public String getText(){
		return getText(state);
	}
	
	public TextBoxDrawingInfo getTextBoxDrawingInfo(){
		return info.getTextBoxDrawingInfo(state);
	}

	public String getText(State state){
		if(state == null)
			return "";
		if(textPerState == null || !textPerState.containsKey(state)){
			return "";
		}
		else
			return textPerState.get(state);
	}
	/**
	 * @return the xPosition
	 */
	public int getxPosition() {
		return xPosition;
	}
	/**
	 * @param xPosition the xPosition to set
	 */
	void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}
	/**
	 * @return the xValue
	 */
	public double getxValue() {
		return xValue;
	}
	
	public MarkInfo getInfo() {
		return info;
	}
	
	public void setInfo(MarkInfo info) {
		this.info = info;
	}
	@Override
	public int compareTo(Mark o) {
		if(xValue < o.xValue)
			return -1;
		else if(xValue > o.xValue)
			return 1;
		return 0;
	}
	void setImaginaryMark(boolean isImaginaryMark) {
		this.isImaginaryMark = isImaginaryMark;
	}
	public boolean isImaginaryMark() {
		return isImaginaryMark;
	}
	
}
