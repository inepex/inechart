package com.inepex.inecharttest.client;

import java.util.TreeMap;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.linechart.LineChart.PointSelectionMode;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;


public class IneChartingNewImplTest{

	IneChart chart;
	LineChart lineChart;
	FlowPanel fp = new FlowPanel();
	public IneChartingNewImplTest() {
		
		long start = System.currentTimeMillis();
		 chart = new IneChart(1000, 600);		 
		 fp.add(chart);
		
		 lineChart = chart.createLineChart();
		 lineChart.setPointSelectionMode(PointSelectionMode.On_Point_Over);
		 Curve curve1 = new Curve(createDataset());
		 Color c = new Color("#9900dd");
		 LineProperties lp = new LineProperties(3, c);
		 curve1.setLineProperties(lp);
		 ShapeProperties sp = new ShapeProperties(lp, new Color(c.getColor(), 0.2));
		 curve1.setNormalPointShape(new Circle(10, sp));
		 curve1.setSelectedPointShape(new Rectangle(20, 14,sp));
		 curve1.setShadowOffsetX(1.3);
		 curve1.setShadowOffsetY(2);
		 curve1.setShadowColor(new Color("grey"));
		 
//		 Curve curve2 = new Curve(DataGenerator.generateRandomSpeedDataForChart(0, 1000, 250));
//		 c = new Color("#16dd16");
//		 lp = new LineProperties(2, c);
//		 curve2.setLineProperties(lp);
//		 sp  = new ShapeProperties(lp, new Color(c.getColor(), 0.3));
////		 curve2.setNormalPointShape(new Rectangle(5, 5, sp));
//		 curve2.setNormalPointShape(null);
//		 curve2.setSelectedPointShape(new Rectangle(10, 10, sp));
//		 curve2.setShadowOffsetX(1.3);
//		 curve2.setShadowOffsetY(2);
//		 curve2.setShadowColor(new Color("grey"));
		 
		 
		 lineChart.addCurve(curve1);
//		 lineChart.addCurve(curve2);
		 lineChart.getViewport().set(14, 50, 100, 220);
		 
		 fp.add(new Label(System.currentTimeMillis() - start +  "ms"));
		 RootPanel.get().add(fp);
		 chart.update();
		 chart.setUpdateInterval(100);
		
	}
	
	
	TreeMap<Double, Double> createDataset(){
		TreeMap<Double, Double> r = new TreeMap<Double, Double>();
		r.put(5d, 99d);
		r.put(15d, 199d);
		r.put(35d, 99d);
		r.put(55d,199d);
		return r;
	}
}
