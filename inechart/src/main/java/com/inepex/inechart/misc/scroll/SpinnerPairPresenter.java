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
		boolean canSpinnersSwapPosition();
		void setSpinnerBeingDragged(boolean spinnerBeingDragged, boolean first);
	}

	protected View view;

	protected SpinnablePair spinnable;
	protected boolean mouseDown = false;
	protected int lastMouseCoord;
	protected int startMouseCoordRelativeToSpinner;
	protected AbsolutePanel mainPanel;
	protected double positionDomain1;
	protected double positionDomain2;
	protected int positionView1;
	protected int positionView2;
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
		positionDomain1 = spinnable.getInitialPosition1();
		positionDomain2 = spinnable.getInitialPosition1();
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
			setDOMFocus(false);
			view.setSpinnerBeingDragged(false, first);
			mouseDown = false;
			spinnable.dragEnd();
			spinnerStop = false;
		}
	}

	protected void setDOMFocus(boolean capture){
		Element focus;
		if(actualSpinnerIsFirst){
			focus = view.getSpinner1().asWidget().getElement();
		}
		else{
			focus = view.getSpinner2().asWidget().getElement();
		}
		if(capture){
			DOM.setCapture(focus);
		}
		else{
			DOM.releaseCapture(focus);
		}
	}

	public void onMouseDown(MouseDownEvent event, boolean first) {
		actualSpinnerIsFirst = first;
		event.preventDefault();
		mouseDown = true;
		lastMouseCoord = event.getClientX();
		startMouseCoordRelativeToSpinner = event.getX();
		setDOMFocus(true);
		view.setSpinnerBeingDragged(true, first);
		spinnable.dragStart();
	}

	public void onMouseMove(MouseMoveEvent event, boolean first) {
		if(!mouseDown){
			return;
		}
		int actualMouseCoord = event.getClientX();
		//if last mousePos isn't differ from actual, we can exit
		if(actualMouseCoord == lastMouseCoord){
			return;
		}
		int actualMouseCoordRelativeToSpinner = event.getX();
		if(spinnerStop){
			switch(spinnerStopCause){
			//if the spinner stopped at an extreme and it still should not move, we can exit 
			case MAX:
				if(actualMouseCoordRelativeToSpinner > startMouseCoordRelativeToSpinner){
					return;
				}
				break;
			case MIN:
				if(actualMouseCoordRelativeToSpinner < startMouseCoordRelativeToSpinner){
					return;
				}
				break;
			case OTHER_SPINNER:
				if(!view.canSpinnersSwapPosition()){
					//if the spinner stopped at its extreme pos and it still should not move, we can exit 
					if(actualSpinnerIsFirst && actualMouseCoordRelativeToSpinner > startMouseCoordRelativeToSpinner ||
						!actualSpinnerIsFirst && actualMouseCoordRelativeToSpinner < startMouseCoordRelativeToSpinner){
						return;
					}
				}
				else{
					int spinnerViewPositionActual, spinnerViewPositionOther;
					if(actualSpinnerIsFirst){
						spinnerViewPositionActual = positionView1;
						spinnerViewPositionOther = positionView2;
					}
					else{
						spinnerViewPositionActual = positionView2;
						spinnerViewPositionOther = positionView1;
					}
					int diffFromStop = actualMouseCoord - lastMouseCoord;
					//lastmousecoord should be the pos where spinner stopped
					if(spinnerViewPositionActual < spinnerViewPositionOther){
						//spinner 
						if(diffFromStop > 0 && 
							( diffFromStop < view.getMinDistanceBetweenSpinners() * 2 /* + view.getSpinnerWidth() */ ||
							  spinnerViewPositionOther + view.getMinDistanceBetweenSpinners() > view.getSpinnableAreaLength()
							)){
							return;
						}
					}
					else{
						//spinner 
						if(diffFromStop < 0 && 
							( Math.abs(diffFromStop) < view.getMinDistanceBetweenSpinners() * 2 /* + view.getSpinnerWidth() */ ||
							  spinnerViewPositionOther - view.getMinDistanceBetweenSpinners() < 0
							)){
							return;
						}
					}
				}
				break;
			}
			spinnerStop = false;
		}
		move(actualMouseCoord);
	}
	
	protected int domainToView(double domain){
		return (int) (view.getSpinnableAreaLength() / (double)spinnable.getSpinnableDomainLength()  * domain);
	}
	
	protected double viewToDomain(int view){
		return spinnable.getSpinnableDomainLength() / (double)this.view.getSpinnableAreaLength() * view; 
	}

	protected void move(int actualMouseCoord){
		int distanceInView = actualMouseCoord - lastMouseCoord;
		
		//check if we should 'cut' the distance
		int spinnerViewPositionActual, spinnerViewPositionOther;
		if(actualSpinnerIsFirst){
			spinnerViewPositionActual = positionView1;
			spinnerViewPositionOther = positionView2;
		}
		else{
			spinnerViewPositionActual = positionView2;
			spinnerViewPositionOther = positionView1;
		}
		if(spinnerViewPositionActual + distanceInView > view.getSpinnableAreaLength()){
			distanceInView = view.getSpinnableAreaLength() - spinnerViewPositionActual;
			spinnerStop = true;
			spinnerStopCause = SpinnerStopCause.MAX;
		}
		if(spinnerViewPositionActual + distanceInView < 0){
			distanceInView = -spinnerViewPositionActual;
			spinnerStop = true;
			spinnerStopCause = SpinnerStopCause.MIN;
		}
		if(spinnerViewPositionActual < spinnerViewPositionOther){
			if(spinnerViewPositionActual + distanceInView > spinnerViewPositionOther - view.getMinDistanceBetweenSpinners() &&
					spinnerViewPositionActual + distanceInView < spinnerViewPositionOther + view.getMinDistanceBetweenSpinners() ){
				distanceInView = spinnerViewPositionOther - view.getMinDistanceBetweenSpinners() - spinnerViewPositionActual;
				spinnerStop = true;
				spinnerStopCause = SpinnerStopCause.OTHER_SPINNER;
			}
		}
		else{
			if(spinnerViewPositionActual + distanceInView < spinnerViewPositionOther + view.getMinDistanceBetweenSpinners() &&
					spinnerViewPositionActual + distanceInView > spinnerViewPositionOther - view.getMinDistanceBetweenSpinners() ){
				distanceInView = spinnerViewPositionOther + view.getMinDistanceBetweenSpinners() - spinnerViewPositionActual;
				spinnerStop = true;
				spinnerStopCause = SpinnerStopCause.OTHER_SPINNER;
			}
		}
		
		lastMouseCoord += distanceInView;
		double distanceInDomain = viewToDomain(distanceInView);
		if(actualSpinnerIsFirst){
			positionDomain1 += distanceInDomain;
		}
		else{
			positionDomain2 += distanceInDomain;
		}

		if(actualSpinnerIsFirst){
			spinnable.spinnerMoved1(distanceInDomain);
		}
		else{
			spinnable.spinnerMoved2(distanceInDomain);
		}
		setSpinner();
	}

	protected void setSpinner(){
		if(actualSpinnerIsFirst){
			positionView1 = domainToView(positionDomain1); 
			view.setSpinnerPosition1(positionView1);
		}
		else{
			positionView2 = domainToView(positionDomain2);
			view.setSpinnerPosition2(positionView2);
		} 

	}

	public void setSpinnerPosition1(double position){
		this.positionDomain1 = position;
		actualSpinnerIsFirst = true;
		setSpinner();
	}

	public void setSpinnerPosition2(double position){
		this.positionDomain2 = position;
		actualSpinnerIsFirst = false;
		setSpinner();
	}
}
