package com.inepex.inecharting.chartwidget.model;

public class Tick implements Comparable<Tick>{
	private int position;
	private boolean hasText;

	@Override
	public int compareTo(Tick o) {
		return position - o.position;
	}
	
}
