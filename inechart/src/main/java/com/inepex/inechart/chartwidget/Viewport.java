package com.inepex.inechart.chartwidget;

import java.util.TreeMap;

import com.inepex.inechart.chartwidget.axes.Axis;

/**
 * 
 * A class representing a rectangle-shaped viewport
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public class Viewport {

	/**
	 * the coordinates of the upper-left(x1,y1), and lower-right(x2,y2) corner
	 * of the viewport
	 */
	double xMin, yMin, xMax, yMax;
	/**
	 * true if the viewport has changed since the last setChanged(false) call it
	 * means that dx, dy, xRatio, yRatio fields should be consistent, and they
	 * should contain the parameters of the last transformation sequences
	 */
	boolean changed;
	double dx, dy;
	/**
	 * Because multiple moduls -over multiple charts too- can share the same
	 * viewport, we should register them, so when a modul/chart finished
	 * updating it will know that it could reset dx,dy.. fields the boolean
	 * value should be set to true by default when a modul finished its update()
	 * it should be set to false
	 */
	TreeMap<IneChartModul2D, Boolean> userModuls = new TreeMap<IneChartModul2D, Boolean>();

	public Viewport() {
		this(0, 0, 1, 1);
	}

	public Viewport(double xMin, double yMin, double xMax, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		changed = false;
		setDefaultParameters();
	}

	public void moveOnX(double dx) {
		move(dx, 0);
	}

	public void move(double dx, double dy) {
		this.dx += dx;
		this.dy += dy;
		xMin += dx;
		xMax += dx;
		yMin += dy;
		yMax += dy;
		changed();
	}

	public void moveOnY(double dy) {
		move(0, dy);
	}

	public void setX(double xMin, double xMax) {
		set(xMin, this.yMin, xMax, this.yMax);
	}

	public void setY(double yMin, double yMax) {
		set(this.xMin, yMin, this.xMax, yMax);
	}

	public void set(double xMin, double yMin, double xMax, double yMax) {
		if (xMin >= xMax || yMin >= yMax)
			return;
		dx += xMin - this.xMin;
		dy += yMin - this.yMin;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		changed();
	}

	public double getWidth() {
		return xMax - xMin;
	}

	public double getHeight() {
		return yMax - yMin;
	}

	/**
	 * @return the xMin
	 */
	public double getXMin() {
		return xMin;
	}

	/**
	 * @param xMin
	 *            the xMin to set
	 */
	public void setXMin(double xMin) {
		if (xMin >= xMax || xMin == this.xMin)
			return;
		dx = xMin - this.xMin;
		dy = 0;
		this.xMin = xMin;
		changed();
	}

	/**
	 * @return the yMin
	 */
	public double getYMin() {
		return yMin;
	}

	/**
	 * @param yMin
	 *            the yMin to set
	 */
	public void setYMin(double yMin) {
		if (yMin >= yMax || yMin == this.yMin)
			return;
		dx = 0;
		dy = yMin - this.yMin;
		this.yMin = yMin;
		changed();
	}

	/**
	 * @return the xMax
	 */
	public double getXMax() {
		return xMax;
	}

	/**
	 * @param xMax
	 *            the xMax to set
	 */
	public void setXMax(double xMax) {
		if (xMax <= xMin || xMax == this.xMax)
			return;
		dx = dy = 0;
		changed();
		this.xMax = xMax;
	}

	/**
	 * @return the yMax
	 */
	public double getYMax() {
		return yMax;
	}

	/**
	 * @param yMax
	 *            the yMax to set
	 */
	public void setYMax(double yMax) {
		if (yMax <= yMin || yMax == this.yMax)
			return;
		dx = dy = 0;
		changed();
		this.yMax = yMax;
	}

	private void changed() {
		changed = true;
		for (IneChartModul2D m : userModuls.keySet())
			userModuls.put(m, true);
	}

	/**
	 * @return the changed
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Do NOT use this method explicitly. {@link IneChart} will update it after
	 * its update() call, if other moduls do not depend on this {@link Viewport}
	 */
	public void resetChanged() {
		setDefaultParameters();
	}

	/**
	 * Sets all parameters back to their default values.
	 */
	void setDefaultParameters() {
		dx = dy = 0;
		changed = false;
	}

	/**
	 * @return the dx
	 */
	public double getDX() {
		return dx;
	}

	/**
	 * @return the dy
	 */
	public double getDY() {
		return dy;
	}

	public void setXValuesFromAxis(Axis axis) {
		xMin = axis.getMin();
		xMax = axis.getMax();
		// TODO
	}

	public void setYValuesFromAxis(Axis axis) {
		yMin = axis.getMin();
		yMax = axis.getMax();
	}

	public TreeMap<IneChartModul2D, Boolean> getUserModuls() {
		return userModuls;
	}
	
	
}
