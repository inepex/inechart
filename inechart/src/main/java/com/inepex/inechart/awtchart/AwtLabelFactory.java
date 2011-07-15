package com.inepex.inechart.awtchart;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.legend.HasLegendEntries;
import com.inepex.inechart.chartwidget.legend.LabelFactory;
import com.inepex.inechart.chartwidget.misc.HasTitle;
import com.inepex.inegraphics.awt.DrawingAreaAwt;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.Text;

public class AwtLabelFactory implements LabelFactory{
	GraphicalObjectContainer goc;
	ArrayList<HasLegendEntries> legendParents;
	HasTitle chartTitle;
	DrawingAreaAwt drawingAreaAwt;
	
	public AwtLabelFactory(DrawingAreaAwt drawingAreaAwt) {
		this.drawingAreaAwt = drawingAreaAwt;
		goc = new GraphicalObjectContainer();
		legendParents = new ArrayList<HasLegendEntries>();
	}

	@Override
	public void addHasLegendEntries(HasLegendEntries hasLegendEntries) {
		legendParents.add(hasLegendEntries);
	}

	@Override
	public void setChartTitle(HasTitle chartTitle) {
		this.chartTitle = chartTitle;
	}

	@Override
	public void updateChartTitle() {
		Text t = new Text(chartTitle.getTitle(), 0, 0);
		goc.addGraphicalObject(t);
	}

	@Override
	public void updateLegends() {
		
	}

	@Override
	public double[] getPadding(boolean includeTitle, boolean includeLegends) {
		return new double[]{10, 3, 3, 3};
	}

}
