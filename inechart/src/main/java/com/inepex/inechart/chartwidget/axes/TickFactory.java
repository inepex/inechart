package com.inepex.inechart.chartwidget.axes;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;

/**
 * Calculates ticks for axes.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class TickFactory {


	public TickFactory() {
	}

	public void autoCreateTicks(Axis axis) {
		autoCreateTicks(axis,
				(int) Math.round(axis.isHorizontal() ? 
						0.4 * Math.sqrt(axis.modulToAlign.getWidth()) :
						0.6 * Math.sqrt(axis.modulToAlign.getHeight())));
	}

	public void autoCreateTicks(Axis axis, int tickNo) {
		if (axis.axisDataType == AxisDataType.Number)
			setNumberAxis(axis, tickNo);
	}

	void setNumberAxis(Axis axis, int tickNo) {
		axis.ticks.clear();
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
		 * while 0.6 would have been the good value it was 0.600000000001
		 * while debugging eclipse's 'watch expression' feature said that 0.3 * 2 = 0.60000000001 (manually typed)
		 * quite peculiar...
		 * values:
		 * start = -1, delta = 0.2222... nDelta = 2.2222..., size = 0.2
		 */
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		while (i * size + start <= axis.max) {
			ticks.add(new Tick(i * size + start));
//			axis.addTick(new Tick(i * size + start));
			i++;
		}
		for(Tick t : clearDoubleBugs(ticks,decimal)){
			axis.addTick(t);
		}
		
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
