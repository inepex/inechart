package com.inepex.inecharttest.client.showcase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.thirdparty.guava.common.primitives.UnsignedBytes;
import com.google.gwt.user.client.Random;

public class DataGenerator {

	public static TreeMap<Double, Double> generateRandomData(int sampleCount){
		return generateRandomData(0, 2, -10, 10, 5, sampleCount);
	}
	
	/**
	 * Generates random double data pairs (x,y).
	 * @param xFrom exclusive x
	 * @param maxDiffX positive double, maximum difference between two neighbour x
	 * @param yFrom inclusive bottom cap for y values (yFrom < yTo)
	 * @param yTo inclusive top cap for y values (yFrom < yTo)
	 * @param maxDiffY maximum difference between two neighbour y
	 * @param count the samplecount
	 * @return
	 */
	public static TreeMap<Double, Double> generateRandomData(double xFrom, double maxDiffX, double yFrom, double yTo, double maxDiffY, int count){
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		double lastX = xFrom;
		double lastY = yFrom;
		for(int i = 0; i < count; i++){
			//generate a distance from lastX
			double xDiff = Random.nextDouble() * maxDiffX;
			if(xDiff == 0){
				xDiff = Double.MIN_VALUE;
			}
			double yDiff = (Random.nextDouble() - 0.5) * maxDiffY * 2;
			if(lastY + yDiff > yTo){
				yDiff = yTo - lastY;
			}
			else if(lastY + yDiff < yFrom){
				yDiff = lastY - yFrom;
			}
			lastX += xDiff;
			lastY += yDiff;
			map.put(lastX, lastY);
		}
		return map;
	}

	public static TreeMap<Double, Double> generateRandomSpeedDataForChart(long start, long end, int maxSpeed){
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		long maxTimeDiff = 5;
		long actualTime = start;
		while(actualTime < end) {
			actualTime  += Random.nextInt((int) maxTimeDiff);
			map.put((double) actualTime, Random.nextDouble()*maxSpeed);
		}
		return map ;
	}
	
	public static ArrayList<Double> generateMarkDataForChart(long start, long end){
		double min = start;
		ArrayList<Double> vs = new ArrayList<Double>();
		while(min < end){
			vs.add(min);
			min += Random.nextDouble() * 1000 * 60 * 60 * 4;
		}
		return vs;
	}

	public static TreeMap<Double, Double> generateSine(int periodCount, int samplesInPeriod){
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		double x = 0;
		for(int period = 0; period < periodCount; period++){
			for(int sample = 0; sample < samplesInPeriod; sample++){
				map.put(x, Math.sin(x));
				x += Math.PI*2 / samplesInPeriod;
			}
		}
		return map;
	}

	public static ArrayList<Double> generateBarChartDataSet(double min, double max, int sampleCount){
		ArrayList<Double> data = new ArrayList<Double>();
		for(int i=0; i<sampleCount;i++){
			data.add(Random.nextDouble() * (max - min) + min);
		}
		return data;
	}
	public static ArrayList<Double> generateBarChartDataSet(int min, int max, int sampleCount){
		ArrayList<Double> data = new ArrayList<Double>();
		for(int i=0; i<sampleCount;i++){
			data.add((double) (Random.nextInt(max-min+1) + min));
		}
		return data;
	}
}
