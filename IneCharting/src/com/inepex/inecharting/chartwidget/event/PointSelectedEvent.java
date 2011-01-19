package com.inepex.inecharting.chartwidget.event;

import com.inepex.inecharting.chartwidget.model.Point;

/**
 * The chart fires this event when a point selected on a curve
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class PointSelectedEvent extends ObjectSelectedEvent {

	private static final Type<ObjectSelectedHandler> TYPE = new Type<ObjectSelectedHandler>();
	
	private Point selected;
	private double[] data;
	
	/**
	 * Chart invokes this constructor, in case of outgoing event
	 * @param selected
	 * @param data
	 */
	public PointSelectedEvent(Point selected, double[] selectedData) {
		this.selected = selected;
		this.data = selectedData;
	}
	
	/**
	 * Invoke this constructor from application
	 * @param selectedData
	 */
	public PointSelectedEvent(double[] selectedData) {
		this.data = selectedData;
	}
	
	@Override
	public Type<ObjectSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ObjectSelectedHandler handler) {
		handler.onObjectSelected(this);
	}
	
	public Point getSelectedPoint() {
		return selected;
	}

	public double[] getSelectedData(){
		return data;
	}
	
	public String getCurveName(){
		return selected.getParent().getName();
	}
}
