package com.inepex.inecharttest.client.showcase;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.barchart.BarChart.BarChartType;
import com.inepex.inechart.chartwidget.barchart.BarChart.BarSequencePosition;

public class BarChartTest extends AbsolutePanel {

	
	public BarChartTest() {
		ArrayList<Double> dataSet1 = DataGenerator.generateBarChartDataSet(-5, 5, 10);
		ArrayList<Double> dataSet2 = DataGenerator.generateBarChartDataSet(-5, 5, 10);
		ArrayList<Double> dataSet3 = DataGenerator.generateBarChartDataSet(-5, 5, 10);
		
		IneChart chart1 = new IneChart(800, 400);
		IneChart chart2 = new IneChart(800, 400);
		IneChart chart3 = new IneChart(800, 400);
		this.add(chart1, 0, 0);
		this.add(chart2, 0, 400);
		this.add(chart3, 0, 800);
		this.setPixelSize(800, 1200);
		
			
		BarChart bc = chart1.createBarChart();
		bc.setAutoCreateTicks(true);
		bc.addDataSet(dataSet1);
		bc.addDataSet(dataSet2);
		bc.addDataSet(dataSet3);
		bc.setBarSequencePosition(BarSequencePosition.After);
		bc.setBarChartType(BarChartType.Stacked);
		
		bc = chart2.createBarChart();
		bc.addDataSet(dataSet1);
		bc.addDataSet(dataSet2);
		bc.addDataSet(dataSet3);
		bc.setBarSequencePosition(BarSequencePosition.Over);
		
		bc = chart3.createBarChart();
		bc.addDataSet(dataSet1);
		bc.addDataSet(dataSet2);
		bc.addDataSet(dataSet3);
		bc.setBarSequencePosition(BarSequencePosition.Before);
		
		chart1.update();
		chart2.update();
		chart3.update();
		
	}
}
