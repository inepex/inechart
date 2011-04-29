package com.inepex.inecharting.chartwidget.newimpl.axes;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.newimpl.properties.Color;
import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties;

public class Axis implements Comparable<Axis>{
	public static enum AxisType{
		X,
		Y,
		Y2
	}

	ArrayList<Tick> ticks;
	TreeMap<Tick[], Color> gridFills;
	boolean changed = true;
	AxisType type;
	LineProperties lineProperties;
	/**
	 * min and max is only matters in case of vertical (Y, Y2) axes
	 */
	double min, max;
	
	//comparing helper fields
	private static int highestComparableNo = 0; 
	private int comparableNo;
	
	
	public Axis() {
		this(LineProperties.getDefaultSolidLine());
	}
	
	public Axis(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
		ticks = new ArrayList<Tick>();
		gridFills = new TreeMap<Tick[], Color>();
		comparableNo = Axis.highestComparableNo++;
	}
	
	public void addTick(Tick tick){
		ticks.add(tick);
		changed = true;
	}
	
	public void clearTicks(){
		ticks.clear();
		changed = true;
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
		changed = true;
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
		changed = true;
	}

	/**
	 * @return the type
	 */
	public AxisType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(AxisType type) {
		this.type = type;
	}

	/**
	 * @return the lineProperties
	 */
	public LineProperties getLineProperties() {
		return lineProperties;
	}

	/**
	 * @param lineProperties the lineProperties to set
	 */
	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}
	
	public ArrayList<Tick> getVisibleTicks(double min, double max){
		java.util.Collections.sort(ticks);
		ArrayList<Tick> vTicks = new ArrayList<Tick>();
		for(Tick tick : ticks){
			if(tick.position > max)
				break;
			else if (tick.position >= min)
				vTicks.add(tick);
		}
		return vTicks;
	}

	/**
	 * @return the max
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(double min) {
		this.min = min;
	}

	@Override
	public int compareTo(Axis o) {
		return comparableNo - o.comparableNo;
//		if(type == AxisType.X){
//			if(o.type != AxisType.X)
//				return 1;
//			else if(ticks.size() > o.ticks.size()){
//				return 1;
//			}
//			else
//				return -1;
//		}
//		else if(o.type == AxisType.X){
//			return -1;
//		}
//		else if(ticks.size() > o.ticks.size()){
//			return 1;
//		}
//		else
//			return -1;
		
	}
}
