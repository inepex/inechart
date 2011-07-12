package com.inepex.inechart.chartwidget.shape;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class Circle extends Shape {

	GraphicalObject inner;
	GraphicalObject outer;

	double radius;

	public Circle(double radius) {
		this(radius, ShapeProperties.defaultShapeProperties());
	}

	public Circle(double radius, ShapeProperties properties) {
		super(properties);
		this.radius = radius;
	}

	@Override
	public ArrayList<GraphicalObject> toGraphicalObjects() {
		if (properties.getLineProperties().getLineWidth() > 0) {
			Context outerContext = new Context(properties.getLineProperties()
					.getLineColor().getAlpha(), properties.getLineProperties()
					.getLineColor().getColor(), properties.getLineProperties()
					.getLineWidth(), Color.DEFAULT_COLOR, shadowOffsetX,
					shadowOffsetY, shadowColor == null ? 0d
							: shadowColor.getAlpha(),
					shadowColor == null ? Color.DEFAULT_COLOR : shadowColor
							.getColor());
			outer = new com.inepex.inegraphics.impl.client.ishapes.Circle(0, 0,
					this.zIndex, outerContext, true, false, this.radius);
		}
		if (properties.getFillColor() != null) {
			Context innerContext = new Context(properties.getFillColor()
					.getAlpha(), Color.DEFAULT_COLOR, 0,
					properties.getFillColor().getColor(),
					// shadowOffsetX,
					// shadowOffsetY,
					0, 0, shadowColor == null ? 0d : shadowColor.getAlpha(),
					shadowColor == null ? Color.DEFAULT_COLOR : shadowColor
							.getColor());
			inner = new com.inepex.inegraphics.impl.client.ishapes.Circle(0, 0,
					this.zIndex, innerContext, false, true,
					outer == null ? this.radius : radius
							- properties.getLineProperties().getLineWidth() + 1);
		}

		ArrayList<GraphicalObject> toRet = new ArrayList<GraphicalObject>();
		if (outer != null)
			toRet.add(outer);
		if (inner != null)
			toRet.add(inner);
		return toRet;
	}

	@Override
	public GraphicalObject toInterActiveGraphicalObject() {
		if (outer == null && inner == null && toGraphicalObjects().size() == 0)
			return null;
		else if (outer != null)
			return outer;
		else
			return inner;
	}

	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

}