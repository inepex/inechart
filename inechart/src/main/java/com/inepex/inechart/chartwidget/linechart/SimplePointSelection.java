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
	
	protected TreeMap<Curve, Shape> selectedPointShapes;
	protected Shape defaultSelectedPointShape;
	protected TreeMap<Curve, Layer> canvasPerCurve;
	protected TreeMap<Curve, ArrayList<GraphicalObject>> registeredInteractivePoints;
	
	public SimplePointSelection() {
		selectedPointShapes = new TreeMap<Curve, Shape>();
		canvasPerCurve = new TreeMap<Curve, Layer>();
		registeredInteractivePoints = new TreeMap<Curve, ArrayList<GraphicalObject>>();
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
			TreeMap<Curve, ArrayList<DataPoint>> selectedPoints,
			TreeMap<Curve, ArrayList<DataPoint>> deselectedPoints) {
		
		for(Curve c : lineChart.curves){
			if(c.hasPoint || lineChart.pointMouseOverRadius > 0){
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
						for(DataPoint dp : selectedPoints.get(c)){
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
	
	protected void checkLayer(Curve c){
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
		for(Curve c : lineChart.curves){
			if(c.hasPoint || lineChart.pointMouseOverRadius > 0){
				updateLayer(c, null);
			}
		}
	}
	
	protected void updateLayer(Curve curve, ArrayList<DataPoint> points){
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
				lineChart.resetInteractivePoint(curve, go);
			}
		}
		for(DataPoint dp : points == null ? curve.getSelectedPoints() : points){
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

	
	public Shape getDefaultSelectedPointShape() {
		return defaultSelectedPointShape;
	}

	
	public void setDefaultSelectedPointShape(Shape defaultSelectedPointShape) {
		this.defaultSelectedPointShape = defaultSelectedPointShape;
	}

	

}
