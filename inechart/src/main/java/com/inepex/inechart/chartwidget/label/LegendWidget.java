package com.inepex.inechart.chartwidget.label;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;

/** 
 * Client side chart legend implementation
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class LegendWidget implements IsWidget{
	
	ComplexPanel mainPanel;
	List<LegendEntry> entries;
	boolean autoDisplay = true;
	
	
	public LegendWidget(){
		this(new ArrayList<LegendEntry>());
	}
	
	public LegendWidget(List<LegendEntry> entries) {
		this.entries = entries;
		mainPanel = new FlowPanel();
		mainPanel.setStyleName(ResourceHelper.getRes().style().legend());
		update();
	}

	public List<LegendEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<LegendEntry> entries) {
		this.entries = entries;
		update();
	}
	
	void update(){
		mainPanel.clear();
		for(LegendEntry e : entries){
			FlowPanel fp = new FlowPanel();
			fp.setStyleName(ResourceHelper.getRes().style().legendEntry());
			
			Label lbl = new Label();
			lbl.setStyleName(ResourceHelper.getRes().style().color());
			lbl.getElement().getStyle().setBackgroundColor(e.getColor().getColor());
			fp.add(lbl);
			
			lbl = new  Label(e.getTitle().getName().text == null ? "" : e.getTitle().getName().text);
			lbl.setStyleName(ResourceHelper.getRes().style().text());
			fp.add(lbl);
			
			mainPanel.add(fp);
		}
	}

	@Override
	public Widget asWidget() {
		return mainPanel;
	}

	public boolean isAutoDisplay() {
		return autoDisplay;
	}

	public void setAutoDisplay(boolean autoDisplay) {
		this.autoDisplay = autoDisplay;
	}

}
