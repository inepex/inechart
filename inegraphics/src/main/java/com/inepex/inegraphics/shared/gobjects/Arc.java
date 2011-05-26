package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Arc extends GraphicalObject {

	double startAngle;
	double arcAngle;
	double radius;

	public Arc(
			double basePointX,
			double basePointY,
			int zIndex,
			Context context,
			boolean stroke,
			boolean fill,
			double radius,
			double startAngle,
			double arcAngle) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
		this.radius = radius;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	public double getArcAngle() {
		return arcAngle;
	}

	public void setArcAngle(double arcAngle) {
		this.arcAngle = arcAngle;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public String toString() {
		return "Arc [basePointX=" + basePointX + ", basePointY=" + basePointY + ", zIndex=" + zIndex + ", context=" + context
				+ ", stroke=" + stroke + ", fill=" + fill + "startAngle=" + startAngle + ", arcAngle=" + arcAngle + ", radius="
				+ radius + "]";
	}

}
