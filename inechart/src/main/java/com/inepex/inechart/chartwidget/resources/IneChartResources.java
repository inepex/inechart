package com.inepex.inechart.chartwidget.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface IneChartResources extends ClientBundle {

	@Source("IneChartStyleBundle.css")
	IneChartStyle style();
	
	@Source("ScrollStyle.css")
	ScrollStyle scrollStyle();
	
	@Source("bullet.png")
	ImageResource bullet();
	
	@Source("bullet_active.png")
	ImageResource bullet_active();
}
