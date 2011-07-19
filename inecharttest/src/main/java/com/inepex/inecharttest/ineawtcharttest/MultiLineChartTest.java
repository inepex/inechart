package com.inepex.inecharttest.ineawtcharttest;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.axes.Axis.AxisPosition;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;



public class MultiLineChartTest extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4542081093511966555L;

	private IneAwtChart chart;
	
	public MultiLineChartTest() {
		super("Multi Line Chart Test");
		setSize(700, 500);
		getContentPane().setLayout(new GridLayout(1, 1, 10, 0));
		
		getContentPane().setBackground(Color.white);
		
		init();
		getContentPane().add(new ChartPanel());
		
		chart.update();
		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(wndCloser);

		setVisible(true);
		chart.saveToFile("multiLineChart.png");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MultiLineChartTest() ;

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
	
	private void init(){
		chart = new IneAwtChart(630, 400);
		chart.setName("Multi Line Chart");
		chart.setDescription("Generated data, default lookout");
		LineChart lineChart = chart.createLineChart();
		for(int i=0;i<5;i++){
			Curve c = new Curve(generateRandomData(0, 10, 100, 600, 40, 15));
			c.setAutoFill(true);
			c.setHasShadow(false);
			c.setUseDefaultPointShape(true);
			lineChart.addCurve(c);
		}
	
		lineChart.setAutoScaleViewport(true);
	
		Axis extra = new Axis();
		extra.setAxisDirection(AxisDirection.Vertical_Ascending_To_Bottom);
		extra.setAxisPosition(AxisPosition.Maximum);
		lineChart.addExtraAxis(extra);
		
	}

	
	private class ChartPanel extends JComponent{
		public ChartPanel() {
			setSize(700, 500);
		}
		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(chart.getImage(), 0, 0, this);
		}
		
	}
}
