package com.inepex.inechart.misc;

/**
 * An object using {@link ScrollBarPresenter} must implement this interface.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public interface Scrollable {

	void scrollBarMoved(double distance);
	
	void scrollBarResized(double position, double width);
	
	double getScrollableDomainLength();
	
	double getInitialIntervalMin();
	
	double getInitialIntervalMax();
	
	double getIntervalStep();
	
	void dragStart();
	
	void dragEnd();

}
