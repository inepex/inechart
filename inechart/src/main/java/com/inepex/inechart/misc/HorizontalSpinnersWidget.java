package com.inepex.inechart.misc;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inechart.misc.scroll.ScrollBarPresenter;
import com.inepex.inechart.misc.scroll.ScrollViewFactory;
import com.inepex.inechart.misc.scroll.Scrollable;
import com.inepex.inechart.misc.scroll.SpinnablePair;
import com.inepex.inechart.misc.scroll.SpinnerPairPresenter;
import com.inepex.inechart.misc.scroll.SpinnerPairPresenter.View;
import com.inepex.inechart.misc.scroll.defaultviews.DefaultScrollViewFactory;
import com.inepex.inechart.misc.scroll.defaultviews.ScrollBarView;

public class HorizontalSpinnersWidget extends Composite {
	
	protected class SpinnerView implements View{
		
		SpinnerWidget spinnerWidget1;
		SpinnerWidget spinnerWidget2;
		int pos1, pos2;
		String defaultCursor;
		
		public SpinnerView() {
			spinnerWidget1 = new SpinnerWidget();
			spinnerWidget1.setLeft();
			spinnerWidget2 = new SpinnerWidget();
			spinnerWidget2.setRight();
		}
		
		@Override
		public int getSpinnableAreaLength() {
			return spinnableWidth;
		}


		@Override
		public IsWidget getSpinner1() {
			return spinnerWidget1;
		}

		@Override
		public IsWidget getSpinner2() {
			return spinnerWidget2;
		}

		@Override
		public void setSpinnerPosition1(int position) {
			pos1 = position;
			updateSideDependentStyles();
			mainPanel.setWidgetPosition(spinnerWidget1, position, 0);
		}

		@Override
		public void setSpinnerPosition2(int position) {
			pos2 = position;
			updateSideDependentStyles();
			mainPanel.setWidgetPosition(spinnerWidget2, position, 0);
		}

		@Override
		public int getMinDistanceBetweenSpinners() {
			return 25;
		}

		@Override
		public boolean canSpinnersSwapPosition() {
			return true;
		}

		@Override
		public void setSpinnerBeingDragged(boolean spinnerBeingDragged,
				boolean first) {
			if(spinnerBeingDragged){
				defaultCursor = RootPanel.get().getElement().getStyle().getCursor();
				RootPanel.get().getElement().getStyle().setProperty("cursor", "e-resize");
			}
			else{
				RootPanel.get().getElement().getStyle().setProperty("cursor", defaultCursor);
			}
		}
		
		private void updateSideDependentStyles(){
			if(pos1 > pos2){
				spinnerWidget1.setRight();
				spinnerWidget2.setLeft();
			}
			else{
				spinnerWidget2.setRight();
				spinnerWidget1.setLeft();
			}
		}
	}
	
	protected class SpinnableImpl implements SpinnablePair{
		
		@Override
		public double getSpinnableDomainLength() {
			return resizableInterval.getMaximumSize();
		}

		@Override
		public void dragStart() {
			resizableInterval.dragStart();			
		}

		@Override
		public void dragEnd() {
			resizableInterval.dragEnd();
		}

		@Override
		public void spinnerMoved1(double distance) {
			min += distance;
			scrollBarPresenter.setSlider(Math.min(min, max), Math.max(min, max));
			resizableInterval.intervalSet(Math.min(min, max), Math.max(min, max));
		}

		@Override
		public void spinnerMoved2(double distance) {
			max += distance;
			scrollBarPresenter.setSlider(Math.min(min, max), Math.max(min, max));
			resizableInterval.intervalSet(Math.min(min, max), Math.max(min, max));
		}

		@Override
		public double getInitialPosition1() {
			return min;
		}

		@Override
		public double getInitialPosition2() {
			return max;
		}	
	}
	
	protected class ScrollableImpl implements Scrollable{

		@Override
		public void scrollBarMoved(double distance) {
			min += distance;
			max += distance;
			resizableInterval.intervalSet(Math.min(min, max), Math.max(min, max));
			spinnerPresenter.setSpinnerPosition1(min);
			spinnerPresenter.setSpinnerPosition2(max);
		}

		@Override
		public void scrollBarResized(double position, double width) {
			resizableInterval.intervalSet(position, position + width);
			spinnerPresenter.setSpinnerPosition1(min);
			spinnerPresenter.setSpinnerPosition2(max);
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
	protected int width;
	protected int spinnableWidth; 
	protected int spinnerWidgetWidth = 10;
	
	protected ResizableInterval resizableInterval;
	
	protected SpinnerPairPresenter spinnerPresenter;
	protected SpinnerView spinnerView;
	protected ScrollBarPresenter scrollBarPresenter;
	protected ScrollBarView scrollBarView;
	
	protected double min, max;

	
	public HorizontalSpinnersWidget(int width, boolean spinnersIncludedInWidth, int height,	ResizableInterval resizableInterval) {
		this.width = width;
		this.height = height;
		if(spinnersIncludedInWidth){
			spinnableWidth = width - spinnerWidgetWidth;
		}
		else{
			spinnableWidth = width;
		}
		this.resizableInterval = resizableInterval;
		min = resizableInterval.getInitialMin();
		max = resizableInterval.getInitialMax();	
		init();
		initWidget(mainPanel);
	}
	
	private void init(){
		if(mainPanel != null){
			mainPanel.clear();
		}
		else{
			mainPanel = new AbsolutePanel();
		}
		spinnerView = new SpinnerView();
		ScrollViewFactory sc = new DefaultScrollViewFactory(ResourceHelper.getRes().scrollStyle2());
		scrollBarView = sc.createScrollBarView(true, spinnableWidth, height, 0, false);
		
		mainPanel.add(scrollBarView, spinnerWidgetWidth / 2, 0);
		mainPanel.add(spinnerView.spinnerWidget1, 0, 0);
		mainPanel.add(spinnerView.spinnerWidget2, 0, 0);
		mainPanel.setPixelSize(width, height);
		
		spinnerPresenter = new SpinnerPairPresenter(spinnerView, new SpinnableImpl());
		scrollBarPresenter = new ScrollBarPresenter(new ScrollableImpl(), scrollBarView, null);
	}
	
	public void setInterval(double min, double max){
		this.min = min;
		this.max = max;
		spinnerPresenter.setSpinnerPosition1(min);
		spinnerPresenter.setSpinnerPosition2(max);
		scrollBarPresenter.setSlider(Math.min(min, max), Math.max(min, max));
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
		this.height = height;
		init();
	}
	
	public void setDimensions(int width, boolean spinnersIncludedInWidth, int height){
		
	}

	public AbsolutePanel getMainPanel() {
		return mainPanel;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getSpinnableWidth() {
		return spinnableWidth;
	}

	public int getSpinnerWidgetWidth() {
		return spinnerWidgetWidth;
	}

	public ResizableInterval getResizableInterval() {
		return resizableInterval;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
}
