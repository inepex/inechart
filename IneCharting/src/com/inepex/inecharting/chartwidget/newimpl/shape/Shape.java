package com.inepex.inecharting.chartwidget.newimpl.shape;

import java.util.ArrayList;

import com.inepex.inecharting.chartwidget.newimpl.misc.HasShadow;
import com.inepex.inecharting.chartwidget.newimpl.misc.HasZIndex;
import com.inepex.inecharting.chartwidget.newimpl.properties.Color;
import com.inepex.inecharting.chartwidget.newimpl.properties.ShapeProperties;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public abstract class Shape implements HasShadow, HasZIndex{

	ShapeProperties properties;
	
	int zIndex;
	double shadowOffsetX = 0, shadowOffsetY = 0;
	Color shadowColor = null;
	
	protected Shape (ShapeProperties prop){
		properties = prop;
	}
	
	abstract public ArrayList<GraphicalObject> toGraphicalObjects();
	abstract public GraphicalObject toInterActiveGraphicalObject();
	
	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public int getZIndex() {
		return this.zIndex;
	}

	@Override
	public void setShadowOffsetX(double offsetX) {
		this.shadowOffsetX = offsetX;
	}

	@Override
	public void setShadowOffsetY(double offsetY) {
		this.shadowOffsetY = offsetY;
	}

	@Override
	public double getShadowOffsetX() {
		return shadowOffsetX;
	}

	@Override
	public double getShadowOffsetY() {
		return shadowOffsetY;
	}
	
	@Override
	public Color getShadowColor() {
		return this.shadowColor;
	}
	
	@Override
	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}
	
	
}
 