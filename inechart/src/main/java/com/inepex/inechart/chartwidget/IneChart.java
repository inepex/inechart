package com.inepex.inechart.chartwidget;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.legend.GWTLabelFactory;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.misc.HasTitle;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inechart.chartwidget.selection.Selection;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class IneChart extends Composite implements HasTitle{
	private static final int DEFAULT_WIDTH = 470;
	private static final int DEFAULT_HEIGHT = 380;
	private AbsolutePanel mainPanel;
	private ArrayList<IneChartModul> moduls;
	/**
	 * singleton per {@link IneChart} instance
	 */
	private Axes axes;
	private Selection selection = null;
	private ArrayList<Viewport> modulViewports;
	private DrawingAreaGWT drawingArea;
	private IneChartModul focus;
	private GWTLabelFactory legendFactory;
	private String title, description;
	private boolean autoScaleModuls = true;
	private boolean includeLegendInPadding = true;
	private boolean includeTitleInPadding = true;

	// properties
	private int canvasWidth;
	private int canvasHeight;
	
	public IneChart(){
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

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
		legendFactory = new GWTLabelFactory(mainPanel, this);
		drawingArea.getCanvasWidget().setStyleName(ResourceHelper.getRes().style().chartWidget());
	}
	
	public void setSize(int width, int height){
		//TODO
	}

	/*
	 * Moduls
	 */

	public LineChart createLineChart() {
		LineChart chart = new LineChart(drawingArea, getAxes());
		moduls.add(chart);
		legendFactory.addHasLegendEntries(chart);
		return chart;
	}

	public LineChart createLineChart(Viewport viewport) {
		LineChart chart = new LineChart(drawingArea, getAxes(), viewport);
		moduls.add(chart);
		legendFactory.addHasLegendEntries(chart);
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
		// grant focus if possible and requested
		if (focus == null) {
			for (IneChartModul modul : moduls) {
				if (modul.isVisible && modul.requestFocus) {
					focusModul(modul);
					break;
				}
			}
		}

		if (autoScaleModuls){
			for (IneChartModul modul : moduls) {
				if(modul instanceof IneChartModul2D){
					((IneChartModul2D) modul).updateModulsAxes();
				}
			}
		}
		
		boolean doRedraw = false;
		// axes should be updated even if a modul has been focused
		if (axes.redrawNeeded()) {
			axes.update();
			doRedraw = true;
		}
		
		//scale moduls 
		if (autoScaleModuls){
			for (IneChartModul modul : moduls) {
				if(modul instanceof IneChartModul2D){
					((IneChartModul2D) modul).calculatePadding(legendFactory.getPadding(includeTitleInPadding, includeLegendInPadding));
				}
			}
			axes.forcedUpdate();
		}
		
		// update moduls if present, update only focused
		if (focus != null) {
			if (focus.redrawNeeded()) {
				focus.update();
				doRedraw = true;
			}
		} 
		else {
			for (IneChartModul modul : moduls) {
				if (modul.redrawNeeded()) {
					modul.update();
					if (modul instanceof HasCoordinateSystem && !modulViewports.contains(((HasCoordinateSystem) modul).getViewport()))
						modulViewports.add(((HasCoordinateSystem) modul).getViewport());
					doRedraw = true;
				}
			}
		}
		// draw graphics
		if (doRedraw) {
			drawingArea.removeAllGraphicalObject();
			for (IneChartModul modul : moduls) {
				if (modul.isVisible){
					drawingArea.addAllGraphicalObject(modul.graphicalObjectContainer);
				}
			}
			drawingArea.addAllGraphicalObject(axes.graphicalObjectContainer);
			drawingArea.update();
		}
		//reset viewports 
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
		
		legendFactory.updateChartTitle();
		legendFactory.updateLegends();
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

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;	
	}

	@Override
	public String getTitle() {
		return title;
	}

}
