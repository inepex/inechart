package com.inepex.inecharting.chartwidget.newimpl;


import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
/**
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class IneChartModul{
//	protected GraphicalObjectContainer graphicalObjectContainer;
	protected DrawingAreaImplCanvas canvas;
	
//	/**
//	 * @return the graphicalObjectContainer
//	 */
//	GraphicalObjectContainer getGraphicalObjectContainer() {
//		return graphicalObjectContainer;
//	}
//
//	/**
//	 * @param graphicalObjectContainer the graphicalObjectContainer to set
//	 */
//	void setGraphicalObjectContainer(
//			GraphicalObjectContainer graphicalObjectContainer) {
//		this.graphicalObjectContainer = graphicalObjectContainer;
//	}

	protected IneChartModul(DrawingAreaImplCanvas canvas){
		this.canvas = canvas;
	}
	
	
	
	protected abstract void update();
	protected abstract void setViewport(double startX, double stopX);
	protected abstract void moveViewport(double dX);
	
}
