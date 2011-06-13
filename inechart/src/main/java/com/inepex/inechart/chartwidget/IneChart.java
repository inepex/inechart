package com.inepex.inechart.chartwidget;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inechart.chartwidget.selection.Selection;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public class IneChart extends Composite {
	private AbsolutePanel mainPanel;
	private ArrayList<IneChartModul> moduls;
	private Axes axes;
	private Selection selection = null;
	private ArrayList<Viewport> modulViewports;
	private DrawingAreaGWT drawingArea;
	private RepeatingCommand updateCommand = null;
	private int updateInterval = DEFAULT_UPDATE_INTERVAL;
	public static final int DEFAULT_UPDATE_INTERVAL = 800;
	private IneChartModul focus;

	// properties
	private int canvasWidth;
	private int canvasHeight;

	public IneChart(int width, int height) {
		canvasHeight = height;
		canvasWidth = width;
		mainPanel = new AbsolutePanel();
		mainPanel.setPixelSize(width, height);
		this.drawingArea = new DrawingAreaGWT(canvasWidth, canvasHeight, false);
		if (DrawingAreaGWT.isHTML5Compatible())
			drawingArea.setCreateShadows(false);
		moduls = new ArrayList<IneChartModul>();
		axes = new Axes(drawingArea);
		modulViewports = new ArrayList<Viewport>();
		mainPanel.add(drawingArea.getWidget(), 0, 0);
		this.initWidget(mainPanel);
	}

	/*
	 * Moduls
	 */

	public LineChart createLineChart() {
		LineChart chart = new LineChart(drawingArea, getAxes());
		moduls.add(chart);
		return chart;
	}

	public LineChart createLineChart(Viewport viewport) {
		LineChart chart = new LineChart(drawingArea, getAxes(), viewport);
		moduls.add(chart);
		return chart;
	}

	public PieChart createPieChart() {
		PieChart chart = new PieChart(drawingArea);
		moduls.add(chart);
		return chart;
	}

	public BarChart createBarChart() {
		BarChart bc = new BarChart(drawingArea, getAxes());
		moduls.add(bc);
		return bc;
	}

	Axes getAxes() {
		return axes;
	}
	
	public Selection getSelection(){
		if(selection == null){
			DrawingAreaGWT selectionLayer = new DrawingAreaGWT(canvasWidth, canvasHeight, false);
			this.selection = new Selection(selectionLayer);
			mainPanel.add(selectionLayer.getWidget(), 0, 0);
			IneChartModul2D modulToSelectFrom = null;
			for(IneChartModul m : moduls){
				if(m instanceof IneChartModul2D){
					modulToSelectFrom = (IneChartModul2D) m;
					break;
				}
			}
			selection.setModulToSelectFrom(modulToSelectFrom);
		}
		return selection;
	}

	/* public methods */

	private void focusModul(IneChartModul modul) {
		for (IneChartModul m : moduls) {
			if (m != modul)
				m.canHandleEvents = false;
			else
				m.canHandleEvents = true;
		}
		focus = modul;
	}

	private void releaseFocusIfPossible() {
		if (focus != null) {
			for (IneChartModul m : moduls) {
				if (focus == m && m.requestFocus == false) {
					for (IneChartModul m1 : moduls) {
						m1.canHandleEvents = true;
					}
					focus = null;
					return;
				}
			}
		}
	}

	public void update() {
		if(selection != null && selection.requestFocus){
			return;
		}
		releaseFocusIfPossible();
		// grant focus
		if (focus == null) {
			for (IneChartModul modul : moduls) {
				if (modul.isVisible && modul.requestFocus) {
					focusModul(modul);
					break;
				}
			}
		}
		boolean doRedraw = false;
		// if present update only focused
		if (focus != null) {
			if (focus.redrawNeeded()) {
				focus.update();
				doRedraw = true;
			}
		} else {
			for (IneChartModul modul : moduls) {
				if (modul.redrawNeeded()) {
					modul.update();
					if (modul instanceof HasCoordinateSystem
							&& !modulViewports
									.contains(((HasCoordinateSystem) modul)
											.getViewport()))
						modulViewports.add(((HasCoordinateSystem) modul)
								.getViewport());
					doRedraw = true;
				}
			}
		}
		// axes modul should always be updated
		if (axes.redrawNeeded()) {
			((IneChartModul)axes).update();
			doRedraw = true;
		}
		if (doRedraw) {
			drawingArea.removeAllGraphicalObject();
			for (IneChartModul modul : moduls) {
				if (modul.isVisible)
					drawingArea
							.addAllGraphicalObject(modul.graphicalObjectContainer);
			}
			drawingArea.addAllGraphicalObject(axes.graphicalObjectContainer);
			drawingArea.update();
		}
		for (Viewport vp : modulViewports) {
			boolean canResetVP = true;
			for (IneChartModul m : vp.userModuls.keySet()) {
				if (vp.userModuls.get(m)) {
					canResetVP = false;
					break;
				}
			}
			if (canResetVP)
				vp.resetChanged();
		}
	}

	public boolean redrawNeeded() {
		for (IneChartModul modul : moduls) {
			if (modul.redrawNeeded())
				return true;
		}
		return axes.redrawNeeded();
	}

	public DrawingAreaGWT getDrawingArea() {
		return this.drawingArea;
	}

	public void setUpdateInterval(int ms) {

		updateCommand = new RepeatingCommand() {

			@Override
			public boolean execute() {
				update();
				return true;
			}
		};
		Scheduler.get().scheduleFixedDelay(updateCommand, ms);

	}

}
