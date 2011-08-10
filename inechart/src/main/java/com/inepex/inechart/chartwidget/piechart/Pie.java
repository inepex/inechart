package com.inepex.inechart.chartwidget.piechart;

import java.util.ArrayList;


public class Pie{
	ArrayList<Slice> slices;
	
	public Pie() {
		slices = new ArrayList<Slice>();
	}

	public Pie(ArrayList<Slice> slices) {
		super();
		this.slices = slices;
	}

	public void addSlice(Slice slice){
		if(slice != null)
			slices.add(slice);
	}
	
	public void removeSlice(Slice slice){
		slices.remove(slice);
	}
	
	/**
	 * @return the slices
	 */
	public ArrayList<Slice> getSlices() {
		return slices;
	}

	/**
	 * @param slices the slices to set
	 */
	public void setSlices(ArrayList<Slice> slices) {
		this.slices = slices;
	}

	protected void calculatePercentages(){
		Double sum = 0.0;
		for (Slice slice : slices){
			sum +=slice.data;
		}
		for(Slice slice: slices){
			slice.percentage = slice.data / sum * 100.0;
		}

	}

}
