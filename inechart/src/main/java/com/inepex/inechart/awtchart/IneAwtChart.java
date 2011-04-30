package com.inepex.inechart.awtchart;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.linechart.LineChartProperties;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inegraphics.awt.DrawingAreaAwt;

public class IneAwtChart {

	private ArrayList<IneChartModul> moduls;
	private DrawingAreaAwt drawingArea;
	
	//properties
	private static final int DEFAULT_PADDING = 30;
	private int widgetWidth;
	private int widgetHeight;
	private int canvasWidth;
	private int canvasHeight;
	
	public IneAwtChart(int width, int height) {
		canvasHeight = height - DEFAULT_PADDING / 2;
		canvasWidth = width - DEFAULT_PADDING / 2;
		widgetHeight = height;
		widgetWidth = width;

		this.drawingArea = new DrawingAreaAwt(canvasWidth,canvasHeight);
		moduls = new ArrayList<IneChartModul>();
	}
	
	/*
	 *  Moduls
	 */
	
	public LineChart createLineChart(){
		Axes axes = new Axes(drawingArea);
		LineChart chart = new LineChart(drawingArea,axes);
		moduls.add(chart);
		moduls.add(axes);
		return chart;
	}
	
	public LineChart createLineChart(LineChartProperties properties){
		LineChart chart = createLineChart();
		chart.setProperties(properties);
		return chart;
	}
	
	public PieChart createPieChart(){
		PieChart chart = new PieChart(drawingArea);
		moduls.add(chart);
		return chart;
	}
	
	/* public methods */
	public void setViewport(double startX, double stopX){
		for(IneChartModul modul : moduls)
			modul.setViewport(startX, stopX);
	}
	public void moveViewport(double dX){
		for(IneChartModul modul : moduls)
			modul.moveViewport(dX);
	}
	
	public void update(){
		//update model, create GOs per modul
		for(IneChartModul modul : moduls){
			modul.update();
			drawingArea.addAllGraphicalObject(modul.getGraphicalObjectContainer());
		}
		//draw
		drawingArea.update();
	}
	
	public void saveToFile(String filename){
		((DrawingAreaAwt)drawingArea).saveToFile(filename);
	}
}
