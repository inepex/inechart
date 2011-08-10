package com.inepex.inecharttest.ineawtcharttest;

import java.util.TreeMap;

import javax.swing.JFrame;
import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;



public class MultiLineChartTest extends JFrame{

	private IneAwtChart chart;
	private LineChart lineChart;
	private Curve curve0;
	private Curve curve1;
	private Curve curve2;
	private Curve curve3;
	private Curve curve4;

	public MultiLineChartTest() {
		init();
		getContentPane().add(chart);
	}

	private void init(){
		chart = new IneAwtChart(670, 490);
		chart.setChartTitle("Multi Line Chart","Generated data, default lookout");
		//		chart.getChartTitle().setVerticalPosition(VerticalPosition.Bottom);
		//		chart.getChartTitle().setHorizontalPosition(HorizontalPosition.Middle);
		lineChart = chart.createLineChart();
		lineChart.getYAxis().setAutoCreateGrids(true);
		curve0 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve0.setHasShadow(true);
		curve0.setHasPoints(true);
		lineChart.addCurve(curve0);
		curve1 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve1.setHasShadow(true);
		curve1.setHasPoints(true);
		lineChart.addCurve(curve1);
		curve2 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve2.setHasShadow(true);
		curve2.setHasPoints(true);
		lineChart.addCurve(curve2);
		curve3 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve3.setHasShadow(true);
		curve3.setHasPoints(true);
		lineChart.addCurve(curve3);
		curve4 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve4.setHasShadow(true);
		curve4.setHasPoints(true);
		lineChart.addCurve(curve4);		

		
	}




	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MultiLineChartTest() ;
//		System.exit(-1);
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
	
//	private void init(){
//		chart = new IneAwtChart(700, 500);
//		chart.setName("Multi Line Chart");
//		chart.setDescription("Generated data, default lookout");
//		LineChart lineChart = chart.createLineChart();
//		for(int i=0;i<5;i++){
//			Curve c = new Curve(generateRandomData(0, 10, 100, 600, 40, 15));
//			c.setName("long name curve NO: "+i);
//			c.setAutoFill(true);
//			c.setHasShadow(false);
//			lineChart.addCurve(c);
//		}
//		lineChart.setAutoScaleViewport(true);
//		Axis extra = new Axis();
//		extra.setAxisDirection(AxisDirection.Vertical_Ascending_To_Bottom);
//		extra.setAxisPosition(AxisPosition.Maximum);
//		lineChart.addExtraAxis(extra);
//		
//	}

	
//	private class ChartPanel extends JComponent{
//		public ChartPanel() {
//			setSize(700, 500);
//		}
//		@Override
//		protected void paintComponent(Graphics g) {
//			g.drawImage(chart.getImage(), 0, 0, this);
//			
//		}
//	}
}
