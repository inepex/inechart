package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public class SimplePointSelection extends LineChartInteractiveModule {
	
	protected TreeMap<Curve2, Shape> selectedPointShapes;
	protected Shape defaultSelectedPointShape;
	protected TreeMap<Curve, DrawingAreaGWT> canvasPerCurve;
	
	public SimplePointSelection() {
		selectedPointShapes = new TreeMap<Curve2, Shape>();
		canvasPerCurve = new TreeMap<Curve, DrawingAreaGWT>();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void pointSelection(
			TreeMap<Curve2, ArrayList<DataPoint2>> selectedPoints,
			TreeMap<Curve2, ArrayList<DataPoint2>> deselectedPoints) {
		
		for(Curve2 c : lineChart.curves){
			if(c.hasPoint){
				boolean updated = false;
				if(selectedPoints.containsKey(c)){
					if(deselectedPoints.containsKey(c)){
						updateLayer(c, null);
						updated = true;
					}
					else{
						checkLayer(c);
						Shape shape = selectedPointShapes.get(c);
						if(shape == null){
							shape = defaultSelectedPointShape;
						}
						if(shape == null){
							shape = Defaults.selectedPoint();
							shape.getProperties().getLineProperties().getLineColor().setColor(c.dataSet.getColor().getColor());
						}
						for(DataPoint2 dp : selectedPoints.get(c)){
							canvasPerCurve.get(c).addAllGraphicalObject(lineChart.createPoint(dp, shape));
						}
						canvasPerCurve.get(c).update();
					}
				}
				if(!updated && deselectedPoints.containsKey(c)){
					updateLayer(c, null);
				}
			}
		}
		
	}
	
	protected void checkLayer(Curve2 c){
		if(!canvasPerCurve.containsKey(c)){
			moduleAssist.createLayer(lineChart);
		}
	}

	@Override
	protected void update() {
		for(Curve2 c : lineChart.curves){
			if(c.hasPoint){
				updateLayer(c, null);
			}
		}
	}
	
	protected void updateLayer(Curve2 curve, ArrayList<DataPoint2> points){
		checkLayer(curve);
		DrawingAreaGWT canvas = canvasPerCurve.get(curve);
		Shape shape = selectedPointShapes.get(curve);
		if(shape == null){
			shape = defaultSelectedPointShape;
		}
		if(shape == null){
			shape = Defaults.selectedPoint();
			shape.getProperties().getLineProperties().getLineColor().setColor(curve.dataSet.getColor().getColor());
		}
		canvas.removeAllGraphicalObjects();
		for(DataPoint2 dp : points == null ? curve.selectedPoints : points){
			if(dp.isInViewport){
				canvas.addAllGraphicalObject(lineChart.createPoint(dp, shape));
			}
		}
		canvas.update();
	}

}
