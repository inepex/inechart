package com.inepex.inecharttest.client.showcase;

import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inecharttest.shared.TestLineChartCreator;

public class LineAndBarChartTest extends FlowPanel {

	public LineAndBarChartTest() {
		IneChart chart = new IneChart(800, 400);
		LineChart lc = chart.createLineChart();
		TestLineChartCreator.setupTestLines(lc);
		BarChart bc = chart.createBarChart();
		TestLineChartCreator.setTestBars(bc);
		TestLineChartCreator.setUseSameViewports(lc, bc);
		add(chart.asWidget());
		chart.update();
	}

}
