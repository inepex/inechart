package com.inepex.inechart.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inechart.chartwidget.Viewport;

public class ViewportChangeEvent extends GwtEvent<ViewportChangeHandler> {

	private static final Type<ViewportChangeHandler> TYPE = new Type<ViewportChangeHandler>();
	
	private Viewport vp;
	
	public ViewportChangeEvent(Viewport vp){
		this.vp = vp;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ViewportChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewportChangeHandler handler) {
		handler.onViewportChange(vp);
	}

}
