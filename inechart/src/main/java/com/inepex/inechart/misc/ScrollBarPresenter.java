package com.inepex.inechart.misc;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


public class ScrollBarPresenter implements MouseDownHandler, 
	MouseUpHandler, MouseMoveHandler, ClickHandler{

	public interface View{
		//for handlerregistrations
		IsWidget getDecreaseButton();
		IsWidget getIncreaseButton();
		IsWidget getSliderButton();
		//for updating the view
		int getSlideableAreaLength();
		int getSliderMinWidth();
		void moveSlider(int distance);
		void setSlider(int position, int width);
	}

	protected Scrollable scrollable;
	protected View view;
	protected double scrollableDomain;
	protected double initialIntervalMin;
	protected double initialIntervalMax;
	protected double intervalStep;
	
	protected int sliderPosition;
	protected int sliderWidth;
	protected int slideableAreaLength;
	protected double visibleMin;
	protected double visibleMax;
	
	/**
	 * Creates a scrollbar from the given parameters
	 * @param scrollable the object to scroll
	 * @param view the {@link View} to bind
	 * @param scrollableDomain the length of the scrollable domain
	 * @param initialIntervalMin the minimum of the default visible interval on domain
	 * @param initialIntervalMax the maximum of the default visible interval on domain
	 * @param intervalStep the visible interval is shifted by this amount over the domain
	 */
	public ScrollBarPresenter(Scrollable scrollable, View view,
			double scrollableDomain, double initialIntervalMin,
			double initialIntervalMax, double intervalStep) {
		this.scrollable = scrollable;
		this.view = view;
		this.scrollableDomain = scrollableDomain;
		visibleMin = this.initialIntervalMin = initialIntervalMin;
		visibleMax = this.initialIntervalMax = initialIntervalMax;
		this.intervalStep = intervalStep;
		
		bindView();
	}
	
	protected void bindView(){
		if(view.getDecreaseButton() != null){
			view.getDecreaseButton().asWidget().addHandler(this, ClickEvent.getType());
			view.getDecreaseButton().asWidget().addHandler(this, MouseDownEvent.getType());
			view.getDecreaseButton().asWidget().addHandler(this, MouseUpEvent.getType());
		}
		if(view.getIncreaseButton() != null){
			view.getIncreaseButton().asWidget().addHandler(this, ClickEvent.getType());
			view.getIncreaseButton().asWidget().addHandler(this, MouseDownEvent.getType());
			view.getIncreaseButton().asWidget().addHandler(this, MouseUpEvent.getType());
		}
		if(view.getSliderButton() != null){
			view.getSliderButton().asWidget().addHandler(this, MouseDownEvent.getType());
			view.getSliderButton().asWidget().addHandler(this, MouseUpEvent.getType());
			view.getSliderButton().asWidget().addHandler(this, MouseMoveEvent.getType());
		}
		slideableAreaLength = view.getSlideableAreaLength();
	}
	
	protected void setSlider(){
		int sliderW = (int) ((visibleMax - visibleMin) / scrollableDomain * slideableAreaLength);
		int sliderPos = (int) (visibleMin / scrollableDomain * slideableAreaLength);
		if(sliderW < view.getSliderMinWidth()){
			sliderW = view.getSliderMinWidth();
		}
		if(sliderPos > slideableAreaLength - view.getSliderMinWidth()){
			sliderPos = slideableAreaLength - view.getSliderMinWidth();
		}
		if(sliderPos < 0){
			sliderPos = 0;
		}
		if(sliderW != sliderWidth || sliderPos != sliderPosition){
			sliderWidth = sliderW;
			sliderPosition = sliderPos;
			view.setSlider(sliderPosition, sliderWidth);
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == view.getDecreaseButton().asWidget()){
			
		}
		else if(event.getSource() == view.getIncreaseButton().asWidget()){
			
		}	
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(event.getSource() == view.getSliderButton().asWidget()){
			
		
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if(event.getSource() == view.getDecreaseButton().asWidget()){
			
		}
		else if(event.getSource() == view.getIncreaseButton().asWidget()){
			
		}
		else if(event.getSource() == view.getSliderButton().asWidget()){
			
		
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if(event.getSource() == view.getDecreaseButton().asWidget()){
			
		}
		else if(event.getSource() == view.getIncreaseButton().asWidget()){
			
		}	
		else if(event.getSource() == view.getSliderButton().asWidget()){
			
			
		}
	}
}
