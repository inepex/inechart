package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.axes.TickFactoryGWT;
import com.inepex.inechart.chartwidget.label.BubbleBox;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.misc.SelectionRange;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;

public class Crosshair extends LineChartInteractiveModule{

	SelectionRange selectionRange;
	TextContainer valueBox;
	LineProperties lineProperties;

	ArrayList<StyledLabel> styledLabels;
	GraphicalObjectContainer gos;

	String xFormat = "";
	String yFormat = "";
	TextProperties textProperties;
	
	BubbleBox xBubbleBox;
	BubbleBox yBubbleBox;
	
	Curve curve;

	Layer layer;

	boolean continuousTracking;
	int snapToValueRange;

	DataPoint focused;

	public Crosshair() {
		super();
		selectionRange = SelectionRange.Both;
		valueBox = Defaults.crosshairTextBox();
		lineProperties = Defaults.crosshair();	
		textProperties = Defaults.crosshairTextProperties();
		styledLabels = new ArrayList<StyledLabel>();
		gos = new GraphicalObjectContainer();
		snapToValueRange = Defaults.snapToValueRange;
		continuousTracking = true;
		xBubbleBox = Defaults.crosshairXBubbleBox();
		yBubbleBox = Defaults.crosshairYBubbleBox();
	}

	public Crosshair(Curve curve) {
		this();
		this.curve = curve;
	}

	protected void selectPoint(DataPoint dp) {
		if(!checkDependencies() || (focused != null && dp.compareTo(focused) == 0)){
			return;
		}
		focused = dp;
		removeGOsAndLabels();
		String formatString;
		if(selectionRange == SelectionRange.Both || selectionRange == SelectionRange.Vertical){
			formatString = xFormat;
			Line vertical = new Line(dp.canvasX, lineChart.getTopPadding(), dp.canvasX,
					lineChart.getTopPadding() + lineChart.getHeight(), 0, createContext(lineProperties));
			gos.addGraphicalObject(vertical);
			if(formatString == null || formatString.length() == 0){
				if(lineChart.getXAxis().getTicks().size() > 0 && lineChart.getXAxis().getTicks().get(0).getFormatString() != null){
					formatString = lineChart.getXAxis().getTicks().get(0).getFormatString();
				}
				else if(lineChart.getXAxis().getDefaultTick() != null && lineChart.getXAxis().getDefaultTick().getFormatString() != null){
					formatString = lineChart.getXAxis().getDefaultTick().getFormatString();
				}
			}
			if(formatString == null || formatString.length() == 0){
				if(lineChart.getXAxis().getAxisDataType() == AxisDataType.Number){
					formatString = Defaults.numberFormat;
				}
				else{
					formatString = Defaults.dateFormat;
				}
			}
			Text t = new Text(TickFactoryGWT.formatValue(lineChart.getXAxis().getAxisDataType(), dp.getData().getX(), formatString), textProperties);
			xBubbleBox.setText(t);
			xBubbleBox.setLeft((int) dp.canvasX);
			xBubbleBox.setTop(lineChart.getBottomEnd());
			styledLabels.add(xBubbleBox);
//			StyledLabel sl = new StyledLabel(t);
//			sl.setBackground(this.valueBox.getBackground());
//			int[] dim = moduleAssist.getLabelFactory().measureStyledLabel(sl);
//			sl.setLeft((int) (dp.canvasX - dim[0] / 2));
//			sl.setTop((int) (lineChart.getTopPadding() + lineChart.getHeight()));
//			styledLabels.add(sl);
		}
		if(selectionRange == SelectionRange.Both || selectionRange == SelectionRange.Horizontal){
			formatString = yFormat;
			Line horizontal = new Line(lineChart.getLeftPadding(), dp.canvasY, lineChart.getLeftPadding() + lineChart.getWidth(), 
					dp.canvasY, 0, createContext(lineProperties));
			gos.addGraphicalObject(horizontal);
			if(formatString == null || formatString.length() == 0){
				if(lineChart.getYAxis().getTicks().size() > 0 && lineChart.getYAxis().getTicks().get(0).getFormatString() != null){
					formatString = lineChart.getYAxis().getTicks().get(0).getFormatString();
				}
				else if(lineChart.getYAxis().getDefaultTick() != null && lineChart.getYAxis().getDefaultTick().getFormatString() != null){
					formatString = lineChart.getYAxis().getDefaultTick().getFormatString();
				}
			}
			if(formatString == null || formatString.length() == 0){
				if(lineChart.getYAxis().getAxisDataType() == AxisDataType.Number){
					formatString = Defaults.numberFormat;
				}
				else{
					formatString = Defaults.dateFormat;
				}
			}
			Text t = new Text(TickFactoryGWT.formatValue(lineChart.getYAxis().getAxisDataType(), dp.getData().getY(), formatString), textProperties);
			yBubbleBox.setText(t);
			yBubbleBox.setLeft(lineChart.getLeftPadding());
			yBubbleBox.setTop((int) dp.canvasY);
			styledLabels.add(yBubbleBox);
//			StyledLabel sl = new StyledLabel(t);
//			sl.setBackground(this.valueBox.getBackground());
//			int[] dim = moduleAssist.getLabelFactory().measureStyledLabel(sl);
//			sl.setLeft(lineChart.getLeftPadding() - dim[0]);
//			sl.setTop((int) (dp.canvasY - dim[1] / 2));
//			styledLabels.add(sl);

		}

		layer.getCanvas().addAllGraphicalObject(gos);

		layer.getCanvas().update();

		for(StyledLabel sl : styledLabels){
			moduleAssist.getLabelFactory().addAndDisplayStyledLabel(sl);
		}
	}

	Context createContext(LineProperties lp ){
		return new Context(lp.getLineColor().getAlpha(), lp.getLineColor().getColor(), lp.getLineWidth(), Defaults.colorString);
	}

	protected void removeGOsAndLabels(){
		for(GraphicalObject go : gos.getGraphicalObjects()){
			layer.getCanvas().removeGraphicalObject(go);
		}
		gos.removeAllGraphicalObjects();
		for(StyledLabel sl : styledLabels){
			moduleAssist.getLabelFactory().removeStyledLabel(sl);
		}
		styledLabels.clear();
	}

	@Override
	public void update() {
		clear();
	}

	protected void clear(){
		if(checkDependencies()){
			removeGOsAndLabels();
			layer.getCanvas().update();
		}
		focused = null;
	}

	protected DataPoint getDataPoint(MouseEvent<?> event){
		int[] point = lineChart.getCoords(event);
		DataPoint dp = lineChart.getClosestDataToPoint(point, curve);
		if(continuousTracking && (dp == null || snapToValueRange <= 0 ||
				Math.abs(dp.canvasX - point[0]) > snapToValueRange 
				)){
			final double[] valuePair = lineChart.getValuePair(event);
			if(valuePair == null){
				return null;
			}
			DataPoint before = curve.getPointBeforeX(valuePair[0]);
			if(before == null){
				return null;
			}
			DataPoint after = curve.getPointAfterX(valuePair[0]);
			if(after == null){
				return null;
			}
			double canvasY = DrawingAreaAssist.getYIntercept(point[0], before.canvasX, before.canvasY, after.canvasX, after.canvasY);
			final double dataY = DrawingAreaAssist.getYIntercept(valuePair[0], before.data.getX(), before.data.getY(), after.data.getX(), after.data.getY());
			dp = new DataPoint(valuePair[0], dataY);
			dp.canvasX = point[0];
			dp.canvasY = canvasY;
		}
		return dp;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(!checkDependencies()){
			return;
		}
		DataPoint dp = getDataPoint(event);
		if(dp == null){
			update();
		}
		else{
			selectPoint(dp);
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseOut(MouseEvent<?> event) {
		update();
	}

	@Override
	public void onMouseOver(MouseEvent<?> event) {
		if(!checkDependencies()){
			return;
		}
		DataPoint dp = getDataPoint(event);
		if(dp == null){
			update();
		}
		else{
			selectPoint(dp);
		}
	}

	@Override
	public void attach(ModuleAssist moduleAssist, LineChart lineChart) {
		if(curve == null && lineChart.curves.size() > 0){
			curve = lineChart.curves.get(lineChart.curves.size() - 1);
		}
		super.attach(moduleAssist, lineChart);
	}

	protected boolean checkDependencies(){
		if(curve == null){
			return false;
		}
		if(layer == null){
			layer = new Layer(Layer.TO_TOP);
			moduleAssist.addCanvasToLayer(layer);
			lineChart.getModuleLayer().addLayer(layer);
			moduleAssist.updateLayerOrder();
		}
		return true;
	}

	public SelectionRange getSelectionRange() {
		return selectionRange;
	}

	public void setSelectionRange(SelectionRange selectionRange) {
		this.selectionRange = selectionRange;
	}

	public TextContainer getValueBox() {
		return valueBox;
	}

	public void setValueBox(TextContainer valueBox) {
		this.valueBox = valueBox;
	}

	public LineProperties getLineProperties() {
		return lineProperties;
	}

	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}

	public String getxFormat() {
		return xFormat;
	}

	public void setxFormat(String xFormat) {
		this.xFormat = xFormat;
	}

	public String getyFormat() {
		return yFormat;
	}

	public void setyFormat(String yFormat) {
		this.yFormat = yFormat;
	}

	public TextProperties getTextProperties() {
		return textProperties;
	}

	public void setTextProperties(TextProperties textProperties) {
		this.textProperties = textProperties;
	}

	public Curve getCurve() {
		return curve;
	}

	public void setCurve(Curve curve) {
		this.curve = curve;
	}

	public boolean isContinuousTracking() {
		return continuousTracking;
	}

	public void setContinuousTracking(boolean continuousTracking) {
		this.continuousTracking = continuousTracking;
	}

	public int getSnapToValueRange() {
		return snapToValueRange;
	}

	public void setSnapToValueRange(int snapToValueRange) {
		this.snapToValueRange = snapToValueRange;
	}

}
