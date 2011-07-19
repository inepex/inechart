package com.inepex.inechart.chartwidget.label;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.label.Legend.LegendEntryLayout;
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

/**
 * Base class for displaying texts in the chart.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class LabelFactoryBase extends IneChartModul{
	/**
	 * a simple helper class for binding {@link HasLegend} entity and {@link GraphicalObject}s
	 */
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
	double[] chartTitleBoundingBox;
	/**
	 * legends excluded from this container
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
		updateChartTitle(false);
		updateLegends(false);
		for(TextContainer tc : textContainers.keySet()){
			graphicalObjectContainer.addAllGraphicalObject(textContainers.get(tc));
		}
	}
	
	public void forcedUpdate(){
		graphicalObjectContainer.removeAllGraphicalObject();
		updateLegends(true);
		updateChartTitle(true);
		for(TextContainer tc : textContainers.keySet()){
			graphicalObjectContainer.addAllGraphicalObject(textContainers.get(tc));
		}
	}
	
	protected void updateLegends(boolean forced){
		for(LegendBinding lb : legendBindings){
			if(lb.hasLegend.showLegend()){
				if(forced || lb.gosPerEntry == null){
					createLegend(lb);			
				}
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
		int rowHeight = Math.max(textHeight, symbolHeight) + legend.paddingBetweenEntries / 2;
		//first create elements, then position them
		int actualX = 0, actualY = 0;
		StyledLabel title = null;
		if(chartTitle != null){
			title = chartTitle.getName();
			if(title == null){
				title = chartTitle.getDescription();
			}
		}
		if(legend.legendEntryLayout == LegendEntryLayout.AUTO && title != null && title.verticalPosition == legend.verticalPosition && legend.maxWidth <= 0){
			legend.maxWidth = (int) (canvas.getWidth() - chartTitleBoundingBox[2]);
		}
		switch (legend.legendEntryLayout) {
		case AUTO:
			int max = legend.maxHeight;
			if(max > 0){
				//columns first
				int maxWidthInColumn = 0;
				for(LegendEntry e : entries){
					int w = createLegendEntry(legend, e, goc, actualX, actualY, rowHeight, symbolHeight, textHeight);
					w += legend.paddingBetweenEntries;
					if(w > maxWidthInColumn){
						maxWidthInColumn = w;
					}
					actualY += rowHeight;
					if(actualY + rowHeight > max){
						actualY = 0;
						actualX += maxWidthInColumn;
						maxWidthInColumn = 0;
					}
				}
			}
			else{
				max = legend.maxWidth;
				if(max < 0){
					max = canvas.getWidth();
				}
				//rows first
				GraphicalObjectContainer entryGoc = new GraphicalObjectContainer();
				for(LegendEntry e : entries){
					int w = createLegendEntry(legend, e, entryGoc, actualX, actualY, rowHeight, symbolHeight, textHeight);
					w += legend.paddingBetweenEntries;
					if(actualX + w > max && actualX != 0){
						entryGoc.moveBasePoints(-actualX, rowHeight);
						actualX = w;
						actualY += rowHeight;
					}
					else{
						actualX += w;
					}
					goc.addAllGraphicalObject(entryGoc);
					entryGoc.removeAllGraphicalObject();
				}
			}
			break;
		case COLUMN:
			for(LegendEntry e : entries){
				if(actualY > canvas.getHeight()){
					break;
				}
				actualY += legend.paddingBetweenEntries;
				createLegendEntry(legend, e, goc, actualX, actualY, rowHeight, symbolHeight, textHeight);
				actualY += rowHeight;
			}
			break;
		case ROW:
			for(LegendEntry e : entries){
				if(actualX > canvas.getWidth()){
					break;
				}
				actualX += legend.paddingBetweenEntries;
				actualX += createLegendEntry(legend, e, goc, actualX, actualY, rowHeight, symbolHeight, textHeight);
			}
			break;
		}
		
		double[] bb = DrawingAreaAssist.getBoundingBox(goc);
		int x = legend.fixedX, y = legend.fixedY;
		if(x < 0){
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
		}
		if(y < 0){
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
		}
		goc.moveBasePoints(x - bb[0], y - bb[1]);
		legendBinding.gosPerEntry = goc;
		bb = legendBinding.boundingBox = new double[]{x,y,bb[2],bb[3]};
		//align to title
		if(title == null || legend.fixedX != -1 && legend.fixedY != -1){
			return;
		}
		
		if(isOverlap(chartTitleBoundingBox, bb)){
			//try to float legend to right
			double dx = 0, dy = 0;
			if(chartTitleBoundingBox[0] + chartTitleBoundingBox[2] + bb[2] <= canvas.getWidth()){
				dx = chartTitleBoundingBox[0] + chartTitleBoundingBox[2] - bb[0];
			}
			//to left
			else if(bb[2] < chartTitleBoundingBox[0]){
				dx = chartTitleBoundingBox[0] - bb[0] - bb[2];
			}
			//to bottom
			else if(chartTitleBoundingBox[1] + chartTitleBoundingBox[3] + bb[3] <= canvas.getHeight()){
				dy = chartTitleBoundingBox[1] + chartTitleBoundingBox[3] - bb[1];
			}
			//to top
			else if(bb[3] < chartTitleBoundingBox[1]){
				dy = chartTitleBoundingBox[1] - bb[1] - bb[3];
			}
			goc.moveBasePoints(dx, dy);
			legendBinding.boundingBox[0] += dx;
			legendBinding.boundingBox[1] += dy;
		}
	}
	
	protected boolean isOverlap(double[] bb1, double[] bb2){
		if(bb1[0] + bb1[2] > bb2[0] && bb1[0] < bb2[0] + bb2[2] &&
			bb1[1] + bb1[3] > bb2[1] && bb1[1] < bb2[1] + bb2[3]){
			return true;
		}
		return false;
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
	
	/**
	 * Returns the padding needed for Legends (whose includeInPadding is set true)
	 * and for the chart's title.
	 * 
	 * @param includeTitle should chart's title be included in padding?
	 * @return [top, right, bottom, left] paddings
	 */
	public double[] getPadding(boolean includeTitle){
		double[] padding = new double[]{0,0,0,0};
		if(includeTitle && chartTitle != null){
			padding = mergePaddings(padding, getPaddingFromBoundingBox(chartTitleBoundingBox));
		}
		for(LegendBinding lb : legendBindings){
			if(lb.hasLegend.showLegend() && lb.hasLegend.getLegend().includeInPadding){
				padding = mergePaddings(padding, getPaddingFromBoundingBox(lb.boundingBox));
			}
		}		
		return padding;
	}
	
	protected double[] getPaddingFromBoundingBox(double[] boundingBox){
		double top, left, right, bottom;
		double space, maxSpace;
		//rectangle above boundingBox
		maxSpace = canvas.getWidth() * boundingBox[1];
		top = 0;
		left = 0;
		right = 0;
		bottom = canvas.getHeight() - boundingBox[1];
		//right to bb
		space = canvas.getHeight() * (canvas.getWidth() - boundingBox[0] - boundingBox[2]);
		if(space > maxSpace){
			maxSpace = space;
			top = 0;
			left = canvas.getWidth() - boundingBox[0] - boundingBox[2];
			right = 0;
			bottom = 0;
		}
		// bottom
		space = (canvas.getHeight() - boundingBox[1] - boundingBox[3]) * canvas.getWidth();
		if(space > maxSpace){
			maxSpace = space;
			top =  boundingBox[1] + boundingBox[3];
			left = 0;
			right = 0;
			bottom = 0;
		}
		//left
		space = boundingBox[0] * canvas.getHeight();
		if(space > maxSpace){
			maxSpace = space;
			top = 0;
			left = 0;
			right = canvas.getWidth() - boundingBox[0];
			bottom = 0;
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
		text.setColor(label.getTextProperties().getColor().getColor());
		return text;
	}

	protected void updateChartTitle(boolean forced){
		if(chartTitle == null ||
			(chartTitle.getName() == null && chartTitle.getDescription() == null) ||
			(!forced && chartTitle.getName() != null && textContainers.get(chartTitle.getName()) != null) ||
			(!forced && chartTitle.getDescription() != null && textContainers.get(chartTitle.getDescription()) != null) )
			return;
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
			chartTitleBoundingBox = DrawingAreaAssist.getBoundingBox(goc);
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
		case Left:
			x = 0;
			break;
		case Right:
			x = (int) (canvas.getWidth() - bb[2]);
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

	protected void floatOverlappingGOCs(GraphicalObjectContainer fix, GraphicalObjectContainer toFloat){
		
	}
}
