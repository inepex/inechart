package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeHandler;
import com.inepex.inechart.chartwidget.label.HasLegend;
import com.inepex.inechart.chartwidget.label.Legend;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.impl.client.ishapes.Rectangle;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;

/**
 * A base class for moduls with axes, viewport and legend.
 * Contains model-to-canvas calculation helper methods.
 * Also supports auto padding calculation.
 *
 *  
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class IneChartModul2D extends IneChartModul implements	HasCoordinateSystem, HasLegend {
	protected class InnerEventHandler implements ViewportChangeHandler, MouseMoveHandler,
		MouseOutHandler, MouseDownHandler, MouseOverHandler, MouseUpHandler, ClickHandler{
		
		protected boolean canHandleViewportChangeEvent(ViewportChangeEvent event){
			List<IneChartModul2D> addressedModuls = event.getAddressedModuls();
			if(addressedModuls != null && 
				( !addressedModuls.contains(IneChartModul2D.this) || event.isModulHandled(IneChartModul2D.this) )){
				return false;
			}
			return true;
		}
		
		protected void setViewportChangeEventHandled(ViewportChangeEvent event){
			for(IneChartModul2D m : viewport.getUserModuls().keySet()){
				event.setModulHandled(m);
			}
		}

		@Override
		public void onMove(ViewportChangeEvent event, double dx, double dy) {
			if(canHandleViewportChangeEvent(event)){
				viewport.move(dx, dy);
				setViewportChangeEventHandled(event);
			}				
		}

		@Override
		public void onSet(ViewportChangeEvent event, double xMin, double yMin,
				double xMax, double yMax) {
			if(canHandleViewportChangeEvent(event)){
				viewport.set(xMin, yMin, xMax, yMax);
				setViewportChangeEventHandled(event);
			}
		}

		@Override
		public void onClick(ClickEvent event) {
			if(getValuePair(event) != null)
				IneChartModul2D.this.onClick(event);
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			if(getValuePair(event) != null)
				IneChartModul2D.this.onMouseUp(event);
		}
		
		@Override
		public void onMouseOver(MouseOverEvent event) {
			if(getValuePair(event) != null)
				IneChartModul2D.this.onMouseOver(event);
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			if(getValuePair(event) != null)
				IneChartModul2D.this.onMouseDown(event);
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
			if(getValuePair(event) != null)
				IneChartModul2D.this.onMouseOut(event);
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			if(getValuePair(event) != null)
				IneChartModul2D.this.onMouseMove(event);
		}
	
	}

	protected InnerEventHandler innerEventHandler;
	protected Axis xAxis;
	protected Axis yAxis;
	protected ArrayList<Axis> extraAxes;
	protected Axes axes;
	protected Viewport viewport;
	protected boolean useViewport;
	protected boolean redrawNeeded;
	protected boolean autoScaleViewport;
	protected boolean autoCalcPadding = true;
	//padding
	protected static final int DEFAULT_PADDING_H = 8;
	protected static final int DEFAULT_PADDING_V = 8;
	protected int topPadding = DEFAULT_PADDING_V,
			leftPadding = DEFAULT_PADDING_H,
			bottomPadding = DEFAULT_PADDING_V,
			rightPadding = DEFAULT_PADDING_H;
	//border and background
	protected static final int backgroundZIndex = Integer.MIN_VALUE + 100;
	protected static final int borderZIndex = backgroundZIndex + 1;
	protected LineProperties border = null;
	protected Color backgroundColor = null;
	//legend
	protected boolean showLegend = true;
	protected Legend legend;
	
	
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
		border = Defaults.border();
		legend = new Legend();
		innerEventHandler = new InnerEventHandler();
	}

	public abstract void updateModulsAxes();
	
	public void update(){
		//border
		if(border != null){
			graphicalObjectContainer.addGraphicalObject(
					new Rectangle(
							leftPadding //- border.getLineWidth()
							,topPadding //- border.getLineWidth()
							,getWidth()// + 2*border.getLineWidth()
							,getHeight()//+ 2*border.getLineWidth()
							,0, 
							borderZIndex,
							new Context(border.getLineColor().getAlpha(),
									border.getLineColor().getColor(),
									border.getLineWidth(),
									Defaults.colorString,
									0,0,0,
									Defaults.colorString),
									true, false));
		}
		if(backgroundColor != null){
			graphicalObjectContainer.addGraphicalObject(
					new Rectangle(leftPadding, topPadding, getWidth(), getHeight(),
							0, 
							backgroundZIndex,
							new Context(backgroundColor.getAlpha(),
									Defaults.colorString,
									0,
									backgroundColor.getColor(),
									0,0,0,
									Defaults.colorString),
									false, true));
		}
	}
	
	public void calculatePadding(double[] minPadding){
		if(!autoCalcPadding)
			return;
		double[] padding = mergePaddings(new double[]{DEFAULT_PADDING_V,DEFAULT_PADDING_H,DEFAULT_PADDING_V,DEFAULT_PADDING_H}, minPadding);
		if(xAxis.isVisible())
			padding = mergePaddings(padding, axes.getPaddingForAxis(xAxis));
		if(yAxis.isVisible())
			padding = mergePaddings(padding, axes.getPaddingForAxis(yAxis));
		for(Axis axis : extraAxes){
			if(axis.isVisible())
				padding = mergePaddings(padding, axes.getPaddingForAxis(axis));
		}
		topPadding = (int) padding[0];
		rightPadding = (int) padding[1];
		bottomPadding = (int) padding[2];
		leftPadding = (int) padding[3];		
	}
	
	private double[] mergePaddings(double[] first, double[] second){
		double[] ret = new double[4];
		for(int i=0;i<4;i++){
			ret[i] = Math.max(first[i], second [i]);
		}
		return ret;
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
	
	protected void alignExtraAxes(){
		for(Axis axis:extraAxes){
			Axis pairAxis = xAxis;
			if(AxisDirection.isPerpendicular(pairAxis, axis)){
				pairAxis = yAxis;
			}
			if(axis.getMax() == axis.getMin()){
				axis.setMax(pairAxis.getMax());
				axis.setMin(pairAxis.getMin());
			}
		}
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
	 * @return -1 if the given {@link AxisDirection} is not horizontal
	 */
	public double getCanvasX(double value) {
		Axis horizontalAxis = xAxis;
		if(!horizontalAxis.isHorizontal()){
			horizontalAxis = yAxis;
		}
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength, visibleMin;
		visibleLength = horizontalAxis.getMax() - horizontalAxis.getMin();
		visibleMin = horizontalAxis.getMin();
		double pos;
		if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
			pos =  (value - visibleMin) * totalWidth / visibleLength	+ leftPadding;
		} 
		else if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) {
			pos = (totalWidth -  ((value - visibleMin) * totalWidth / visibleLength)) + leftPadding;
		}
		else
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
	 * @return -1 if the given {@link AxisDirection} is not vertical
	 */
	public double getCanvasY(double value) {
		Axis verticalAxis = yAxis;
		if(verticalAxis.isHorizontal()){
			verticalAxis = yAxis;
		}
		int totalHeight = canvas.getHeight() - topPadding - bottomPadding;
		double visibleLength, visibleMin;
		visibleLength = verticalAxis.getMax() - verticalAxis.getMin();
		visibleMin = verticalAxis.getMin();
		double pos;
		if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) {
			pos = ((value - visibleMin) * totalHeight / visibleLength)	+ topPadding;
		}
		else if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			pos = (totalHeight - (value - visibleMin) * totalHeight / visibleLength) + topPadding;
		}
		else
			return -1;
		return pos;
	}
	
	/**
	 * Returns a value for a x position over canvas 
	 * @param canvasX
	 * @return x or y (depends on horizontal axis)
	 */
	public double getValueForCanvasX(int canvasX){
		Axis horizontalAxis = xAxis;
		if(!horizontalAxis.isHorizontal()){
			horizontalAxis = yAxis;
		}
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength, visibleMin;
		visibleLength = horizontalAxis.getMax() - horizontalAxis.getMin();
		visibleMin = horizontalAxis.getMin();
		double pos;
		if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
			pos =  (canvasX - leftPadding) * visibleLength / totalWidth + visibleMin;
		} 
		else if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) {
			pos =  (totalWidth + leftPadding - canvasX) * visibleLength / totalWidth + visibleMin;
		}
		else
			return -1;
		return pos;
	}
	
	/**
	 * Returns a value for an y position over canvas 
	 * @param canvasY
	 * @return y or x (depends on vertical axis)
	 */
	public double getValueForCanvasY(int canvasY){
		Axis verticalAxis = yAxis;
		if(verticalAxis.isHorizontal()){
			verticalAxis = yAxis;
		}
		int totalHeight = canvas.getHeight() - topPadding - bottomPadding;
		double visibleLength, visibleMin;
		visibleLength = verticalAxis.getMax() - verticalAxis.getMin();
		visibleMin = verticalAxis.getMin();
		double pos;
		if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) {
			pos = (canvasY - topPadding) * visibleLength / totalHeight + visibleMin;
		}
		else if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			pos = (totalHeight + topPadding - canvasY) * visibleLength / totalHeight + visibleMin;
		}
		else
			return -1;
		return pos;
	}
	
	/**
	 * Translates a point over the canvas to a point in a model
	 * @param canvasX
	 * @param canvasY
	 * @return model's [x,y]
	 */
	public double[] getValuePair(int canvasX, int canvasY){
		double[] ret = new double[2];
		if (xAxis.isHorizontal())
			ret[0] = getValueForCanvasX(canvasX);
		else
			ret[0] = getCanvasY(canvasY);
		if (yAxis.isHorizontal())
			ret[1] = getValueForCanvasX(canvasX);
		else
			ret[1] = getCanvasY(canvasY);
		return ret;
	}
	
	/**
	 * 
	 * @param mouseEvent any {@link MouseEvent}
	 * @return model's [x,y], or null if it is not a client side chart or is not over this modul
	 */
	public double[] getValuePair(MouseEvent<?> mouseEvent){
		if(canvas instanceof DrawingAreaGWT){
			int canvasX = mouseEvent.getRelativeX(((DrawingAreaGWT) canvas).getWidget().getElement());
			int canvasY = mouseEvent.getRelativeY(((DrawingAreaGWT) canvas).getWidget().getElement());
			if(isInsideModul(canvasX, canvasY))
				return getValuePair(canvasX, canvasY);
		}
		return null;
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
			ret[0] = getCanvasX(x);
		else
			ret[0] = getCanvasX(y);
		if (yAxis.isHorizontal())
			ret[1] = getCanvasY(x);
		else
			ret[1] = getCanvasY(y);
		return ret;
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
		alignExtraAxes();
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

	public boolean isAutoCalcPadding() {
		return autoCalcPadding;
	}

	public void setAutoCalcPadding(boolean autocalcPadding) {
		this.autoCalcPadding = autocalcPadding;
	}

	protected boolean isInsideModul(double posOnCanvas, boolean isX){
		//y
		if(!isX){
			if(posOnCanvas < topPadding  || posOnCanvas > canvas.getHeight() - bottomPadding){
				return false;
			}
			return true;
		}
		//x
		else{
			if(posOnCanvas > leftPadding && posOnCanvas < canvas.getWidth() - rightPadding){
				return true;
			}
			return false;
		}
	}

	protected boolean isInsideModul(double posXOnCanvas, double posYOnCanvas){
		if(isInsideModul(posXOnCanvas, true) && isInsideModul(posYOnCanvas, false))
			return true;
		return false;
	}
	
	public void setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
	}

	public int getWidth() {
		return canvas.getWidth() - leftPadding - rightPadding;
	}

	public int getHeight() {
		return canvas.getHeight() - topPadding - bottomPadding;
	}

	public int getTopPadding() {
		return topPadding;
	}

	public void setTopPadding(int topPadding) {
		this.topPadding = topPadding;
	}

	public int getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
	}

	public int getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
	}

	public LineProperties getBorder() {
		return border;
	}

	public void setBorder(LineProperties border) {
		this.border = border;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	@Override
	public boolean showLegend() {
		return showLegend;
	}

	@Override
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	@Override
	public Legend getLegend() {
		return legend;
	}

	@Override
	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	protected abstract void onClick(ClickEvent event);

	protected abstract void  onMouseUp(MouseUpEvent event);
	
	protected abstract void onMouseOver(MouseOverEvent event);

	protected abstract void onMouseDown(MouseDownEvent event);

	protected abstract void onMouseOut(MouseOutEvent event);
	
	protected abstract void onMouseMove(MouseMoveEvent event);
	
}
