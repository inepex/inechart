package com.inepex.inechart.misc.scroll;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.IsWidget;

public class SpinnerPairPresenter{
	
	private enum SpinnerStopCause{
		MAX,
		MIN,
		OTHER_SPINNER
	}
	
	protected class EventHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler{
		
		private final boolean first;
		
		public EventHandler(boolean first) {
			this.first = first;
		}
		

		@Override
		public void onMouseUp(MouseUpEvent event) {
			SpinnerPairPresenter.this.onMouseUp(event, first);
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			SpinnerPairPresenter.this.onMouseMove(event, first);
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			SpinnerPairPresenter.this.onMouseDown(event, first);
		}
		
	}
	
	public interface View{
		IsWidget getSpinner1();
		IsWidget getSpinner2();
		int getSpinnableAreaLength();
		void setSpinnerPosition1(int position);
		void setSpinnerPosition2(int position);
		int getMinDistanceBetweenSpinners();
//		void setSpinnerBeingDragged(boolean spinnerBeingDragged);
	}
	
	protected View view;
	
	protected SpinnablePair spinnable;
	protected boolean mouseDown = false;
	protected int lastMouseCoord;
	protected int mouseDownOnSpinner;
	protected AbsolutePanel mainPanel;
	protected double position1;
	protected double position2;
	protected int viewPosition1;
	protected int viewPosition2;
	protected SpinnerStopCause spinnerStopCause;
	protected boolean spinnerStop = false;
	protected ArrayList<HandlerRegistration> handlerRegistrations;
	
	private boolean actualSpinnerIsFirst; 
	
	public SpinnerPairPresenter(View view, SpinnablePair spinnable) {
		this.view = view;
		this.spinnable = spinnable;
		if(view != null){
			setView(view);
		}
	}
		
	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}
	/**
	 * @param view the view to set
	 */
	public void setView(View view) {
		this.view = view;
		deregisterHandlers();
		registerHandlers();
		position1 = spinnable.getInitialPosition1();
		position2 = spinnable.getInitialPosition1();
		setSpinner();
	}
	
	protected void deregisterHandlers() {
		if(handlerRegistrations != null){
			for(HandlerRegistration h : handlerRegistrations){
				h.removeHandler();
			}
		}
	}
	
	protected void registerHandlers(){
		handlerRegistrations = new ArrayList<HandlerRegistration>();
		EventHandler eh1 = new EventHandler(true);
		EventHandler eh2 = new EventHandler(false);
		handlerRegistrations.add(view.getSpinner1().asWidget().addDomHandler(eh1, MouseDownEvent.getType()));
		handlerRegistrations.add(view.getSpinner1().asWidget().addDomHandler(eh1, MouseUpEvent.getType()));
		handlerRegistrations.add(view.getSpinner1().asWidget().addDomHandler(eh1, MouseMoveEvent.getType()));
		handlerRegistrations.add(view.getSpinner2().asWidget().addDomHandler(eh2, MouseDownEvent.getType()));
		handlerRegistrations.add(view.getSpinner2().asWidget().addDomHandler(eh2, MouseUpEvent.getType()));
		handlerRegistrations.add(view.getSpinner2().asWidget().addDomHandler(eh2, MouseMoveEvent.getType()));
	}
	
	/**
	 * @return the spinnable
	 */
	public SpinnablePair getSpinnable() {
		return spinnable;
	}
	/**
	 * @param spinnable the spinnable to set
	 */
	public void setSpinnable(SpinnablePair spinnable) {
		this.spinnable = spinnable;
	}

	
	public void onMouseUp(MouseUpEvent event, boolean first) {
		if(mouseDown){
			Element focus;
			if(first){
				focus = view.getSpinner1().asWidget().getElement();
			}
			else{
				focus = view.getSpinner2().asWidget().getElement();
			}
			DOM.releaseCapture(focus);
//			view.setSpinnerBeingDragged(false);
			mouseDown = false;
			spinnable.dragEnd();
		}
	}

	public void onMouseMove(MouseMoveEvent event, boolean first) {
		if(mouseDown){
			int actual = event.getClientX();
			if(actual == lastMouseCoord){
				return;
			}
			int actualRel = event.getX();
			if(spinnerStop){
				if(spinnerStopCause == SpinnerStopCause.MAX && actualRel > mouseDownOnSpinner ||
					spinnerStopCause == SpinnerStopCause.MIN && actualRel < mouseDownOnSpinner){
					return;
				}
				else if(spinnerStopCause != SpinnerStopCause.OTHER_SPINNER){
					spinnerStop = false;
				}
				switch(spinnerStopCause){
				case MAX:
					lastMouseCoord = actual + mouseDownOnSpinner - actualRel;
					break;
				case MIN:
					lastMouseCoord = actual - (actualRel - mouseDownOnSpinner); 
					break;
				case OTHER_SPINNER:
				
					break;
				}
			}
			spinnerStop = !move(actual);
			if(spinnerStop && spinnerStopCause == SpinnerStopCause.OTHER_SPINNER){
				return;
			}
			lastMouseCoord = actual;
		}
	}

	public void onMouseDown(MouseDownEvent event, boolean first) {
		actualSpinnerIsFirst = first;
		event.preventDefault();
		mouseDown = true;
		lastMouseCoord = event.getClientX();
		mouseDownOnSpinner = event.getX();
		Element focus;
		if(first){
			focus = view.getSpinner1().asWidget().getElement();
		}
		else{
			focus = view.getSpinner2().asWidget().getElement();
		}
		DOM.setCapture(focus);
//		view.setSpinnerBeingDragged(true);
		spinnable.dragStart();
	}
	
	protected boolean move(int actual){
		double distancePX = actual - lastMouseCoord;
		double position;
		if(actualSpinnerIsFirst){
			position = position1;
		}
		else{
			position = position2;
		}
		//check if the spinner is at max or min so can't be moved more
		if(distancePX > 0 && position == spinnable.getSpinnableDomainLength()){
			spinnerStopCause = SpinnerStopCause.MAX;
			return false;
		}
		if(position == 0 && distancePX < 0){
			spinnerStopCause = SpinnerStopCause.MIN;
			return false;
		}
		//the domain distance
		double distanceDom = spinnable.getSpinnableDomainLength() / (double)view.getSpinnableAreaLength() * distancePX;
		int actualVP, otherVP;
		if(actualSpinnerIsFirst){
			actualVP = viewPosition1;
			otherVP = viewPosition2;
		}
		else{
			actualVP = viewPosition2;
			otherVP = viewPosition1;
		}
//		if(dist > 0){
//			if(actualVP + dist > otherVP - view.getMinDistanceBetweenSpinners() && actualVP + dist < otherVP + view.getMinDistanceBetweenSpinners()){
//				spinnerStopCause = SpinnerStopCause.OTHER_SPINNER;
//				return false;
//			}
//		}
//		else{
//			if(actualVP + dist < otherVP + view.getMinDistanceBetweenSpinners() && actualVP + dist > otherVP - view.getMinDistanceBetweenSpinners()){
//				spinnerStopCause = SpinnerStopCause.OTHER_SPINNER;
//				return false;
//			}
//		}
		//'cut' the distance to min and max
		if(position + distanceDom> spinnable.getSpinnableDomainLength()){
			distanceDom = spinnable.getSpinnableDomainLength() - position;
		}
		else if(position + distanceDom < 0){
			distanceDom = -position;
		}
		else{
//			if(distanceDom > 0){
//				if(actualVP + distancePX > otherVP - view.getMinDistanceBetweenSpinners() && actualVP + distancePX < otherVP + view.getMinDistanceBetweenSpinners()){
//					double cutDistInPx = actualVP + distancePX - (otherVP - view.getMinDistanceBetweenSpinners());
//					double cutDistInDomain = spinnable.getSpinnableDomainLength() / (double)view.getSpinnableAreaLength() * cutDistInPx;
//					distanceDom -= cutDistInDomain;
//					lastMouseCoord = (int) (actual + distancePX - cutDistInPx);
//					spinnerStop = true;
//				}
//			}
//			else{
//				if(actualVP + distancePX < otherVP + view.getMinDistanceBetweenSpinners() && actualVP + distancePX > otherVP - view.getMinDistanceBetweenSpinners()){
//					double cutDistInPx = otherVP + view.getMinDistanceBetweenSpinners() - (actual + distancePX);
//					double cutDistInDomain = spinnable.getSpinnableDomainLength() / (double)view.getSpinnableAreaLength() * cutDistInPx;
//					distanceDom -= cutDistInDomain;
//					lastMouseCoord = (int) (actual + (distancePX + cutDistInPx)); 
//					spinnerStop = true;
//				}
//			}
		}
		if(actualSpinnerIsFirst){
			position1 += distanceDom;
		}
		else{
			position2 += distanceDom;
		}
		
		if(actualSpinnerIsFirst){
			spinnable.spinnerMoved1(distanceDom);
		}
		else{
			spinnable.spinnerMoved2(distanceDom);
		}
		setSpinner();
		return true;
	}
	
	protected void setSpinner(){
		if(actualSpinnerIsFirst){
			viewPosition1 = (int) (position1 * view.getSpinnableAreaLength() / spinnable.getSpinnableDomainLength()); 
			view.setSpinnerPosition1(viewPosition1);
		}
		else{
			viewPosition2 = (int) (position2 * view.getSpinnableAreaLength() / spinnable.getSpinnableDomainLength()); 
			view.setSpinnerPosition2(viewPosition2);
		} 
		
	}
	
	public void setSpinnerPosition1(double position){
		this.position1 = position;
		actualSpinnerIsFirst = true;
		setSpinner();
	}
	
	public void setSpinnerPosition2(double position){
		this.position2 = position;
		actualSpinnerIsFirst = false;
		setSpinner();
	}
}
