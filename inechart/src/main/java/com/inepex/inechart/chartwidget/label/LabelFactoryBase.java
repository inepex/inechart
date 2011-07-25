package com.inepex.inechart.chartwidget.label;


import java.util.ArrayList;
import com.inepex.inechart.chartwidget.IneChartModule;
import com.inepex.inegraphics.shared.DrawingArea;

/**
 * Base class for displaying texts in the chart.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class LabelFactoryBase extends IneChartModule{
	
	protected ChartTitle chartTitle;
	protected ArrayList<HasLegend> legendOwners;
	
	
	
	protected LabelFactoryBase(DrawingArea canvas) {
		super(canvas);
		legendOwners = new ArrayList<HasLegend>();
	}
	
	public void setChartTitle(ChartTitle chartTitle) {
		this.chartTitle = chartTitle;
	}
	
	/**
	 * @return the chartTitle
	 */
	public ChartTitle getChartTitle() {
		return chartTitle;
	}

	public void addLegendOwner(HasLegend legendOwner){
		legendOwners.add(legendOwner);
	}
	
	public abstract double[] getPaddingNeeded();
	
	public static double[] mergePaddings(double[] first, double[] second){
		double[] ret = new double[4];
		for(int i=0;i<4;i++){
			ret[i] = Math.max(first[i], second [i]);
		}
		return ret;
	}
	
	public static double[] addPaddings(double[] first, double[] second){
		double[] ret = new double[4];
		for(int i=0;i<4;i++){
			ret[i] = first[i] + second [i];
		}
		return ret;
	}
	
}
