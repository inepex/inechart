package com.inepex.inechart.chartwidget.misc;

import com.inepex.inechart.chartwidget.properties.Color;

public class ColorSet {

	static final Color[] defaultColors = { new Color("#edc240"),
			new Color("#afd8f8"), new Color("#cb4b4b"), new Color("#4da74d"),
			new Color("#9440ed"), new Color("#9440ed"), new Color("#9440ed"),
			new Color("#9440ed") };
	Color[] colors;
	int actual = 0;

	public ColorSet() {
		this(defaultColors);
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
}
