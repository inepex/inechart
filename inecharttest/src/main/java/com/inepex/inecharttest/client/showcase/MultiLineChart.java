package com.inepex.inecharttest.client.showcase;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;

public class MultiLineChart extends FlowPanel{
	
	private IneChart chart;
	private LineChart lineChart;
	
	public MultiLineChart(){
		super();
		init();
		this.add(chart);
		chart.getElement().getStyle().setMarginLeft(14, Unit.PX);
	}
	
	private void init(){
		chart = new IneChart(670, 390);
		chart.setName("Multi Line Chart");
		chart.setDescription("Generated data, default lookout");
		lineChart = chart.createLineChart();
		for(int i=0;i<5;i++){
			Curve c = new Curve(DataGenerator.generateRandomData(0, 10, 100, 600, 40, 15));
			c.setAutoFill(true);
			c.setHasShadow(false);
			c.setUseDefaultPointShape(true);
			lineChart.addCurve(c);
		}
		lineChart.setAutoScaleViewport(true);	
	}

	/**
	 * IE 6-8 hack
	 */
	@Override
	protected void onLoad() {
		chart.update();
		super.onLoad();
	}
	

}
