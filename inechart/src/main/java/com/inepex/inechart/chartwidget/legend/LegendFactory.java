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
	private class LegendBinding{
		HasLegendEntries modul;
		LegendWidget view;
		public LegendBinding(HasLegendEntries modul, LegendWidget view) {
			super();
			this.modul = modul;
			this.view = view;
		}
		public HasLegendEntries getModul() {
			return modul;
		}
		public void setModul(HasLegendEntries modul) {
			this.modul = modul;
		}
		public LegendWidget getView() {
			return view;
		}
		public void setView(LegendWidget view) {
			this.view = view;
		}
		
	}

	AbsolutePanel chartMainPanel;
	ArrayList<HasLegendEntries> legendModuls;
	ArrayList<LegendWidget> legends;
	ArrayList<LegendBinding> bindings;
	HasTitle chartTitle;
	FlowPanel chartTitlePanel;
	
	public LegendFactory(AbsolutePanel chartMainPanel) {
		this.chartMainPanel = chartMainPanel;
		bindings = new ArrayList<LegendFactory.LegendBinding>();
		chartTitlePanel = new FlowPanel();
		chartTitlePanel.setStyleName(ResourceHelper.getRes().style().chartTitle());
	}
	
	
	public void updateLegends(){
		for(LegendBinding b : bindings){
			if(b.modul.showLegend()){
				if(!b.getView().asWidget().isAttached())
					chartMainPanel.add(b.getView().asWidget());
				b.getView().setEntries(b.getModul().getEntries());
			}
			else{
				b.getView().asWidget().removeFromParent();
			}
		}
	}
	
	public void addHasLegendEntries(HasLegendEntries hasLegendEntries){
		bindings.add(new LegendBinding(hasLegendEntries, new LegendWidget()));
//		if(hasLegendEntries.showLegend())
//			chartMainPanel.add(bindings.get(bindings.size()-1).getView().asWidget());
	}
	
	
	public void updateChartTitle(){
		if(chartTitle == null)
			return;
		if(!chartTitlePanel.isAttached())
			chartMainPanel.add(chartTitlePanel);
		chartTitlePanel.clear();
		Label title = new Label(chartTitle.getTitle() == null ? "" : chartTitle.getTitle());
		title.setStyleName(ResourceHelper.getRes().style().title());
		chartTitlePanel.add(title);
		title = new Label(chartTitle.getDescription() == null ? "" : chartTitle.getDescription());
		title.setStyleName(ResourceHelper.getRes().style().description());
		chartTitlePanel.add(title);
	}
	
}
