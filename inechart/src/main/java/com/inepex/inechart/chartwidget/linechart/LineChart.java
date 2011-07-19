package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.Viewport;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.axes.Axis;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.label.LegendEntry;
import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.LineProperties.LineStyle;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.impl.client.GraphicalObjectEventHandler;
import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;

public class LineChart extends IneChartModul2D implements GraphicalObjectEventHandler, MouseMoveHandler, MouseOutHandler {

	public enum PointSelectionMode {
		/**
		 * The closest point to the cursor will be selected
		 */
		Closest_To_Cursor,
		/**
		 * The clicked point will be selected
		 */
		On_Point_Click,
		/**
		 * The mouse overed point will be selected
		 */
		On_Point_Over
	}

	//defaults
	ColorSet colors = new ColorSet();
	public static final double defaultLineWidth = 2.1;
	
	public static final double defaultShadowOffsetX = 1.2;
	public static final double defaultShadowOffsetY = 2.4;
	public static final Color defaultShadowColor =  new Color("#D8D8D8", 0.74);
	
	// model fields
	ArrayList<Curve> curves = new ArrayList<Curve>();
	int highestZIndex = 1;
	int overlapFilterDistance;
	//TODO: tengelyenkent kulon-kulon
	boolean autoCreateAxes;
	PointSelectionMode pointSelectionMode;


	// interactivity and graphicalobjects
	/**
	 * A collection containing mouseOver related points, whose implements
	 * {@link InteractiveGraphicalObject}
	 */
	TreeMap<GraphicalObject, Point> interactivePoints = new TreeMap<GraphicalObject, Point>();
	/**
	 * should contain all of the selected-state points (outside the actual vp
	 * too)
	 */
	ArrayList<Point> selectedPoints = new ArrayList<Point>();
	/**
	 * all gos per curve (should not contain gos of points)
	 */
	TreeMap<Curve, GraphicalObjectContainer> gosPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
	/**
	 * all gos per point, should contain only points inside vp!
	 */
	TreeMap<Curve, TreeMap<Point, GraphicalObjectContainer>> gosPerPoint = new TreeMap<Curve, TreeMap<Point, GraphicalObjectContainer>>();
	/**
	 * If a path is to be clipped, the final {@link GraphicalObject}'s reference
	 * is stored in this container.
	 */
	TreeMap<Path, Path> clippedPaths = new TreeMap<Path, Path>();
	

	public LineChart(DrawingArea canvas, Axes axes) {
		this(canvas, axes, new Viewport());
		autoScaleViewport = true;
	}

	public LineChart(DrawingArea canvas, Axes axes, Viewport vp) {
		super(canvas, axes, vp);
		if (canvas instanceof DrawingAreaGWT) {
			((DrawingAreaGWT) canvas).addGraphicalObjectEventHandler(this);
			((DrawingAreaGWT) canvas).addMouseMoveHandler(this);
			((DrawingAreaGWT) canvas).addMouseOutHandler(this);
		}

		// defaults
		pointSelectionMode = PointSelectionMode.Closest_To_Cursor;
		overlapFilterDistance = 0;
		autoScaleViewport = false;
	}

	public void addCurve(Curve curve) {
		if (curve == null)
			return;
		if (curves == null)
			curves = new ArrayList<Curve>();
		if (curve.getLineProperties() == null){
			curve.setLineProperties(new LineProperties(defaultLineWidth, colors
					.getNextColor()));
			curve.setShadowOffsetY(defaultShadowOffsetX);
			curve.setShadowOffsetX(defaultShadowOffsetY);
			curve.setShadowColor(defaultShadowColor);
		}
		if (curve.getLineProperties().getLineColor() == null){
			curve.getLineProperties().setLineColor(colors.getNextColor());
		}
		curves.add(curve);
		if (curve.zIndex == Integer.MIN_VALUE)
			curve.zIndex = ++highestZIndex;
		else if (curve.zIndex > highestZIndex)
			highestZIndex = curve.zIndex;
		redrawNeeded = true;
		curve.modelChanged = true;
	}

	public void removeCurve(Curve curve) {
		if (curve == null)
			return;
		curves.remove(curve);
		removeAllGORelatedToCurve(curve);
		redrawNeeded = true;
	}

	@Override
	public void updateModulsAxes() {
		if (curves == null || curves.size() == 0)
			return;
		if (autoScaleViewport) {
			double yMin = Double.MAX_VALUE;
			double yMax = -Double.MAX_VALUE;
			double xMin = Double.MAX_VALUE;
			double xMax = -Double.MAX_VALUE;
			
			for (Curve c : curves) {
				if (c.xMax > xMax)
					xMax = c.xMax;
				if (c.yMax > yMax)
					yMax = c.yMax;
				if (c.xMin < xMin)
					xMin = c.xMin;
				if (c.yMin < yMin)
					yMin = c.yMin;
			}
			viewport.set(xMin, yMin, xMax, yMax);
			autoScaleViewport = false;
		}

		// update axes and the viewport to match values
		alignViewportAndAxes();
	}
	
	@Override
	public void update() {
		if (curves == null || curves.size() == 0)
			return;
		for (Curve curve : curves) {
			if (viewport.isChanged() || curve.modelChanged) {
				curve.calculatePoints(viewport.getXMin(),
						viewport.getXMax(), this);
				curve.updateVisiblePoints(viewport.getXMin(),
						viewport.getXMax(), overlapFilterDistance);
				removeAndAddAllGO(curve);
				curve.modelChanged = false;
			}
		}

		redrawNeeded = false;
		super.update();
	}

	void calculatePoint(Point point) {
		if (xAxis.isHorizontal()) {
			point.setPosX(getX(point.getDataX(), xAxis));
			point.setPosY(getY(point.getDataY(), yAxis));
		} else {
			point.setPosX(getX(point.getDataY(), yAxis));
			point.setPosY(getY(point.getDataX(), xAxis));
		}
	}

	double getX(double value, Axis horizontalAxis) {
		double totalWidth = canvas.getWidth() - leftPadding - rightPadding;
		double visibleLength, visibleMin;
		Viewport viewport;
		viewport = this.viewport;
		visibleLength = viewport.getWidth();
		visibleMin = viewport.getXMin();
		double pos;
		if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
			pos =  ((value - visibleMin) * totalWidth / visibleLength)	+ leftPadding;
		}
		else if (horizontalAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) {
			pos = (totalWidth -  ((value - visibleMin) * totalWidth / visibleLength)) + leftPadding;
		}
		else
			return -1;
		return pos;
	}

	double getY(double value, Axis verticalAxis) {
		double totalHeight = canvas.getHeight() - topPadding - bottomPadding;
		double visibleLength, visibleMin;
		Viewport viewport;
		viewport = this.viewport;
		visibleLength = viewport.getHeight();
		visibleMin = viewport.getYMin();
		double pos;
		if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) {
			pos = ((value - visibleMin) * totalHeight / visibleLength)
					+ topPadding;
		} else if (verticalAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			pos = (totalHeight - (value - visibleMin) * totalHeight / visibleLength) + topPadding;
		} else
			return -1;
		return pos;
	}

	/**
	 * (Re)creates the {@link GraphicalObject}s for this curve and puts them
	 * into the main {@link GraphicalObjectContainer}. Any previous gos will be
	 * dropped from the related containers.
	 * 
	 * @param curve
	 */
	protected void removeAndAddAllGO(Curve curve) {
		removeAllGORelatedToCurve(curve);
		createLineChartGOs(curve);
		createPointChartGOs(curve);
		if (gosPerCurve.get(curve) != null) {
			for (GraphicalObject go : gosPerCurve.get(curve)
					.getGraphicalObjects()) {
				graphicalObjectContainer.addGraphicalObject(go);
			}
		}
		if (gosPerPoint.get(curve) != null) {
			for (Point point : gosPerPoint.get(curve).keySet()) {
				graphicalObjectContainer.addAllGraphicalObject(gosPerPoint.get(
						curve).get(point));
			}
		}
	}

	/**
	 * Creates line chart {@link GraphicalObject}s:
	 *  -lines,
	 *  -fills
	 *  and puts them into {@link #gosPerCurve} container
	 * 
	 * @param curve
	 */
	protected void createLineChartGOs(Curve curve) {
		GraphicalObjectContainer gos = new GraphicalObjectContainer();
		Path path;
		path = curve.getVisiblePath();
		if (path == null) {
			return;
		}
		if (curve.getLineProperties() != null) {
			Path line = new Path(path);
			line.setContext(createLineContext(curve));
			line.setStroke(true);
			line.setzIndex(curve.getZIndex());
			if (curve.getLineProperties().getStyle().equals(LineStyle.DASHED))
				gos.addGraphicalObject(DrawingAreaAssist.createDashedLine(line,
						curve.getLineProperties().getDashStrokeLength(), curve
								.getLineProperties().getDashDistance()));
			else
				gos.addGraphicalObject(line);
		}
		if (curve.autoFill) {
			Path fill = new Path(path);
			Point tmp = new Point(viewport.getXMin(), viewport.getYMin());
			calculatePoint(tmp);
			fill.lineTo(fill.getLastPathElement().getEndPointX(),tmp.getPosY(), false);
			fill.lineTo(fill.getBasePointX(), tmp.getPosY(), false);
			fill.lineToBasePoint();
			fill.setFill(true);
			fill.setzIndex(curve.getZIndex());
			Color c;
			if (curve.getLineProperties() != null){
				c = curve.getLineProperties().getLineColor();
			}
			else {
				c = colors.getNextColor();
			}
			c.setAlpha(0.45);
			fill.setContext(createFillContext(c));
			gos.addGraphicalObject(fill);
		}
		if (curve.toCanvasYFills != null && curve.toCanvasYFills.size() > 0) {
			// TODO axis direction
			for (int i : curve.toCanvasYFills.keySet()) {
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
		if (curve.toCurveFills != null && curve.toCurveFills.size() > 0) {
			for (Curve toCurve : curve.toCurveFills.keySet()) {
				Path fill = new Path(path.getBasePointX(),
						path.getBasePointY(), curve.getZIndex(),
						createFillContext(curve.toCurveFills.get(toCurve)),
						false, true);
				Path otherPath = toCurve.getVisiblePath(leftPadding, topPadding,
							canvas.getWidth() - leftPadding - rightPadding,
							canvas.getHeight() - topPadding - bottomPadding);
				
				if (otherPath == null)
					continue;
				for (PathElement e : path.getPathElements()) {
					fill.lineTo(e.getEndPointX(), e.getEndPointY(), false);
				}
				for (int i = otherPath.getPathElements().size() - 1; i >= 0; i--) {
					PathElement e = otherPath.getPathElements().get(i);
					fill.lineTo(e.getEndPointX(), e.getEndPointY(), false);
				}
				fill.lineTo(otherPath.getBasePointX(),
						otherPath.getBasePointY(), false);
				fill.lineToBasePoint();
				gos.addGraphicalObject(fill);
			}
		}
		if (curve.toYFills != null && curve.toYFills.size() > 0) {
			for (double i : curve.toYFills.keySet()) {
				Path fill = new Path(path);
				Point tmp = new Point(0, i);
				calculatePoint(tmp);
				fill.lineTo(fill.getLastPathElement().getEndPointX(),
						tmp.getPosY(), false);
				fill.lineTo(fill.getBasePointX(), tmp.getPosY(), false);
				fill.lineToBasePoint();
				fill.setFill(true);
				fill.setzIndex(curve.getZIndex());
				fill.setContext(createFillContext(curve.toYFills.get(i)));
				gos.addGraphicalObject(fill);
			}
		}
		gosPerCurve.put(curve, gos);
	}

	/**
	 * Creates point chart {@link GraphicalObject}s and puts them into
	 * {@link #gosPerPoint} container
	 * 
	 * @param curve
	 */
	protected void createPointChartGOs(Curve curve) {
		// if the curve has any shape defined for pointChart
		if (curve.normalPointShape != null || curve.selectedPointShape != null || curve.isUseDefaultPointShape()) {
			if (!gosPerPoint.containsKey(curve)) {
				TreeMap<Point, GraphicalObjectContainer> pointsGOs = new TreeMap<Point, GraphicalObjectContainer>();
				gosPerPoint.put(curve, pointsGOs);
			}
			ArrayList<Point> visiblePoints = curve.getVisiblePoints(
					viewport.getYMin(), viewport.getYMax());
			// check if we got displayable points
			if (visiblePoints != null && visiblePoints.size() > 0) {
				// if the point can be selected via mouseOver, then we register
				// as interactive GO
				boolean mouseOverRelatedSelection = pointSelectionMode == PointSelectionMode.On_Point_Click
						|| pointSelectionMode == PointSelectionMode.On_Point_Over;
				for (Point point : visiblePoints) {
					gosPerPoint.get(curve)
							.put(point,
									createGOsForPoint(point,
											mouseOverRelatedSelection));
				}
			}
		}
	}

	/**
	 * Removes all {@link GraphicalObject} from this modul's
	 * {@link GraphicalObjectContainer} based on {@link #gosPerCurve} and
	 * {@link #gosPerPoint} containers.
	 * 
	 * @param curve
	 */
	protected void removeAllGORelatedToCurve(Curve curve) {
		if (gosPerCurve.containsKey(curve)) {
			for (GraphicalObject go : gosPerCurve.get(curve)
					.getGraphicalObjects()) {
				graphicalObjectContainer.removeGraphicalObject(go);
				if (clippedPaths.containsKey(go)) {
					graphicalObjectContainer.removeGraphicalObject(clippedPaths
							.get(go));
					clippedPaths.remove(go);
				}
			}
		}
		if (gosPerPoint.containsKey(curve) && gosPerPoint.get(curve) != null) {
			for (Point p : gosPerPoint.get(curve).keySet()) {
				removeAllGORelatedToPoint(p);
			}
			gosPerPoint.get(curve).clear();
		}
	}

	protected void removeAllGORelatedToPoint(Point point) {
		for (GraphicalObject go : gosPerPoint.get(point.parent).get(point)
				.getGraphicalObjects())
			graphicalObjectContainer.removeGraphicalObject(go);
		gosPerPoint.get(point.parent).get(point).removeAllGraphicalObject();
		GraphicalObject igo = null;
		for (GraphicalObject go : interactivePoints.keySet()) {
			if (interactivePoints.get(go).equals(point)) {
				igo = go;
				// max 1 igo present per point
				break;
			}
		}
		if (igo != null)
			interactivePoints.remove(igo);
	}

	GraphicalObjectContainer createGOsForPoint(Point point,
			boolean mouseOverRelatedSelection) {
		GraphicalObjectContainer gocForPoint = new GraphicalObjectContainer();
		if (point == null)
			return gocForPoint;
		if(point.parent.useDefaultPointShape){
			if(point.parent.hasShadow){
				point.parent.defaultPointShape.setShadowColor(point.parent.shadowColor);
				point.parent.defaultPointShape.setShadowOffsetX(point.parent.shadowOffsetX);
				point.parent.defaultPointShape.setShadowOffsetY(point.parent.shadowOffsetY);
			}
			if(point.parent.lineProperties != null){
				ShapeProperties sp = new ShapeProperties(point.parent.lineProperties);
				sp.setFillColor(sp.getLineProperties().getLineColor());
				sp.getFillColor().setAlpha(1);
				sp.getLineProperties().getLineColor().setAlpha(1);
				sp.getLineProperties().setLineWidth(0);
				point.parent.defaultPointShape.setProperties(sp);
			}
			// set the proper pos for gos
			for (GraphicalObject go : point.parent.defaultPointShape.toGraphicalObjects()) {
				if (point.parent.defaultPointShape instanceof Circle) {
					go.setBasePointX(point.getPosX());
					go.setBasePointY(point.getPosY());
					go.setzIndex(point.parent.zIndex);
				} else if (point.parent.defaultPointShape instanceof Rectangle) {
					go.setBasePointX(point.getPosX()
							- ((Rectangle) point.parent.defaultPointShape)
									.getWidth() / 2);
					go.setBasePointY(point.getPosY()
							- ((Rectangle) point.parent.defaultPointShape)
									.getHeight() / 2);
					go.setzIndex(point.parent.zIndex);
				}
				gocForPoint.addGraphicalObject(go);
			}
		}
		else {
			if (point.parent.setCurveShadowForPoint) {
				if (point.parent.selectedPointShape != null) {
					point.parent.selectedPointShape
							.setShadowColor(point.parent.shadowColor);
					point.parent.selectedPointShape
							.setShadowOffsetX(point.parent.shadowOffsetX);
					point.parent.selectedPointShape
							.setShadowOffsetY(point.parent.shadowOffsetY);
				}
				if (point.parent.normalPointShape != null) {
					point.parent.normalPointShape
							.setShadowColor(point.parent.shadowColor);
					point.parent.normalPointShape
							.setShadowOffsetX(point.parent.shadowOffsetX);
					point.parent.normalPointShape
							.setShadowOffsetY(point.parent.shadowOffsetY);
				}
			}
			// selected state
			if (selectedPoints.contains(point)) {
				// if selectedPointShape is null we do not draw
				if (point.parent.selectedPointShape != null) {
					ArrayList<GraphicalObject> gosOfPoint = point.parent.selectedPointShape
							.toGraphicalObjects();
					// set the proper pos for gos
					for (GraphicalObject go : gosOfPoint) {
						if (point.parent.selectedPointShape instanceof Circle) {
							go.setBasePointX(point.getPosX());
							go.setBasePointY(point.getPosY());
							go.setzIndex(point.parent.zIndex);
						} else if (point.parent.selectedPointShape instanceof Rectangle) {
							go.setBasePointX(point.getPosX()
									- ((Rectangle) point.parent.selectedPointShape)
											.getWidth() / 2);
							go.setBasePointY(point.getPosY()
									- ((Rectangle) point.parent.selectedPointShape)
											.getHeight() / 2);
							go.setzIndex(point.parent.zIndex);
						}
						gocForPoint.addGraphicalObject(go);
					}
					if (mouseOverRelatedSelection
							&& canvas instanceof DrawingAreaGWT) {
						GraphicalObject interactivePoint = point.parent.selectedPointShape
								.toInterActiveGraphicalObject();
						interactivePoints.put(interactivePoint, point);
						((DrawingAreaGWT) canvas)
								.addInteractiveGO(interactivePoint);
					}
				}
			}
			// normal state
			else {
				if (point.parent.normalPointShape != null) {
					ArrayList<GraphicalObject> gosOfPoint = point.parent.normalPointShape
							.toGraphicalObjects();
					// set the proper pos for gos
					for (GraphicalObject go : gosOfPoint) {
						if (point.parent.normalPointShape instanceof Circle) {
							go.setBasePointX(point.getPosX());
							go.setBasePointY(point.getPosY());
							go.setzIndex(point.parent.zIndex);
						} else if (point.parent.normalPointShape instanceof Rectangle) {
							go.setBasePointX(point.getPosX()
									- ((Rectangle) point.parent.normalPointShape)
											.getWidth() / 2);
							go.setBasePointY(point.getPosY()
									- ((Rectangle) point.parent.normalPointShape)
											.getHeight() / 2);
							go.setzIndex(point.parent.zIndex);
						}
						gocForPoint.addGraphicalObject(go);
					}
					if (mouseOverRelatedSelection
							&& canvas instanceof DrawingAreaGWT) {
						GraphicalObject interactivePoint = point.parent.normalPointShape
								.toInterActiveGraphicalObject();
						interactivePoints.put(interactivePoint, point);
						((DrawingAreaGWT) canvas)
								.addInteractiveGO(interactivePoint);
					}
				}
				// if we do not have normal-state point but the points get selected
				// via mouseOver
				// we should add a transparent interactiveGO to recieve mouseEvents
				else if (mouseOverRelatedSelection
						&& point.parent.selectedPointShape != null) {
					GraphicalObject interactivePoint = point.parent.selectedPointShape
							.toInterActiveGraphicalObject();
					if (point.parent.selectedPointShape instanceof Circle) {
						interactivePoint.setBasePointX(point.getPosX());
						interactivePoint.setBasePointY(point.getPosY());
						interactivePoint.setzIndex(point.parent.zIndex);
					} else if (point.parent.selectedPointShape instanceof Rectangle) {
						interactivePoint.setBasePointX(point.getPosX()
								- ((Rectangle) point.parent.selectedPointShape)
										.getWidth() / 2);
						interactivePoint.setBasePointY(point.getPosY()
								- ((Rectangle) point.parent.selectedPointShape)
										.getHeight() / 2);
						interactivePoint.setzIndex(point.parent.zIndex);
					}
					interactivePoints.put(interactivePoint, point);
					((DrawingAreaGWT) canvas).addInteractiveGO(interactivePoint);
				}
			}
		}
		return gocForPoint;
	}

	static Context createFillContext(Color fillColor) {
		return new Context(fillColor.getAlpha(), Defaults.colorString, 0,
				fillColor.getColor(), 0, 0, Defaults.alpha,
				Defaults.colorString);
	}

	static Context createLineContext(Curve curve) {
		return new Context(
				curve.lineProperties.getLineColor().getAlpha(),
				curve.lineProperties.getLineColor().getColor(),
				curve.lineProperties.getLineWidth(),
				Defaults.colorString,
				curve.hasShadow ? curve.shadowOffsetX : 0,
				curve.hasShadow ? curve.shadowOffsetY : 0,
				curve.shadowColor == null ? Defaults.alpha
						: curve.shadowColor.getAlpha(),
				curve.shadowColor == null ? Defaults.colorString
						: curve.shadowColor.getColor());
	}

	@Override
	public void onMouseClick(ArrayList<GraphicalObject> sourceGOs) {
		if (canHandleEvents
				&& pointSelectionMode == PointSelectionMode.On_Point_Click) {
			for (GraphicalObject go : sourceGOs) {
				Point point = interactivePoints.get(go);
				selectedPoints.add(point);
				interactivePoints.remove(go);
				createGOsForPoint(point, true);
			}
		}
	}

	@Override
	public void onMouseMove(ArrayList<GraphicalObject> mouseOver,
			ArrayList<GraphicalObject> mouseOut) {
		if (canHandleEvents
				&& pointSelectionMode == PointSelectionMode.On_Point_Over) {
			for (GraphicalObject go : mouseOver) {
				Point point = interactivePoints.get(go);
				selectedPoints.add(point);
				interactivePoints.remove(go);
				createGOsForPoint(point, true);
				redrawNeeded = true;
			}
			for (GraphicalObject go : mouseOut) {
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
		if (canHandleEvents
				&& pointSelectionMode == PointSelectionMode.Closest_To_Cursor) {
			ArrayList<Point> actualSelection = new ArrayList<Point>();
			final int mouseX = event
					.getRelativeX(((DrawingAreaGWT) this.canvas)
							.getCanvasWidget().getElement());
			for (Curve curve : curves) {
				int d = Integer.MAX_VALUE;
				int actualD;
				Point last = null;
				for (Point point : curve.getVisiblePoints(viewport.getYMin(),
						viewport.getYMax())) {
					actualD = (int) Math.abs(point.getPosX() - mouseX);
					if (d > actualD) {
						d = actualD;
					} else {
						break;
					}
					last = point;
				}
				if (last != null)
					actualSelection.add(last);
			}
			ArrayList<Point> stateChangedPoints = new ArrayList<Point>();
			for (Point p : actualSelection) {
				if (!selectedPoints.contains(p)) {
					stateChangedPoints.add(p);
					removeAllGORelatedToPoint(p);
				}
			}
			for (Point p : selectedPoints) {
				if (!actualSelection.contains(p)) {
					stateChangedPoints.add(p);
					removeAllGORelatedToPoint(p);
				}
			}
			selectedPoints = actualSelection;

			if (stateChangedPoints.size() > 0) {
				redrawNeeded = true;
				for (Point p : stateChangedPoints) {
					graphicalObjectContainer
							.addAllGraphicalObject(createGOsForPoint(p, false));
				}
			}
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (canHandleEvents
				&& pointSelectionMode == PointSelectionMode.Closest_To_Cursor) {
			ArrayList<Point> stateChanged = new ArrayList<Point>();
			for (Point point : selectedPoints) {
				removeAllGORelatedToPoint(point);
				stateChanged.add(point);
			}
			if (stateChanged.size() > 0) {
				redrawNeeded = true;
				selectedPoints.clear();
				for (Point point : stateChanged) {
					graphicalObjectContainer
							.addAllGraphicalObject(createGOsForPoint(point,
									false));
				}
			}
		}
	}

	public PointSelectionMode getPointSelectionMode() {
		return pointSelectionMode;
	}

	public void setPointSelectionMode(PointSelectionMode pointSelectionMode) {
		this.pointSelectionMode = pointSelectionMode;
	}

	/**
	 * @return the overlapFilterDistance
	 */
	public int getOverlapFilterDistance() {
		return overlapFilterDistance;
	}

	/**
	 * @param overlapFilterDistance
	 *            the overlapFilterDistance to set
	 */
	public void setOverlapFilterDistance(int overlapFilterDistance) {
		if (this.overlapFilterDistance != overlapFilterDistance) {
			redrawNeeded = true;
			setCurvesModelChanged();
		}
		this.overlapFilterDistance = overlapFilterDistance;
	}

	private void setCurvesModelChanged() {
		for (Curve c : curves)
			c.modelChanged = true;
	}

	/**
	 * @param otherLineChart
	 */
	public void cloneProperties(LineChart otherLineChart) {
		autoScaleViewport = otherLineChart.autoScaleViewport;
		pointSelectionMode = otherLineChart.pointSelectionMode;
		useViewport = otherLineChart.useViewport;
		overlapFilterDistance = otherLineChart.overlapFilterDistance;
		redrawNeeded = true;
		setCurvesModelChanged();
	}

	@Override
	public boolean redrawNeeded() {
		for (Curve c : curves)
			if (c.modelChanged)
				return true;
		return super.redrawNeeded();
	}

	
	@Override
	public List<LegendEntry> getLegendEntries() {
		ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
		for(Curve c : curves){
			LegendEntry e = new LegendEntry(c, c.getLineProperties().getLineColor());
			entries.add(e);
		}
		return entries;
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
	protected void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void setColors(ColorSet colors) {
		this.colors = colors;
	}
	
}
