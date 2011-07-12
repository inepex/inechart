package com.inepex.inechart.chartwidget;

import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.impl.client.ishapes.Rectangle;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;

/**
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public abstract class IneChartModul implements Comparable<IneChartModul> {
	private static int highestModulComparatorID = Integer.MIN_VALUE;
	private final int modulComparatorID;
	protected GraphicalObjectContainer graphicalObjectContainer;
	protected DrawingArea canvas;
	protected static final int DEFAULT_PADDING_H = 46;
	protected static final int DEFAULT_PADDING_V = 28;
	protected int topPadding = DEFAULT_PADDING_V,
			leftPadding = DEFAULT_PADDING_H,
			bottomPadding = DEFAULT_PADDING_V,
			rightPadding = DEFAULT_PADDING_H;
	
	protected static final int backgroundZIndex = Integer.MIN_VALUE + 100;
	protected static final int borderZIndex = backgroundZIndex + 1;
	

	/**
	 * The modul should not recieve (handle) events when false
	 */
	protected boolean canHandleEvents;
	/**
	 * The modul can request focus from its parent, which will be granted at the
	 * next update() call on parent {@link IneChart}, if other modul is not
	 * focused. If Focused: - other modul's canRecieveEvents field is set to
	 * false, - they will not be updated (their graphicalObjects will be
	 * freezed)
	 * 
	 * Set back to false if finished
	 */
	protected boolean requestFocus;
	/**
	 * @return the graphicalObjectContainer
	 */
	protected boolean isVisible;
	
	protected LineProperties border = null;
	protected Color backgroundColor = null;
	public static final LineProperties defaultBorder = new LineProperties(1, new Color("#000", 1.0));
	
	
	
	public GraphicalObjectContainer getGraphicalObjectContainer() {
		return graphicalObjectContainer;
	}

	protected IneChartModul(DrawingArea canvas) {
		modulComparatorID = ++highestModulComparatorID;
		graphicalObjectContainer = new GraphicalObjectContainer();
		this.canvas = canvas;
		canHandleEvents = false;
		requestFocus = false;
		isVisible = true;
		border = defaultBorder;
	}

	public void update(){
		//border
		if(border != null){
			graphicalObjectContainer.addGraphicalObject(
					new Rectangle(
							leftPadding //- border.getLineWidth()
							,topPadding //- border.getLineWidth()
							,getWidth()// + 2*border.getLineWidth()
							,getHeight()//+ 2*border.getLineWidth()
							,0, 
							borderZIndex,
							new Context(border.getLineColor().getAlpha(),
									border.getLineColor().getColor(),
									border.getLineWidth(),
									Color.DEFAULT_COLOR,
									0,0,0,
									Color.DEFAULT_COLOR),
							true, false));
		}
		if(backgroundColor != null){
			graphicalObjectContainer.addGraphicalObject(
					new Rectangle(leftPadding, topPadding, getWidth(), getHeight(),
							0, 
							backgroundZIndex,
							new Context(backgroundColor.getAlpha(),
									Color.DEFAULT_COLOR,
									0,
									backgroundColor.getColor(),
									0,0,0,
									Color.DEFAULT_COLOR),
							false, true));
		}
	}
	
	protected boolean isInsideModul(double posOnCanvas, boolean isX){
		//y
		if(!isX){
			if(posOnCanvas < topPadding  || posOnCanvas > canvas.getHeight() - bottomPadding){
				return false;
			}
			return true;
		}
		//x
		else{
			if(posOnCanvas > leftPadding && posOnCanvas < canvas.getWidth()-rightPadding){
				return true;
			}
			return false;
		}
	}

	public void setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
	}

	public abstract boolean redrawNeeded();

	/**
	 * @return the topPadding
	 */
	public int getTopPadding() {
		return topPadding;
	}

	/**
	 * @param topPadding
	 *            the topPadding to set
	 */
	public void setTopPadding(int topPadding) {
		this.topPadding = topPadding;
	}

	/**
	 * @return the bottomPadding
	 */
	public int getBottomPadding() {
		return bottomPadding;
	}

	/**
	 * @param bottomPadding
	 *            the bottomPadding to set
	 */
	public void setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
	}

	/**
	 * @return the rightPadding
	 */
	public int getRightPadding() {
		return rightPadding;
	}

	/**
	 * @param rightPadding
	 *            the rightPadding to set
	 */
	public void setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
	}

	/**
	 * @return the leftPadding
	 */
	public int getLeftPadding() {
		return leftPadding;
	}

	public int getWidth() {
		return canvas.getWidth() - leftPadding - rightPadding;
	}

	public int getHeight() {
		return canvas.getHeight() - topPadding - bottomPadding;
	}

	/**
	 * @return the canHandleEvents
	 */
	public boolean canHandleEvents() {
		return canHandleEvents;
	}

	/**
	 * @param canHandleEvents
	 *            the canHandleEvents to set
	 */
	public void setCanHandleEvents(boolean canHandleEvents) {
		this.canHandleEvents = canHandleEvents;
	}

	@Override
	public int compareTo(IneChartModul o) {
		return modulComparatorID - o.modulComparatorID;
	}

	/**
	 * @return the requestFocus
	 */
	public boolean isRequestFocus() {
		return requestFocus;
	}

	/**
	 * @param requestFocus the requestFocus to set
	 */
	public void setRequestFocus(boolean requestFocus) {
		this.requestFocus = requestFocus;
	}

	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param isVisible the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * @return the border
	 */
	public LineProperties getBorder() {
		return border;
	}

	/**
	 * @param border the border to set
	 */
	public void setBorder(LineProperties border) {
		this.border = border;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the canHandleEvents
	 */
	public boolean isCanHandleEvents() {
		return canHandleEvents;
	}

	
}
