package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inecharting.chartwidget.model.HasState;
import com.inepex.inecharting.chartwidget.model.State;

public class StateChangeEvent extends GwtEvent<StateChangeHandler>{

	public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();
	
	private HasState sourceObject;
	private State previousState;
	
	public StateChangeEvent(HasState sourceObject, State previousState) {
		this.sourceObject = sourceObject;
		this.previousState = previousState;
	}

	@Override
	protected void dispatch(StateChangeHandler handler) {
		handler.onStateChange(this);
	}

	@Override
	public Type<StateChangeHandler> getAssociatedType() {
		return TYPE;
	}

	public HasState getSourceObject() {
		return sourceObject;
	}
	
	public State getPreviousState(){
		return previousState;
	}
	
	public State getNewState(){
		return sourceObject.getState();
	}

}
