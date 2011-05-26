package com.inepex.inecharttest.client.showcase;


import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.impl.client.canvas.CanvasWidget;

public class SpeedTest extends FlowPanel {
	IneChart chart = null;
	TextBox chartWidthTB;
	TextBox chartHeightTB;
	TextBox pointCountTB;
	Button go;
	Curve curve;
	Curve sineCurve;
	FlexTable results;
	
	public SpeedTest() {
		KeyPressHandler validator = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char keyCode = event.getCharCode();
				
				if (!Character.isDigit(keyCode) && (keyCode != 127 || keyCode != 8)){
					((TextBox)event.getSource()).cancelKey();
				}
			}
		};
		ClickHandler clickClear = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				((TextBox)event.getSource()).setText("");				
			}
		};
	
		chartWidthTB = new TextBox();
		chartWidthTB.addKeyPressHandler(validator);
		chartWidthTB.setPixelSize(90, 16);
		chartWidthTB.setText("width");
		chartWidthTB.addClickHandler(clickClear);
//		chartWidthTB.addStyleName("AlignMid");
		
		chartHeightTB = new TextBox();
		chartHeightTB.addKeyPressHandler(validator);
		chartHeightTB.setPixelSize(90, 16);
		chartHeightTB.setText("height");
		chartHeightTB.addClickHandler(clickClear);
//		chartHeightTB.setStyleName("AlignMid");
		
		pointCountTB = new TextBox();
		pointCountTB.addKeyPressHandler(validator);
		pointCountTB.setPixelSize(90, 16);
		pointCountTB.setText("point count");
		pointCountTB.addClickHandler(clickClear);
//		pointCountTB.setStyleName("AlignMid");
		
		go = new Button("update()");
		go.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				go();
			}
		});
//		go.setStyleName("AlignMid");
		
		
		results = new FlexTable();
		results.setWidget(0, 0, chartWidthTB);
		results.setWidget(0, 1, chartHeightTB);
		results.setWidget(0, 2, pointCountTB);
		results.setWidget(0, 3, go);
		results.setWidget(1, 0, new Label("Results"));
		results.getFlexCellFormatter().setColSpan(1, 0, 4);
		results.setWidget(2, 0, new Label("Chart width"));
		results.setWidget(2, 1, new Label("Chart height"));
		results.setWidget(2, 2, new Label("Point count"));
		results.setWidget(2, 3, new Label("update() duration"));
		results.getColumnFormatter().setWidth(0, "100 px");
		results.getColumnFormatter().setWidth(1, "100 px");
		results.getColumnFormatter().setWidth(2, "120 px");
		results.getColumnFormatter().setWidth(3, "120 px");
		results.setBorderWidth(1);
		results.setCellSpacing(1);
		results.setStyleName("AlignMid");
		this.add(results);
		sineCurve = new Curve(DataGenerator.generateSine(4, 60));
//		sineCurve.setLineProperties(new LineProperties(2, new Color("#fc0")));
		
	}
	
	void go(){
		if(chart != null){
			this.remove(chart);
		}
		long start = System.currentTimeMillis();
		chart = new IneChart(Integer.parseInt(chartWidthTB.getText()), Integer.parseInt(chartHeightTB.getText()));
		chart.getDrawingArea().getCanvasWidget().translate(0.5, 0.5);
		this.add(chart);
		curve = new Curve(DataGenerator.generateRandomData(0, 0.5, -1, 1, 0.2, 50));
		LineChart lc= chart.createLineChart();
		lc.addCurve(curve);
		lc.addCurve(sineCurve);
		long start1 = System.currentTimeMillis();
		chart.update();
		long end = System.currentTimeMillis();
		
		int row = results.insertRow(results.getRowCount());
		results.setWidget(row, 0, new Label(chartWidthTB.getText()));
		results.setWidget(row, 1, new Label(chartHeightTB.getText()));
		results.setWidget(row, 2, new Label(pointCountTB.getText()));
		results.setWidget(row, 3, new Label(end - start1 + " ms "));
	}

}
