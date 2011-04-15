package com.inepex.inecharting.chartwidget.newimpl.linechart;

import java.util.Comparator;


public class Point {
	public static Comparator<Point> dataXComparator(){
		return new Comparator<Point>() {
			
			@Override
			public int compare(Point o1, Point o2) {
				if(o1.dataX - o1.dataX > 0){
					return 1;
				}
				else if (o1.dataX - o1.dataX == 0){
					return 0;
				}
				else{
					return -1;
				}
			}
			
		};
	}
	public static Comparator<Point> posXComparator(){
		return new Comparator<Point>() {
			
			@Override
			public int compare(Point o1, Point o2) {
				return o1.posX - o2.posX;
			}
		};
	}
	public static int distance(Point o1, Point o2){
		return (o2.posX - o1.posX)^2 + (o2.posY - o1.posY)^2;
	}
	private double dataX, dataY;
	private int posX, posY;
	
	public Point(double dataX, double dataY) {
		this.dataX = dataX;
		this.dataY = dataY;
	}
	
	Point(){
		
	}
	/**
	 * @return the dataX
	 */
	public double getDataX() {
		return dataX;
	}
	/**
	 * @return the dataY
	 */
	public double getDataY() {
		return dataY;
	}
	/**
	 * @return the posX
	 */
	public int getPosX() {
		return posX;
	}
	/**
	 * @return the posY
	 */
	public int getPosY() {
		return posY;
	}
	/**
	 * @param dataX the dataX to set
	 */
	public void setDataX(double dataX) {
		this.dataX = dataX;
	}
	/**
	 * @param dataY the dataY to set
	 */
	public void setDataY(double dataY) {
		this.dataY = dataY;
	}
	/**
	 * @param posX the posX to set
	 */
	public void setPosX(int posX) {
		this.posX = posX;
	}
	/**
	 * @param posY the posY to set
	 */
	public void setPosY(int posY) {
		this.posY = posY;
	}
}
