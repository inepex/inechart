package com.inepex.inecharting.chartwidget.graphics.gwtgraphics;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.graphics.AxisVisualizer;
import com.inepex.inecharting.chartwidget.graphics.TickTextVisualizer;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.ModelManager;

public class VerticalAxisVisualizer extends AxisVisualizer {
	private AbsolutePanel ap;
	private TickTextVisualizer ttv;
	private ModelManager mm;

	public VerticalAxisVisualizer(Widget canvas, AbsolutePanel ap, Axis axis, ModelManager mm) {
		super(canvas, axis);
		this.ap = ap;
		this.mm = mm;
		ttv = new TickTextVisualizer(canvas, axis, mm, false);
	}

	public AbsolutePanel getTextPositionerAbsolutePanel() {
		return ap;
	}

	@Override
	public void display() {
		
	}
}
