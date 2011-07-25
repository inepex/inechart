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
	protected List<IneChartModule2D> addressedModuls;

	public ViewportChangeEvent(IneChart sourceChart, double xMin,
			double yMin, double xMax, double yMax,
			List<IneChartModule2D> addressedModuls) {
		super(sourceChart);
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		setAddressedModuls(addressedModuls);
	}

	public ViewportChangeEvent(IneChart sourceChart, double dx, double dy,
			List<IneChartModule2D> addressedModuls) {
		super(sourceChart);
		this.dx = dx;
		this.dy = dy;
		setAddressedModuls(addressedModuls);
	}
	
	public void setAddressedModuls(List<IneChartModule2D> addressedModuls){
		if(addressedModuls == null || addressedModuls.size() == 0)
			return;
		this.addressedModuls = addressedModuls;
	}
	
	public void addAddressedModul(IneChartModule2D addressedModul){
		if(addressedModuls == null){
			addressedModuls = new ArrayList<IneChartModule2D>();
		}
		addressedModuls.add(addressedModul);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ViewportChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewportChangeHandler handler) {
		if(dx != 0 || dy != 0)
			handler.onMove(this,dx, dy);
		else
			handler.onSet(this,xMin, yMin, xMax, yMax);
	}

	
	public List<IneChartModule2D> getAddressedModuls(){
		return addressedModuls;
	}
}
