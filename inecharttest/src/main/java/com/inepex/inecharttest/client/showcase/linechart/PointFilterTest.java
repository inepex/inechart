package com.inepex.inecharttest.client.showcase.linechart;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve2;
import com.inepex.inechart.chartwidget.linechart.LineChart2;
import com.inepex.inechart.chartwidget.linechart.PointFilter;
import com.inepex.inechart.chartwidget.linechart.PointFilter.Policy;
import com.inepex.inecharttest.client.showcase.DataGenerator;

public class PointFilterTest extends FlowPanel implements ClickHandler{

	AbsolutePanel mainPanel;
	IneChart mainChart;
	IneChart viewportSelectorChart;
	LineChart2 lineChart;
	PointFilter pointFilter;
	TextBox horizontalFilterBox;
	TextBox verticalFilterBox;
	ListBox policyBox;
	Button updateButton;
	
	public PointFilterTest() {
		init();
	}
	
	private void init(){
		
		mainChart = new IneChart(600, 490);
		lineChart = mainChart.createLineChart2();
		Curve2 curve = new Curve2(DataGenerator.generateKeyValueDataSet(6000));
		curve.setHasLine(true);
		curve.setHasPoints(true);
		lineChart.addCurve2(curve);
		pointFilter = lineChart.getPointFilter();
		viewportSelectorChart = mainChart.createViewportSelectorChart(600, 100);
		mainPanel = new AbsolutePanel();
		mainPanel.setPixelSize(700, 700);
		mainPanel.add(mainChart, 40, 20);
		mainPanel.add(viewportSelectorChart, 40, 530);
		this.add(mainPanel);
		
		HorizontalPanel hp = new HorizontalPanel();
		horizontalFilterBox = new TextBox();
		hp.add(new Label("horizontal: "));
		hp.add(horizontalFilterBox);
		verticalFilterBox = new TextBox();
		hp.add(new Label("vertical: "));
		hp.add(verticalFilterBox);
		policyBox = new ListBox();
		policyBox.addItem(Policy.lower.toString());
		policyBox.addItem(Policy.average.toString());
		policyBox.addItem(Policy.higher.toString());
		hp.add(policyBox);
		updateButton = new Button("update");
		updateButton.addClickHandler(this);
		hp.add(updateButton);
		this.add(hp);		
	}
	
	@Override
	protected void onLoad() {
		mainChart.update();
		viewportSelectorChart.update();
	}

	@Override
	public void onClick(ClickEvent event) {
		try{
			pointFilter.setHorizontalFilter(Integer.parseInt(horizontalFilterBox.getText()));
			pointFilter.setVerticalFilter(Integer.parseInt(verticalFilterBox.getText()));
		}
		catch(Exception e){
			
		}
		Policy policy = null;
		if(policyBox.getSelectedIndex() == 0){
			policy = Policy.lower;
		}
		else if(policyBox.getSelectedIndex() == 1){
			policy = Policy.average;
		}
		else if(policyBox.getSelectedIndex() == 2){
			policy = Policy.higher;
		}
		pointFilter.setPolicy(policy);
		mainChart.update();
	}
}
