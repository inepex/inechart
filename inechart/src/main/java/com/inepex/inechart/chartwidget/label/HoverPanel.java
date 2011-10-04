package com.inepex.inechart.chartwidget.label;

import java.util.TreeMap;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.inepex.inechart.chartwidget.event.PointHoverListener;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.DataPoint;
import com.inepex.inechart.chartwidget.linechart.LineChart;

public class HoverPanel extends Composite implements PointHoverListener{

	private FlowPanel mainPanel;
	private FlexTable table;
	private int width, height;
	private LineChart lineChart;
	
	public HoverPanel(int width, int height) {
		this.height = height;
		this.width = width;
		init();
	}
	
	private void init(){
		mainPanel = new FlowPanel();
		mainPanel.setPixelSize(width, height);
		
		table = new FlexTable();
		table.setBorderWidth(1);
		table.setCellPadding(0);
		table.setCellSpacing(1);
		
		mainPanel.add(table);
		initWidget(mainPanel);
	}


	@Override
	public void onPointHover(TreeMap<Curve, DataPoint> hoveredPoints) {
		// TODO Auto-generated method stub
		
	}
}