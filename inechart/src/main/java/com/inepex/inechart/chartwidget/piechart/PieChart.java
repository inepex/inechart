package com.inepex.inechart.chartwidget.piechart;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.label.HasLegend;
import com.inepex.inechart.chartwidget.label.Legend;
import com.inepex.inechart.chartwidget.label.LegendEntry;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.piechart.Pie.Slice;
import com.inepex.inegraphics.impl.client.GraphicalObjectEventHandler;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.gobjects.Arc;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Text;

public class PieChart extends IneChartModul implements HasLegend,GraphicalObjectEventHandler, MouseMoveHandler, MouseOutHandler {
	
	public static interface PieLabeler {
		public String getLabel(String name, String value, String pctg);
	}

	Pie pie;
	
	PieLabeler pieLabeler;
	boolean showLegend = true;
	Legend legend = new Legend();

	public PieChart(DrawingArea canvas) {
		super(canvas);
		pieLabeler = new PieLabeler() {
			
			@Override
			public String getLabel(String name, String value, String pctg) {
				return name + "\n" + pctg + "%";
			}
		};
	}

	public void setPie(Pie pie) {
		this.pie = pie;
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseClick(ArrayList<GraphicalObject> sourceGOs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseMove(ArrayList<GraphicalObject> mouseOver,
			ArrayList<GraphicalObject> mouseOut) {
		// TODO Auto-generated method stub

	}

	
	
	@Override
	public void update() {
		if (pie == null)
			throw new RuntimeException(
					"There is nothing to show. You should call setPie on PieChart!");
		
		// calculate angles
		// create graphical objects
		Double actualAngle = 0.0;
		int basePointX = canvas.getWidth() / 2;
		int basePointY = canvas.getHeight() / 2;
		Long radius = Math
				.round((canvas.getWidth() < canvas.getHeight()) ? canvas
						.getWidth() / 2.0 * 0.9
						: canvas.getHeight() / 2.0 * 0.9);
		double start = 0.0;
		double finish = 0.0;
		for (String key : pie.getKeys()) {
			// add arcs
			double pctg = pie.percentages.get(key);
			if (pctg < 0.5)
				continue;
			Double angle = 360.0 * pctg / 100.0;
			finish = start + angle;
			Double f1 = Math.min(90.0 - start, 90.0 - finish);
			Double f2 = Math.max(90.0 - start, 90.0 - finish);

			Context context = new Context(1.0, "white", 4, pie.getColorMap()
					.get(key), 0.0, 0.0, 0.0, "white");
			Arc arc = new Arc(basePointX, basePointY, 1, context, true, true,
					radius.intValue(), f1, f2 - f1);
			getGraphicalObjectContainer().addGraphicalObject(arc);
			actualAngle += angle;
			start = finish;
//			double f = ((f1 + f2) / 2 + 90.0) * Math.PI / 180.0;
//			double d = radius * 0.7;
////			Double textX = basePointX + d * Math.sin(f);
//			Double textY = basePointY + d * Math.cos(f);
//			String pieLabel = pieLabeler.getLabel(key, "" + pie.getDataMap().get(key), "" + Math.round(pctg));
//			Double offsetY = pieLabel.split("\n").length * 10.0;
//			
//			Text label = new Text(pieLabel, textX.intValue(), textY.intValue()  - offsetY.intValue());
//			label.setzIndex(2);
//			label.setContext(getTextContext());
//			getGraphicalObjectContainer().addGraphicalObject(label);
			
		
		}
	}

	private Context getTextContext() {
		return new Context(0.5, "black", 1, "white", 0.0, 0.0, 0.0, "black");
	}

	@Override
	public boolean redrawNeeded() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setPieLabeler(PieLabeler pieLabeler) {
		this.pieLabeler = pieLabeler;
	}

	@Override
	public List<LegendEntry> getLegendEntries() {
		ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
		for(String key : pie.sliceMap.keySet()){
			Slice s = pie.sliceMap.get(key);
			entries.add(new LegendEntry(s, s.c));
		}
		return entries;
	}

	@Override
	public boolean showLegend() {
		return showLegend;
	}

	@Override
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	@Override
	public Legend getLegend() {
		return legend;
	}

	@Override
	public void setLegend(Legend legend) {
		this.legend = legend;
	}
	
	

}
