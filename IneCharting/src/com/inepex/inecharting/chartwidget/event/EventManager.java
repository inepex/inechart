package com.inepex.inecharting.chartwidget.event;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChart;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.Mark;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;
import com.inepex.inecharting.chartwidget.model.State;


/**
 * 
 * This class is responsible for handling both in and outgoing and also for private (inner) events.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class EventManager extends HandlesAllMouseEvents implements ClickHandler{
	public static EventManager instance = null;
	
	public static EventManager get(){
		return instance;
	}
	public static EventManager create(IneChartProperties prop, IneChart chartInstance, DrawingFactory  df, ArrayList<Curve> curves){
		instance = new EventManager(prop, chartInstance,  df, curves);
		return instance;
	}
	
	private IneChartProperties prop;
	private HandlerManager hm;
	private boolean readyForEvents;
	private Widget chartCanvas;
	private DrawingFactory df;
	private int mouseX, mouseY;
	private boolean mouseOverChartCanvas = false;
	private IneChart parent;
	private ArrayList<Curve> curves;
	private ArrayList<Point> selectedPoints;
	private Mark selectedMark;
	private Mark selectedImaginaryMark;
	private Mark focusedMark;
	private ArrayList<Point> focusedPoints;
	
	private EventManager(IneChartProperties prop, IneChart chartInstance, DrawingFactory df, ArrayList<Curve> curves) {
		this.prop = prop;
		hm = new HandlerManager(chartInstance);
		this.chartCanvas = df.getChartCanvas();
		selectedPoints = new ArrayList<Point>();
		focusedPoints = new ArrayList<Point>();
		this.curves = curves;
		this.df = df;
		chartCanvas.addDomHandler(this, MouseDownEvent.getType());
		chartCanvas.addDomHandler(this, MouseUpEvent.getType());
		chartCanvas.addDomHandler(this, MouseMoveEvent.getType());
		chartCanvas.addDomHandler(this, MouseOutEvent.getType());
		chartCanvas.addDomHandler(this, MouseOverEvent.getType());
		chartCanvas.addDomHandler(this, MouseWheelEvent.getType());
		chartCanvas.addDomHandler(this, ClickEvent.getType());
		addExtremesChangeHandler(ModelManager.get().getMarkContainer());
	}

	private void updateSelection(){
		ArrayList<Point> newSelection = new ArrayList<Point>();
		Mark newlySelectedMark = null;
		boolean redrawNeeded = false;
		if(mouseOverChartCanvas){
			//check selected points
			for(Curve curve : curves){
				if(curve.getCurveDrawingInfo().hasPoints()){
					int diff = Integer.MAX_VALUE;
					Point closest = null;
					for(int i = 0; i < curve.getVisiblePoints().size(); i++){
						Point act = curve.getVisiblePoints().get(i);
						if(act.getxPos() < ModelManager.get().getViewportMinInPx()){
							continue;
						}
						else if(act.getxPos() > ModelManager.get().getViewportMaxInPx()){
							break;
						}
						else if(Math.abs(ModelManager.get().getViewportMinInPx() + mouseX - act.getxPos()) < diff){
							diff = Math.abs(ModelManager.get().getViewportMinInPx() + mouseX - act.getxPos());
							closest = act;
						}
						else{
							break;
						}
					}
					if(closest != null)
						newSelection.add(closest);
				}
			}
			//check selected mark
			for(Mark mark : df.getMarkBoundingBoxes().keySet()){
				if(isMouseInBoundingBox(df.getMarkBoundingBoxes().get(mark))){
					newlySelectedMark = mark;
				}
				else if(newlySelectedMark != null){
					break;
				}
			}
		}
		/* Marks */
		//we only need to change smg when the new mark differs
		if(selectedMark != newlySelectedMark){
			
			redrawNeeded = true;
			//setting previously selected mark(s)'s state back to normal
			if(selectedMark != null){
				selectedMark.setState(State.NORMAL);
			}
			if(newlySelectedMark != null){
				newlySelectedMark.setState(State.ACTIVE);
				if(newlySelectedMark.isImaginaryMark()){
					//set the previously selected back to normal
					if(selectedImaginaryMark != null && !newlySelectedMark.equals(selectedImaginaryMark)){
						selectedImaginaryMark.setState(State.NORMAL);
					}
					selectedImaginaryMark = newlySelectedMark;
					// 'change' the imaginary to the first real mark
					selectedMark = ModelManager.get().getMarkContainer().getMarksForImaginaryMark(selectedImaginaryMark).get(0);
					selectedMark.setState(State.ACTIVE);
				}	
				else{
					selectedMark = newlySelectedMark;
					if(selectedImaginaryMark != null && !ModelManager.get().getMarkContainer().getMarksForImaginaryMark(selectedImaginaryMark).contains(newlySelectedMark))
						selectedImaginaryMark.setState(State.NORMAL);
				}
			}
			else{
				if(selectedImaginaryMark != null)
					selectedImaginaryMark.setState(State.NORMAL);
				selectedMark = selectedImaginaryMark = null;
			}
		}
	
		
			

		/* Points */
		ArrayList<Point> toUpdate = new ArrayList<Point>();
		for(Point point:newSelection){
//			State prevState = point.getState();
			point.setState(State.ACTIVE);
			toUpdate.add(point);
//			hm.fireEvent(new StateChangeEvent(point, prevState));
			redrawNeeded = true;
		}
		//previously selected points
		for(Point point:selectedPoints){
			if(!newSelection.contains(point)){
//				State prevState = point.getState();
				point.setState(State.NORMAL);
				toUpdate.add(point);
//				hm.fireEvent(new StateChangeEvent(point, prevState));
				redrawNeeded = true;
			}
		}
		selectedPoints =  newSelection;
		if(redrawNeeded)
			df.setViewport(ModelManager.get().getViewportMin(), ModelManager.get().getViewportMax());
	}
	
	private void updateFocus() {
		
	}
		
	public void fireEvent(GwtEvent<?> event){
		hm.fireEvent(event);
	}
	
	/* handler registration methods */
	public HandlerRegistration addExtremesChangeHandler(ExtremesChangeHandler handler) {
		return hm.addHandler(ExtremesChangeEvent.TYPE, handler);
	}

	public HandlerRegistration addStateChangeHandler(StateChangeHandler handler) {
		return hm.addHandler(StateChangeEvent.TYPE, handler);
	}
	
	public HandlerRegistration addViewportChangeHandler(ViewportChangeHandler handler) {
		return hm.addHandler(ViewportChangeEvent.TYPE, handler);
	}
	
	/**
	 * If you set true the manager will skip mouse events.
	 * @param readyForEvents
	 */
	public void setReadyForEvents(boolean readyForEvents) {
		this.readyForEvents = readyForEvents;
	}
	
	/* MouseEvent methods */
	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		mouseX = event.getRelativeX(chartCanvas.getElement());
		mouseY = event.getRelativeY(chartCanvas.getElement());
		if(readyForEvents)
			updateSelection();
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		this.mouseOverChartCanvas = false;
		updateSelection();
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		this.mouseOverChartCanvas = true;
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		event.preventDefault();
	}

	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		if(readyForEvents)
			updateFocus();
	}	


	/**
	 * Checks whether the actual mouse position is in the given area (a rectangle)
	 * @param boundingBox as: [index] = box' parameter -> [0] = x, [1] = y, [2] = width, [3] = height
	 * @return true if it is inside
	 */
	private boolean isMouseInBoundingBox(int[] boundingBox){
		if(mouseX < boundingBox[0] ||
				mouseX > boundingBox[0] + boundingBox[2] ||
				mouseY < boundingBox[1] || 
				mouseY > boundingBox[1] + boundingBox[3])
			return false;
		else return true;
	}
	
	/**
	 * Checks whether the actual mouse position is in the given area (a circle)
	 * @param boundingBox as: [index] = circle's parameter -> [0] = x, [1] = y, [2] = radius
	 * @return true if it is inside
	 */
//	private boolean isMouseInBoundingCircle(int[] boundingCircle){
//		double distance = 
//		if()
//	}

}
