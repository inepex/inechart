package com.inepex.inechart.chartwidget.event;

import java.util.ArrayList;
import java.util.List;

import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModule2D;

public class ViewportChangeEvent extends IneChartEvent<ViewportChangeHandler> {
	
	public static final Type<ViewportChangeHandler> TYPE = new Type<ViewportChangeHandler>();
	
	protected double dx,dy;
	protected double xMin;
	protected double yMin;
	protected double xMax;
	protected double yMax;
	protected List<IneChartModule2D> addressedModules;
	protected boolean isXChange;
	protected boolean isYChange;
	
	public ViewportChangeEvent(){
		this(null, 0, 0, null);
	}
	
	public ViewportChangeEvent(double min, double max, boolean isX) {
		super(null);
		if(isX){
			this.xMin = min;
			this.xMax = max;
		}
		else{
			this.yMin = min;
			this.yMax = max;
		}
		this.isXChange = isX;
		this.isYChange = !isX;
	}
	
	public ViewportChangeEvent(double distance, boolean isX) {
		super(null);
		if(isX){
			dx = distance;
		}
		else{
			dy = distance;
		}
		this.isXChange = isX;
		this.isYChange = !isX;
	}

	public ViewportChangeEvent(double xMin, double yMin, double xMax, double yMax) {
		this(null, xMin, yMin, xMax, yMax, null);
	}

	public ViewportChangeEvent(IneChart sourceChart, double xMin,
			double yMin, double xMax, double yMax,
			List<IneChartModule2D> addressedModules) {
		super(sourceChart);
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.isXChange = isYChange = true;
		setAddressedModules(addressedModules);
	}
	
	public ViewportChangeEvent(double dx, double dy){
		this(null, dx, dy, null);
	}

	public ViewportChangeEvent(IneChart sourceChart, double dx, double dy,
			List<IneChartModule2D> addressedModules) {
		super(sourceChart);
		this.dx = dx;
		this.dy = dy;
		this.isXChange = isYChange = true;
		setAddressedModules(addressedModules);
	}
	
	public void setAddressedModules(List<IneChartModule2D> addressedModules){
//		if(addressedModules == null || addressedModules.size() == 0)
//			return;
		this.addressedModules = addressedModules;
	}
	
	public void addAddressedModule(IneChartModule2D addressedModule){
		if(addressedModules == null){
			addressedModules = new ArrayList<IneChartModule2D>();
		}
		addressedModules.add(addressedModule);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ViewportChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewportChangeHandler handler) {
		if(dx != 0 || dy != 0) { 
			if(isXChange && !isYChange) {
				handler.onMoveAlongX(this, dx);
			}
			else if(!isXChange && isYChange) {
				handler.onMoveAlongY(this, dy);
			}
			else {
				handler.onMove(this,dx, dy);
			}
		}
		else{
			if(isXChange && !isYChange) {
				handler.onSetX(this, xMin, xMax);
			}
			else if(!isXChange && isYChange) {
				handler.onSetY(this, yMin, yMax);
			}
			else {
				handler.onSet(this,xMin, yMin, xMax, yMax);
			}
		}
	}

	
	public List<IneChartModule2D> getAddressedModules(){
		return addressedModules;
	}
	
	public boolean isXChange() {
		return isXChange;
	}
	
	public boolean isYChange() {
		return isYChange;
	}

	public double getDx() {
		return dx;
	}

	public double getDy() {
		return dy;
	}

	public double getxMin() {
		return xMin;
	}

	public double getyMin() {
		return yMin;
	}

	public double getxMax() {
		return xMax;
	}

	public double getyMax() {
		return yMax;
	}
	
}
