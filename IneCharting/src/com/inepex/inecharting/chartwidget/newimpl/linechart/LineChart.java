package com.inepex.inecharting.chartwidget.newimpl.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sourceforge.htmlunit.corejs.javascript.ast.Yield;

import com.inepex.inecharting.chartwidget.newimpl.IneChartModul;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axes;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axis;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axis.AxisType;
import com.inepex.inecharting.chartwidget.newimpl.linechart.LineChartProperties.PointSelectionMode;
import com.inepex.inecharting.chartwidget.newimpl.properties.Color;
import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties;
import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties.LineStyle;
import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;
import com.inepex.inegraphics.impl.client.GraphicalObjectEventHandler;
import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;

public class LineChart extends IneChartModul implements GraphicalObjectEventHandler{
	
	LineChartProperties properties = null;
	double viewportMin=0, viewportMax=0;
	boolean viewportResized = false;
	boolean viewportChanged  = false;

	//model fields
	ArrayList<Curve> curves;
	Axis xAxis;
	Axis yAxis;
	Axis y2Axis;
	Axes axes;
	double yMax, y2Max, xMax, yMin, y2Min, xMin, yRatio, y2Ratio, xRatio;
	
	//interavtive- and graphicalobjects
	TreeMap<GraphicalObject, Point> interactivePoints = new TreeMap<GraphicalObject, Point>();
	ArrayList<Point> selectedPoints = new ArrayList<Point>();
	TreeMap<Curve, GraphicalObjectContainer> gosPerCurve;
	TreeMap<Curve, TreeMap<Point, GraphicalObjectContainer>> gosPerPoint;
	boolean redrawNeeded = false;
	
	public LineChart(DrawingAreaImplCanvas canvas, Axes axes) {
		super(canvas);
		canvas.addGraphicalObjectEventHandler(this);
		this.axes = axes;
	}

	
	protected void calculateAxes(double min, double max){
		
	}

	public void addCurve(Curve curve) {
		if(curves == null)
			curves = new ArrayList<Curve>();
		curves.add(curve);
	}	
	
	@Override
	protected void setViewport(double startX, double stopX) {
		if(startX != viewportMin || stopX != viewportMax)
			viewportResized = true;
		viewportMax = stopX;
		viewportMin = startX;
	}

	@Override
	protected void moveViewport(double dX) {
		if(dX != 0)
			viewportChanged = true;
		viewportMin += dX;
		viewportMax += dX;
	}

	@Override
	protected void update() {
		//if no property defined yet, then use the default
		if(properties == null)
			properties = LineChartProperties.getDefaultLineChartProperties();
		
		//update model
		//if one curve's model changed we have to update extremes
		boolean updated = false;
		for(Curve curve : curves){
			//this cond enough in case of newly added curve, too
			if(curve.modelChanged){
				if(!updated){
					updateExtremes();
					updateRatios();
					updated = true;
				}
				curve.modelChanged = false;
			}
		}
		//if vp resized
		if(viewportResized){
			if(properties.precalculatePoints){
				//calc all points
				for(Curve curve : curves){
					curve.calculatePoints(curve.xMin-1, curve.xMax+1, false, this);
					curve.uncalculatedPoints.clear();
				}
			}
			else{
				//calc visible points
				for(Curve curve : curves){
					curve.calculatePoints(viewportMin, viewportMax, false, this);
					curve.updateUncalculatedPoints(viewportMin, viewportMax);
				}
			}
			for(Curve curve : curves){
				curve.updateVisiblePoints(viewportMin, viewportMax, properties.overlapFilterDistance);
			}
			viewportResized = false;
		}
		//no vp resize
		else{
			//viewport moved
			if(viewportChanged){
				//if precalculatePoints is true we dont have to calc
				if(!properties.precalculatePoints){
					for(Curve curve : curves){
						curve.calculatePoints(viewportMin,viewportMax, true, this);
					}
				}	
				for(Curve curve : curves){
					curve.updateVisiblePoints(viewportMin, viewportMax, properties.overlapFilterDistance);
				}
				viewportChanged = false;
			}
		}
		
		//update graphics
		for(Curve curve : curves){
			curveToGOs(curve);
		}		
	}
	
	protected void updateExtremes(){
		xMax = xMin = y2Max = y2Min = yMin = yMax = 0;
		for(Curve curve : curves){
			if(xMin == xMax && xMax == 0){
				xMax = curve.xMax;
				xMin = curve.xMin;
			}
			else{
				if(xMax < curve.xMax)
					xMax = curve.xMax;
				if(xMin > curve.xMin)
					xMin = curve.xMin;
			}
			if(curve.getyAxis() == AxisType.Y){
				if(yMin == yMax && yMax == 0){
					yMax = curve.yMax;
					yMin = curve.yMin;
				}
				else{
					if(yMax < curve.yMax)
						yMax = curve.yMax;
					if(yMin > curve.yMin)
						yMin = curve.yMin;
				}
			}
			else if(curve.getyAxis() == AxisType.Y2){
				if(y2Min == y2Max  && y2Max == 0){
					y2Max = curve.yMax;
					y2Min = curve.yMin;
				}
				else{
					if(y2Max < curve.yMax)
						y2Max = curve.yMax;
					if(y2Min > curve.yMin)
						y2Min = curve.yMin;
				}
			}
		}
	}
	
	protected void updateRatios(){
		xRatio  = canvas.getCanvas().getWidth() / (viewportMax - viewportMin);
		yRatio = (canvas.getCanvas().getHeight() - properties.topPadding ) / (yMax - yMin);
		y2Ratio = (canvas.getCanvas().getHeight() - properties.topPadding ) / (y2Max - y2Min);
	}
	
	void calculatePoint(Point point, AxisType axis){
		point.setPosX((int) (xRatio * (point.getDataX() - xMin)));
		if(axis == AxisType.Y){
			point.setPosY((int) (yRatio * (yMax - point.getDataY())) + properties.topPadding);
		}
		else if(axis == AxisType.Y2){
			point.setPosY((int) (y2Ratio * (y2Max - point.getDataY())) + properties.topPadding);
		} 
	}
	
	protected void curveToGOs(Curve curve){
		GraphicalObjectContainer gos = new  GraphicalObjectContainer();
		
		//linechart
		Path path = curve.getVisiblePath();
		if(path == null)
			return;
		if(curve.getLineProperties() != null){
			Path line = new Path(path);
			line.setContext(createLineContext(curve));
			line.setStroke(true);
			line.setzIndex(curve.getZIndex());
			if(curve.getLineProperties().getStyle().equals(LineStyle.DASHED))
				gos.addGraphicalObject(DrawingArea.createDashedLinePath(line, curve.getLineProperties().getDashStrokeLength(), curve.getLineProperties().getDashDistance()));
			else
				gos.addGraphicalObject(line);
		}
		if(curve.toCanvasYFills != null && curve.toCanvasYFills.size() > 0){
			for(int i : curve.toCanvasYFills.keySet()){
				Path fill = new Path(path);
				fill.lineTo(fill.getLastPathElement().getEndPointX(), i, false);
				fill.lineTo(fill.getBasePointX(), i, false);
				fill.lineToBasePoint();
				fill.setFill(true);
				fill.setzIndex(curve.getZIndex());
				fill.setContext(createFillContext(curve.toCanvasYFills.get(i)));
				gos.addGraphicalObject(fill);
			}
		}
		if(curve.toCurveFills != null && curve.toCurveFills.size() > 0){
			for(Curve toCurve : curve.toCurveFills.keySet()){
				Path fill = new Path(path);
				Path otherPath = toCurve.getVisiblePath();
				if(otherPath == null)
					continue;
				for(int i = otherPath.getPathElements().size()-1; i >= 0; i--){
					fill.getPathElements().add(otherPath.getPathElements().get(i));
				}
				fill.lineToBasePoint();
				fill.setzIndex(curve.getZIndex());
				fill.setContext(createFillContext(curve.toCurveFills.get(toCurve)));
				fill.setFill(true);
				gos.addGraphicalObject(fill);
			}
		}
		if(curve.toYFills != null && curve.toYFills.size() > 0){
			for(double i : curve.toYFills.keySet()){
				Path fill = new Path(path);
				Point tmp = new Point(0, i);
				calculatePoint(tmp, curve.yAxis);
				fill.lineTo(fill.getLastPathElement().getEndPointX(), tmp.getPosY(), false);
				fill.lineTo(fill.getBasePointX(), tmp.getPosY(), false);
				fill.lineToBasePoint();
				fill.setFill(true);
				fill.setzIndex(curve.getZIndex());
				fill.setContext(createFillContext(curve.toYFills.get(i)));
				gos.addGraphicalObject(fill);
			}
		}
		ArrayList<Point> visiblePoints = curve.getVisiblePoints();
		if(visiblePoints != null && visiblePoints.size() > 0){
			//if the point can be selected if mouseOver we register the points
			// as interactive GOs
			if(properties.pointSelectionMode == PointSelectionMode.On_Point_Click ||
					properties.pointSelectionMode == PointSelectionMode.On_Point_Over){
				for(Point point : visiblePoints){
					if(curve.normalPointShape != null){
						this.interactivePoints.put(curve.normalPointShape.toInterActiveGraphicalObject(), point);
						//TODO
						//gos.addGraphicalObject(curve.normalPointShape.toGraphicalObjects());
					}
					else if(curve.selectedPointShape != null){
						
					}
				}
			}
		}
	}

	static Context createFillContext(Color fillColor){
		return new Context(
				fillColor.getAlpha(),
				fillColor.getColor(),
				0,
				Color.DEFAULT_COLOR,
				0,
				0,
				0,
				Color.DEFAULT_COLOR);
	}

	static Context createLineContext(Curve curve){
		return new Context(
				curve.lineProperties.getLineColor().getAlpha(),
				curve.lineProperties.getLineColor().getColor(),
				curve.lineProperties.getLineWidth(),
				Color.DEFAULT_COLOR,
				curve.shadowOffsetX,
				curve.shadowOffsetY,
				curve.shadowColor == null ? Color.DEFAULT_ALPHA : curve.shadowColor.getAlpha(),
				curve.shadowColor == null ? Color.DEFAULT_COLOR : curve.shadowColor.getColor());
	}
	
	/**
	 * @return the xAxis
	 */
	public Axis getxAxis() {
		return xAxis;
	}
	
	/**
	 * @param xAxis the xAxis to set
	 */
	public void setxAxis(Axis xAxis) {
		this.xAxis = xAxis;
	}

	/**
	 * @return the yAxis
	 */
	public Axis getyAxis() {
		return yAxis;
	}

	/**
	 * @param yAxis the yAxis to set
	 */
	public void setyAxis(Axis yAxis) {
		this.yAxis = yAxis;
	}

	/**
	 * @return the y2Axis
	 */
	public Axis getY2Axis() {
		return y2Axis;
	}

	/**
	 * @param y2Axis the y2Axis to set
	 */
	public void setY2Axis(Axis y2Axis) {
		this.y2Axis = y2Axis;
	}

	/**
	 * @return the properties
	 */
	public LineChartProperties getProperties() {
		return properties;
	}
	
	/**
	 * 
	 * @param properties
	 */
	public void setProperties(LineChartProperties properties){
		this.properties = properties;
	}

	@Override
	public void onMouseClick(ArrayList<GraphicalObject> sourceGOs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseOver(ArrayList<GraphicalObject> sourceGOs) {
		// TODO Auto-generated method stub
		
	}
}
