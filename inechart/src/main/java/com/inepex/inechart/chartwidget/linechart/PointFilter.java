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
			ArrayList<DataPoint> newDp = null;
			DataPoint last = null;
			for(DataPoint actual : unfiltered){
				if(last == null){
					last = actual;
					continue;
				}
				if(areOverlappingPoints(last, actual)){
					if(newDp == null){
						newDp = new ArrayList<DataPoint>();
						newDp.add(last);
					}
					newDp.add(actual);
				}
				else{
					if(newDp == null) {
						filtered.add(last);
						last = actual;
					}
					else{
						filtered.add(applyFilterPolicy(newDp));
						newDp = null;
						last = null;
					}
				}
			}
			if(newDp != null) {
				filtered.add(applyFilterPolicy(newDp));
			}
			else if(last != null){
				filtered.add(last);
			}
			return filtered;
		}	
	}

	protected DataPoint applyFilterPolicy(ArrayList<DataPoint> dps){
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = - Double.MAX_VALUE, maxY = - Double.MAX_VALUE;
		DataPoint ret = new DataPoint();
		for(DataPoint dp : dps){
			if(minX > dp.canvasX){
				minX = dp.canvasX;
			}
			if(maxX < dp.canvasX){
				maxX = dp.canvasX;
			}
			if(minY > dp.canvasY){
				minY = dp.canvasY;
			}
			if(maxY < dp.canvasY){
				maxY = dp.canvasY;
			}
			ret.addFilteredPoint(dp);
		}
		switch (policy) {
		case average:
			ret.canvasX = minX + (maxX - minX) / 2;
			ret.canvasY = minY + (maxY - minY) / 2;
			break;
		case lower:
			ret.canvasX = maxX;
			ret.canvasY = maxY;
			break;
		case higher:
			ret.canvasX = minX;
			ret.canvasY = minY;
			break;
		}	
		ret.isInViewport = lineChart.isInsideModul(ret.canvasX, ret.canvasY);
		return ret;
	}

	protected boolean areOverlappingPoints(DataPoint first, DataPoint second){
		if(Math.abs(first.canvasX - second.canvasX) <= horizontalFilter || Math.abs(first.canvasY - second.canvasY) <= verticalFilter){
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
