package com.inepex.inechart.misc.scroll.defaultviews;

import com.inepex.inechart.chartwidget.resources.ResourceHelper;
import com.inepex.inechart.chartwidget.resources.ScrollStyle;
import com.inepex.inechart.misc.scroll.ScrollViewFactory;

public class DefaultScrollViewFactory implements ScrollViewFactory{

	private final ScrollStyle scrollStyle;
	
	public DefaultScrollViewFactory() {
		this(ResourceHelper.getRes().scrollStyle());
	}
	
	public DefaultScrollViewFactory(ScrollStyle scrollStyle) {
		this.scrollStyle = scrollStyle;
	}

	@Override
	public ScrollBarView createScrollBarView(boolean horizontal) {
		return new ScrollBarView(horizontal, scrollStyle);
	}

	@Override
	public ScrollBarView createScrollBarView(boolean isHorizontal, int width,
			int height, int padding, boolean hasBorder) {
		return new ScrollBarView(isHorizontal, width, height, padding, hasBorder, scrollStyle);
	}

	@Override
	public SpinnerView createSpinnerView(int width, int spinnerWidth, int spinnerHeight) {
		return new SpinnerView(width, spinnerWidth, spinnerHeight, scrollStyle);
	}

	@Override
	public ScrollStyle getScrollStyle() {
		return scrollStyle;
	}
	
	
}
