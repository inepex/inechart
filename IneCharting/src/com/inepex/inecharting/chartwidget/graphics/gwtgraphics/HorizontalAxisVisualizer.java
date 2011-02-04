package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.graphics.AxisVisualizer;
import com.inepex.inecharting.chartwidget.graphics.HasViewport;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;

/**
 * A horizontal axis' implementation in case of using gwt-graphics as visualization tool.
 * Because the axis can be drawn with a few graphical objects, those are shown dynamicly.
 * 
 *@author Miklós Süveges / Inepex Ltd.
 */
public class HorizontalAxisVisualizer extends AxisVisualizer implements	HasViewport {

	private ModelManager modelManager;
	private Rectangle background;
	private ArrayList<Line> ticks;
	private HorizontalAxisDrawingInfo info;
	
	
	public HorizontalAxisVisualizer(Widget canvas, Axis axis, ModelManager modelManager) {
		super(canvas, axis);
		this.modelManager = modelManager;
		info = (HorizontalAxisDrawingInfo) axis.getDrawingInfo();
		init();
	}

	@Override
	public void moveViewport(double dx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		// TODO Auto-generated method stub

	}
	
	private void init(){
		background = new Rectangle(0, 0, modelManager.getChartCanvasWidth(), modelManager.getChartCanvasHeight());
		background.setFillColor(info.getBackgroundColor());
		background.setFillOpacity(1);
		background.setStrokeColor(info.getBackgroundColor());
		((DrawingArea)canvas).add(background);
		
		ticks = new ArrayList<Line>();
	}
	
	

}
