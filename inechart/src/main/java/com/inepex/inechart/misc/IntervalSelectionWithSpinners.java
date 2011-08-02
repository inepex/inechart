package com.inepex.inechart.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inechart.misc.SpinnerPresenter.View;

public class IntervalSelectionWithSpinners extends Composite {
	
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
			mainPanel.setWidgetPosition(spinner, position, 0);
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
	}
	
	protected AbsolutePanel mainPanel;
	
	protected int spinnableWidth; 
	protected int spinnerWidgetWidth = 20;
	protected int spinnerWidgetHeight = 32;
	
	protected ResizableInterval resizableInterval;
	protected SpinnerPresenter spinnerPresenter1;
	protected SpinnerPresenter spinnerPresenter2;
	
	protected SpinnerView spinnerView1;
	protected SpinnerView spinnerView2;
	
	protected double min, max;
	
	public IntervalSelectionWithSpinners(int width, ResizableInterval resizableInterval) {
		spinnableWidth = width - spinnerWidgetWidth;
		this.resizableInterval = resizableInterval;
		min = resizableInterval.getInitialMin();
		max = resizableInterval.getInitialMax();	
		init();
	}
	
	protected void init(){
		spinnerView1 = new SpinnerView();
		spinnerView2 = new SpinnerView();
		mainPanel = new AbsolutePanel();
		mainPanel.add(spinnerView1.spinner);
		mainPanel.add(spinnerView2.spinner);
		mainPanel.setPixelSize(spinnableWidth + spinnerWidgetWidth, spinnerWidgetHeight);
		initWidget(mainPanel);
		spinnerPresenter1 = new SpinnerPresenter(spinnerView1, new SpinnableImpl(true));
		spinnerPresenter2 = new SpinnerPresenter(spinnerView2, new SpinnableImpl(false));
	}	
}
