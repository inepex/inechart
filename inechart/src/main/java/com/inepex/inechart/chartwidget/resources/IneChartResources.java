package com.inepex.inechart.chartwidget.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface IneChartResources extends ClientBundle {

	@Source("IneChartStyleBundle.css")
	IneChartStyle style();
	
	@Source("ScrollStyle.css")
	ScrollStyle scrollStyle();
	
	@Source("ScrollStyle2.css")
	ScrollStyle scrollStyle2();
	
	@Source("SpinnerWidget.css")
	SpinnerWidgetStyle spinnerWidgetStyle();
	
	@Source("bullet.png")
	ImageResource bullet();
	
	@Source("bullet_active.png")
	ImageResource bullet_active();
	
	@Source("timeline.png")
	ImageResource timeline();
	
	@Source("timeline_hover.png")
	ImageResource timeline_hover();
}
