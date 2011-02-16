package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.graphics.TickTextVisualizer;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;

public class VerticalAxisVisualizer extends AxisVisualizer {
	private AbsolutePanel ap;
	private TickTextVisualizer ttv;
	private ModelManager mm;
	private Curve.Axis y;
	private VerticalAxisDrawingInfo info;
	
	public VerticalAxisVisualizer(Widget canvas, AbsolutePanel ap, Axis axis, ModelManager mm, Curve.Axis y) {
		super(canvas, axis);
		this.ap = ap;
		this.mm = mm;
		this.y = y;
		ttv = new TickTextVisualizer(ap, axis, mm, false);
		info = (VerticalAxisDrawingInfo) axis.getDrawingInfo();
	}

	public AbsolutePanel getTextPositionerAbsolutePanel() {
		return ap;
	}

	public void display() {
		
		((DrawingArea)canvas).clear();
		int axisShift = 0;
		switch(info.getTickLocation()){
		case INSIDE:
			if(y.equals(Curve.Axis.Y)){
				axisShift = 0;
			}
			else if(y.equals(Curve.Axis.Y2)){
				axisShift = info.getTickLength();
			}
			else{
				return;
			}
			break;
		case OUTSIDE:
			if(y.equals(Curve.Axis.Y)){
				axisShift = info.getTickLength();
			}
			else if(y.equals(Curve.Axis.Y2)){
				axisShift = 0;
			}
			else{
				return;
			}
			break;
		case OVER:
			axisShift = info.getTickLength()/2;
			break;
		}
		Line axis = new Line(
				axisShift, 
				0,
				axisShift,
				mm.getChartCanvasHeight());
		axis.setStrokeColor(info.getAxisColor());
		axis.setStrokeOpacity(1);
		axis.setStrokeWidth(2);
		((DrawingArea)canvas).add(axis);
		double min = 0,max = 0;
		if(y.equals(Curve.Axis.Y)){
			min = mm.getyMin();
			max = mm.getyMax();
		}
		else if(y.equals(Curve.Axis.Y2)){
			min = mm.getY2Min();
			max = mm.getY2Max();
		}
		ttv.displayVerticalTicks(y);
		for(double x:ttv.getActualTicks()){
			Line tick = new Line(
					0,
					mm.calculateYWithoutPadding(x, min, max),
					info.getTickLength(),
					mm.calculateYWithoutPadding(x, min, max));
			tick.setStrokeColor(info.getTickColor());
			tick.setStrokeWidth(2);
			tick.setStrokeOpacity(1);
			((DrawingArea)canvas).add(tick);
		}
	}
	
	public void refresh(){
		canvas.getElement().setAttribute("zIndex", "3");
		canvas.getElement().setAttribute("opacity", "1");
		ap.getElement().setAttribute("zIndex", "3");
		ap.getElement().setAttribute("opacity", "1");
	}
}
