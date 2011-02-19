package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inecharting.chartwidget.model.HasState;

public class StateChangeEvent extends GwtEvent<StateChangeHandler>{

	public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();
	
	private HasState sourceObject;
	
	public StateChangeEvent(HasState sourceObject) {
		this.sourceObject = sourceObject;
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

}
