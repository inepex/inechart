package com.inepex.inecharting.chartwidget.newimpl.axes;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.newimpl.IneChartModul;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axis.AxisType;
import com.inepex.inecharting.chartwidget.newimpl.misc.LabelPositioner;
import com.inepex.inecharting.chartwidget.newimpl.misc.PositionedLabel;
import com.inepex.inecharting.chartwidget.newimpl.properties.Color;
import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties;
import com.inepex.inegraphics.impl.client.ishapes.Rectangle;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;

public class Axes extends IneChartModul {
	
	private ArrayList<Axis> axes;
	
	private TreeMap<Axis, GraphicalObjectContainer> gosPerAxis;
	private TreeMap<Axis, ArrayList<PositionedLabel>> labelsPerAxis;
	private LabelPositioner labelPositioner;

	private int zIndex = Integer.MIN_VALUE;
	
	public Axes(DrawingArea canvas, LabelPositioner labelPositioner) {
		super(canvas);
		axes = new ArrayList<Axis>();
		this.labelPositioner = labelPositioner;
		gosPerAxis = new TreeMap<Axis, GraphicalObjectContainer>(); 
		labelsPerAxis = new TreeMap<Axis,  ArrayList<PositionedLabel>>();
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
				x = 0;
				y2 = y = canvas.getHeight();
				x2 = canvas.getWidth();
				break;
			case Y:
				x = x2 = 0;
				y = 0;
				y2 = canvas.getHeight();
				break;
			case Y2:
				x = x2 = canvas.getWidth();
				y = 0;
				y2 = canvas.getHeight();
				break;
			default:
				x = x2 = y = y2 = 0;
				break;
			}
			Line axisLine = new Line(x, y, x2, y2, zIndex+1, createContext(axis.lineProperties));
			goc.addGraphicalObject(axisLine);
		}
		//ticks
		 ArrayList<PositionedLabel> pls = new ArrayList<PositionedLabel>();
		for(Tick tick : axis.getVisibleTicks(viewportMin, viewportMax)){
			switch(axis.type) {
			case X:
				gridX = x = x2 = getPositionRelativeToViewport(tick.position);
				y =  canvas.getHeight();
				y2 = y - tick.tickLength;
				gridY = 0;
				break;
			case Y:
				x = 0;
				x2 = x + tick.tickLength;
				gridY = y = y2 = getYForHorizontalTick(tick, axis);
				gridX = canvas.getWidth();
				break;
			case Y2:
				x = canvas.getWidth();
				x2 = x - tick.tickLength;
				gridY = y = y2 = getYForHorizontalTick(tick, axis);
				gridX = 0;
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
				PositionedLabel pl = new PositionedLabel(tick.tickText, x, y, false);
				labelPositioner.addLabel(pl);
				pls.add(pl);
			}
			//TODO DASHed line!!!
		}
		labelsPerAxis.put(axis, pls);
		for(Tick[] tickPair : axis.gridFills.keySet()){
			if(axis.type == AxisType.X){
				x = getPositionRelativeToViewport(tickPair[0].position);
				x2 = getPositionRelativeToViewport(tickPair[1].position);
				y = 0;
				y2 = canvas.getHeight();
			}
			else{
				x = 0;
				x2 = canvas.getWidth();
				y = getYForHorizontalTick(tickPair[0], axis);
				y2 = getYForHorizontalTick(tickPair[1], axis);
			}
			Rectangle fill = new  Rectangle(x, y, x2 - x, y2 - y, 0, zIndex, createFillContext(axis.gridFills.get(tickPair)), false, true);
			goc.addGraphicalObject(fill);
		}
		gosPerAxis.put(axis, goc);
		graphicalObjectContainer.addAllGraphicalObject(goc);
	}
	
	int getYForHorizontalTick(Tick tick, Axis axis){
		return (int) (canvas.getHeight() - (tick.position - axis.min) * canvas.getHeight() / (axis.max - axis.min));
	}
	
	Context createFillContext(Color fillColor){
		return new Context(
				fillColor.getAlpha(),
				fillColor.getColor(),
				0,
				Color.DEFAULT_COLOR,
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
		if(labelsPerAxis.get(axis)!= null){
			for(PositionedLabel pl : labelsPerAxis.get(axis)){
				labelPositioner.removeLabel(pl);
			}
		}
		gosPerAxis.remove(axis);
		labelsPerAxis.remove(axis);
	}
	
	

	
}
