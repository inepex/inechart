package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inecharting.chartwidget.model.Axes;

public class ExtremesChangeEvent extends GwtEvent<ExtremesChangeHandler> {

	public static final Type<ExtremesChangeHandler> TYPE = new Type<ExtremesChangeHandler>();
	
	private Axes axis;
	private double newMin;
	private double newMax;
	
	@Override
	public Type<ExtremesChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ExtremesChangeHandler handler) {
		handler.onExtremesChange(this);
	}

	public ExtremesChangeEvent(Axes axis, double newMin, double newMax) {
		this.axis = axis;
		this.newMin = newMin;
		this.newMax = newMax;
	}

	public Axes getAxis() {
		return axis;
	}

	public double getMin() {
		return newMin;
	}

	public double getMax() {
		return newMax;
	}

}
