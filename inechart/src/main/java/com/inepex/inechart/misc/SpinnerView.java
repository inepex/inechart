package com.inepex.inechart.misc;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inechart.misc.SpinnerPresenter.View;

public class SpinnerView extends Composite implements View {
	
	protected AbsolutePanel mainPanel;
	protected Label spinner;
	protected int spinnerWidth;
	protected int width;
	
	public SpinnerView(int width, int spinnerWidth, int spinnerHeight) {
		this.spinnerWidth = spinnerWidth;
		this.width = width;
		mainPanel = new AbsolutePanel();
		mainPanel.setStyleName(ResourceHelper.getRes().style().spinnerView());
		mainPanel.setPixelSize(width, spinnerHeight);
		spinner = new Label();
		spinner.setStyleName(ResourceHelper.getRes().style().normal());
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
			spinner.setStyleName(ResourceHelper.getRes().style().active());
		}
		else{
			spinner.setStyleName(ResourceHelper.getRes().style().normal());
		}
	}

}
