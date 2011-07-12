package com.inepex.inecharttest.client.showcase;

import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;

public class MultiLineChart extends FlowPanel{
	
	private IneChart chart;
	private LineChart lineChart;
	
	public MultiLineChart(){
		super();
		init();
		this.add(chart);
		chart.update();
	}
	
	private void init(){
		chart = new IneChart(670, 390);
		lineChart = chart.createLineChart();
		for(int i=0;i<5;i++){
			Curve c = new Curve(DataGenerator.generateRandomData(0, 10, 100, 600, 40, 15));
			c.setAutoFill(true);
			c.setHasShadow(false);
			c.setUseDefaultPointShape(true);
			lineChart.addCurve(c);
		}
		lineChart.setAutoCreateTicks(true);
		lineChart.setAutoScaleViewport(true);
		lineChart.autoCreateAxes();	
	}

}
