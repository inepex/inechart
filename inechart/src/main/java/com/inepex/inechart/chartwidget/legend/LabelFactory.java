package com.inepex.inechart.chartwidget.legend;

import com.inepex.inechart.chartwidget.misc.HasTitle;

public interface LabelFactory {
	double[] getPadding(boolean includeTitle, boolean includeLegends);
	void addHasLegendEntries(HasLegendEntries hasLegendEntries);
	void setChartTitle(HasTitle chartTitle);
	void updateChartTitle();
	void updateLegends();
}
