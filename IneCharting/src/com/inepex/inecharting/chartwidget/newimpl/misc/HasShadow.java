package com.inepex.inecharting.chartwidget.newimpl.misc;

import com.inepex.inecharting.chartwidget.newimpl.properties.Color;

public interface HasShadow {
	void setShadowOffsetX(double offsetX);
	void setShadowOffsetY(double offsetY);
	double getShadowOffsetX();
	double getShadowOffsetY();
	Color getShadowColor();
	void setShadowColor(Color shadowColor);
	
	
}
