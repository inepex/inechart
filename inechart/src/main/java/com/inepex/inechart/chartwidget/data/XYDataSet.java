package com.inepex.inechart.chartwidget.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.properties.Color;

/**
 * 
 * DataSet containing [x,y] datapairs, but you can use it as an only-value container, in this case
 * the x values are growing integers from 0.
 * The color, title and description is related to the dataset, not to its elements.
 * 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class XYDataSet extends AbstractDataSet {
	
	protected Color color;
	
	protected boolean allowXDuplicates = true;
	protected boolean sortable = false;
	protected boolean sorted = false;
	protected boolean onlyValues = false;
	
	protected double xMax, yMax, xMin, yMin;
	
	public XYDataSet() {
		super();
	}
	
	public void addYList(List<Double> values){
		onlyValues = true;
		sortable = false;
		int i = 0;
		for(double y : values){
			dataEntries.add(new DataEntry(i++, y));
		}
	}
	
	public void addY(double y){
		onlyValues = true;
		sortable = false;
		int i = 0;
		if(dataEntries.size() > 0){
			i = (int) dataEntries.get(dataEntries.size() - 1).key;
		}
		dataEntries.add(new DataEntry(i + 1, y));
	}
		
	public void addDataPairs(Map<? extends Number, ? extends Number> dataPairs){
		for(Number n : dataPairs.keySet()){
			dataEntries.add(new DataEntry(n.doubleValue(), dataPairs.get(n).doubleValue()));
		}
		findExtremes();
		if(!allowXDuplicates){
			eliminateDuplicates();
		}
		sorted = false;
		fireDataSetChangeEvent();
	}
	
	public void addDataPair(double[] dataPair){
		if(dataPair.length < 2){
			return;
		}
		addDataPair(dataPair[0], dataPair[1]);
	}
	
	public void addDataPair(double x, double y){
		if(!allowXDuplicates){
			DataEntry duplicate = getEntryByKey(x);
			if(duplicate != null){
				duplicate.key = x;
				duplicate.value = y;
			}
			else {
				dataEntries.add(new DataEntry(x, y));
			}
		}
		else {
			dataEntries.add(new DataEntry(x, y));
		}
		checkExtremes(x, y);
		sorted = false;
		fireDataSetChangeEvent();
	}

	public void removeEntry(double x){
		DataEntry e = getEntryByKey(x);
		if(e != null){
			dataEntries.remove(e);
		}
		findExtremes();
		fireDataSetChangeEvent();
	}
	
	protected void findExtremes(){
		xMax = yMax = - Double.MAX_VALUE;
		yMin = xMin = Double.MAX_VALUE;
		for(DataEntry de : dataEntries){
			checkExtremes(de.key, de.value);
		}
	}
	
	protected void checkExtremes(double x, double y){
		if(x > xMax){
			xMax = x;
		}
		else if(x < xMin){
			xMin = x;
		}
		if(y > yMax){
			yMax = y;
		}
		else if(y < yMin){
			yMin = y;
		}
	}

	
	protected void sortIf(){
		if(!sortable || sorted){
			return;
		}
		Collections.sort(this.dataEntries, DataEntry.keyComparator());
		sorted = true;
	}
	
	protected void eliminateDuplicates(){
		ArrayList<DataEntry> toRemove = new ArrayList<DataEntry>();
		for(DataEntry de : dataEntries){
			DataEntry e = getEntryByKey(de.key);
			if(e != null && e != de){
				toRemove.add(e);
			}
		}
		for(DataEntry de : toRemove){
			dataEntries.remove(de);
		}
	}
	
	public Color getColor() {
		return color;
	}
	

	public void setColor(Color color) {
		this.color = color;
		fireDataSetChangeEvent();
	}
	

	public double getxMax() {
		return xMax;
	}
	

	public double getyMax() {
		return yMax;
	}
	

	public double getxMin() {
		return xMin;
	}
	

	public double getyMin() {
		return yMin;
	}
		
	public TreeMap<Double, Double> getXYMap(){
		sortIf();
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		int i = 0;
		for(DataEntry de : dataEntries){
			if(onlyValues){
				map.put((double) i++, de.value);
			}
			else {
				map.put(de.key, de.value);
			}
		}
		return map;
	}
	
	public ArrayList<Double> getXList(){
		sortIf();
		ArrayList<Double> list = new ArrayList<Double>();
		int i = 0;
		for(DataEntry de : dataEntries){
			if(onlyValues){
				list.add((double) i++);
			}
			else {
				list.add(de.key);
			}
		}
		return list;
	}
	
	public ArrayList<Double> getYList(){
		sortIf();
		ArrayList<Double> list = new ArrayList<Double>();
		for(DataEntry de : dataEntries){
			list.add(de.value);
		}
		return list;
	}
	
}
