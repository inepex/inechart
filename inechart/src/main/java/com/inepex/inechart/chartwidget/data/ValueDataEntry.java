package com.inepex.inechart.chartwidget.data;

import java.util.Comparator;

public class ValueDataEntry extends AbstractDataEntry implements XYDataEntry{

	protected double value;

	public ValueDataEntry(double value) {
		super();
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		fireChangeEvent();
	}

	public static Comparator<ValueDataEntry> getValueComparator(){
		return new Comparator<ValueDataEntry>() {
			
			@Override
			public int compare(ValueDataEntry o1, ValueDataEntry o2) {
				return Double.compare(o1.value, o2.value);
			}
		};
	}

	@Override
	public double getX() {
		if(container != null && container instanceof AbstractXYDataSet){
			return ((AbstractXYDataSet)container).getXForEntry(this);
		}
		return 0;
	}

	@Override
	public double getY() {
		return value;
	}

	@Override
	public int compareTo(XYDataEntry o) {
		int i = Double.compare(getX(), o.getX());
		if(i == 0){
			i = Double.compare(getY(), o.getY());
			if(i == 0 && o instanceof AbstractDataEntry){
				i = container.title.compareTo(((AbstractDataEntry)o).container.title);
			}
		}
		return i;
	}
	

}
