package com.inepex.inecharttest.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.Random;

public class DataGenerator {


//	
//	public static Map<Long, Double> createSpeedDataSetForChart(List<GwtReport> reportList){
//		TreeMap<Long, Double> map = new TreeMap<Long, Double>();
//		for(GwtReport report:reportList)
//			map.put(report.getDateTime().getTime(), report.getSpeed());
//		return map;
//	}
//	
//	public static TreeMap<Double, Double> createSpeedDataSetForIneChart(List<GwtReport> reportList){
//		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
//		for(GwtReport report:reportList)
//			map.put((double) report.getDateTime().getTime(), report.getSpeed());
//		return map;
//	}
	
	
	@SuppressWarnings("deprecation")
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
	
	public static TreeMap<Double, Double> generateRandomDataForChart(int from, int to, int maxDiffBetweenPoints){
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		double actual = from;
		while(actual < to) {
			actual  += Random.nextInt(maxDiffBetweenPoints);
			map.put( actual, Random.nextDouble()*500);
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
}
