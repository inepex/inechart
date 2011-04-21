package com.inepex.inecharting.chartwidget.newimpl.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sourceforge.htmlunit.corejs.javascript.ast.Yield;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.inepex.inecharting.chartwidget.newimpl.IneChartModul;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axes;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axis;
import com.inepex.inecharting.chartwidget.newimpl.axes.Axis.AxisType;
import com.inepex.inecharting.chartwidget.newimpl.linechart.LineChartProperties.PointSelectionMode;
import com.inepex.inecharting.chartwidget.newimpl.properties.Color;
import com.inepex.inecharting.chartwidget.newimpl.properties.LineProperties.LineStyle;
import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;
import com.inepex.inegraphics.impl.client.GraphicalObjectEventHandler;
import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;

public class LineChart extends IneChartModul implements GraphicalObjectEventHandler, MouseMoveHandler{
	
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
	
	//interactivity and graphicalobjects
	/**
	 * A collection containing mouseOver related points, whose implements {@link InteractiveGraphicalObject}
	 */
	TreeMap<GraphicalObject, Point> interactivePoints = new TreeMap<GraphicalObject, Point>();
	/**
	 * should contain all of the selected-state points (outside the actual vp too)
	 */
	ArrayList<Point> selectedPoints = new ArrayList<Point>();
	/**
	 * all gos per curve (should not contain gos of points)
	 */
	TreeMap<Curve, GraphicalObjectContainer> gosPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
	/**
	 * all gos per point, should contain only points inside vp!
	 */
	TreeMap<Curve, TreeMap<Point, GraphicalObjectContainer>> gosPerPoint = new  TreeMap<Curve, TreeMap<Point,GraphicalObjectContainer>>();
	
	
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
		for(Curve curve : curves){
			//this cond enough in case of newly added curve, too
			if(curve.modelChanged){
				updateExtremes();
				updateRatios();
				break;
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
			}
		}
		
		//update graphics
		for(Curve curve : curves){
			curveToGOs(curve);
		}		
		//reset indicator fields
		viewportChanged = false;
		viewportResized = false;
		
		for(Curve curve : curves){
			curve.modelChanged = false;
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
		xRatio  = canvas.getWidth() / (viewportMax - viewportMin);
		yRatio = (canvas.getHeight() - properties.topPadding)  / (yMax - yMin);
		y2Ratio = (canvas.getHeight() - properties.topPadding)  / (y2Max - y2Min);
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
		boolean change = false;
		/* LINECHART */
		//if no change in vp and point we should not update linechart gobjects
		if(viewportChanged || viewportResized || curve.modelChanged || !gosPerCurve.containsKey(curve)){
			GraphicalObjectContainer gos = new  GraphicalObjectContainer();
			Path path = curve.getVisiblePath(-getViewportMinInPX());
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
					Path otherPath = toCurve.getVisiblePath(-getViewportMinInPX());
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
			
			this.gosPerCurve.put(curve, gos);
			change = true;
			
		}
		
		/* POINTCHART */
		//if no shape defined for neither state, it means its only a linechart 
		if(curve.normalPointShape != null || curve.selectedPointShape != null){
			TreeMap<Point, GraphicalObjectContainer> pointsGOs = new TreeMap<Point, GraphicalObjectContainer>();
			ArrayList<Point> visiblePoints = curve.getVisiblePoints();
			//if vp or model changed we clear our related container
			if(viewportChanged || viewportResized || curve.modelChanged || !gosPerPoint.containsKey(curve)){
				this.gosPerPoint.put(curve, pointsGOs);
				this.interactivePoints.clear();
			}
			
			//check if we got displayable points
			if(visiblePoints != null && visiblePoints.size() > 0 && gosPerPoint.get(curve).size() == 0){
				//if the point can be selected via mouseOver, then we register as interactive GO
				boolean mouseOverRelatedSelection = properties.pointSelectionMode == PointSelectionMode.On_Point_Click ||
						properties.pointSelectionMode == PointSelectionMode.On_Point_Over;				
				for(Point point : visiblePoints){
					createGOsForPoint(point, mouseOverRelatedSelection);
				}
			}
			change = true;
		}
		if(change){
			graphicalObjectContainer.removeAllGraphicalObject();
			for(GraphicalObject go : gosPerCurve.get(curve).getGraphicalObjects()){
				graphicalObjectContainer.addGraphicalObject(go);
			}
			for(Point point : gosPerPoint.get(curve).keySet()){
				for(GraphicalObject go : gosPerPoint.get(curve).get(point).getGraphicalObjects()){
					graphicalObjectContainer.addGraphicalObject(go);
				}
			}
		}
		
	}
	
	void createGOsForPoint(Point point, boolean mouseOverRelatedSelection){
		//selected state
		GraphicalObjectContainer gocForPoint = new GraphicalObjectContainer(); 
		if(selectedPoints.contains(point)){
			//if selectedPointShape is null we do not draw
			if(point.parent.selectedPointShape != null){
				ArrayList<GraphicalObject> gosOfPoint = point.parent.selectedPointShape.toGraphicalObjects();
				//set the proper pos for gos
				for(GraphicalObject go : gosOfPoint){
					go.setBasePointX(getCanvasXForPoint(point));
					go.setBasePointY(point.getPosY());
					gocForPoint.addGraphicalObject(go);
				}
				if(mouseOverRelatedSelection){
					GraphicalObject interactivePoint = point.parent.selectedPointShape.toInterActiveGraphicalObject();
					interactivePoints.put(interactivePoint, point);
				}
			}
		}
		//normal state
		else{
			if(point.parent.normalPointShape != null){
				ArrayList<GraphicalObject> gosOfPoint = point.parent.normalPointShape.toGraphicalObjects();
				//set the proper pos for gos
				for(GraphicalObject go : gosOfPoint){
					go.setBasePointX(getCanvasXForPoint(point));
					go.setBasePointY(point.getPosY());
					gocForPoint.addGraphicalObject(go);
				}
				if(mouseOverRelatedSelection){
					GraphicalObject interactivePoint = point.parent.selectedPointShape.toInterActiveGraphicalObject();
					interactivePoints.put(interactivePoint, point);
				}
			}
			//if we do not hace normal-state point but the points get selected via mouseOver
			//we should add a transparent interactiveGO to recieve mouseEvents
			else if(mouseOverRelatedSelection && point.parent.selectedPointShape != null){
				GraphicalObject interactivePoint = point.parent.selectedPointShape.toInterActiveGraphicalObject();
				interactivePoint.setBasePointX(getCanvasXForPoint(point));
				interactivePoint.setBasePointY(point.getPosY());
				//make it transparent
				interactivePoint.getContext().setAlpha(0d);
				interactivePoints.put(interactivePoint, point);
			}
		}
		if(!gosPerPoint.containsKey(point.parent))
			gosPerPoint.put(point.parent, new TreeMap<Point, GraphicalObjectContainer>());
		gosPerPoint.get(point.parent).put(point, gocForPoint);
	}

	int getCanvasXForPoint(Point point){
		return point.getPosX() - getViewportMinInPX(); 
	}
	
	int getViewportMinInPX(){
		return (int) ((viewportMin - xMin) * xRatio);
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
		if(properties.pointSelectionMode == PointSelectionMode.On_Point_Click){
			for(GraphicalObject go : sourceGOs){
				Point point = interactivePoints.get(go);
				selectedPoints.add(point);
				interactivePoints.remove(go);
				createGOsForPoint(point, true);
			}
		}
	}

	@Override
	public void onMouseMove(ArrayList<GraphicalObject> mouseOver, ArrayList<GraphicalObject> mouseOut) {
		if(properties.pointSelectionMode == PointSelectionMode.On_Point_Over){
			for(GraphicalObject go : mouseOver){
				Point point = interactivePoints.get(go);
				selectedPoints.add(point);
				interactivePoints.remove(go);
				createGOsForPoint(point, true);
				redrawNeeded = true;
			}
			for(GraphicalObject go : mouseOut){
				Point point = interactivePoints.get(go);
				selectedPoints.remove(point);
				interactivePoints.remove(go);
				createGOsForPoint(point, true);
				redrawNeeded = true;
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(properties.pointSelectionMode == PointSelectionMode.Closest_To_Cursor){
			ArrayList<Point> actualSelection = new ArrayList<Point>();
			final int mouseX = event.getRelativeX(((DrawingAreaImplCanvas) this.canvas).getCanvas().getElement());
			for(Curve curve : curves){
				int d = Integer.MAX_VALUE;
				int actualD;
				Point last = null;
				for(Point point : curve.getVisiblePoints()){
					actualD = Math.abs(getCanvasXForPoint(point) - mouseX);
					if(d < actualD){
						d = actualD;
					}
					else{
						actualSelection.add(last);
						break;
					}
					last = point;
				}
			}
			for(Point p : actualSelection){
				if(!selectedPoints.contains(p)){
					createGOsForPoint(p, false);
					redrawNeeded = true;
				}
			}
			for(Point p : selectedPoints){
				if(!actualSelection.contains(p)){
					createGOsForPoint(p, false);
					redrawNeeded = true;
				}
			}
			selectedPoints = actualSelection;
		}			
	}


	
	
	
	
}
