package com.inepex.inecharting.chartwidget.properties;


import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.event.StateChangeEvent;
import com.inepex.inecharting.chartwidget.model.State;

public class EventProperties {
	enum GraphicalObjectType{
		AXIS,
		CURVE,
		POINT,
		MARK
	}
	
	TreeMap<GraphicalObjectType, State[]> eventFiringPolicy;
	boolean curvePointsSelectionByXposOnly;
	
	
	/**
	 * @return the curvePointsSelectionByXposOnly
	 */
	public boolean isCurvePointsSelectionByXposOnly() {
		return curvePointsSelectionByXposOnly;
	}

	/**
	 * @param curvePointsSelectionByXposOnly the curvePointsSelectionByXposOnly to set
	 */
	public void setCurvePointsSelectionByXposOnly(
			boolean curvePointsSelectionByXposOnly) {
		this.curvePointsSelectionByXposOnly = curvePointsSelectionByXposOnly;
	}

	public EventProperties() {
		eventFiringPolicy = new TreeMap<EventProperties.GraphicalObjectType, State[]>();
		setEventFiringStatesForAxis(State.FOCUSED);
		setEventFiringStatesForMark(State.FOCUSED, State.ACTIVE);
		setEventFiringStatesForPoint(State.FOCUSED, State.ACTIVE);
		setEventFiringStatesForCurve(State.FOCUSED);
	}
	
	/**
	 * sets states which trigger {@link StateChangeEvent} for Curve objects
	 * @param states
	 */
	public void setEventFiringStatesForCurve(State... states){
		this.eventFiringPolicy.put(GraphicalObjectType.CURVE, states);
	}
	/**
	 * sets states which trigger {@link StateChangeEvent} for Mark objects
	 * @param states
	 */
	public void setEventFiringStatesForMark(State... states){
		this.eventFiringPolicy.put(GraphicalObjectType.MARK, states);
	}
	/**
	 * sets states which trigger {@link StateChangeEvent} for Point objects
	 * @param states
	 */
	public void setEventFiringStatesForPoint(State... states){
		this.eventFiringPolicy.put(GraphicalObjectType.POINT, states);
	}
	/**
	 * sets states which trigger {@link StateChangeEvent} for Axis objects
	 * @param states
	 */
	public void setEventFiringStatesForAxis(State... states){
		this.eventFiringPolicy.put(GraphicalObjectType.AXIS, states);
	}
	
	

}
