package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.TreeMap;

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
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionHandler;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.event.DataSetChangeHandler;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeHandler;
import com.inepex.inechart.chartwidget.label.HasLegendEntries;
import com.inepex.inechart.chartwidget.label.LabelFactory;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.Rectangle;

/**
 * A base class for modules with axes and legend.
 * Contains model-to-canvas calculation helper methods.
 * Also supports auto padding calculation.
 *
 *  
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class IneChartModule2D extends IneChartModule implements HasCoordinateSystem, HasLegendEntries{
	
	protected class InnerEventHandler implements ViewportChangeHandler, MouseMoveHandler,
		MouseOutHandler, MouseDownHandler, MouseOverHandler, MouseUpHandler, ClickHandler, DataSetChangeHandler, DataEntrySelectionHandler{
		
		boolean isMouseOverModul = false;
		
		public boolean isAddressed(ViewportChangeEvent event){
			if(event.getAddressedModuls() == null || event.getAddressedModuls().contains(IneChartModule2D.this)){
				return true;
			}
			else{
				return false;
			}
		}
		
		@Override
		public void onMove(ViewportChangeEvent event, double dx, double dy) {
			if(!canHandleEvents)
				return;
			if(!isAddressed(event))
				return;
			if(xAxis.getModulToAlign() == IneChartModule2D.this){
				xAxis.setMin(xAxis.getMin() + dx);
				xAxis.setMax(xAxis.getMax() + dx);
				redrawNeeded = true;
			}
			if(yAxis.getModulToAlign() == IneChartModule2D.this){
				yAxis.setMin(yAxis.getMin() + dy);
				yAxis.setMax(yAxis.getMax() + dy);
				redrawNeeded = true;
			}
			IneChartModule2D.this.onMove(event, dx, dy);
		}

		@Override
		public void onSet(ViewportChangeEvent event, double xMin, double yMin,
				double xMax, double yMax) {
			if(!canHandleEvents)
				return;
			if(!isAddressed(event))
				return;
			if(xAxis.getModulToAlign() == IneChartModule2D.this){
				xAxis.setMin(xMin);
				xAxis.setMax(xMax);
				redrawNeeded = true;
			}
			if(yAxis.getModulToAlign() == IneChartModule2D.this){
				yAxis.setMin(yMin);
				yAxis.setMax(yMax);
				redrawNeeded = true;
			}
			IneChartModule2D.this.onSet(event, xMin, yMin, xMax, yMax);
		}

		@Override
		public void onClick(ClickEvent event) {
			if(!canHandleEvents)
				return;
			if(getValuePair(event) != null)
				IneChartModule2D.this.onClick(event);
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			if(!canHandleEvents)
				return;
			if(getValuePair(event) != null)
				IneChartModule2D.this.onMouseUp(event);
		}
		
		@Override
		public void onMouseOver(MouseOverEvent event) {
			if(!canHandleEvents)
				return;
			if(getValuePair(event) != null)
				IneChartModule2D.this.onMouseOver(event);
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			if(!canHandleEvents)
				return;
			if(getValuePair(event) != null)
				IneChartModule2D.this.onMouseDown(event);
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
			if(!canHandleEvents)
				return;
			IneChartModule2D.this.onMouseOut(event);
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			if(!canHandleEvents)
				return;

			IneChartModule2D.this.onMouseMove(event);
			if(getValuePair(event) != null){
				if(!isMouseOverModul){
					isMouseOverModul = true;
					IneChartModule2D.this.onMouseOver(event);
//					moduleAssist.eventManager.fireEvent(new ModuleMouseOverEvent(event, IneChartModule2D.this));
				}
			}
			else{
				isMouseOverModul = false;
				IneChartModule2D.this.onMouseOut(event);
//				moduleAssist.eventManager.fireEvent(new ModuleMouseOutEvent(event, IneChartModule2D.this));
			}
		}

		@Override
		public void onMoveAlongX(ViewportChangeEvent event, double dx) {
			if(!canHandleEvents)
				return;
			if(!isAddressed(event))
				return;
			if(xAxis.getModulToAlign() == IneChartModule2D.this){
				xAxis.setMin(xAxis.getMin() + dx);
				xAxis.setMax(xAxis.getMax() + dx);
				redrawNeeded = true;
			}
			IneChartModule2D.this.onMoveAlongX(event, dx);
		}

		@Override
		public void onMoveAlongY(ViewportChangeEvent event, double dy) {
			if(!canHandleEvents)
				return;
			if(!isAddressed(event))
				return;
			if(yAxis.getModulToAlign() == IneChartModule2D.this){
				yAxis.setMin(yAxis.getMin() + dy);
				yAxis.setMax(yAxis.getMax() + dy);
				redrawNeeded = true;
			}
			IneChartModule2D.this.onMoveAlongY(event, dy);
		}

		@Override
		public void onSetX(ViewportChangeEvent event, double xMin, double xMax) {
			if(!canHandleEvents)
				return;
			if(!isAddressed(event))
				return;
			if(xAxis.getModulToAlign() == IneChartModule2D.this){
				xAxis.setMin(xMin);
				xAxis.setMax(xMax);
				redrawNeeded = true;
			}
			IneChartModule2D.this.onSetX(event, xMin, xMax);
		}

		@Override
		public void onSetY(ViewportChangeEvent event, double yMin, double yMax) {
			if(!canHandleEvents)
				return;
			if(!isAddressed(event))
				return;
			if(yAxis.getModulToAlign() == IneChartModule2D.this){
				yAxis.setMin(yMin);
				yAxis.setMax(yMax);
				redrawNeeded = true;
			}
			IneChartModule2D.this.onSetY(event, yMin, yMax);
		}

		@Override
		public void onDataSetChange(DataSetChangeEvent event) {
			IneChartModule2D.this.onDataSetChange(event);
		}

	
		@Override
		public void onSelect(DataEntrySelectionEvent event) {
			IneChartModule2D.this.onSelect(event);			
		}

		
		@Override
		public void onDeselect(DataEntrySelectionEvent event) {
			IneChartModule2D.this.onDeselect(event);			
		}
	
	}
	
	protected InnerEventHandler innerEventHandler;
	
	protected Axis xAxis;
	protected Axis yAxis;
	protected ArrayList<Axis> extraAxes;
	
	protected boolean autoScaleViewport;
	protected boolean autoCalcPadding = true;
	
	//padding
	protected int minTopPadding = Defaults.paddingVertical;
	protected int minLeftPadding = Defaults.paddingHorizontal;
	protected int minBottomPadding = Defaults.paddingVertical;
	protected int minRightPadding = Defaults.paddingHorizontal;
	protected int topPadding = minTopPadding;
	protected int leftPadding = minLeftPadding;
	protected int bottomPadding = minBottomPadding;
	protected int rightPadding = minRightPadding;
	//border and background
	protected static final int backgroundZIndex = Integer.MIN_VALUE + 100;
	protected static final int borderZIndex = backgroundZIndex + 1;
	protected LineProperties border = null;
	protected Color backgroundColor = null;
	//legend
	protected boolean displayLegendEntries = true;
	protected TreeMap<String, Color> legendEntries;
	
	protected LinkedLayers moduleLayer;
	
	protected IneChartModule2D(ModuleAssist moduleAssist) {
		super(moduleAssist);
		this.moduleAssist = moduleAssist;
		moduleAssist.labelFactory.addLegendOwner(this);
		autoScaleViewport = true;
		if(moduleAssist.isClientSide()){
			moduleLayer = new LinkedLayers();
			moduleAssist.addLinkedLayers(moduleLayer);
		}
		// default axes
		xAxis = new Axis();
		xAxis.setAxisDirection(AxisDirection.Horizontal_Ascending_To_Right);
		xAxis.setModulToAlign(this);
		yAxis = new Axis();
		yAxis.setAxisDirection(AxisDirection.Vertical_Ascending_To_Top);
		yAxis.setModulToAlign(this);
		moduleAssist.axes.addAxis(xAxis);
		moduleAssist.axes.addAxis(yAxis);
		extraAxes = new ArrayList<Axis>();
		border = Defaults.border();
		innerEventHandler = new InnerEventHandler();
		if(moduleAssist.eventManager != null){
			moduleAssist.eventManager.addMouseDownHandler(innerEventHandler);
			moduleAssist.eventManager.addMouseMoveHandler(innerEventHandler);
			moduleAssist.eventManager.addMouseOutHandler(innerEventHandler);
			moduleAssist.eventManager.addMouseOverHandler(innerEventHandler);
			moduleAssist.eventManager.addMouseUpHandler(innerEventHandler);
			moduleAssist.eventManager.addClickHandler(innerEventHandler);
			moduleAssist.eventManager.addDataEntrySelectionHandler(innerEventHandler);
			moduleAssist.eventManager.addDataSetChangeHandler(innerEventHandler);
		}
	}
	
	protected abstract void onClick(ClickEvent event);

	protected abstract void onMouseUp(MouseUpEvent event);
	
	protected abstract void onMouseOver(MouseEvent<?> event);

	protected abstract void onMouseDown(MouseDownEvent event);

	protected abstract void onMouseOut(MouseEvent<?> event);
	
	protected abstract void onMouseMove(MouseMoveEvent event);

	protected abstract void onDataSetChange(DataSetChangeEvent event);
	
	protected abstract void onSelect(DataEntrySelectionEvent event);
	
	protected abstract void onDeselect(DataEntrySelectionEvent event);
	
	protected abstract void onMove(ViewportChangeEvent event, double dx, double dy);
	
	protected abstract void onMoveAlongX(ViewportChangeEvent event, double dx);
	
	protected abstract void onMoveAlongY(ViewportChangeEvent event, double dy);
	
	protected abstract void onSet(ViewportChangeEvent event, double xMin, double yMin, double xMax, double yMax);
	
	protected abstract void onSetX(ViewportChangeEvent event, double xMin, double xMax);
	
	protected abstract void onSetY(ViewportChangeEvent event, double yMin, double yMax);

	/**
	 * a module should override this method to calculate and set its axes before the update() call
	 */
	public void preUpdateModule(){
		alignExtraAxes();
	}
	
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
//		super.update();
	}

	public void setPadding(double[] padding){
		topPadding = (int) Math.max(padding[0], minTopPadding);
		rightPadding = (int) Math.max(padding[1], minRightPadding);
		bottomPadding = (int) Math.max(padding[2], minBottomPadding);
		leftPadding = (int) Math.max(padding[3], minLeftPadding);	
	}
	
	public double[] getPaddingForAxes(){
		double[] padding = new double[]{minTopPadding,minRightPadding,minBottomPadding,minLeftPadding};
		if(xAxis.isVisible()) {
			padding = LabelFactory.mergePaddings(padding, moduleAssist.axes.getActualModulPaddingForAxis(xAxis));
		}
		if(yAxis.isVisible()) {
			padding = LabelFactory.mergePaddings(padding, moduleAssist.axes.getActualModulPaddingForAxis(yAxis));
		}
		for(Axis axis : extraAxes){
			if(axis.isVisible())
				padding = LabelFactory.mergePaddings(padding, moduleAssist.axes.getActualModulPaddingForAxis(axis));
		}
		return padding;
	}
	
	@Override
	public void setXAxis(Axis xAxis) {
		moduleAssist.axes.removeAxis(this.xAxis);
		this.xAxis = xAxis;
		moduleAssist.axes.addAxis(xAxis);
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
	}

	@Override
	public Axis getXAxis() {
		return xAxis;
	}

	@Override
	public void setYAxis(Axis yAxis) {
		moduleAssist.axes.removeAxis(this.yAxis);
		this.yAxis = yAxis;
		moduleAssist.axes.addAxis(yAxis);
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
	}

	@Override
	public Axis getYAxis() {
		return yAxis;
	}

	public void addExtraAxis(Axis axis) {
		axis.setModulToAlign(this);
		
		moduleAssist.axes.addAxis(axis);
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
		moduleAssist.axes.removeAxis(axis);
	}

	/**
	 * Model to canvas calculation
	 * @param value on the horizontal axis
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
	
	public double getValueDistanceX(double distanceOnCanvas){
		Axis horizontalAxis = xAxis;
		if(!horizontalAxis.isHorizontal()){
			horizontalAxis = yAxis;
		}
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength = horizontalAxis.getMax() - horizontalAxis.getMin();
		return distanceOnCanvas * visibleLength / totalWidth;
	}
	
	public double getValueDistanceY(double distanceOnCanvas){
		Axis verticalAxis = yAxis;
		if(verticalAxis.isHorizontal()){
			verticalAxis = xAxis;
		}
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength = verticalAxis.getMax() - verticalAxis.getMin();
		return distanceOnCanvas * visibleLength / totalWidth;
	}
	
	public double getCanvasDistanceX(double distanceInValue){
		Axis horizontalAxis = xAxis;
		if(!horizontalAxis.isHorizontal()){
			horizontalAxis = yAxis;
		}
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength = horizontalAxis.getMax() - horizontalAxis.getMin();
		return distanceInValue * totalWidth / visibleLength;
	}
	
	public double getCanvasDistanceY(double distanceInValue){
		Axis verticalAxis = yAxis;
		if(verticalAxis.isHorizontal()){
			verticalAxis = xAxis;
		}
		int totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength = verticalAxis.getMax() - verticalAxis.getMin();
		return distanceInValue * totalWidth / visibleLength;
	}

	/**
	 * Model to canvas calculation
	 * @param value on the vertical axis
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
	public double getValueForCanvasX(double canvasX){
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
	public double getValueForCanvasY(double canvasY){
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
	public double[] getValuePair(double canvasX, double canvasY){
		double[] ret = new double[2];
		if (xAxis.isHorizontal())
			ret[0] = getValueForCanvasX(canvasX);
		else
			ret[0] = getValueForCanvasY(canvasY);
		if (yAxis.isHorizontal())
			ret[1] = getValueForCanvasX(canvasX);
		else
			ret[1] = getValueForCanvasY(canvasY);
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

	public int[] getCoords(MouseEvent<?> e){
		return new int[]{e.getRelativeX(((DrawingAreaGWT) canvas).getWidget().getElement()),
				e.getRelativeY(((DrawingAreaGWT) canvas).getWidget().getElement())}	;
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

	public boolean isInsideModul(double posXOnCanvas, double posYOnCanvas){
		if(isInsideModul(posXOnCanvas, true) && isInsideModul(posYOnCanvas, false))
			return true;
		return false;
	}
	
	public boolean isAutoCalcPadding() {
		return autoCalcPadding;
	}

	/**
	 * @param autocalcPadding if true the module should be positioned manually
	 */
	public void setAutoCalcPadding(boolean autocalcPadding) {
		this.autoCalcPadding = autocalcPadding;
	}

	/**
	 * @return the autoScaleViewport
	 */
	public boolean isAutoScaleViewport() {
		return autoScaleViewport;
	}

	/**
	 * @param autoScaleViewport the autoScaleViewport to set
	 */
	public void setAutoScaleViewport(boolean autoScaleViewport) {
		this.autoScaleViewport = autoScaleViewport;
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

	public int getRightEnd(){
		return canvas.getWidth() - rightPadding;
	}
	
	public int getBottomEnd(){
		return canvas.getHeight() - bottomPadding;
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
	
	public int getMinTopPadding() {
		return minTopPadding;
	}
	
	public void setMinTopPadding(int minTopPadding) {
		this.minTopPadding = minTopPadding;
	}

	public int getMinLeftPadding() {
		return minLeftPadding;
	}
	
	public void setMinLeftPadding(int minLeftPadding) {
		this.minLeftPadding = minLeftPadding;
	}
	
	public int getMinBottomPadding() {
		return minBottomPadding;
	}
	
	public void setMinBottomPadding(int minBottomPadding) {
		this.minBottomPadding = minBottomPadding;
	}
	
	public int getMinRightPadding() {
		return minRightPadding;
	}
	
	public void setMinRightPadding(int minRightPadding) {
		this.minRightPadding = minRightPadding;
	}
	
	@Override
	public void setDisplayLegendEntries(boolean displayEntries) {
		this.displayLegendEntries = displayEntries;
	}

	@Override
	public boolean isDisplayLegendEntries() {
		return displayLegendEntries;
	}

	public LinkedLayers getModuleLayer() {
		return moduleLayer;
	}

	@Override
	public void setLegendEntries(TreeMap<String, Color> legendEntries) {
		this.legendEntries = legendEntries;
	}
}
