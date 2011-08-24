package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.TickFactoryGWT;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.intervalchart.IntervalChart;
import com.inepex.inechart.chartwidget.label.ChartTitle;
import com.inepex.inechart.chartwidget.label.GWTLabelFactory;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.label.Legend;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inechart.chartwidget.selection.RectangularSelection;
import com.inepex.inechart.chartwidget.selection.RectangularSelection.RectangularSelectionMode;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public class IneChart extends Composite{
	private static final int DEFAULT_WIDTH = 470;
	private static final int DEFAULT_HEIGHT = 380;
	private AbsolutePanel mainPanel;
	private DrawingAreaGWT drawingArea;
	private ArrayList<IneChartModule> moduls;
	
	private Axes axes;
	private RectangularSelection selection = null;
	private LabelFactoryBase labelFactory;
	private IneChartEventManager eventManager;
	
	private IneChartModule focus;
	
	private boolean autoScaleModuls = true;
	
	private boolean isUpdatingInProgress = false;

	/**
	 * @return the isUpdatingInProgress
	 */
	public boolean isUpdatingInProgress() {
		return isUpdatingInProgress;
	}



	// properties
	private int canvasWidth;
	private int canvasHeight;
	
	public IneChart(){
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public IneChart(int width, int height) {
		//dimensions, layout
		canvasHeight = height;
		canvasWidth = width;
		mainPanel = new AbsolutePanel();
		mainPanel.setPixelSize(width, height);
		initWidget(mainPanel);
		drawingArea = new DrawingAreaGWT(canvasWidth, canvasHeight, false);
		mainPanel.add(drawingArea.getWidget(),0,0);
		
		moduls = new ArrayList<IneChartModule>();
		labelFactory = new GWTLabelFactory(drawingArea,mainPanel);
		axes = new Axes(drawingArea,labelFactory);
		axes.setTickFactory(new TickFactoryGWT());
		
		//event
		eventManager = new IneChartEventManager(this);
		addDomHandler(eventManager, MouseDownEvent.getType());
		addDomHandler(eventManager, MouseUpEvent.getType());
		addDomHandler(eventManager, MouseMoveEvent.getType());
		addDomHandler(eventManager, ClickEvent.getType());
	}
	
	public void setSize(int width, int height){
		//TODO
	}

	public boolean isRedrawNeeded(){
		for(IneChartModule m : moduls){
			if(m.isRedrawNeeded()){
				return true;
			}
		}
		return false;
	}
	
	public void update() {
		isUpdatingInProgress = true;
//		long start = System.currentTimeMillis();
		releaseFocusIfPossible();
		// grant focus if possible and requested
		if (focus == null) {
			for (IneChartModule modul : moduls) {
				if (modul.isVisible && modul.requestFocus) {
					focusModul(modul);
					break;
				}
			}
		}

		if (autoScaleModuls){
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D){
					((IneChartModule2D) modul).updateModulesAxes();
				}
			}
		}
//		long start2 = System.currentTimeMillis();
		labelFactory.update();
//		Log.debug(System.currentTimeMillis() - start2 + " ms - labelFactory update");
//		start2 = System.currentTimeMillis();
		
		
//		Log.info(System.currentTimeMillis() - start2 + " ms - axes update");
//		start2 = System.currentTimeMillis();
		//scale moduls 
		if (autoScaleModuls){
			axes.updateForPaddingCalculation();
			double[] padding = new double[]{0,0,0,0};
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D && ((IneChartModule2D) modul).autoCalcPadding){
					padding = LabelFactoryBase.mergePaddings(padding, ((IneChartModule2D) modul).getPaddingForAxes());
				}
			}
			padding = LabelFactoryBase.addPaddings(labelFactory.getPaddingNeeded(), padding);
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D && ((IneChartModule2D) modul).autoCalcPadding){
					((IneChartModule2D) modul).setPadding(padding);
				}
			}
			axes.updateWithOutAutoTickCreation();
//			Log.info(System.currentTimeMillis() - start2 + " ms - axes second update");
//			start2 = System.currentTimeMillis();
		}
		else{
			axes.update();
		}
		//FIXME think about removing focus...
		// update moduls if present, update only focused
		if (focus != null) {
			focus.update();
		} 
		else {
			for (IneChartModule modul : moduls) {
				modul.update();
			}
		}

		drawingArea.removeAllGraphicalObject();
		for (IneChartModule modul : moduls) {
			if (modul.isVisible){
				drawingArea.addAllGraphicalObject(modul.graphicalObjectContainer);
			}
		}
		drawingArea.addAllGraphicalObject(axes.graphicalObjectContainer);
		drawingArea.addAllGraphicalObject(labelFactory.graphicalObjectContainer);
//		Log.info(System.currentTimeMillis() - start + " ms - modules update");
//		start = System.currentTimeMillis();
		drawingArea.update();
//		Log.info(System.currentTimeMillis() - start + " ms - drawingArea update");
//		Log.info("===============================================================");
		isUpdatingInProgress = false;
	}

	/*
	 * Moduls
	 */

	public LineChart createLineChart() {
		DrawingAreaGWT overlay = new DrawingAreaGWT(canvasWidth, canvasHeight, false);
		mainPanel.add(overlay.getWidget(),0,0);
		LineChart chart = new LineChart(drawingArea, labelFactory, getAxes(), overlay, eventManager);
		moduls.add(chart);
		eventManager.addViewportChangeHandler(chart.innerEventHandler);
		return chart;
	}

	public PieChart createPieChart() {
		PieChart chart = new PieChart(drawingArea, labelFactory, getAxes());
		moduls.add(chart);
		return chart;
	}

	public BarChart createBarChart() {
		BarChart bc = new BarChart(drawingArea, labelFactory, getAxes());
		moduls.add(bc);
		eventManager.addViewportChangeHandler(bc.innerEventHandler);
		return bc;
	}
	
	public IntervalChart createIntervalChart(){
		IntervalChart ic = new IntervalChart(drawingArea, labelFactory, axes, eventManager);
		moduls.add(ic);
		eventManager.addViewportChangeHandler(ic.innerEventHandler);
		return ic;
	}

	Axes getAxes() {
		return axes;
	}
	
	LabelFactoryBase getLabelFactory(){
		return labelFactory;
	}
	
	public RectangularSelection getRectangularSelection(){
		if(selection == null){
			DrawingAreaGWT selectionLayer = new DrawingAreaGWT(canvasWidth, canvasHeight, false);
			this.selection = new RectangularSelection(selectionLayer,eventManager);
			mainPanel.add(selectionLayer.getWidget(),0,0);
			IneChartModule2D modulToSelectFrom = null;
			for(IneChartModule m : moduls){
				if(m instanceof IneChartModule2D){
					modulToSelectFrom = (IneChartModule2D) m;
					break;
				}
			}
			selection.setModulToSelectFrom(modulToSelectFrom);
		}
		return selection;
	}

	private void focusModul(IneChartModule modul) {
		for (IneChartModule m : moduls) {
			if (m != modul)
				m.canHandleEvents = false;
			else
				m.canHandleEvents = true;
		}
		focus = modul;
	}

	private void releaseFocusIfPossible() {
		if (focus != null) {
			for (IneChartModule m : moduls) {
				if (focus == m && m.requestFocus == false) {
					for (IneChartModule m1 : moduls) {
						m1.canHandleEvents = true;
					}
					focus = null;
					return;
				}
			}
		}
	}
	
	/* public methods */
	public List<LineChart> getLineCharts(){
		ArrayList<LineChart> lineCharts = new ArrayList<LineChart>();
		for(IneChartModule m : moduls){
			if(m instanceof LineChart){
				lineCharts.add((LineChart) m);
			}
		}
		return lineCharts;
	}
	
	public List<BarChart> getBarCharts(){
		ArrayList<BarChart> barCharts = new ArrayList<BarChart>();
		for(IneChartModule m : moduls){
			if(m instanceof BarChart){
				barCharts.add((BarChart) m);
			}
		}
		return barCharts;
	}
	
	public List<PieChart> getPieCharts(){
		ArrayList<PieChart> pieCharts = new ArrayList<PieChart>();
		for(IneChartModule m : moduls){
			if(m instanceof PieChart){
				pieCharts.add((PieChart) m);
			}
		}
		return pieCharts;
	}
 	
	public List<IneChartModule2D> getIneChartModul2Ds(){
		ArrayList<IneChartModule2D> module2Ds = new ArrayList<IneChartModule2D>();
		for(IneChartModule m : moduls){
			if(m instanceof IneChartModule2D){
				module2Ds.add((IneChartModule2D) m);
			}
		}
		return module2Ds;
	}
	
	public DrawingAreaGWT getDrawingArea() {
		return this.drawingArea;
	}

	public void setChartTitle(String title){
		setChartTitle(new ChartTitle(title));
	}
	
	public void setChartTitle(String title,String description){
		setChartTitle(new ChartTitle(title,description));
	}
	
	public void setChartTitle(ChartTitle title){
		labelFactory.setChartTitle(title);
	}
	
	public ChartTitle getChartTitle(){
		return labelFactory.getChartTitle();
	}
	
	public void setLegend(Legend legend){
		labelFactory.setLegend(legend);
	}
	
	public Legend getLegend(){
		return labelFactory.getLegend();
	}
	
	public void setEventBus(EventBus eventBus){
		eventManager.setEventBus(eventBus);
	}

	public IneChart createViewportSelectorChart(int width, int height){
		IneChart viewportSelectorChart = new IneChart(width, height);
		RectangularSelection rs = viewportSelectorChart.getRectangularSelection();
		
		for(IneChartModule module : moduls){
			if(module instanceof LineChart){
				LineChart vpLineChart = viewportSelectorChart.createLineChart();
				for(Curve c : ((LineChart)module).getCurves()){
					vpLineChart.addCurve(c);
				}
				vpLineChart.setDisplayEntries(false);
				rs.setModulToSelectFrom(vpLineChart);
				rs.getAddressedModuls().add((IneChartModule2D) module);
				break;
			}
			else if(module instanceof BarChart){
				BarChart vpBarChart = viewportSelectorChart.createBarChart();
				for(DataSet d : ((BarChart) module).getDataSets()){
					vpBarChart.addDataSet(d,((BarChart) module).getLookout(d));
				}
				vpBarChart.setDisplayEntries(false);
				rs.setModulToSelectFrom(vpBarChart);
				rs.getAddressedModuls().add((IneChartModule2D) module);
				break;
			}
		}
		rs.setSelectionMode(RectangularSelectionMode.Horizontal);
		rs.getAddressedCharts().add(this);
		rs.setDisplayRectangleAfterSelection(true);
		if(eventManager.getEventBus() == null){
			eventManager.setEventBus(new SimpleEventBus());
		}
		viewportSelectorChart.setEventBus(eventManager.getEventBus());
		
		return viewportSelectorChart;
	}
	
	/**
	 * Can return null
	 * @return
	 */
	public EventBus getEventBus(){
		return eventManager.getEventBus();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
