package com.inepex.inechart.chartwidget.label;


import java.util.ArrayList;

import com.inepex.inechart.chartwidget.IneChartModule;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inegraphics.shared.gobjects.Text;

/**
 * Base class for displaying texts in the chart.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class LabelFactory extends IneChartModule{

	protected ChartTitle chartTitle;
	protected Legend legend;
	protected ArrayList<HasLegendEntries> legendOwners;
	protected ArrayList<StyledLabel> styledLabels; 
	protected double[] paddingNeeded;
	
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
		
 	protected LabelFactory(ModuleAssist moduleAssist) {
		super(moduleAssist);
		legendOwners = new ArrayList<HasLegendEntries>();
		styledLabels = new ArrayList<StyledLabel>();
		legend = new Legend();
	}
 	 	
 	@Override
 	public void update() {
 		clear();	
 		
 		if(chartTitle != null &&
 				(chartTitle.description != null || chartTitle.title != null)){
 			createChartTitle();
 		}
 		
 		createLegend();
 		
 		for(StyledLabel label:styledLabels){
			createStyledLabel(label);
		}
 		
 		measurePadding();
 		
// 		super.update();
 	}
 	
 	
 	protected boolean isFixedPosition(TextContainer tc){
 		if(tc.left >= 0 || tc.top >= 0){
 			return true;
 		}
 		return false;
 	}
  	
 	protected abstract void clear();
 	
 	protected abstract void createChartTitle();
	
 	protected abstract void createLegend();
 	
 	protected abstract void createStyledLabel(StyledLabel label); 
 	
 	/**
 	 * Updates the given {@link StyledLabel}, without recalculating the padding.
 	 * Use this method when you want to (re)position a label.
 	 * @param label
 	 */
 	public abstract void updateStyledLabel(StyledLabel label);
 	
 	protected abstract void removeDisplayedStyledLabel(StyledLabel label);
 	 	
 	protected abstract void measurePadding();
 	
 	public void setChartTitle(ChartTitle chartTitle) {
		this.chartTitle = chartTitle;
	}
	
	public ChartTitle getChartTitle() {
		return chartTitle;
	}

	public void addLegendOwner(HasLegendEntries legendOwner){
		legendOwners.add(legendOwner);
	}
	
	public double[] getPaddingNeeded() {
		return paddingNeeded;
	}
	
	public void addStyledLabel(StyledLabel label){
		styledLabels.add(label);
	}
	
	public void removeStyledLabel(StyledLabel label){
		styledLabels.remove(label);
		removeDisplayedStyledLabel(label);
	}
	
	public void addAndDisplayStyledLabel(StyledLabel label){
		addStyledLabel(label);
		createStyledLabel(label);
	}

	/**
	 * @return the legend
	 */
	public Legend getLegend() {
		return legend;
	}

	/**
	 * @param legend the legend to set
	 */
	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public int[] measureStyledLabel(StyledLabel lbl){
		Text text = new Text(lbl.getText().getText(), 0, 0);
		text.setFontFamily(lbl.text.getTextProperties().getFontFamily());
		text.setFontStyle(lbl.text.getTextProperties().getFontStyle());
		text.setFontWeight(lbl.text.getTextProperties().getFontWeight());
		text.setColor(lbl.text.getTextProperties().getColor().getColor());
		text.setFontSize(lbl.text.getTextProperties().getFontSize());
		canvas.measureText(text);
		text.setWidth( (int) (text.getWidth() +
				lbl.getLeftPadding() + lbl.getRightPadding() +
				lbl.getBackground().getLineProperties().getLineWidth() * 2) );
		text.setHeight( (int) (text.getHeight() + 
				lbl.getTopPadding() + lbl.getBottomPadding() +
				lbl.getBackground().getLineProperties().getLineWidth() * 2) );
		return new int[]{text.getWidth(), text.getHeight()};
	}
}
