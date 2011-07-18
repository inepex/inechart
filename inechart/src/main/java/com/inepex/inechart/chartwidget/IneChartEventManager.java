package com.inepex.inechart.chartwidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
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
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.inepex.inechart.chartwidget.event.IneChartEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeHandler;

public class IneChartEventManager implements HasAllMouseHandlers, ViewportChangeHandler,
	MouseDownHandler, MouseOutHandler, MouseMoveHandler, MouseOverHandler, MouseUpHandler, ClickHandler {
	protected EventBus eventBus = null;
	protected HandlerManager handlerManager;
	protected IneChart parent;
	
	public IneChartEventManager(IneChart parent) {
		handlerManager = new HandlerManager(parent);
	}


	public EventBus getEventBus() {
		return eventBus;
	}

	
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.addHandler(ViewportChangeEvent.TYPE, this);
	}
	
	
	@Override
	public void fireEvent(GwtEvent<?> event){
		eventBus.fireEventFromSource(event, parent);
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
	public void onMove(ViewportChangeEvent event, double dx, double dy) {
		if(event.getSourceChart() != null && event.getSourceChart().equals(parent))
			return;
		if(event.getAddressedCharts() == null || event.getAddressedCharts().contains(parent)){
			handlerManager.fireEvent(event);
		}
	}


	@Override
	public void onSet(ViewportChangeEvent event, double xMin, double yMin,
			double xMax, double yMax) {
		if(event.getSourceChart() != null && event.getSourceChart().equals(parent))
			return;
		if(event.getAddressedCharts() == null || event.getAddressedCharts().contains(parent)){
			handlerManager.fireEvent(event);
		}
	}


	@Override
	public void onMouseUp(MouseUpEvent event) {
		handlerManager.fireEvent(event);
	}


	@Override
	public void onMouseOver(MouseOverEvent event) {
		handlerManager.fireEvent(event);
	}


	@Override
	public void onMouseMove(MouseMoveEvent event) {
		handlerManager.fireEvent(event);
	}


	@Override
	public void onMouseOut(MouseOutEvent event) {
		handlerManager.fireEvent(event);
	}


	@Override
	public void onMouseDown(MouseDownEvent event) {
		handlerManager.fireEvent(event);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		handlerManager.fireEvent(event);
	}

	public void fireViewportChangedEvent(ViewportChangeEvent event){
		handlerManager.fireEvent(event);
		setSourceChart(event);
		fireEvent(event);
	}
	
	private void setSourceChart(IneChartEvent<?> event){
		event.setSourceChart(parent);
	}


	
	
}