package com.inepex.inechart.chartwidget.shape;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class Circle extends Shape {

	GraphicalObject inner;
	GraphicalObject outer;

	double radius;

	public Circle(double radius) {
		this(radius, Defaults.shapeProperties());
	}

	public Circle(double radius, ShapeProperties properties) {
		super(properties);
		this.radius = radius;
	}

	@Override
	public ArrayList<GraphicalObject> toGraphicalObjects() {
		if (properties.getLineProperties() != null && properties.getLineProperties().getLineWidth() > 0) {
			Context outerContext = new Context(properties.getLineProperties()
					.getLineColor().getAlpha(), properties.getLineProperties()
					.getLineColor().getColor(), properties.getLineProperties()
					.getLineWidth(), Defaults.colorString, shadowOffsetX,
					shadowOffsetY, shadowColor == null ? 0d
							: shadowColor.getAlpha(),
							shadowColor == null ? Defaults.colorString : shadowColor
									.getColor());
			outer = new com.inepex.inegraphics.shared.gobjects.Circle(0, 0,
					this.zIndex, outerContext, true, false, this.radius);
		}
		if (properties.getFillColor() != null) {
			Context innerContext = new Context(properties.getFillColor()
					.getAlpha(), Defaults.colorString, 0,
					properties.getFillColor().getColor(),
					// shadowOffsetX,
					// shadowOffsetY,
					0, 0, shadowColor == null ? 0d : shadowColor.getAlpha(),
							shadowColor == null ? Defaults.colorString : shadowColor
									.getColor());
			inner = new com.inepex.inegraphics.shared.gobjects.Circle(0, 0,
					zIndex, innerContext, false, true,
					outer == null ? radius : radius
							- properties.getLineProperties().getLineWidth()/2d 
					);
		}

		ArrayList<GraphicalObject> toRet = new ArrayList<GraphicalObject>();
		if (outer != null)
			toRet.add(outer);
		if (inner != null)
			toRet.add(inner);
		return toRet;
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

	@Override
	public GraphicalObject toInteractiveGraphicalObject(int distance) {
		if(distance <= 0){
			if(outer == null && inner == null){
				toGraphicalObjects();
			}
			if(outer != null){
				return outer;
			}
			if(inner != null){
				return inner;
			}
			return null;
		}
		else{
			toGraphicalObjects();
			if(outer != null){
				((com.inepex.inegraphics.shared.gobjects.Circle)outer).setRadius(radius + distance);
				return outer;
			}
			if(inner != null){
				((com.inepex.inegraphics.shared.gobjects.Circle)inner).setRadius(radius + distance);
				return inner;
			}
			else{
				return new com.inepex.inegraphics.shared.gobjects.Circle(0, 0, 0,
						new Context(Defaults.alpha, Defaults.colorString, 0, Defaults.colorString),
						true, properties.getFillColor() == null, radius + distance);
			}
		}
	}

}
