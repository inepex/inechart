package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;

import com.inepex.inechart.chartwidget.Defaults;

public class PointFilter {

	public enum Policy{
		lower,
		higher,
		average
	}

	protected int horizontalFilter, verticalFilter;
	protected Policy policy;
	protected LineChart lineChart;

	public PointFilter() {
		this(Defaults.hotizontalFilter, Defaults.verticalFilter, Defaults.filterPolicy);
	}

	public PointFilter(int horizontalFilter, int verticalFilter, Policy policy) {
		this.horizontalFilter = horizontalFilter;
		this.verticalFilter = verticalFilter;
		this.policy = policy;
	}

	public ArrayList<DataPoint> filterDataPoints(ArrayList<DataPoint> unfiltered){

		if(horizontalFilter <= 0 && verticalFilter <= 0 || unfiltered.size() < 2){
			return unfiltered;
		}
		else{
			ArrayList<DataPoint> filtered = new ArrayList<DataPoint>();
			ArrayList<DataPoint> overlapping = null;
			DataPoint last = null;
			for(DataPoint actual : unfiltered){
				if(last == null){
					last = actual;
					continue;
				}
				if(areOverlappingPoints(last, actual)){
					if(overlapping == null){
						overlapping = new ArrayList<DataPoint>();
						overlapping.add(last);
					}
					overlapping.add(actual);
				}
				else{
					if(overlapping == null) {
						filtered.add(last);
					}
					else{
						filtered.add(applyFilterPolicy(overlapping));
						overlapping = null;
					}
					last = actual;
				}
			}
			if(overlapping != null) {
				filtered.add(applyFilterPolicy(overlapping));
			}
			else if(last != null){
				filtered.add(last);
			}
			return filtered;
		}	
	}

	protected DataPoint applyFilterPolicy(ArrayList<DataPoint> dps){
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = - Double.MAX_VALUE, maxY = - Double.MAX_VALUE;

		for(DataPoint dp : dps){
			if(minX > dp.data.getX()){
				minX = dp.data.getX();
			}
			if(maxX < dp.data.getX()){
				maxX = dp.data.getX();
			}
			if(minY > dp.data.getY()){
				minY = dp.data.getY();
			}
			if(maxY < dp.data.getY()){
				maxY = dp.data.getY();
			}

		}
		DataPoint ret;
		switch (policy) {
		case average:
			ret = new DataPoint(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2);
			break;
		case lower:
			ret = new DataPoint(minX + (maxX - minX) / 2, minY);
			break;
		case higher:
		default:
			ret = new DataPoint(minX + (maxX - minX) / 2, maxY);
			break;
		}	
		lineChart.setDataPoint(ret);
		ret.setFilteredPoints(dps);
		return ret;
	}

	protected boolean areOverlappingPoints(DataPoint first, DataPoint second){
		if(first.unfilterable || second.unfilterable){
			return false;
		}
		else if(Math.abs(first.canvasX - second.canvasX) <= horizontalFilter || Math.abs(first.canvasY - second.canvasY) <= verticalFilter){
			return true;
		}
		else return false;
	}

	protected void setLineChart(LineChart lineChart){
		this.lineChart = lineChart;
	}

	public int getHorizontalFilter() {
		return horizontalFilter;
	}

	public void setHorizontalFilter(int horizontalFilter) {
		this.horizontalFilter = horizontalFilter;
	}

	public int getVerticalFilter() {
		return verticalFilter;
	}

	public void setVerticalFilter(int verticalFilter) {
		this.verticalFilter = verticalFilter;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

}
