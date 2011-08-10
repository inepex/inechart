package com.inepex.inecharttest.client.showcase;

import java.util.TreeMap;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;

public class MultiLineChart extends FlowPanel{

	private IneChart chart;
	private LineChart lineChart;
	private HorizontalPanel modPanel;
	private Curve curve0;
	private Curve curve1;
	private Curve curve2;
	private Curve curve3;
	private Curve curve4;

	public MultiLineChart(){
		super();
		init();
		chart.getElement().getStyle().setMarginLeft(14, Unit.PX);
	}

	private void init(){
		chart = new IneChart(670, 490);
		chart.setChartTitle("Multi Line Chart","Generated data, default lookout");
		lineChart = chart.createLineChart();
		lineChart.getYAxis().setAutoCreateGrids(true);
		lineChart.getXAxis().setAxisLabel("x Axis");
		lineChart.getYAxis().setAxisLabel("y<br> <br>A<br>x<br>i<br>s");
		curve0 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve0.setHasShadow(true);
		curve0.setHasPoints(true);
		lineChart.addCurve(curve0);
		curve1 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve1.setHasShadow(true);
		curve1.setHasPoints(true);
		lineChart.addCurve(curve1);
		curve2 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve2.setHasShadow(true);
		curve2.setHasPoints(true);
		lineChart.addCurve(curve2);
		curve3 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve3.setHasShadow(true);
		curve3.setHasPoints(true);
		lineChart.addCurve(curve3);
		curve4 = new Curve(new DataSet(generateRandomData(0, 10, 100, 600, 40, 15)));
		curve4.setHasShadow(true);
		curve4.setHasPoints(true);
		lineChart.addCurve(curve4);		

		modPanel = new HorizontalPanel();
		final CheckBox cb0 = new CheckBox(curve0.getDataSet().getName());
		cb0.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				curve0.setAutoFill(cb0.getValue());
			}
		});
		final CheckBox cb1 = new CheckBox(curve1.getDataSet().getName());
		cb1.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				curve1.setAutoFill(cb1.getValue());
			}
		});
		final CheckBox cb2 = new CheckBox(curve2.getDataSet().getName());
		cb2.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				curve2.setAutoFill(cb2.getValue());
			}
		});
		final CheckBox cb3 = new CheckBox(curve3.getDataSet().getName());
		cb3.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				curve3.setAutoFill(cb3.getValue());
			}
		});
		final CheckBox cb4 = new CheckBox(curve4.getDataSet().getName());
		cb4.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				curve4.setAutoFill(cb4.getValue());
			}
		});
		final Button updateButton = new Button("update", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				chart.update();
			}
		});
		modPanel.add(new Label("Fills: "));
		modPanel.add(cb0);
		modPanel.add(cb1);
		modPanel.add(cb2);
		modPanel.add(cb3);
		modPanel.add(cb4);
		modPanel.add(updateButton);
		
		this.add(chart);
		this.add(modPanel);
	}



	public static TreeMap<Double, Double> generateRandomData(double xFrom, double maxDiffX, double yFrom, double yTo, double maxDiffY, int count){
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		double lastX = xFrom;
		double lastY = yFrom;
		for(int i = 0; i < count; i++){
			//generate a distance from lastX

			double xDiff = Math.random() * maxDiffX;
			if(xDiff == 0){
				xDiff = Double.MIN_VALUE;
			}
			double yDiff = (Math.random() - 0.5) * maxDiffY * 2;
			if(lastY + yDiff > yTo){
				yDiff = yTo - lastY;
			}
			else if(lastY + yDiff < yFrom){
				yDiff = lastY - yFrom;
			}
			lastX += xDiff;
			lastY += yDiff;
			map.put(lastX, lastY);
		}
		return map;
	}

	/**
	 * IE 6-8 hack
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		chart.update();
	}


}
