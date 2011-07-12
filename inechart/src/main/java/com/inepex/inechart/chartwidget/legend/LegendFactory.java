package com.inepex.inechart.chartwidget.legend;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.misc.HasTitle;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;

/**
 * 
 * Class for positioning and organizing legends.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class LegendFactory {

	AbsolutePanel chartMainPanel;
	ArrayList<HasLegendEntries> legendModuls;
	ArrayList<Legend> legends;
	HasTitle chartTitle;
	FlowPanel chartTitlePanel;
	
	public LegendFactory(AbsolutePanel chartMainPanel) {
		this.chartMainPanel = chartMainPanel;
		chartTitlePanel = new FlowPanel();
		chartTitlePanel.setStyleName(ResourceHelper.getRes().style().chartTitle());
	}
	
	
	public void updateLegends(){
//		for(IneChartModul m : legendsPerModul.keySet()){
//			Legend l = 	legendsPerModul.get(m);
//			l.update();
//			if(l.autoDisplay && !l.asWidget().isAttached()){
//				chartMainPanel.add(l.asWidget());
//			}
//		}
	}
	
	public void updateChartTitle(){
		if(chartTitle == null)
			return;
		if(!chartTitlePanel.isAttached())
			chartMainPanel.add(chartTitlePanel);
		chartMainPanel.clear();
		Label title = new Label(chartTitle.getTitle());
		title.setStyleName(ResourceHelper.getRes().style().title());
		chartTitlePanel.add(title);
		title = new Label(chartTitle.getDescription());
		title.setStyleName(ResourceHelper.getRes().style().description());
		chartTitlePanel.add(title);
	}
	
}
