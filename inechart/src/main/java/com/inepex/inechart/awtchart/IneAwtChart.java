package com.inepex.inechart.awtchart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.label.ChartTitle;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.linechart.LineChart;
import com.inepex.inechart.chartwidget.piechart.PieChart;
import com.inepex.inegraphics.awt.DrawingAreaAwt;

public class IneAwtChart  extends JLayeredPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734001048666513584L;
	
	private class ChartPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6503310025811306567L;
		public ChartPanel(Dimension dimension) {
			setSize(dimension);
			setPreferredSize(dimension);
			setMaximumSize(dimension);
			setMinimumSize(dimension);
		}
		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(IneAwtChart.this.getImage(), 0, 0, this);
		}
	}
	
	private ArrayList<IneChartModule> moduls;
	private Axes axes;
	private DrawingAreaAwt drawingArea;
	private IneChartModule focus;
	private boolean autoScaleModuls = true;
	private AwtLabelFactory labelFactory;
	
	private Dimension dimension;
	private ChartPanel chartPanel;
	private JPanel labelPanel;
	private boolean updateRequested = false;
	
	public IneAwtChart(int width, int height){
		super();
		dimension = new Dimension(width, height);
		this.drawingArea = new DrawingAreaAwt(width, height);
		
		moduls = new ArrayList<IneChartModule>();
		axes = new Axes(drawingArea,labelFactory);
		axes.setTickFactory(new AwtTickFactory());
		
		initLayout();
		
		labelFactory = new AwtLabelFactory(drawingArea,labelPanel);
	}
	
	private void initLayout(){
		setSize(dimension);
		setPreferredSize(dimension);
		setMaximumSize(dimension);
		setMinimumSize(dimension);
		chartPanel = new ChartPanel(dimension);
		labelPanel = new JPanel();
		setLayout(null);
		chartPanel.setBounds(0, 0, dimension.width, dimension.height);
		labelPanel.setBounds(0, 0, dimension.width, dimension.height);
		labelPanel.setOpaque(false);
		add(chartPanel, 1);
		add(labelPanel, 0);
	
	}

	public LineChart createLineChart() {
		LineChart chart = new LineChart(drawingArea, getLabelFactor(), getAxes());
		moduls.add(chart);
		return chart;
	}

	public PieChart createPieChart() {
		PieChart chart = new PieChart(drawingArea, getLabelFactor(), getAxes());
		moduls.add(chart);
		return chart;
	}

	public BarChart createBarChart() {
		BarChart bc = new BarChart(drawingArea,  getLabelFactor(), getAxes());
		moduls.add(bc);
		return bc;
	}

	Axes getAxes() {
		return axes;
	}
	
	LabelFactoryBase getLabelFactor() {
		return labelFactory;
	}

	private void focusModul(IneChartModule modul) {
		for (IneChartModule m : moduls) {
			if (m != modul)
				m.setCanHandleEvents(false);
			else
				m.setCanHandleEvents(true);
		}
		focus = modul;
	}

	private void releaseFocusIfPossible() {
		if (focus != null) {
			for (IneChartModule m : moduls) {
				if (focus == m && m.isRequestFocus() == false) {
					for (IneChartModule m1 : moduls) {
						m1.setCanHandleEvents(true);
					}
					focus = null;
					return;
				}
			}
		}
	}

	public void update() {
		labelFactory.update();
		updateRequested = true;
		repaint();		
	}
	
	private void updateModuls(){
		releaseFocusIfPossible();
		// grant focus if possible and requested
		if (focus == null) {
			for (IneChartModule modul : moduls) {
				if (modul.isVisible() && modul.isRequestFocus()) {
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
		axes.update();
		
		validate();
		
		//scale moduls 
		if (autoScaleModuls){
			double[] padding = new double[4];
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D && ((IneChartModule2D) modul).isAutoCalcPadding()){
					padding = LabelFactoryBase.mergePaddings(padding,((IneChartModule2D) modul).getPaddingForAxes());
				}
			}
			padding = LabelFactoryBase.addPaddings(padding, labelFactory.getPaddingNeeded());
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D && ((IneChartModule2D) modul).isAutoCalcPadding()){
					((IneChartModule2D) modul).setPadding(padding);
				}
			}
			axes.updateWithOutAutoTickCreation();
			
			validate();
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

		drawingArea.removeAllGraphicalObjects();
		for (IneChartModule modul : moduls) {
			if (modul.isVisible()){
				drawingArea.addAllGraphicalObject(modul.getGraphicalObjectContainer());
			}
		}
		drawingArea.addAllGraphicalObject(axes.getGraphicalObjectContainer());
		drawingArea.addAllGraphicalObject(labelFactory.getGraphicalObjectContainer());
		
		drawingArea.update();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(updateRequested){
			updateModuls();
//			chartPanel.repaint();
//			labelPanel.repaint();
			updateRequested = false;
		}
		
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
}
