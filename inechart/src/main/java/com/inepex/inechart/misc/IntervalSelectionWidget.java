package com.inepex.inechart.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inechart.misc.SpinnerPresenter.View;

public class IntervalSelectionWidget extends Composite {
	
	protected class SpinnerView implements View{

		Label spinner;
		
		public SpinnerView() {
			spinner = new Label();
			spinner.setStyleName(ResourceHelper.getRes().style().normal());
		}
		
		@Override
		public IsWidget getSpinner() {
			return spinner;
		}

		@Override
		public int getSpinnableAreaLength() {
			return spinnableWidth;
		}

		@Override
		public void setSpinnerPosition(int position) {
			mainPanel.setWidgetPosition(spinner, position, showScrollBar ? scrollBarView.getHeight() : 0);
		}

		@Override
		public void setSpinnerBeingDragged(boolean spinnerBeingDragged) {
			if(spinnerBeingDragged){
				spinner.setStyleName(ResourceHelper.getRes().style().active());
				DOM.setStyleAttribute(spinner.getElement(), "zIndex", DOM.getElementPropertyInt(mainPanel.getElement(), "zIndex")+1+"");
				
			}
			else{
				spinner.setStyleName(ResourceHelper.getRes().style().normal());
				DOM.setStyleAttribute(spinner.getElement(), "zIndex", DOM.getElementPropertyInt(mainPanel.getElement(), "zIndex")+"");
			}
		}
		
		
	}
	
	protected class SpinnableImpl implements Spinnable{
		boolean isMin;
		
		protected SpinnableImpl(boolean isMin) {
			this.isMin = isMin;
		}

		@Override
		public void spinnerMoved(double distance) {
			if(isMin){
				min += distance;
			}
			else{
				max += distance;
			}
			scrollBarPresenter.setSlider(min, max);
			resizableInterval.intervalSet(Math.min(min, max), Math.max(min, max));
		}

		@Override
		public double getSpinnableDomainLength() {
			return resizableInterval.getMaximumSize();
		}

		@Override
		public double getInitialPosition() {
			if(isMin){
				return min;
			}
			else{
				return max;
			}
		}

		@Override
		public void dragStart() {
			resizableInterval.dragStart();			
		}

		@Override
		public void dragEnd() {
			resizableInterval.dragEnd();
		}	
	}
	
	protected class ScrollableImpl implements Scrollable{

		@Override
		public void scrollBarMoved(double distance) {
			min += distance;
			max += distance;
			resizableInterval.intervalSet(min, max);
			spinnerPresenter1.setSpinnerPosition(min);
			spinnerPresenter2.setSpinnerPosition(max);
		}

		@Override
		public void scrollBarResized(double position, double width) {
			resizableInterval.intervalSet(position, position + width);
			spinnerPresenter1.setSpinnerPosition(position);
			spinnerPresenter2.setSpinnerPosition(position + width);
		}

		@Override
		public double getScrollableDomainLength() {
			return resizableInterval.getMaximumSize();
		}

		@Override
		public double getInitialIntervalMin() {
			return resizableInterval.getInitialMin();
		}

		@Override
		public double getInitialIntervalMax() {
			return resizableInterval.getInitialMax();
		}

		@Override
		public double getIntervalStep() {
			// not used, because theres no arrows
			return 0;
		}

		@Override
		public void dragStart() {
			resizableInterval.dragStart();			
		}

		@Override
		public void dragEnd() {
			resizableInterval.dragEnd();
		}
		
	}
	
	protected AbsolutePanel mainPanel;
	
	protected int spinnableWidth; 
	protected int spinnerWidgetWidth = 20;
	protected int spinnerWidgetHeight = 32;
	
	protected ResizableInterval resizableInterval;
	
	protected SpinnerPresenter spinnerPresenter1;
	protected SpinnerPresenter spinnerPresenter2;
	protected ScrollBarPresenter scrollBarPresenter;
	protected SpinnerView spinnerView1;
	protected SpinnerView spinnerView2;
	protected ScrollBarView scrollBarView;
	protected boolean showScrollBar;
	
	protected double min, max;
	
	public IntervalSelectionWidget(int width, boolean spinnersIncludedInWidth, boolean showScrollBar, ResizableInterval resizableInterval) {
		if(spinnersIncludedInWidth){
			spinnableWidth = width - spinnerWidgetWidth;
		}
		else{
			spinnableWidth = width;
		}
		this.showScrollBar = showScrollBar;
		this.resizableInterval = resizableInterval;
		min = resizableInterval.getInitialMin();
		max = resizableInterval.getInitialMax();	
		init();
		initWidget(mainPanel);
	}
	
	protected void init(){
		if(mainPanel != null){
			mainPanel.clear();
		}
		else{
			mainPanel = new AbsolutePanel();
		}
		spinnerView1 = new SpinnerView();
		spinnerView2 = new SpinnerView();
				
		scrollBarView = new ScrollBarView(true, spinnableWidth, 10, 2, false);
				
		mainPanel.add(spinnerView1.spinner, 0, showScrollBar ? scrollBarView.getHeight() : 0);
		mainPanel.add(spinnerView2.spinner, 0, showScrollBar ? scrollBarView.getHeight() : 0);
		mainPanel.setPixelSize(spinnableWidth + spinnerWidgetWidth,
				spinnerWidgetHeight + (showScrollBar ? scrollBarView.getHeight() : 0));
		if(showScrollBar){
			mainPanel.add(scrollBarView, spinnerWidgetWidth / 2, 0);
		}
		
		spinnerPresenter1 = new SpinnerPresenter(spinnerView1, new SpinnableImpl(true));
		spinnerPresenter2 = new SpinnerPresenter(spinnerView2, new SpinnableImpl(false));
		scrollBarPresenter = new ScrollBarPresenter(new ScrollableImpl(), scrollBarView);
	}

	public int getSpinnerWidgetWidth() {
		return spinnerWidgetWidth;
	}

	public int getSpinnerWidgetHeight() {
		return spinnerWidgetHeight;
	}	
	
	public int getTotalHeight(){
		return spinnerWidgetHeight + (showScrollBar ? scrollBarView.getHeight() : 0);
	}
	
	public void setInterval(double min, double max){
		this.min = min;
		this.max = max;
		spinnerPresenter1.setSpinnerPosition(min);
		spinnerPresenter2.setSpinnerPosition(max);
		scrollBarPresenter.setSlider(min, max);
	}
	
	public void setWidth(int width, boolean spinnersIncludedInWidth){
		if(spinnersIncludedInWidth){
			spinnableWidth = width - spinnerWidgetWidth;
		}
		else{
			spinnableWidth = width;
		}
		init();
	}
}
