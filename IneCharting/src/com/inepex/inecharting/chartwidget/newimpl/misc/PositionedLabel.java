package com.inepex.inecharting.chartwidget.newimpl.misc;

public class PositionedLabel implements Comparable<PositionedLabel>{

	String text;
	double posX, posY;
	boolean absolutePosition;
	
	
	public PositionedLabel(String text, double posX, double posY,
			boolean absolutePosition) {
		if(text != null)
			this.text = text;
		else
			this.text = "";
		this.posX = posX;
		this.posY = posY;
		this.absolutePosition = absolutePosition;
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

	/**
	 * @return the posX
	 */
	public double getPosX() {
		return posX;
	}

	/**
	 * @param posX the posX to set
	 */
	public void setPosX(double posX) {
		this.posX = posX;
	}

	/**
	 * @return the posY
	 */
	public double getPosY() {
		return posY;
	}

	/**
	 * @param posY the posY to set
	 */
	public void setPosY(double posY) {
		this.posY = posY;
	}

	/**
	 * @return the absolutePosition
	 */
	public boolean isAbsolutePosition() {
		return absolutePosition;
	}

	/**
	 * @param absolutePosition the absolutePosition to set
	 */
	public void setAbsolutePosition(boolean absolutePosition) {
		this.absolutePosition = absolutePosition;
	}

	@Override
	public int compareTo(PositionedLabel o) {
		if(posX > o.posX)
			return 1;
		else if(posX < o.posX)
			return -1;
		else if(posY > o.posY)
			return 1;
		else if(posY < o.posY)
			return -1;
		else if(text.length() > o.text.length())
			return 1;
		else if(text.length() < o.text.length())
			return -1;
		else
			return 0;
	}
	
	
}
