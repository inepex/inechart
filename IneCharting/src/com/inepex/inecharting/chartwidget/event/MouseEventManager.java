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
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChart;
import com.inepex.inecharting.chartwidget.model.Curve;

public class MouseEventManager extends HandlesAllMouseEvents implements ClickHandler{

	
	private Widget chartCanvas;
	private int mouseX, mouseY;
	private boolean mouseOverChartCanvas = false;
	private IneChart parent;
	private ArrayList<Curve> curves;
	
	public MouseEventManager(Widget chartCanvas, ArrayList<Curve> curves) {
		this.chartCanvas = chartCanvas;
		chartCanvas.addDomHandler(this, MouseDownEvent.getType());
		chartCanvas.addDomHandler(this, MouseUpEvent.getType());
		chartCanvas.addDomHandler(this, MouseMoveEvent.getType());
		chartCanvas.addDomHandler(this, MouseOutEvent.getType());
		chartCanvas.addDomHandler(this, MouseOverEvent.getType());
//		chartCanvas.addDomHandler(this, MouseWheelEvent.getType());
		chartCanvas.addDomHandler(this, ClickEvent.getType());
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.preventDefault();
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
		mouseX = event.getRelativeX(chartCanvas.getElement());
		mouseY = event.getRelativeY(chartCanvas.getElement());
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		event.preventDefault();
		this.mouseOverChartCanvas = false;
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		event.preventDefault();
		this.mouseOverChartCanvas = true;
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		event.preventDefault();
		
	}

	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
	}
	
	
}
