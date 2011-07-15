package com.inepex.inechart.chartwidget.shape;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.misc.HasShadow;
import com.inepex.inechart.chartwidget.misc.HasZIndex;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public abstract class Shape implements HasShadow, HasZIndex {

	ShapeProperties properties;

	int zIndex;
	double shadowOffsetX = 0, shadowOffsetY = 0;
	Color shadowColor = null;

	protected Shape(){
		this(new ShapeProperties());
	}
	
 	protected Shape(ShapeProperties prop) {
		properties = prop;
	}

	/**
	 * When called the Shape creates (new) {@link GraphicalObject}s representing
	 * itself.
	 * 
	 * @return
	 */
	abstract public ArrayList<GraphicalObject> toGraphicalObjects();

	/**
	 * This method created an {@link InteractiveGraphicalObject} from one of the
	 * {@link GraphicalObject} returned by calling (or a previously called)
	 * toGraphicalObjects() method
	 * 
	 * @return
	 */
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

	public ShapeProperties getProperties() {
		return properties;
	}

	public void setProperties(ShapeProperties properties) {
		this.properties = properties;
	}

}
