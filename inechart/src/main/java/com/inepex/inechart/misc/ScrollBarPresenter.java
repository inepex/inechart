package com.inepex.inechart.misc;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Presenter of a scrollbar widget, contains event handling and logic.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class ScrollBarPresenter implements MouseDownHandler, 
	MouseUpHandler, MouseMoveHandler, ClickHandler, MouseOutHandler{

	public interface View{
		//for handlerregistrations
		IsWidget getDecreaseButton();
		IsWidget getIncreaseButton();
		IsWidget getSliderButton();
		//for updating the view
		boolean isHorizontal();
		int getSlideableAreaLength();
		int getSliderMinLength();
		void moveSlider(int distance);
		void setSlider(int position, int length);
		void setSliderBeingDragged(boolean sliderBeingDragged, boolean isMouseOverSlider);
		void setEnabled(boolean enabled);
	}
	
	protected RepeatingCommand increaseCommand;
	protected RepeatingCommand decreaseCommand;
	protected final int DELAY = 250;
	protected Scrollable scrollable;
	protected View view;

	protected int sliderPosition;
	protected int sliderWidth;

	protected double visibleMin;
	protected double visibleMax;
	
	protected boolean mouseDown = false;
	protected int mouseDownCoordinate_Client;
	protected int mouseDownCoordinate_Slider;
	protected int lastMouseMoveCoordinate;
	protected boolean sliderStopAtMax;
	protected boolean sliderStop;
	
	protected boolean enabled = true;
	
	protected ArrayList<HandlerRegistration> handlerRegistrations;
	
	public ScrollBarPresenter(Scrollable scrollable){
		this(scrollable,null);
	}
	
	/**
	 * Creates a scrollbar from the given parameters
	 * @param scrollable the object to scroll
	 * @param view the {@link View} to bind
	 */
	public ScrollBarPresenter(Scrollable scrollable, View view) {
		this.scrollable = scrollable;
		this.view = view;
		visibleMin = scrollable.getInitialIntervalMin();
		visibleMax = scrollable.getInitialIntervalMax();
		if(view != null){
			setView(view);
		}
		
		
		increaseCommand = new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				return mouseDown && increase();
			}
		};
		
		decreaseCommand = new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				return mouseDown && decrease();
			}
		};
	}
	
	protected void registerHandlers(){
		handlerRegistrations = new ArrayList<HandlerRegistration>();
		if(view.getDecreaseButton() != null){
			handlerRegistrations.add(view.getDecreaseButton().asWidget().addDomHandler(this, ClickEvent.getType()));
			handlerRegistrations.add(view.getDecreaseButton().asWidget().addDomHandler(this, MouseDownEvent.getType()));
			handlerRegistrations.add(view.getDecreaseButton().asWidget().addDomHandler(this, MouseUpEvent.getType()));
		}
		if(view.getIncreaseButton() != null){
			handlerRegistrations.add(view.getIncreaseButton().asWidget().addDomHandler(this, ClickEvent.getType()));
			handlerRegistrations.add(view.getIncreaseButton().asWidget().addDomHandler(this, MouseDownEvent.getType()));
			handlerRegistrations.add(view.getIncreaseButton().asWidget().addDomHandler(this, MouseUpEvent.getType()));
		}
		if(view.getSliderButton() != null){
			handlerRegistrations.add(view.getSliderButton().asWidget().addDomHandler(this, MouseDownEvent.getType()));
			handlerRegistrations.add(view.getSliderButton().asWidget().addDomHandler(this, MouseUpEvent.getType()));
			handlerRegistrations.add(view.getSliderButton().asWidget().addDomHandler(this, MouseMoveEvent.getType()));
		}
	}
	
	protected void deregisterHandlers() {
		for(HandlerRegistration h : handlerRegistrations){
			h.removeHandler();
		}
	}
	
	public void setSlider(double visibleMin, double visibleMax){
		//validate values
		if(visibleMin < 0){
			visibleMin = 0;
		}
		if(visibleMax > scrollable.getScrollableDomainLength()){
			visibleMax = scrollable.getScrollableDomainLength();
		}
		//ensure that min is smaller than max
		this.visibleMin = Math.min(visibleMin, visibleMax);
		this.visibleMax = Math.max(visibleMin, visibleMax);
		setSlider();	
	}
	
	protected void setSlider(){
		int sliderW = (int) ((visibleMax - visibleMin) / scrollable.getScrollableDomainLength() * view.getSlideableAreaLength());
		int sliderPos = (int) (visibleMin / scrollable.getScrollableDomainLength() * view.getSlideableAreaLength());
		if(sliderW < view.getSliderMinLength()){
			sliderW = view.getSliderMinLength();
		}
		if(sliderPos > view.getSlideableAreaLength() - view.getSliderMinLength()){
			sliderPos = view.getSlideableAreaLength() - view.getSliderMinLength();
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
	
	protected void updateScrollable(double dist){
		if(dist > 0 && dist + visibleMax > scrollable.getScrollableDomainLength()){
			dist = scrollable.getScrollableDomainLength() - visibleMax;
		}
		else if(dist < 0 && visibleMin + dist < 0){
			dist = 0 - visibleMin;
		}
		if(dist == 0)
			return;
		visibleMax += dist;
		visibleMin += dist;
		scrollable.scrollBarMoved(dist);
	}
	
	protected boolean increase(){
		if(visibleMax == scrollable.getScrollableDomainLength())
			return false;
		updateScrollable(scrollable.getIntervalStep());
		setSlider();
		return true;
	}
	
	protected boolean decrease(){
		if(visibleMin == 0)
			return false;
		updateScrollable(-scrollable.getIntervalStep());
		setSlider();
		return true;
	}
	
	protected boolean move(int distance){
		if(distance > 0 && visibleMax == scrollable.getScrollableDomainLength() || distance < 0 && visibleMin == 0)
			return false;
		double dist = distance / (double)view.getSlideableAreaLength() * scrollable.getScrollableDomainLength();
		updateScrollable(dist);
		setSlider();
		return true;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == view.getDecreaseButton().asWidget()){
			decrease();
		}
		else if(event.getSource() == view.getIncreaseButton().asWidget()){
			increase();
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(mouseDown && event.getSource() == view.getSliderButton().asWidget()){
			int actualCoordinate;
			int actualCoordinate_Slider;
			if(view.isHorizontal()){
				actualCoordinate = event.getClientX();
				actualCoordinate_Slider = event.getRelativeX(view.getSliderButton().asWidget().getElement());
			}
			else{
				actualCoordinate = event.getClientY();
				actualCoordinate_Slider = event.getRelativeY(view.getSliderButton().asWidget().getElement());
			}
			if(actualCoordinate == lastMouseMoveCoordinate)
				return;
			if(sliderStop && 
					(sliderStopAtMax && actualCoordinate_Slider > mouseDownCoordinate_Slider ||
							!sliderStopAtMax && actualCoordinate_Slider < mouseDownCoordinate_Slider)){
				return;
			}
			sliderStop = false;
			if(!move(actualCoordinate - lastMouseMoveCoordinate)){
				sliderStop = true;
				sliderStopAtMax = actualCoordinate > lastMouseMoveCoordinate;
			}
			lastMouseMoveCoordinate = actualCoordinate;
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mouseDown = false;
		DOM.releaseCapture(view.getSliderButton().asWidget().getElement());
		int x = event.getRelativeX(view.getSliderButton().asWidget().getElement());
		int y = event.getRelativeY(view.getSliderButton().asWidget().getElement());
		int width = view.getSliderButton().asWidget().getElement().getOffsetWidth();
		int height = view.getSliderButton().asWidget().getElement().getOffsetHeight();
		boolean isOverSlider = false;
		if(x > 0 && y > 0 && y < height && x < width)
			isOverSlider = true;
		view.setSliderBeingDragged(false, isOverSlider);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		if(view.getDecreaseButton() != null && event.getSource() == view.getDecreaseButton().asWidget()){
			mouseDown = true;
			Scheduler.get().scheduleFixedDelay(decreaseCommand, DELAY);
		}
		else if(view.getIncreaseButton() != null && event.getSource() == view.getIncreaseButton().asWidget()){
			mouseDown = true;
			Scheduler.get().scheduleFixedDelay(increaseCommand, DELAY);
		}	
		else if(event.getSource() == view.getSliderButton().asWidget()){
			mouseDown = true;
			view.setSliderBeingDragged(true, true);
			if(view.isHorizontal()){
				lastMouseMoveCoordinate = mouseDownCoordinate_Client = event.getClientX();
				mouseDownCoordinate_Slider = event.getRelativeX(view.getSliderButton().asWidget().getElement());
			}
			else{
				lastMouseMoveCoordinate = mouseDownCoordinate_Client = event.getClientY();
				mouseDownCoordinate_Slider = event.getRelativeY(view.getSliderButton().asWidget().getElement());
			}
			DOM.setCapture(view.getSliderButton().asWidget().getElement());
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if(event.getSource() == view.getDecreaseButton().asWidget()){
			mouseDown = false;
		}
		else if(event.getSource() == view.getIncreaseButton().asWidget()){
			mouseDown = false;
		}
	}

	public Scrollable getScrollable() {
		return scrollable;
	}

	public void setScrollable(Scrollable scrollable) {
		this.scrollable = scrollable;
	}

	public View getView() {
		return view;
	}
	
	public void setView(View view) {
		this.view = view;
		visibleMin = scrollable.getInitialIntervalMin();
		visibleMax = scrollable.getInitialIntervalMax();
		setSlider();
		registerHandlers();
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		view.setEnabled(enabled);
		if(!enabled){
			deregisterHandlers();
		}
		else{
			registerHandlers();
		}
	}

}
