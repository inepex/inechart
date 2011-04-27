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
	protected double viewportMin=0;
	protected double viewportMax=0;
	protected boolean viewportResized = false;
	protected boolean viewportMoved  = false;
	
	/**
	 * @return the graphicalObjectContainer
	 */
	public GraphicalObjectContainer getGraphicalObjectContainer() {
		return graphicalObjectContainer;
	}


	protected IneChartModul(DrawingArea canvas){
		graphicalObjectContainer = new GraphicalObjectContainer();
		this.canvas = canvas;
	}
	
	
	
	public abstract void update();
	

	public void setViewport(double startX, double stopX) {
		if(startX != viewportMin || stopX != viewportMax)
			viewportResized = true;
		viewportMax = stopX;
		viewportMin = startX;
	}

	public void moveViewport(double dX) {
		if(dX != 0){
			viewportMoved = true;
			viewportMin += dX;
			viewportMax += dX;
		}
	}
	
	protected int getPositionRelativeToViewport(double x){
		 return (int) ((x - viewportMin) * canvas.getWidth() / (viewportMax - viewportMin));
	}
	
}
