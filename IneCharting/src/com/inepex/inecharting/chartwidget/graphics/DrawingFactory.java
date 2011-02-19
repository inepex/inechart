package com.inepex.inecharting.chartwidget.graphics;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChart;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.event.EventManager;
import com.inepex.inecharting.chartwidget.graphics.canvas.DrawingFactoryImplCanvas;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.DrawingFactoryImplGwtGraphics;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.Point;

public abstract class DrawingFactory implements HasViewport{
	
	public static DrawingFactory instance = null;
	
	public static DrawingFactory get(){
		return instance;
	}
	public static DrawingFactory create(AbsolutePanel chartMainPanel, IneChartProperties properties, ModelManager modelManager, Axis xAxis, Axis yAxis, Axis y2Axis){
		switch (properties.getDrawingTool()) {
		case VAADIN_GWT_GRAPHICS:
			instance = new DrawingFactoryImplGwtGraphics(chartMainPanel, properties, modelManager, xAxis, yAxis, y2Axis);
			break;
		case CANVAS:
			instance = new DrawingFactoryImplCanvas(chartMainPanel, properties, modelManager, xAxis, yAxis, y2Axis);
			break;
		default:
			break;
		} 
		return instance;
	} 
	
	
	protected IneChartProperties properties;
	protected ModelManager modelManager;
	protected Widget chartCanvas;
	
	protected AbsolutePanel chartMainPanel;
	
	protected DrawingFactory(AbsolutePanel chartMainPanel, IneChartProperties properties, ModelManager modelManager, Axis xAxis, Axis yAxis, Axis y2Axis) {
		this.properties = properties;
		this.chartMainPanel = chartMainPanel;
		this.modelManager = modelManager;
		init(xAxis, yAxis, y2Axis);
	}
	
	protected abstract void init(Axis xAxis, Axis yAxis, Axis y2Axis);
	
	public abstract void assembleLayout();
	
	/**
 	 * draws a curve on canvas
 	 * @param curve
 	 */
	public abstract void addCurve(Curve curve);
	
 	/**
 	 * removes a curve from canvas
 	 * @param index
 	 */
 	public abstract void removeCurve(Curve curve);
 	
 	public Widget getChartCanvas() {
		return chartCanvas;
	}
 	
 	public abstract void drawPoints(ArrayList<Point>  points);
}