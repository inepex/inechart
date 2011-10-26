package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.Comparator;

import com.inepex.inechart.chartwidget.data.XYDataEntry;

public class DataPoint2 implements Comparable<DataPoint2>{

	public static Comparator<DataPoint2> canvasXComparator = new Comparator<DataPoint2>() {
		
		@Override
		public int compare(DataPoint2 o1, DataPoint2 o2) {
			return Double.compare(o1.canvasX, o2.canvasX);
		}
	};
	protected XYDataEntry data;
	protected ArrayList<DataPoint2> filteredPoints;
	protected double canvasX, canvasY;
	protected boolean isInViewport;

	public DataPoint2() {
		filteredPoints = new ArrayList<DataPoint2>();
	}

	public DataPoint2(XYDataEntry data) {
		filteredPoints = new ArrayList<DataPoint2>();
		this.data = data;
	}
	
	protected void addFilteredPoint(DataPoint2 dp){
		filteredPoints.add(dp);
	}

//	public void setData(XYDataEntry e){
//		this.data = e;
//	}

	public XYDataEntry getData(){
		return data;
	}

	public boolean containsHiddenData(){
		return filteredPoints.size() > 1;
	}

	public ArrayList<DataPoint2> getFilteredPoints(){
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
	public int compareTo(DataPoint2 o) {
		return data.compareTo(o.data);
	}
}
