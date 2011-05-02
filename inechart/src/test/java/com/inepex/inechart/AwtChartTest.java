package com.inepex.inechart;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.linechart.LineChartProperties;
import com.inepex.inechart.chartwidget.linechart.Point;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;

public class AwtChartTest {

	public static void main(String[] args) {
		IneAwtChart chart = new IneAwtChart(800, 400);
		LineChart lc = chart.createLineChart();
		lc.setProperties(LineChartProperties.getDefaultLineChartProperties());
		lc.getProperties().setAutoCalcViewport(true);
		lc.getProperties().setLeftPadding(50);
		
//		lc.addCurve(getTestCurve("#0055FF"));
		lc.addCurve(getTestCurve2("#363A42"));

		
		lc.calculateAxes();
		lc.getyAxis().setMin(0.0);
		//TODO: set axis labels
		lc.getyAxis().addTick(new Tick(0.0, "0"));
		lc.getxAxis().addTick(new Tick(1.0, "2010.jan"));
		lc.getxAxis().addTick(new Tick(2.0, "2010.feb"));
		
		lc.getyAxis().addTick(new Tick(0.3, "min min"));
		//TODO: set backround colors
		lc.getyAxis().fillBetweenTicks(new Tick(0.1), new Tick(0.3), new Color("red"));
		
		//TODO: draw some kind of legend
		
		chart.update();
		
		chart.saveToFile("chart.png");
	}

	private static Curve getTestCurve(String color){
		Curve c = new Curve();
		c.setLineProperties(new LineProperties(1, new Color(color)));
		
		for (double x = 0.0; x < 24; x++){
			c.addPoint(new Point(x, Math.random() * 0.5));
		}
		
		return c;
	}
	
	private static Curve getTestCurve2(String color){
		Curve c = new Curve();
		c.setLineProperties(new LineProperties(1, new Color(color)));
		
		c.addPoint(new Point(1.0, 10.0));
		c.addPoint(new Point(2.0, 15.0));
		c.addPoint(new Point(3.0, 10.0));
		
		return c;
	}
	
}
