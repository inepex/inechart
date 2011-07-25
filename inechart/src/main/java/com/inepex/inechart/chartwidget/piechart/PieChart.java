package com.inepex.inechart.chartwidget.piechart;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.label.Legend;
import com.inepex.inechart.chartwidget.label.LegendEntry;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.gobjects.Arc;

public class PieChart extends IneChartModule2D{
	
	Pie pie;
	boolean showLegend = true;
	Legend legend = new Legend();

	public PieChart(DrawingArea canvas, LabelFactoryBase labelFactory, Axes axes) {
		super(canvas, labelFactory, axes);
	}

	public void setPie(Pie pie) {
		this.pie = pie;
	}
	
	@Override
	public void update() {
		if (pie == null)
			throw new RuntimeException(
					"There is nothing to show. You should call setPie on PieChart!");
		
		// calculate angles
		// create graphical objects
		Double actualAngle = 0.0;
		int basePointX = getWidth() / 2;
		int basePointY = getHeight() / 2;
		Long radius = Math
				.round((getWidth() < getHeight()) ? 
						getWidth() / 2.0 * 0.9
						: getHeight() / 2.0 * 0.9);
		double start = 0.0;
		double finish = 0.0;
		for (Slice slice : pie.slices) {
			// add arcs
			if (slice.percentage < 0.5)
				continue;
			Double angle = 360.0 * slice.percentage / 100.0;
			finish = start + angle;
			Double f1 = Math.min(90.0 - start, 90.0 - finish);
			Double f2 = Math.max(90.0 - start, 90.0 - finish);

			Arc arc = new Arc(basePointX, basePointY, 1, createSliceStrokeContext(slice), true, false,
					radius, f1, f2 - f1);
			getGraphicalObjectContainer().addGraphicalObject(arc);
			arc = new Arc(basePointX, basePointY, 1, createSliceFillContext(slice), false, true,
					radius - slice.getLookOut().getLineProperties().getLineWidth() / 2, f1, f2 - f1);
			getGraphicalObjectContainer().addGraphicalObject(arc);
			
			actualAngle += angle;
			start = finish;
		}
	}
	
	private Context createSliceFillContext(Slice slice){
		return new Context(
				slice.lookOut.getFillColor().getAlpha(),
				Defaults.colorString,
				0,
				slice.lookOut.getFillColor().getColor());
	}
	
	private Context createSliceStrokeContext(Slice slice){
		return new Context(
				slice.lookOut.getLineProperties().getLineColor().getAlpha(),
				slice.lookOut.getLineProperties().getLineColor().getColor(),
				slice.lookOut.getLineProperties().getLineWidth(),
				Defaults.colorString);
				
				
	}

	private Context getTextContext() {
		return new Context(0.5, "black", 1, "white", 0.0, 0.0, 0.0, "black");
	}

	@Override
	public List<LegendEntry> getLegendEntries() {
		ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
//		for(String key : pie.sliceMap.keySet()){
//			Slice s = pie.sliceMap.get(key);
//			entries.add(new LegendEntry(s, s.c));
//		}
		return entries;
	}

	@Override
	public boolean isShowLegend() {
		return showLegend;
	}

	@Override
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	@Override
	public Legend getLegend() {
		return legend;
	}

	@Override
	public void updateModulesAxes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}
}
