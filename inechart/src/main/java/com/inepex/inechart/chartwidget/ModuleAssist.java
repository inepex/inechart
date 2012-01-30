package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.label.LabelFactory;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.shared.DrawingArea;

public class ModuleAssist {

	protected Axes axes;
	protected LabelFactory labelFactory;
	protected IneChartEventManager eventManager;
	protected DrawingArea mainCanvas;
	protected final IneChart clientSideChart;
	protected ArrayList<Layer> layers;
	protected AbsolutePanel chartMainPanel;

	public ModuleAssist(){
		clientSideChart = null;
	}

	public ModuleAssist(IneChart clientSideChart) {
		this.clientSideChart = clientSideChart;
		layers = new ArrayList<Layer>();
	}

	public boolean isClientSide() {
		return clientSideChart != null;
	}

	public Axes getAxes() {
		return axes;
	}

	public void setAxes(Axes axes) {
		this.axes = axes;
	}

	public LabelFactory getLabelFactory() {
		return labelFactory;
	}

	public void setLabelFactory(LabelFactory labelFactory) {
		this.labelFactory = labelFactory;
	}

	public IneChartEventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(IneChartEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public DrawingArea getMainCanvas() {
		return mainCanvas;
	}

	public void setMainCanvas(DrawingArea mainCanvas) {
		this.mainCanvas = mainCanvas;
	}
	
	public Layer createAndAttachLayer(){
		return createAndAttachLayer(Layer.TO_TOP);
	}
	
	public Layer createAndAttachLayer(int zIndex){
		if(!isClientSide()){
			return null;
		}
		Layer layer = new Layer(clientSideChart.createLayer(), zIndex);
		layers.add(layer);
		updateLayerOrder();
		return layer;
	}
	
	public void addLinkedLayers(LinkedLayers layerGroup){
		removeLayers(layerGroup);
		layers.add(layerGroup);
		updateLayerOrder();
	}
	
	public void addCanvasToLayer(Layer layer){
		if(layer.getCanvas() != null || layer instanceof LinkedLayers){
			return;
		}
		layer.setCanvas(clientSideChart.createLayer());
	}
	
	private void removeLayers(LinkedLayers layerGroup){
		for(Layer layer : layerGroup.getLayers()){
			if(layer instanceof LinkedLayers){
				removeLayers((LinkedLayers) layer);
			}
			else{
				layers.remove(layer);
			}
		}
	}
	
	public void destroyLayer(Layer layer){
		if(layer instanceof LinkedLayers){
			for(Layer lyr : ((LinkedLayers) layer).getLayers()){
				destroyLayer(lyr);
			}
		}
		else{
			clientSideChart.removeLayer(layer.getCanvas());
		}
		layers.remove(layer);
	}
		
	public ArrayList<DrawingAreaGWT> getLayers(){
		Collections.sort(layers);
		ArrayList<DrawingAreaGWT> ordered = new ArrayList<DrawingAreaGWT>();
		for(Layer lyr:layers){
			findCanvases(ordered, lyr);
		}
		return ordered;
	}
	
	public void updateLayerOrder(){
		clientSideChart.setLayerOrder(getLayers());
	}
	
	private void findCanvases(ArrayList<DrawingAreaGWT> layers, Layer layer){
		if(layer instanceof LinkedLayers){
			for(Layer lyr:((LinkedLayers) layer).getLayers()){
				findCanvases(layers, lyr);
			}
		}
		else{
			layers.add(layer.getCanvas());
		}
	}

	public AbsolutePanel getChartMainPanel() {
		return chartMainPanel;
	}

	public void setChartMainPanel(AbsolutePanel chartMainPanel) {
		this.chartMainPanel = chartMainPanel;
	}

	public void removeLayer(Layer layer){
		if(layer instanceof LinkedLayers){
			for(Layer lyr : ((LinkedLayers) layer).getLayers()){
				removeLayer(lyr);
			}
		}
		else{
			clientSideChart.removeLayer(layer.getCanvas());
		}
	}
	
	public void addLayer(Layer layer){
		if(layer instanceof LinkedLayers){
			for(Layer lyr : ((LinkedLayers) layer).getLayers()){
				addLayer(lyr);
			}
		}
		else{
			clientSideChart.addLayer(layer.getCanvas());
		}
	}
	
	public boolean isLayerAttached(Layer layer){
		boolean ret = true;
		if(layer instanceof LinkedLayers){
			for(Layer lyr : ((LinkedLayers) layer).getLayers()){
				if(!isLayerAttached(lyr)){
					ret = false;
					break;
				}
			}
		}
		else{
			if(!clientSideChart.containsLayer(layer.getCanvas())){
				ret = false;
			}
		}
		return ret;
	}
}
