package com.inepex.inechart.misc.scroll.defaultviews;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.inepex.inechart.chartwidget.resources.ScrollStyle;
import com.inepex.inechart.misc.scroll.SpinnerPresenter.View;

public class SpinnerView extends Composite implements View {
	
	protected AbsolutePanel mainPanel;
	protected Label spinner;
	protected int spinnerWidth;
	protected int width;
	protected ScrollStyle scrollStyle;
	
	SpinnerView(int width, int spinnerWidth, int spinnerHeight, ScrollStyle scrollStyle) {
		this.scrollStyle=scrollStyle;
		this.spinnerWidth = spinnerWidth;
		this.width = width;
		mainPanel = new AbsolutePanel();
		mainPanel.setStyleName(scrollStyle.spinnerView());
		mainPanel.setPixelSize(width, spinnerHeight);
		spinner = new Label();
		spinner.setStyleName(scrollStyle.normal());
		mainPanel.add(spinner);
		initWidget(mainPanel);
	}
	

	@Override
	public IsWidget getSpinner() {
		return spinner;
	}

	@Override
	public int getSpinnableAreaLength() {
		return width - spinnerWidth;
	}

	@Override
	public void setSpinnerPosition(int position) {
		mainPanel.setWidgetPosition(spinner, position, 0);
	}

	@Override
	public void setSpinnerBeingDragged(boolean spinnerBeingDragged) {
		if(spinnerBeingDragged){
			spinner.setStyleName(scrollStyle.active());
		}
		else{
			spinner.setStyleName(scrollStyle.normal());
		}
	}

}
