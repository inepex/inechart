package com.inepex.inecharttest.client.showcase;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;

public class SpeedTest extends FlowPanel {
	IneChart chart = null;
	TextBox chartWidthTB;
	TextBox chartHeightTB;
	TextBox pointCountTB;
	CheckBox sinusOrLine;
	CheckBox axes;
	CheckBox labels;
	CheckBox shadows;
	CheckBox points;
	CheckBox filterByXDensity;
	
	Button go;
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
		
		labels = new CheckBox("labels");
		axes = new CheckBox("axes");
		shadows = new CheckBox("shadows");
		points = new CheckBox("points");
		filterByXDensity = new CheckBox("filterX");
		sinusOrLine = new CheckBox("sinus/line");
		
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
		results.setWidget(0, 3, sinusOrLine);
		results.setWidget(0, 4, labels);
		results.setWidget(0, 5, axes);
		results.setWidget(0, 6, shadows);
		results.setWidget(0, 7, points);
		results.setWidget(0, 8, filterByXDensity);		
		results.setWidget(0, 9, go);

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
		results.setCellPadding(0);
		results.setStyleName("AlignMid");
		this.add(results);
		
//		sineCurve.setLineProperties(new LineProperties(2, new Color("#fc0")));
		
	}
	
	void go(){
		if(chart != null){
			this.remove(chart);
		}
		chart = new IneChart(Integer.parseInt(chartWidthTB.getText()), Integer.parseInt(chartHeightTB.getText()));
		chart.getDrawingArea().setCreateShadows(shadows.getValue());
		this.add(chart);
		
		if (sinusOrLine.getValue())
			sineCurve = new Curve(DataGenerator.generateSinePeriod(Integer.parseInt(pointCountTB.getText())));
		else 
			sineCurve = new Curve(DataGenerator.generatePlainData(Integer.parseInt(pointCountTB.getText())));
		LineChart lc= chart.createLineChart();
		sineCurve.getDataSet().setAllowDuplicateXes(true);
		lc.addCurve(sineCurve);
		lc.getXAxis().setAutoCreateGrids(axes.getValue());			
		lc.getYAxis().setAutoCreateGrids(axes.getValue());
		lc.getXAxis().setAutoCreateTicks(labels.getValue());
		lc.getYAxis().setAutoCreateTicks(labels.getValue());			

		sineCurve.setHasPoints(points.getValue());
//		sineCurve.addFill(0d, new Color(sineCurve.getDataSet().getColor().getColor(),0.5));
		chart.setChartTitle("Sine curve", "One period with "+Integer.parseInt(pointCountTB.getText())+" data samples");
		final long start = System.currentTimeMillis();
		chart.update();
		long end = System.currentTimeMillis();
		
		int row = results.insertRow(results.getRowCount());
		results.setWidget(row, 0, new Label(chartWidthTB.getText()));
		results.setWidget(row, 1, new Label(chartHeightTB.getText()));
		results.setWidget(row, 2, new Label(pointCountTB.getText()));
		results.setWidget(row, 3, new Label(end - start + " ms "));
		
		
	}

}
