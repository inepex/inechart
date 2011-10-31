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

	protected boolean redrawNeeded;
	

	/**
	 * The modul should not recieve (handle) events when false
	 */
	protected boolean canHandleEvents;

	/**
	 * @return the graphicalObjectContainer
	 */
	protected boolean isVisible;
		
	protected ModuleAssist moduleAssist;

	protected IneChartModule(ModuleAssist moduleAssist) {
		this.moduleAssist = moduleAssist;
		modulComparatorID = ++highestModulComparatorID;
		graphicalObjectContainer = new GraphicalObjectContainer();
		this.canvas = moduleAssist.getMainCanvas();
		canHandleEvents = true;
		isVisible = true;
	}


	public abstract void update();
	
	public GraphicalObjectContainer getGraphicalObjectContainer() {
		return graphicalObjectContainer;
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

//	/**
//	 * @return the redrawNeeded
//	 */
//	public boolean isRedrawNeeded() {
//		return redrawNeeded;
//	}
}
