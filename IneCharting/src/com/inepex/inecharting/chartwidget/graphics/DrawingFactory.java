package com.inepex.inecharting.chartwidget.graphics;

import java.util.ArrayList;
import java.util.TreeMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.LineCurveVisualizer;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.PointCurveVisualizer;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.ModelManager;

/**
 * 
 * A class for drawing the chart's model to the canvas.
 * The complete view of the IneChart belongs here.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class DrawingFactory implements HasViewport{

	public static enum DrawingTool{
		VAADIN_GWT_GRAPHICS
	}
	
	/**
	 * The tool with 
	 */
	private DrawingTool drawingTool;
	private IneChartProperties properties;
	private ModelManager modelManager;
	private TreeMap<String, ArrayList<CurveVisualizer>> curveVisualizers;
	private Widget chartCanvas;
	
	
	/**
	 * Creates an instance with the defined drawing toolkit,
	 * and initializes the related objects needed for drawing (e.g.: the 'canvas')
	 * @param drawingTool
	 */
	public DrawingFactory(DrawingTool drawingTool, IneChartProperties properties, ModelManager mm) {
		this.drawingTool = drawingTool;
		this.properties = properties;
		this.modelManager = mm;
		curveVisualizers = new TreeMap<String, ArrayList<CurveVisualizer>>();
		switch (drawingTool) {
		case VAADIN_GWT_GRAPHICS:
			initGwtGraphicsFields();
		default:
			return;
		}
	}
	
	private void initGwtGraphicsFields(){
		this.chartCanvas = new DrawingArea(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		Rectangle border = new Rectangle(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		((DrawingArea)chartCanvas).add(border);
	}
	
 	/**
 	 * draws a curve on canvas
 	 * @param curve
 	 */
 	public void addCurve(Curve curve){
 		switch (drawingTool) {
		case VAADIN_GWT_GRAPHICS:
			ArrayList<CurveVisualizer> visualizers = new ArrayList<CurveVisualizer>();
			if(curve.hasLine()){
				LineCurveVisualizer lcv = new LineCurveVisualizer(chartCanvas, curve, modelManager);
				lcv.drawCurve(modelManager.getViewportMin(), modelManager.getViewportMax());
				visualizers.add(lcv);
			}
			if(curve.hasPoints()){
				PointCurveVisualizer pcv = new PointCurveVisualizer(chartCanvas, curve, modelManager, properties);
				pcv.drawCurve(modelManager.getViewportMin(), modelManager.getViewportMax());
				visualizers.add(pcv);
			}
			curveVisualizers.put(curve.getName(), visualizers);
			break;
		default:
			return;
		}
 	}
 
 	/**
 	 * removes a curve from canvas
 	 * @param index
 	 */
 	public void removeCurve(String name){
 		for(CurveVisualizer cv:curveVisualizers.get(name)){
 			cv.removeFromCanvas();
 		}
 		curveVisualizers.remove(name);
 	}
 	
 	public Widget getChartCanvas(){
 		return chartCanvas;		
 	}

	
 	@Override
	public void moveViewport(double dx) {
		for(String curveName : curveVisualizers.keySet())
			for(CurveVisualizer visualizer: curveVisualizers.get(curveName))
				visualizer.moveViewport(dx);		
	}

	
 	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
 		for(String curveName : curveVisualizers.keySet())
			for(CurveVisualizer visualizer: curveVisualizers.get(curveName))
				visualizer.setViewPort(viewportMin, viewportMax);		
	}
}
