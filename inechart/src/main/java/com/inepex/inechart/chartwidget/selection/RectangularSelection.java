package com.inepex.inechart.chartwidget.selection;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
	
	protected class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, ClickHandler{
		@Override
		public void onMouseUp(MouseUpEvent event) {
			if(mouseDownCoords == null)
				return;
			DOM.releaseCapture(eventManager.getCaptureElement());
//			eventManager.releaseFocus();
			areaSelected();
			mouseDownCoords = null;	
			dragging = false;
			if(!displayRectangleAfterSelection){
				canvas.removeAllGraphicalObject();
				canvas.update();
			}
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			if(mouseDownCoords != null){
				int[] actualCoords = normalizeCoords(getCoords(event));
				int x = 0,y = 0,width = 0,height = 0;
				if(dragging){
					width = w;
					height = h;
					int xDiff = actualCoords[0] - mouseDownCoords[0];
					int yDiff = actualCoords[1] - mouseDownCoords[1];
					x = RectangularSelection.this.x + xDiff;
					y = RectangularSelection.this.y + yDiff;
					if(x < modulToSelectFrom.getLeftPadding())
						x = modulToSelectFrom.getLeftPadding();
					if(y < modulToSelectFrom.getTopPadding())
						y = modulToSelectFrom.getTopPadding();
					if(x + width > modulToSelectFrom.getWidth() + modulToSelectFrom.getLeftPadding())
						x = modulToSelectFrom.getWidth() + modulToSelectFrom.getLeftPadding() - width;
					if(y + height > modulToSelectFrom.getHeight() + modulToSelectFrom.getTopPadding())
						y = modulToSelectFrom.getHeight() + modulToSelectFrom.getTopPadding() - height;				
					mouseDownCoords = actualCoords;
					if(!fireEventAfterDragging){
						areaSelected();
					}
				}
				else{
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
				}
				updateSelection(x, y, width, height) ;		
			}
			else{
				updateCursor(event);
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
			dragging = isDraggableSelection && isOverSelection(event);
			DOM.setCapture(eventManager.getCaptureElement());
//			eventManager.blindAllModules();
		}

		@Override
		public void onClick(ClickEvent event) {
//			canvas.removeAllGraphicalObject();
//			canvas.update();
		}
	}
		
	protected RectangularSelectionMode selectionMode;
	protected MouseHandler handler;
	protected int[] mouseDownCoords;
	protected boolean displayRectangleAfterSelection;
	protected boolean isDraggableSelection;
	protected boolean fireEventAfterDragging;
	protected boolean rectangleSelected;
	protected boolean invertSelection;
	protected int minSelectionSize;
	protected int x,y,w,h;
	protected boolean dragging;
	
	/**
	 * Creates a selection modul.
	 * Use a separate canvas for the best performance.
	 * @param canvas
	 */
	public RectangularSelection(DrawingAreaGWT canvas, IneChartEventManager eventManager) {
		super(eventManager, canvas);
		selectionMode = RectangularSelectionMode.Both;
		selectionLookOut = Defaults.selectionLookout();
		minSelectionSize = Defaults.minSelectionSize;
		modulToSelectFrom = null;
		mouseDownCoords = null;
		handler = new MouseHandler();
		eventManager.addMouseDownHandler(handler);
		eventManager.addMouseUpHandler(handler);
		eventManager.addMouseMoveHandler(handler);
		displayRectangleAfterSelection = false;
		rectangleSelected = false;
		dragging = false;
		isDraggableSelection = true;
		fireEventAfterDragging = true;
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

	protected void areaSelected(){
		if(modulToSelectFrom == null)
			return;
		double xMin = modulToSelectFrom.getValueForCanvasX(x);
		double xMax = modulToSelectFrom.getValueForCanvasX(x + w);
		double yMin = modulToSelectFrom.getValueForCanvasY(y);
		double yMax = modulToSelectFrom.getValueForCanvasY(y + h);
		ViewportChangeEvent event = null;
		switch (selectionMode) {
		case Horizontal:
			event  = new ViewportChangeEvent(xMin, xMax, true);
			break;			
		case Vertical:
			event  = new ViewportChangeEvent(yMin, yMax, false);
			break;
		case Both:
			event  = new ViewportChangeEvent(xMin, yMin, xMax, yMax);
			break;
		}
		event.setAddressedCharts(addressedCharts);
		event.setAddressedModuls(addressedModuls);
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

	protected void updateSelection(int x, int y, int width, int height){
		if(w < minSelectionSize || h < minSelectionSize){
			rectangleSelected = false;
			canvas.removeAllGraphicalObject();
			canvas.update();
		}
		if(invertSelection){
			//its easier to draw on canvas directly, and since its uses a separate canvas, we can clear it.
			canvas.getCanvasWidget().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			canvas.getCanvasWidget().setGlobalAlpha(selectionLookOut.getFillColor().getAlpha());
			canvas.getCanvasWidget().setFillStyle(selectionLookOut.getFillColor().getColor());
			canvas.getCanvasWidget().fillRect(modulToSelectFrom.getLeftPadding(), modulToSelectFrom.getTopPadding(), modulToSelectFrom.getWidth(), modulToSelectFrom.getHeight());
			canvas.getCanvasWidget().clearRect(x, y, width, height);
			canvas.getCanvasWidget().setGlobalAlpha(selectionLookOut.getLineProperties().getLineColor().getAlpha());
			canvas.getCanvasWidget().setStrokeStyle(selectionLookOut.getLineProperties().getLineColor().getColor());
			canvas.getCanvasWidget().setLineWidth(selectionLookOut.getLineProperties().getLineWidth());
			canvas.getCanvasWidget().strokeRect(x, y, width, height);
		}
		else{
			canvas.removeAllGraphicalObject();
			Rectangle r = new Rectangle(width, height, x, y, selectionLookOut);
			canvas.addAllGraphicalObject(r.toGraphicalObjects());
			canvas.update();
		}
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
		rectangleSelected = true;
	}

	protected void updateCursor(MouseEvent<?> event){
		int[] coords = getCoords(event);
		if(isPointOverModul(coords)){
			if(isDraggableSelection && rectangleSelected && isOverSelection(event)){
				DOM.setStyleAttribute(canvas.getCanvasWidget().getElement(), "cursor", "move");
			}
			else{
				DOM.setStyleAttribute(canvas.getCanvasWidget().getElement(), "cursor", "crosshair");
			}
		}	
	}
	
	protected boolean isOverSelection(MouseEvent<?> event){
		if(rectangleSelected == false)
			return false;
		int[] coords = getCoords(event);
		if(coords[0] >= x &&
			coords[0] <= x + w &&
			coords[1] >= y && 
			coords[1] <= y + h)
			return true;
		else
			return false;
	}
}
