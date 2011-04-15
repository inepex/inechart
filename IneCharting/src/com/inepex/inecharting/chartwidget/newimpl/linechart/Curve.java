package com.inepex.inecharting.chartwidget.newimpl.linechart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import com.inepex.inecharting.chartwidget.newimpl.axes.Axis;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axis.AxisType;
import com.inepex.inecharting.chartwidget.newimpl.axes.Tick;
import com.inepex.inecharting.chartwidget.newimpl.misc.HasShadow;
import com.inepex.inecharting.chartwidget.newimpl.misc.HasZIndex;
import com.inepex.inecharting.chartwidget.newimpl.properties.Color;
import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties;
import com.inepex.inecharting.chartwidget.newimpl.properties.ShapeProperties;
import com.inepex.inegraphics.shared.gobjects.Path;

/**
 * 
 * Represents a Line- or a Point- (or both) curve's model
 * Stores both the data, and the translated points.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class Curve implements HasZIndex,HasShadow{

	ArrayList<Point> uncalculatedPoints;
	/**
	 * All of the points
	 * in ascending order, the ordering key is: dataX
	 */
	ArrayList<Point> points;
	/**
	 * Points inside the viewPort
	 * in ascending order, the ordering key is: posX
	 */
	ArrayList<Point> visiblePoints;
	/**
	 * Stores information about lineCurve's discontinuity
	 * (If a point present in this Collection then there must not be line to next point)
	 */
	ArrayList<Point> discontinuities  = new ArrayList<Point>();
	
	
	/* Lookout */
	//Fills
	TreeMap<Curve, Color> toCurveFills;
	TreeMap<Integer, Color> toCanvasYFills;
	TreeMap<Double, Color> toYFills;
	//line
	LineProperties lineProperties;
	//points
	ShapeProperties pointProperties;
	
	double shadowOffsetX=0, shadowOffsetY=0;
	int zIndex = 0;
	//Axis
	AxisType yAxis = AxisType.Y;
	boolean modelChanged = false;
	double xMin, xMax, yMin, yMax, vpMin, vpMax;
	
	/**
	 * Creates an empty (without points) curve
	 */
	public Curve(){
		
	}
	
	/**
	 * This constructor creates a curve from the given dataSets, with discontinuities from
	 * each endPoints of the dataSets to the start of the next(in case of LineChart)
	 * @param dataSets
	 */
	public Curve(TreeMap<Double, Double>... dataSets) {
		for(TreeMap<Double, Double> dataSet: dataSets){
			addDataSet(dataSet);
		}
	}
	
	/**
	 * creates a curve from the given dataSet
	 * @param dataSet
	 */
	public Curve(TreeMap<Double, Double> dataSet) {
		addDataSet(dataSet);
	}
	
	/**
	 * 
	 * @param point
	 */
	public void addPoint(Point point){
		double x = point.getDataX();
		double y = point.getDataY();
		if(points == null){
			points = new ArrayList<Point>();
			uncalculatedPoints = new ArrayList<Point>();
			xMax = xMin = x;
			yMax = yMin = y;
		}
		points.add(point);
		uncalculatedPoints.add(point);
		
		//extremes
		if(x > xMax){
			xMax = x;
			modelChanged = true;
		}
		else if(x < xMin){
			xMin = x;
			modelChanged = true;
		}
		if(y > yMax){
			yMax = y;
			modelChanged = true;
		}
		else if(y < yMin){
			yMin = y;
			modelChanged = true;
		}
	}
	
	/**
	 * Removes the given Point from the curve
	 * @param point
	 * @return the removed Point, or null if there is no such Point
	 */
	public Point removePoint(Point point){
		int index = points.indexOf(point);
		if(index == -1)
			return null;
		else{
			uncalculatedPoints.remove(point);
			removeDiscontinuity(point);
			modelChanged = true;
			return points.remove(index);
		}	
	}
	
	/**
	 * Use this method in case of LineCurve.
	 * Defines a discontinuity to the next point
	 * If the point is not present, then adds it to the curve
	 * @param point
	 */
	public void addDiscontinuity(Point point){
		int index = points.indexOf(point);
		if(index == -1)
			points.add(point);
		discontinuities.add(point);
	}
	
	/**
	 * Removes the given point's discontinuity.
	 * @param point
	 */
	public void removeDiscontinuity(Point point){
		int index = discontinuities.indexOf(point);
		if(index == -1)
			return;
		discontinuities.remove(index);
	}
	
	/**
	 * Adds a new dataSet to the curve
	 * @param dataSet
	 */
	public void addDataSet(TreeMap<Double, Double> dataSet){
		//add disc. at the last point
		if(points != null && points.size()>0)
			addDiscontinuity(points.get(points.size()-1));
		for(double x : dataSet.keySet()){
			addPoint(new Point(x, dataSet.get(x)));
		}
	}
	
	/**
	 * Returns a {@link Point} with the given dataX
	 * @param dataX
	 * @return null if there is no Point created with the given value
	 */
	public Point getPoint(double dataX){
		for(Point point : points){
			if(dataX > point.getDataX())
				break;
			else if(dataX == point.getDataX())
				return point;
		}
		return null;
	}
	
	/**
	 * 
	 * @param point
	 * @return the point after, or null if it is the last.
	 */
	public Point getNextPoint(Point point){
		int index = points.indexOf(point);
		if(index != -1 && index < points.size() - 1)
			return points.get(index+1);
		else
			return null;
	}
	
	/**
	 * 
	 * @param point
	 * @return the point before, or null if it is the first.
	 */
	public Point getPreviousPoint(Point point){
		int index = points.indexOf(point);
		if(index > 0)
			return points.get(index-1);
		else
			return null;
	}

	/**
	 * 
	 * @return the list of all points contained by this curve 
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	/**
	 * 
	 * @return the list of all currently visible Points
	 */
	public ArrayList<Point> getVisiblePoints() {
		return visiblePoints;
	}  
	
	/**
	 * Updates the visible points container
	 * @param vpMin left side of the viewport
	 * @param vpMax right side of the viewport
	 * @param overlapFilterDistance 
	 * @return visible points
	 */
	ArrayList<Point> updateVisiblePoints(double vpMin, double vpMax, int overlapFilterDistance){
		if(visiblePoints == null)
			visiblePoints = new ArrayList<Point>();
		else
			visiblePoints.clear();
		for(Point point : points){
			if(point.getDataX() > vpMax)
				break;
			else if(point.getDataX() > vpMin)
				visiblePoints.add(point);
		}
		//TODO check speed & consistency
		if(overlapFilterDistance > 1 && visiblePoints.size() > 1){
			ArrayList<Point> cleared = new ArrayList<Point>();
			Point startPoint = visiblePoints.get(0);
			for(int i = 1; i < visiblePoints.size(); i++){
				Point actual = visiblePoints.get(i);
				while(Point.distance(startPoint, actual) < overlapFilterDistance){
					if(++i < visiblePoints.size())			
						actual = visiblePoints.get(i);
					else
						break;
				}
				//we found overlapping points
				if(actual != visiblePoints.get(--i)){
					//dont skip the first not overlapping point
					
					Point imgPoint = new Point();
					imgPoint.setPosX((startPoint.getPosX() + actual.getPosX()) / 2);
					imgPoint.setPosY((startPoint.getPosY() + actual.getPosY()) / 2);
					
					cleared.add(imgPoint);
				}
				else
					cleared.add(startPoint);
				startPoint = actual;
			}
			cleared.add(startPoint);
			visiblePoints = cleared;		
		}
		this.vpMax = vpMax;
		this.vpMin = vpMin;
		return visiblePoints;
	}
	
	/**
	 * Fills the area between this and the given curve
	 * @param curve
	 * @param color {@link Color}
	 */
	public void addFill(Curve curve, Color color){
		if(this.toCurveFills == null)
			toCurveFills = new TreeMap<Curve, Color>();
		toCurveFills.put(curve, color);		
	}
	
	/**
	 * Fills an area between the curve and a horizontal line at the given y value
	 * @param y y position of the line
	 * @param color {@link Color}
	 */
	public void addFill(double y, Color color){
		if(this.toYFills == null)
			toYFills = new TreeMap<Double, Color>();
		toYFills.put(y, color);		
	}
	
	/**
   	* Fills an area between a tick and the curve
	* Same as addFill(tick.getPosition(), color);
	* @param tick
	* @param color
	*/
	public void addFill(Tick tick, Color color){
		if(this.toYFills == null)
			toYFills = new TreeMap<Double, Color>();
		toYFills.put(tick.getPosition(), color);		
	}
	
	/**
	 * Fills the area between the curve and a horizontal line at a fix position of the chart's canvas
	 * @param y in px (independent from axis scaling)
	 */
	public void addFill(int y, Color color){
		if(this.toCanvasYFills == null)
			toCanvasYFills = new TreeMap<Integer, Color>();
		toCanvasYFills.put(y, color);		
	}
	
	/**
	 * Fills the area between this and the given curve
	 * @param curve
	 * @param color {@link Color}
	 */
	public void removeFill(Curve curve){
		toCurveFills.remove(curve);		
	}
	
	/**
	 * Fills the area between the curve and a horizontal line at the given y value
	 * @param y y position of the line
	 * @param color {@link Color}
	 */
	public void removeFill(double y){
		toYFills.remove(y);		
	}
	
	/**
	 * Fills the area between the curve and a horizontal line at a fix position of the chart's canvas
	 * @param y in px (independent from axis scaling)
	 */
	public void removeFill(int y, Color color){
		toCanvasYFills.remove(y);		
	}
	
	/**
	 * @return the lineProperties
	 */
	public LineProperties getLineProperties() {
		return lineProperties;
	}

	/**
	 * @param lineProperties the line of the linechart
	 */
	public void setLineProperties(LineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}

	/**
	 * @return the yAxis
	 */
	public AxisType getyAxis() {
		return yAxis;
	}
	

	/**
	 * @param yAxis the yAxis to set
	 */
	public void setyAxis(AxisType yAxis) {
		if(this.yAxis != yAxis)
			modelChanged = true;
		this.yAxis = yAxis;
	}
	
	/**
	 * Calculates the pos of the points based on data
	 * @param from x interval left
	 * @param to x interval right
	 * @param onlyUncalculated true if calculate only the points contained by 
	 * uncalculatedPoints (newly added or not yet recalculated), false if all the points
	 * @param xRatio * dataX = posX 
	 * @param yRatio * dataY = posY
	 */
	void calculatePoints(double from, double to, boolean onlyUncalculated, LineChart calculatorInst){
		ArrayList<Point> calculated = new ArrayList<Point>();
		Collections.sort(uncalculatedPoints, Point.dataXComparator());
		if(onlyUncalculated){
			for(Point point : uncalculatedPoints){
				if(point.getDataX() > to)
					break;
				else if(point.getDataX() > from){
					calculatorInst.calculatePoint(point, yAxis);
					calculated.add(point);
				}
			}
		}
		else{
			for(Point point : points){
				if(point.getDataX() > to)
					break;
				else if(point.getDataX() > from){
					calculatorInst.calculatePoint(point, yAxis);
					calculated.add(point);
				}
			}
		}
		if(calculated.size() > 0){
			Point tmp = getPreviousPoint(calculated.get(0));
			Point tmp2 = getNextPoint(calculated.get(calculated.size()-1));
			if(tmp != null)
				calculatorInst.calculatePoint(tmp,yAxis);
			if(tmp2 != null)
				calculatorInst.calculatePoint(tmp2, yAxis);
			if(tmp2 != null)
				calculated.add(tmp2);
			if(tmp != null)
				calculated.add(tmp);
		}
		for(Point point : calculated){
			uncalculatedPoints.remove(point);
		}
	}
	
	void updateUncalculatedPoints(double viewportMin, double viewportMax){
		uncalculatedPoints.clear();
		for(Point point : points){
			if(point.getDataX() <= viewportMin || point.getDataX() >= viewportMax){
				uncalculatedPoints.add(point);
			}
		}
	}
	
	public Point getPointBefore(double x){
		Point last = null;
		for(Point point : points){
			if(point.getDataX() > x)
				return last;
			last = point;
		}
		return null;
	}
	
	public Point getPointAfter(double x){
		for(Point point : points){
			if(point.getDataX() > x)
				return point;
		}
		return null;
	}
	
	/**
	 * 
	 * @return a path with null context, no fill, no stroke -> 'bare' path
	 */
	Path getVisiblePath(){
		ArrayList<Point> points = getVisiblePoints();
		if(points.size() == 0){
			Point bef = getPointBefore(vpMin);
			if(bef != null && !discontinuities.contains(bef)){
				points.add(bef);
				Point aft = getNextPoint(bef);
				if(aft != null)
					points.add(aft);
				else{
					points.clear();
					return null;
				}
			}
			else return null;
		}
		Path line;
		Point temp = getPreviousPoint(points.get(0));
		int i = 0;
		if(temp != null && !discontinuities.contains(temp)){ 
			//there is not discontuniuty
			//TODO zIndex
			line = new Path(temp.getPosX(), temp.getPosY(), 1, null, false, false);
		}
		else{
			line = new Path(points.get(i).getPosX(), points.get(i).getPosY(), 1, null, false, false);
			i++;
		}
		for(; i < points.size(); i++){
			if(!discontinuities.contains(points.get(i)))
				line.lineTo(points.get(i).getPosX(), points.get(i).getPosY(), false);
			else
				line.moveTo(points.get(i).getPosX(), points.get(i).getPosY(), false);
		}
		temp = getNextPoint(points.get(points.size() - 1));
		if(temp != null && !discontinuities.contains(points.get(points.size() - 1))){ 
			line.lineTo(temp.getPosX(), temp.getPosY(), false);
		}
		return line;
	}

	
	
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
}
