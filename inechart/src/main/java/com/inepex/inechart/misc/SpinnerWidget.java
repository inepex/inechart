package com.inepex.inechart.misc;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.inepex.inechart.chartwidget.resources.ResourceHelper;

public class SpinnerWidget extends Composite {
	
	final private Button button;
	
	public SpinnerWidget() {
		this(true);
	}
	
	public SpinnerWidget(boolean left) {
		button = new Button(
				"<div style=\"padding-top: 1px;\">.</div> " +
				"<div style=\"margin-top: -10px;\">.</div>" +
				"<div style=\"margin-top: -10px; padding-bottom: 10px;\">.</div>");
		if(left){
			button.setStyleName(ResourceHelper.getRes().spinnerWidgetStyle().MapControlBarLeft());
		}
		else{
			button.setStyleName(ResourceHelper.getRes().spinnerWidgetStyle().MapControlBarRight());
		}
		initWidget(button);
	}
	
	public void setLeft(){
		button.setStyleName(ResourceHelper.getRes().spinnerWidgetStyle().MapControlBarLeft());
	}
	
	public void setRight(){
		button.setStyleName(ResourceHelper.getRes().spinnerWidgetStyle().MapControlBarRight());
	}
	
}

