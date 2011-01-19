package com.inepex.inecharting.chartwidget.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Use this event when you want the chart to focus on a point on a curve or a mark. 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class ObjectSelectedEvent extends GwtEvent<ObjectSelectedHandler> {
 
	public static enum ObjectType{
		POINT,
		MARK
	}
	
	private ObjectType objectType;
	
	public ObjectType getObjectType() {
		return objectType;
	}
	
	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}
}
