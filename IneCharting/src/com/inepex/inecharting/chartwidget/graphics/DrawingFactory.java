package com.inepex.inecharting.chartwidget.graphics;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.ModelManager;

public abstract class DrawingFactory implements HasViewport{
	
	protected IneChartProperties properties;
	protected ModelManager modelManager;
	protected Widget chartCanvas;
	
	protected AbsolutePanel chartMainPanel;
	
	public DrawingFactory(AbsolutePanel chartMainPanel, IneChartProperties properties, ModelManager modelManager, Axis xAxis, Axis yAxis, Axis y2Axis) {
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
 	
 	
}