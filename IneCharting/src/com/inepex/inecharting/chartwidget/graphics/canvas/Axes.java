package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.graphics.gwtgraphics.AxisVisualizer;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.Curve;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis.Resolution;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.HorizontalTimeAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.AxisDrawingInfo.AxisType;

public class Axes implements HasViewport{
	private ModelManager mm;
	private IneChartProperties prop;
	private Axis xAxis;
	private Axis yAxis;
	private Axis y2Axis;
	private Context2d curveCanvas;
	private Context2d xAxisCanvas;
	private Context2d y2AxisCanvas;
	private Context2d yAxisCanvas;
	
	public Axes(Context2d curveCanvas, ModelManager mm, IneChartProperties prop) {
		this.curveCanvas = curveCanvas;
		this.mm = mm;
		this.prop = prop;
	}

	@Override
	public void moveViewport(double dx) {
		setViewport(mm.getViewportMin(), mm.getViewportMax());
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		if(xAxis != null && xAxis.getTickDistance() != 0)
			drawX(viewportMin, viewportMax);
		if(yAxis != null && yAxis.getTickDistance() != 0)
			drawY();
		if(y2Axis != null && y2Axis.getTickDistance() != 0)
			drawY2();
	}
	
	public void addXAxis(Axis xAxis, Context2d xAxisCanvas){
		this.xAxis = xAxis;
		this.xAxisCanvas = xAxisCanvas;
	} 
	
	public void addYAxis(Axis yAxis, Context2d yAxisCanvas){
		this.yAxis = yAxis;
		this.yAxisCanvas = yAxisCanvas;
	}
	
	public void addY2Axis(Axis y2Axis, Context2d y2AxisCanvas){
		this.y2Axis = y2Axis;
		this.y2AxisCanvas = y2AxisCanvas;
	} 
	
	public void	drawGridLines(){
		if(yAxis != null && yAxis.getTickDistance() != 0){
			VerticalAxisDrawingInfo info = (VerticalAxisDrawingInfo) yAxis.getDrawingInfo();
			if(info.hasGridLines()){
				double tick = getFirstVisibleTick(mm.getyMin(),yAxis);
				curveCanvas.beginPath();
				while(tick <= getLastVisibleTick(mm.getyMax(), yAxis)){
					curveCanvas.moveTo(0,mm.calculateYWithoutPadding(tick, mm.getyMin(), mm.getyMax()));
					curveCanvas.lineTo(prop.getChartCanvasWidth(),mm.calculateYWithoutPadding(tick, mm.getyMin(), mm.getyMax()));
					tick += yAxis.getTickDistance();
				}
				curveCanvas.save();
				curveCanvas.setStrokeStyle(info.getGridLineColor());
				curveCanvas.setLineWidth(info.getGridLineWidth());
				curveCanvas.stroke();
				curveCanvas.restore();
				curveCanvas.closePath();
			}
		}
		if(y2Axis != null && y2Axis.getTickDistance() != 0){
			VerticalAxisDrawingInfo info = (VerticalAxisDrawingInfo) y2Axis.getDrawingInfo();
			if(info.hasGridLines()){
				double tick = getFirstVisibleTick(mm.getY2Min(),y2Axis);
				curveCanvas.beginPath();
				while(tick <= getLastVisibleTick(mm.getY2Max(), y2Axis)){
					curveCanvas.moveTo(0,mm.calculateYWithoutPadding(tick, mm.getY2Min(), mm.getY2Max()));
					curveCanvas.lineTo(prop.getChartCanvasWidth(),mm.calculateYWithoutPadding(tick, mm.getY2Min(), mm.getY2Max()));
					tick += y2Axis.getTickDistance();
				}
				curveCanvas.save();
				curveCanvas.setStrokeStyle(info.getGridLineColor());
				curveCanvas.setLineWidth(info.getGridLineWidth());
				curveCanvas.stroke();
				curveCanvas.restore();
				curveCanvas.closePath();
			}
		}
		if(xAxis != null && xAxis.getTickDistance() != 0){
			HorizontalAxisDrawingInfo info = (HorizontalAxisDrawingInfo) xAxis.getDrawingInfo();
			if(info.hasGridLines()){
				mm.getAxisCalculator().setHorizontalAxis(xAxis, (int) measureMaxTickText());
				double tick = getFirstVisibleTick(mm.getViewportMin(), xAxis);	
				curveCanvas.beginPath();
				while(tick <= getLastVisibleTick(mm.getViewportMax(), xAxis)){
					curveCanvas.moveTo(mm.calculateXRelativeToViewport(tick),0);
					curveCanvas.lineTo(mm.calculateXRelativeToViewport(tick),prop.getChartCanvasHeight());
					tick += xAxis.getTickDistance();
				}
				curveCanvas.save();
				curveCanvas.setStrokeStyle(info.getGridLineColor());
				curveCanvas.setLineWidth(info.getGridLineWidth());
				curveCanvas.stroke();
				curveCanvas.restore();
				curveCanvas.closePath();
			}
		}
	}
	
	private double getFirstVisibleTick(double x,Axis axis){
		int multiplier = (int) ((x - axis.getFixTick()) / axis.getTickDistance());
		if(x > axis.getFixTick())
			multiplier++;
		return multiplier * axis.getTickDistance() + axis.getFixTick();
	}
	
	private double getLastVisibleTick(double x,Axis axis){
		int multiplier = (int) ((x - axis.getFixTick()) / axis.getTickDistance());
		if(x < axis.getFixTick())
			multiplier++;
		return multiplier * axis.getTickDistance() + axis.getFixTick();
	}
	
	private void drawX(double min, double max){
		HorizontalAxisDrawingInfo info = (HorizontalAxisDrawingInfo) xAxis.getDrawingInfo();
//		mm.getAxisCalculator().setHorizontalAxis(xAxis, (int) measureMaxTickText());
		double tick = getFirstVisibleTick(min, xAxis);
		
		xAxisCanvas.save();
		xAxisCanvas.setFillStyle(info.getBackgroundColor());
		xAxisCanvas.fillRect(0, 0, prop.getChartCanvasWidth(), info.getTickPanelHeight());
		xAxisCanvas.restore();
		
		xAxisCanvas.beginPath();
		xAxisCanvas.save();
		xAxisCanvas.setFont(info.getTickTextFontFamily() + " " + info.getTickTextFontWeight().toString() + " " + info.getTickTextFontStyle().toString());
		xAxisCanvas.setTextAlign(TextAlign.LEFT);
		xAxisCanvas.setTextBaseline(TextBaseline.TOP);
		xAxisCanvas.setFillStyle(info.getTickTextColor());
		while(tick <= getLastVisibleTick(max, xAxis)){
			xAxisCanvas.moveTo(mm.calculateXRelativeToViewport(tick),info.getTickPanelHeight());
			xAxisCanvas.lineTo(mm.calculateXRelativeToViewport(tick), 0);
			xAxisCanvas.fillText(formatData(tick, xAxis), mm.calculateXRelativeToViewport(tick) + 5, 7);
			tick += xAxis.getTickDistance();
		}
		xAxisCanvas.restore();
		xAxisCanvas.save();
		xAxisCanvas.setStrokeStyle(info.getTickColor());
		xAxisCanvas.setLineWidth(info.getTickLineWidth());
		xAxisCanvas.stroke();
		xAxisCanvas.restore();
	}
	
	private void drawY(){
		VerticalAxisDrawingInfo info = (VerticalAxisDrawingInfo) yAxis.getDrawingInfo();
		double tick = getFirstVisibleTick(mm.getyMin(),yAxis);
		curveCanvas.save();
		curveCanvas.setFont(info.getTickTextFontFamily() + " " + info.getTickTextFontWeight().toString() + " " + info.getTickTextFontStyle().toString());
		curveCanvas.setFillStyle(info.getTickTextColor());
		curveCanvas.setTextAlign(TextAlign.LEFT);
		curveCanvas.setTextBaseline(TextBaseline.BOTTOM);
		while(tick <= getLastVisibleTick(mm.getyMax(), yAxis)){
			curveCanvas.fillText(formatData(tick, yAxis), 2, mm.calculateYWithoutPadding(tick, mm.getyMin(), mm.getyMax()) );
			tick += yAxis.getTickDistance();
		}
		curveCanvas.restore();
	}
	
	private void drawY2(){
		VerticalAxisDrawingInfo info = (VerticalAxisDrawingInfo) y2Axis.getDrawingInfo();
		double tick = getFirstVisibleTick(mm.getY2Min(),y2Axis);
		curveCanvas.save();
		curveCanvas.setFont(info.getTickTextFontFamily() + " " + info.getTickTextFontWeight().toString() + " " + info.getTickTextFontStyle().toString());
		curveCanvas.setFillStyle(info.getTickTextColor());
		curveCanvas.setTextAlign(TextAlign.RIGHT);
		curveCanvas.setTextBaseline(TextBaseline.BOTTOM);
		while(tick <= getLastVisibleTick(mm.getY2Max(), y2Axis)){
			curveCanvas.fillText(formatData(tick, y2Axis), prop.getChartCanvasWidth() - 2, mm.calculateYWithoutPadding(tick, mm.getY2Min(), mm.getY2Max()) );
			tick += y2Axis.getTickDistance();
		}
		curveCanvas.restore();
	}
	
	private String formatData(double data, Axis axis){
		String text = "";
		if(axis instanceof HorizontalTimeAxis)
			text = DateTimeFormat.getFormat( ((HorizontalTimeAxisDrawingInfo)axis.getDrawingInfo()).getDateTimeFormat( ((HorizontalTimeAxis)axis).getResolution())).format(new Date((long)data) );
		else if(axis.getDrawingInfo().getType().equals(AxisType.TIME))
			text = DateTimeFormat.getFormat(axis.getDrawingInfo().getTickTextFormat()).format(new Date((long) data));
		else if(axis.getDrawingInfo().getType().equals(AxisType.NUMBER))
			text = NumberFormat.getFormat(axis.getDrawingInfo().getTickTextFormat()).format(data);
		return text;
	}
	
	private double measureMaxTickText(){
		HorizontalAxisDrawingInfo info = (HorizontalAxisDrawingInfo) xAxis.getDrawingInfo();
		xAxisCanvas.save();
		xAxisCanvas.setFont(info.getTickTextFontFamily() + " " + info.getTickTextFontWeight().toString() + " " + info.getTickTextFontStyle().toString());
		double width = 0;
		if(xAxis instanceof HorizontalTimeAxis){
			for(Resolution res:Resolution.values()){
				double w2  = xAxisCanvas.measureText(
						DateTimeFormat.getFormat(((HorizontalTimeAxisDrawingInfo)xAxis.getDrawingInfo()).getDateTimeFormat(res))
						.format(new Date((long)mm.getViewportMax()) )).getWidth();
				if(w2 > width)
					width = w2;
			}
		}
		else if(xAxis.getDrawingInfo().getType().equals(AxisType.TIME)){
			width = xAxisCanvas.measureText(DateTimeFormat.getFormat(xAxis.getDrawingInfo().getTickTextFormat()).format(new Date((long)mm.getViewportMax()))).getWidth();
		}
		else{
			width = xAxisCanvas.measureText(NumberFormat.getFormat(xAxis.getDrawingInfo().getTickTextFormat()).format(mm.getViewportMax())).getWidth();
		}
		xAxisCanvas.restore();
		return width;
	}
}
