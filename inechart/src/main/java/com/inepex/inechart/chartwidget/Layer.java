package com.inepex.inechart.chartwidget;

import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public class Layer implements Comparable<Layer>{

	public static int minZIndex = 0;
	public static final int mainCanvasZIndex = 0;
	public static int maxZIndex = 0;
	public static final int TO_TOP = Integer.MAX_VALUE - 10;
	public static final int TO_BOT = Integer.MIN_VALUE + 10;
	public static final int ALWAYS_TOP = Integer.MAX_VALUE;
	public static final int ALWAYS_BOT = Integer.MIN_VALUE;
	
	private DrawingAreaGWT canvas;
	private int zIndex;
	
	private IneChartModule2D relatedModule;

	public Layer(int zIndex) {
		this(null, zIndex);
	}
	
	public Layer(DrawingAreaGWT canvas){
		this(canvas, TO_TOP);
	}

	public Layer(DrawingAreaGWT canvas, int zIndex) {
		this.canvas = canvas;
		setzIndex(zIndex);
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
		case TO_BOT:
			this.zIndex = --minZIndex;
			break;
		case TO_TOP:
			this.zIndex = ++maxZIndex;
			break;
		case ALWAYS_BOT:
		case ALWAYS_TOP:
			this.zIndex = zIndex;
			break;
		default:
			this.zIndex = zIndex;
			if(zIndex > maxZIndex)
				maxZIndex = zIndex;
			else if(zIndex < minZIndex)
				minZIndex = zIndex;
		}
	}

	public void bringToTop(){
		setzIndex(TO_TOP);
	}
	
	public void bringToBot(){
		setzIndex(TO_BOT);
	}
	
	@Override
	public int compareTo(Layer o) {
		return ((Integer)zIndex).compareTo(o.zIndex);
	}
		
	public void setOpacity(double alpha){
		canvas.getWidget().getElement().getStyle().setOpacity(0);
	}

	public IneChartModule2D getRelatedModule() {
		return relatedModule;
	}

	public void setRelatedModule(IneChartModule2D relatedModule) {
		this.relatedModule = relatedModule;
	}
}
