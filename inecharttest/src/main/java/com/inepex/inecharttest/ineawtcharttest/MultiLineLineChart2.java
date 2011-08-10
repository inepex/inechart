package com.inepex.inecharttest.ineawtcharttest;

import java.util.TreeMap;

import javax.swing.JFrame;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;

public class MultiLineLineChart2 extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MultiLineLineChart2();

	}
	private IneAwtChart chart;
	private LineChart lineChart;
	
	
	MultiLineLineChart2(){
		chart = new IneAwtChart(670, 390);
		setSize(700, 440);
		getContentPane().add(chart);
		chart.setChartTitle("Multi Line Chart","Generated data, default lookout");
//		chart.getChartTitle().setVerticalPosition(VerticalPosition.Bottom);
//		chart.getChartTitle().setHorizontalPosition(HorizontalPosition.Middle);
		lineChart = chart.createLineChart();
		for(int i=0;i<4;i++){
			Curve c = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
			c.setAutoFill(true);
			c.setHasShadow(true);
			c.setHasPoints(true);
			lineChart.addCurve(c);
		}

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
//		pack();
		chart.update();
	}
	
	public static TreeMap<Double, Double> generateRandomData(double xFrom, double maxDiffX, double yFrom, double yTo, double maxDiffY, int count){
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		double lastX = xFrom;
		double lastY = yFrom;
		for(int i = 0; i < count; i++){
			//generate a distance from lastX
			
			double xDiff = Math.random() * maxDiffX;
			if(xDiff == 0){
				xDiff = Double.MIN_VALUE;
			}
			double yDiff = (Math.random() - 0.5) * maxDiffY * 2;
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

}
