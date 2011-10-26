package com.inepex.inechart.chartwidget;

import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.linechart.PointFilter.Policy;
import com.inepex.inechart.chartwidget.linechart.PointSelectionMode;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;

public class Defaults {
	

	public static final int chartWidth = 470;
	public static final int chartHeight = 380;

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
	public static TextProperties textProperties(){
		return new TextProperties("Verdana, Arial, sans-serif", 13, "normal", "normal", color());
	}
	
	//data
	private static int uniqueEntryNameNo = 0;
	private static final String uniqueEntryName = "Untitled entry #"; 
	public static String uniqueEntryName(){
		return uniqueEntryName + ++uniqueEntryNameNo;
	}
	
	//selection
	public static final ShapeProperties selectionLookout(){
		return new ShapeProperties(new LineProperties(1.2, new Color("#FFFFFF", 0.84)), new Color("#FFFFFF", 0.66));
	}
	public static final int minSelectionSize = 4;
	//chart title
	public static final TextProperties chartTitle_Name = new TextProperties("Arial, sans-serif", 18, "normal", "bold");
	public static final TextProperties chartTitle_Description = new TextProperties("Arial, sans-serif", 12, "normal", "normal", new Color("#8d8d8d"));	
	public static final ShapeProperties chartTitleBackground = new ShapeProperties(new Color("white", 0));
	//axis
	public static final int tickLength = 7;
	public static TextProperties tickTextProperties(){
		return  new TextProperties("Calibri, Verdana, Arial, sans-serif", 12, "normal", "normal", color());
	}
	public static TextContainer tickTextContainer(){
		TextContainer tc = new TextContainer(VerticalPosition.Auto, HorizontalPosition.Auto, new ShapeProperties(new LineProperties(0, new Color("black")), new Color("white", 0.7)));
		tc.setPadding(2);
		return tc;
	}
	public static StyledLabel axisLabel(){
		return new StyledLabel(VerticalPosition.Auto, HorizontalPosition.Auto, new ShapeProperties(new LineProperties(0, new Color("black")), new Color("white", 0.7)), new Text(""));
	}
	//modul2d
	public static LineProperties gridLine(){
		return new LineProperties(1.8, new Color("#E8E8E8", 1),4,4);
	}
	public static LineProperties border(){
		return new LineProperties(1, new Color("#000", 1.0));
	}
	public static int paddingHorizontal = 5;
	public static int paddingVertical = 3;
	//linechart
	public static Shape normalPoint(){
		return new Circle(3.5, new ShapeProperties(new LineProperties(lineWidth),colorWhite()));
	}
	public static Shape selectedPoint(){
		return new Circle(7, new ShapeProperties(new LineProperties(5, new Color("black", 0.6))));
	}
	public static final double fillOpacity = 0.4;
	public static final double lineWidth = 2.1;
	public static final com.inepex.inechart.chartwidget.linechart.PointSelectionMode selectPoint = PointSelectionMode.On_Click;
	public static final int pointMouseOverRadius = 10;
	public static final int verticalFilter = 0;
	public static final int hotizontalFilter = 10;
	public static final Policy filterPolicy = Policy.average;
	
	//shadow
	public static final double shadowOffsetX = 1.2;
	public static final double shadowOffsetY = 2.4;
	public static Color shadowColor(){
		return  new Color("#D8D8D8", 0.74);
	}
	//barchart
	public static final int fixedBarWidth = -1; 
	public static final int barSpacing = 2;
	public static final int maxBarWidth = 18;
	public static final int minBarWidth = 3;
	public static final double barBorderWidth = 0;
	public static final double barFillOpacity = 0.85;
	//piechart
	public static ShapeProperties pie(){
		return new ShapeProperties(new LineProperties(2, color()), new Color(colorString, 0.8));
	}
	//textcontainer
	public static final TextProperties textContainerText = new TextProperties("Arial, sans-serif", 11);
	public static final ShapeProperties textContainerBackground = new ShapeProperties(new LineProperties(0, new Color("gray")), new Color("#ffffff", 0.8));
	public static final int textContainerPadding_H = 4;
	public static final int textContainerPadding_V = 2;
	//legend
	public static final Rectangle legendSymbol = new Rectangle(28, 9,new ShapeProperties(new LineProperties(0), color()));
	//intervalchart
	public static final double yValue = 1.0;
	public static final String name = "DataSet No. ";
	public static final ShapeProperties intervalShapeProperties(){
		return new ShapeProperties(new LineProperties(0, null), null);
	}
	public static final double intervalRoundedCornerRadius = 0;
	
	//event
	public static final long minimumTimeOutBetweenVPEvents = 79;
	
	//crosshair
	public static final LineProperties crosshair(){
		return new LineProperties(2, new Color("red", 0.8));
	}
	
	public static final TextContainer crosshairTextBox(){
		TextContainer tc = new TextContainer();
		tc.setPadding(4);
		tc.setBackground(new ShapeProperties(crosshair(), new Color("white", 0.6)));
		return tc;
	}
	public static final TextProperties crosshairTextProperties(){
		return new TextProperties("Calibri, Verdana, Arial, sans-serif", 12, "normal", "normal", new Color("red"));
	}
}
