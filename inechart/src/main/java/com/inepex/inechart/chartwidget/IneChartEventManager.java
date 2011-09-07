package com.inepex.inechart.chartwidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.inepex.inechart.chartwidget.event.IneChartEvent;
import com.inepex.inechart.chartwidget.event.PointSelectionEvent;
import com.inepex.inechart.chartwidget.event.PointSelectionHandler;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeHandler;

public class IneChartEventManager implements HasAllMouseHandlers, ViewportChangeHandler, PointSelectionHandler,
	MouseDownHandler, MouseOutHandler, MouseMoveHandler, MouseOverHandler, MouseUpHandler, ClickHandler, HasClickHandlers {
	protected EventBus eventBus = null;
	protected HandlerManager handlerManager;
	protected IneChart parent;
	protected ViewportChangeEventStack viewportChangeEventStack;
	
	public IneChartEventManager(IneChart parent) {
		this.parent = parent;
		handlerManager = new HandlerManager(parent);
		viewportChangeEventStack = new ViewportChangeEventStack(this);
	}


	public EventBus getEventBus() {
		return eventBus;
	}

	
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.addHandler(ViewportChangeEvent.TYPE, this);
		eventBus.addHandler(PointSelectionEvent.TYPE, this);
	}
	
	
	@Override
	public void fireEvent(GwtEvent<?> event){
		if(eventBus != null)
			eventBus.fireEvent(event);
	}

	
	protected void fireInnerEvent(GwtEvent<?> event){
		handlerManager.fireEvent(event);
//		if(parent.isRedrawNeeded()){
////			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
////				
////				@Override
////				public void execute() {
//			if(!parent.isUpdatingInProgress())
//				parent.update();
////				}
////			});
//			
//		}
	}
	
	
	protected void eventFinished(){
		parent.update();
	}
	

	public void addViewportChangeHandler(ViewportChangeHandler handler){
		handlerManager.addHandler(ViewportChangeEvent.TYPE, handler);
	}

	
	public void addPointSelectionHandler(PointSelectionHandler handler){
		handlerManager.addHandler(PointSelectionEvent.TYPE, handler);
	}
	
	
	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return handlerManager.addHandler(MouseDownEvent.getType(), handler);
	}


	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return handlerManager.addHandler(MouseUpEvent.getType(), handler);
	}


	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return handlerManager.addHandler(MouseOutEvent.getType(), handler);
	}


	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return handlerManager.addHandler(MouseOverEvent.getType(), handler);
	}


	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return handlerManager.addHandler(MouseMoveEvent.getType(), handler);
	}


	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return handlerManager.addHandler(MouseWheelEvent.getType(), handler);
	}

	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return handlerManager.addHandler(ClickEvent.getType(), handler);
	}

	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		fireInnerEvent(event);
	}


	@Override
	public void onMouseOver(MouseOverEvent event) {
		fireInnerEvent(event);
	}


	@Override
	public void onMouseMove(MouseMoveEvent event) {
		fireInnerEvent(event);
	}


	@Override
	public void onMouseOut(MouseOutEvent event) {
		fireInnerEvent(event);
	}


	@Override
	public void onMouseDown(MouseDownEvent event) {
		fireInnerEvent(event);
	}
	
	
	@Override
	public void onClick(ClickEvent event) {
		fireInnerEvent(event);
	}

	
	public void fireViewportChangedEvent(ViewportChangeEvent event){
		setSourceChart(event);
		viewportChangeEventStack.pushEvent(event);
		fireEvent(event);
	}
	
	
	private void setSourceChart(IneChartEvent<?> event){
		event.setSourceChart(parent);
	}

	
	public Element getCaptureElement(){
		return parent.getElement();
	}


	@Override
	public void onMoveAlongX(ViewportChangeEvent event, double dx) {
		checkAddressAndFireIfRelevant(event);
	}


	@Override
	public void onMoveAlongY(ViewportChangeEvent event, double dy) {
		checkAddressAndFireIfRelevant(event);
	}


	@Override
	public void onSetX(ViewportChangeEvent event, double xMin, double xMax) {
		checkAddressAndFireIfRelevant(event);
	}


	@Override
	public void onSetY(ViewportChangeEvent event, double yMin, double yMax) {
		checkAddressAndFireIfRelevant(event);
	}
	
	
	@Override
	public void onMove(ViewportChangeEvent event, double dx, double dy) {
		checkAddressAndFireIfRelevant(event);
	}


	@Override
	public void onSet(ViewportChangeEvent event, double xMin, double yMin,
			double xMax, double yMax) {
		checkAddressAndFireIfRelevant(event);
	}
	
	
	private void checkAddressAndFireIfRelevant(IneChartEvent<?> event){
		if(event.getSourceChart() != null && event.getSourceChart().equals(parent))
			return;
		if(event.getAddressedCharts() == null || event.getAddressedCharts().contains(parent)){
			if(event instanceof ViewportChangeEvent){
				viewportChangeEventStack.pushEvent((ViewportChangeEvent) event);
			}
			else{
				fireInnerEvent(event);
			}
		}
	}


	
	@Override
	public void onSelect(PointSelectionEvent event) {
		fireInnerEvent(event);
		
	}


	@Override
	public void onDeselect(PointSelectionEvent event) {
		fireInnerEvent(event);
		
	}
	
	public void blindAllModules(){
		parent.setCanHandleEventsForAllModule(false);
	}
	
	public void releaseFocus(){
		parent.setCanHandleEventsForAllModule(true);
	}
}
