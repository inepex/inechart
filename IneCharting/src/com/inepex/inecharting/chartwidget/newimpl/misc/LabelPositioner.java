package com.inepex.inecharting.chartwidget.newimpl.misc;


import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.inepex.inecharting.chartwidget.newimpl.IneChartModul;
import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;

public class LabelPositioner extends IneChartModul {

	ArrayList<PositionedLabel> labels;
	TreeMap<PositionedLabel, InlineLabel> displayedLabels;
	int dx, dy;
	
	boolean changed = false;
	AbsolutePanel panel;
	
	public LabelPositioner(AbsolutePanel panel, int dx, int dy, DrawingAreaImplCanvas canvas) {
		super(canvas);
		this.panel = panel;
		this.dx = dx;
		this.dy = dy;
		labels = new ArrayList<PositionedLabel>();
		displayedLabels = new TreeMap<PositionedLabel, InlineLabel>();
	}
	
	public void addLabel(PositionedLabel label){
		labels.add(label);
		redrawNeeded = changed = true;
	}
	
	public void removeLabel(PositionedLabel label){
		labels.remove(label);
		InlineLabel displayed = displayedLabels.get(label);
		if(displayed != null)
			panel.remove(displayed);
		redrawNeeded = changed = true;
	}


	
	public ArrayList<PositionedLabel> getVisibleLabels(){
		ArrayList<PositionedLabel> ret = new ArrayList<PositionedLabel>();
		for(PositionedLabel label:labels){
			if(label.absolutePosition || (label.posX >= viewportMin && label.posX <= viewportMax))
				ret.add(label);
		}
		return ret;
	}
	
	public void hideLabels(boolean onlyRelativePositionedLabels){
		ArrayList<PositionedLabel> toRemove = new ArrayList<PositionedLabel>();
		for(PositionedLabel label : displayedLabels.keySet()){
			if(onlyRelativePositionedLabels && label.absolutePosition)
				continue;
			panel.remove(displayedLabels.get(label));
			toRemove.add(label);
		}
		for(PositionedLabel label : toRemove){
			displayedLabels.remove(label);
		}
	}

	void displayVisibleLabels(boolean onlyRelativePositionedLabels){
		for(PositionedLabel label : getVisibleLabels()){
			if(label.absolutePosition)
				panel.add(createLabel(label), (int)label.posX + dx, (int) label.posY + dy);
			else
				panel.add(createLabel(label), getPositionRelativeToViewport(label.posX) + dx, (int) label.posY + dy);
		}
	}
	
	static InlineLabel createLabel(PositionedLabel label){
		InlineLabel lbl = new InlineLabel(label.text);
		//TODO
		return lbl;
	}

	@Override
	public void update() {
		if(viewportMoved || viewportResized || changed){
			if(changed){
				hideLabels(false);
				displayVisibleLabels(false);
			}
			else{
				hideLabels(true);
				displayVisibleLabels(true);
			}
			
		}
		
		
		
		redrawNeeded = changed = viewportResized = viewportMoved = false;
	}

}
