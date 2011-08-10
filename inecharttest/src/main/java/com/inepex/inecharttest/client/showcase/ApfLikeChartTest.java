package com.inepex.inecharttest.client.showcase;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.axes.Axis.AxisPosition;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.barchart.BarChart.BarSequencePosition;
import com.inepex.inechart.chartwidget.label.Legend.LegendEntryLayout;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Circle;

public class ApfLikeChartTest extends FlowPanel {

	String[] curveNames = { 
			"Airspace Infringement", 
			"Near Controlled Flight Into Terrain", 
			"Level Bust",
			"Prolonged Loss of Communications" 
			};

	Double[][] values = { 
			{ 12.0, 11.0, 8.0, 16.0, 10.0, 9.0, 16.0, 10.0, 14.0, 11.0, 8.0, 9.0 },
			{ 3.0, 6.0, 1.0, 2.0, 4.0, 1.0, 5.0, 1.0, 3.0, 3.0, 1.0, 2.0 },
			{ 0.0, 3.0, 1.0, 5.0, 4.0, 4.0, 3.0, 2.0, 7.0, 3.0, 2.0, 4.0 },
			{ 4.0, 0.0, 3.0, 6.0, 1.0, 2.0, 5.0, 1.0, 3.0, 3.0, 1.0, 2.0 },
			{ 10d, 10d, 3d, 4d,5d}


	};
	
	
	private IneChart chart;
	
	public ApfLikeChartTest() {
		init();
		this.add(chart);
		chart.update();
		getElement().getStyle().setBackgroundColor("lightyellow");
	}
	
	
	
	
	private void init() {
		chart = new IneChart(1200, 800);
		chart.setChartTitle("Potential/Near Collisions Air");
//		chart.getChartTitle().setHorizontalPosition(HorizontalPosition.Middle);
		LineChart lineChart = chart.createLineChart();
		
		//create barchart
		BarChart barChart = chart.createBarChart();
		barChart.getYAxis().setAutoCreateTicks(false);
		barChart.getXAxis().setAutoCreateTicks(false);
		barChart.setBarSequencePosition(BarSequencePosition.Over);
		barChart.setXAxis(lineChart.getXAxis());
	

		// add curves
		for (int i = 0; i < curveNames.length; i++) {
			lineChart.addCurve(new Curve(getCurve(i)));
			barChart.addDataSet(getCurve(i));
		}

		// set linechart properties
		lineChart.setAutoScaleViewport(false);
		lineChart.setAutoCalcPadding(true);
		
		//set legend properties
//		lineChart.getLegend().setLegendEntryLayout(LegendEntryLayout.AUTO);
//		lineChart.getLegend().setHorizontalPosition(HorizontalPosition.Right);
//		barChart.setShowLegend(false);

		// set axis properties
		lineChart.getYAxis().setAutoCreateTicks(false);
		lineChart.getXAxis().setAutoCreateTicks(false);
		lineChart.getYAxis().setFilterFrequentTicks(true);
		lineChart.getXAxis().setFilterFrequentTicks(true);
		lineChart.getXAxis().setMin(0);
		lineChart.getXAxis().setMax(10);
		lineChart.getYAxis().setMin(0.0);
		lineChart.getYAxis().setMax(16.0 * 1.2); // 16 is the hihghest value
		
		Axis y2Axis = new Axis();
		y2Axis.setAxisDirection(AxisDirection.Vertical_Ascending_To_Top);
		y2Axis.setAxisPosition(AxisPosition.Maximum);
		
		y2Axis.setVisible(true);
		y2Axis.setMin(0.0);
		y2Axis.setAutoCreateTicks(true);
		y2Axis.setAutoCreateGrids(false);
//		y2Axis.getLineProperties().setStyle(LineStyle.)t
		lineChart.addExtraAxis(y2Axis);

		// add y ticks
		LineProperties tickLineProperties = new LineProperties(1, new com.inepex.inechart.chartwidget.properties.Color("#A3A3A3"));

		Map<Double, String> yTicks = new TreeMap<Double, String>();
		yTicks.put(10.0, "min");
		yTicks.put(11.0, "2/3 min");
		yTicks.put(12.0, "1/3 min");
		yTicks.put(13.0, "BL Avg");
		yTicks.put(14.0, "1/3 max");
		yTicks.put(15.0, "2/3 max");
		yTicks.put(16.0, "max");
		for (Entry<Double, String> entry : yTicks.entrySet()) {
			lineChart.getYAxis().addTick(new Tick(entry.getKey(), tickLineProperties, null, 0, entry.getValue()));
		}

		// add y markings
		lineChart.getYAxis().fillBetween(
				new Tick(-1.0),
				new Tick(14.0),
				new com.inepex.inechart.chartwidget.properties.Color("#C2EBC2"));
		lineChart.getYAxis().fillBetween(
				new Tick(14.0),
				new Tick(15.0),
				new com.inepex.inechart.chartwidget.properties.Color("#FFFFAD"));
		lineChart.getYAxis().fillBetween(
				new Tick(15.0),
				new Tick(16.0),
				new com.inepex.inechart.chartwidget.properties.Color("#FFD6AD"));
		lineChart.getYAxis().fillBetween(
				new Tick(16.0),
				new Tick(16.0 * 1.2),
				new com.inepex.inechart.chartwidget.properties.Color("#FFA6A6"));
		
		// add x ticks
		String monthlyDateFormat = "MMM yyyy";
		DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(monthlyDateFormat);

		for (int i = 0; i < 12; i++) {
			String tickLabel = dateTimeFormat.format(new Date(2010 - 1900, i, 1));
			lineChart.getXAxis().addTick(new Tick(i, tickLineProperties, null, 0, tickLabel));
		}
	}
	
	private DataSet getCurve(int i) {
		DataSet dataSet = new DataSet();
		for (int j = 0; j < values[i].length; j++) {
			dataSet.addDataPair(j, values[i][j]);
		}
		dataSet.setName(curveNames[i]);
		
		return dataSet;

	}

}
