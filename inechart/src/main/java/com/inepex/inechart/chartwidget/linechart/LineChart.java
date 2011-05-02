package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.axes.Axis.AxisType;
import com.inepex.inechart.chartwidget.linechart.LineChartProperties.PointSelectionMode;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.LineProperties.LineStyle;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inegraphics.impl.client.DrawingAreaImplCanvas;
import com.inepex.inegraphics.impl.client.GraphicalObjectEventHandler;
import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;

public class LineChart extends IneChartModul implements GraphicalObjectEventHandler, MouseMoveHandler, MouseOutHandler{
	
	LineChartProperties properties = null;

	//model fields
	ArrayList<Curve> curves;
	Axis xAxis;
	Axis yAxis;
	Axis y2Axis;
	Axes axes;
	double yMax, y2Max, xMax, yMin, y2Min, xMin, yRatio, y2Ratio, xRatio;
	int highestZIndex = 1;
	final int DEFAULT_VERTICAL_TICK_DISTANCE = 50;
	final int DEFAULT_HORIZONTAL_TICK_DISTANCE = 20;
	
	
	
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
		
	ColorSet colors = new ColorSet();
	
	public LineChart(DrawingArea canvas, Axes axes) {
		super(canvas);
		if (canvas instanceof DrawingAreaImplCanvas)
			((DrawingAreaImplCanvas)canvas).addGraphicalObjectEventHandler(this);
		this.axes = axes;
	}

	public void calculateAxes(){
		if(xAxis != null){
			axes.removeAxis(xAxis);
		}
		xAxis = new Axis(LineProperties.getDefaultSolidLine());
		xAxis.setType(AxisType.X);
		axes.addAxis(xAxis);
		//TODO
//		double x = xMin;
//		for(int i=0;i<20;i++){
//			xAxis.addTick(new Tick(x, new LineProperties(1, new Color("grey")), LineProperties.getDefaultSolidLine(), 5, x+""));
//			x += (xMax - xMin) / 20;
//		}
		if(yAxis != null){
			axes.removeAxis(yAxis);
		}
		yAxis = new Axis(LineProperties.getDefaultSolidLine());
		yAxis.setType(AxisType.Y);
		yAxis.setMin(yMin);
		yAxis.setMax(yMax);
		axes.addAxis(yAxis);
		//TODO
//		double y = yMin;
//		for(int i=0;i<20;i++){
//			yAxis.addTick(new Tick(y, null, new LineProperties(2, new Color("red")), 3, y+""));
//			y += (yMax - yMin) / 20;
//		}
		if(y2Axis != null){
			axes.removeAxis(y2Axis);			
		}
	}

	public void addCurve(Curve curve) {
		if(curves == null)
			curves = new ArrayList<Curve>();
		if (curve.getLineProperties().getLineColor() == null) curve.getLineProperties().setLineColor(new Color(colors.getNextColor()));
		curves.add(curve);
		if(curve.zIndex == Integer.MIN_VALUE)
			curve.zIndex = ++highestZIndex;
		else if(curve.zIndex > highestZIndex)
			highestZIndex = curve.zIndex;
		
	}	
	
	@Override
	public void update() {
		//if no property defined yet, then use the default
		if(properties == null)
			properties = LineChartProperties.getDefaultLineChartProperties();
		
		this.leftPadding = properties.getLeftPadding();
		if (axes != null)
			axes.setLeftPadding(properties.getLeftPadding());
		
		if (properties.isAutoCalcViewport()){
			double max = 0.0;
			for(Curve curve : curves){
				if (curve.getxMax() > max) max = curve.getxMax();
			}
			setViewport(0, max);
			if (axes != null) axes.setViewport(0, max);
		}
		
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
			if(viewportMoved){
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
		viewportMoved = false;
		viewportResized = false;
		redrawNeeded = false;
		
		for(Curve curve : curves){
			curve.modelChanged = false;
		}
		
	}
	
	protected void updateExtremes(){
		xMax = xMin = y2Max = y2Min = yMin = yMax = 0;
		for(Curve curve : curves){
			if(curve.zIndex > highestZIndex)
				highestZIndex = curve.zIndex;
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
		if(xAxis != null){
			xAxis.setMax(xMax);
			xAxis.setMin(xMin);
		}
		if(yAxis != null){
			yAxis.setMax(yMax);
			yAxis.setMin(yMin);
		}
		if(y2Axis != null){
			y2Axis.setMax(y2Max);
			y2Axis.setMin(y2Min);
		}
	}
	
	protected void updateRatios(){
		xRatio  = (canvas.getWidth() - leftPadding - rightPadding)/ (viewportMax - viewportMin);
		yRatio = (canvas.getHeight() - properties.topPadding - topPadding - bottomPadding)  / (yMax - yMin);
		y2Ratio = (canvas.getHeight() - properties.topPadding - topPadding - bottomPadding)  / (y2Max - y2Min);
	}
	
	void calculatePoint(Point point, AxisType axis){
		point.setPosX((int) (xRatio * (point.getDataX() - xMin)) + leftPadding);
		if(axis == AxisType.Y){
			point.setPosY((int) (yRatio * (yMax - point.getDataY())) + properties.topPadding + topPadding);
		}
		else if(axis == AxisType.Y2){
			point.setPosY((int) (y2Ratio * (y2Max - point.getDataY())) + properties.topPadding + topPadding);
		} 
	}
	
	protected void curveToGOs(Curve curve){
		boolean change = false;
		/* LINECHART */
		//if no change in vp and point we should not update linechart gobjects
		if(viewportMoved || viewportResized || curve.modelChanged || !gosPerCurve.containsKey(curve)){
			GraphicalObjectContainer gos = new  GraphicalObjectContainer();
			Path path = curve.getVisiblePath(-getViewportMinInPX(), leftPadding, canvas.getWidth() - rightPadding);
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
					Path otherPath = toCurve.getVisiblePath(-getViewportMinInPX(), leftPadding, canvas.getWidth() - rightPadding);
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
			if(viewportMoved || viewportResized || curve.modelChanged || !gosPerPoint.containsKey(curve)){
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
			removeAllGORelatedToCurve(curve);
			for(GraphicalObject go : gosPerCurve.get(curve).getGraphicalObjects()){
				graphicalObjectContainer.addGraphicalObject(go);
			}
			if (gosPerPoint.get(curve) != null) {
				for(Point point : gosPerPoint.get(curve).keySet()){
					for(GraphicalObject go : gosPerPoint.get(curve).get(point).getGraphicalObjects()){
						graphicalObjectContainer.addGraphicalObject(go);
					}
				}
			}
		}
		
	}
	
	void removeAllGORelatedToCurve(Curve curve){
		for(GraphicalObject go : gosPerCurve.get(curve).getGraphicalObjects())
			graphicalObjectContainer.removeGraphicalObject(go);
		if (gosPerPoint.get(curve) != null){
			for(Point p : gosPerPoint.get(curve).keySet())
				removeAllGORelatedToPoint(p);
		}
		
	}
	
	void removeAllGORelatedToPoint(Point point){
		
		for(Point p : gosPerPoint.get(point.parent).keySet())
			for(GraphicalObject go : gosPerPoint.get(point.parent).get(p).getGraphicalObjects())
				graphicalObjectContainer.removeGraphicalObject(go);
		
	}
	
	void createGOsForPoint(Point point, boolean mouseOverRelatedSelection){
		if(point == null)
			return;
		//selected state
		GraphicalObjectContainer gocForPoint = new GraphicalObjectContainer(); 
		if(selectedPoints.contains(point)){
			//if selectedPointShape is null we do not draw
			if(point.parent.selectedPointShape != null){
				ArrayList<GraphicalObject> gosOfPoint = point.parent.selectedPointShape.toGraphicalObjects();
				//set the proper pos for gos
				for(GraphicalObject go : gosOfPoint){
					if(point.parent.selectedPointShape instanceof Circle){
						go.setBasePointX(getCanvasXForPoint(point));
						go.setBasePointY(point.getPosY());
						go.setzIndex(point.parent.zIndex);
					}
					else if(point.parent.selectedPointShape instanceof Rectangle){
						go.setBasePointX(getCanvasXForPoint(point) - ((Rectangle)point.parent.selectedPointShape).getWidth()/2);
						go.setBasePointY(point.getPosY() - ((Rectangle)point.parent.selectedPointShape).getHeight()/2);
						go.setzIndex(point.parent.zIndex);
					}
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
					if(point.parent.normalPointShape instanceof Circle){
						go.setBasePointX(getCanvasXForPoint(point));
						go.setBasePointY(point.getPosY());
						go.setzIndex(point.parent.zIndex);
					}
					else if(point.parent.normalPointShape instanceof Rectangle){
						go.setBasePointX(getCanvasXForPoint(point) - ((Rectangle)point.parent.normalPointShape).getWidth()/2);
						go.setBasePointY(point.getPosY() - ((Rectangle)point.parent.normalPointShape).getHeight()/2);
						go.setzIndex(point.parent.zIndex);
					}
					gocForPoint.addGraphicalObject(go);
				}
				if(mouseOverRelatedSelection){
					GraphicalObject interactivePoint = point.parent.selectedPointShape.toInterActiveGraphicalObject();
					interactivePoints.put(interactivePoint, point);
				}
			}
			//if we do not have normal-state point but the points get selected via mouseOver
			//we should add a transparent interactiveGO to recieve mouseEvents
			else if(mouseOverRelatedSelection && point.parent.selectedPointShape != null){
				GraphicalObject interactivePoint = point.parent.selectedPointShape.toInterActiveGraphicalObject();
				if(point.parent.selectedPointShape instanceof Circle){
					interactivePoint.setBasePointX(getCanvasXForPoint(point));
					interactivePoint.setBasePointY(point.getPosY());
					interactivePoint.setzIndex(point.parent.zIndex);
				}
				else if(point.parent.selectedPointShape instanceof Rectangle){
					interactivePoint.setBasePointX(getCanvasXForPoint(point) - ((Rectangle)point.parent.selectedPointShape).getWidth()/2);
					interactivePoint.setBasePointY(point.getPosY() - ((Rectangle)point.parent.selectedPointShape).getHeight()/2);
					interactivePoint.setzIndex(point.parent.zIndex);
				}
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
		return (int) ((viewportMin - xMin) * xRatio + leftPadding);
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
		if (canvas instanceof DrawingAreaImplCanvas) {
			if(properties.pointSelectionMode == PointSelectionMode.Closest_To_Cursor){
				((DrawingAreaImplCanvas) canvas).addMouseMoveHandler(this);
				((DrawingAreaImplCanvas) canvas).addMouseOutHandler(this);
			}
			else
				((DrawingAreaImplCanvas) canvas).addGraphicalObjectEventHandler(this);
		}
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
					if(d > actualD){
						d = actualD;
					}
					else {
						break;
					}
					last = point;
				}
				if(last != null)
					actualSelection.add(last);
			}
			ArrayList<Point> stateChangedPoints = new ArrayList<Point>();
			for(Point p : actualSelection){
				if(!selectedPoints.contains(p)){
					stateChangedPoints.add(p);
					removeAllGORelatedToPoint(p);
				}
			}
			for(Point p : selectedPoints){
				if(!actualSelection.contains(p)){
					stateChangedPoints.add(p);
					removeAllGORelatedToPoint(p);
				}
			}
			selectedPoints = actualSelection;
		
			
			if(stateChangedPoints.size() > 0){
				redrawNeeded = true;
				for(Point p : stateChangedPoints){
					createGOsForPoint(p, false);
				}
			}
		}			
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if(properties.pointSelectionMode == PointSelectionMode.Closest_To_Cursor){
			ArrayList<Point> stateChanged = new ArrayList<Point>();
			for(Point point : selectedPoints){
				removeAllGORelatedToPoint(point);
				stateChanged.add(point);
			}
			if(stateChanged.size() > 0){
				redrawNeeded = true;
				selectedPoints.clear();
				for(Point point : stateChanged){
					createGOsForPoint(point, false);
				}
			}
			
		}
		
	}


	
	
	
	
}
