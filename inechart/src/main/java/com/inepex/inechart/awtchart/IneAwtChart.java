package com.inepex.inechart.awtchart;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.HasCoordinateSystem;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.Viewport;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.label.HasTitle;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inegraphics.awt.DrawingAreaAwt;

public class IneAwtChart implements HasTitle{
	private ArrayList<IneChartModul> moduls;
	Axes axes;
	private ArrayList<Viewport> modulViewports;
	private DrawingAreaAwt drawingArea;
	private IneChartModul focus;
	private StyledLabel title, description;
	private boolean autoScaleModuls = true;
	private boolean includeTitleInPadding = true;
	private LabelFactoryBase legendFactory;
	

	// properties
	private int canvasWidth;

	private int canvasHeight;

	public IneAwtChart(int width, int height) {
		canvasHeight = height;
		canvasWidth = width;
		this.drawingArea = new DrawingAreaAwt(canvasWidth, canvasHeight);
		legendFactory = new LabelFactoryBase(drawingArea);
		legendFactory.setChartTitle(this);
		moduls = new ArrayList<IneChartModul>();
		axes = new Axes(drawingArea);
		modulViewports = new ArrayList<Viewport>();
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
		PieChart chart = new PieChart(drawingArea, getAxes());
		moduls.add(chart);
		legendFactory.addHasLegendEntries(chart);
		return chart;
	}

	public BarChart createBarChart() {
		BarChart bc = new BarChart(drawingArea, getAxes());
		legendFactory.addHasLegendEntries(bc);
		moduls.add(bc);
		return bc;
	}

	Axes getAxes() {
		return axes;
	}

	/* public methods */

	private void focusModul(IneChartModul modul) {
		for (IneChartModul m : moduls) {
			if (m != modul)
				m.setCanHandleEvents(false);
			else
				m.setCanHandleEvents(true);
		}
		focus = modul;
	}

	private void releaseFocusIfPossible() {
		if (focus != null) {
			for (IneChartModul m : moduls) {
				if (focus == m && m.isRequestFocus() == false) {
					for (IneChartModul m1 : moduls) {
						m1.setCanHandleEvents(true);
					}
					focus = null;
					return;
				}
			}
		}
	}

	public void update() {
		
		releaseFocusIfPossible();
		// grant focus if possible and requested
		if (focus == null) {
			for (IneChartModul modul : moduls) {
				if (modul.isVisible() && modul.isRequestFocus()) {
					focusModul(modul);
					break;
				}
			}
		}

		legendFactory.update();
		
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
			double[] padding = legendFactory.getPadding(includeTitleInPadding);
			for (IneChartModul modul : moduls) {
				if(modul instanceof IneChartModul2D && ((IneChartModul2D) modul).isAutoCalcPadding()){
					padding = IneChartModul2D.mergePaddings(padding,((IneChartModul2D) modul).getPaddingForAxes());
				}
			}
			for (IneChartModul modul : moduls) {
				if(modul instanceof IneChartModul2D && ((IneChartModul2D) modul).isAutoCalcPadding()){
					((IneChartModul2D) modul).setPadding(padding);
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
				if (modul.isVisible()){
					drawingArea.addAllGraphicalObject(modul.getGraphicalObjectContainer());
				}
			}
			drawingArea.addAllGraphicalObject(axes.getGraphicalObjectContainer());
			drawingArea.addAllGraphicalObject(legendFactory.getGraphicalObjectContainer());
			drawingArea.update();
		}
		//reset viewports 
		for (Viewport vp : modulViewports) {
			boolean canResetVP = true;
			for (IneChartModul m : vp.getUserModuls().keySet()) {
				if (vp.getUserModuls().get(m)) {
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

	public void saveToFile(String filename) {
		((DrawingAreaAwt) drawingArea).saveToFile(filename);
	}

	public void saveToOutputStream(OutputStream outputStream) {
		((DrawingAreaAwt) drawingArea).saveToOutputStream(outputStream);
	}

	public BufferedImage getImage() {
		return ((DrawingAreaAwt) drawingArea).getImage();
	}

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	@Override
	public void setName(StyledLabel name) {
		this.title = name;
	}

	@Override
	public StyledLabel getName() {
		return title;
	}

	@Override
	public void setDescription(StyledLabel description) {
		this.description = description;
	}

	@Override
	public StyledLabel getDescription() {
		return description;
	}
	
	@Override
	public void setName(String name) {
		setName(new StyledLabel(name, Defaults.chartTitle_Name, 1, Defaults.chartTitleBackground));
		
	}

	@Override
	public void setDescription(String description) {
		setDescription(new StyledLabel(description, Defaults.chartTitle_Description, 1, Defaults.chartTitleBackground));
	}

}
