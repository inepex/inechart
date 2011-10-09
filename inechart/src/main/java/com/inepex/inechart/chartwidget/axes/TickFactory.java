package com.inepex.inechart.chartwidget.axes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;

/**
 * Calculates and creates ticks for axes.
 * 
 * @author Miklós Süveges, Tibor Somodi / Inepex Ltd.
 *
 */
public abstract class TickFactory {

	protected class TickSizePair{
		double multiplier;
		TimeUnits timeUnit;
		public TickSizePair(double multiplier, TimeUnits timeUnit) {
			this.multiplier = multiplier;
			this.timeUnit = timeUnit;
		}
		public TickSizePair(TickSizePair oth){
			this.multiplier = oth.multiplier;
			this.timeUnit = oth.timeUnit;
		}
		
		public double getDurationMS(){
			return multiplier * timeUnit.durationMS;
		}
	}

	/**
	 * Approximate duration of time units in milliseconds
	 */
	protected enum TimeUnits{
		Second(1000),
		Minute(60 * Second.durationMS),
		Hour(60 * Minute.durationMS),
		Day(24 * Hour.durationMS),
		Month(30 * Day.durationMS),
		Year(365.2425 * Day.durationMS);

		private double durationMS;
		private TimeUnits(double ms) {
			this.durationMS = ms;
		}		
		public double durationMS(){
			return durationMS;
		}		
	}

	protected final ArrayList<TickSizePair> allowedTickSizePairs = new ArrayList<TickFactory.TickSizePair>();

	public TickFactory() {
		allowedTickSizePairs.add(new TickSizePair(1, TimeUnits.Second));
		allowedTickSizePairs.add(new TickSizePair(2, TimeUnits.Second));
		allowedTickSizePairs.add(new TickSizePair(5, TimeUnits.Second));
		allowedTickSizePairs.add(new TickSizePair(10, TimeUnits.Second));
		allowedTickSizePairs.add(new TickSizePair(20, TimeUnits.Second));
		allowedTickSizePairs.add(new TickSizePair(30, TimeUnits.Second));

		allowedTickSizePairs.add(new TickSizePair(1, TimeUnits.Minute));
		allowedTickSizePairs.add(new TickSizePair(2, TimeUnits.Minute));
		allowedTickSizePairs.add(new TickSizePair(5, TimeUnits.Minute));
		allowedTickSizePairs.add(new TickSizePair(10, TimeUnits.Minute));
		allowedTickSizePairs.add(new TickSizePair(20, TimeUnits.Minute));
		allowedTickSizePairs.add(new TickSizePair(30, TimeUnits.Minute));

		allowedTickSizePairs.add(new TickSizePair(1, TimeUnits.Hour));
		allowedTickSizePairs.add(new TickSizePair(2, TimeUnits.Hour));
		allowedTickSizePairs.add(new TickSizePair(4, TimeUnits.Hour));
		allowedTickSizePairs.add(new TickSizePair(6, TimeUnits.Hour));
		allowedTickSizePairs.add(new TickSizePair(8, TimeUnits.Hour));
		allowedTickSizePairs.add(new TickSizePair(12, TimeUnits.Hour));

		allowedTickSizePairs.add(new TickSizePair(1, TimeUnits.Day));
		allowedTickSizePairs.add(new TickSizePair(2, TimeUnits.Day));
		allowedTickSizePairs.add(new TickSizePair(3, TimeUnits.Day));
		allowedTickSizePairs.add(new TickSizePair(5, TimeUnits.Day));

		allowedTickSizePairs.add(new TickSizePair(0.25, TimeUnits.Month));
		allowedTickSizePairs.add(new TickSizePair(0.5, TimeUnits.Month));
		allowedTickSizePairs.add(new TickSizePair(1, TimeUnits.Month));
		allowedTickSizePairs.add(new TickSizePair(2, TimeUnits.Month));
		allowedTickSizePairs.add(new TickSizePair(3, TimeUnits.Month));
		allowedTickSizePairs.add(new TickSizePair(6, TimeUnits.Month));

		allowedTickSizePairs.add(new TickSizePair(1, TimeUnits.Year));
	}

	public abstract String formatTickText(Tick tick, AxisDataType dataType); 

	public ArrayList<Tick> filterFequentTicks(Axis axis, ArrayList<Tick> visibleTicks) {
		if (visibleTicks.size() <= 1) return visibleTicks;
		double avgTextLength = 0.0;
		double sum = 0.0;
		for (Tick tick : visibleTicks){
			sum += tick.getText().getText().length();
		}
		avgTextLength = sum / new Double(visibleTicks.size());
		double avgTextWidth = avgTextLength * 10;

		sum = 0.0;
		double avgDistanceBetweenTicks = 0.0;
		for (int i = 0; i<visibleTicks.size()-1; i++){
			if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
				double x = visibleTicks.get(i).getPosition();
				double nextX =  visibleTicks.get(i + 1).getPosition();
				sum += (axis.getModulToAlign().getCanvasX(nextX) 
						- axis.getModulToAlign().getCanvasX(x));
			} else if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
				double y = visibleTicks.get(i).getPosition();
				double nextY =  visibleTicks.get(i + 1).getPosition();
				sum += (axis.getModulToAlign().getCanvasY(y) 
						- axis.getModulToAlign().getCanvasY(nextY));
			}	
		}
		avgDistanceBetweenTicks = sum / new Double(visibleTicks.size());

		if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			avgTextWidth = 10;
		}

		ArrayList<Tick> filteredTicks = new ArrayList<Tick>();
		if (avgDistanceBetweenTicks < avgTextWidth) {
			Long ratio = Math.round(avgTextWidth / avgDistanceBetweenTicks);
			int counter = ratio.intValue();
			for (int i = 0; i<visibleTicks.size(); i++){
				if (visibleTicks.get(i).isUnfiltereble() || counter == ratio.intValue()) {
					filteredTicks.add(visibleTicks.get(i));
					counter = 0;
				}
				counter++;	
			}
		} else {
			filteredTicks.addAll(visibleTicks);
		}
		return filteredTicks;
	}

	/**
	 * Creates ticks for the given axis
	 * @param axis
	 */
	public void autoCreateTicks(Axis axis) {
		autoCreateTicks(axis,
				(int) Math.round(axis.isHorizontal() ? 
						0.345 * Math.sqrt(axis.modulToAlign.getWidth()) :
							0.476 * Math.sqrt(axis.modulToAlign.getHeight())));
	}

	/**
	 * creates ticks for the given axis with a desired tick count
	 * @param axis
	 * @param tickNo
	 */
	public void autoCreateTicks(Axis axis, int tickNo) {
		axis.ticks.clear();
		if (axis.axisDataType == AxisDataType.Number){
			setNumberAxis(axis, tickNo);
		}
		else{
			setTimeAxis(axis, tickNo);
		}
	}

	protected void setNumberAxis(Axis axis, int tickNo) {
		// delta between ticks
		double delta = (axis.max - axis.min) / tickNo;
		// log(base10)delta
		int decimal = (int) Math.floor(Math.log10(delta));
		double nDelta = Math.pow(10, decimal);
		double n = delta / nDelta; // delta > nDelta, 1<n<10
		double size;
		if (n < 1.5) {
			size = 1;
		} else if (n < 3) {
			size = 2;
		} else if (n < 7.5) {
			size = 5;
		} else {
			size = 10;
		}
		size *= nDelta;
		double start =  (Double)Math.floor(axis.min / size) * size;
		int i = 0;

		/**
		 * Double bug :
		 * Bug appeared (May 25th) incharttest/SpeedTest.java devMode:
		 * -chart height 400 px, yAxis (vertical)
		 * while 0.6 should have been the good value it was 0.600000000001
		 * while debugging eclipse's 'watch expression' feature showed that 0.3 * 2 = 0.60000000001 (manually typed)
		 * quite peculiar...
		 * values:
		 * start = -1, delta = 0.2222... nDelta = 2.2222..., size = 0.2
		 */
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		while (i * size + start <= axis.max) {
			ticks.add(new Tick(i * size + start));
			i++;
		}
		for(Tick t : clearDoubleBugs(ticks,decimal)){
			axis.addTick(t);
		}

	}

	protected int floorInBase(int n, double base){
		return (int) (base * Math.floor((double)n / base));
	}

	@SuppressWarnings("deprecation")
	protected void setTimeAxis(Axis axis, int tickNo){
		TickSizePair tickTimeAndMultiplier = new TickSizePair(1, TimeUnits.Minute);
		double delta = (axis.getMax() - axis.getMin()) / tickNo;
		Iterator<TickSizePair> tickSIt = allowedTickSizePairs.iterator();
		TickSizePair prevTickS = tickSIt.next();
		TickSizePair actualTickS;
		while(tickSIt.hasNext()){
			actualTickS = tickSIt.next();
			if(delta < (prevTickS.getDurationMS() +	actualTickS.getDurationMS()) / 2){
				tickTimeAndMultiplier = new TickSizePair(prevTickS);
				break;
			}
			prevTickS = actualTickS;
		}
		
		// special-case the possibility of several years
		if (tickTimeAndMultiplier.timeUnit.equals(TimeUnits.Year)) {
			double magn;
			double norm;
			magn = Math.pow(10, Math.floor(Math.log(delta / TimeUnits.Year.durationMS()) / Math.log(10)));
			norm = (delta / TimeUnits.Year.durationMS()) / magn;
			if (norm < 1.5){
				tickTimeAndMultiplier.multiplier = 1;
			}
			else if (norm < 3){
				tickTimeAndMultiplier.multiplier = 2;
			}
			else if (norm < 7.5){
				tickTimeAndMultiplier.multiplier = 5;
			}
			else{
				tickTimeAndMultiplier.multiplier = 10;
			}
			tickTimeAndMultiplier.multiplier *= magn;
		}

		double step = tickTimeAndMultiplier.getDurationMS();
		Date date = new Date((long) axis.getMin());

		switch (tickTimeAndMultiplier.timeUnit) {
		case Second:
			date.setSeconds((int) floorInBase(date.getSeconds(), tickTimeAndMultiplier.multiplier));
			break;
		case Minute:
			date.setMinutes((int) floorInBase(date.getMinutes(), tickTimeAndMultiplier.multiplier));
			break;
		case Hour:
			date.setHours((int) floorInBase(date.getHours(), tickTimeAndMultiplier.multiplier));
			break;
		case Month:
			date.setMonth((int) floorInBase(date.getMonth(), tickTimeAndMultiplier.multiplier));
			break;
		case Year:
			date.setYear((int) floorInBase(date.getYear() + 1900, tickTimeAndMultiplier.multiplier) - 1900);
			break;
		}
		date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
		
		if (step >= TimeUnits.Minute.durationMS()){
			date.setSeconds(0);
		}
		if (step >= TimeUnits.Hour.durationMS()){
			date.setMinutes(0);
		}
		if (step >= TimeUnits.Day.durationMS()){
			date.setHours(0);
		}
		if (step >= TimeUnits.Day.durationMS() * 4){
			date.setDate(1);
		}
		if (step >= TimeUnits.Year.durationMS()){
			date.setMonth(0);
		}

		double carry = 0, v = Double.NaN, prev;
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		do {
			prev = v;
			v = date.getTime();
			ticks.add(new Tick(v));
			if (tickTimeAndMultiplier.timeUnit.equals(TimeUnits.Month)) {
				if (tickTimeAndMultiplier.multiplier < 1) {
					date.setDate(1);
					double start = date.getTime();
					date.setMonth(date.getMonth() + 1);
					double end = date.getTime();
					date.setTime((long) (v + carry * TimeUnits.Hour.durationMS() + (end - start) * tickTimeAndMultiplier.multiplier));
					carry = date.getHours();
					date.setHours(0);
				}
				else{
					date.setMonth((int) (date.getMonth() + tickTimeAndMultiplier.multiplier));
				}
			}
			else if (tickTimeAndMultiplier.timeUnit.equals(TimeUnits.Year)) {
				date.setYear((int) (date.getYear() + tickTimeAndMultiplier.multiplier));
			}
			else{
				date.setTime((long) (v + step));
			}
		} 
		while (v < axis.getMax() && v != prev);
		
		if(axis.defaultTick == null || axis.defaultTick.formatString == null || axis.defaultTick.formatString.length() == 0){
			String formatString = "";
			double totalDomain = axis.getMax() - axis.getMin();
			if(step < TimeUnits.Minute.durationMS()){
				formatString = "mm:ss";
			}
			else if(step < TimeUnits.Hour.durationMS()){
				formatString = "H:mm";
			}
			else if(step < TimeUnits.Day.durationMS()){
				if(totalDomain < 2 * TimeUnits.Day.durationMS()){
					formatString = "H:mm";
				}
				else{
//					formatString = "dd k:mm";
					formatString = "MMM d. H:mm";
				}
			}
			else if(step < TimeUnits.Month.durationMS()){
				formatString = "MMM d.";
			}
			else if(step < TimeUnits.Year.durationMS()){
				if(totalDomain < TimeUnits.Year.durationMS()){
					formatString = "MMM";
				}
				else{
					formatString = "MMM yyyy";
				}
			}
			else{
				formatString = "yyyy";
			}
			for(Tick t : ticks){
				t.setFormatString(formatString);
			}
		}
		
		axis.setTicks(ticks);
	}






	/*
	 *
	 *  Hacks Part
	 *  Does not contain any valuable code.
	 *  
	 */

	/**
	 * Does NOT work.
	 * @param ticks
	 * @param decimal
	 * @return
	 */
	ArrayList<Tick> clearDoubleBugs2(ArrayList<Tick> ticks, int decimal){
		for(Tick t : ticks){
			String[] splitted = Double.toString(t.position).split("\\.");
			double fixed = t.position;
			if(splitted[1].contains("9999")){
				fixed = ((float)Math.pow(10f,decimal))*Math.round(((float)Math.pow(10,-decimal)*t.position));

			}
			else if(splitted[1].contains("0000")){
				if(fixed < 0){
					fixed =  ((float)Math.pow(10,decimal))*Math.ceil(Math.pow(10,-decimal)*t.position);
				}
				else{
					fixed =  ((float)Math.pow(10,decimal))*Math.floor(Math.pow(10,-decimal)*t.position);
				}
			}
			t.setPosition(fixed);			
		}
		return ticks;
	}

	ArrayList<Tick> clearDoubleBugs(ArrayList<Tick> ticks, int decimal){
		ArrayList<Tick> temp = new ArrayList<Tick>();
		TreeMap<Tick, String> toStringMap = new TreeMap<Tick, String>();
		int minCharCount = Integer.MAX_VALUE;
		for(Tick t : ticks){
			String doubleString = Double.toString(t.position);
			if(doubleString.length() < minCharCount)
				minCharCount = doubleString.length();
			toStringMap.put(t, doubleString);
		}
		for(Tick t : ticks){
			String s = toStringMap.get(t);
			if(s.length() > minCharCount + 3){
				String[] splitted = s.split("\\.");
				if(splitted.length == 2){
					if(splitted[1].contains("9999")){
						temp.add(new Tick(secondFix9999(t.position)));
					}
					else if(splitted[1].contains("0000")){
						int i = s.indexOf("0000");
						String clip = s.substring(0, i+1);
						if(s.contains("E")){
							clip += s.substring(s.indexOf("E"),s.length());
						}
						temp.add(new Tick(Double.parseDouble(clip)));
					}
					else{
						temp.add(t);
					}
				}
				else{
					temp.add(t);
				}
			}
			else{
				temp.add(t);
			}			
		}
		return temp;
	}

	Double fix9999(double toFix, int decimal){
		String s = ((Double)toFix).toString();
		int i = s.indexOf("9999");
		if(i == -1)
			return toFix;
		String clip = s.substring(0, i+1);
		String plus = "";
		int j = 0;
		for(Character c : clip.toCharArray()){
			if(j++ == i)
				plus += "1";
			else
				plus += c == '.' ? "." : "0";
		}
		double clipD = Double.parseDouble(clip);
		Double fixed;
		if(clipD > 0)
			fixed = Double.parseDouble(plus) + clipD;
		else
			fixed = clipD - Double.parseDouble(plus);
		//		if(fixed.toString().contains("9999")){
		//			fixed = (double) (((float)Math.pow(10f,decimal))*Math.round(((float)Math.pow(10,-decimal)*fixed)));
		//		}
		if(fixed.toString().contains("0000")){
			int k = fixed.toString().indexOf("0000");
			String clip2 = fixed.toString().substring(0, k+1);
			fixed = Double.parseDouble(clip2);
		}

		return fixed;
	}

	Double secondFix9999(double toFix){
		String s = ((Double)toFix).toString();
		String sci = null;
		if(s.contains("E")){
			sci = s.substring(s.indexOf("E"),s.length());
			s = s.substring(0,s.indexOf("E"));
		}
		s = s.substring(0,s.lastIndexOf("9999"));
		char[] chars = s.toCharArray();
		StringBuffer ret = new StringBuffer();
		boolean plusOne = true;
		for(int j=chars.length-1;j>=0;j--){
			Character ch = chars[j];
			String toAdd;
			if(Character.isDigit(ch)){
				Integer value = Integer.parseInt(ch.toString());
				if(plusOne){
					value++;
				}
				if(value == 10){
					plusOne = true;
					toAdd = "0";
				}
				else{
					plusOne = false;
					toAdd = value.toString();
				}
			}
			else{
				toAdd = ch.toString();
			}
			ret.insert(0, toAdd);
		}
		if(plusOne){
			if(Character.isDigit(ret.charAt(0))){
				ret.insert(0, "1");
			}
			else{
				ret.insert(1, "1");
			}
		}
		if(sci != null){
			ret.append(sci);
		}
		return Double.parseDouble(ret.toString());
	}
}
