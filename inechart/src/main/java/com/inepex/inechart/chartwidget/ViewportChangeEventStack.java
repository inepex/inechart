package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;

public class ViewportChangeEventStack {

	private TreeMap<Long, ViewportChangeEvent> events;
	private long minimumTimeOut;
	private long lastSent;
	private RepeatingCommand firingCommmand;
	private boolean firingScheduled;
	private IneChartEventManager eventManager;

	public ViewportChangeEventStack(IneChartEventManager eventManager) {
		events = new TreeMap<Long, ViewportChangeEvent>();
		minimumTimeOut = Defaults.minimumTimeOutBetweenVPEvents;
		lastSent = System.currentTimeMillis();
		firingScheduled = false;
		this.eventManager = eventManager;
	}

	public void pushEvent(ViewportChangeEvent viewportChangeEvent){
		events.put(System.currentTimeMillis(), viewportChangeEvent);
		scheduleFiring();
	}

	private void scheduleFiring(){
		if(firingScheduled)
			return;
		firingScheduled = true;

		firingCommmand = new RepeatingCommand() {
			@Override
			public boolean execute() {
				fireEvent(mergeEvents());
				if(events.size() > 0){
					scheduleFiring();
				}
				firingScheduled = false;
				eventManager.eventFinished();
				return false;
			}
		};

		long now = System.currentTimeMillis();
		int delay = 1;
		if(now - lastSent < minimumTimeOut){
			delay = (int) (minimumTimeOut - (now - lastSent));
		}
		Scheduler.get().scheduleFixedDelay(firingCommmand, delay);

	}

	private void fireEvent(ViewportChangeEvent event){
		eventManager.fireInnerEvent(event);
		lastSent = System.currentTimeMillis();
	}

	private ViewportChangeEvent mergeEvents(){
		TreeMap<IneChartModule2D, TreeMap<Long, ViewportChangeEvent>> eventsSortedByAddress = new TreeMap<IneChartModule2D, TreeMap<Long,ViewportChangeEvent>>();
		TreeMap<Long, ViewportChangeEvent> addressedToAll = new TreeMap<Long, ViewportChangeEvent>();
		for(long time : events.keySet()){
			ViewportChangeEvent actualEvent = events.get(time);
			ArrayList<IneChartModule2D> addressedModuls = (ArrayList<IneChartModule2D>) actualEvent.getAddressedModules();
			if(addressedModuls == null || addressedModuls.size() == 0){
				addressedToAll.put(time, actualEvent);
			}
			else{
				for(IneChartModule2D addressed : addressedModuls){
					TreeMap<Long, ViewportChangeEvent> eventsPerModule = eventsSortedByAddress.get(addressed);
					if(eventsPerModule == null){
						eventsPerModule = new TreeMap<Long, ViewportChangeEvent>();
						eventsSortedByAddress.put(addressed, eventsPerModule);
					}
					eventsPerModule.put(time, actualEvent);
				}
			}
		}
		TreeMap<Long, ViewportChangeEvent> eventsToMerge = null;
		IneChartModule2D addressedModul = null;
		//non specified addres is higher priority
		if(addressedToAll.size() > 0){
			eventsToMerge = addressedToAll;
		}
		else{
			long earliest = Long.MAX_VALUE;
			for(IneChartModule2D m : eventsSortedByAddress.keySet()){
				if(earliest > eventsSortedByAddress.get(m).firstKey()){
					earliest = eventsSortedByAddress.get(m).firstKey();
					eventsToMerge = eventsSortedByAddress.get(m);
					addressedModul = m;
				}
			}
		}
		if(eventsToMerge == null){
			return null;
		}

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		double dx = 0, dy = 0;
		boolean isXChange = false, isYChange = false;
		for(long time : eventsToMerge.keySet()){
			ViewportChangeEvent actualEvent = eventsToMerge.get(time);
			if(actualEvent.getDx() != 0 || actualEvent.getDy() != 0){
				isXChange = isXChange || actualEvent.isXChange();
				isYChange = isYChange || actualEvent.isYChange();
				dx += actualEvent.getDx();
				dy += actualEvent.getDy();
			}
			else{
				isXChange = actualEvent.isXChange();
				isYChange = actualEvent.isYChange();
				xMax = actualEvent.getxMax();
				xMin = actualEvent.getxMin();
				yMin = actualEvent.getyMin();
				yMax = actualEvent.getyMax();
				dx = dy = 0;
			}
		}
		ViewportChangeEvent event;
		if(dx != 0 || dy != 0){
			if(isXChange && !isYChange){
				event = new ViewportChangeEvent(dx, true);
			}
			else if(!isXChange && isYChange){
				event = new ViewportChangeEvent(dy, false);
			}
			else{
				event = new ViewportChangeEvent(dx, dy);
			}
		}
		else{
			if(isXChange && !isYChange){
				event = new ViewportChangeEvent(xMin, xMax, true);
			}
			else if(!isXChange && isYChange){
				event = new ViewportChangeEvent(yMin, yMax, false);
			}
			else{
				event = new ViewportChangeEvent(xMin, yMin, xMax, yMax);
			}
		}
		if(addressedModul != null){
			event.addAddressedModule(addressedModul);
		}
		//remove merged events
		for(Long time : eventsToMerge.keySet()){
			ViewportChangeEvent ev = events.get(time);
			if(addressedModul != null && ev.getAddressedModules() != null && ev.getAddressedModules().size() > 1){
				ev.getAddressedModules().remove(addressedModul);
			}
			else{
				events.remove(time);
			}
		}
		return event;
	}
	

	
}
