package com.inepex.inechart.awtchart;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;

import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.Viewport;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.linechart.LineChartProperties;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inegraphics.awt.DrawingAreaAwt;

public class IneAwtChart {

	private ArrayList<IneChartModul> moduls;
	private DrawingAreaAwt drawingArea;

	// properties
	private static final int DEFAULT_PADDING = 30;
	private int widgetWidth;
	private int widgetHeight;
	private int canvasWidth;
	private int canvasHeight;
	private final Viewport viewport;

	public IneAwtChart(int width, int height) {
		canvasHeight = height - DEFAULT_PADDING / 2;
		canvasWidth = width - DEFAULT_PADDING / 2;
		widgetHeight = height;
		widgetWidth = width;
		viewport = new Viewport(0, 0, 1, 1);
		this.drawingArea = new DrawingAreaAwt(canvasWidth, canvasHeight);
		moduls = new ArrayList<IneChartModul>();
	}

	/*
	 * Moduls
	 */

	public LineChart createLineChart() {
		LineChart chart = new LineChart(drawingArea, getAxes(), viewport);
		moduls.add(chart);
		chart.setProperties(LineChartProperties.getDefaultLineChartProperties());
		return chart;
	}

	public LineChart createLineChart(LineChartProperties properties) {
		LineChart chart = createLineChart();
		chart.setProperties(properties);
		return chart;
	}

	public PieChart createPieChart() {
		PieChart chart = new PieChart(drawingArea);
		moduls.add(chart);
		return chart;
	}

	public Axes getAxes() {
		for (IneChartModul m : moduls)
			if (m instanceof Axes)
				return (Axes) m;
		Axes n = new Axes(drawingArea, viewport);
		moduls.add(n);
		return n;
	}

	public void addModul(IneChartModul modul) {
		if (modul != null)
			moduls.add(modul);
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void update() {
		// update model, create GOs per modul
		for (IneChartModul modul : moduls) {
			modul.update();
			drawingArea.addAllGraphicalObject(modul
					.getGraphicalObjectContainer());
		}
		// draw
		drawingArea.update();
	}

	public void saveToFile(String filename) {
		((DrawingAreaAwt) drawingArea).saveToFile(filename);
	}

	public void saveToOutputStream(OutputStream outputStream) {
		((DrawingAreaAwt) drawingArea).saveToOutputStream(outputStream);
	}

	public BufferedImage getImage() {
		return ((DrawingAreaAwt) drawingArea).getImage();
	}
}
