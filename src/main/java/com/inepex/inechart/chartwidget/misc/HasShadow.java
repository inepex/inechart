package com.inepex.inechart.chartwidget.misc;

import com.inepex.inechart.chartwidget.properties.Color;

public interface HasShadow {
	void setShadowOffsetX(double offsetX);
	void setShadowOffsetY(double offsetY);
	double getShadowOffsetX();
	double getShadowOffsetY();
	Color getShadowColor();
	void setShadowColor(Color shadowColor);
	
	
}
