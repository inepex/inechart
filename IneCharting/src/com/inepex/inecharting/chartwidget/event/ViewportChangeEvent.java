package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;

public class ViewportChangeEvent extends GwtEvent<ViewportChangeHandler> {

	public static final Type<ViewportChangeHandler> TYPE = new Type<ViewportChangeHandler>();
	
	private double dx,min,max;
	
	public ViewportChangeEvent(double dx) {
		this.dx = dx;
		min = max = 0;
	}
	
	public ViewportChangeEvent(double viewportMin, double viewportMax) {
		min = viewportMin;
		max = viewportMax;
		dx = 0;
	}
	
	@Override
	public Type<ViewportChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewportChangeHandler handler) {
		if(min == max && min == 0)
			handler.onViewportScrolled(dx);
		else
			handler.onViewportResized(min, max);
	}

}
