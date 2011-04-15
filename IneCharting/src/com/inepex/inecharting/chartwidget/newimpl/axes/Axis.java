package com.inepex.inecharting.chartwidget.newimpl.axes;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.inepex.inecharting.chartwidget.newimpl.properties.Color;

public class Axis {
	public static enum AxisType{
		X,
		Y,
		Y2
	}

	protected Set<Tick> ticks;
	protected TreeMap<Tick[], Color> gridFills;
	
	
	public Axis() {
		ticks = new TreeSet<Tick>();
		gridFills = new TreeMap<Tick[], Color>();
	}
	
	public void addTick(Tick tick){
		ticks.add(tick);
	}
	
	public void clearTicks(){
		ticks.clear();
	}
	
 	public void fillBetweenTicks(Tick tick1, Tick tick2, Color color){
		if(color == null)
			return;
		switch (tick1.compareTo(tick2)) {
		case 0:
			return;
		case 1:
			gridFills.put(new Tick[]{tick2, tick1}, color);
			break;
		case -1:
			gridFills.put(new Tick[]{tick1, tick2}, color);
			break;
		}
	}
	
	public void removeFill(Tick tick1, Tick tick2){
		Tick[] pair = null;
		switch (tick1.compareTo(tick2)) {
		case 0:
			return;
		case 1:
			pair = new Tick[]{tick2, tick1};
			break;
		case -1:
			pair = new Tick[]{tick1, tick2};
			break;
		}
		gridFills.remove(pair);
	}
	
}
