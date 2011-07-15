package com.inepex.inechart.chartwidget.label;

import java.util.List;

public interface HasLegend {
	List<LegendEntry> getLegendEntries();
	boolean showLegend();
	void setShowLegend(boolean showLegend);
	Legend getLegend();
	void setLegend(Legend legend);
}
