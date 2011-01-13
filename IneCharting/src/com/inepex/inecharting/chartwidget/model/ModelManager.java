package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;
import java.util.TreeMap;

import com.allen_sauer.gwt.log.client.Log;

/**
 * An utility class for managing the IneChart's underlying model.
 * 
 * Usage:
 * Create an instance with initial parameters of the canvas, then update it with setViewport method each time the viewport changes
 * @author Miklós Süveges
 *
 */
public class ModelManager {
	 
	private int chartCanvasWidth;
	private int chartCanvasHeight;
	private int chartCanvasTopPaddingPercentage;
	private double xMin;
	private double viewportMin;
	private double viewportMax;
	
		
	public ModelManager(int width, int height, double xMin, int chartCanvasTopPaddingPercentage){
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
	 * Filters a curve's calculatedPoints to extend the pointsToDraw container, based on curve's related policies.
	 * @param curve
	 * @param start
	 * @param stop
	 */
	public void filterOverlappingPoints(Curve curve, double start, double stop){
		long startTime = System.currentTimeMillis();
		TreeMap<Double, Point> pointsToFilter = new TreeMap<Double,Point>();
		TreeMap<Double, Point> xFiltered = new TreeMap<Double, Point>();
		TreeMap<Double, Point> squareFiltered = new TreeMap<Double, Point>();
		ArrayList<Double> problematicIndices = null;
		int willNotShowCount = 0;
		for(Double x:curve.getCalculatedPoints().keySet())
			if(x >= start && x <= stop){
				if(curve.getPointsToDraw().get(x) == null)
					pointsToFilter.put(x, curve.getCalculatedPoints().get(x));
			}
		//the actual examinee
		Double firstIndex = null;
	
	/* applying X - filter */
		for(double x:pointsToFilter.keySet()){
			if(firstIndex == null){
				firstIndex = x;
				continue;
			}
			// if it is problematic element
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
	/* applying square - filter */
		startTime = System.currentTimeMillis();
		willNotShowCount = 0;
		if(curve.getOverlapFilterSquareSize() <= curve.getOverlapFilterXWidth())
			curve.getPointsToDraw().putAll(xFiltered);

		Integer squareTopMax = null;
		Integer highestYinSquare = null;
		Integer squareBottomMin = null;
		Integer lowestYinSquare = null;
		firstIndex = null;
		problematicIndices = null;
		for(double x:xFiltered.keySet()){
			Point actual = xFiltered.get(x);
			if(firstIndex == null){
				firstIndex = x;
				squareTopMax = actual.getyPos() + curve.getOverlapFilterSquareSize();
				squareBottomMin = squareTopMax -  2 * curve.getOverlapFilterSquareSize();
				highestYinSquare = lowestYinSquare = actual.getyPos();
				continue;
			}
			// if the actual point's x position is in a square and ...
			else if(actual.getxPos() - xFiltered.get(firstIndex).getxPos() < curve.getOverlapFilterSquareSize()  &&
				actual.getyPos() >  squareBottomMin   &&   actual.getyPos() < squareTopMax){ // ...check the same condition on y
				if(problematicIndices == null){ 
					problematicIndices = new ArrayList<Double>();
					problematicIndices.add(firstIndex);
				}
				problematicIndices.add(x);
				//refine the square's min max values
				if(actual.getyPos() > highestYinSquare){
					squareBottomMin += actual.getyPos() - highestYinSquare;
					highestYinSquare = actual.getyPos();
				}
				else if (actual.getyPos() < lowestYinSquare){
					squareTopMax -= lowestYinSquare - actual.getyPos();
					lowestYinSquare = actual.getyPos();
				}
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
						squareFiltered.put(key, pointToShow);
					problematicIndices = null;
				}
				//we need to put the last key to pointsToDraw
				else{
					squareFiltered.put(firstIndex, curve.getCalculatedPoints().get(firstIndex));
				}
				firstIndex = x;
				squareTopMax = actual.getyPos() + curve.getOverlapFilterSquareSize();
				squareBottomMin = squareTopMax -  2 * curve.getOverlapFilterSquareSize();
				highestYinSquare = lowestYinSquare = actual.getyPos();
			}
		}
		Log.debug(willNotShowCount + " points have been thrown out due to square-overlap-policy in " + (System.currentTimeMillis() - startTime) + " ms" );
		curve.getPointsToDraw().putAll(squareFiltered);
	}
	
	/**
	 * From the given parameters, chooses a point to draw.
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
