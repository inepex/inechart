package com.inepex.inechart.chartwidget.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * A data collection containing [x,y] datapairs - a list of {@link KeyValueDataEntry}
 * The color, title and description is related to the dataset, not to its elements.
 * 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class KeyValueDataSet extends AbstractXYDataSet{
	
	protected boolean sorted = false;
	
	protected double xMax = - Double.MAX_VALUE, yMax = - Double.MAX_VALUE, xMin = Double.MAX_VALUE, yMin = Double.MAX_VALUE;
	
	protected ArrayList<KeyValueDataEntry> entries;
	
	public KeyValueDataSet() {
		super();
		entries = new ArrayList<KeyValueDataEntry>();
	}
	
	public KeyValueDataSet(String title){
		super(title);
		entries = new ArrayList<KeyValueDataEntry>();
	}
	
	public KeyValueDataSet(String title, String description) {
		super(title, description);
		entries = new ArrayList<KeyValueDataEntry>();
	}
	
	public void addDataPairs(Map<? extends Number, ? extends Number> dataPairs){
		ArrayList<KeyValueDataEntry> entries = new ArrayList<KeyValueDataEntry>();
		for(Number n : dataPairs.keySet()){
			entries.add(new KeyValueDataEntry(n.doubleValue(), dataPairs.get(n).doubleValue()));
		}
		addKeyValueDataEntries(entries);
	}
	
	public void addDataPairs(Collection<? extends Number[]> dataPairs){
		ArrayList<KeyValueDataEntry> entries = new ArrayList<KeyValueDataEntry>();
		Iterator<? extends Number[]> it = dataPairs.iterator();
		while(it.hasNext()){
			Number[] dp = it.next();
			entries.add(new KeyValueDataEntry(dp[0].doubleValue(), dp[1].doubleValue()));
		}
		addKeyValueDataEntries(entries);
	}
	
	public void addKeyValueDataEntries(List<KeyValueDataEntry> entries){
		for(KeyValueDataEntry e : entries){
			e.container = this;
			this.entries.add(e);
		}
		if(!allowXDuplicates){
			eliminateDuplicates();
		}
		findExtremes();
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
		addKeyValueDataEntry(new KeyValueDataEntry(x, y));
	}
	
	public void addKeyValueDataEntry(KeyValueDataEntry entry){
		entry.container = this;
		if(!allowXDuplicates){
			KeyValueDataEntry duplicate = getSameKeyedEntry(entry);
			if(duplicate != null){
				duplicate.value = entry.value;
			}
			else {
				entries.add(entry);
			}
		}
		else {
			entries.add(entry);
		}
		checkExtremes(entry.key, entry.value);
		sorted = false;
		fireDataSetChangeEvent();
	}

	public void removeEntry(double x){
		KeyValueDataEntry e = (KeyValueDataEntry) getEntry(x);
		if(e != null){
			entries.remove(e);
			findExtremes();
			fireDataSetChangeEvent();
		}
	}
	
	public void removeEntry(KeyValueDataEntry entry){
		if(entries.remove(entry)){
			findExtremes();
			fireDataSetChangeEvent();
		}
	}
	
	protected void findExtremes(){
		xMax = yMax = - Double.MAX_VALUE;
		yMin = xMin = Double.MAX_VALUE;
		for(KeyValueDataEntry de : entries){
			checkExtremes(de.key, de.value);
		}
	}
	
	protected void checkExtremes(double x, double y){
		if(x > xMax){
			xMax = x;
		}
		if(x < xMin){
			xMin = x;
		}
		if(y > yMax){
			yMax = y;
		}
		if(y < yMin){
			yMin = y;
		}
	}

	protected void sortIf(){
		if(!sortable || sorted){
			return;
		}
		Collections.sort(this.entries);
		sorted = true;
	}
	
	public KeyValueDataEntry getSameKeyedEntry(KeyValueDataEntry entry){
		for(KeyValueDataEntry e : entries){
			if(e.compareTo(entry) == 0){
				return e;
			}
		}
		return null;
	}
	
	public double getY(double x){
		KeyValueDataEntry e = (KeyValueDataEntry) getEntry(x);
		return e == null ? 0 : e.value;
	}
	
	protected void eliminateDuplicates(){
		ArrayList<KeyValueDataEntry> toRemove = new ArrayList<KeyValueDataEntry>();
		for(KeyValueDataEntry de : entries){
			KeyValueDataEntry e = (KeyValueDataEntry) getEntry(de.key);
			if(e != null && e != de){
				toRemove.add(e);
			}
		}
		for(KeyValueDataEntry de : toRemove){
			entries.remove(de);
		}
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
		for(KeyValueDataEntry de : entries){
			map.put(de.key, de.value);
		}
		return map;
	}
	
	public ArrayList<Double> getXList(){
		sortIf();
		ArrayList<Double> list = new ArrayList<Double>();
		for(KeyValueDataEntry de : entries){
			list.add(de.key);
		}
		return list;
	}
	
	public ArrayList<Double> getYList(){
		sortIf();
		ArrayList<Double> list = new ArrayList<Double>();
		for(KeyValueDataEntry de : entries){
			list.add(de.value);
		}
		return list;
	}

	public final ArrayList<KeyValueDataEntry> getEntries() {
		sortIf();
		return entries;
	}

	public void setEntries(ArrayList<KeyValueDataEntry> entries) {
		this.entries = entries;
	}

	@Override
	public ArrayList<XYDataEntry> getXYDataEntries() {
		ArrayList<XYDataEntry> map = new ArrayList<XYDataEntry>();
		sortIf();
		for(KeyValueDataEntry e : entries){
			map.add(e);
		}
		return map;
	}

	@Override
	protected double getXForEntry(AbstractDataEntry child) {
		return ((KeyValueDataEntry)child).key;
	}
	
	@Override
	public boolean containsXYDataEntry(XYDataEntry entry) {
		if(entry instanceof KeyValueDataEntry){
			return entries.contains(entry);
		}
		return false;
	}

	@Override
	public ArrayList<XYDataEntry> getXYDataEntries(double fromX, double toX) {
		ArrayList<XYDataEntry> map = new ArrayList<XYDataEntry>();
		sortIf();
		for(KeyValueDataEntry e : entries){
			if(sortable && e.key > toX){
				break;
			}
			if(e.key >= fromX && e.key <= toX){
				map.add(e);
			}
		}
		return map;
	}

	@Override
	public XYDataEntry getEntry(double x, double y) {
		for(KeyValueDataEntry e : entries){
			if(Double.compare(x, e.key) == 0 && Double.compare(y, e.value) == 0){
				return e;
			}
		}
		return null;
	}

	
	@Override
	public XYDataEntry getEntry(double x) {
		for(KeyValueDataEntry e : entries){
			if(Double.compare(x, e.key) == 0){
				return e;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		entries.clear();		
	}
	
}
