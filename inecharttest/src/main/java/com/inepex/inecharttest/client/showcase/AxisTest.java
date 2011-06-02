package com.inepex.inecharttest.client.showcase;

import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.Viewport;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.axes.Axis.AxisPosition;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Shape;

public class AxisTest extends FlowPanel {
	
	IneChart chart1;
	IneChart chart2;
	IneChart chart3;
	IneChart chart4;
	TreeMap<Double, Double> dataSet;
	
	RepeatingCommand c;
	
	public AxisTest() {
		fillDataSet();
		int chartHeight = Math.min(Window.getClientHeight()/2 - 30, 350);
		int chartWidth = Math.min(Window.getClientWidth()/2 - 15,540);
		
		chart1 = new IneChart(chartWidth, chartHeight);
		chart1.setStyleName("AxisTestChart");
		chart2 = new IneChart(chartWidth, chartHeight);
		chart2.setStyleName("AxisTestChart");
		chart3 = new IneChart(chartWidth, chartHeight);
		chart3.setStyleName("AxisTestChart");
		chart4 = new IneChart(chartWidth, chartHeight);
		chart4.setStyleName("AxisTestChart");
		
		this.add(chart1);
		this.add(chart2);
		this.add(chart3);
		this.add(chart4);
		
		LineProperties lineProperties = new LineProperties(1.5, new Color("#9900FF"));
		Shape pointOnCurve = new Circle(4, new ShapeProperties(lineProperties));
		
		Curve curve = new Curve(dataSet);
		curve.addDiscontinuity(curve.getPoint(20));
		curve.setLineProperties(lineProperties);
		curve.setNormalPointShape(pointOnCurve);
		
		final Viewport vp = new Viewport(0, -2, 22, 12);
		
		LineChart lineChart = chart1.createLineChart(vp);
		lineChart.addCurve(curve);
		lineChart.getXAxis().setAxisDirection(AxisDirection.Horizontal_Ascending_To_Left);
		lineChart.getXAxis().setAxisPosition(AxisPosition.Minimum);
		lineChart.getYAxis().setAxisDirection(AxisDirection.Vertical_Ascending_To_Top);
		lineChart.getYAxis().setAxisPosition(AxisPosition.Minimum);
		
		
		lineChart = chart2.createLineChart(vp);
		lineChart.addCurve(curve);
		lineChart.getXAxis().setAxisDirection(AxisDirection.Horizontal_Ascending_To_Right);
		lineChart.getXAxis().setAxisPosition(AxisPosition.Minimum);
		lineChart.getYAxis().setAxisDirection(AxisDirection.Vertical_Ascending_To_Top);
		lineChart.getYAxis().setAxisPosition(AxisPosition.Minimum);
		
		
		lineChart = chart3.createLineChart(vp);
		lineChart.addCurve(curve);
		lineChart.getXAxis().setAxisDirection(AxisDirection.Horizontal_Ascending_To_Left);
		lineChart.getXAxis().setAxisPosition(AxisPosition.Minimum);
		lineChart.getYAxis().setAxisDirection(AxisDirection.Vertical_Ascending_To_Bottom);
		lineChart.getYAxis().setAxisPosition(AxisPosition.Minimum);
		
		
		lineChart = chart4.createLineChart(vp);
		lineChart.addCurve(curve);
		lineChart.getXAxis().setAxisDirection(AxisDirection.Horizontal_Ascending_To_Right);
		lineChart.getXAxis().setAxisPosition(AxisPosition.Minimum);
		lineChart.getYAxis().setAxisDirection(AxisDirection.Vertical_Ascending_To_Bottom);
		lineChart.getYAxis().setAxisPosition(AxisPosition.Minimum);
		
		
//		barChart1.createBarChart();
		
		
	
		c = new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				chart1.update();
				chart2.update();
				chart3.update();
				chart4.update();
				vp.moveOnX(1);
				if(vp.getXMin() > 400)
					return false;
				return true;
			}
		};

	}
	
	@Override
	protected void onLoad() {
		Scheduler.get().scheduleFixedDelay(c, 100);
		super.onLoad();
	}
	
	@Override
	protected void onUnload() {
		super.onUnload();
	}

	void fillDataSet(){
		dataSet = new TreeMap<Double, Double>();
		for(int i = 1; i <= 400; i++){
			dataSet.put((double)i, (double)Random.nextInt(10));
		}
	}
}
