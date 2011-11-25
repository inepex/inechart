package com.inepex.inechart.chartwidget.interactivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.ModuleAssist;

public abstract class AbstractInteractiveModule {
	
	protected class InnerEventHandler implements MouseMoveHandler, ClickHandler, MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler{

		@Override
		public void onMouseOver(MouseOverEvent event) {
			AbstractInteractiveModule.this.onMouseOver(event);	
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
			AbstractInteractiveModule.this.onMouseOut(event);
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			AbstractInteractiveModule.this.onMouseUp(event);
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			AbstractInteractiveModule.this.onMouseDown(event);
		}

		@Override
		public void onClick(ClickEvent event) {
			AbstractInteractiveModule.this.onClick(event);
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			AbstractInteractiveModule.this.onMouseMove(event);
		}
		
	}
	
	protected ModuleAssist moduleAssist;
	protected InnerEventHandler innerEventHandler;
	protected IneChartModule2D relatedIneChartModule2D;
	
	public void attach(ModuleAssist moduleAssist, IneChartModule2D relatedIneChartModule2D){
		this.moduleAssist = moduleAssist;
		this.relatedIneChartModule2D = relatedIneChartModule2D;
		init();
	}
	
	public void detach(){
		//TODO handlerRemove
	}
	
	protected void init(){
		innerEventHandler = new InnerEventHandler();
		if(moduleAssist.getEventManager() != null){
			moduleAssist.getEventManager().addMouseDownHandler(innerEventHandler);
			moduleAssist.getEventManager().addMouseMoveHandler(innerEventHandler);
			moduleAssist.getEventManager().addMouseOutHandler(innerEventHandler);
			moduleAssist.getEventManager().addMouseOverHandler(innerEventHandler);
			moduleAssist.getEventManager().addMouseUpHandler(innerEventHandler);
			moduleAssist.getEventManager().addClickHandler(innerEventHandler);
		}
	}
	
	
	protected abstract void onClick(ClickEvent event);

	protected abstract void onMouseUp(MouseUpEvent event);
	
	protected abstract void onMouseOver(MouseOverEvent event);

	protected abstract void onMouseDown(MouseDownEvent event);

	protected abstract void onMouseOut(MouseOutEvent event);
	
	protected abstract void onMouseMove(MouseMoveEvent event);
	
	public abstract void preUpdate();
	
	public abstract void update();

	protected Layer createLayer(int zIndex){
//		layer = new Layer(Layer.ALWAYS_TOP);
//		moduleAssist.addCanvasToLayer(layer);
		Layer layer = moduleAssist.createAndAttachLayer(Layer.ALWAYS_TOP);
		relatedIneChartModule2D.getModuleLayer().addLayer(layer);
		moduleAssist.updateLayerOrder();
		return layer;
	}

}
