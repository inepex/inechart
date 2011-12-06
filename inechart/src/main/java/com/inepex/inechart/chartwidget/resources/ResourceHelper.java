package com.inepex.inechart.chartwidget.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class ResourceHelper {
	
	static IneChartResources icResources = null;
	
	public static String getImageAsString(ImageResource resource) {
		Image img = new Image();
		img.setResource(resource);
		return img.getElement().getString();
	}
	
	public static IneChartResources getRes() {
		if (icResources == null) {
			icResources = GWT.create(IneChartResources.class);
			icResources.style().ensureInjected();
			icResources.scrollStyle().ensureInjected();
		}
		return  icResources;
	}
}
