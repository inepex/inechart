package com.inepex.inechart.chartwidget.linechart;

public class DataPoint implements Comparable<DataPoint> {
	
	double x;
	double y;
	
	double actualXPos;
	double actualYPos;
	

	public DataPoint(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}



	@Override
	public int compareTo(DataPoint arg0) {
		if(arg0.x > x){
			return -1;
		}
		else if(arg0.x < x){
			return 1;
		}
		else if(arg0.y > y){
			return -1;
		}
		else if(arg0.y < y){
			return 1;
		}
		else return 0;
	}
}
