package com.inepex.inechart.misc;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.IsWidget;

public class SpinnerPresenter implements MouseDownHandler, MouseMoveHandler, MouseUpHandler{
	
	public interface View{
		IsWidget getSpinner();
		int getSpinnableAreaLength();
		void setSpinnerPosition(int position);
		void setSpinnerBeingDragged(boolean spinnerBeingDragged);
	}
	
	protected View view;
	protected Spinnable spinnable;
	protected boolean mouseDown = false;
	protected int lastMouseCoord;
	protected int mouseDownOnSpinner;
	protected AbsolutePanel mainPanel;
	protected double position;
	protected boolean spinnerStopAtMax = false;
	protected boolean spinnerStop = false;
	

	protected ArrayList<HandlerRegistration> handlerRegistrations;
	
	public SpinnerPresenter(View view, Spinnable spinnable) {
		this.view = view;
		this.spinnable = spinnable;
		if(view != null)
			setView(view);
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
		position = spinnable.getInitialPosition();
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
		handlerRegistrations.add(view.getSpinner().asWidget().addDomHandler(this, MouseDownEvent.getType()));
		handlerRegistrations.add(view.getSpinner().asWidget().addDomHandler(this, MouseUpEvent.getType()));
		handlerRegistrations.add(view.getSpinner().asWidget().addDomHandler(this, MouseMoveEvent.getType()));
	}
	
	/**
	 * @return the spinnable
	 */
	public Spinnable getSpinnable() {
		return spinnable;
	}
	/**
	 * @param spinnable the spinnable to set
	 */
	public void setSpinnable(Spinnable spinnable) {
		this.spinnable = spinnable;
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if(mouseDown){
			DOM.releaseCapture(view.getSpinner().asWidget().getElement());
			view.setSpinnerBeingDragged(false);
			mouseDown = false;
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(mouseDown){
			int actual = event.getClientX();
			if(actual == lastMouseCoord){
				return;
			}
			int actualRel = event.getX();
			if(spinnerStop){
				if(spinnerStopAtMax && actualRel > mouseDownOnSpinner ||
					!spinnerStopAtMax && actualRel < mouseDownOnSpinner){
					return;
				}
				spinnerStop = false;
				lastMouseCoord = spinnerStopAtMax ? actual + mouseDownOnSpinner - actualRel : actual - (actualRel - mouseDownOnSpinner); 
			}
			if(!move(actual - lastMouseCoord)){
				spinnerStop = true;
				spinnerStopAtMax = position == spinnable.getSpinnableDomainLength();
			}
			lastMouseCoord = actual;
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		mouseDown = true;
		lastMouseCoord = event.getClientX();
		mouseDownOnSpinner = event.getX();
		DOM.setCapture(view.getSpinner().asWidget().getElement());
		view.setSpinnerBeingDragged(true);
	}
	
	protected boolean move(int distance){
		if(distance > 0 && position == spinnable.getSpinnableDomainLength() || position == 0 && distance < 0){
			return false;
		}
		double dist = spinnable.getSpinnableDomainLength() / (double)view.getSpinnableAreaLength() * distance;
		
		if(position + dist> spinnable.getSpinnableDomainLength()){
			dist = spinnable.getSpinnableDomainLength() - position;
		}
		else if(position + dist < 0){
			dist = -position;
		}
		position += dist;
		
		spinnable.spinnerMoved(dist);
		setSpinner();
		return true;
	}
	
	protected void setSpinner(){
		int spinnerPos = (int) (position * view.getSpinnableAreaLength() / spinnable.getSpinnableDomainLength()); 
		view.setSpinnerPosition(spinnerPos);
	}
}
