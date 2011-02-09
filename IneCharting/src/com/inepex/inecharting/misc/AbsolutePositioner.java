package com.inepex.inecharting.misc;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class AbsolutePositioner {
	private AbsolutePanel panel;
	private ArrayList<Widget> widgets;
	private boolean dropInvisibleWidgets;
	
	public AbsolutePositioner(AbsolutePanel panel) {
		this.panel = panel;
		widgets = new ArrayList<Widget>();
		dropInvisibleWidgets = true;
	}
	
	
	public boolean isVisibleOverPanel(Widget widget){
		boolean visible = false;
		if(widget.getParent().equals(panel) && 
				panel.getWidgetLeft(widget) <= panel.getOffsetWidth() && 
				panel.getWidgetTop(widget) <= panel.getOffsetHeight() &&
				panel.getWidgetLeft(widget) >= 0 &&
				panel.getWidgetTop(widget) >= 0)
			visible = true;
		return visible;		
	}
	
	public boolean addWidget(Widget w, int left, int top){
		widgets.add(w);
		panel.add(w, left, top);
		if(!isVisibleOverPanel(w)){
			panel.remove(w);
			widgets.remove(w);
			return false;
		}
		return true;
	}
	
	public Widget addWidget(Widget w){
		widgets.add(w);
		panel.add(w);
		return w;
	}
	
	public static Widget setRight(Widget w, int right){
		DOM.setStyleAttribute(w.getElement(), "position", "absolute");
		DOM.setStyleAttribute(w.getElement(), "right", right+"px");
		return w;
	}
	
	public static Widget setTop(Widget w, int top){
		DOM.setStyleAttribute(w.getElement(), "position", "absolute");
		DOM.setStyleAttribute(w.getElement(), "top", top+"px");
		return w;
	}
	
	public boolean removeWidget(Widget w){
		return widgets.remove(w);
	}
	
	public void setDropInvisibleWidgets(boolean dropInvisibleWidgets) {
		this.dropInvisibleWidgets = dropInvisibleWidgets;
	}
	
	public boolean isDropInvisibleWidgets() {
		return dropInvisibleWidgets;
	}
	
	public void setWidgetPosition(Widget w, int left, int top){
		if(w.getParent().equals(panel))
			panel.setWidgetPosition(w, left, top);
	}
	
	public void moveWidgets(int dx, int dy){
		ArrayList<Widget> toRemove = new ArrayList<Widget>();
		for(Widget w : widgets){
			panel.setWidgetPosition(w, panel.getWidgetLeft(w) + dx, panel.getWidgetTop(w) + dy);
			if(dropInvisibleWidgets && !isVisibleOverPanel(w))
				toRemove.add(w);
		}
		for(Widget w : toRemove){
			panel.remove(w);
			widgets.remove(w);
		}
	}

	public void removeAllWidgets(){
		for(Widget w : widgets){
			panel.remove(w);
		}
		widgets.clear();
	}
	
	public int getWidgetCount(){
		return widgets.size();
	}
}
