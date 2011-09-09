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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DataPoint && ((DataPoint) obj).x == this.x && ((DataPoint) obj).y == this.y)
			return true;
		else
			return false;
		
	}



	public double getX() {
		return x;
	}



	public void setX(double x) {
		this.x = x;
	}



	public double getY() {
		return y;
	}



	public void setY(double y) {
		this.y = y;
	}



	public double getActualXPos() {
		return actualXPos;
	}



	public void setActualXPos(double actualXPos) {
		this.actualXPos = actualXPos;
	}



	public double getActualYPos() {
		return actualYPos;
	}



	public void setActualYPos(double actualYPos) {
		this.actualYPos = actualYPos;
	}
	
	
}
