package com.inepex.inechart.chartwidget;

import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;

/**
 * 
 * An {@link IneChartModul} updates its model and (re)draw its graphics on {@link #update()} call.
 * The graphics should be stored in graphicalObjectContainer.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public abstract class IneChartModul implements Comparable<IneChartModul> {
	private static int highestModulComparatorID = Integer.MIN_VALUE;
	private final int modulComparatorID;
	protected GraphicalObjectContainer graphicalObjectContainer;
	protected DrawingArea canvas;
		

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
	}

	/**
	 * If a subclass is to be changed after creation, it should override this method.
	 * If overridden, the subclass should update its {@link #graphicalObjectContainer} during this method,
	 * so the container {@link IneChart} can display the up-to-date graphics.
	 * Do not forget to call super.update()!
	 */
	public abstract void update();
	
	/**
	 * Tells the chart if the modul needs an {@link #update()} call
	 * @return
	 */
	public abstract boolean redrawNeeded();
	
	
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

}
