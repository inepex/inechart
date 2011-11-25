package com.inepex.inechart.chartwidget.event;

import java.util.List;

import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModule2D;

public interface FiresViewportChangeEvent {

	/**
	 * @return
	 */
	public List<IneChart> getAddressedCharts();

	/**
	 * @param addressedCharts the addressedCharts to set
	 */
	public void setAddressedCharts(List<IneChart> addressedCharts);

	/**
	 * @return the addressedModuls
	 */
	public List<IneChartModule2D> getAddressedModules();

	/**
	 * @param addressedModules the addressedModuls to set
	 */
	public void setAddressedModules(List<IneChartModule2D> addressedModules);
	
	public void addAddressedModule(IneChartModule2D addressedModule);
}
