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
import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;
import com.inepex.inegraphics.impl.client.canvas.Canvas;
import com.inepex.inegraphics.impl.client.canvas.CanvasWidget;

public class IneChart extends Composite {

	private ArrayList<IneChartModul> moduls;
	private AbsolutePanel mainPanel;
	private DrawingAreaImplCanvas drawingArea;
	private EventBus eventBus;
	private Timer updateTimer;
	private int updateInterval = DEFAULT_UPDATE_INTERVAL;
	public static final int DEFAULT_UPDATE_INTERVAL = 800;
	
	//properties
	private static final int DEFAULT_PADDING = 30;
	private int widgetWidth;
	private int widgetHeight;
	private int canvasWidth;
	private int canvasHeight;
	
	public IneChart(int width, int height) {
		eventBus = new SimpleEventBus();
		canvasHeight = height - DEFAULT_PADDING / 2;
		canvasWidth = width - DEFAULT_PADDING / 2;
		widgetHeight = height;
		widgetWidth = width;
		this.mainPanel =  new AbsolutePanel();
		mainPanel.setPixelSize(widgetWidth, widgetHeight);
		this.drawingArea = new DrawingAreaImplCanvas(canvasWidth,canvasHeight);
		moduls = new ArrayList<IneChartModul>();
		mainPanel.add(drawingArea.getCanvas(), widgetWidth-canvasWidth, widgetHeight-canvasHeight);
		this.initWidget(mainPanel);
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
