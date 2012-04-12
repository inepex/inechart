package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.Comparator;

import com.inepex.inechart.chartwidget.data.XYDataEntry;

public class DataPoint implements Comparable<DataPoint>{

	public static Comparator<DataPoint> canvasXComparator = new Comparator<DataPoint>() {
		
		@Override
		public int compare(DataPoint o1, DataPoint o2) {
			return Double.compare(o1.canvasX, o2.canvasX);
		}
	};
	
	protected class SimpleXYDataEntry implements XYDataEntry{
		double x, y;
		
		public SimpleXYDataEntry(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(XYDataEntry o) {
			int i = Double.compare(x, o.getX());
			if(i == 0){
				i = Double.compare(y, o.getY());
			}
			return i;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
	}
		
	protected XYDataEntry data;
	protected ArrayList<DataPoint> filteredPoints;
	protected DataPoint bestMatchingFilteredPoint;
	protected double canvasX, canvasY;
	protected boolean isInViewport;
	protected boolean unfilterable = false;

	public DataPoint() {
		filteredPoints = new ArrayList<DataPoint>();
	}
	
	public DataPoint(double x, double y) {
		filteredPoints = new ArrayList<DataPoint>();
		data = new SimpleXYDataEntry(x, y);
	}
	
	
	public DataPoint(XYDataEntry data) {
		filteredPoints = new ArrayList<DataPoint>();
		this.data = data;
	}
	
	protected void addFilteredPoint(DataPoint dp){
		filteredPoints.add(dp);
	}


	public XYDataEntry getData(){
		return data;
	}

	public boolean containsHiddenData(){
		return filteredPoints.size() > 1;
	}

	public ArrayList<DataPoint> getFilteredPoints(){
		return filteredPoints;
	}
	
	
	public boolean isInViewport() {
		return isInViewport;
	}

	public double getCanvasX() {
		return canvasX;
	}

	public double getCanvasY() {
		return canvasY;
	}

	@Override
	public int compareTo(DataPoint o) {
		return data.compareTo(o.data);
	}
	
	public XYDataEntry getFirstUnderlyingData(){
		return containsHiddenData() ? 
			getFilteredPoints().get(0).getData() :
			getData();
	}

	public DataPoint getBestMatchingFilteredPoint() {
		return bestMatchingFilteredPoint;
	}

	
}
