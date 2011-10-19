package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.border.LineBorder;

import org.w3c.css.sac.LangCondition;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.TickFactoryGWT;
import com.inepex.inechart.chartwidget.event.PointHoverListener;
import com.inepex.inechart.chartwidget.label.GWTLabelFactory;
import com.inepex.inechart.chartwidget.label.LabelFactory;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.misc.SelectionRange;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;

public class Crosshair implements LineChartSelection, PointHoverListener{

	Axes axes;
	LabelFactory labelFactory;
	DrawingArea drawingArea;
	SelectionRange selectionRange;
	TextContainer valueBox;
	LineProperties lineProperties;

	LineChart lineChart;

	ArrayList<StyledLabel> styledLabels;
	GraphicalObjectContainer gos;

	String xFormat = "";
	String yFormat = "";
	TextProperties textProperties;

	public Crosshair(ModuleAssist moduleAssist) {
		this.axes = moduleAssist.getAxes();
		this.labelFactory = moduleAssist.getLabelFactory();
		selectionRange = SelectionRange.Both;
		valueBox = Defaults.crosshairTextBox();
		lineProperties = Defaults.crosshair();	
		textProperties = Defaults.tickTextProperties();

		styledLabels = new ArrayList<StyledLabel>();
		gos = new GraphicalObjectContainer();

	}

	@Override
	public void setDrawingArea(DrawingArea drawingArea) {
		this.drawingArea = drawingArea;
	}

	@Override
	public void selectPoint(Curve c, DataPoint dp) {
		for(GraphicalObject go : gos.getGraphicalObjects()){
			drawingArea.removeGraphicalObject(go);
		}
		gos.removeAllGraphicalObjects();
		for(StyledLabel sl : styledLabels){
			labelFactory.removeStyledLabel(sl);
		}
		styledLabels.clear();
		if(selectionRange == SelectionRange.Both || selectionRange == SelectionRange.Vertical){
			Line vertical = new Line(dp.getActualXPos(), lineChart.getTopPadding(), dp.getActualXPos(), lineChart.getTopPadding() + lineChart.getHeight(), 0, createContext(lineProperties));
			gos.addGraphicalObject(vertical);
			if(xFormat == null || xFormat.length() == 0){
				if(lineChart.getXAxis().getTicks().size() > 0){
					xFormat = lineChart.getXAxis().getTicks().get(0).getFormatString();
				}
			}
			Text t = new Text(TickFactoryGWT.formatValue(lineChart.getXAxis(), dp.getX(), xFormat), textProperties);
			StyledLabel sl = new StyledLabel(t);
			sl.setBackground(this.valueBox.getBackground());
			int[] dim = labelFactory.measureStyledLabel(sl);
			sl.setLeft((int) (dp.getActualXPos() - dim[0] / 2));
			sl.setTop((int) (lineChart.getTopPadding() + lineChart.getHeight()));
			styledLabels.add(sl);
		}
		if(selectionRange == SelectionRange.Both || selectionRange == SelectionRange.Horizontal){
			Line horizontal = new Line(lineChart.getLeftPadding(), dp.getActualYPos(), lineChart.getLeftPadding() + lineChart.getWidth(), dp.getActualYPos(), 0, createContext(lineProperties));
			gos.addGraphicalObject(horizontal);
			if(yFormat == null || yFormat.length() == 0){
				if(lineChart.getYAxis().getTicks().size() > 0){
					yFormat = lineChart.getYAxis().getTicks().get(0).getFormatString();
				}
			}
			Text t = new Text(TickFactoryGWT.formatValue(lineChart.getYAxis(), dp.getY(), yFormat), textProperties);
			StyledLabel sl = new StyledLabel(t);

			sl.setBackground(this.valueBox.getBackground());
			int[] dim = labelFactory.measureStyledLabel(sl);
			sl.setLeft(lineChart.getLeftPadding() - dim[0]);
			sl.setTop((int) (dp.getActualYPos() - dim[1] / 2));
			
			styledLabels.add(sl);
		}

		drawingArea.addAllGraphicalObject(gos);

		drawingArea.update();
		
		for(StyledLabel sl : styledLabels){
			labelFactory.addAndDisplayStyledLabel(sl);
		}
	}

	Context createContext(LineProperties lp ){
		return new Context(lp.getLineColor().getAlpha(), lp.getLineColor().getColor(), lp.getLineWidth(), Defaults.colorString);
	}

	@Override
	public void deselectPoint(Curve c, DataPoint dp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineChart(LineChart lineChart) {
		this.lineChart = lineChart;
	}


	@Override
	public void onPointHover(TreeMap<Curve, DataPoint> hoveredPoints) {
		if(hoveredPoints.size() > 0){
			selectPoint(hoveredPoints.firstKey(), hoveredPoints.get(hoveredPoints.firstKey()));
		}
	}


}
