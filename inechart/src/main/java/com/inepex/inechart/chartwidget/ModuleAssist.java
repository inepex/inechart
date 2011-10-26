package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.Collections;

import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.label.LabelFactory;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.shared.DrawingArea;

public class ModuleAssist {

	public int minZIndex = 0;
	public static final int mainCanvasZIndex = 0;
	public int maxZIndex = 0;
	public static final int TOP = Integer.MAX_VALUE;
	public static final int BOT = Integer.MIN_VALUE;

	public class CanvasLayer implements Comparable<CanvasLayer>{

		ArrayList<IneChartModule> users;
		DrawingAreaGWT canvas;
		int zIndex;
		CanvasLayer layerAbove;

		public CanvasLayer(IneChartModule user,
				DrawingAreaGWT canvas, int zIndex) {

			this.users = new ArrayList<IneChartModule>();
			users.add(user);
			this.canvas = canvas;
			setzIndex(zIndex);
			this.zIndex = zIndex;
		}
		
		public void addUser(IneChartModule m){
			users.add(m);
		}
		
		public ArrayList<IneChartModule> getUsers() {
			return users;
		}

		public void setUsers(ArrayList<IneChartModule> users) {
			this.users = users;
		}

		public DrawingAreaGWT getCanvas() {
			return canvas;
		}

		public void setCanvas(DrawingAreaGWT canvas) {
			this.canvas = canvas;
		}

		public int getzIndex() {
			return zIndex;
		}

		public void setzIndex(int zIndex) {
			switch(zIndex){
			case BOT:
				this.zIndex = --minZIndex;
				break;
			case TOP:
				this.zIndex = ++maxZIndex;
				break;
			default:
				this.zIndex = zIndex;
				if(zIndex > maxZIndex)
					maxZIndex = zIndex;
				else if(zIndex < minZIndex)
					minZIndex = zIndex;
			}
		}

		@Override
		public int compareTo(CanvasLayer o) {
			return zIndex - o.zIndex;
		}
	}

	protected Axes axes;
	protected LabelFactory labelFactory;
	protected IneChartEventManager eventManager;
	protected DrawingArea mainCanvas;
	protected final IneChart clientSideChart;
	protected ArrayList<CanvasLayer> layers;

	public ModuleAssist(){
		clientSideChart = null;
	}

	public ModuleAssist(IneChart clientSideChart) {
		this.clientSideChart = clientSideChart;
		layers = new ArrayList<ModuleAssist.CanvasLayer>();
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

	public DrawingAreaGWT createLayer(IneChartModule module){
		return createLayer(module, TOP);
	}
	
	public DrawingAreaGWT createLayer(IneChartModule module, int zIndex){
		if(!isClientSide()){
			return null;
		}
		CanvasLayer layer = new CanvasLayer(module, clientSideChart.createLayer(), zIndex);
		layers.add(layer);
		clientSideChart.setLayerOrder(getLayers());
		return layer.canvas;
	}
	
	public void linkLayers(DrawingAreaGWT bottomLayer, DrawingAreaGWT topLayer){
		CanvasLayer bot = findLayer(bottomLayer);
		if(bot == null){
			return;
		}
		CanvasLayer top = findLayer(topLayer);
		if(top == null){
			return;
		}
		bot.layerAbove = top;
	}
	
	public void delinkLayers(DrawingAreaGWT bottomLayer, DrawingAreaGWT topLayer){
		CanvasLayer bot = findLayer(bottomLayer);
		if(bot == null){
			return;
		}
		bot.layerAbove = null;
	}
	
	
	private CanvasLayer findLayer(DrawingAreaGWT layer){
		for(CanvasLayer lyr:layers){
			if(layer == lyr.canvas){
				return lyr;
			}
		}	
		return null;
	}
	
	public void destroyLayer(DrawingAreaGWT layer){
		CanvasLayer cLyr = findLayer(layer);
		if(cLyr != null){
			clientSideChart.removeLayer(cLyr.canvas);
			layers.remove(cLyr);
		}
		
	}
	
	public ArrayList<DrawingAreaGWT> getLayers(){
		Collections.sort(layers);
		ArrayList<DrawingAreaGWT> ordered = new ArrayList<DrawingAreaGWT>();
		for(CanvasLayer lyr:layers){
			if(ordered.contains(lyr.canvas)){
				continue;
			}
			ordered.add(lyr.canvas);
			if(lyr.layerAbove != null){
				ordered.add(lyr.layerAbove.canvas);
			}
		}
		return ordered;
	}
}
