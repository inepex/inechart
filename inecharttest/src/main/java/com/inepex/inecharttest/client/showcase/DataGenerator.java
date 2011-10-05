package com.inepex.inecharttest.client.showcase;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.Random;
import com.inepex.inechart.chartwidget.DataSet;

public class DataGenerator {

	
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
	
	public static DataSet generateRandomData(int sampleCount){
		DataSet dataSet = new DataSet(true, false);
		for(int sample = 0; sample < sampleCount; sample++){
			dataSet.addDataPair(sample, Random.nextDouble());
		}
		return dataSet;
	}
	
	public static DataSet generateSinePeriod(int sampleCount){
		DataSet dataSet = new DataSet(true, false);
		double x = 0;
		for(int sample = 0; sample < sampleCount; sample++){
			dataSet.addDataPair(x, Math.sin(x));
			x += Math.PI*2 / (sampleCount-1);
		}
		return dataSet;
	}
	
	public static DataSet generatePlainData(int sampleCount){
		DataSet dataSet = new DataSet(true, false);
		dataSet.addDataPair(0.0, 3.0);
		for(double sample = 1.0; sample < sampleCount-1; sample++){
			dataSet.addDataPair(sample, 1.0);
		}
		dataSet.addDataPair(sampleCount-1, 0.0);
		return dataSet;
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
