package com.inepex.inechart.chartwidget.selection;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.MouseEvent;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartEventManager;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.event.FiresViewportChangeEvent;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public abstract class RangeSelectionBase implements FiresViewportChangeEvent{

	protected ShapeProperties selectionLookOut;
	protected IneChartEventManager eventManager;
	protected IneChartModule2D modulToSelectFrom;
	protected DrawingAreaGWT canvas;
	protected List<IneChart> addressedCharts;
	protected List<IneChartModule2D> addressedModules;

	protected RangeSelectionBase(ModuleAssist moduleAssist) {
		this.eventManager = moduleAssist.getEventManager();
		this.canvas = moduleAssist.createAndAttachLayer(Layer.ALWAYS_TOP).getCanvas();
		addressedCharts = new ArrayList<IneChart>();
		addressedModules = new ArrayList<IneChartModule2D>();
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
	public IneChartModule2D getModulToSelectFrom() {
		return modulToSelectFrom;
	}

	/**
	 * @param modulToSelectFrom the modulToSelectFrom to set
	 */
	public void setModulToSelectFrom(IneChartModule2D modulToSelectFrom) {
		this.modulToSelectFrom = modulToSelectFrom;
	}

	/**
	 * @return the addressedCharts
	 */
	@Override
	public List<IneChart> getAddressedCharts() {
		return addressedCharts;
	}

	/**
	 * @param addressedCharts the addressedCharts to set
	 */
	@Override
	public void setAddressedCharts(List<IneChart> addressedCharts) {
		this.addressedCharts = addressedCharts;
	}

	/**
	 * @return the addressedModuls
	 */
	@Override
	public List<IneChartModule2D> getAddressedModules() {
		return addressedModules;
	}

	/**
	 * @param addressedModuls the addressedModuls to set
	 */
	@Override
	public void setAddressedModules(List<IneChartModule2D> addressedModuls) {
		this.addressedModules = addressedModuls;
	}

	
}
