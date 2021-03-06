package com.inepex.inecharttest.ineawtcharttest;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.inepex.inechart.awtchart.IneAwtChart;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.axes.Axis.AxisPosition;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.label.Legend.LegendEntryLayout;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Circle;

public class ApfBarChartTest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4542081093511966555L;

	private IneAwtChart chart;

	public ApfBarChartTest() {
		super("Apf-like chart test");
		setSize(1300, 700);
		getContentPane().setLayout(new GridLayout(1, 1, 10, 0));

		getContentPane().setBackground(Color.lightGray);

		init();
		getContentPane().add(new ChartPanel());

		chart.update();
		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(wndCloser);

		setVisible(true);
		chart.saveToFile("multiLineChart.png");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ApfBarChartTest();

	}

	String[] curveNames = { 
			"Airspace Infringement", 
			"Near Controlled Flight Into Terrain", 
			"Level Bust",
			"Prolonged Loss of Communications" 
			};

	Double[][] values = { 
			{ 12.0, 11.0, 8.0, 16.0, 10.0, 9.0, 16.0, 10.0, 14.0, 11.0, 8.0, 9.0 },
			{ 3.0, 6.0, 1.0, 2.0, 4.0, 1.0, 5.0, 1.0, 3.0, 3.0, 1.0, 2.0 },
			{ 0.0, 3.0, 1.0, 5.0, 4.0, 4.0, 3.0, 2.0, 7.0, 3.0, 2.0, 4.0 },
			{ 4.0, 0.0, 3.0, 6.0, 1.0, 2.0, 5.0, 1.0, 3.0, 3.0, 1.0, 2.0 },
			{ 1.2818774975743393E-6, 1.2140364313571E-6, 1.7912267015441461E-6}


	};

	private Curve getCurve(int i) {
		TreeMap<Double, Double> data = new TreeMap<Double, Double>();
		for (int j = 0; j < values[i].length; j++) {
			data.put(new Double(j), values[i][j]);
		}
		Curve c = new Curve(data);
		c.setName(curveNames[i]);
		// set curve properties
		c.setHasShadow(false);
//		c.set

		ShapeProperties sp_bogyo = new ShapeProperties(
				c.getLineProperties(),
				new com.inepex.inechart.chartwidget.properties.Color("white"));
		// ez igy 5px vonalvastagságú kört rajzol, közepe üres
		// az hogy fehér színnel legyen kitöltve a közepe megoldható: new
		// ShapeProperties(new LineProperties(5, new Color("blue")), new
		// Color("white");
		Circle bogyo = new Circle(5, sp_bogyo); // inechartos Circle class nem
													// graphicsos
		c.setNormalPointShape(bogyo); // c egy Curve objektum
		c.setUseDefaultPointShape(false);
		return c;

	}

	private void init() {
		chart = new IneAwtChart(1200, 600);
		chart.setName("Potential/Near Collisions Air");
		chart.getName().getTextProperties().setFontSize(26);
		BarChart barChart = chart.createBarChart();
		Curve c = getCurve(0);
		barChart.addDataSet(c);
		
		barChart.setLookout(c.getName().getText(), new ShapeProperties(new com.inepex.inechart.chartwidget.properties.Color("red")));
		// add curves
//		for (int i = 0; i < curveNames.length; i++) {
//			barChart.addDataSet(getCurve(i));
//		}

		// set linechart properties
		barChart.setAutoScaleViewport(false);
		barChart.setUseViewport(false);
		barChart.setAutoCalcPadding(true);
		barChart.setColorSet(ColorSet.flotColorSet());
		barChart.setMinRightPadding(30);
		barChart.setMinBottomPadding(30);
		barChart.setMinTopPadding(30);
		barChart.setMinLeftPadding(80);
		
		//set legend properties
		barChart.getLegend().setLegendEntryLayout(LegendEntryLayout.AUTO);
		barChart.getLegend().setHorizontalPosition(HorizontalPosition.Right);
		barChart.getLegend().setFixedX(80);
		barChart.getLegend().setFixedY(80);
		barChart.getLegend().setMaxWidth(1200);
		barChart.getLegend().getTextProperties().setFontSize(18);
		

		// set axis properties
		barChart.getYAxis().setAutoCreateTicks(false);
		barChart.getXAxis().setAutoCreateTicks(false);
		barChart.getYAxis().setFilterFrequentTicks(true);
		barChart.getXAxis().setFilterFrequentTicks(true);
		barChart.getXAxis().setMin(0);
		barChart.getXAxis().setMax(10);
		barChart.getYAxis().setMin(0.0);
		barChart.getYAxis().setMax(16.0 * 1.2); // 16 is the hihghest value
		
//		Axis y2Axis = new Axis();
//		y2Axis.setAxisDirection(AxisDirection.Vertical_Ascending_To_Top);
//		y2Axis.setAxisPosition(AxisPosition.Maximum);
//		
//		y2Axis.setVisible(true);
//		y2Axis.setMin(0.0);
//		y2Axis.setAutoCreateTicks(true);
//		y2Axis.setAutoCreateGrids(false);
////		y2Axis.getLineProperties().setStyle(LineStyle.)t
//		barChart.addExtraAxis(y2Axis);

		// add y ticks
//		LineProperties tickLineProperties = new LineProperties(1, new com.inepex.inechart.chartwidget.properties.Color("#A3A3A3"));
//
//		Map<Double, String> yTicks = new TreeMap<Double, String>();
//		yTicks.put(10.0, "min");
//		yTicks.put(11.0, "2/3 min");
//		yTicks.put(12.0, "1/3 min");
//		yTicks.put(13.0, "BL Avg");
//		yTicks.put(14.0, "1/3 max");
//		yTicks.put(15.0, "2/3 max");
//		yTicks.put(16.0, "max");
//		for (Entry<Double, String> entry : yTicks.entrySet()) {
//			barChart.getYAxis().addTick(new Tick(entry.getKey(), tickLineProperties, null, 0, entry.getValue()));
//		}
//
//		// add y markings
//		barChart.getYAxis().fillBetween(
//				new Tick(-1.0),
//				new Tick(14.0),
//				new com.inepex.inechart.chartwidget.properties.Color("#C2EBC2"));
//		barChart.getYAxis().fillBetween(
//				new Tick(14.0),
//				new Tick(15.0),
//				new com.inepex.inechart.chartwidget.properties.Color("#FFFFAD"));
//		barChart.getYAxis().fillBetween(
//				new Tick(15.0),
//				new Tick(16.0),
//				new com.inepex.inechart.chartwidget.properties.Color("#FFD6AD"));
//		barChart.getYAxis().fillBetween(
//				new Tick(16.0),
//				new Tick(16.0 * 1.2),
//				new com.inepex.inechart.chartwidget.properties.Color("#FFA6A6"));
//		
//		// add x ticks
//		String monthlyDateFormat = "MMM yyyy";
//		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(monthlyDateFormat);
//
//		for (int i = 0; i < 12; i++) {
//			String tickLabel = dateTimeFormat.format(new Date(2010 - 1900, i, 1));
//			barChart.getXAxis().addTick(new Tick(i, tickLineProperties, null, 0, tickLabel));
//		}

	}

	private class ChartPanel extends JComponent {
		public ChartPanel() {
			setSize(700, 500);
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(chart.getImage(), 0, 0, this);
		}

	}
}
