package com.inepex.inechart.misc.scroll;

public interface SpinnablePair {

	void spinnerMoved1(double distance);
	
	void spinnerMoved2(double distance);
	
	double getSpinnableDomainLength();
	
	double getInitialPosition1();
	
	double getInitialPosition2();
	
	void dragStart();
	
	void dragEnd();
}
