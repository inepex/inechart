package com.inepex.inecharting.chartwidget.model;

public interface HaszIndex {
	public final int alwaysOnTop = Integer.MAX_VALUE;
	public final int alwaysOnBot = Integer.MIN_VALUE;
	
	public int getzIndex();
	
	public void setzIndex(int zIndex);

}
