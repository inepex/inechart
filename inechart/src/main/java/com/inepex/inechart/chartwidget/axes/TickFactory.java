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

	// constants
	static final int DEFAULT_TICK_DISTANCE_VERTICAL = 41;
	static final int DEFAULT_TICK_DISTANCE_HORIZONTAL = 82;
	static final int MINIMUM_TICK_DISTANCE_VERTICAL = 14;
	static final int MINIMUM_TICK_DISTANCE_HORIZONTAL = 29;

	int canvasHeight, canvasWidth;

	public TickFactory() {
	}

	public void autoCreateTicks(Axis axis) {
		autoCreateTicks(axis,
				(int) (0.5 * Math.sqrt(axis.isHorizontal() ? axis.modulToAlign
						.getWidth() : axis.modulToAlign.getHeight())));
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
		double start = Math.floor(axis.min / size) * size;
		int i = 0;
		
		/**
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
			i++;
		}
		for(Tick t : clearDoubleBugs(ticks)){
			axis.addTick(t);
		}

	}
	//TODO find better solution!!
	//TODO if there is not any, then report a BUG!!!
	ArrayList<Tick> clearDoubleBugs(ArrayList<Tick> ticks){
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
						int i = s.indexOf("9999");
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
						double fixed;
						if(clipD > 0)
							fixed = Double.parseDouble(plus) + clipD;
						else
							fixed = clipD - Double.parseDouble(plus);
						temp.add(new Tick(fixed));
					}
					else if(splitted[1].contains("0000")){
						int i = s.indexOf("0000");
						String clip = s.substring(0, i+1);
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

}
