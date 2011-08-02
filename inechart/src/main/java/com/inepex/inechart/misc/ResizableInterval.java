package com.inepex.inechart.misc;

public interface ResizableInterval {
	double getMaximumSize();
	
	double getInitialMin();
	
	double getInitialMax();
	
	void intervalSet(double min, double max);

}
