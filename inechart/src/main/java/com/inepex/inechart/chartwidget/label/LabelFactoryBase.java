package com.inepex.inechart.chartwidget.label;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Text;

public class LabelFactoryBase extends IneChartModul{
	protected class LegendBinding{
		HasLegend hasLegend;
		GraphicalObjectContainer gosPerEntry;
		double[] boundingBox;
		public LegendBinding(HasLegend hasLegend) {
			this.hasLegend = hasLegend;
		}
	}
	
	ArrayList<LegendBinding> legendBindings;
	HasTitle chartTitle;
	/**
	 * legend excluded
	 */
	TreeMap<TextContainer, GraphicalObjectContainer> textContainers;
	
	public LabelFactoryBase(DrawingArea canvas) {
		super(canvas);
		legendBindings = new ArrayList<LabelFactoryBase.LegendBinding>();
		textContainers = new TreeMap<TextContainer, GraphicalObjectContainer>();
	}
	
	public void addHasLegendEntries(HasLegend hasLegendEntries) {
		legendBindings.add(new LegendBinding(hasLegendEntries));
	}
	
	public void setChartTitle(HasTitle chartTitle) {
		this.chartTitle = chartTitle;
		
	}

	@Override
	public void update() {
		graphicalObjectContainer.removeAllGraphicalObject();
		updateLegends();
		updateChartTitle();
	}
	
	protected void updateLegends(){
		for(LegendBinding lb : legendBindings){
			if(lb.hasLegend.showLegend()){
				createLegend(lb);				
				graphicalObjectContainer.addAllGraphicalObject(lb.gosPerEntry);
			}
			else{
				lb.gosPerEntry = null;
				lb.boundingBox = null;
			}
		}
	}
	
	protected void createLegend(LegendBinding legendBinding){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		List<LegendEntry> entries = legendBinding.hasLegend.getLegendEntries();
		Legend legend = legendBinding.hasLegend.getLegend();
		int textHeight = measureLegendTextHeight(legend);
		int symbolHeight = getLegendSymbolHeight(legend);
		if(symbolHeight == 0)
			symbolHeight = textHeight;
		int rowHeight = Math.max(textHeight, symbolHeight);
		
		int actualX = 0, actualY = 0;
		
		for(int i = 0; i < entries.size(); i++){
			LegendEntry e = entries.get(i);
			switch (legend.legendEntryLayout) {
			case AUTO:   //TODO
			case COLUMN: //TODO
			case ROW:
				actualX += legend.paddingBetweenEntries;
				actualX += createLegendEntry(legend, e, goc, actualX, actualY, rowHeight, symbolHeight, textHeight);
				break;
			default:
				break;
			}
		}
		double[] bb = DrawingAreaAssist.getBoundingBox(goc);
		int x = 0, y=0;
		switch (legend.horizontalPosition) {
		case Auto:
		case Right:
			x = (int) (canvas.getWidth() - bb[2]);
			break;
		case Left:
			x = 0;
			break;
		case Middle:
			x = (int) (canvas.getWidth() - bb[2]) / 2;
			break;
		}
		switch (legend.verticalPosition) {
		case Auto:
		case Top:
			y = 0;			
			break;
		case Bottom:
			y = (int) (canvas.getHeight() - bb[3]);
			break;
		case Middle:
			y = (int) (canvas.getHeight() - bb[3]) / 2;
			break;
		}
		goc.moveBasePoints(x, y);
		legendBinding.gosPerEntry = goc;
		legendBinding.boundingBox = new double[]{x,y,bb[2],bb[3]};
	}
	
	protected int createLegendEntry(Legend legend, LegendEntry e, GraphicalObjectContainer goc, int actualX, int actualY, int rowHeight, int symbolHeight, int textHeight){
		Shape s = legend.getLegendSymbol();
		int startX = actualX;
		if(s != null){
			actualY += (rowHeight - symbolHeight) / 2;
			s.getProperties().setFillColor(e.color);
			ArrayList<GraphicalObject> symbolGOs = s.toGraphicalObjects();
			int symbolWidth = 0;
			for(GraphicalObject go : symbolGOs){
				if(go instanceof com.inepex.inegraphics.shared.gobjects.Rectangle){
					go.setBasePointX(actualX);
					go.setBasePointY(actualY);
					if(((com.inepex.inegraphics.shared.gobjects.Rectangle) go).getWidth() > symbolWidth)
						symbolWidth = (int) ((com.inepex.inegraphics.shared.gobjects.Rectangle) go).getWidth();
				}
				else if(go instanceof com.inepex.inegraphics.shared.gobjects.Circle){
					go.setBasePointX(actualX + ((com.inepex.inegraphics.shared.gobjects.Circle)go).getRadius());
					go.setBasePointY(actualY + ((com.inepex.inegraphics.shared.gobjects.Circle)go).getRadius());
					if(((com.inepex.inegraphics.shared.gobjects.Circle) go).getRadius() * 2 > symbolWidth)
						symbolWidth = (int) (((com.inepex.inegraphics.shared.gobjects.Circle) go).getRadius() * 2);
				}
				goc.addGraphicalObject(go);
			}
			actualX += symbolWidth;
			actualY -=(rowHeight - symbolHeight) / 2;
		}
		actualX += legend.getPaddingBetweenTextAndSymbol();
		actualY += (rowHeight - textHeight) / 2;
		Text text = createText(e.getTitle().getName(), actualX, actualY);
		canvas.measureText(text);
		actualX += text.getWidth();
		goc.addGraphicalObject(text);
		return actualX - startX;
	}
	
	protected int getLegendSymbolHeight(Legend legend){
		int legendSymbolHeight;
		Shape s = legend.getLegendSymbol() ;
		if(s instanceof Rectangle){
			legendSymbolHeight = (int) ((Rectangle) s).getHeight();
		}
		else if(s instanceof Circle){
			legendSymbolHeight = (int) (((Circle) s).getRadius() * 2);
		}
		else{
			legendSymbolHeight = 0;
		}
		return legendSymbolHeight;
	}
	
	protected int measureLegendTextHeight(Legend legend){
		TextProperties tp = legend.getTextProperties();
		Text m = new Text("Quick Fox Jumps over the Lazy Dog", 0, 0);
		m.setFontWeight(tp.getFontWeight());
		m.setFontFamily(tp.getFontFamily());
		m.setFontSize(tp.getFontSize());
		m.setFontStyle(tp.getFontStyle());
		canvas.measureText(m);
		return m.getHeight();
	}

	@Override
	public boolean redrawNeeded() {
		return false;
	}
	
	public double[] getPadding(boolean includeTitle){
		double[] padding = new double[]{0,0,0,0};
		for(LegendBinding lb : legendBindings){
			if(lb.hasLegend.showLegend() && lb.hasLegend.getLegend().includeInPadding){
				padding = mergePaddings(padding, getPadding(lb.boundingBox, lb.hasLegend.getLegend().horizontalPosition, lb.hasLegend.getLegend().verticalPosition));
			}
		}
		if(includeTitle){
			HorizontalPosition hp = null;
			 VerticalPosition vp = null;
			GraphicalObjectContainer goc = new GraphicalObjectContainer();
			if(chartTitle.getName() != null){
				goc.addAllGraphicalObject(textContainers.get(chartTitle.getName()));
				hp = chartTitle.getName().horizontalPosition;
				vp = chartTitle.getName().verticalPosition;
			}
			if(chartTitle.getDescription() != null){
				goc.addAllGraphicalObject(textContainers.get(chartTitle.getDescription()));
				if(hp == null){
					hp = chartTitle.getDescription().horizontalPosition;
					vp = chartTitle.getDescription().verticalPosition;
				}
			}
			if(goc.getGraphicalObjects().size() > 0){
				padding = mergePaddings(padding, getPadding(DrawingAreaAssist.getBoundingBox(goc),hp,vp));
			}
		}
		return padding;
	}
	
	protected double[] getPadding(double[] boundingBox, HorizontalPosition hp, VerticalPosition vp){
		double top=0, left=0, right=0, bottom=0;
		switch (hp) {
		case Auto:
		case Right:
			right = canvas.getWidth() - boundingBox[0];
			break;
		case Left:
			left = boundingBox[0];
			break;
		}
		switch (vp) {
		case Auto:
		case Top:
			top = boundingBox[1];			
			break;
		case Bottom:
			bottom = canvas.getHeight() - boundingBox[1];
			break;
		}
		return new double[]{top,right,bottom,left};
	}
	
	private double[] mergePaddings(double[] first, double[] second){
		double[] ret = new double[4];
		for(int i=0;i<4;i++){
			ret[i] = Math.max(first[i], second [i]);
		}
		return ret;
	}
	
	protected Text createText(StyledLabel label, int x, int y){
		Text text = new Text(label.text,x,y);
		text.setFontWeight(label.getTextProperties().getFontWeight());
		text.setFontFamily(label.getTextProperties().getFontFamily());
		text.setFontSize(label.getTextProperties().getFontSize());
		text.setFontStyle(label.getTextProperties().getFontStyle());
		return text;
	}

	protected void updateChartTitle(){
		
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		 HorizontalPosition hp = null;
		 VerticalPosition vp = null;
		 int dy = 0;
		if(chartTitle.getName() != null){
			textContainers.put(chartTitle.getName(), createLabel(chartTitle.getName()));
			goc.addAllGraphicalObject(textContainers.get(chartTitle.getName()));
			double[] bb = DrawingAreaAssist.getBoundingBox(goc);
			dy = (int) bb[3];
			hp = chartTitle.getName().horizontalPosition;
			vp = chartTitle.getName().verticalPosition;
		}
		if(chartTitle.getDescription() != null){
			textContainers.put(chartTitle.getDescription(), createLabel(chartTitle.getDescription()));
			goc.addAllGraphicalObject(textContainers.get(chartTitle.getDescription()));
			if(hp == null){
				hp = chartTitle.getDescription().horizontalPosition;
				vp = chartTitle.getDescription().verticalPosition;
			}
			else{
				textContainers.get(chartTitle.getDescription()).moveBasePoints(0, dy);
			}
		}
		if(goc.getGraphicalObjects().size() > 0){
			align(goc, hp, vp);
			graphicalObjectContainer.addAllGraphicalObject(goc);
		}
		
	}
	
	protected GraphicalObjectContainer createLabel(StyledLabel label){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		Text text = createText(label, 0, 0);
		canvas.measureText(text);
		double[] bb = DrawingAreaAssist.getBoundingBox(text);
		goc.addGraphicalObject(text);
		Rectangle bg = new Rectangle(
				bb[2] + label.leftPadding + label.rightPadding, 
				bb[3] + label.bottomPadding + label.topPadding,
				label.backgroundRoundedCornerRadius,
				label.background);
		
		for(GraphicalObject go : bg.toGraphicalObjects()){
			go.setBasePointX(bb[0] - label.leftPadding);
			go.setBasePointY(bb[1] - label.rightPadding);
			goc.addGraphicalObject(go);
		}
		return goc;
	}
	
	protected void align(GraphicalObjectContainer gocToAlign, HorizontalPosition hp, VerticalPosition vp){
		double[] bb = DrawingAreaAssist.getBoundingBox(gocToAlign);
		int x = 0, y = 0;
		switch (hp) {
		case Auto:
		case Right:
			x = (int) (canvas.getWidth() - bb[2]);
			break;
		case Left:
			x = 0;
			break;
		case Middle:
			x = (int) (canvas.getWidth() - bb[2]) / 2;
			break;
		}
		switch (vp) {
		case Auto:
		case Top:
			y = 0;			
			break;
		case Bottom:
			y = (int) (canvas.getHeight() - bb[3]);
			break;
		case Middle:
			y = (int) (canvas.getHeight() - bb[3]) / 2;
			break;
		}
		gocToAlign.moveBasePoints(x - bb[0], y - bb[1]);
	}
}
