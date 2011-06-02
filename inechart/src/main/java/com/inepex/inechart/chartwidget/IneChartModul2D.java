package com.inepex.inechart.chartwidget;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.axes.TickFactory;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.shared.DrawingArea;

public abstract class IneChartModul2D extends IneChartModul implements
		HasCoordinateSystem {

	protected Axis xAxis;
	protected Axis yAxis;
	protected ArrayList<Axis> extraAxes;
	protected Axes axes;
	protected Viewport viewport;
	protected boolean useViewport;
	protected boolean redrawNeeded;
	protected boolean autoScaleViewport;
	public static final LineProperties defaultGridLine = new LineProperties(0.8, new Color("#C8C8C8", 1));
	
	protected IneChartModul2D(DrawingArea canvas, Axes axes) {
		this(canvas, axes, new Viewport());
	}

	protected IneChartModul2D(DrawingArea canvas, Axes axes,
			Viewport defaultViewport) {
		super(canvas);
		this.axes = axes;
		viewport = defaultViewport;
		viewport.userModuls.put(this, false);
		useViewport = true;
		redrawNeeded = true;
		autoScaleViewport = true;
		// default axes
		xAxis = new Axis();
		xAxis.setAxisDirection(AxisDirection.Horizontal_Ascending_To_Right);
		xAxis.setModulToAlign(this);
		yAxis = new Axis();
		yAxis.setAxisDirection(AxisDirection.Vertical_Ascending_To_Top);
		yAxis.setModulToAlign(this);
		axes.addAxis(xAxis);
		axes.addAxis(yAxis);
		extraAxes = new ArrayList<Axis>();
	}

	@Override
	public void setXAxis(Axis xAxis) {
		axes.removeAxis(this.xAxis);
		this.xAxis = xAxis;
		axes.addAxis(xAxis);
		if (xAxis.getModulToAlign() == null) {
			xAxis.setModulToAlign(this);
		} else {// scale this modul as the other
			if (xAxis.isHorizontal()) {
				rightPadding = xAxis.getModulToAlign().rightPadding;
				leftPadding = xAxis.getModulToAlign().leftPadding;
			} else {
				bottomPadding = xAxis.getModulToAlign().bottomPadding;
				topPadding = xAxis.getModulToAlign().topPadding;
			}
		}
		redrawNeeded = true;
	}

	@Override
	public Axis getXAxis() {
		return xAxis;
	}

	@Override
	public void setYAxis(Axis yAxis) {
		axes.removeAxis(this.yAxis);
		this.yAxis = yAxis;
		axes.addAxis(yAxis);
		if (yAxis.getModulToAlign() == null) {
			yAxis.setModulToAlign(this);
		} else {// scale this modul as the other
			if (yAxis.isHorizontal()) {
				rightPadding = yAxis.getModulToAlign().rightPadding;
				leftPadding = yAxis.getModulToAlign().leftPadding;
			} else {
				topPadding = yAxis.getModulToAlign().topPadding;
				bottomPadding = yAxis.getModulToAlign().bottomPadding;
			}
		}
		redrawNeeded = true;
	}

	@Override
	public Axis getYAxis() {
		return yAxis;
	}

	public void addExtraAxis(Axis axis) {
		axis.setModulToAlign(this);
		axes.addAxis(axis);
		extraAxes.add(axis);
	}

	public void removeExtraAxis(Axis axis) {
		extraAxes.remove(axis);
		axes.removeAxis(axis);
	}

	@Override
	public void setViewport(Viewport viewport) {
		this.viewport.userModuls.remove(this);
		this.viewport = viewport;
		this.viewport.userModuls.put(this, false);
		redrawNeeded = true;
	}

	@Override
	public Viewport getViewport() {
		return viewport;
	}

	@Override
	public void setUseViewport(boolean useViewport) {
		this.useViewport = useViewport;
		this.viewport.userModuls.put(this, false);
	}

	@Override
	public boolean useViewport() {
		return useViewport;
	}

	/**
	 * Model to canvas transformation if useViewport is true the axis will be
	 * used only to determine direction if it is false, the {@link Viewport} is
	 * not used in calculation
	 * 
	 * @param value
	 *            to transform
	 * @param horizontalAxis
	 *            a horizontal {@link Axis}
	 * @return -1 if the given {@link AxisDirection} is not horizontal
	 */
	public double getCanvasX(double value, Axis horizontalAxis) {
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength, visibleMin;
		if (useViewport) {
			visibleLength = viewport.getWidth();
			visibleMin = viewport.getXMin();
		} else {
			visibleLength = horizontalAxis.getMax() - horizontalAxis.getMin();
			visibleMin = horizontalAxis.getMin();
		}
		double pos;
		if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
			pos =  (value - visibleMin) * totalWidth / visibleLength	+ leftPadding;
		} else if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) {
			pos = totalWidth - ((value - visibleMin) * totalWidth / visibleLength - rightPadding);
		} else
			return -1;
		return pos;
	}

	/**
	 * Model to canvas transformation if useViewport is true the axis will be
	 * used only to determine direction if it is false, the {@link Viewport} is
	 * not used in calculation
	 * 
	 * @param value
	 *            to transform
	 * @param verticalAxis
	 *            a vertical {@link Axis}
	 * @return -1 if the given {@link AxisDirection} is not vertical
	 */
	public double getCanvasY(double value, Axis verticalAxis) {
		int totalHeight = canvas.getHeight() - topPadding - bottomPadding;
		double visibleLength, visibleMin;
		if (useViewport) {
			visibleLength = viewport.getHeight();
			visibleMin = viewport.getYMin();
		} else {
			visibleLength = verticalAxis.getMax() - verticalAxis.getMin();
			visibleMin = verticalAxis.getMin();
		}
		double pos;
		if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) {
			pos = ((value - visibleMin) * totalHeight / visibleLength)
					+ topPadding;
		} else if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			pos = totalHeight
					-  ((value - visibleMin) * totalHeight / visibleLength - bottomPadding);
		} else
			return -1;
		return pos;
	}

	/**
	 * Translates a point(x,y) in model to a point on canvas
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double[] getCanvasPosition(double x, double y) {
		double[] ret = new double[2];
		if (xAxis.isHorizontal())
			ret[0] = getCanvasX(x, xAxis);
		else
			ret[0] = getCanvasX(y, yAxis);
		if (yAxis.isHorizontal())
			ret[1] = getCanvasY(x, xAxis);
		else
			ret[1] = getCanvasY(y, yAxis);
		return ret;
	}

	public void autoCreateAxes() {
		TickFactory tf = new TickFactory();
		if (xAxis.isChanged()){
			tf.autoCreateTicks(xAxis);
			for(Tick t : xAxis.getTicks()){
				if(t.getPosition() != xAxis.getMin() && t.getPosition() != xAxis.getMax()){
					t.setGridLine(defaultGridLine);
				}
				t.setTickText(t.getPosition() + "");
			}
		}
		if (yAxis.isChanged()){
			tf.autoCreateTicks(yAxis);
			for(Tick t : yAxis.getTicks()){
				if(t.getPosition() != yAxis.getMin() && t.getPosition() != yAxis.getMax()){
					t.setGridLine(defaultGridLine);
				}
				t.setTickText(t.getPosition() + "");
			}
		}
	}
	protected void alignViewportAndAxes(){
		if (useViewport) {
			if (viewport.getXMin() != xAxis.getMin())
				xAxis.setMin(viewport.getXMin());
			if (viewport.getXMax() != xAxis.getMax())
				xAxis.setMax(viewport.getXMax());
			if (viewport.getYMin() != yAxis.getMin())
				yAxis.setMin(viewport.getYMin());
			if (viewport.getYMax() != yAxis.getMax())
				yAxis.setMax(viewport.getYMax());
		} else {
			if (xAxis.isChanged()) {
				viewport.setX(xAxis.getMin(), xAxis.getMax());
			}
			if (yAxis.isChanged()) {
				viewport.setY(yAxis.getMin(), yAxis.getMax());
			}
		}
		viewport.userModuls.put(this, false);
	}

	/**
	 * @return the autoScaleViewport
	 */
	public boolean isAutoScaleViewport() {
		return autoScaleViewport;
	}

	/**
	 * @param autoScaleViewport
	 *            the autoScaleViewport to set
	 */
	public void setAutoScaleViewport(boolean autoScaleViewport) {
		this.autoScaleViewport = autoScaleViewport;
	}

	@Override
	public boolean redrawNeeded() {
		if (redrawNeeded || xAxis.isChanged() || yAxis.isChanged()
				|| viewport.isChanged())
			return true;
		return false;
	}
}
