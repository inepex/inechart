package com.inepex.inechart.chartwidget.label;

import java.util.List;

/**
 * 
 * Chart entities should implement this interface to display legend
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public interface HasLegend {
	List<LegendEntry> getLegendEntries();
	void setLegendEntries(List<LegendEntry> legendEntries);
	boolean isShowLegend();
	void setShowLegend(boolean showLegend);
	Legend getLegend();
//	void setLegend(Legend legend);
}
