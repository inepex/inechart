package com.inepex.inechart.chartwidget.misc;

import com.inepex.inechart.chartwidget.properties.Color;

public class ColorSet {

	static final Color[] flotColorSet = { new Color("#edc240"),
			new Color("#afd8f8"), new Color("#cb4b4b"), new Color("#4da74d"),
			new Color("#9440ed"), new Color("#1c91db"), new Color("#dfd212"),
			new Color("#de8238") };
	
	static final Color[] ineColorSet = {
		new Color("#1c91db"),
		new Color("#dfd212"),
		new Color("#de8238"),
		new Color("#e4292d"),
		new Color("#12e8de"),
		new Color("#b6c317"),
		new Color("#f5a032"),
		new Color("#fb3f21"),
		new Color("#0fcbf2"),
		new Color("#d5b914"),
		new Color("#eba626"),
		new Color("#f15b1f")
	};
	
	Color[] colors;
	int actual = 0;

	public ColorSet() {
		this(ineColorSet);
	}

	public ColorSet(Color... colors) {
		if (colors != null && colors.length > 0)
			this.colors = colors;
	}

	public String getNextColorName() {
		return getNextColor().getColor();
	}

	public Color getNextColor() {
		if (actual >= colors.length)
			actual = 0;
		return colors[actual++];
	}
	
	public static ColorSet flotColorSet(){
		return new ColorSet(flotColorSet);
	}
}
