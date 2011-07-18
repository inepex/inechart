package com.inepex.inechart.chartwidget.label;


public interface HasTitle {
	public void setName(String name);
	public void setName(StyledLabel name);
	public StyledLabel getName();
	void setDescription(String description);
	public void setDescription(StyledLabel description);
	public StyledLabel getDescription();
}
