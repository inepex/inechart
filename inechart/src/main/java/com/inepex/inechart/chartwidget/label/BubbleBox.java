package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.Position;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;

/**
 * 
 * A label with an optional tail.
 * The position of the tail determines the whole label's position regardless on tail's appearance
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class BubbleBox extends StyledLabel {

	/**
	 * the horizontal position of the tail relative to the label
	 */
	protected HorizontalPosition tailHorizontalPosition;
	/**
	 * the vertical position of the tail relative to the label
	 */
	protected VerticalPosition tailVerticalPosition;
	/**
	 * the tail's position relative to the label
	 */
	protected Position tailPosition;
	/**
	 * the size of rotated rectangle's side representing the tail
	 */
	protected int tailSize;
	/**
	 * the distance between the tail and the point
	 */
	protected int distanceFromPoint;
	private boolean displayTail;
	private boolean autoFit;
	
	public BubbleBox(BubbleBox copy){
		this(copy.getText());
		tailHorizontalPosition = copy.tailHorizontalPosition;
		tailPosition = copy.tailPosition;
		tailSize = copy.tailSize;
		tailVerticalPosition = copy.tailVerticalPosition;
		setDisplayTail(copy.isDisplayTail());
		distanceFromPoint = copy.distanceFromPoint;
		setAutoFit(copy.isAutoFit());
	}
	
	public BubbleBox(String text){
		this(new Text(text, Defaults.bb_TextProperties()));
	}
	
	public BubbleBox(Text text) {
		super(text);
		tailHorizontalPosition = Defaults.tailHorizontalPosition;
		tailVerticalPosition = Defaults.tailVerticalPosition;
		tailPosition = Defaults.tailPosition;
		tailSize = Defaults.tailSize;
		distanceFromPoint = Defaults.distanceFromPoint;
		background = Defaults.bb_BackGround();
		roundedCornerRadius = Defaults.bb_RoundedCornerRadius;
		setDisplayTail(true);
		setAutoFit(true);
	}

	public HorizontalPosition getTailHorizontalPosition() {
		return tailHorizontalPosition;
	}

	public void setTailHorizontalPosition(HorizontalPosition tailHorizontalPosition) {
		this.tailHorizontalPosition = tailHorizontalPosition;
	}

	public VerticalPosition getTailVerticalPosition() {
		return tailVerticalPosition;
	}

	public void setTailVerticalPosition(VerticalPosition tailVerticalPosition) {
		this.tailVerticalPosition = tailVerticalPosition;
	}

	public Position getTailPosition() {
		return tailPosition;
	}

	public void setTailPosition(Position tailPosition) {
		this.tailPosition = tailPosition;
	}

	public int getTailSize() {
		return tailSize;
	}

	public void setTailSize(int tailSize) {
		this.tailSize = tailSize;
	}

	public int getDistanceFromPoint() {
		return distanceFromPoint;
	}

	public void setDistanceFromPoint(int distanceFromPoint) {
		this.distanceFromPoint = distanceFromPoint;
	}

	public boolean isDisplayTail() {
		return displayTail;
	}

	public void setDisplayTail(boolean displayTail) {
		this.displayTail = displayTail;
	}

	public boolean isAutoFit() {
		return autoFit;
	}

	public void setAutoFit(boolean autoFit) {
		this.autoFit = autoFit;
	}
	
}
