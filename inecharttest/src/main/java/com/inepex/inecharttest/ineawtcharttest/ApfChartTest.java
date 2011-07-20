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
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Circle;

public class ApfChartTest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4542081093511966555L;

	private IneAwtChart chart;

	public ApfChartTest() {
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
		new ApfChartTest();

	}

	String[] curveNames = { "Airspace Infringement", "Near Controlled Flight Into Terrain", "Level Bust",
			"Prolonged Loss of Communications", };

	Double[][] values = { { 12.0, 11.0, 8.0, 16.0, 10.0, 9.0, 16.0, 10.0, 14.0, 11.0, 8.0, 9.0 },
			{ 3.0, 6.0, 1.0, 2.0, 4.0, 1.0, 5.0, 1.0, 3.0, 3.0, 1.0, 2.0 },
			{ 0.0, 3.0, 1.0, 5.0, 4.0, 4.0, 3.0, 2.0, 7.0, 3.0, 2.0, 4.0 },
			{ 4.0, 1.0, 3.0, 6.0, 1.0, 2.0, 5.0, 1.0, 3.0, 3.0, 1.0, 2.0 },
			{ 5.0, 1.0, 3.0, 3.0, 1.0, 2.0, 3.0, 6.0, 1.0, 2.0, 4.0, 1.0 }

	};

	private Curve getCurve(int i) {
		TreeMap<Double, Double> data = new TreeMap<Double, Double>();
		for (int j = 0; j < values[0].length; j++) {
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
		LineChart lineChart = chart.createLineChart();

		// add curves
		for (int i = 0; i < curveNames.length; i++) {
			lineChart.addCurve(getCurve(i));
		}

		// set linechart properties
		lineChart.setAutoScaleViewport(false);
		lineChart.setUseViewport(false);
		lineChart.setAutoCalcPadding(true);
		lineChart.setColors(ColorSet.flotColorSet());

		// set axis properties
		lineChart.getYAxis().setAutoCreateTicks(false);
		lineChart.getXAxis().setAutoCreateTicks(false);
		lineChart.getYAxis().setFilterFrequentTicks(true);
		lineChart.getXAxis().setFilterFrequentTicks(true);
		lineChart.getXAxis().setMin(0);
		lineChart.getXAxis().setMax(11);
		lineChart.getYAxis().setMin(0.0);
		lineChart.getYAxis().setMax(16.0 * 1.2); // 16 is the hihghest value

		// add y ticks
		LineProperties tickLineProperties = new LineProperties(1, new com.inepex.inechart.chartwidget.properties.Color("#A3A3A3"));

		Map<Double, String> yTicks = new TreeMap<Double, String>();
		yTicks.put(10.0, "min");
		yTicks.put(11.0, "2/3 min");
		yTicks.put(12.0, "1/3 min");
		yTicks.put(13.0, "BL Avg");
		yTicks.put(14.0, "1/3 max");
		yTicks.put(15.0, "2/3 max");
		yTicks.put(16.0, "max");
		for (Entry<Double, String> entry : yTicks.entrySet()) {
			lineChart.getYAxis().addTick(new Tick(entry.getKey(), tickLineProperties, null, 0, entry.getValue()));
		}

		// add y markings
		lineChart.getYAxis().fillBetween(
				new Tick(0.0),
				new Tick(14.0),
				new com.inepex.inechart.chartwidget.properties.Color("#C2EBC2"));
		lineChart.getYAxis().fillBetween(
				new Tick(14.0),
				new Tick(15.0),
				new com.inepex.inechart.chartwidget.properties.Color("#FFFFAD"));
		lineChart.getYAxis().fillBetween(
				new Tick(15.0),
				new Tick(16.0),
				new com.inepex.inechart.chartwidget.properties.Color("#FFD6AD"));
		lineChart.getYAxis().fillBetween(
				new Tick(16.0),
				new Tick(16.0 * 1.2),
				new com.inepex.inechart.chartwidget.properties.Color("#FFA6A6"));

		// add x ticks
		String monthlyDateFormat = "MMM yyyy";
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(monthlyDateFormat);

		for (int i = 0; i < 12; i++) {
			String tickLabel = dateTimeFormat.format(new Date(2010 - 1900, i, 1));
			lineChart.getXAxis().addTick(new Tick(i, tickLineProperties, null, 0, tickLabel));
		}

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
