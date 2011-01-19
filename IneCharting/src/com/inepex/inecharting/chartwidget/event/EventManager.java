package com.inepex.inecharting.chartwidget.event;


/**
 * 
 * This class is responsible for both in and outgoing event handling, and also for private (inner) events.s
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class EventManager implements ObjectSelectedHandler{

	@Override
	public void onObjectSelected(ObjectSelectedEvent event) {
		if(event.getObjectType().equals(ObjectSelectedEvent.ObjectType.MARK)){
			
		}
		else if(event.getObjectType().equals(ObjectSelectedEvent.ObjectType.POINT)){
			
		}
			
	}
	

}
