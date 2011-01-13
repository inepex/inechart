package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;
import java.util.TreeMap;

import com.allen_sauer.gwt.log.client.Log;

/**
 * An utility class for managing the IneChart's underlying model.
 * @author Miklós Süveges
 *
 */
public class ModelManager {
	 
	private static ModelManager inst = null;
	public static ModelManager get(int width, int height, double xMin, int chartCanvasTopPaddingPercentage){
		if(inst == null)
			inst = new ModelManager( width, height, xMin,chartCanvasTopPaddingPercentage);
		return inst;
	}
	
	private int chartCanvasWidth;
	private int chartCanvasHeight;
	private int chartCanvasTopPaddingPercentage;
	private double xMin;
	private double viewportMin;
	private double viewportMax;
	
	private ModelManager() {
		
	}
	
	private ModelManager(int width, int height, double xMin, int chartCanvasTopPaddingPercentage){
		this.chartCanvasHeight = height;
		this.chartCanvasWidth = width;
		this.xMin = xMin;
		
		this.chartCanvasTopPaddingPercentage = chartCanvasTopPaddingPercentage;
	}
	
	/**
	 * This method should be called each time when viewport's resolution changes
	 * @param viewportMin
	 * @param viewportMax
	 */
	public void setViewport( double viewportMin, double viewportMax){
		this.viewportMax = viewportMax;
		this.viewportMin = viewportMin;
	}
	
	/**
	 * Translates a datapoint's x value to the canvas' coordinate system
	 * @param x data to translate
	 * @param chartCanvasWidth the canvas' width
	 * @param viewportMin left side of the actual viewport
	 * @param viewportMax right side of the actual viewport
	 * @param xMin the lowest key in datamapping (in case of more curves, should be the lowest key in all the datamaps)
	 * @return
	 */
	public double calculateX(double x) {
		double unit = chartCanvasWidth / (viewportMax - viewportMin);
		return unit * (x - xMin);
	}
	
	/**
	 * Translates a datapoint's y value to the canvas' coordinate system
	 * @param y data to translate
	 * @param minValue the lowest value in the curve's datamap
	 * @param maxValue the highest value in the curve's datamap
	 * @param chartCanvasHeight the canvas' height
	 * @param chartCanvasTopPaddingPercentage size of the blank area (padding) at the top of the canvas, in percentage
	 * @return
	 */
	public double calculateY(double y, double minValue, double maxValue){
		int padding = (int) ((chartCanvasTopPaddingPercentage / 100d) * chartCanvasHeight);
		double unit = (chartCanvasHeight - padding ) / (maxValue - minValue);
		return unit * (maxValue - y) + padding;
	}
	
	/**
	 * Creates an interval in the underlying data's dimension for an x (pixel) position in canvas
	 * @param x a point's x position in the canvas' coordinate system
	 * @param mathematicalRounding 
	 * @param viewportMin left side of the actual viewport
	 * @param viewportMax right side of the actual viewport
	 * @param xMin the lowest key in datamapping (in case of more curves, hould be the lowest key in all datamap)
	 * @param chartCanvasWidth the canvas' width
	 * @return
	 */
	public double[] calculateDataIntervalXForPoint(int x, boolean mathematicalRounding){
		double dataPerPixel = (viewportMax - viewportMin) / (double)chartCanvasWidth;
		double data = dataPerPixel * x + xMin;
		if(mathematicalRounding)
			return new double[]{data-dataPerPixel/2,data+dataPerPixel/2};
		else
			return new double[]{data,data+dataPerPixel};
	}
	/**
	 * Distance translation from data to canvas
	 * @param dx distance in data
	 * @return distance in pixels
	 */
	public int calculateDistance(double dx, int chartCanvasWidth){
		return (int) (calculateX(xMin + dx)
				- calculateX(xMin));
	}
	
	/** 
	 * Updates a curve's calculatedPoints container with the points calculated at the given data interval.
	 * @param curve
	 * @param start
	 * @param stop
	 */
	public void calculateAndSetPointsForInterval(Curve curve, double start, double stop){
		TreeMap<Double, Double> dataToCheck  = new TreeMap<Double, Double>();
		for(Double x:curve.getDataMap().keySet())
			if(x >= start && x <= stop)
				dataToCheck.put(x, curve.getDataMap().get(x));
		for(double x : dataToCheck.keySet()){
			//check if we have calculated a point before
			Point point = curve.getCalculatedPoints().get(x);
			//if no point for this data
			if(point == null){
				int xPos, yPos;
				if(curve.isMathematicalRounding()){ //use Math.round on each value
					xPos = (int) Math.round(calculateX(x));
					yPos = (int) Math.round(calculateY(dataToCheck.get(x), curve.getMinValue(), curve.getMaxValue()));
				}
				else{ //simply cast to int
					xPos = (int) calculateX(x);
					yPos = (int) calculateY(dataToCheck.get(x), curve.getMinValue(), curve.getMaxValue());
				}
				point = new Point(xPos, yPos, false, curve);
				curve.getCalculatedPoints().put(x, point);
			}
		}
	}
	/**
	 * Filters a curve's calculatedPoints container, and extends the pointsToDraw, based on curve's related policies.
	 * @param curve
	 * @param start
	 * @param stop
	 */
	public void filterOverlappingPoints(Curve curve, double start, double stop){
		long startTime = System.currentTimeMillis();
		TreeMap<Double, Point> pointsToFilter = new TreeMap<Double,Point>();
		TreeMap<Double, Point> xFiltered = new TreeMap<Double, Point>();
		ArrayList<Double> problematicIndices = null;
		int willNotShowCount = 0;
		for(Double x:curve.getCalculatedPoints().keySet())
			if(x >= start && x <= stop){
				if(curve.getPointsToDraw().get(x) == null)
					pointsToFilter.put(x, curve.getCalculatedPoints().get(x));
			}
		Double firstIndex = null;
	/* applying X - filter */
		for(double x:pointsToFilter.keySet()){
			if(firstIndex == null){
				firstIndex = x;
				continue;
			}
			//problematic element
			else if(pointsToFilter.get(x).getxPos() - pointsToFilter.get(firstIndex).getxPos() < curve.getOverlapFilterXWidth()){
				if(problematicIndices == null){ 
					problematicIndices = new ArrayList<Double>();
					problematicIndices.add(firstIndex);
				}
				problematicIndices.add(x);
			}
			//not problematic
			else{
				//there were some  points need  filtering
				if(problematicIndices != null){
					willNotShowCount +=  problematicIndices.size() - 1; 
					ArrayList<Point> points = new ArrayList<Point>();
					for(Double key:problematicIndices)
						points.add(curve.getCalculatedPoints().get(key));
					Point pointToShow = choosePointByPolicy(points, curve);
					for(Double key:problematicIndices)
						xFiltered.put(key, pointToShow);
					problematicIndices = null;
				}
				//we need to put the last key to pointsToDraw
				else{
					xFiltered.put(firstIndex, curve.getCalculatedPoints().get(firstIndex));
				}
				firstIndex = x;
			}
		}
		Log.debug(willNotShowCount + " points have been thrown out due to x-overlap-policy in " + (System.currentTimeMillis() - startTime) + " ms" );
		startTime = System.currentTimeMillis();
		willNotShowCount = 0;
		if(curve.getOverlapFilterSquareSize() <= curve.getOverlapFilterXWidth())
			curve.getPointsToDraw().putAll(xFiltered);
	}
	
	/**
	 * From the given parameters, chooses a point to draw.
	 * The x value will be the average of the first and the last value.
	 * 
	 * @param problematicPoints
	 * @param curve
	 * @return a new Point object 
	 */
	private Point choosePointByPolicy(ArrayList<Point> problematicPoints, Curve curve){
		int x = problematicPoints.get(0).getxPos() + (problematicPoints.get(problematicPoints.size() - 1).getxPos() - problematicPoints.get(0).getxPos()) / 2;
		switch(curve.getOverlapFilerPolicy()){
		case FIRST_POINT:
			return new Point( x, problematicPoints.get(0).getxPos(), true, curve);
		case LAST_POINT:
			return new Point(x,problematicPoints.get(problematicPoints.size()-1).getyPos(), true, curve);
		case AVERAGE:
			int sum = 0;
			for(Point axr: problematicPoints)
				sum += axr.getxPos();
			return new Point( x,sum/problematicPoints.size(), true, curve);
		case LOWER:
			int lowest = problematicPoints.get(0).getyPos();
			for(Point act: problematicPoints)
				if(act.getyPos()< lowest)
					lowest = act.getyPos();
			return new Point( x,lowest, true, curve);
		case HIGHER:
			int highest = problematicPoints.get(0).getyPos();
			for(Point act: problematicPoints)
				if(act.getyPos() > highest)
					highest = act.getyPos();
			return new Point( x,highest, true, curve);
		default: return null;
		}
	}

	

}
