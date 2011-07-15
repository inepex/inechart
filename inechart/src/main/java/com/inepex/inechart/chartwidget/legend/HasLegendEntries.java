package com.inepex.inechart.chartwidget.legend;

import java.util.List;

public interface HasLegendEntries {
	List<LegendEntry> getEntries();
	boolean showLegend();
	void setShowLegend(boolean showLegend);
}
