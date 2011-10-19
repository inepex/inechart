package com.inepex.inechart.awtchart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.inepex.inechart.chartwidget.IneChartModule;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.barchart.BarChart;
import com.inepex.inechart.chartwidget.label.ChartTitle;
import com.inepex.inechart.chartwidget.label.LabelFactory;
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

	private boolean autoScaleModuls = true;

	private ModuleAssist moduleAssist;

	private Dimension dimension;
	private ChartPanel chartPanel;
	private JPanel labelPanel;
	private boolean updateRequested = false;

	public IneAwtChart(int width, int height){
		super();
		dimension = new Dimension(width, height);
		moduleAssist = new ModuleAssist();
		moduleAssist.setMainCanvas(new DrawingAreaAwt(width, height));


		initLayout();

		moduleAssist.setLabelFactory(new AwtLabelFactory(moduleAssist.getMainCanvas(), labelPanel));
		moduls = new ArrayList<IneChartModule>();
		moduleAssist.setAxes(new Axes(moduleAssist.getMainCanvas(), moduleAssist.getLabelFactory()));
		moduleAssist.getAxes().setTickFactory(new AwtTickFactory());

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
		LineChart chart = new LineChart(moduleAssist);
		moduls.add(chart);
		return chart;
	}

	public PieChart createPieChart() {
		PieChart chart = new PieChart(moduleAssist);
		moduls.add(chart);
		return chart;
	}

	public BarChart createBarChart() {
		BarChart bc = new BarChart(moduleAssist);
		moduls.add(bc);
		return bc;
	}

	public void update() {
		moduleAssist.getLabelFactory().update();
		updateRequested = true;
		repaint();		
	}

	private void updateModuls(){

		if (autoScaleModuls){
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D){
					((IneChartModule2D) modul).preUpdateModule();
				}
			}
		}
		moduleAssist.getAxes().update();

		validate();

		//scale moduls 
		if (autoScaleModuls){
			double[] padding = new double[4];
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D && ((IneChartModule2D) modul).isAutoCalcPadding()){
					padding = LabelFactory.mergePaddings(padding,((IneChartModule2D) modul).getPaddingForAxes());
				}
			}
			padding = LabelFactory.addPaddings(padding, moduleAssist.getLabelFactory().getPaddingNeeded());
			for (IneChartModule modul : moduls) {
				if(modul instanceof IneChartModule2D && ((IneChartModule2D) modul).isAutoCalcPadding()){
					((IneChartModule2D) modul).setPadding(padding);
				}
			}
			moduleAssist.getAxes().updateWithOutAutoTickCreation();

			validate();
		}


		for (IneChartModule modul : moduls) {
			modul.update();

		}

		moduleAssist.getMainCanvas().removeAllGraphicalObjects();
		for (IneChartModule modul : moduls) {
			if (modul.isVisible()){
				moduleAssist.getMainCanvas().addAllGraphicalObject(modul.getGraphicalObjectContainer());
			}
		}
		moduleAssist.getMainCanvas().addAllGraphicalObject(moduleAssist.getAxes().getGraphicalObjectContainer());
		moduleAssist.getMainCanvas().addAllGraphicalObject(moduleAssist.getLabelFactory().getGraphicalObjectContainer());

		moduleAssist.getMainCanvas().update();
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
		((DrawingAreaAwt) moduleAssist.getMainCanvas()).saveToFile(filename);
	}

	public void saveToOutputStream(OutputStream outputStream) {
		((DrawingAreaAwt) moduleAssist.getMainCanvas()).saveToOutputStream(outputStream);
	}

	public BufferedImage getImage() {
		return ((DrawingAreaAwt) moduleAssist.getMainCanvas()).getImage();
	}

	public void setChartTitle(String title){
		setChartTitle(new ChartTitle(title));
	}

	public void setChartTitle(String title,String description){
		setChartTitle(new ChartTitle(title,description));
	}

	public void setChartTitle(ChartTitle title){
		moduleAssist.getLabelFactory().setChartTitle(title);
	}

	public ChartTitle getChartTitle(){
		return moduleAssist.getLabelFactory().getChartTitle();
	}
}
