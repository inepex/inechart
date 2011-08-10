package com.inepex.inecharttest.client.showcase;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;

public class ViewportSelectorChartTest extends FlowPanel {

	AbsolutePanel mainPanel;
	IneChart mainChart;
	IneChart viewportSelectorChart;
	
	public ViewportSelectorChartTest() {
		init();
	}
	
	private void init(){
		
		mainChart = new IneChart(600, 490);
		LineChart lineChart = mainChart.createLineChart();
		
		Curve sineCurve = new Curve(DataGenerator.generateSinePeriod(100));
		sineCurve.setAutoFill(true);
		Curve randomCurve = new Curve(DataGenerator.generateRandomData(10));
		randomCurve.setHasPoints(true);
		
		lineChart.addCurve(sineCurve);
		lineChart.addCurve(randomCurve);
//		lineChart.getXAxis().setAutoCreateGrids(true);
//		lineChart.getYAxis().setAutoCreateGrids(true);
		
		viewportSelectorChart = mainChart.createViewportSelectorChart(600, 100);
		
		mainPanel = new AbsolutePanel();
		mainPanel.setPixelSize(700, 700);
		mainPanel.add(mainChart, 40, 20);
		mainPanel.add(viewportSelectorChart, 40, 530);
		this.add(mainPanel);
	}
	
	@Override
	protected void onLoad() {
		mainChart.update();
		viewportSelectorChart.update();
	}
}
