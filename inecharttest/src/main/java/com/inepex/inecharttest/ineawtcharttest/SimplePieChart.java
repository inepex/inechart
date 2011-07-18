package com.inepex.inecharttest.ineawtcharttest;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.piechart.Pie;
import com.inepex.inechart.chartwidget.piechart.PieChart;

public class SimplePieChart extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4542081093511966555L;

	private IneAwtChart chart;
	
	public SimplePieChart() {
		super("Simple Pie Chart");
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
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SimplePieChart() ;

	}
		
	private void init(){
		chart = new IneAwtChart(630, 400);
		chart.setName(new StyledLabel("Pie Chart"));
//		chart.setDescription(new StyledLabel("Generated data, default lookout"));
		PieChart pieChart = chart.createPieChart();
		Pie pie = new Pie();
		pieChart.setPie(pie);
		pie.setData(generateData());
		
	}
	
	SortedMap<String, Double> generateData(){
		SortedMap<String, Double> data = new TreeMap<String, Double>();
		for(int i=0;i<7;i++){
			data.put(i+"data", Math.random()*50);
		}
		return data;
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
