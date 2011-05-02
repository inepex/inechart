package com.inepex.inechart.chartwidget.axes;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.axes.Axis.AxisType;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.impl.client.ishapes.Rectangle;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.Text;

public class Axes extends IneChartModul {
	
	private ArrayList<Axis> axes;
	
	private TreeMap<Axis, GraphicalObjectContainer> gosPerAxis;
	private TreeMap<Axis, ArrayList<Text>> labelsPerAxis;

	private int zIndex = -100;
	
	public Axes(DrawingArea canvas) {
		super(canvas);
		axes = new ArrayList<Axis>();
		gosPerAxis = new TreeMap<Axis, GraphicalObjectContainer>(); 
		labelsPerAxis = new TreeMap<Axis,  ArrayList<Text>>();
	}

	
	
	public void addAxis(Axis axis){
		axes.add(axis);
		redrawNeeded = true; 
		
	}
	
	public void removeAxis(Axis axis){
		removeAllGOAndLabelRelatedToAxis(axis);
		gosPerAxis.remove(axis);
		labelsPerAxis.remove(axis);
		axes.remove(axis);
		redrawNeeded = true;
	}

	@Override
	public void update() {
		for(Axis axis:axes){
			if(axis.changed){
				removeAllGOAndLabelRelatedToAxis(axis);
				createGOsAndLabelsForAxis(axis);
				axis.changed = false;
			}
		}
		redrawNeeded = false;
	}
	
	void createGOsAndLabelsForAxis(Axis axis){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		int x, y, x2, y2, gridX, gridY;
		//axis' line
		if(axis.lineProperties != null){
			switch (axis.type) {
			case X:
				x = leftPadding;
				y2 = y = canvas.getHeight() - bottomPadding;
				x2 = canvas.getWidth() - rightPadding;
				break;
			case Y:
				x = x2 = leftPadding;
				y = topPadding;
				y2 = canvas.getHeight() - bottomPadding;
				break;
			case Y2:
				x = x2 = canvas.getWidth() - rightPadding;
				y = topPadding;
				y2 = canvas.getHeight() - bottomPadding;
				break;
			default:
				x = x2 = y = y2 = 0;
				break;
			}
			Line axisLine = new Line(x, y, x2, y2, zIndex+1, createContext(axis.lineProperties));
			goc.addGraphicalObject(axisLine);
		}
		//ticks
		 ArrayList<Text> pls = new ArrayList<Text>();
		for(Tick tick : axis.getVisibleTicks(viewportMin, viewportMax)){
			switch(axis.type) {
			case X:
				gridX = x = x2 = getPositionRelativeToViewport(tick.position) ;
				y =  canvas.getHeight() - bottomPadding;
				y2 = y - tick.tickLength;
				gridY = topPadding;
				break;
			case Y:
				x = leftPadding;
				x2 = x + tick.tickLength;
				gridY = y = y2 = getYForHorizontalTick(tick, axis);
				gridX = canvas.getWidth() - rightPadding;
				break;
			case Y2:
				x = canvas.getWidth();
				x2 = x - tick.tickLength;
				gridY = y = y2 = getYForHorizontalTick(tick, axis);
				gridX = leftPadding;
				break;
			default:
				x = x2 = y = y2 = gridX = gridY = 0;
				break;
			}
			if(tick.tickLine != null){
				Line tickLine = new Line(x, y, x2, y2, zIndex+1, createContext(tick.tickLine));
				goc.addGraphicalObject(tickLine);
			}
			if(tick.gridLine != null){
				Line gridLine = new Line(x2, y2, gridX, gridY, zIndex+1, createContext(tick.gridLine));
				goc.addGraphicalObject(gridLine);
			}
			if(tick.tickText != null && tick.tickText.length() > 0){
//				TODO: a general solution for text positioning
				switch(axis.type) {
				case X:
					x = x - tick.tickText.length() / 2 * 5;
					y = y + 15;
					break;
				case Y:
					x = x - tick.tickText.length() * 8;
					y = y + 3;
					break;
				case Y2:
					
					break;
				}
					
				Text pl = new Text(tick.tickText, x, y);
				goc.addGraphicalObject(pl);
			}
			//TODO DASHed line!!!
		}
		labelsPerAxis.put(axis, pls);
		for(Object[] tickPair : axis.gridFills){
			if(axis.type == AxisType.X){
				x = getPositionRelativeToViewport(((Tick)tickPair[0]).position);
				x2 = getPositionRelativeToViewport(((Tick)tickPair[1]).position);
				y = topPadding;
				y2 = canvas.getHeight() - bottomPadding;
			}
			else{
				x = leftPadding;
				x2 = canvas.getWidth() - rightPadding;
				y = getYForHorizontalTick((Tick) tickPair[0], axis);
				y2 = getYForHorizontalTick((Tick) tickPair[1], axis);
			}
			Rectangle fill = new  Rectangle(x, y-1, x2 - x, y2 - y, 0, zIndex, createFillContext((Color)tickPair[2]), false, true);
			goc.addGraphicalObject(fill);
		}
		gosPerAxis.put(axis, goc);
		graphicalObjectContainer.addAllGraphicalObject(goc);
	}
	
	int getYForHorizontalTick(Tick tick, Axis axis){
		return (int) (canvas.getHeight() - topPadding - bottomPadding - (tick.position - axis.min) * (canvas.getHeight() - topPadding - bottomPadding) / (axis.max - axis.min)) + topPadding;
	}
	
	Context createFillContext(Color fillColor){
		return new Context(
				Color.DEFAULT_ALPHA,
				Color.DEFAULT_COLOR,
				0,
				fillColor.getColor(),
				0,
				0,
				0,
				Color.DEFAULT_COLOR);
	}
	
	Context createContext (LineProperties lineProperties){
		return new Context(
				lineProperties.getLineColor().getAlpha(),
				lineProperties.getLineColor().getColor(),
				lineProperties.getLineWidth(),
				Color.DEFAULT_COLOR,
				0,
				0,
				Color.DEFAULT_ALPHA,
				Color.DEFAULT_COLOR);
	}
	
	void removeAllGOAndLabelRelatedToAxis(Axis axis){
		if(gosPerAxis.get(axis) != null){
			for(GraphicalObject go : gosPerAxis.get(axis).getGraphicalObjects()){
				graphicalObjectContainer.removeGraphicalObject(go);
			}
		}
		gosPerAxis.remove(axis);
		labelsPerAxis.remove(axis);
	}
	
	

	
}
