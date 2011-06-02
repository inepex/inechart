package com.inepex.inecharttest.ineawtcharttest;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inecharttest.shared.TestLineChartCreator;

public class AwtBarchartTest {

	public static void main(String[] args) {
		IneAwtChart chart = new IneAwtChart(800, 400);
		LineChart lc = chart.createLineChart();
		TestLineChartCreator.setupTestLines(lc);
		BarChart bc = chart.createBarChart();
		TestLineChartCreator.setTestBars(bc);
		//TODO: draw some kind of legend
		
		chart.update();
		
		chart.saveToFile("chart.png");
	}
}
