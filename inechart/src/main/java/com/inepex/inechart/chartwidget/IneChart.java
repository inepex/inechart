
package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.TickFactoryGWT;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.intervalchart.IntervalChart;
import com.inepex.inechart.chartwidget.label.ChartTitle;
import com.inepex.inechart.chartwidget.label.GWTLabelFactory;
import com.inepex.inechart.chartwidget.label.LabelFactory;
import com.inepex.inechart.chartwidget.label.Legend;
import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.misc.SelectionRange;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inechart.chartwidget.selection.RectangularSelection;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;

public class IneChart extends Composite{
	private AbsolutePanel mainPanel;
	private DrawingAreaGWT drawingArea;
	private ArrayList<IneChartModule> modules;

	private Axes axes;
	private RectangularSelection selection = null;
	private LabelFactory labelFactory;
	private IneChartEventManager eventManager;
	
	private ModuleAssist moduleAssist;

	private boolean autoScaleModules = true;

	private int canvasWidth;
	private int canvasHeight;

	public IneChart(){
		this(Defaults.chartWidth, Defaults.chartHeight);
	}

	public IneChart(int width, int height) {
		moduleAssist = new ModuleAssist(this);
		//dimensions, layout
		mainPanel = new AbsolutePanel();
		moduleAssist.setChartMainPanel(mainPanel);
		initWidget(mainPanel);
		drawingArea = new DrawingAreaGWT(width, height);
		moduleAssist.setMainCanvas(drawingArea);
		mainPanel.add(drawingArea.getWidget(), 0, 0);
		canvasHeight = height;
		canvasWidth = width;
		mainPanel.setPixelSize(width, height);
		drawingArea.setSize(width, height);

		modules = new ArrayList<IneChartModule>();
		labelFactory = new GWTLabelFactory(moduleAssist);
		moduleAssist.setLabelFactory(labelFactory);
		axes = new Axes(moduleAssist, new TickFactoryGWT());
		moduleAssist.setAxes(axes);
		
		//event
		eventManager = new IneChartEventManager(this);
		moduleAssist.setEventManager(eventManager);
		addDomHandler(eventManager, MouseDownEvent.getType());
		addDomHandler(eventManager, MouseUpEvent.getType());
		addDomHandler(eventManager, MouseMoveEvent.getType());
		addDomHandler(eventManager, ClickEvent.getType());
		addDomHandler(eventManager, MouseOutEvent.getType());
		addDomHandler(eventManager, MouseOverEvent.getType());

	}

	public void setSize(int width, int height){
		canvasHeight = height;
		canvasWidth = width;
		mainPanel.setPixelSize(width, height);
		drawingArea.setSize(width, height);
		for(DrawingAreaGWT da:moduleAssist.getLayerCanvases()){
			da.setSize(width, height);
		}
	}

	public void update() {
		// pre-update modules
		if (autoScaleModules){
			for (IneChartModule modul : modules) {
				if(modul instanceof IneChartModule2D){
					((IneChartModule2D) modul).preUpdateModule();
				}
			}
		}
		
		labelFactory.update();
	
		//scale modules 
		if (autoScaleModules){
			axes.updateForPaddingCalculation();
			double[] modulePadding = new double[]{0,0,0,0};
			for (IneChartModule modul : modules) {
				if(modul instanceof IneChartModule2D && modul.isVisible && ((IneChartModule2D) modul).autoCalcPadding){
					modulePadding = LabelFactory.mergePaddings(modulePadding, ((IneChartModule2D) modul).getPaddingForAxes());
				}
			}
			modulePadding = LabelFactory.addPaddings(labelFactory.getPaddingNeeded(), modulePadding);
			for (IneChartModule modul : modules) {
				if(modul instanceof IneChartModule2D && modul.isVisible && ((IneChartModule2D) modul).autoCalcPadding){
					((IneChartModule2D) modul).setPadding(modulePadding);
				}
			}
			axes.updateWithOutAutoTickCreation();
		}
		else {
			axes.update();
		}

		//update modules
		for (IneChartModule modul : modules) {
			modul.update();
		}

		drawingArea.removeAllGraphicalObjects();
		for (IneChartModule modul : modules) {
			if (modul.isVisible){
				drawingArea.addAllGraphicalObject(modul.graphicalObjectContainer);
			}
		}
		drawingArea.addAllGraphicalObject(axes.graphicalObjectContainer);
		drawingArea.addAllGraphicalObject(labelFactory.graphicalObjectContainer);
		drawingArea.update();

	}

	public void updateAxes(){
		axes.update();
	}

	public LineChart createLineChart() {
		DrawingAreaGWT overlay = new DrawingAreaGWT(canvasWidth, canvasHeight);
		mainPanel.add(overlay.getWidget(),0,0);
		LineChart chart = new LineChart(moduleAssist);
		modules.add(chart);
		eventManager.addViewportChangeHandler(chart.innerEventHandler);
		return chart;
	}

	public PieChart createPieChart() {
		PieChart chart = new PieChart(moduleAssist);
		modules.add(chart);
		return chart;
	}

	public BarChart createBarChart() {
		BarChart bc = new BarChart(moduleAssist);
		modules.add(bc);
		eventManager.addViewportChangeHandler(bc.innerEventHandler);
		return bc;
	}

	public IntervalChart createIntervalChart(){
		IntervalChart ic = new IntervalChart(moduleAssist);
		modules.add(ic);
		eventManager.addViewportChangeHandler(ic.innerEventHandler);
		return ic;
	}

	public RectangularSelection getRectangularSelection(){
		if(selection == null){
			DrawingAreaGWT selectionLayer = new DrawingAreaGWT(canvasWidth, canvasHeight);
			this.selection = new RectangularSelection(selectionLayer,eventManager);
			mainPanel.add(selectionLayer.getWidget(),0,0);
			IneChartModule2D modulToSelectFrom = null;
			for(IneChartModule m : modules){
				if(m instanceof IneChartModule2D){
					modulToSelectFrom = (IneChartModule2D) m;
					break;
				}
			}
			selection.setModulToSelectFrom(modulToSelectFrom);
		}
		return selection;
	}

	protected void setCanHandleEventsForAllModule(boolean canHandleEvents){
		for (IneChartModule m : modules) {
			m.canHandleEvents = canHandleEvents;
		}
	}

	/* public methods */
	public List<LineChart> getLineCharts(){
		ArrayList<LineChart> lineCharts = new ArrayList<LineChart>();
		for(IneChartModule m : modules){
			if(m instanceof LineChart){
				lineCharts.add((LineChart) m);
			}
		}
		return lineCharts;
	}

	public List<BarChart> getBarCharts(){
		ArrayList<BarChart> barCharts = new ArrayList<BarChart>();
		for(IneChartModule m : modules){
			if(m instanceof BarChart){
				barCharts.add((BarChart) m);
			}
		}
		return barCharts;
	}

	public List<PieChart> getPieCharts(){
		ArrayList<PieChart> pieCharts = new ArrayList<PieChart>();
		for(IneChartModule m : modules){
			if(m instanceof PieChart){
				pieCharts.add((PieChart) m);
			}
		}
		return pieCharts;
	}

	public List<IneChartModule2D> getIneChartModule2Ds(){
		ArrayList<IneChartModule2D> module2Ds = new ArrayList<IneChartModule2D>();
		for(IneChartModule m : modules){
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
	
	/**
	 * Can return null
	 * @return
	 */
	public EventBus getEventBus(){
		return eventManager.getEventBus();
	}
	
	public IneChart createViewportSelectorChart(int width, int height){
		IneChart viewportSelectorChart = new IneChart(width, height);
		RectangularSelection rs = viewportSelectorChart.getRectangularSelection();

		for(IneChartModule module : modules){
			if(module instanceof LineChart){
				LineChart vpLineChart = viewportSelectorChart.createLineChart();
				for(Curve c : ((LineChart)module).getCurves()){
					vpLineChart.addCurve(c);
				}
				vpLineChart.setDisplayLegendEntries(false);
				rs.setModulToSelectFrom(vpLineChart);
				rs.getAddressedModules().add((IneChartModule2D) module);
				break;
			}
			else if(module instanceof BarChart){
//				BarChart vpBarChart = viewportSelectorChart.createBarChart();
//				for(DataSet d : ((BarChart) module).getDataSets()){
//					vpBarChart.addDataSet(d,((BarChart) module).getLookout(d));
//				}
//				vpBarChart.setDisplayLegendEntries(false);
//				rs.setModulToSelectFrom(vpBarChart);
//				rs.getAddressedModuls().add((IneChartModule2D) module);
				break;
			}
		}
		rs.setSelectionMode(SelectionRange.Horizontal);
		rs.getAddressedCharts().add(this);
		rs.setDisplayRectangleAfterSelection(true);
		if(eventManager.getEventBus() == null){
			eventManager.setEventBus(new SimpleEventBus());
		}
		viewportSelectorChart.setEventBus(eventManager.getEventBus());
		return viewportSelectorChart;
	}

	protected DrawingAreaGWT createLayer(){
		DrawingAreaGWT layer = new DrawingAreaGWT(canvasWidth, canvasHeight);
		mainPanel.add(layer.getWidget(), 0, 0);
		return layer;
	}
	
	protected void removeLayer(DrawingAreaGWT layer){
		mainPanel.remove(layer.getWidget());
	}
	
	protected void addLayer(DrawingAreaGWT layer){
		mainPanel.add(layer.getWidget(), 0, 0);
	}
	
	protected boolean containsLayer(DrawingAreaGWT layer){
		for(Widget w : mainPanel){
			if(w == layer.getWidget()){
				return true;
			}
		}
		return false;
	}
	
	protected void setLayerOrder(ArrayList<DrawingAreaGWT> layers){
		int zIndex = 0;
		for(DrawingAreaGWT layer:layers){
			DOM.setElementAttribute(layer.getWidget().getElement(), "zIndex", zIndex++ +"");
		}
		
	}
	
	public boolean containsModule(IneChartModule module){
		return modules.contains(module);
	}
	
	public IneChartModule2D getVisibleTopModule(){
		ArrayList<Layer> layers = moduleAssist.getLayers();
		for(int i = layers.size() - 1; i >= 0; i--){
			Layer lyr = layers.get(i);
			IneChartModule2D m = lyr.getRelatedModule();
			if(m!= null && m.isVisible()){
				return m;
			}
		}
		return null;
	}
}
