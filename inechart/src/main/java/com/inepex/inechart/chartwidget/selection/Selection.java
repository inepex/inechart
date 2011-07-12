package com.inepex.inechart.chartwidget.selection;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.shared.DrawingArea;


//TODO Selection should not be a modul (it is only used on client side, and it does not fit)
public class Selection extends IneChartModul implements MouseDownHandler, MouseMoveHandler, MouseUpHandler{

	public enum SelectionMode{
		Rectangle
	}
	
	protected SelectionMode selectionMode;
	protected ShapeProperties selectionLookOut;
	private final ShapeProperties defaultSelectionLookout = new ShapeProperties(new LineProperties(1.2, new Color("#FFFF66", 0.84)), new Color("#FFFF66", 0.66));
	
	protected IneChartModul2D modulToSelectFrom;
	

	private int[] mouseDownCoords;
	
	/**
	 * Creates a selection modul.
	 * Use a separate canvas for the best performance.
	 * @param canvas
	 */
	public Selection(DrawingArea canvas) {
		super(canvas);
		selectionMode = SelectionMode.Rectangle;
		selectionLookOut = defaultSelectionLookout;
		modulToSelectFrom = null;
		mouseDownCoords = null;
		backgroundColor = null;
		border = null;
		
		((DrawingAreaGWT)canvas).getWidget().addDomHandler(this, MouseDownEvent.getType());
		((DrawingAreaGWT)canvas).getWidget().addDomHandler(this, MouseUpEvent.getType());
		((DrawingAreaGWT)canvas).getWidget().addDomHandler(this, MouseMoveEvent.getType());
	}

	@Override
	public boolean redrawNeeded() {
		return false;
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mouseDownCoords = null;
		
		
		requestFocus = false;
		DOM.releaseCapture(((DrawingAreaGWT)canvas).getWidget().getElement());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(mouseDownCoords != null){
			if(selectionMode.equals(SelectionMode.Rectangle)){
				int[] actualCoords = normalizeCoords(getCoords(event));
				Rectangle r = new Rectangle(
						Math.abs(actualCoords[0] - mouseDownCoords[0]),
						Math.abs(actualCoords[1] - mouseDownCoords[1]),
						Math.min(actualCoords[0], mouseDownCoords[0]),
						Math.min(actualCoords[1], mouseDownCoords[1]),
						selectionLookOut);
				//do update
				canvas.removeAllGraphicalObject();
				canvas.addAllGraphicalObject(r.toGraphicalObjects());
				canvas.update();
			}
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if(modulToSelectFrom == null)
			return;
		mouseDownCoords = getCoords(event);
		if(!isPointOverModul(mouseDownCoords)){
			mouseDownCoords = null;
			return;
		}
		canvas.removeAllGraphicalObject();
		requestFocus = true;
		DOM.setCapture(((DrawingAreaGWT)canvas).getWidget().getElement());
	}
	
	private int[] normalizeCoords(int[] coords){
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
	
	private boolean isPointOverModul(int[] coords){
		if(coords[0] >= modulToSelectFrom.getLeftPadding() &&
			coords[0] <= modulToSelectFrom.getLeftPadding() + modulToSelectFrom.getWidth() &&
			coords[1] >= modulToSelectFrom.getTopPadding() && 
			coords[1] <= modulToSelectFrom.getTopPadding() + modulToSelectFrom.getHeight())
			return true;
		else
			return false;
	}
	
	private int[] getCoords(MouseEvent<?> e){
		return new int[]{e.getRelativeX(((DrawingAreaGWT) canvas).getWidget().getElement()),
				e.getRelativeY(((DrawingAreaGWT) canvas).getWidget().getElement())}	;
	}
	
	@Override
	public void update() {
	}

	/**
	 * @return the selectionMode
	 */
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode the selectionMode to set
	 */
	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	/**
	 * @return the selectionLookOut
	 */
	public ShapeProperties getSelectionLookOut() {
		return selectionLookOut;
	}

	/**
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
