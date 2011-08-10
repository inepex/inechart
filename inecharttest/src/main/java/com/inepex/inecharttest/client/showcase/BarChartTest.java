package com.inepex.inecharttest.client.showcase;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.barchart.BarChart.BarChartType;
import com.inepex.inechart.chartwidget.barchart.BarChart.BarSequencePosition;
import com.inepex.inechart.chartwidget.label.Legend.LegendEntryLayout;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;

public class BarChartTest extends AbsolutePanel {
	DataSet d1 = DataGenerator.generateRandomData(10);
	DataSet d2 = DataGenerator.generateRandomData(10);
	DataSet d3 = DataGenerator.generateRandomData(10);
	
	public BarChartTest() {
		

		IneChart chart1 = new IneChart(800, 400);
		IneChart chart2 = new IneChart(800, 400);
		IneChart chart3 = new IneChart(800, 400);
		this.add(chart1, 0, 0);
		this.add(chart2, 0, 400);
		this.add(chart3, 0, 800);
		this.setPixelSize(800, 1200);
		
			
		BarChart bc = chart1.createBarChart();
		setUpBarChart(bc);
		bc.setBarChartType(BarChartType.Simple);
		
		bc = chart2.createBarChart();
		setUpBarChart(bc);
		bc.setBarChartType(BarChartType.Sorted);
		
		bc = chart3.createBarChart();
		setUpBarChart(bc);
		bc.setBarChartType(BarChartType.Stacked);
		

		chart1.getLegend().setVerticalPosition(VerticalPosition.Middle);
		chart1.getLegend().setHorizontalPosition(HorizontalPosition.Left);
		chart1.getLegend().setLegendEntryLayout(LegendEntryLayout.COLUMN);
		chart1.update();
		chart2.update();
		chart3.update();
		
	}
	
	private void setUpBarChart(BarChart barChart){
		barChart.addDataSet(d1);
		barChart.addDataSet(d2);
		barChart.addDataSet(d3);
		barChart.getXAxis().setAutoCreateGrids(true);
		barChart.getYAxis().setAutoCreateGrids(true);
		barChart.setBarSequencePosition(BarSequencePosition.After);
		barChart.setBarSpacing(5);
		barChart.setShadowOffsetX(3);
		barChart.setShadowOffsetY(3);
		barChart.setShadowColor(Defaults.shadowColor());
	}
}
