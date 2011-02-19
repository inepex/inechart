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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChart;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.DrawingFactory;
import com.inepex.inecharting.chartwidget.model.Curve;
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
	private ArrayList<Point> focusedPoints;
	
	private EventManager(IneChartProperties prop, IneChart chartInstance, DrawingFactory df, ArrayList<Curve> curves) {
		this.prop = prop;
		hm = new HandlerManager(chartInstance);
		this.chartCanvas = df.getChartCanvas();
		selectedPoints = new ArrayList<Point>();
		focusedPoints = new ArrayList<Point>();
		this.curves = curves;
		chartCanvas.addDomHandler(this, MouseDownEvent.getType());
		chartCanvas.addDomHandler(this, MouseUpEvent.getType());
		chartCanvas.addDomHandler(this, MouseMoveEvent.getType());
		chartCanvas.addDomHandler(this, MouseOutEvent.getType());
		chartCanvas.addDomHandler(this, MouseOverEvent.getType());
//		chartCanvas.addDomHandler(this, MouseWheelEvent.getType());
		chartCanvas.addDomHandler(this, ClickEvent.getType());
	}

	private void updateSelection(){
		ArrayList<Point> newSelection = new ArrayList<Point>();
		if(mouseOverChartCanvas){
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
		}
		ArrayList<Point> toUpdate = new ArrayList<Point>();
		for(Point point:newSelection){
			point.setState(State.ACTIVE);
			toUpdate.add(point);
		}
		for(Point point:selectedPoints)
			if(!newSelection.contains(point)){
				point.setState(State.NORMAL);
				toUpdate.add(point);
			}
		DrawingFactory.get().drawPoints(toUpdate);
		selectedPoints =  newSelection;
	}
	
	private void updateFocus() {
		
	}
	
	
	
	
	
	
	
	public HandlerRegistration addExtremesChangeHandler(ExtremesChangeHandler handler) {
		return hm.addHandler(ExtremesChangeEvent.TYPE, handler);
	}

	public HandlerRegistration addStateChangeHandler(StateChangeHandler handler) {
		return hm.addHandler(StateChangeEvent.TYPE, handler);
	}
	
	public HandlerRegistration addViewportChangeHandler(ViewportChangeHandler handler) {
		return hm.addHandler(ViewportChangeEvent.TYPE, handler);
	}
	
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
		
		
	}

	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		if(readyForEvents)
			updateFocus();
	}	

}
