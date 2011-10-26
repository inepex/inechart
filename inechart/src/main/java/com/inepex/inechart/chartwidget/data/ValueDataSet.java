package com.inepex.inechart.chartwidget.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.inepex.inechart.chartwidget.properties.Color;

/**
 * 
 * A data collection containing values - a list of {@link ValueDataEntry}
 * The color, title and description is related to the dataset, not to its elements.
 * 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class ValueDataSet extends AbstractDataSet {

	protected Color color;
	
	protected boolean sortable = false;
	protected boolean sorted = false;
	
	protected double max = - Double.MAX_VALUE, min = Double.MAX_VALUE;
	
	protected ArrayList<ValueDataEntry> entries;
	
	public ValueDataSet() {
		super();
		entries = new ArrayList<ValueDataEntry>();
	}
	
	public ValueDataSet(String title){
		super(title);
		entries = new ArrayList<ValueDataEntry>();
	}
	
	public ValueDataSet(String title, String description) {
		super(title, description);
		entries = new ArrayList<ValueDataEntry>();
	}
	
	protected void findExtremes(){
		max = - Double.MAX_VALUE;
		min = Double.MAX_VALUE;
		for(ValueDataEntry e : entries){
			checkExtremes(e.value);
		}
	}
	
	protected void checkExtremes(double value){
		if(value > max){
			max = value;
		}
		else if(value < min){
			min = value;
		}
	}
	
	protected void sortIf(){
		if(!sortable || sorted){
			return;
		}
		Collections.sort(this.entries, ValueDataEntry.getValueComparator());
		sorted = true;
	}
	
	public void addValueDataEntries(Collection<ValueDataEntry> entries){
		for(ValueDataEntry e : entries){
			e.container = this;
			this.entries.add(e);
		}
		findExtremes();
		sorted = false;
		fireDataSetChangeEvent();
	}
	
	public void addValues(double[] values){
		for(double v : values){
			ValueDataEntry e = new ValueDataEntry(v);
			e.container = this;
			entries.add(e);
		}
		findExtremes();
		sorted = false;
		fireDataSetChangeEvent();
	}
	
	public void addValues(Collection<? extends Number> values){
		Iterator<? extends Number> it = values.iterator();
		while(it.hasNext()){
			ValueDataEntry e = new ValueDataEntry(it.next().doubleValue());
			e.container = this;
			entries.add(e);
		}
		findExtremes();
		sorted = false;
		fireDataSetChangeEvent();
	}
	
	public void addValue(double value){
		addValueDataEntry(new ValueDataEntry(value));
	}
	
	public void addValueDataEntry(ValueDataEntry e){
		e.container = this;
		checkExtremes(e.value);
		sorted = false;
		fireDataSetChangeEvent();
	}
	
	public void removeEntry(ValueDataEntry e){
		if(entries.remove(e)){
			findExtremes();
			fireDataSetChangeEvent();
		}
	}
	
	public ArrayList<ValueDataEntry> getEntries() {
		return entries;
	}
	
}
