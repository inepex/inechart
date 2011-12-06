package com.inepex.inechart.misc.scroll;

public interface Spinnable {
	
	void spinnerMoved(double distance);

	double getSpinnableDomainLength();
	
	double getInitialPosition();
	
	void dragStart();
	
	void dragEnd();
	
}
