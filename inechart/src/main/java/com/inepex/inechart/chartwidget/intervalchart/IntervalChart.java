package com.inepex.inechart.chartwidget.intervalchart;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.data.IntervalDataSet;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;

public class IntervalChart extends IneChartModule2D {

	private ArrayList<IntervalDataSet> dataSets;

	public IntervalChart(ModuleAssist moduleAssist) {
		super(moduleAssist);
		dataSets = new ArrayList<IntervalDataSet>();
	}

	public void addDataSet(IntervalDataSet dataSet){
		dataSets.add(dataSet);
	}

	@Override
	public TreeMap<String, Color> getLegendEntries() {
		TreeMap<String, Color> entries = new TreeMap<String, Color>();
		for(IntervalDataSet ds : dataSets){
			entries.put(ds.getTitle(), ds.getColor());
		}
		return entries;
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
	protected void onMouseOver(MouseEvent<?> event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseOut(MouseEvent<?> event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preUpdateModule() {
		if(autoScaleViewport){
			double yMin = 0;
			double yMax = -Double.MAX_VALUE;
			double xMin = Double.MAX_VALUE;
			double xMax = -Double.MAX_VALUE;
			for(IntervalDataSet dataSet : dataSets){
				for(double[] interval : dataSet.getIntervals()){
					if (interval[1] > xMax)
						xMax = interval[1];
					if (interval[0] < xMin)
						xMin = interval[0];
				}
				if (dataSet.getyValue() > yMax)
					yMax = dataSet.getyValue();
			}
			xAxis.setMax(xMax);
			yAxis.setMax(yMax);
			xAxis.setMin(xMin);
			yAxis.setMin(yMin);
			autoScaleViewport = false;
		}
		super.preUpdateModule();
	}

	@Override
	public void update() {
		graphicalObjectContainer.removeAllGraphicalObjects();
		for(IntervalDataSet dataSet : dataSets){
			graphicalObjectContainer.addAllGraphicalObject(createInterval(dataSet));
		}
		super.update();
	}

	protected GraphicalObjectContainer createInterval(IntervalDataSet dataSet){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		double left, top, height, width;
		for(double[] interval : dataSet.getIntervals()){
			if(interval[0] > xAxis.getMax() || interval[1] < xAxis.getMin()){
				continue;
			}
			left = Math.max(getCanvasX(interval[0]), leftPadding);
			top = Math.max(getCanvasY(dataSet.getyValue()), topPadding);
//			height = getCanvasDistanceY(Math.abs(dataSet.getyValue()));
			height = Math.abs(getCanvasY(dataSet.getyValue()) - getCanvasY(0));
			width = Math.min(getCanvasX(interval[1]), leftPadding + getWidth()) - left;
			Rectangle r = new Rectangle(width, height, left, top, dataSet.getShapeProperties());
			r.setRoundedCornerR(dataSet.getRoundedCornerRadius());
			goc.addAllGraphicalObject(r.toGraphicalObjects());
		}
		return goc;
	}

	@Override
	protected void onDataSetChange(DataSetChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSelect(DataEntrySelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDeselect(DataEntrySelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
