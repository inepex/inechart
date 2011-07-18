package com.inepex.inechart.chartwidget.selection;

import com.google.gwt.event.dom.client.MouseEvent;
import com.inepex.inechart.chartwidget.IneChartEventManager;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public abstract class SelectionBase {

	protected ShapeProperties selectionLookOut;
	protected IneChartEventManager eventManager;
	protected IneChartModul2D modulToSelectFrom;
	protected DrawingAreaGWT canvas;

	protected SelectionBase(IneChartEventManager eventManager,
			DrawingAreaGWT canvas) {
		this.eventManager = eventManager;
		this.canvas = canvas;
	}
	
	protected int[] normalizeCoords(int[] coords){
		if(modulToSelectFrom == null)
			return coords;
		int[] ret = new int[]{coords[0], coords[1]};
		if(coords[0] < modulToSelectFrom.getLeftPadding())
			ret[0] = modulToSelectFrom.getLeftPadding();
		if(coords[0] > modulToSelectFrom.getLeftPadding() + modulToSelectFrom.getWidth())
			ret[0] = modulToSelectFrom.getLeftPadding() + modulToSelectFrom.getWidth();
		if(coords[1] < modulToSelectFrom.getTopPadding())
			ret[1] = modulToSelectFrom.getTopPadding();
		if(coords[1] > modulToSelectFrom.getTopPadding() + modulToSelectFrom.getHeight())
			ret[1] = modulToSelectFrom.getTopPadding() + modulToSelectFrom.getHeight();
		return ret;			
	}
	
	protected boolean isPointOverModul(int[] coords){
		if(modulToSelectFrom == null)
			return false;
		if(coords[0] >= modulToSelectFrom.getLeftPadding() &&
			coords[0] <= modulToSelectFrom.getLeftPadding() + modulToSelectFrom.getWidth() &&
			coords[1] >= modulToSelectFrom.getTopPadding() && 
			coords[1] <= modulToSelectFrom.getTopPadding() + modulToSelectFrom.getHeight())
			return true;
		else
			return false;
	}
	
	protected int[] getCoords(MouseEvent<?> e){
		return new int[]{e.getRelativeX(((DrawingAreaGWT) canvas).getWidget().getElement()),
				e.getRelativeY(((DrawingAreaGWT) canvas).getWidget().getElement())}	;
	}
	
	/**
	 * @return the selectionLookOut
	 */
	public ShapeProperties getSelectionLookOut() {
		return selectionLookOut;
	}

	/**
	 * The lookout properties of the selection's shape.
	 * @param selectionLookOut the selectionLookOut to set
	 */
	public void setSelectionLookOut(ShapeProperties selectionLookOut) {
		this.selectionLookOut = selectionLookOut;
	}

	/**
	 * @return the modulToSelectFrom
	 */
	public IneChartModul2D getModulToSelectFrom() {
		return modulToSelectFrom;
	}

	/**
	 * @param modulToSelectFrom the modulToSelectFrom to set
	 */
	public void setModulToSelectFrom(IneChartModul2D modulToSelectFrom) {
		this.modulToSelectFrom = modulToSelectFrom;
	}

}
