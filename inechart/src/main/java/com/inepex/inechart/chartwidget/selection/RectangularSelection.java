package com.inepex.inechart.chartwidget.selection;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartEventManager;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;


public class RectangularSelection extends SelectionBase{

	public enum RectangularSelectionMode{
		/**
		 * The selection is free over x and y values 
		 */
		Both,
		/**
		 * Only y values may vary
		 */
		Vertical,
		/**
		 * Only x values may vary
		 */
		Horizontal
	}
	
	protected class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler{
		@Override
		public void onMouseUp(MouseUpEvent event) {
			if(mouseDownCoords != null){
				DOM.releaseCapture(eventManager.getCaptureElement());
				int[] actualCoords = normalizeCoords(getCoords(event));
				areaSelected(mouseDownCoords, actualCoords);
				mouseDownCoords = null;	
				if(!displayRectangleAfterSelection){
					canvas.removeAllGraphicalObject();
					canvas.update();
				}
			}
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			if(mouseDownCoords != null){
				int[] actualCoords = normalizeCoords(getCoords(event));
				double x = 0,y = 0,width = 0,height = 0;
				switch (selectionMode) {
				case Both:
					width = Math.abs(actualCoords[0] - mouseDownCoords[0]);
					height = Math.abs(actualCoords[1] - mouseDownCoords[1]);
					x = Math.min(actualCoords[0], mouseDownCoords[0]);
					y = Math.min(actualCoords[1], mouseDownCoords[1]);
					break;
				case Horizontal:
					width = Math.abs(actualCoords[0] - mouseDownCoords[0]);
					x = Math.min(actualCoords[0], mouseDownCoords[0]);
					y = modulToSelectFrom.getTopPadding();
					height = modulToSelectFrom.getHeight();
					break;
				case Vertical:
					height = Math.abs(actualCoords[1] - mouseDownCoords[1]);
					y = Math.min(actualCoords[1], mouseDownCoords[1]);
					x = modulToSelectFrom.getLeftPadding();
					width = modulToSelectFrom.getWidth();
					break;
				}
				Rectangle r = new Rectangle(width, height, x, y, selectionLookOut);
				//do update
				canvas.removeAllGraphicalObject();
				canvas.addAllGraphicalObject(r.toGraphicalObjects());
				canvas.update();
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
			
			DOM.setCapture(eventManager.getCaptureElement());
		}
	}
		
	protected RectangularSelectionMode selectionMode;
	protected MouseHandler handler;
	protected int[] mouseDownCoords;
	protected boolean displayRectangleAfterSelection;
	
	/**
	 * Creates a selection modul.
	 * Use a separate canvas for the best performance.
	 * @param canvas
	 */
	public RectangularSelection(DrawingAreaGWT canvas, IneChartEventManager eventManager) {
		super(eventManager, canvas);
		selectionMode = RectangularSelectionMode.Both;
		selectionLookOut = Defaults.selectionLookout;
		modulToSelectFrom = null;
		mouseDownCoords = null;
		handler = new MouseHandler();
		eventManager.addMouseDownHandler(handler);
		eventManager.addMouseUpHandler(handler);
		eventManager.addMouseMoveHandler(handler);
		displayRectangleAfterSelection = false;
	}
	
	
	/**
	 * @return the selectionMode
	 */
	public RectangularSelectionMode getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode the selectionMode to set
	 */
	public void setSelectionMode(RectangularSelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	protected void areaSelected(int[] from, int[] to){
		if(modulToSelectFrom == null)
			return;
		double[] modelFrom = modulToSelectFrom.getValuePair(from[0], from[1]);
		double[] modelTo = modulToSelectFrom.getValuePair(to[0], to[1]);
		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		switch (selectionMode) {
		case Both:
			xMin = Math.min(modelFrom[0], modelTo[0]);
			xMax = Math.max(modelFrom[0], modelTo[0]);
			yMin = Math.min(modelFrom[1], modelTo[1]);
			yMax = Math.max(modelFrom[1], modelTo[1]);
			break;
		case Horizontal:
			xMin = Math.min(modelFrom[0], modelTo[0]);
			xMax = Math.max(modelFrom[0], modelTo[0]);
			yMin = modulToSelectFrom.getYAxis().getMin();
			yMax = modulToSelectFrom.getYAxis().getMax();
			break;			
		case Vertical:
			yMin = Math.min(modelFrom[1], modelTo[1]);
			yMax = Math.max(modelFrom[1], modelTo[1]);
			xMin = modulToSelectFrom.getXAxis().getMin();
			xMax = modulToSelectFrom.getXAxis().getMax();
			break;
		}
		ViewportChangeEvent event = new ViewportChangeEvent(null, xMin, yMin, xMax, yMax, null);
		event.setAddressedCharts(addressedCharts);
		event.setAddressedModuls(addressedModuls);;
		eventManager.fireViewportChangedEvent(event);
	}


	/**
	 * @return the displayRectangleAfterSelection
	 */
	public boolean isDisplayRectangleAfterSelection() {
		return displayRectangleAfterSelection;
	}


	/**
	 * @param displayRectangleAfterSelection the displayRectangleAfterSelection to set
	 */
	public void setDisplayRectangleAfterSelection(
			boolean displayRectangleAfterSelection) {
		this.displayRectangleAfterSelection = displayRectangleAfterSelection;
	}
}
