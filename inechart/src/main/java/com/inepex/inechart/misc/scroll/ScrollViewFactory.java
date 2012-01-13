package com.inepex.inechart.misc.scroll;

import com.inepex.inechart.misc.scroll.defaultviews.ScrollBarView;
import com.inepex.inechart.misc.scroll.defaultviews.SpinnerView;
import com.inepex.inechart.chartwidget.resources.ScrollStyle;

public interface ScrollViewFactory {

	public ScrollBarView createScrollBarView(boolean horizontal);
	public ScrollBarView createScrollBarView(boolean isHorizontal, int width, int height, int padding, boolean hasBorder);
	public SpinnerView createSpinnerView(int width, int spinnerWidth, int spinnerHeight);
	
	public ScrollStyle getScrollStyle();
	
}
