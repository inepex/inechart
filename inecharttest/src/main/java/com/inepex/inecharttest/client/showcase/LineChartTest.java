package com.inepex.inecharttest.client.showcase;

import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inecharttest.shared.TestLineChartCreator;

public class LineChartTest extends FlowPanel {

	public LineChartTest() {
		IneChart chart = new IneChart(800, 400);
		LineChart lc = chart.createLineChart();
		TestLineChartCreator.setupTestLines(lc);
		
		add(chart.asWidget());
		chart.update();
	}

}
