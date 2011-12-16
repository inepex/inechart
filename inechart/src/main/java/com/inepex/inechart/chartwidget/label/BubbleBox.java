package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.Position;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;

public class BubbleBox extends StyledLabel {

	protected HorizontalPosition tailHorizontalPosition;
	protected VerticalPosition tailVerticalPosition;
	protected Position tailPosition;
	protected int tailSize;
	protected int distanceFromPoint;
	protected boolean displayTail;
	
	public BubbleBox(BubbleBox copy){
		this(copy.getText());
		tailHorizontalPosition = copy.tailHorizontalPosition;
		tailPosition = copy.tailPosition;
		tailSize = copy.tailSize;
		tailVerticalPosition = copy.tailVerticalPosition;
		displayTail = copy.displayTail;
		distanceFromPoint = copy.distanceFromPoint;
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
		displayTail = true;
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
	
}
