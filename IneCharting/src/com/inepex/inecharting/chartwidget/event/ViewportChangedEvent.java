package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

public class ViewportChangedEvent extends GwtEvent<ViewportChangedHandler> {

	private static final Type<ViewportChangedHandler> TYPE = new Type<ViewportChangedHandler>();
	
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ViewportChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewportChangedHandler handler) {
		// TODO Auto-generated method stub
		
	}

}
