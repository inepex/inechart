package com.inepex.inecharting.chartwidget.newimpl;



import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
/**
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class IneChartModul{
	protected GraphicalObjectContainer graphicalObjectContainer;
	protected DrawingArea canvas;
	protected boolean redrawNeeded;
	
	/**
	 * @return the graphicalObjectContainer
	 */
	public GraphicalObjectContainer getGraphicalObjectContainer() {
		return graphicalObjectContainer;
	}

	/**
	 * @param graphicalObjectContainer the graphicalObjectContainer to set
	 */
	void setGraphicalObjectContainer(
			GraphicalObjectContainer graphicalObjectContainer) {
		this.graphicalObjectContainer = graphicalObjectContainer;
	}

	protected IneChartModul(DrawingArea canvas){
		graphicalObjectContainer = new GraphicalObjectContainer();
		this.canvas = canvas;
	}
	
	
	
	public abstract void update();
	public abstract void setViewport(double startX, double stopX);
	public abstract void moveViewport(double dX);

	
}
