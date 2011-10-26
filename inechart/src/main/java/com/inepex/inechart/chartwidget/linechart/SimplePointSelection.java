package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class SimplePointSelection extends LineChartInteractiveModule {
	
	protected TreeMap<Curve2, Shape> selectedPointShapes;
	protected Shape defaultSelectedPointShape;
	protected TreeMap<Curve2, Layer> canvasPerCurve;
	protected TreeMap<Curve2, ArrayList<GraphicalObject>> registeredInteractivePoints;
	
	public SimplePointSelection() {
		selectedPointShapes = new TreeMap<Curve2, Shape>();
		canvasPerCurve = new TreeMap<Curve2, Layer>();
		registeredInteractivePoints = new TreeMap<Curve2, ArrayList<GraphicalObject>>();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
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
						if(!registeredInteractivePoints.containsKey(c)){
							registeredInteractivePoints.put(c, new ArrayList<GraphicalObject>());
						}
						for(DataPoint2 dp : selectedPoints.get(c)){
							canvasPerCurve.get(c).getCanvas().addAllGraphicalObject(lineChart.createPoint(c, dp, shape, true, registeredInteractivePoints.get(c)));
						}
						canvasPerCurve.get(c).getCanvas().update();
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
			Layer lyr = new Layer(Layer.TO_TOP);
			moduleAssist.addCanvasToLayer(lyr);
			lineChart.linkedLayersPerCurve.get(c).addLayer(lyr);
			canvasPerCurve.put(c, lyr);
			moduleAssist.updateLayerOrder();
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
		DrawingAreaGWT canvas = canvasPerCurve.get(curve).getCanvas();
		Shape shape = selectedPointShapes.get(curve);
		if(shape == null){
			shape = defaultSelectedPointShape;
		}
		if(shape == null){
			shape = Defaults.selectedPoint();
			shape.getProperties().getLineProperties().getLineColor().setColor(curve.dataSet.getColor().getColor());
		}
		canvas.removeAllGraphicalObjects();
		if(!registeredInteractivePoints.containsKey(curve)){
			registeredInteractivePoints.put(curve, new ArrayList<GraphicalObject>());
		}
		else{
			for(GraphicalObject go : registeredInteractivePoints.get(curve)){
				lineChart.interactiveGOsPerCurve.get(curve).removeGraphicalObject(go);
				lineChart.interactivePoints.remove(go);
			}
		}
		for(DataPoint2 dp : points == null ? curve.selectedPoints : points){
			if(dp.isInViewport){
				canvas.addAllGraphicalObject(lineChart.createPoint(curve, dp, shape, true, registeredInteractivePoints.get(curve)));
			}
		}
		canvas.update();
	}

	@Override
	public void onMouseOut(MouseEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseOver(MouseEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

}
