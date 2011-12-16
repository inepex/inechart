package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;

public class StyledLabel extends TextContainer {

	protected Text text;

	public StyledLabel(String text){
		this(new Text(text, Defaults.textContainerText));
	}
	
	public StyledLabel(Text text) {
		super();
		this.text = text;
	}

	public StyledLabel(VerticalPosition verticalPosition,
			HorizontalPosition horizontalPosition, ShapeProperties background,
			Text text) {
		super(verticalPosition, horizontalPosition, background);
		this.text = text;
	}

	/**
	 * @return the text
	 */
	public Text getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(Text text) {
		this.text = text;
	}
	
	
}
