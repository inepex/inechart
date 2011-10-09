package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Event;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartEventManager;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.event.PointHoverListener;
import com.inepex.inechart.chartwidget.event.PointSelectionEvent;
import com.inepex.inechart.chartwidget.event.PointSelectionHandler;
import com.inepex.inechart.chartwidget.label.LabelFactory;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inechart.chartwidget.properties.LineProperties.LineStyle;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;

public class LineChart extends IneChartModule2D implements PointSelectionHandler{

	/**
	 * 
	 * An enum which defines the select/deselect mode of Points in {@link LineChart}
	 * 
	 */
	public enum PointSelectionMode {
		/**
		 * The closest point to the cursor
		 *  (if mouse is over modul a point will be selected always)
		 */
		Closest_To_Cursor,
		/**
		 * The clicked point
		 */
		On_Click,
		/**
		 * The mouse overed point
		 */
		On_Over,
		/**
		 * The right clicked point
		 */
		On_Right_Click
	}

	/**
	 * Overlay canvas for speed up point selection.
	 * All selected points are drawn over this area,
	 * while all normal state points drawn on inherited canvas.
	 */
	DrawingArea overlay;
	boolean useOverlay;
	TreeMap<Curve, GraphicalObjectContainer> overlayGosPerCurve;
	int pointMouseOverRadius;

	PointSelectionMode selectPoint;
	boolean singlePointSelection;
	
	// model fields
	ArrayList<Curve> curves = new ArrayList<Curve>();
	int highestZIndex = 1;
//	/**
//	 * stores the calculated points inside viewport
//	 */
//	TreeMap<Curve, ArrayList<DataPoint>> calculatedPointsPerCurve;
	TreeMap<Curve, Path> visiblePathPerCurve;
	TreeMap<Curve, Path> fillPathPerCurve;

	/**
	 * all {@link GraphicalObject}s related to line chart per curve
	 */
	TreeMap<Curve, GraphicalObjectContainer> lineChartGOsPerCurve;

	/**
	 * all {@link GraphicalObject}s related to point chart per curve
	 */
	TreeMap<Curve, GraphicalObjectContainer> pointChartGOsPerCurve;
	
	ArrayList<PointHoverListener> pointHoverListeners;

	public LineChart(DrawingArea canvas, LabelFactory labelFactory, Axes axes) {
		this(canvas, labelFactory, axes, null, null);
		useOverlay = false;
	}

	public LineChart(DrawingArea canvas, LabelFactory labelFactory, Axes axes, DrawingArea overLay, IneChartEventManager eventManager) {
		super(canvas, labelFactory, axes, eventManager);
		this.overlay = overLay;
//		calculatedPointsPerCurve = new TreeMap<Curve, ArrayList<DataPoint>>();
		visiblePathPerCurve = new TreeMap<Curve, Path>();
		fillPathPerCurve = new TreeMap<Curve, Path>();
		lineChartGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		pointChartGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		overlayGosPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		pointHoverListeners = new ArrayList<PointHoverListener>();
		useOverlay = true;
		if(eventManager != null){
			eventManager.addPointSelectionHandler(this);
		}
		// defaults
		selectPoint = Defaults.selectPoint;
		autoScaleViewport = true;
		pointMouseOverRadius = Defaults.pointMouseOverRadius;
		singlePointSelection = true;
	}

	public void addCurve(Curve curve) {
		if (curve == null)
			return;
		if (curves == null)
			curves = new ArrayList<Curve>();
		curves.add(curve);
		if (curve.zIndex == Integer.MIN_VALUE)
			curve.zIndex = ++highestZIndex;
		else if (curve.zIndex > highestZIndex)
			highestZIndex = curve.zIndex;
		curve.dataSet.update();
	}

	public void removeCurve(Curve curve) {
		if (curve == null)
			return;
		removeAllGORelatedToCurve(curve);
		lineChartGOsPerCurve.remove(curve);
		overlayGosPerCurve.remove(curve);
		curves.remove(curve);
	}

	@Override
	public void preUpdateModule() {
		if (curves == null || curves.size() == 0)
			return;
		if (autoScaleViewport) {
			double yMin = Double.MAX_VALUE;
			double yMax = -Double.MAX_VALUE;
			double xMin = Double.MAX_VALUE;
			double xMax = -Double.MAX_VALUE;

			for (Curve c : curves) {
				if (c.dataSet.getxMax() > xMax)
					xMax = c.dataSet.getxMax();
				if (c.dataSet.getyMax() > yMax)
					yMax = c.dataSet.getyMax();
				if (c.dataSet.getxMin() < xMin)
					xMin = c.dataSet.getxMin();
				if (c.dataSet.getyMin() < yMin)
					yMin = c.dataSet.getyMin();
			}
			autoScaleViewport = false;
			xAxis.setMax(xMax);
			yAxis.setMax(yMax);
			xAxis.setMin(xMin);
			yAxis.setMin(yMin);
		}
		super.preUpdateModule();
	}

	@Override
	public void update() {
		if (curves == null || curves.size() == 0)
			return;
		graphicalObjectContainer.removeAllGraphicalObjects();
		//do model to canvas calculations
//		long start;
		for (Curve curve : curves) {
//			Log.debug("----"+curve.getDataSet().getName()+"-----");
			if(curve.hasLine){
//				start = System.currentTimeMillis();
				createVisiblePathForCurve(curve);
//				Log.debug("visiblePath calc: " + (System.currentTimeMillis() - start) + " ms");
			}
			if(shouldCalculateFillPathForCurve(curve)){
//				start = System.currentTimeMillis();
				createFillPathForCurve(curve);
//				Log.debug("fillPath calc: " + (System.currentTimeMillis() - start) + " ms");
			}
			if(curve.hasPoint){
//				start = System.currentTimeMillis();
				calculatePointsForCurve(curve);
//				Log.debug("point calc: " + (System.currentTimeMillis() - start) + " ms");
			}
		}
		//remove previous and create new GOs
		for (Curve curve : curves) {
//			Log.debug("----"+curve.getDataSet().getName()+"-----");
			if(curve.hasLine){
//				start = System.currentTimeMillis();
				createLineChartGOs(curve);
//				Log.debug("lineChart GO creation: " + (System.currentTimeMillis() - start) + " ms");
			}
			if(curve.hasPoint){
//				start = System.currentTimeMillis();
				createPointChartGOs(curve, false);
//				Log.debug("pointChart GO creation: " + (System.currentTimeMillis() - start) + " ms");
			}
		}
		for(Curve curve:curves){
			if(lineChartGOsPerCurve.get(curve) != null){
				graphicalObjectContainer.addAllGraphicalObject(lineChartGOsPerCurve.get(curve));
			}
			if(pointChartGOsPerCurve.get(curve) != null){
				graphicalObjectContainer.addAllGraphicalObject(pointChartGOsPerCurve.get(curve));
			}
		}
		if(useOverlay){
			updateOverLay();
		}
		super.update();
	}	

	protected void updateOverLay(){
		overlay.removeAllGraphicalObjects();
		for(Curve curve:overlayGosPerCurve.keySet()){
			if(overlayGosPerCurve.get(curve) != null){
				overlay.addAllGraphicalObject(overlayGosPerCurve.get(curve));
			}
		}
		overlay.update();
	}

	protected boolean shouldCalculateFillPathForCurve(Curve curve){
		if( curve.autoFill || (curve.toCanvasYFills != null && curve.toCanvasYFills.size() > 0) ||
				(curve.toCurveFills != null && curve.toCurveFills.size() > 0) || 
				(curve.toYFills != null && curve.toYFills.size() > 0)){
			return true;
		}
		else{
			for(Curve c : curves){
				if(c == curve )
					continue;
				if(c.toCurveFills != null && c.toCurveFills.containsKey(curve))
					return true;
			}
		}
		return false;
	}

	protected void calculatePointsForCurve(Curve curve){
//		long start = System.currentTimeMillis();
		curve.dataSet.update();
		List<double[]> dataPairs = curve.dataSet.getDataPairs();
		
		for(DataPoint selected : curve.selectedPoints){
			setDataPoint(selected);
		}
//		Log.debug("    selected points calc: " + (System.currentTimeMillis() - start) + " ms");
		
		//TODO DataSet pointAdded/removed events!!
		//iterate through dataPairs and add if s
		curve.dataPoints.clear();
		for(double[] dataPair : dataPairs){
			DataPoint point = new DataPoint(dataPair[0], dataPair[1]);
			setDataPoint(point);
			curve.dataPoints.add(point);
//			start = System.currentTimeMillis();
//			DataPoint point = new DataPoint(dataPair[0], dataPair[1]);
//			if(curve.dataPoints.contains(point)){
//				Log.debug("    § curve.dataPoints.contains ?: " + (System.currentTimeMillis() - start) + " ms");
//				point = curve.dataPoints.get(curve.dataPoints.indexOf(point));
//			}
//			else{
//				Log.debug("    § curve.dataPoints.contains ?: " + (System.currentTimeMillis() - start) + " ms");
//				curve.dataPoints.add(point);
//			}
//			start = System.currentTimeMillis();
//			setDataPoint(point);
//			Log.debug("    § setting data point: " + (System.currentTimeMillis() - start) + " ms");
//			if(point.x > xAxis.getMax()){
//				if(curve.dataSet.isSortable()){
//					break;
//				}
//			}
//			else if(point.x >= xAxis.getMin() && point.y >= yAxis.getMin() && point.y <= yAxis.getMax()){
//				calculatedPoints.add(point);
//			}
		}
//		Log.debug("    normal points calc: " + (System.currentTimeMillis() - start) + " ms");
//		start = System.currentTimeMillis();
		Collections.sort(curve.dataPoints);
//		Log.debug("    sorting points: " + (System.currentTimeMillis() - start) + " ms");
	}

	protected void createFillPathForCurve(Curve curve){
		curve.dataSet.update();
		List<double[]> dataPairs = curve.dataSet.getDataPairs();

		Path path = null;
		double[] lastDataPair = null;
		double[] lastLineEnd = null;
		for(double[] dataPair : dataPairs){
			double x = dataPair[0] > xAxis.getMax() ? xAxis.getMax() : (dataPair[0]  < xAxis.getMin() ? xAxis.getMin() : dataPair[0]);
			double y = dataPair[1] > yAxis.getMax() ? yAxis.getMax() : (dataPair[1]  < yAxis.getMin() ? yAxis.getMin() : dataPair[1]);
			if(path == null){
				path = new Path(getCanvasX(x), getCanvasY(y), curve.zIndex, null, false, true);
			}
			else{
				double[] intersection = DrawingAreaAssist.getIntersection(
						lastDataPair[0], lastDataPair[1],
						dataPair[0], dataPair[1],
						xAxis.getMin(),
						yAxis.getMin(),
						xAxis.getMax() - xAxis.getMin(),
						yAxis.getMax() - yAxis.getMin());
				if(intersection != null){
					if(lastLineEnd[0] != intersection[0] || lastLineEnd[1] != intersection[1]){
						path.lineTo(getCanvasX(intersection[0]), getCanvasY(intersection[1]), false);
					}
					path.lineTo(getCanvasX(intersection[2]), getCanvasY(intersection[3]), false);
				}
				path.lineTo(getCanvasX(x), getCanvasY(y), false);
			}
			lastDataPair = dataPair;
			lastLineEnd = new double[]{x,y};
		}
		fillPathPerCurve.put(curve, path);
	}

	protected void createVisiblePathForCurve(Curve curve){
		curve.dataSet.update();
		List<double[]> dataPairs = curve.dataSet.getDataPairs();

		double[] lastDataPair = null;
		double[] lastLineEnd = null;
		boolean ready = false;
		Path path = null;
		for(double[] dataPair : dataPairs){
			if(ready){
				break;
			}
			if(curve.dataSet.isSortable() && dataPair[0] > xAxis.getMax()){
				ready =  true;
			}

			if(curve.uncalcedDiscontinuities.contains(dataPair[0])){
				lastDataPair = null;
			}
			else{
				if(lastDataPair != null){
					double[] intersection = DrawingAreaAssist.getIntersection(
							lastDataPair[0], lastDataPair[1],
							dataPair[0], dataPair[1],
							xAxis.getMin(),
							yAxis.getMin(),
							xAxis.getMax() - xAxis.getMin(),
							yAxis.getMax() - yAxis.getMin());
					if(intersection != null){
						if(path == null){
							path = new Path(getCanvasX(intersection[0]), getCanvasY(intersection[1]), curve.zIndex, null, true, false);
							path.lineTo(getCanvasX(intersection[2]), getCanvasY(intersection[3]), false);
						}
						else{
							if(lastLineEnd[0] != intersection[0] || lastLineEnd[1] != intersection[1]){
								path.moveTo(getCanvasX(intersection[0]), getCanvasY(intersection[1]), false);
							}
							path.lineTo(getCanvasX(intersection[2]), getCanvasY(intersection[3]), false);
						}
						lastLineEnd = new double[]{intersection[2], intersection[3]};
					}
				}
				lastDataPair = dataPair;
			}
			
		}
		if(path != null){
			visiblePathPerCurve.put(curve, path);
		}
	}

	/**
	 * Creates line chart {@link GraphicalObject}s:
	 *  -lines,
	 *  -fills
	 *  and puts them into {@link #lineChartGOsPerCurve} container
	 * 
	 * @param curve
	 */
	protected void createLineChartGOs(Curve curve) {
		GraphicalObjectContainer gos = lineChartGOsPerCurve.get(curve);
		if(gos == null){
			gos = new GraphicalObjectContainer();
			lineChartGOsPerCurve.put(curve, gos);
		}
		gos.removeAllGraphicalObjects();
		Path strokePath = visiblePathPerCurve.get(curve);

		if (curve.hasLine && strokePath != null) {
			Path line = new Path(strokePath);
			line.setContext(createLineContext(curve));
			line.setStroke(true);
			if (curve.lineProperties != null && curve.lineProperties.getStyle().equals(LineStyle.DASHED)){
				gos.addGraphicalObject(DrawingAreaAssist.createDashedLine(line,
						curve.getLineProperties().getDashStrokeLength(), curve
						.getLineProperties().getDashDistance()));
			}
			else{
				gos.addGraphicalObject(line);
			}
		}
		Path fillPath = fillPathPerCurve.get(curve);
		if(fillPath == null)
			return;
		if (curve.autoFill) {
			Path fill = new Path(fillPath);
			double minY = getCanvasY(yAxis.getMin());
			fill.lineTo(fill.getLastPathElement().getEndPointX(), minY, false);
			fill.lineTo(fill.getBasePointX(), minY, false);
			fill.lineToBasePoint();
			fill.setFill(true);
			//			fill = DrawingAreaAssist.replaceAllPathElementsWithLineTo(fill);
			Color c;
			if (curve.getLineProperties() != null){
				c = curve.getLineProperties().getLineColor();
			}
			else {
				c = curve.dataSet.getColor();
			}
			fill.setContext(createFillContext(c));
			fill.getContext().setAlpha(Defaults.fillOpacity);
			gos.addGraphicalObject(fill);
		}
		if (curve.toCanvasYFills != null && curve.toCanvasYFills.size() > 0) {
			// TODO axis direction
			for (int i : curve.toCanvasYFills.keySet()) {
				Path fill = new Path(fillPath);
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
				Path fill = new Path(fillPath.getBasePointX(),
						fillPath.getBasePointY(), curve.getZIndex(),
						createFillContext(curve.toCurveFills.get(toCurve)),
						false, true);
				Path otherPath = visiblePathPerCurve.get(toCurve);

				if (otherPath == null)
					continue;
				for (PathElement e : fillPath.getPathElements()) {
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
			//TODO axis direction!!
			for (double y : curve.toYFills.keySet()) {
				Path fill = new Path(fillPath);
				double calculatedY = getCanvasPosition(0, y)[1];
				fill.lineTo(fill.getLastPathElement().getEndPointX(),calculatedY, false);
				fill.lineTo(fill.getBasePointX(), calculatedY, false);
				fill.lineToBasePoint();
				fill.setFill(true);
				fill.setzIndex(curve.getZIndex());
				fill.setContext(createFillContext(curve.toYFills.get(y)));
				gos.addGraphicalObject(fill);
			}
		}
	}

	/**
	 * Creates point chart {@link GraphicalObject}s and puts them into
	 * {@link #pointChartGOsPerCurve} container
	 * 
	 * @param curve
	 * @param onlyOverlay
	 */
	protected void createPointChartGOs(Curve curve, boolean onlyOverlay) {
		if(curve.dataPoints.size() == 0){
			return;
		}
		GraphicalObjectContainer goc = pointChartGOsPerCurve.get(curve);
		if(goc == null){
			goc = new GraphicalObjectContainer();
			pointChartGOsPerCurve.put(curve, goc);
		}
		else{
			goc.removeAllGraphicalObjects();
		}
		GraphicalObjectContainer overlayGoc = overlayGosPerCurve.get(curve);
		if(overlayGoc == null){
			overlayGoc = new GraphicalObjectContainer();
			overlayGosPerCurve.put(curve, overlayGoc);
		}
		else{
			overlayGoc.removeAllGraphicalObjects();
		}
		
		Shape normal = curve.normalPoint;
		Shape selected = curve.selectedPoint;
		if(normal == null && selected == null){
			//use defaults
			normal = Defaults.normalPoint();
			selected = Defaults.selectedPoint();
			normal.getProperties().getLineProperties().setLineColor(curve.dataSet.getColor());
			selected.getProperties().getLineProperties().getLineColor().setColor(curve.dataSet.getColor().getColor());
		}
		if(normal != null){
			normal.setZIndex(curve.zIndex);
		}
		if(selected != null){
			selected.setZIndex(curve.zIndex);
		}
		if(curve.applyCurveShadowForPoint){
			if(curve.hasShadow){
				if(normal != null){
					normal.setShadowColor(curve.shadowColor);
					normal.setShadowOffsetX(curve.shadowOffsetX);
					normal.setShadowOffsetY(curve.shadowOffsetY);
				}
//				if(selected != null){
//					selected.setShadowColor(curve.shadowColor);
//					selected.setShadowOffsetX(curve.shadowOffsetX);
//					selected.setShadowOffsetY(curve.shadowOffsetY);
//				}
			}
			else{
				if(normal != null){
					normal.setShadowOffsetX(0);
					normal.setShadowOffsetY(0);
				}
				if(selected != null){
					selected.setShadowOffsetX(0);
					selected.setShadowOffsetY(0);
				}
			}
		}
		if(curve.useCurveLinePropertiesForShape){
			if(normal != null){
				normal.getProperties().setLineProperties(curve.lineProperties != null ? curve.lineProperties : new LineProperties(Defaults.lineWidth, curve.dataSet.getColor()));
			}
			if(selected != null){
				selected.getProperties().setLineProperties(curve.lineProperties != null ? curve.lineProperties : new LineProperties(Defaults.lineWidth, curve.dataSet.getColor()));
			}
		}
		if(onlyOverlay){
			for(DataPoint point : curve.selectedPoints){
				overlayGoc.addAllGraphicalObject(createPoint(point, selected));
			}
		}
		else{
			for(DataPoint point : curve.dataPoints){
				if(!point.isInViewport){
					continue;
				}
				//selected
				if(curve.selectedPoints.contains(point)){
					if(selected != null){
						if(useOverlay){
							//we put normal point on backing canvas
							if(normal != null){
								goc.addAllGraphicalObject(createPoint(point, normal));
							}
							overlayGoc.addAllGraphicalObject(createPoint(point, selected));
						}
						else{
							goc.addAllGraphicalObject(createPoint(point, selected));
						}
					}
				}
				//normal
				else if(normal != null){
					goc.addAllGraphicalObject(createPoint(point, normal));
				}
			}
		}
	}

	protected GraphicalObjectContainer createPoint(DataPoint point, Shape shape){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		if(shape != null){
			for(GraphicalObject go : shape.toGraphicalObjects()){
				if (shape instanceof Circle) {
					go.setBasePointX(point.actualXPos);
					go.setBasePointY(point.actualYPos);
				}
				else if (shape instanceof Rectangle) {
					go.setBasePointX(point.actualXPos - ((Rectangle) shape).getWidth() / 2);
					go.setBasePointY(point.actualYPos - ((Rectangle) shape).getHeight() / 2);
				}
				goc.addGraphicalObject(go);
			}
		}
		return goc;
	}

	/**
	 * Removes all {@link GraphicalObject} from this modul's
	 * {@link GraphicalObjectContainer} based on {@link #lineChartGOsPerCurve} and
	 * {@link #gosPerPoint} containers.
	 * 
	 * @param curve
	 */
	protected void removeAllGORelatedToCurve(Curve curve) {
		if (lineChartGOsPerCurve.containsKey(curve)) {
			for (GraphicalObject go : lineChartGOsPerCurve.get(curve).getGraphicalObjects()) {
				graphicalObjectContainer.removeGraphicalObject(go);
			}
		}
		if (useOverlay && overlayGosPerCurve.containsKey(curve)){
			for (GraphicalObject go : overlayGosPerCurve.get(curve).getGraphicalObjects()) {
				overlay.removeGraphicalObject(go);
			}
		}	
	}

	static Context createFillContext(Color fillColor) {
		return new Context(fillColor.getAlpha(), Defaults.colorString, 0,
				fillColor.getColor(), 0, 0, Defaults.alpha,
				Defaults.colorString);
	}

	static Context createLineContext(Curve curve) {
		return new Context(
				curve.lineProperties != null ? curve.lineProperties.getLineColor().getAlpha() :	Defaults.alpha,
				curve.lineProperties != null ? curve.lineProperties.getLineColor().getColor() : curve.dataSet.getColor().getColor(),
				curve.lineProperties != null ? curve.lineProperties.getLineWidth() : Defaults.lineWidth,
				Defaults.colorString,
				curve.hasShadow ? curve.shadowOffsetX : 0,
				curve.hasShadow ? curve.shadowOffsetY : 0,
				curve.shadowColor == null ? Defaults.alpha : curve.shadowColor.getAlpha(),
				curve.shadowColor == null ? Defaults.colorString : curve.shadowColor.getColor());
	}

	@Override
	public TreeMap<String, Color> getLegendEntries() {
		if(legendEntries == null){
			TreeMap<String, Color> entries = new TreeMap<String, Color>();
			for(Curve c : curves){
				entries.put(c.getDataSet().getName(),
						c.getLineProperties() != null && c.getLineProperties().getLineColor() != null ?
								c.getLineProperties().getLineColor() :
									c.getDataSet().getColor());
			}

			return entries;
		}
		else{
			return legendEntries;
		}
	}
	
	/**
	 * @return the curves
	 */
	public List<Curve> getCurves() {
		return curves;
	}

	/**
	 * @return the pointMouseOverRadius
	 */
	public int getPointMouseOverRadius() {
		return pointMouseOverRadius;
	}

	/**
	 * @param pointMouseOverRadius -1 if you want to use the default
	 */
	public void setPointMouseOverRadius(int pointMouseOverRadius) {
		this.pointMouseOverRadius = pointMouseOverRadius;
	}

	/**
	 * @return the selectPoint
	 */
	public PointSelectionMode getPointSelectionMode() {
		return selectPoint;
	}

	/**
	 * @param selectPoint the selectPoint to set
	 */
	public void setPointSelectionMode(PointSelectionMode selectPoint) {
		this.selectPoint = selectPoint;
	}

	@Override
	protected void onClick(ClickEvent event) {
		if(!canHandleEvents)
			return;
		if(event.getNativeButton() == Event.BUTTON_RIGHT){
			if(selectPoint == PointSelectionMode.On_Right_Click){
				selectPointEvent(getMouseOverPoints(event));
			}
		}
		else{
			if(selectPoint == PointSelectionMode.On_Click){
				selectPointEvent(getMouseOverPoints(event));
			}
		}
	}

	@Override
	protected void onMouseUp(MouseUpEvent event) {
		
	}

	@Override
	protected void onMouseOver(MouseEvent<?> event) {
//		if(selectPoint == PointSelectionMode.On_Over || selectPoint == PointSelectionMode.Closest_To_Cursor){
//			selectPointEvent(getClosestToMousePoints(event));
//		}
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		
	}

	@Override
	protected void onMouseOut(MouseEvent<?> event) {
		if(selectPoint == PointSelectionMode.On_Over || selectPoint == PointSelectionMode.Closest_To_Cursor){
			selectPointEvent(new TreeMap<Curve, DataPoint>()); //deselect points
		}
	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		TreeMap<Curve, DataPoint> hoveredPoints = getMouseOverPoints(event);
		for(PointHoverListener phl : pointHoverListeners){
			phl.onPointHover(hoveredPoints);
		}
		if(selectPoint == PointSelectionMode.On_Over){
			selectPointEvent(hoveredPoints);
		}
		else if(selectPoint == PointSelectionMode.Closest_To_Cursor){
			selectPointEvent(getClosestToMousePoints(event));
		}
	}
	
	/**
	 * Returns a curve-point mapping for each mouseovered point
	 * @param e
	 * @return
	 */
	public TreeMap<Curve, DataPoint> getMouseOverPoints(MouseEvent<?> e){
		TreeMap<Curve, DataPoint> mouseOver = new TreeMap<Curve, DataPoint>();
		int[] eventLocation = getCoords(e);
		for(Curve c : curves){
			if(!c.hasPoint){
				continue;
			}
			DataPoint overed = null;
			for(DataPoint point : c.dataPoints){
				if(!point.isInViewport){
					continue;
				}
				if(point.actualXPos >= eventLocation[0]  - pointMouseOverRadius &&
						point.actualXPos <= eventLocation[0]  + pointMouseOverRadius &&
						point.actualYPos >= eventLocation[1] - pointMouseOverRadius &&
						point.actualYPos <= eventLocation[1] + pointMouseOverRadius ){
					overed = point;
					break;
				}
			}
			if(overed != null){
				mouseOver.put(c, overed);
			}
		}
		return mouseOver;
	}
	
	/**
	 * Returns a curve - closest point to cursor mapping
	 * @param e
	 * @return
	 */
	public TreeMap<Curve, DataPoint> getClosestToMousePoints(MouseEvent<?> e){
		TreeMap<Curve, DataPoint> mouseOver = new TreeMap<Curve, DataPoint>();
		int[] eventLocation = getCoords(e);
		for(Curve c : curves){
			if(!c.hasPoint){
				continue;
			}
			DataPoint overed = null;
			double closestDiff = Double.MAX_VALUE;
			for(DataPoint point : c.dataPoints){
				if(!point.isInViewport){
					continue;
				}
				if(Math.abs(point.actualXPos - eventLocation[0]) < closestDiff){
					closestDiff = Math.abs(point.actualXPos - eventLocation[0]);
					overed = point;
				}
			}
			if(overed != null){
				mouseOver.put(c, overed);
			}
		}
		return mouseOver;
	}
	
	public boolean isPointVisible(DataPoint dataPoint){
		if(dataPoint.getX() >= xAxis.getMin() && dataPoint.getX() <= xAxis.getMax() &&
				dataPoint.getY() >= yAxis.getMin() && dataPoint.getY() <= yAxis.getMax()){
			return true;
		}
		return false;
	}
	
	protected void selectPointEvent(TreeMap<Curve, DataPoint> selectedPoints){
		if(!useOverlay)
			return;
		TreeMap<Curve, DataPoint> justSelected = new TreeMap<Curve, DataPoint>();
		TreeMap<Curve, DataPoint> justDeselected = new TreeMap<Curve, DataPoint>();
		
		for(Curve c : selectedPoints.keySet()){
			DataPoint actual = selectedPoints.get(c);
			if(c.selectedPoints.size() > 0){
				ArrayList<DataPoint> prevSelected = c.selectedPoints;
				//currently selected point isn't selected
				if(!prevSelected.contains(actual)){
					//single point selection in this modes
					if(prevSelected.size() > 0 && 
							(singlePointSelection || selectPoint == PointSelectionMode.Closest_To_Cursor ||
							selectPoint == PointSelectionMode.On_Over)){
						justDeselected.put(c, prevSelected.get(0));
						prevSelected.clear();
					}
					prevSelected.add(actual);
					justSelected.put(c, actual);
				}
				//currently selected were previously selected
				else{
					if(selectPoint == PointSelectionMode.On_Click ||
							selectPoint == PointSelectionMode.On_Right_Click){
						justDeselected.put(c, actual);
						prevSelected.remove(actual);
					}
				}
			}
			else{
//				c.selectedPoints.add(actual);
				justSelected.put(c, actual);
			}
		}
		//update model (curves selectedpoints)
		for(Curve c : justSelected.keySet()){
			DataPoint data = justSelected.get(c);
			if(!c.selectedPoints.contains(data)){
				c.selectedPoints.add(data);
			}
		}
		for(Curve c : justDeselected.keySet()){
			DataPoint data = justDeselected.get(c);
			if(c.selectedPoints.contains(data)){
				c.selectedPoints.remove(data);
			}
		}
		
		//update overlay
		for(Curve c : curves){
			createPointChartGOs(c, true);
		}
		updateOverLay();
		
		//fire events
		for(Curve c : justSelected.keySet()){
			PointSelectionEvent e = new PointSelectionEvent(
					true, justSelected.get(c), c);
			eventManager.fireEvent(e);
		}
		for(Curve c : justDeselected.keySet()){
			PointSelectionEvent e = new PointSelectionEvent(
					false,justSelected.get(c), c);
			eventManager.fireEvent(e);
		}
	}

	@Override
	public void onSelect(PointSelectionEvent event) {
		if(!canHandleEvents){
			return;
		}
		Curve curve = event.getCurve();
		if(curve == null){
			for(Curve c : curves){
				if(c.dataPoints.contains(event.getPoint())){
					curve = c;
					break;
				}
			}
		}
		if(curve == null) {
			return;
		}
	
		if(!curve.selectedPoints.contains(event.getPoint())){
			setDataPoint(event.getPoint());
			if(singlePointSelection){
				curve.selectedPoints.clear();
			}
			curve.selectedPoints.add(event.getPoint());
			updateOverLay();
		}

	}

	@Override
	public void onDeselect(PointSelectionEvent event) {
		if(!canHandleEvents)
			return;
		Curve curve = event.getCurve();
		if(curve == null){
			for(Curve c : curves){
				if(c.dataPoints.contains(event.getPoint())){
					curve = c;
					break;
				}
			}
		}
		if(curve == null) {
			return;
		}
		if(curve.selectedPoints.contains(event.getPoint())) {
			curve.selectedPoints.remove(event.getPoint());
			updateOverLay();
		}
//		if(event.getPoint()[0] < xAxis.getMax() && event.getPoint()[0] > xAxis.getMin() && 
//				event.getPoint()[1] < yAxis.getMax() && event.getPoint()[1] > yAxis.getMin()){
//			selectedPointsPerCurve.get(curve).remove(getCanvasPosition(event.getPoint()[0], event.getPoint()[1]));
//		}
		
	}
	
	private void setDataPoint(DataPoint dataPoint) {
//		long start = System.currentTimeMillis();
		double[] canvasPos = getCanvasPosition(dataPoint.x, dataPoint.y);
//		Log.debug("    § canvas position calc: " + (System.currentTimeMillis() - start) + " ms");
//		start = System.currentTimeMillis();
		dataPoint.actualXPos = canvasPos[0];
		dataPoint.actualYPos = canvasPos[1];
		dataPoint.isInViewport = isPointVisible(dataPoint);
//		Log.debug("    § is point visible?: " + (System.currentTimeMillis() - start) + " ms");
	}

	public boolean isSinglePointSelection() {
		return singlePointSelection;
	}
	
	public void setSinglePointSelection(boolean singlePointSelection) {
		this.singlePointSelection = singlePointSelection;
	}
	
	public void addPointHoverListener(PointHoverListener pointHoverListener){
		pointHoverListeners.add(pointHoverListener);
	}
	
	public void removePointHoverListener(PointHoverListener pointHoverListener){
		pointHoverListeners.remove(pointHoverListener);
	}
	
}

