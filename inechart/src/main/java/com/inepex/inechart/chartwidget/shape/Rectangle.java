package com.inepex.inechart.chartwidget.shape;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class Rectangle extends Shape {

	GraphicalObject inner, outer;

	double width, height, roundedCornerR;

	Double baseX = null, baseY = null;

	public Rectangle(double width, double height) {
		this(width, height, Defaults.shapeProperties());
	}

	public Rectangle(double width, double height, double roundedCornerR) {
		this(width, height, roundedCornerR, Defaults.shapeProperties());
	}

	public Rectangle(double width, double height, ShapeProperties properties) {
		super(properties);
		this.width = width;
		this.height = height;
		this.roundedCornerR = 0;
	}

	public Rectangle(double width, double height, double x, double y,
			ShapeProperties properties) {
		super(properties);
		this.width = width;
		this.height = height;
		this.roundedCornerR = 0;
		this.baseX = x;
		this.baseY = y;
	}

	public Rectangle(double width, double height, double roundedCornerR,
			ShapeProperties properties) {
		super(properties);
		this.width = width;
		this.height = height;
		this.roundedCornerR = roundedCornerR;
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
			outer = new com.inepex.inegraphics.shared.gobjects.Rectangle(
					baseX == null ? 0 : baseX, baseY == null ? 0 : baseY,
					this.width, this.height, this.roundedCornerR, zIndex,
					outerContext, true, false);
		}
		if (properties.getFillColor() != null) {
			Context innerContext = new Context(properties.getFillColor()
					.getAlpha(), Defaults.colorString, 0,
					properties.getFillColor().getColor(),
					 shadowOffsetX,
					 shadowOffsetY,
//					0, 0,
					shadowColor == null ? 0d : shadowColor.getAlpha(),
					shadowColor == null ? Defaults.colorString : shadowColor
							.getColor());
			inner = new com.inepex.inegraphics.shared.gobjects.Rectangle(
					baseX == null ? 0 : baseX, baseY == null ? 0 : baseY,
					this.width, this.height, this.roundedCornerR, zIndex,
					innerContext, false, true);
		}
		ArrayList<GraphicalObject> toRet = new ArrayList<GraphicalObject>();
		if (outer != null)
			toRet.add(outer);
		if (inner != null)
			toRet.add(inner);
		return toRet;
	}


	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @return the roundedCornerR
	 */
	public double getRoundedCornerR() {
		return roundedCornerR;
	}

	/**
	 * @param roundedCornerR
	 *            the roundedCornerR to set
	 */
	public void setRoundedCornerR(double roundedCornerR) {
		this.roundedCornerR = roundedCornerR;
	}
}
