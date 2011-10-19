package com.inepex.inechart.chartwidget;

import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;

/**
 * 
 * An {@link IneChartModule} updates its model and (re)draw its graphics on {@link #update()} call.
 * The graphics should be stored in graphicalObjectContainer.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public abstract class IneChartModule implements Comparable<IneChartModule> {
	private static int highestModulComparatorID = Integer.MIN_VALUE;
	private final int modulComparatorID;
	protected GraphicalObjectContainer graphicalObjectContainer;
	protected DrawingArea canvas;

	/**
	 * Tells whether the module has been changed since its last update call.
	 * Typically set true after a module successfully handled a change event.
	 */
	protected boolean redrawNeeded;
	

	/**
	 * The modul should not recieve (handle) events when false
	 */
	protected boolean canHandleEvents;

	/**
	 * @return the graphicalObjectContainer
	 */
	protected boolean isVisible;
		
	public GraphicalObjectContainer getGraphicalObjectContainer() {
		return graphicalObjectContainer;
	}

	protected IneChartModule(DrawingArea canvas) {
		modulComparatorID = ++highestModulComparatorID;
		graphicalObjectContainer = new GraphicalObjectContainer();
		this.canvas = canvas;
		canHandleEvents = true;
		isVisible = true;
	}

	/**
	 * If a subclass is to be changed after creation, it should override this method.
	 * If overridden, the subclass should update its {@link #graphicalObjectContainer} during this method,
	 * so the container {@link IneChart} can display the up-to-date graphics.
	 * Do not forget to call super.update()!
	 */
	public void update(){
		redrawNeeded = false;
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
	public int compareTo(IneChartModule o) {
		return modulComparatorID - o.modulComparatorID;
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
	 * @return the redrawNeeded
	 */
	public boolean isRedrawNeeded() {
		return redrawNeeded;
	}
}
