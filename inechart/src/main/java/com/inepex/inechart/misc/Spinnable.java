package com.inepex.inechart.misc;

public interface Spinnable {
	
	void spinnerMoved(double distance);

	double getSpinnableDomainLength();
	
	double getInitialPosition();
	
}
