package com.inepex.inechart.chartwidget.label;

import com.inepex.inechart.chartwidget.Defaults;

public class ChartTitle extends TextContainer{
	
	Text title;
	Text description;
	
	public ChartTitle(String title){
		this(title, null);
	}
	
	public ChartTitle(String title, String description){
		this(title == null ? null : new Text(title, Defaults.chartTitle_Name), description == null ? null : new Text(description, Defaults.chartTitle_Description));
	}
	
	public ChartTitle(Text title, Text description) {
		super();
		this.title = title;
		this.description = description;
		includeInPadding = true;
	}
	/**
	 * @return the title
	 */
	public Text getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(Text title) {
		this.title = title;
	}
	/**
	 * @return the description
	 */
	public Text getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(Text description) {
		this.description = description;
	}
	
	
	
}
