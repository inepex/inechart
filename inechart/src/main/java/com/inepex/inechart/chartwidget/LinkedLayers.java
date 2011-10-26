package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.Collections;

public class LinkedLayers extends Layer {

	private ArrayList<Layer> layers;

	public LinkedLayers(){
		super(null);
		layers = new ArrayList<Layer>();
	}

	public LinkedLayers(int zIndex){
		super(null,zIndex);
		layers = new ArrayList<Layer>();
	}

	public LinkedLayers(Layer layer){
		this();
		addLayer(layer);
	}

	public LinkedLayers(ArrayList<Layer> layers) {
		this(TO_TOP, layers);
	}

	public LinkedLayers(int zIndex, ArrayList<Layer> layers) {
		this();
		setzIndex(zIndex);
		this.layers = layers;
	}

	public ArrayList<Layer> getLayers() {
		Collections.sort(layers);
		return layers;
	}

	public void addLayer(Layer layer){
		layers.add(layer);
	}

	public void removeLayer(Layer layer){
		if(layers != null){
			layers.remove(layer);
		}
	}
	
	public Layer getBottom(){
		if(layers.size() < 1){
			return null;
		}
		Collections.sort(layers);
		return layers.get(0);
	}
	
	public Layer getTop(){
		if(layers.size() < 1){
			return null;
		}
		Collections.sort(layers);
		return layers.get(layers.size() - 1);
	}

}
