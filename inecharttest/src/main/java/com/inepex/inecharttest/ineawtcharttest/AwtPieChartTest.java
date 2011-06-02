package com.inepex.inecharttest.ineawtcharttest;

import java.util.SortedMap;
import java.util.TreeMap;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.piechart.Pie;
import com.inepex.inechart.chartwidget.piechart.PieChart;

public class AwtPieChartTest {
	
	public static void main(String[] args) {
		IneAwtChart chart = new IneAwtChart(800, 400);
		PieChart pc = chart.createPieChart();
		Pie pie = new Pie();
		pie.addData("nul", 0.01, null);
		pie.addData("first", 10.0, null);
		pie.addData("second", 20.0, null);
		pie.addData("third", 40.0, null);
		pie.addData("fourth", 80.0, null);
		pc.setPie(pie);

		chart.update();
		chart.saveToFile("chart.png");
	}
	
	public static SortedMap<String, Double> getTestData(){
		SortedMap<String, Double> data = new TreeMap<String, Double>();
		data.put("first", 10.0);
		data.put("second", 20.0);
		data.put("third", 40.0);
		data.put("fourth", 80.0);
		return data;
	}
}
