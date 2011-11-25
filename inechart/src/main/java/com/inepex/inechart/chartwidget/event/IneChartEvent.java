package com.inepex.inechart.chartwidget.event;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.inepex.inechart.chartwidget.IneChart;

public abstract class IneChartEvent<H extends EventHandler> extends GwtEvent<H> {

	protected IneChart sourceChart;
	protected List<IneChart> addressedCharts;

	protected IneChartEvent(IneChart sourceChart) {
		this.sourceChart = sourceChart;
		this.addressedCharts = null;
	}

	protected IneChartEvent(IneChart sourceChart, List<IneChart> addressedCharts) {
		this.sourceChart = sourceChart;
		this.addressedCharts = addressedCharts;
	}

	protected IneChartEvent(IneChart sourceChart, IneChart... addressedCharts){
		this.sourceChart = sourceChart;
		setAddressedCharts(addressedCharts);
	}

	/**
	 * The source chart of this event.
	 * Null if the source is not an {@link IneChart}.
	 * @return
	 */
	public IneChart getSourceChart() {
		return sourceChart;
	}

	/**
	 * see {@link #getSourceChart()}
	 * @param sourceChart
	 */
	public void setSourceChart(IneChart sourceChart) {
		this.sourceChart = sourceChart;
	}

	/**
	 * The list of {@link IneChart}s which should handle this event.
	 * Null if this option is not specified, so all charts are addressed.
	 * @return
	 */
	public List<IneChart> getAddressedCharts() {
		return addressedCharts;
	}
	
	/**
	 * see {@link #setAddressedCharts(List)}
	 * @param addressedCharts
	 */
	public void setAddressedCharts(List<IneChart> addressedCharts) {
		this.addressedCharts = addressedCharts;
	}
	
	public void setAddressedCharts(IneChart... addressedCharts){
		if(addressedCharts != null && addressedCharts.length > 0){
			this.addressedCharts = new ArrayList<IneChart>();
			for(IneChart addressed:addressedCharts){
				this.addressedCharts.add(addressed);
			}
		}
	}

	public void addAddressedChart(IneChart addressedChart){
		if(addressedCharts == null){
			addressedCharts = new ArrayList<IneChart>();
		}
		addressedCharts.add(addressedChart);
	}
}
