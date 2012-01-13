package com.inepex.inechart.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.inepex.inechart.chartwidget.resources.ScrollStyle;
import com.inepex.inechart.misc.scroll.ScrollBarPresenter;
import com.inepex.inechart.misc.scroll.ScrollViewFactory;
import com.inepex.inechart.misc.scroll.Scrollable;
import com.inepex.inechart.misc.scroll.Spinnable;
import com.inepex.inechart.misc.scroll.SpinnerPresenter;
import com.inepex.inechart.misc.scroll.SpinnerPresenter.View;
import com.inepex.inechart.misc.scroll.defaultviews.ScrollBarView;

public class IntervalSelectionWidget extends Composite {
	
	protected class SpinnerView implements View{
		Label spinner;
		ScrollStyle scrollStyle;
		
		public SpinnerView(ScrollStyle scrollStyle) {
			this.scrollStyle = scrollStyle;
			spinner = new Label();
			spinner.setStyleName(scrollStyle.normal());
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
			mainPanel.setWidgetPosition(spinner, position, spinnerViewTop);
		}

		@Override
		public void setSpinnerBeingDragged(boolean spinnerBeingDragged) {
			if(spinnerBeingDragged){
				spinner.setStyleName(scrollStyle.active());
				DOM.setStyleAttribute(spinner.getElement(), "zIndex", DOM.getElementPropertyInt(mainPanel.getElement(), "zIndex")+1+"");
				
			}
			else{
				spinner.setStyleName(scrollStyle.normal());
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
			scrollBarPresenter.setSlider(Math.min(min, max), Math.max(min, max));
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
			resizableInterval.intervalSet(Math.min(min, max), Math.max(min, max));
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
	
	protected int height;
	protected int spinnableWidth; 
	protected int spinnerWidgetWidth = 10;
	protected int spinnerWidgetHeight = 25;
	protected int spinnerViewTop = 0;
	
	protected ResizableInterval resizableInterval;
	
	protected SpinnerPresenter spinnerPresenter1;
	protected SpinnerPresenter spinnerPresenter2;
	protected ScrollBarPresenter scrollBarPresenter;
	protected SpinnerView spinnerView1;
	protected SpinnerView spinnerView2;
	protected ScrollBarView scrollBarView;
	protected boolean showScrollBar;
	protected ScrollViewFactory viewFactory;
	
	protected double min, max;
	
	public IntervalSelectionWidget(int width, boolean spinnersIncludedInWidth, int height, boolean showScrollBar,
			ResizableInterval resizableInterval, ScrollViewFactory viewFactory) {
		this.viewFactory = viewFactory;
		this.height = height;
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
		spinnerView1 = new SpinnerView(viewFactory.getScrollStyle());
		spinnerView2 = new SpinnerView(viewFactory.getScrollStyle());

		scrollBarView = viewFactory.createScrollBarView(true, spinnableWidth, height, 2, false);
				
		if(showScrollBar){
			mainPanel.add(scrollBarView, spinnerWidgetWidth / 2, 0);
		}
		
		mainPanel.add(spinnerView1.spinner, 0,
//				showScrollBar ? scrollBarView.getHeight() :
					0);
		mainPanel.add(spinnerView2.spinner, 0,
//				showScrollBar ? scrollBarView.getHeight() : 
					0);
		mainPanel.setPixelSize(spinnableWidth + spinnerWidgetWidth,
				height > 0 ? height : spinnerWidgetHeight 
//						+(showScrollBar ? scrollBarView.getHeight() : 0)
						);
		
		
		spinnerPresenter1 = new SpinnerPresenter(spinnerView1, new SpinnableImpl(true));
		spinnerPresenter2 = new SpinnerPresenter(spinnerView2, new SpinnableImpl(false));
		
		scrollBarPresenter = new ScrollBarPresenter(new ScrollableImpl(), scrollBarView, null);
//		spinnerViewTop = showScrollBar ? scrollBarView.getHeight() : 0;
	}

	public int getSpinnerWidgetWidth() {
		return spinnerWidgetWidth;
	}

	public int getSpinnerWidgetHeight() {
		return spinnerWidgetHeight;
	}	
	
	public int getTotalHeight() {
		return spinnerWidgetHeight
//				+ (showScrollBar ? scrollBarView.getHeight() : 0)
				;
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
	
	public void setHeight(int height){
		if(height > 0){
			spinnerViewTop = (height - spinnerWidgetHeight 
//					+(showScrollBar ? scrollBarView.getHeight() : 0)
					) / 2;
		}
		this.height = height;
	}
}
