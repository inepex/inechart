package com.inepex.inecharting.chartwidget.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.Visualizer;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis;
import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo.AxisType;
import com.inepex.inecharting.chartwidget.properties.HorizontalTimeAxisDrawingInfo;
import com.inepex.inecharting.misc.AbsolutePositioner;

public class TickTextVisualizer implements HasViewport{
	
	private ModelManager mm;
	private Axis axis;
	private AbsolutePositioner aPositioner;
	private boolean horizontal;
	private DateTimeFormat dtf;
	private NumberFormat nf;
	private ArrayList<Double> actualTicks;
	private final int xShift_Horizontal = 4;
	private final int yShift_Horizontal = 2;
	private final int xShift_Vertical = 4;
	private final int yShift_Vertical = 1;
	
	public TickTextVisualizer(Widget canvas, Axis axis, ModelManager mm, boolean horizontal) {
		
		this.axis = axis;
		this.aPositioner = new AbsolutePositioner((AbsolutePanel) canvas);
		this.actualTicks = new ArrayList<Double>();
		this.mm = mm;
		if(!(axis instanceof HorizontalTimeAxis)){
			if(axis.getDrawingInfo().getType().equals(AxisType.TIME))
				dtf = DateTimeFormat.getFormat(axis.getDrawingInfo().getTickTextFormat());
			else if(axis.getDrawingInfo().getType().equals(AxisType.NUMBER))
				nf = NumberFormat.getFormat(axis.getDrawingInfo().getTickTextFormat());
		}
		this.horizontal = horizontal;
	}

	/** 
	 * @param x
	 * @return the first visible tick with a value bigger than x
	 */
	public double getFirstVisibleTick(double x){
		int multiplier = (int) ((x - axis.getFixTick()) / axis.getTickDistance());
		if(x > axis.getFixTick())
			multiplier++;
		return multiplier * axis.getTickDistance() + axis.getFixTick();
	}
	
	/** 
	 * @param x
	 * @return the last visible tick with a value smaller than x
	 */
	public double getLastVisibleTick(double x){
		int multiplier = (int) ((x - axis.getFixTick()) / axis.getTickDistance());
		if(x < axis.getFixTick())
			multiplier++;
		return multiplier * axis.getTickDistance() + axis.getFixTick();
	}

	@Override
	public void moveViewport(double dx) {
		if(horizontal){
			aPositioner.moveWidgets(mm.calculateDistance(-dx), 0);
			//viewport slides right - text and ticks go left
			if(dx > 0){
				double actualData = getFirstVisibleTick(mm.getViewportMin());
				if(actualData <= actualTicks.get(actualTicks.size()-1))
					actualData = actualTicks.get(actualTicks.size()-1) + axis.getTickDistance();
				for(; actualData <= getLastVisibleTick(mm.getViewportMax()); actualData += axis.getTickDistance()){
					actualTicks.add(actualData);
					aPositioner.addWidget(
							createLabel(actualData),
							mm.calculateDistance(actualData - mm.getViewportMin())+ xShift_Horizontal,
							yShift_Horizontal); //the y pos TODO
				}
			}
			else{
				double actualData = getLastVisibleTick(mm.getViewportMax());
				if(actualData >= actualTicks.get(0))
					actualData = actualTicks.get(0) - axis.getTickDistance();
				for(; actualData >= getFirstVisibleTick(mm.getViewportMin()); actualData -= axis.getTickDistance()){
					actualTicks.add(actualData);
					aPositioner.addWidget(
							createLabel(actualData),
							mm.calculateDistance(actualData - mm.getViewportMin()) + xShift_Horizontal,
							yShift_Horizontal); //the y pos TODO
				}
			}
			//remove tickpositions outside vp
			Iterator<Double> tickIt = actualTicks.iterator();
			while (tickIt.hasNext()) {
				Double act = tickIt.next();
				if(act < mm.getViewportMin() || act > mm.getViewportMax())
					tickIt.remove();			
			}
			Collections.sort(actualTicks);
		}
	}

	@Override
	public void setViewPort(double viewportMin, double viewportMax) {
		if(horizontal){
			aPositioner.removeAllWidgets();
			if(axis instanceof HorizontalTimeAxis){
				dtf = DateTimeFormat.getFormat(((HorizontalTimeAxisDrawingInfo)axis.getDrawingInfo()).getDateTimeFormat(((HorizontalTimeAxis) axis).getResolution()));
			}
			for(double actualData = getFirstVisibleTick(viewportMin); actualData <= getLastVisibleTick(viewportMax); actualData += axis.getTickDistance()){
				actualTicks.add(actualData);
				aPositioner.addWidget(
						createLabel(actualData),
						mm.calculateDistance(actualData - viewportMin) + xShift_Horizontal,
						yShift_Horizontal); //the y pos TODO
			}
		}
	}
	
	private String formatData(double data){
		String text = "";
		if(axis.getDrawingInfo().getType().equals(AxisType.TIME))
			text = dtf.format(new Date((long) data));
		else if(axis.getDrawingInfo().getType().equals(AxisType.NUMBER))
			text = nf.format(data);
		return text;
	}
	
	private Label createLabel(double data){
		InlineLabel label = new InlineLabel(formatData(data));
		DOM.setElementAttribute(label.getElement(), "backgroundColor", axis.getDrawingInfo().getTickTextBackgroundColor());
		DOM.setElementAttribute(label.getElement(), "color", axis.getDrawingInfo().getTickTextColor());
		DOM.setElementAttribute(label.getElement(), "fontFamily", axis.getDrawingInfo().getTickTextFontFamily());			
		DOM.setElementAttribute(label.getElement(), "opacity", axis.getDrawingInfo().getTickTextBackgroundOpacity()+"");
		DOM.setElementAttribute(label.getElement(), "fontStyle", axis.getDrawingInfo().getTickTextFontStyle().toString());
		DOM.setElementAttribute(label.getElement(), "fontWeight", axis.getDrawingInfo().getTickTextFontWeight().toString());
		DOM.setElementAttribute(label.getElement(), "padding", "0px");
		DOM.setElementAttribute(label.getElement(), "margin", "0px");
		return label;
	}
	
	public ArrayList<Double> getActualTicks() {
		return actualTicks;
	}

	public void displayVerticalTicks(Curve.Axis y){
		if(!horizontal){
			aPositioner.removeAllWidgets();
			double min,max;
			if(y.equals(Curve.Axis.Y)){
				min = mm.getyMin();
				max = mm.getyMax();
			}
			else if(y.equals(Curve.Axis.Y2)){
				min = mm.getY2Min();
				max = mm.getY2Max();
			}
			else{
				return;
			}
			for(double actualData = axis.getFixTick(); actualData <= max; actualData += axis.getTickDistance()){
				actualTicks.add(actualData);
				Label label = createLabel(actualData);
				
				if(y.equals(Curve.Axis.Y)){
					aPositioner.addWidget(
							label,
							xShift_Vertical,
							mm.calculateYWithoutPadding(actualData, min, max) + yShift_Vertical);
				}
				else if(y.equals(Curve.Axis.Y2)){
					AbsolutePositioner.setRight(
							AbsolutePositioner.setTop(
								aPositioner.addWidget(label),
								mm.calculateYWithoutPadding(actualData, min, max) + yShift_Vertical),
							xShift_Vertical);							
				}	
			}
		}
	}
}
