package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;

/**
 * Base class for displaying textboxes, it does not contain Textual objects,
 * only lookout and alignment properties.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class TextContainer implements Comparable<TextContainer>{
	private static int highestComparatorID = Integer.MIN_VALUE;
	private final int comparatorID;
	
	protected int left = -1;
	protected int top = -1;
	protected VerticalPosition verticalPosition;
	protected HorizontalPosition horizontalPosition;
	protected ShapeProperties background;
	
	protected int topPadding = Defaults.textContainerPadding_V;
	protected int leftPadding =  Defaults.textContainerPadding_H;
	protected int bottomPadding =  Defaults.textContainerPadding_V;
	protected int rightPadding =  Defaults.textContainerPadding_H;

	
	public TextContainer(){
		this(VerticalPosition.Auto, HorizontalPosition.Auto, Defaults.textContainerBackground);
	}
	
	public TextContainer(VerticalPosition verticalPosition,
			HorizontalPosition horizontalPosition, ShapeProperties background) {
		this.verticalPosition = verticalPosition;
		this.horizontalPosition = horizontalPosition;
		this.background = background;
		comparatorID = ++highestComparatorID;
	}
	public VerticalPosition getVerticalPosition() {
		return verticalPosition;
	}
	public void setVerticalPosition(VerticalPosition verticalPosition) {
		this.verticalPosition = verticalPosition;
	}
	public HorizontalPosition getHorizontalPosition() {
		return horizontalPosition;
	}
	public void setHorizontalPosition(HorizontalPosition horizontalPosition) {
		this.horizontalPosition = horizontalPosition;
	}

	public ShapeProperties getBackground() {
		return background;
	}
	public void setBackground(ShapeProperties background) {
		this.background = background;
	}

	@Override
	public int compareTo(TextContainer o) {
		return this.comparatorID - o.comparatorID;
	}

	public void setPadding(int padding){
		topPadding = bottomPadding = rightPadding = leftPadding = padding;
	}

	public int getTopPadding() {
		return topPadding;
	}

	public void setTopPadding(int topPadding) {
		this.topPadding = topPadding;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
	}

	public int getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
	}

	public int getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
	}
	
	/**
	 * @return the left
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(int left) {
		this.left = left;
	}

	/**
	 * @return the top
	 */
	public int getTop() {
		return top;
	}

	/**
	 * @param top the top to set
	 */
	public void setTop(int top) {
		this.top = top;
	}

	public void copyLookoutProperties(TextContainer other){
		this.background = other.background;
		this.bottomPadding = other.bottomPadding;
		this.leftPadding = other.leftPadding;
		this.rightPadding = other.rightPadding;
		this.topPadding = other.topPadding;
	}
	
}
