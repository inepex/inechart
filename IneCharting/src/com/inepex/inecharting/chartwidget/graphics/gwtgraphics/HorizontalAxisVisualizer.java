package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.graphics.TickTextVisualizer;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.HasViewport;
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
	private TreeMap<Double, Line> ticks;
	private HorizontalAxisDrawingInfo info;
	private TickTextVisualizer ttv;
	private AbsolutePanel ap;
	
	
	public HorizontalAxisVisualizer(Widget canvas, AbsolutePanel ap, Axis axis, ModelManager modelManager) {
		super(canvas, axis);
		this.modelManager = modelManager;
		info = (HorizontalAxisDrawingInfo) axis.getDrawingInfo();
		this.ap = ap;
		init();
	}

	@Override
	public void moveViewport(double dx) {
		ttv.moveViewport(dx);
		for(double x : ttv.getActualTicks()){
			if(ticks.containsKey(x)){
				moveTick(x, -dx);
			}
			else{
				addTick(x);
			}
		}
		ArrayList<Double> toRemove = new ArrayList<Double>();
		for(double key : ticks.keySet()){
			if(!ttv.getActualTicks().contains(key))
				toRemove.add(key);
		}
		for(double x : toRemove){
			removeTick(x);
		}
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		removeAllTicks();
		ttv.setViewport(viewportMin, viewportMax);
		for(double x : ttv.getActualTicks()){
			addTick(x);
		}
	}
	
	private void init(){
		background = new Rectangle(0, info.getTickLengthInside(), modelManager.getChartCanvasWidth(), modelManager.getChartCanvasHeight());
		background.setFillColor(info.getBackgroundColor());
		background.setFillOpacity(1);
		background.setStrokeColor(info.getBackgroundColor());
		((DrawingArea)canvas).add(background);
		ttv = new TickTextVisualizer(ap, axis, modelManager, true);
		ticks = new TreeMap<Double, Line>();
	}
	
	public AbsolutePanel getTextPositionerAbsolutePanel(){
		return ap;
	}
	
	private void removeAllTicks(){
//		for(double x : ticks.keySet()){
//			Line line = ticks.get(x);
//			if(line != null && line.getParent().equals(canvas))
//				((DrawingArea)canvas).remove(line);
//		}
		((DrawingArea)canvas).clear();
		ticks.clear();
	}
	
	private void removeTick(double pos){
		Line line = ticks.get(pos);
		if(line != null && line.getParent().equals(canvas))
			((DrawingArea)canvas).remove(line);
		ticks.remove(pos);
	}
	
	private void addTick(double pos){
		int xPos = modelManager.calculateDistance(pos - modelManager.getViewportMin());
		Line l = new Line(
				xPos, 
				0,
				xPos,
				info.getTickLengthInside() + info.getTickPanelHeight());
		
		l.setStrokeColor(info.getTickColor());
		l.setStrokeOpacity(1);
		l.setStrokeWidth(1);
		ticks.put(pos, l);
		((DrawingArea)canvas).add(l);
	}
	
	private void moveTick(double pos, double dx){
		Line l = ticks.get(pos);
		l.setX1(l.getX1() + modelManager.calculateDistance(dx));
		l.setX2(l.getX2() + modelManager.calculateDistance(dx));
	}

	public HorizontalAxisDrawingInfo getInfo() {
		return info;
	}

	public void display() {
		setViewport(modelManager.getViewportMin(), modelManager.getViewportMax());
		
	}
}
