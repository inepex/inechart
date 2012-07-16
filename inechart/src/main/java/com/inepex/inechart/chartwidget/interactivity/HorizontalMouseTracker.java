package com.inepex.inechart.chartwidget.interactivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.axes.TickFactoryGWT;
import com.inepex.inechart.chartwidget.label.BubbleBox;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.Line;

public class HorizontalMouseTracker extends AbstractInteractiveModule {

	BubbleBox label;
	Line tracker;
	Layer layer;
	LineProperties lineProperties;
	TextProperties textProperties;
	String format;
	
	public HorizontalMouseTracker() {
		lineProperties = Defaults.crosshair();	
		textProperties = Defaults.crosshairTextProperties();
		label = Defaults.crosshairXBubbleBox();
	}
	
	@Override
	protected void onClick(ClickEvent event) {}

	@Override
	protected void onMouseUp(MouseUpEvent event) {}

	@Override
	protected void onMouseOver(MouseOverEvent event) {
		trackMouse(event);
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {}

	@Override
	protected void onMouseOut(MouseOutEvent event) {
		clear();
	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		trackMouse(event);
	}

	@Override
	public void preUpdate() {}

	@Override
	public void update() {
		checkDependencies();
		clear();
	}
	
	private void trackMouse(MouseEvent<?> event){
		checkDependencies();
		layer.getCanvas().removeAllGraphicalObjects();
		if(label != null){
			moduleAssist.getLabelFactory().removeStyledLabel(label);
		}
		int[] coords = relatedIneChartModule2D.getCoords(event);
		if(!relatedIneChartModule2D.isInsideModul(coords[0], coords[1])) {
			layer.getCanvas().update();
			return;
		}
		tracker = new Line(coords[0], relatedIneChartModule2D.getTopPadding(), coords[0], relatedIneChartModule2D.getBottomEnd(), 0, createContext(lineProperties));
		String formatString = format;
		if(formatString == null || formatString.length() == 0){
			if(relatedIneChartModule2D.getXAxis().getTicks().size() > 0 && relatedIneChartModule2D.getXAxis().getTicks().get(0).getFormatString() != null){
				formatString = relatedIneChartModule2D.getXAxis().getTicks().get(0).getFormatString();
			}
			else if(relatedIneChartModule2D.getXAxis().getDefaultTick() != null && relatedIneChartModule2D.getXAxis().getDefaultTick().getFormatString() != null){
				formatString = relatedIneChartModule2D.getXAxis().getDefaultTick().getFormatString();
			}
		}
		if(formatString == null || formatString.length() == 0){
			if(relatedIneChartModule2D.getXAxis().getAxisDataType() == AxisDataType.Number){
				formatString = Defaults.numberFormat;
			}
			else{
				formatString = Defaults.dateFormat;
			}
		}
		double value = relatedIneChartModule2D.getValueForCanvasX(coords[0]);
		Text t = new Text(TickFactoryGWT.formatValue(relatedIneChartModule2D.getXAxis().getAxisDataType(), value, formatString), textProperties);
		label.setText(t);
		label.setLeft(coords[0]);
		label.setTop(relatedIneChartModule2D.getBottomEnd());
		moduleAssist.getLabelFactory().addAndDisplayStyledLabel(label);
		layer.getCanvas().addGraphicalObject(tracker);
		layer.getCanvas().update();
		moduleAssist.updateLayerOrder();
	}
	
	Context createContext(LineProperties lp ){
		return new Context(lp.getLineColor().getAlpha(), lp.getLineColor().getColor(), lp.getLineWidth(), Defaults.colorString);
	}
	
	private void clear(){
		layer.getCanvas().removeAllGraphicalObjects();
		layer.getCanvas().update();
		moduleAssist.getLabelFactory().removeStyledLabel(label);
//		label = null;
		tracker = null;
	}

	protected void checkDependencies(){
		if(layer == null){
			layer = new Layer(Layer.TO_TOP);
			layer.setRelatedModule(relatedIneChartModule2D);
			moduleAssist.addCanvasToLayer(layer);
			relatedIneChartModule2D.getModuleLayer().addLayer(layer);
			moduleAssist.updateLayerOrder();
		}
	}

	public LineProperties getLineProperties() {
		return lineProperties;
	}

	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}

	public TextProperties getTextProperties() {
		return textProperties;
	}

	public void setTextProperties(TextProperties textProperties) {
		this.textProperties = textProperties;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
}
