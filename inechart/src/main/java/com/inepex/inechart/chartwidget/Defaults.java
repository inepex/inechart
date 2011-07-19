package com.inepex.inechart.chartwidget;

import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;

public class Defaults {

	//color
	public static final double alpha = 1.0;
	public static final String colorString = "#000000";
	public static Color color(){
		return new Color();
	}
	public static Color colorWhite(){
		return new Color("#FFFFFF");
	}
	
	//lineproperty
	public static final double dashStrokeLength = 3.5;
	public static final double dashDistance = 3.5;
	public static LineProperties dashedLine(){
		return new LineProperties(1, color(), dashStrokeLength, dashDistance);
	}
	public static LineProperties solidLine(){
		return new LineProperties(1, color());
	}
	public static ShapeProperties shapeProperties(){
		return  new ShapeProperties(solidLine(), colorWhite());
	}
	
	//textproperty
	public static TextProperties getTextProperties(){
		return new TextProperties("Verdana, Arial, sans-serif", 13, "normal", "normal", color());
	}
	
	//designed
	public static final TextProperties chartTitle_Name = new TextProperties("Arial, sans-serif", 18, "normal", "bold");
	public static final TextProperties chartTitle_Description = new TextProperties("Arial, sans-serif", 12, "normal", "normal", new Color("#8d8d8d"));	
	public static final ShapeProperties chartTitleBackground = new ShapeProperties(new Color("white", 0));
	public static TextProperties tickTextProperties(){
		return  new TextProperties("Calibri, Verdana, Arial, sans-serif", 12, "normal", "normal", color());
	}
	public static final ShapeProperties selectionLookout = new ShapeProperties(new LineProperties(1.2, new Color("#FFFF66", 0.84)), new Color("#FFFF66", 0.66));
	public static LineProperties gridLine(){
		return new LineProperties(1.8, new Color("#E8E8E8", 1),4,4);
	}
	public static LineProperties border(){
		return new LineProperties(1, new Color("#000", 1.0));
	}

}
