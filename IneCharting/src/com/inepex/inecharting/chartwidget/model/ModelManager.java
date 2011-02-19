package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.IneChartProperties;

/**
 * An utility class for managing the IneChart's underlying model.
 * 
 *
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class ModelManager {

	public static ModelManager instance = null;
	
	public static ModelManager get(){
		return instance;
	}
	public static ModelManager create(IneChartProperties prop){
		instance = new ModelManager(prop);
		return instance;
	}
	
	
	private int chartCanvasWidth;
	private int chartCanvasHeight;
	private int chartCanvasTopPaddingPercentage;
	private Double xMin = null;
	private Double xMax = null;
	private Double yMax = null;
	private Double yMin = null;
	private Double y2Max = null;
	private Double y2Min = null;
	private double viewportMin;
	private double viewportMax;
	private AxisCalculator axisCalculator;
	private IneChartProperties properties;
		
	private ModelManager(IneChartProperties properties){
		this.chartCanvasHeight = properties.getChartCanvasHeight();
		this.chartCanvasWidth = properties.getChartCanvasWidth();
		this.chartCanvasTopPaddingPercentage = properties.getChartCanvasTopPaddingPercentage();
		xMin = null;
		axisCalculator = new AxisCalculator(this);
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
	 * The viewport's minimum in pixels.
	 * @return
	 */
	public int getViewportMinInPx(){
		return (int) calculateX(viewportMin);
	}
	
	/**
	 * The viewport's maximum in pixels.
	 * @return
	 */
	public int getViewportMaxInPx(){
		return (int) calculateX(viewportMax);
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
	public int calculateXRelativeToViewport(double x) {
		return (int) (calculateX(x) - calculateX(viewportMin)); 
	}
	
	/**
	 * Translates a datapoint's y value to the canvas' coordinate system
	 * @param y data to translate
	 * @param minValue the lowest value in the curve's datamap
	 * @param maxValue the highest value in the curve's datamap
	 * @return
	 */
	public double calculateYWithPadding(double y, double minValue, double maxValue){
		int padding = (int) ((chartCanvasTopPaddingPercentage / 100d) * chartCanvasHeight);
		double unit = (chartCanvasHeight - padding ) / (maxValue - minValue);
		return unit * (maxValue - y) + padding;
	}
	
	public double calculateYDistance(int dyInPx,double minValue, double maxValue){
		double unit = (maxValue - minValue) / (chartCanvasHeight);
		return unit * dyInPx;
	}
	
	public int calculateYWithoutPadding(double y, double minValue, double maxValue){
		double unit = (chartCanvasHeight) / (maxValue - minValue);
		return (int) (unit * (maxValue - y));
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
	public int calculateDistance(double dx){
		return (int) (calculateX(xMin + dx)
				- calculateX(xMin));
	}
	
	/**
	 * Distance translation from canvas to data
	 * @param dx distance in pixels
	 * @return distance in data
	 */
	public double calculateDistance(int dxInPx){
		double unit =  (viewportMax - viewportMin) / chartCanvasWidth;
		return unit * dxInPx;
	}
	
	/** 
	 * Updates a curve's calculatedPoints container with the points calculated
	 * within given data interval.
	 * @param curve
	 * @param start
	 * @param stop
	 */
	public void calculateAndSetPointsForInterval(Curve curve, double start, double stop){
		if(curve.getDataMap().firstKey() > stop  || curve.getDataMap().lastKey() < start)
			return;
		TreeMap<Double, Double> dataToCheck  = new TreeMap<Double, Double>();
		for(Double x:curve.getDataMap().keySet())
			if(x >= start && x <= stop && !curve.getCalculatedPoints().containsKey(x))
				dataToCheck.put(x, curve.getDataMap().get(x));
		for(double x : dataToCheck.keySet()){
			//check if we have calculated a point before
			Point point = curve.getCalculatedPoints().get(x);
			//if no point for this data
			if(point == null){
				int xPos, yPos;
				if(curve.getCurveDrawingInfo().isMathematicalRounding()){ //use Math.round on each value
					xPos = (int) Math.round(calculateX(x));
					yPos = (int) Math.round(calculateYWithPadding(dataToCheck.get(x), curve.getMinValue(), curve.getMaxValue()));
				}
				else{ //simply cast to int
					xPos = (int) calculateX(x);
					yPos = (int) calculateYWithPadding(dataToCheck.get(x), curve.getMinValue(), curve.getMaxValue());
				}
				point = new Point(xPos, yPos, false, curve);
				curve.getCalculatedPoints().put(x, point);
			}
		}
	}
	
	/**
	 * Applies overlap-policies on the given curve within the given interval.
	 * @param curve
	 * @param start
	 * @param stop
	 */
	public void filterOverlappingPoints(Curve curve, double start, double stop){
		if(curve.getDataMap().firstKey() > stop  || curve.getDataMap().lastKey() < start)
			return;
		long startTime = System.currentTimeMillis();		
		TreeMap<Double, Point> pointsToFilter = new TreeMap<Double,Point>();
		TreeMap<Double, Point> xFiltered = new TreeMap<Double, Point>();
		TreeMap<Double, Point> squareFiltered = new TreeMap<Double, Point>();
		ArrayList<Double> problematicIndices = null;
		int willNotShowCount = 0;
		for(Double x:curve.getCalculatedPoints().keySet())
			if(x >= start && x <= stop){
				if(!curve.getPointsToDraw().containsKey(x))
					pointsToFilter.put(x, curve.getCalculatedPoints().get(x));
			}
		//the actual
		Double firstIndex = null;
	
	/* applying X - filter */
		for(double x:pointsToFilter.keySet()){
			if(firstIndex == null){
				firstIndex = x;
				continue;
			}
			// if it is problematic element
			else if(pointsToFilter.get(x).getxPos() - pointsToFilter.get(firstIndex).getxPos() < curve.getCurveDrawingInfo().getOverlapFilterXWidth()){
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
		if(firstIndex != null){
			//there were some  points need  filtering after ending cycle
			if(problematicIndices != null){
				willNotShowCount +=  problematicIndices.size() - 1; 
				ArrayList<Point> points = new ArrayList<Point>();
				for(Double key:problematicIndices)
					points.add(curve.getCalculatedPoints().get(key));
				Point pointToShow = choosePointByPolicy(points, curve);
				for(Double key:problematicIndices)
					xFiltered.put(key, pointToShow);
			}
			//we need to put the last key to pointsToDraw
			else{
				xFiltered.put(firstIndex, curve.getCalculatedPoints().get(firstIndex));
			}
		}
//		Log.debug(willNotShowCount + " points have been thrown out due to x-overlap-policy in " + (System.currentTimeMillis() - startTime) + " ms" );
	/* applying square - filter */
		startTime = System.currentTimeMillis();
		willNotShowCount = 0;
		if(curve.getCurveDrawingInfo().getOverlapFilterSquareSize() <= curve.getCurveDrawingInfo().getOverlapFilterXWidth()){
			curve.getPointsToDraw().putAll(xFiltered);
			return;
		}

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
				squareTopMax = actual.getyPos() + curve.getCurveDrawingInfo().getOverlapFilterSquareSize();
				squareBottomMin = squareTopMax -  2 * curve.getCurveDrawingInfo().getOverlapFilterSquareSize();
				highestYinSquare = lowestYinSquare = actual.getyPos();
				continue;
			}
			// if the actual point's x position is in a square and ...
			else if(actual.getxPos() - xFiltered.get(firstIndex).getxPos() < curve.getCurveDrawingInfo().getOverlapFilterSquareSize()  &&
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
				squareTopMax = actual.getyPos() + curve.getCurveDrawingInfo().getOverlapFilterSquareSize();
				squareBottomMin = squareTopMax -  2 * curve.getCurveDrawingInfo().getOverlapFilterSquareSize();
				highestYinSquare = lowestYinSquare = actual.getyPos();
			}
		}
		if(firstIndex != null){
			//there were some  points need  filtering
			if(problematicIndices != null){
				willNotShowCount +=  problematicIndices.size() - 1; 
				ArrayList<Point> points = new ArrayList<Point>();
				for(Double key:problematicIndices)
					points.add(curve.getCalculatedPoints().get(key));
				Point pointToShow = choosePointByPolicy(points, curve);
				for(Double key:problematicIndices)
					squareFiltered.put(key, pointToShow);
			}
			//we need to put the last key to pointsToDraw
			else{
				squareFiltered.put(firstIndex, curve.getCalculatedPoints().get(firstIndex));
			}
		}
//		Log.debug(willNotShowCount + " points have been thrown out due to square-overlap-policy in " + (System.currentTimeMillis() - startTime) + " ms" );
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
		switch(curve.getCurveDrawingInfo().getOverlapFilerPolicy()){
		case FIRST_POINT:
			return new Point( x, problematicPoints.get(0).getyPos(), true, curve);
		case LAST_POINT:
			return new Point(x,problematicPoints.get(problematicPoints.size()-1).getyPos(), true, curve);
		case AVERAGE:
			int sum = 0;
			for(Point axr: problematicPoints)
				sum += axr.getyPos();
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

	/**
	 * When viewport's resolution changes, call this method to set the new x position for calculated points in the curve
	 * @param curve
	 * @param shrinkRatio
	 */
	public void setXPositionForCalculatedPoints(Curve curve, double shrinkRatio){
		for(Double x:curve.getCalculatedPoints().keySet()){
			if(curve.getCurveDrawingInfo().isMathematicalRounding())
				curve.getCalculatedPoints().get(x).setxPos((int) Math.round(curve.getCalculatedPoints().get(x).getxPos() * shrinkRatio));
			else
				curve.getCalculatedPoints().get(x).setxPos((int) (curve.getCalculatedPoints().get(x).getxPos() * shrinkRatio));
		}
	}
	
	/**
	 * When adding a curve to the chart, which has a lower xMin than the actual, there's no need to recalculate all the points of all curves,
	 * just shift them with the (positive) difference between the new and the old xMin
	 *  (and do not forget to do this with the curve's visualizer!)
	 * @param curve
	 * @param dx
	 */
	public void movePoints(Curve curve, double dx){
		for(Double x:curve.getCalculatedPoints().keySet()){
			curve.getCalculatedPoints().get(x).setxPos(curve.getCalculatedPoints().get(x).getxPos() + calculateDistance(dx));
		}
		for(Double x:curve.getPointsToDraw().keySet()){
			if(curve.getPointsToDraw().get(x).isImaginaryPoint())
				curve.getPointsToDraw().get(x).setxPos(curve.getPointsToDraw().get(x).getxPos() + calculateDistance(dx));
		}
	}
	
	public void rescaleYPositions(Curve curve, double min, double max){
		for(Double x:curve.getCalculatedPoints().keySet()){
			curve.getCalculatedPoints().get(x).setyPos((int) calculateYWithPadding(curve.getDataMap().get(x), min, max));
		}
		curve.getPointsToDraw().clear();
	}
	
	/**
	 * Returns x value(s in case of imaginary Point)(from the datamap) for a point
	 * @param point
	 * @return can return an empty list if the point is not in calculatedPoints, neither in pointsToDraw collection
	 */
 	public ArrayList<Double> getDataForPoint(Point point) {
		ArrayList<Double> datas = new ArrayList<Double>();
		
		if( /* point.isImaginaryPoint() && */	point.getParent().getPointsToDraw().containsValue(point)){
			for(Double x : point.getParent().getPointsToDraw().keySet()){
				if(point.getParent().getPointsToDraw().get(x).equals(point))
					datas.add(x);
			}
		}
		//not imaginary - 1 data
		else{
			for(Double x : point.getParent().getCalculatedPoints().keySet()){
				if(point.getParent().getCalculatedPoints().get(x).equals(point)){
					datas.add(x);
					break;
				}
			}
		}
		return datas;
	}

	public int getChartCanvasHeight() {
		return chartCanvasHeight;
	}

	public int getChartCanvasWidth() {
		return chartCanvasWidth;
	}
	
	public Double getxMin() {
		return xMin;
	}

	public double getViewportMin() {
		return viewportMin;
	}

	public double getViewportMax() {
		return viewportMax;
	}

	public Double getxMax() {
		return xMax;
	}

	public void setxMax(Double xMax) {
		this.xMax = xMax;
	}

	public AxisCalculator getAxisCalculator() {
		return axisCalculator;
	}

	public Double getyMax() {
		return yMax;
	}

	public Double getyMin() {
		return yMin;
	}

	public Double getY2Max() {
		return y2Max;
	}

	public Double getY2Min() {
		return y2Min;
	}

	public void setxMin(Double xMin) {
		this.xMin = xMin;
	}

	public void setyMax(Double yMax) {
		this.yMax = yMax;
	}

	public void setyMin(Double yMin) {
		this.yMin = yMin;
	}

	public void setY2Max(Double y2Max) {
		this.y2Max = y2Max;
	}

	public void setY2Min(Double y2Min) {
		this.y2Min = y2Min;
	}
	
	public void setVisiblePoints(Curve curve){
		Double start, stop;
		start = curve.getLastInvisiblePointBeforeViewport(viewportMin);
		stop = curve.getFirstInvisiblePointAfterViewport(viewportMax);
		if(start == null)
			start = curve.getDataMap().firstKey();
		if(stop == null)
			stop = curve.getDataMap().lastKey();
		for(double x : curve.getPointsToDraw().keySet()){
			if(x > stop)
				break;
			else if(x >= start){
				Point point = curve.getPointsToDraw().get(x);
				if(!curve.getVisiblePoints().contains(point))
					curve.getVisiblePoints().add(point);
			}
		}
		if(curve.getVisiblePoints().size() == 1)
			curve.getVisiblePoints().clear();
	}
	
	/**
	 * Sets the curve's point containers to fit the viewport
	 *
	 * @param curve
	 * @param resolutionChanged if true all point-container will be cleared
	 */
	public void getPointsForCurve(Curve curve, boolean resolutionChanged){
		//clear previous points
		curve.getVisiblePoints().clear();
		if(resolutionChanged){
			curve.getCalculatedPoints().clear();
			curve.getPointsToDraw().clear();
		}
		//setting the interval to calculate
		Double start, stop;
		if( curve.getCurveDrawingInfo().isPreCalculatePoints() ){ 
			start = curve.getDataMap().firstKey();
			stop = curve.getDataMap().lastKey();
		}
		else{
			start = curve.getLastInvisiblePointBeforeViewport(viewportMin);
			stop = curve.getFirstInvisiblePointAfterViewport(viewportMax);
			if(start == null)
				start = curve.getDataMap().firstKey();
			if(stop == null)
				stop = curve.getDataMap().lastKey();
		}
		//calculate points, filter  them at the interval
		if(curve.getCalculatedPoints().size() != curve.getDataMap().size()
			|| curve.getCalculatedPoints().size() != curve.getPointsToDraw().size()){
			calculateAndSetPointsForInterval(curve, start, stop);
			filterOverlappingPoints(curve, start, stop);
		}
	
		setVisiblePoints(curve);
	}
}
