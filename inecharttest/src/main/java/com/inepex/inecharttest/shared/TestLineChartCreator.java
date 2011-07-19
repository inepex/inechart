package com.inepex.inecharttest.shared;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.linechart.Point;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;

public class TestLineChartCreator {

	private static Curve curve = getTestCurve("red");
	
	public static void setupTestLines(LineChart lc){
		lc.setAutoScaleViewport(true);
		lc.setLeftPadding(50);
		lc.setAutoCreateAxes(false);
//		lc.getYAxis().setMin(0.0);
//		lc.getYAxis().setMax(5.0);
//		lc.getViewport().setYMin(0.0);
//		lc.getViewport().setXMin(0.0);
		
		lc.addCurve(getTestCurve("#0055FF"));
		lc.addCurve(getFixCurve());
		lc.addCurve(getTestCurve2("#363A42"));

//		TODO: set axis labels
		lc.getYAxis().setFilterFrequentTicks(true);
		lc.getXAxis().setFilterFrequentTicks(true);
		
		lc.getYAxis().addTick(new Tick(10.0, "1"));
//		lc.getXAxis().addTick(new Tick(0.0, "0"));
		lc.getXAxis().addTick(new Tick(1.0, "2010.jan"));
		lc.getXAxis().addTick(new Tick(2.0, Defaults.solidLine(), null, 5,  "2010.feb"));
		lc.getXAxis().addTick(new Tick(3.0, null, null, 5,  "2010.márc"));
		lc.getXAxis().addTick(new Tick(3.1, Defaults.solidLine(), null, 5,  "").setUnfiltereble(true));
		lc.getXAxis().addTick(new Tick(4.0, null, null, 5,  "2010.ápr"));
		lc.getXAxis().addTick(new Tick(5.0, null, null, 5,  "2010.május"));
		lc.getXAxis().addTick(new Tick(6.0, null, null, 5,  "2010.június"));
//		
		lc.getYAxis().addTick(new Tick(0.3, "min min"));
		lc.getYAxis().addTick(new Tick(4.3, "na"));
		lc.getYAxis().addTick(new Tick(8, "hello2"));
		lc.getYAxis().addTick(new Tick(12, "a"));
		//TODO: set backround colors
//		lc.getYAxis().fillBetweenTicks(new Tick(0.3), new Tick(5.0), new Color("red"));
		
	}
	
	public static void setTestBars(BarChart bc){
		bc.setAutoScaleViewport(false);
		bc.setLeftPadding(50);
		bc.getYAxis().setMin(0.0);
		bc.getYAxis().setMax(5.0);
		
//		bc.addDataSet(getTestCurve("black"));
		bc.addDataSet(getFixCurve());
		
	}
	
	private static Curve getFixCurve(){
		Curve c = new Curve();
		c.setLineProperties(new LineProperties(1, new Color("black")));
		
		c.addPoint(new Point(1.0, 2.0));
		c.addPoint(new Point(2.0, 3.0));
		c.addPoint(new Point(3.0, 4.0));
		
		return c;
	}
	
	public static void setUseSameViewports(IneChartModul2D viewportHost, IneChartModul2D viewportPeer){
		viewportPeer.setViewport(viewportHost.getViewport());
		viewportPeer.setAutoScaleViewport(false);
	}
	
	private static Curve getTestCurve(String color){
		Curve c = new Curve();
		c.setLineProperties(new LineProperties(1, null));
		
		for (double x = 0.0; x < 24; x++){
			c.addPoint(new Point(x, Math.random() + 3));
		}
		
		return c;
	}
	
	private static Curve getTestCurve2(String color){
		Curve c = new Curve();
		c.setLineProperties(new LineProperties(1, new Color(color)));
		
		c.addPoint(new Point(0.0, 0.0));
//		c.addPoint(new Point(2.0, 15.0));
//		c.addPoint(new Point(3.0, 10.0));
		
		return c;
	}
}
