package com.inepex.inechart.chartwidget.event;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.Viewport;

public class ViewportChangeEvent extends IneChartEvent<ViewportChangeHandler> {
	
	public static final Type<ViewportChangeHandler> TYPE = new Type<ViewportChangeHandler>();
	
	protected double dx,dy;
	protected double xMin;
	protected double yMin;
	protected double xMax;
	protected double yMax;
	protected TreeMap<IneChartModul2D, Boolean> addressedModuls;

	public ViewportChangeEvent(IneChart sourceChart, double xMin,
			double yMin, double xMax, double yMax,
			List<IneChartModul2D> addressedModuls) {
		super(sourceChart);
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		setAddressedModuls(addressedModuls);
	}

	public ViewportChangeEvent(IneChart sourceChart, double dx, double dy,
			List<IneChartModul2D> addressedModuls) {
		super(sourceChart);
		this.dx = dx;
		this.dy = dy;
		setAddressedModuls(addressedModuls);
	}
	
	public void setAddressedModuls(List<IneChartModul2D> addressedModuls){
		if(addressedModuls == null || addressedModuls.size() == 0)
			return;
		this.addressedModuls = new TreeMap<IneChartModul2D, Boolean>();
		for(IneChartModul2D addressed : addressedModuls){
			this.addressedModuls.put(addressed, false);
		}
	}
	
	public void addAddressedModul(IneChartModul2D addressedModul){
		if(addressedModuls == null){
			addressedModuls = new TreeMap<IneChartModul2D, Boolean>();
		}
		addressedModuls.put(addressedModul, false);
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

	/**
	 * If there are {@link IneChartModul2D}s which share the same {@link Viewport}
	 * They should know that their {@link Viewport} was set before they recieved this event.
	 * @param modul
	 */
	public void setModulHandled(IneChartModul2D modul){
		if(addressedModuls == null || !addressedModuls.containsKey(modul))
			return;
		addressedModuls.put(modul, true);
	}	
	
	public boolean isModulHandled(IneChartModul2D modul){
		if(addressedModuls == null || !addressedModuls.containsKey(modul))
			return true;
		return addressedModuls.get(modul);
	}
	
	public List<IneChartModul2D> getAddressedModuls(){
		if(addressedModuls == null || addressedModuls.size() == 0)
			return null;
		ArrayList<IneChartModul2D> ret = new ArrayList<IneChartModul2D>();
		for(IneChartModul2D m : addressedModuls.keySet())
			ret.add(m);
		return ret;
	}
}
