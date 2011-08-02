package com.inepex.inechart.chartwidget.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface IneChartResources extends ClientBundle {

	@Source("IneChartStyleBundle.css")
	IneChartStyle style();
	
	@Source("bullet.png")
//	@ImageOptions(repeatStyle=RepeatStyle.None)
	ImageResource bullet();
	
	@Source("bullet_active.png")
//	@ImageOptions(repeatStyle=RepeatStyle.None)
	ImageResource bullet_active();
}
