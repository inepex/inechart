package com.inepex.inecharting.chartwidget.newimpl;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axes;
import com.inepex.inecharting.chartwidget.newimpl.linechart.LineChart;
import com.inepex.inecharting.chartwidget.newimpl.linechart.LineChartProperties;
import com.inepex.inecharting.chartwidget.newimpl.piechart.PieChart;
import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;

public class IneChart extends Composite {

	private ArrayList<IneChartModul> moduls;
	private DrawingAreaImplCanvas drawingArea;
	private Timer updateTimer;
	private int updateInterval = DEFAULT_UPDATE_INTERVAL;
	public static final int DEFAULT_UPDATE_INTERVAL = 800;
	
	//properties
	private int canvasWidth;
	private int canvasHeight;
	
	public IneChart(int width, int height) {
		canvasHeight = height;
		canvasWidth = width;
		this.drawingArea = new DrawingAreaImplCanvas(canvasWidth,canvasHeight);
		moduls = new ArrayList<IneChartModul>();
		this.initWidget(drawingArea.getWidget());
		updateTimer = new Timer() {
			
			@Override
			public void run() {
				if(redrawNeeded())
					update();
				if(updateInterval > 0)
					updateTimer.schedule(updateInterval);
			}
		};
	}
	
	/*
	 *  Moduls
	 */
	
	public LineChart createLineChart(){
		Axes axes = new Axes(drawingArea);
		LineChart chart = new LineChart(drawingArea,axes);
		moduls.add(chart);
		moduls.add(axes);
		chart.setProperties(LineChartProperties.getDefaultLineChartProperties());
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
		drawingArea.removeAllGraphicalObject();
		for(IneChartModul modul : moduls){
			modul.update();
			drawingArea.addAllGraphicalObject(modul.graphicalObjectContainer);
		}
		//draw
		drawingArea.update();
	}
	
	boolean redrawNeeded(){
		for(IneChartModul modul : moduls){
			if(modul.redrawNeeded)
				return true;
		}
		return false;
	}
	
	public DrawingAreaImplCanvas getCanvas(){
		return this.drawingArea;
	}
	
	public void setUpdateInterval(int ms){
		this.updateInterval = ms;
		if(updateInterval > 0)
			updateTimer.schedule(updateInterval);
	}
}
