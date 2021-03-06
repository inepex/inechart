package com.inepex.inechart.chartwidget.barchart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.DataSet;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.misc.HasShadow;
import com.inepex.inechart.chartwidget.misc.HasZIndex;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;

/**
 * 
 * Simple Bar Chart module.
 * 
 * @author Miklós Süveges, Tibor Somodi / Inepex Ltd.
 *
 */
public class BarChart extends IneChartModule2D implements HasShadow, HasZIndex {
	public enum BarChartType {
		Stacked, Simple, Sorted;
	}

	/**
	 * The alignment of the barsequences (one or more bars (y values) at a x)
	 * relative to their x	 *
	 */
	public enum BarSequencePosition {
		After, Before, Over
	}
	
	protected BarChartType barChartType;
	protected BarSequencePosition barSequencePosition;
	/**
	 * is it a (x,y)[] or a y[] chart.
	 */
	protected boolean hasXValues;
	/**
	 * if -1 the width of the bars will be auto-calculated,
	 * else they displayed with that fixed size in px
	 */
	protected int fixedBarWidth;
	/**
	 * max width of the bars in px
	 */
	protected int maxBarWidth;
	/**
	 * min width of the bars in px
	 */
	protected int minBarWidth;
	/**
	 * spacing between bars within a barsequence
	 */
	protected int barSpacing;
	/**
	 * All y will be relative to baseY
	 * (One side of the squares representing the chart)
	 */
	protected double baseY;
	protected boolean divideSameValueSortedBars;
	
	//shadow
	protected Color shadowColor;
	protected double shadowOffsetX = 0, shadowOffsetY = 0;
	/**
	 * all bars share the same zIndex
	 */
	protected int zIndex;
	protected ColorSet colorSet;
	
	/**
	 * all bar data stored in this container all of the x values contained
	 * within the given dataSets if a dataSet does not contain a mapping(y) for
	 * the given x, a 0 y value will be mapped to it. The order of y values
	 * contained by the ArrayList is their adding time (first added dataset ->
	 * ArrayList.get(0) )
	 * updated at update() call
	 */
	protected TreeMap<Double, ArrayList<Double>> normalizedData;
	protected ArrayList<DataSet> dataSets;
	
	protected ArrayList<ShapeProperties> lookOut;

	public BarChart(ModuleAssist moduleAssist) {
		super(moduleAssist);
		dataSets = new ArrayList<DataSet>();
		normalizedData = new TreeMap<Double, ArrayList<Double>>();
		lookOut = new ArrayList<ShapeProperties>();

		// defaults
		this.barChartType = BarChartType.Simple;
		this.barSequencePosition = BarSequencePosition.Over;
		this.hasXValues = true;
		autoScaleViewportHorizontal = true;
		
		this.baseY = 0;
		this.barSpacing = Defaults.barSpacing;
		this.maxBarWidth = Defaults.maxBarWidth;
		this.minBarWidth = Defaults.minBarWidth;
		this.fixedBarWidth = Defaults.fixedBarWidth;
		this.divideSameValueSortedBars = true;
	}

	protected void updateLookOut() {
		for (int i=0; i < dataSets.size(); i++) {
			if(lookOut.get(i) == null){
				LineProperties lp = new LineProperties(Defaults.barBorderWidth, new Color(dataSets.get(i).getColor().getColor(),1));
				ShapeProperties sp = new ShapeProperties(lp, new Color(lp.getLineColor().getColor(), Defaults.barFillOpacity));
				lookOut.set(i,sp);
			}
		}
	}

	public void addDataSet(DataSet dataSet){
		addDataSet(dataSet, null);
	}
	
	public void addDataSet(DataSet dataSet, ShapeProperties lookout){
		if(dataSet == null)
			return;
		dataSet.update();
		dataSets.add(dataSet);
		lookOut.add(lookout);
	}
	
	public void removeDataSet(DataSet dataSet){
		int i = dataSets.indexOf(dataSet);
		if(i == -1)
			return;
		dataSets.remove(i);
		lookOut.remove(i);
	}

	protected void normalizeData(){
		normalizedData.clear();
		for(DataSet dataSet : dataSets){
			Map<Double, Double> map = dataSet.getDataMap();
			for(double x : map.keySet()){
				//no mapping for that x we fill up with 0 values
				if(!normalizedData.containsKey(x)){
					ArrayList<Double> yValues = new ArrayList<Double>();
					for(int i=0; i < dataSets.indexOf(dataSet); i++){
						yValues.add(0d);
					}
					normalizedData.put(x, yValues);
				}
				//add actual datasets y
				normalizedData.get(x).add(map.get(x));
			}
			//fill up normalized values with zeros, where this dataset does not contain x 
			for(double otherX : normalizedData.keySet()){
				if(!map.containsKey(otherX)){
					normalizedData.get(otherX).add(0d);
				}
			}
		}
	}
	
	public void scaleViewportToFitAllBars(){
		Iterator<Double> keyIt = normalizedData.keySet().iterator();
		ArrayList<Double> keys = new ArrayList<Double>();
		double xMin = keyIt.next();
		keys.add(xMin);
		double xMax = xMin;
		while (keyIt.hasNext()) {
			xMax = keyIt.next();
			keys.add(xMax);
		}
		double yMin = Double.MAX_VALUE, yMax = -Double.MAX_VALUE;
		double maxSumY = -Double.MAX_VALUE;
		for (double x : normalizedData.keySet()) {
			double sumY = 0;
			for (double y : normalizedData.get(x)) {
				if (y > yMax)
					yMax = y;
				if (y < yMin)
					yMin = y;
				if(y >= 0)
					sumY +=y;			
			}
			if(sumY > maxSumY)
				maxSumY = sumY;
		}
		if (xMax == xMin) {// one element
			xMin--;
			xMax++;
		}
		else{
			switch (barSequencePosition) {
			case After:
				xMax += xMax - keys.get(keys.size()-2);
				break;
			case Before:
				xMin -= keys.get(1) - xMin;
				break;
			case Over:
				xMax += (xMax - keys.get(keys.size()-2))/2;
				xMin -= (keys.get(1) - xMin)/2;
				break;
			}
		}
		if (yMin > baseY && yMax > baseY)
			yMin = baseY;
		if (yMin < baseY && yMax < baseY)
			yMax = baseY;
		if (barChartType == BarChartType.Stacked)
			yMax = maxSumY;
		xAxis.setMin(xMin);
		xAxis.setMax(xMax);
		yAxis.setMin(yMin);
		yAxis.setMax(yMax);
	}

	@Override
	public void update() {
		if (dataSets.size() == 0)
			return;
		
		normalizeData();
		updateLookOut();

		graphicalObjectContainer.removeAllGraphicalObjects();
		ArrayList<Double> xes = new ArrayList<Double>();
		for (double x : normalizedData.keySet()) {
			xes.add(x);
		}
		if(xes.size() == 1){
			switch (barChartType) {
			case Simple:
				graphicalObjectContainer.addAllGraphicalObject(
						createNormalBarSequence(xAxis.getMin(), xAxis.getMax(), normalizedData.get(xes.get(0))));
				break;
			case Sorted:
				graphicalObjectContainer.addAllGraphicalObject(
						createSortedBarSequence(xAxis.getMin(), xAxis.getMax(), normalizedData.get(xes.get(0))));
				break;
			case Stacked:
				graphicalObjectContainer.addAllGraphicalObject(
						createStackedBarSequence(xAxis.getMin(), xAxis.getMax(), normalizedData.get(xes.get(0))));
				break;
			}
		}
		else{
			int i=0;
			for(double x:xes){
				double to = 0,from = 0;
				switch (barSequencePosition) {
				case After:
					from = x;
					if(i == xes.size()-1){
						to = x + (x - xes.get(i-1));
					}
					else{
						to = xes.get(i+1);
					}
					break;
				case Over:
					if(i == xes.size()-1){
						to = x + (x - xes.get(i-1))/2;
					}
					else{
						to = x + (xes.get(i+1) - x)/2;
					}
					if(i == 0){
						from = x - (xes.get(1) - x)/2;
					}
					else{
						from = x - (x - xes.get(i-1))/2;
					}
					break;
				case Before:
					to = x;
					if(i == 0){
						from = x - (xes.get(1) - x);
					}
					else{
						from = xes.get(i-1);
					}
					break;
				}
				switch (barChartType) {
				case Simple:
					graphicalObjectContainer.addAllGraphicalObject(createNormalBarSequence(from, to, normalizedData.get(x)));
					break;
				case Stacked:
					graphicalObjectContainer.addAllGraphicalObject(createStackedBarSequence(from, to, normalizedData.get(x)));
					break;
				case Sorted:
					graphicalObjectContainer.addAllGraphicalObject(createSortedBarSequence(from, to, normalizedData.get(x)));
					break;
				}
				i++;
			}
			
			graphicalObjectContainer = DrawingAreaAssist.clipRectanglesWithRectangle(graphicalObjectContainer, leftPadding, topPadding, getWidth(), getHeight());
		}
		super.update();
	}

	protected GraphicalObjectContainer createSortedBarSequence(double fromX,
			double toX, ArrayList<Double> yValues) {
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		double sequenceWidth = getWidthOnCanvas(fromX, toX);
		int barWidth;
		if(fixedBarWidth <= 0){
			barWidth = (int) (sequenceWidth - 2 * barSpacing);
			if (barWidth < minBarWidth){
				barWidth = minBarWidth;
			}
			else if (barWidth > maxBarWidth){
				barWidth = maxBarWidth;
			}
		}
		else{
			barWidth = fixedBarWidth;
		}
		Double leftTopX = null, leftTopY = null, width = null, height = null;
		double distanceFromSequenceStart = sequenceWidth / 2 - (barWidth + 2 * barSpacing) / 2;
		
		switch (xAxis.getAxisDirection()) {
		case Horizontal_Ascending_To_Left:
			leftTopX = getCanvasX(toX) + distanceFromSequenceStart + barSpacing;
			width = (double) barWidth;
			break;
		case Horizontal_Ascending_To_Right:
			leftTopX = getCanvasX(fromX) + distanceFromSequenceStart + barSpacing;
			width = (double) barWidth;
			break;
		case Vertical_Ascending_To_Bottom:
			leftTopY = getCanvasY(fromX) + distanceFromSequenceStart + barSpacing;
			height = (double) barWidth;
			break;
		case Vertical_Ascending_To_Top:
			leftTopY = getCanvasY(toX) + distanceFromSequenceStart + barSpacing;
			height = (double) barWidth;
			break;
		}
		ArrayList<Double> upperValues = new ArrayList<Double>();
		ArrayList<Double> lowerValues = new ArrayList<Double>();
		for(double y : yValues){
			if(y >= baseY)
				upperValues.add(y);
			else
				lowerValues.add(y);
		}
		Collections.sort(upperValues);
		Collections.sort(lowerValues);
		Collections.reverse(lowerValues);
		double last;
		if (xAxis.isHorizontal()) {
			last = getCanvasY(baseY);
		} else {
			last = getCanvasX(baseY);
		}
		//positive stack
		for(int i = 0; i<upperValues.size();i++){
			double y = upperValues.get(i);
			//check if the next element(s) has same value
			int sameValueCount = 1;
			while(upperValues.size() > i + 1 && upperValues.get(i+1) == y){
				sameValueCount ++;
				i++;
			}
			if(!divideSameValueSortedBars)
				sameValueCount = 1;
			if (xAxis.isHorizontal()) {
				leftTopY = getCanvasY(y);
				height = Math.abs(leftTopY - last);
				
				for(int j = 0;j<sameValueCount;j++){
					int index = 0, k=0;
					for(double actY : yValues){
						if(actY == y){
							if(k == j){
								break;
							}
							k++;
						}
						index++;
					}
					ShapeProperties sp = lookOut.get(index);
					Rectangle r = new Rectangle(width/sameValueCount, height, leftTopX + j*(width/sameValueCount), leftTopY, sp);
					setShadowForShape(r);
					goc.addAllGraphicalObject(r.toGraphicalObjects());
				}
				last = leftTopY;
			} else {
				leftTopX = getCanvasX(y);
				width = Math.abs(leftTopX - last);
				
				for(int j = 0;j<sameValueCount;j++){
					int index = 0, k=0;
					for(double actY : yValues){
						if(actY == y){
							if(k == j){
								break;
							}
							k++;
						}
						index++;
					}
					ShapeProperties sp = lookOut.get(index);
					Rectangle r = new Rectangle(width, height/sameValueCount, leftTopX , leftTopY+ j*(height/sameValueCount), sp);
					setShadowForShape(r);
					goc.addAllGraphicalObject(r.toGraphicalObjects());
				}
				last = leftTopX;
			}
		}
		if (xAxis.isHorizontal()) {
			last = getCanvasY(baseY);
		} else {
			last = getCanvasX(baseY);
		}
		//negative stack
		for(int i = 0; i<lowerValues.size();i++){
			double y = lowerValues.get(i);
			//check if the next element(s) has same value
			int sameValueCount = 1;
			while(lowerValues.size() > i + 1 && lowerValues.get(i+1) == y){
				sameValueCount ++;
				i++;
			}
			if(!divideSameValueSortedBars)
				sameValueCount = 1;
			if (xAxis.isHorizontal()) {
				leftTopY = last;
				height = Math.abs(getCanvasY(y) - leftTopY);
				
				for(int j = 0;j<sameValueCount;j++){
					int index = 0, k=0;
					for(double actY : yValues){
						if(actY == y){
							if(k == j){
								break;
							}
							k++;
						}
						index++;
					}
					ShapeProperties sp = lookOut.get(index);
					Rectangle r = new Rectangle(width/sameValueCount, height, leftTopX + j*(width/sameValueCount), leftTopY, sp);
					setShadowForShape(r);
					goc.addAllGraphicalObject(r.toGraphicalObjects());
				}
				last = leftTopY + height;
			} else {
				leftTopX = last;
				width =  Math.abs(getCanvasX(y) - leftTopX);
				
				for(int j = 0;j<sameValueCount;j++){
					int index = 0, k=0;
					for(double actY : yValues){
						if(actY == y){
							if(k == j){
								break;
							}
							k++;
						}
						index++;
					}
					ShapeProperties sp = lookOut.get(index);
					Rectangle r = new Rectangle(width, height/sameValueCount, leftTopX , leftTopY+ j*(height/sameValueCount), sp);
					setShadowForShape(r);
					goc.addAllGraphicalObject(r.toGraphicalObjects());
				}
				last = leftTopX + width;
			}
		}
		return goc;
	}
	
	protected GraphicalObjectContainer createStackedBarSequence(double fromX,
			double toX, ArrayList<Double> yValues) {
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		double sequenceWidth = getWidthOnCanvas(fromX, toX);
		int barWidth;
		if(fixedBarWidth <= 0){
			barWidth = (int) (sequenceWidth - 2 * barSpacing);
			if (barWidth < minBarWidth){
				barWidth = minBarWidth;
			}
			else if (barWidth > maxBarWidth){
				barWidth = maxBarWidth;
			}
		}
		else{
			barWidth = fixedBarWidth;
		}
		Double leftTopX = null, leftTopY = null, width = null, height = null;
		double distanceFromSequenceStart = sequenceWidth / 2 - (barWidth + 2 * barSpacing) / 2;
		switch (xAxis.getAxisDirection()) {
		case Horizontal_Ascending_To_Left:
			leftTopX = getCanvasX(toX) + distanceFromSequenceStart + barSpacing;
			width = (double) barWidth;
			break;
		case Horizontal_Ascending_To_Right:
			leftTopX = getCanvasX(fromX)+ distanceFromSequenceStart + barSpacing;
			width = (double) barWidth;
			break;
		case Vertical_Ascending_To_Bottom:
			leftTopY = getCanvasY(fromX)+ distanceFromSequenceStart + barSpacing;
			height = (double) barWidth;
			break;
		case Vertical_Ascending_To_Top:
			leftTopY = getCanvasY(toX) + distanceFromSequenceStart + barSpacing;
			height = (double) barWidth;
			break;
		}
		double last;
		boolean finished = false;
		if (xAxis.isHorizontal()) {
			last = getCanvasY(baseY);
			if( (last < topPadding && yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) ||
				(last > getHeight() - bottomPadding && yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) ){
				finished = true;
			}
		} else {
			last = getCanvasX(baseY);
			if( (last > canvas.getWidth() - rightPadding && xAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) ||
				(last < leftPadding && xAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) ){
				finished = true;
			}
		}
		double lastY = baseY;
		int barSpacing = 0;
		for(int i = 0; i<yValues.size() && !finished;i++){
			double y = yValues.get(i);
			//we do not display negative values
			if(y < 0)
				continue;
			ShapeProperties sp = lookOut.get(i);
			if (xAxis.isHorizontal()) {
				leftTopY = getCanvasY(y + lastY);
				if( leftTopY < topPadding && yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top){
					finished = true;
				}
				height = Math.abs(leftTopY - last);
				if( leftTopY + height > canvas.getHeight() - bottomPadding && yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom){
					finished = true;
				}
				last = leftTopY ;
				if(yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom)
					last += height + barSpacing;
				else
					last -= barSpacing;
			} else {
				leftTopX = getCanvasX(y + lastY);
				if(leftTopX > canvas.getWidth()-rightPadding && xAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right){
					finished = true;
				}
				width = Math.abs(leftTopX - last);
				if(leftTopX < leftPadding && xAxis.getAxisDirection() ==  AxisDirection.Horizontal_Ascending_To_Left){
					finished = true;
				}
				last = leftTopX ;
				if(xAxis.getAxisDirection() ==  AxisDirection.Horizontal_Ascending_To_Right)
					last += width + barSpacing;
				else
					last -= barSpacing;
			}
			Rectangle r = new Rectangle(width, height, leftTopX, leftTopY, sp);
			setShadowForShape(r);
			goc.addAllGraphicalObject(r.toGraphicalObjects());
			lastY += y;
		}
		return goc;
	}

	protected GraphicalObjectContainer createNormalBarSequence(double fromX,
			double toX, ArrayList<Double> yValues) {
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		double sequenceWidth = getWidthOnCanvas(fromX, toX);
		ArrayList<Double> ySequence = new ArrayList<Double>();
		boolean reversedSeq = false;
		if (xAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right
				|| xAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			ySequence = yValues;
		} else {
			// reverse
			for (int i = yValues.size() - 1; i >= 0; i--) {
				ySequence.add(yValues.get(i));
			}
			reversedSeq = true;
		}
		// width of one bar (relative to axis)
		int barWidth;
		if(fixedBarWidth <= 0){
			barWidth = (int) ((sequenceWidth - (ySequence.size() + 1) * barSpacing) / ySequence.size());
			if (barWidth < minBarWidth){
				barWidth = minBarWidth;
			}
			else if (barWidth > maxBarWidth){
				barWidth = maxBarWidth;
			}
		}
		else{
			barWidth = fixedBarWidth;
		}
		double distanceBetweenBarStart;
		Double leftTopX = null, leftTopY = null, width = null, height = null;
		//the bars will overlap
		if((ySequence.size()+1)*barSpacing + ySequence.size()*barWidth > sequenceWidth){
			distanceBetweenBarStart = (sequenceWidth - 2*ySequence.size()*barSpacing)/(ySequence.size()+1);
			switch (xAxis.getAxisDirection()) {
			case Horizontal_Ascending_To_Left:
				leftTopX = getCanvasX(toX) - barSpacing;
				width = (double) barWidth;
				break;
			case Horizontal_Ascending_To_Right:
				leftTopX = getCanvasX(fromX) + barSpacing;
				width = (double) barWidth;
				break;
			case Vertical_Ascending_To_Bottom:
				leftTopY = getCanvasY(fromX)+ barSpacing;
				height = (double) barWidth;
				break;
			case Vertical_Ascending_To_Top:
				leftTopY = getCanvasY(toX) - barSpacing;
				height = (double) barWidth;
				break;
			}
		}
		//align to mid
		else{
			distanceBetweenBarStart = barSpacing + barWidth;
			double realSequenceWidth = ySequence.size() * barWidth + (ySequence.size()+1) * barSpacing;
			double distanceFromSequenceStart = sequenceWidth / 2 - realSequenceWidth / 2;
			switch (xAxis.getAxisDirection()) {
			case Horizontal_Ascending_To_Left:
				leftTopX = getCanvasX(toX) - distanceFromSequenceStart - barSpacing;
				width = (double) barWidth;
				break;
			case Horizontal_Ascending_To_Right:
				leftTopX = getCanvasX(fromX) + distanceFromSequenceStart + barSpacing;
				width = (double) barWidth;
				break;
			case Vertical_Ascending_To_Bottom:
				leftTopY = getCanvasY(fromX) + distanceFromSequenceStart + barSpacing;
				height = (double) barWidth;
				break;
			case Vertical_Ascending_To_Top:
				leftTopY = getCanvasY(toX) - distanceFromSequenceStart - barSpacing;
				height = (double) barWidth;
				break;
			}
		}
		int i = 0;
		for (double y : ySequence) {
			if (xAxis.isHorizontal()) {
				leftTopY = getYOnCanvas(y, baseY, true);
				height = getYOnCanvas(y, baseY, false) - leftTopY;
			} else {
				leftTopX = getXOnCanvas(y, baseY, true);
				width = getXOnCanvas(y, baseY, false) - leftTopX;
			}
			Rectangle r = new Rectangle(width, height, leftTopX, leftTopY,
					lookOut.get(reversedSeq ? ySequence.size() - i : i));
			setShadowForShape(r);
			goc.addAllGraphicalObject(r.toGraphicalObjects());
			i++;
			if (xAxis.isHorizontal()) {
				leftTopX += distanceBetweenBarStart;
			}
			else{
				leftTopY += distanceBetweenBarStart;
			}
		}
		return goc;
	}
	
	protected void setShadowForShape(Shape shape){
		shape.setShadowColor(shadowColor);
		shape.setShadowOffsetX(shadowOffsetX);
		shape.setShadowOffsetY(shadowOffsetY);
	}

	protected double getYOnCanvas(double a, double b, boolean lowerOnCanvas) {
		if ((yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom && lowerOnCanvas)
				|| (yAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top && !lowerOnCanvas)) {
			return getCanvasY(Math.min(a, b));
		} else {
			return getCanvasY(Math.max(a, b));
		}
	}

	protected double getXOnCanvas(double a, double b, boolean lowerOnCanvas) {
		if ((xAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right && lowerOnCanvas)
				|| (xAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left && !lowerOnCanvas)) {
			return getCanvasX(Math.min(a, b));
		} else {
			return getCanvasX(Math.max(a, b));
		}
	}

	protected double getWidthOnCanvas(double from, double to) {
		if (xAxis.isHorizontal())
			return Math.abs(getCanvasX(to) - getCanvasX(from));
		else
			return Math.abs(getCanvasY(to) - getCanvasY(from));
	}

/* Getters & Setters */
	@Override
	public void setShadowOffsetX(double offsetX) {
		this.shadowOffsetX = offsetX;
	}

	@Override
	public void setShadowOffsetY(double offsetY) {
		this.shadowOffsetY = offsetY;
	}

	@Override
	public double getShadowOffsetX() {
		return shadowOffsetX;
	}

	@Override
	public double getShadowOffsetY() {
		return shadowOffsetY;
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public int getZIndex() {
		return this.zIndex;
	}

	@Override
	public Color getShadowColor() {
		return shadowColor;
	}

	@Override
	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}

	/**
	 * @return the barChartType
	 */
	public BarChartType getBarChartType() {
		return barChartType;
	}

	/**
	 * @param barChartType the barChartType to set
	 */
	public void setBarChartType(BarChartType barChartType) {
		this.barChartType = barChartType;
	}

	/**
	 * @return the barSequencePosition
	 */
	public BarSequencePosition getBarSequencePosition() {
		return barSequencePosition;
	}

	/**
	 * @param barSequencePosition the barSequencePosition to set
	 */
	public void setBarSequencePosition(BarSequencePosition barSequencePosition) {
		this.barSequencePosition = barSequencePosition;
	}

	/**
	 * @return the fixedBarWidth
	 */
	public int getFixedBarWidth() {
		return fixedBarWidth;
	}

	/**
	 * @param fixedBarWidth the fixedBarWidth to set
	 */
	public void setFixedBarWidth(int fixedBarWidth) {
		this.fixedBarWidth = fixedBarWidth;
	}

	/**
	 * @return the maxBarWidth
	 */
	public int getMaxBarWidth() {
		return maxBarWidth;
	}

	/**
	 * @param maxBarWidth the maxBarWidth to set
	 */
	public void setMaxBarWidth(int maxBarWidth) {
		this.maxBarWidth = maxBarWidth;
	}

	/**
	 * @return the minBarWidth
	 */
	public int getMinBarWidth() {
		return minBarWidth;
	}

	/**
	 * @param minBarWidth the minBarWidth to set
	 */
	public void setMinBarWidth(int minBarWidth) {
		this.minBarWidth = minBarWidth;
	}

	/**
	 * @return the barSpacing
	 */
	public int getBarSpacing() {
		return barSpacing;
	}

	/**
	 * @param barSpacing the barSpacing to set
	 */
	public void setBarSpacing(int barSpacing) {
		this.barSpacing = barSpacing;
	}

	/**
	 * @return the baseY
	 */
	public double getBaseY() {
		return baseY;
	}

	/**
	 * @param baseY the baseY to set
	 */
	public void setBaseY(double baseY) {
		this.baseY = baseY;
	}

	@Override
	public void preUpdateModule() {
		if (dataSets.size() == 0)
			return;
		if (autoScaleViewportHorizontal) {
			normalizeData();
			scaleViewportToFitAllBars();
			autoScaleViewportHorizontal = false;
		}

		// update axes and viewport
		super.preUpdateModule();
	}

	@Override
	public TreeMap<String, Color> getLegendEntries() {
		if(legendEntries == null){
			TreeMap<String, Color> entries = new TreeMap<String, Color>();
			for(DataSet c : dataSets){
				entries.put(c.getName(), c.getColor());
			}
			
			return entries;
		}
		else{
			return legendEntries;
		}
	}
	@Override
	protected void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseOver(MouseEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseOut(MouseEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLookout(DataSet dataSet, ShapeProperties lookout){
		int i = dataSets.indexOf(dataSet);
		if(i == -1)
			return;
		this.lookOut.set(i, lookout);
	}
	
	public ShapeProperties getLookout(DataSet dataSet){
		int i = dataSets.indexOf(dataSet);
		if(i == -1)
			return null;
		return lookOut.get(i);
	}
	
	/**
	 * @return the dataSets
	 */
	public ArrayList<DataSet> getDataSets() {
		return dataSets;
	}

	@Override
	protected void onDataSetChange(DataSetChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSelect(DataEntrySelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDeselect(DataEntrySelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMove(ViewportChangeEvent event, double dx, double dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMoveAlongX(ViewportChangeEvent event, double dx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMoveAlongY(ViewportChangeEvent event, double dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSet(ViewportChangeEvent event, double xMin, double yMin,
			double xMax, double yMax) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSetX(ViewportChangeEvent event, double xMin, double xMax) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSetY(ViewportChangeEvent event, double yMin, double yMax) {
		// TODO Auto-generated method stub
		
	}

}
