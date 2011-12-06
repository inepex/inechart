package com.inepex.inechart.misc.scroll.defaultviews;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.chartwidget.resources.ScrollStyle;
import com.inepex.inechart.misc.scroll.ScrollBarPresenter.View;

/**
 * 
 * Default {@link View} implementation.
 * Without increase and decrease buttons.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class ScrollBarView extends Composite implements View {
	protected final static int DEFAULT_SIZE = 16;
	protected final static int DEFAULT_PADDING = 2;
	protected boolean isHorizontal;
	protected AbsolutePanel panel;
	protected Widget slider;
	protected int width, height, padding;
	boolean hasBorder;
	protected boolean sliderBeingDragged = false;
	protected boolean enabled = true;
	protected ScrollStyle scrollStyle;
	
	protected ArrayList<HandlerRegistration> handlerRegistrations;
	
	protected ScrollBarView(boolean isHorizontal, ScrollStyle scrollStyle){
		this(isHorizontal, isHorizontal ? 0 : DEFAULT_SIZE, isHorizontal ? DEFAULT_SIZE : 0, DEFAULT_PADDING, true, scrollStyle);
	}
	
	protected ScrollBarView(boolean isHorizontal, int width, int height, int padding, boolean hasBorder, ScrollStyle scrollStyle) {
		this.scrollStyle=scrollStyle;
		this.isHorizontal = isHorizontal;
		this.height = height;
		this.width = width;
		this.padding = padding;
		this.hasBorder = hasBorder;
		initLayout();
		registerHandlers();
	}
	
	protected void initLayout(){
		panel = new AbsolutePanel();
		initWidget(panel);
		panel.setStyleName(scrollStyle.scrollBar());
		if(hasBorder){
			panel.addStyleName(scrollStyle.border());
		}
		slider = new Label();
		slider.setStyleName(scrollStyle.slider());
		panel.add(slider);
		resize(width, height, padding);
	}
	
	protected void resize(int width, int height, int padding){
		if(width < 0 || height < 0 || padding < 0)
			return;
		if(isHorizontal){
			if(height < padding * 2){
				return;
			}
			slider.setHeight((height - padding * 2) + "px");
		}
		else{
			if(width < padding * 2){
				return;
			}
			slider.setWidth((width - padding * 2) + "px");
		}
		panel.setPixelSize(width, height);
		this.height = height;
		this.width = width;
		this.padding = padding;
	}

	@Override
	public IsWidget getDecreaseButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IsWidget getIncreaseButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IsWidget getSliderButton() {
		return slider;
	}

	@Override
	public int getSlideableAreaLength() {
		if(isHorizontal){
			return width - 2 * padding;
		}
		else{
			return height - 2 * padding;
		}
	}

	@Override
	public int getSliderMinLength() {
		return 10;
	}

	@Override
	public void moveSlider(int distance) {
		if(isHorizontal){
			panel.setWidgetPosition(slider, panel.getWidgetLeft(slider) + distance, padding);
		}
		else{
			panel.setWidgetPosition(slider, padding, panel.getWidgetTop(slider) + distance);
			
		}
	}

	@Override
	public void setSlider(int position, int length) {
		if(isHorizontal){
			panel.setWidgetPosition(slider, position + padding, padding);
			slider.setWidth(length+"px");
		}
		else{
			panel.setWidgetPosition(slider, padding, position + padding);
			slider.setHeight(length+"px");
		}
	}

	@Override
	public boolean isHorizontal() {
		return isHorizontal;
	}

	@Override
	public void setSliderBeingDragged(boolean sliderBeingDragged, boolean isMouseOverSlider) {
		this.sliderBeingDragged = sliderBeingDragged;
		if(!sliderBeingDragged && !isMouseOverSlider){
			slider.setStyleName(scrollStyle.slider());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(enabled){
			registerHandlers();
		}
		else{
			deregisterHandlers();
		}
		this.enabled = enabled;
	}
	
	protected void registerHandlers(){
		handlerRegistrations = new ArrayList<HandlerRegistration>();
		handlerRegistrations.add(slider.addDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(!sliderBeingDragged){
					slider.setStyleName(scrollStyle.sliderMouseOver());
				}
			}
		}, MouseOverEvent.getType()));
		handlerRegistrations.add(slider.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(!sliderBeingDragged){
					slider.setStyleName(scrollStyle.slider());
				}
			}
		}, MouseOutEvent.getType()));
	}
	
	protected void deregisterHandlers() {
		for(HandlerRegistration h : handlerRegistrations){
			h.removeHandler();
		}
	}

	@Override
	public void setPixelSize(int width, int height) {
		resize(width, height, padding);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		resize(width, height, padding);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		resize(width, height, padding);
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		resize(width, height, padding);
	}
}
