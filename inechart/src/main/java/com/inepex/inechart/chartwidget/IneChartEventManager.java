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
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Element;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionHandler;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.event.DataSetChangeHandler;
import com.inepex.inechart.chartwidget.event.IneChartEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeHandler;

public class IneChartEventManager implements HasAllMouseHandlers, ViewportChangeHandler,
MouseDownHandler, MouseOutHandler, MouseMoveHandler, MouseOverHandler, MouseUpHandler, ClickHandler, HasClickHandlers, HasHandlers {
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
	}

	/**
	 * fires the given event via {@link IneChart}'s eventBus (outgoing events)
	 */
	public void fireEvent(GwtEvent<?> event){
		if(eventBus != null)
			eventBus.fireEvent(event);
	}

	/**
	 * fires the given event only inside this chart 
	 * @param event
	 */
	protected void fireInnerEvent(GwtEvent<?> event){
		handlerManager.fireEvent(event);
	}


	protected void eventFinished(){
		parent.update();
	}
	
	protected boolean isRelatedEvent(IneChartEvent<?> event){
		if(event.getAddressedCharts() != null && !event.getAddressedCharts().contains(parent)){
			return false;
		}
		return true;
	}
	
	protected boolean isRelatedVPEvent(ViewportChangeEvent event){
		boolean related = isRelatedEvent(event);
		if(event.getAddressedModules() != null){
			for(IneChartModule m : event.getAddressedModules()){
				if(parent.containsModule(m)){
					return true;
				}
			}
			return false;
		}
		return related;
	}

	public void addViewportChangeHandler(ViewportChangeHandler handler){
		handlerManager.addHandler(ViewportChangeEvent.TYPE, handler);
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


	public HandlerRegistration addDataEntrySelectionHandler(DataEntrySelectionHandler handler){
		return handlerManager.addHandler(DataEntrySelectionEvent.TYPE, handler);
	}
	
	
	public HandlerRegistration addDataSetChangeHandler(DataSetChangeHandler handler){
		return handlerManager.addHandler(DataSetChangeEvent.TYPE, handler);
	}

	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.preventDefault();
		fireInnerEvent(event);
	}


	@Override
	public void onMouseOver(MouseOverEvent event) {
		event.preventDefault();
		fireInnerEvent(event);
	}


	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
		fireInnerEvent(event);
	}


	@Override
	public void onMouseOut(MouseOutEvent event) {
		event.preventDefault();
		fireInnerEvent(event);
	}


	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		fireInnerEvent(event);
	}


	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		fireInnerEvent(event);
	}


	public void fireViewportChangedEvent(ViewportChangeEvent event){
		setSourceChart(event);
		if(isRelatedVPEvent(event)){
			viewportChangeEventStack.pushEvent(event);
		}
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


	public void blindAllModules(){
		parent.setCanHandleEventsForAllModule(false);
	}
}
