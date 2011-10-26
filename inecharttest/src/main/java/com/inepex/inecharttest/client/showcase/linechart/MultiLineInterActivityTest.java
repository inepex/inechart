package com.inepex.inecharttest.client.showcase.linechart;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve2;
import com.inepex.inechart.chartwidget.linechart.PointSelectionMode;
import com.inepex.inechart.chartwidget.linechart.LineChart2;
import com.inepex.inechart.chartwidget.linechart.SimplePointSelection;
import com.inepex.inecharttest.client.showcase.DataGenerator;

public class MultiLineInterActivityTest extends FlowPanel implements ChangeHandler{

	IneChart chart;
	LineChart2 lineChart;
	ListBox pointSelectionModeBox;
	

	public MultiLineInterActivityTest() {
		chart = new IneChart(500, 400);
		this.add(chart);

		lineChart = chart.createLineChart2();
		for(int i=0; i<5; i++){
			Curve2 curve = new Curve2(DataGenerator.generateKeyValueDataSet(20));
			curve.setHasLine(true);
			curve.setHasPoints(true);
			lineChart.addCurve2(curve);
		}

		lineChart.addInteractiveModule(new SimplePointSelection());
		lineChart.setPointSelectionMode(PointSelectionMode.Closest_To_Cursor);
		
		
		pointSelectionModeBox = new ListBox();
		pointSelectionModeBox.addItem(PointSelectionMode.Closest_To_Cursor.toString());
		pointSelectionModeBox.addItem(PointSelectionMode.On_Click.toString());
		pointSelectionModeBox.addItem(PointSelectionMode.On_Right_Click.toString());
		pointSelectionModeBox.addItem(PointSelectionMode.On_Over.toString());
		pointSelectionModeBox.addChangeHandler(this);
		this.add(pointSelectionModeBox);
	}
	
	@Override
	protected void onLoad() {
		chart.update();
	}


	@Override
	public void onChange(ChangeEvent event) {
		pointSelectionModeBox.getSelectedIndex();
		if(pointSelectionModeBox.getSelectedIndex() == 0){
			lineChart.setPointSelectionMode(PointSelectionMode.Closest_To_Cursor);
		}
		else if(pointSelectionModeBox.getSelectedIndex() == 1){
			lineChart.setPointSelectionMode(PointSelectionMode.On_Click);
		}
		else if(pointSelectionModeBox.getSelectedIndex() == 2){
			lineChart.setPointSelectionMode(PointSelectionMode.On_Right_Click);
		}
		else if(pointSelectionModeBox.getSelectedIndex() == 3){
			lineChart.setPointSelectionMode(PointSelectionMode.On_Over);
		}
	}
}
