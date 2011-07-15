package com.inepex.inechart.chartwidget.label;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

/**
 * 
 * Class for positioning and organizing legends.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class GWTLabelFactory extends LabelFactoryBase{
	private class LegendBinding{
		HasLegend modul;
		LegendWidget view;
		public LegendBinding(HasLegend modul, LegendWidget view) {
			super();
			this.modul = modul;
			this.view = view;
		}
		public HasLegend getModul() {
			return modul;
		}
		public LegendWidget getView() {
			return view;
		}
	}

	AbsolutePanel chartMainPanel;
	ArrayList<HasLegend> legendModuls;
	ArrayList<LegendWidget> legends;
	ArrayList<LegendBinding> bindings;
	HasTitle chartTitle;
	FlowPanel chartTitlePanel;
	
	public GWTLabelFactory(AbsolutePanel chartMainPanel, HasTitle chartTitle, DrawingAreaGWT canvas) {
		super(canvas);
		this.chartMainPanel = chartMainPanel;
		this.chartTitle = chartTitle;
		bindings = new ArrayList<GWTLabelFactory.LegendBinding>();
		chartTitlePanel = new FlowPanel();
		chartTitlePanel.setStyleName(ResourceHelper.getRes().style().chartTitle());
	}
	
	public void updateLegends(){
		for(LegendBinding b : bindings){
			if(b.modul.showLegend()){
				if(!b.getView().asWidget().isAttached())
					chartMainPanel.add(b.getView().asWidget());
				b.getView().setEntries(b.getModul().getLegendEntries());
			}
			else{
				b.getView().asWidget().removeFromParent();
			}
		}
	}
	
	@Override
	public void addHasLegendEntries(HasLegend hasLegendEntries){
		bindings.add(new LegendBinding(hasLegendEntries, new LegendWidget()));
	}
	
	public void updateChartTitle(){
		if(chartTitle == null)
			return;
		if(!chartTitlePanel.isAttached())
			chartMainPanel.add(chartTitlePanel);
		chartTitlePanel.clear();
		Label title = new Label(chartTitle.getName().text == null ? "" : chartTitle.getName().text);
		title.setStyleName(ResourceHelper.getRes().style().title());
		chartTitlePanel.add(title);
		title = new Label(chartTitle.getDescription().text == null ? "" : chartTitle.getDescription().text);
		title.setStyleName(ResourceHelper.getRes().style().description());
		chartTitlePanel.add(title);
	}
	
	public double[] measureTitle(){
		updateChartTitle();
		double[] ret = new double[]{
				chartTitlePanel.getAbsoluteLeft() - chartMainPanel.getAbsoluteLeft(),
				chartTitlePanel.getAbsoluteTop() - chartMainPanel.getAbsoluteTop(),
				chartTitlePanel.getOffsetWidth(),
				chartTitlePanel.getOffsetHeight()};
		chartTitlePanel.removeFromParent();
		return ret;
	}
	
	public ArrayList<double[]> measureLegends(){
		updateLegends();
		ArrayList<double[]> ret = new ArrayList<double[]>();
		for(LegendBinding b : bindings){
			Widget w = b.getView().asWidget();
			double[] d = new double[]{
					w.getAbsoluteLeft() - chartMainPanel.getAbsoluteLeft(),
					w.getAbsoluteTop() - chartMainPanel.getAbsoluteTop(),
					w.getOffsetWidth(),
					w.getOffsetHeight()};
			ret.add(d);
			w.removeFromParent();
		}
		return ret;
	}
	
	@Override
	public double[] getPadding(boolean includeTitle){
		double[] ret = new double[]{0,0,0,0};
		if(includeTitle){
			ret = mergePaddings(ret, measureTitle());
		}
		for(double[] d : measureLegends()){
			ret = mergePaddings(ret, d);
		}

		return ret;
	}

	private double[] mergePaddings(double[] padding, double[] dimensions){
		//TODO calculate other positions
		//for now only top positioned legends and title is measured correctly
		double[] ret = new double[4];
		double[] p = new double[]{
			dimensions[1] + dimensions[3], //top
			0,
			0,
			0
		};
		for(int i=0;i<4;i++){
			ret[i] = Math.max(padding[i],  p[i]);
		}
		return ret;
	}

	
	@Override
	public void setChartTitle(HasTitle chartTitle) {
		this.chartTitle = chartTitle;
	}

	
}