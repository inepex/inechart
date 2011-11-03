package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Event;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.LinkedLayers;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.data.AbstractDataEntry;
import com.inepex.inechart.chartwidget.data.XYDataEntry;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.event.PointSelectionEvent;
import com.inepex.inechart.chartwidget.event.PointSelectionHandler;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties.LineStyle;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.impl.client.MouseAssist;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;

public class LineChart extends IneChartModule2D implements PointSelectionHandler{

	// model fields
	ArrayList<Curve> curves;
	int highestZIndex = 1;
	int zIndexDiffBetweenCurves = 3;

	TreeMap<Curve, Path> visiblePathPerCurve;
	TreeMap<Curve, Path> fillPathPerCurve;

	PointFilter pointFilter;

	int pointMouseOverRadius;
	PointSelectionMode pointSelectionMode;
	boolean singlePointSelection;

	/**
	 * all {@link GraphicalObject}s related to line chart per Curve2
	 */
	TreeMap<Curve, GraphicalObjectContainer> lineChartGOsPerCurve;

	/**
	 * all {@link GraphicalObject}s related to point chart per Curve2
	 */
	TreeMap<Curve, GraphicalObjectContainer> pointChartGOsPerCurve;

	TreeMap<Curve, GraphicalObjectContainer> interactiveGOsPerCurve;
	TreeMap<GraphicalObject, DataPoint> interactivePoints;

	TreeMap<Curve, LinkedLayers> linkedLayersPerCurve;

	ArrayList<LineChartInteractiveModule> interactiveModules;

	public LineChart(ModuleAssist moduleAssist) {
		super(moduleAssist);

		curves = new ArrayList<Curve>();
		visiblePathPerCurve = new TreeMap<Curve, Path>();
		fillPathPerCurve = new TreeMap<Curve, Path>();
		lineChartGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		pointChartGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		interactiveGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		interactivePoints = new TreeMap<GraphicalObject, DataPoint>();
		linkedLayersPerCurve = new TreeMap<Curve, LinkedLayers>();
		interactiveModules = new ArrayList<LineChartInteractiveModule>();

		// defaults
		pointSelectionMode = Defaults.selectPoint;
		autoScaleViewport = true;
		pointMouseOverRadius = Defaults.pointMouseOverRadius;
		singlePointSelection = false;
		setPointFilter(new PointFilter());
	}

	public void addCurve(Curve curve) {
		if (curve == null){
			return;
		}
		if (curves == null){
			curves = new ArrayList<Curve>();
		}
		curves.add(curve);
		if (curve.zIndex == Integer.MIN_VALUE){
			highestZIndex += zIndexDiffBetweenCurves;
			curve.zIndex = highestZIndex;
		}
		else if (curve.zIndex > highestZIndex){
			highestZIndex = curve.zIndex;
		}
		if(moduleAssist.isClientSide()){
			Layer layer = moduleAssist.createAndAttachLayer(Layer.ALWAYS_BOT);
			LinkedLayers layerGroup = new LinkedLayers(highestZIndex);
			linkedLayersPerCurve.put(curve, layerGroup);
			layerGroup.addLayer(layer);
			moduleLayer.addLayer(layerGroup);
			moduleAssist.updateLayerOrder();

			curve.dataSet.setAttached(true);
			curve.dataSet.setEventManager(moduleAssist.getEventManager());
		}
	}

	protected DrawingArea getCanvas(Curve curve){
		if(moduleAssist.isClientSide()){
			return linkedLayersPerCurve.get(curve).getBottom().getCanvas();
		}
		else{
			return canvas;
		}
	}

	public void removeCurve(Curve curve) {
		if (curve == null)
			return;
		removeAllGORelatedToCurve(curve);
		if(moduleAssist.isClientSide()){
			moduleAssist.destroyLayer(linkedLayersPerCurve.get(curve));
			curve.dataSet.setAttached(false);
		}
		pointChartGOsPerCurve.remove(curve);
		lineChartGOsPerCurve.remove(curve);
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
		for (Curve curve : curves) {
			updateCurveModel(curve);
		}
		//remove previous and create new GOs
		for (Curve curve : curves) {
			removeAndCreateCurveGOs(curve);
			drawGOsAndUpdateCanvasIfClient(curve);
		}
		for(LineChartInteractiveModule im : interactiveModules){
			im.update();
		}
		super.update();
	}

	protected void updateCurveModel(Curve curve){
		calculatePointsForCurve(curve);
		if(curve.hasLine){
			Path path = createStrokePathForCurve(curve, leftPadding, getRightEnd());
			if(path != null){
				visiblePathPerCurve.put(curve, path);
			}
		}
		if(shouldCalculateFillPathForCurve(curve)){
			Path path = createFillPathForCurve(curve, leftPadding, getRightEnd());
			if(path != null){
				fillPathPerCurve.put(curve, path);
			}
		}
	}

	protected void removeAndCreateCurveGOs(Curve curve){
		removeAllGORelatedToCurve(curve);
		if(curve.hasLine){
			createLineChartGOs(curve);
		}
		if(curve.hasPoint){
			createPointChartGOs(curve);
		}
		else if(pointMouseOverRadius > 0){
			for(DataPoint point : curve.dataPoints){
				if(!point.isInViewport){
					continue;
				}
				createInteractivePoint(curve, point, null, null);
			}
		}
	}

	protected void drawGOsAndUpdateCanvasIfClient(Curve curve){
		if(lineChartGOsPerCurve.get(curve) != null){
			getCanvas(curve).addAllGraphicalObject(lineChartGOsPerCurve.get(curve));
		}
		if(pointChartGOsPerCurve.get(curve) != null){
			getCanvas(curve).addAllGraphicalObject(pointChartGOsPerCurve.get(curve));
		}
		if(moduleAssist.isClientSide()){
			getCanvas(curve).update();
		}
	}

	public void updateCurve(Curve curve){
		updateCurveModel(curve);
		removeAndCreateCurveGOs(curve);
		drawGOsAndUpdateCanvasIfClient(curve);
	}

	/**
	 * curve.dataPoints and
	 * curve.discontinuities must be cleared in the given range.
	 * curve.selectedPoints must be sorted
	 * only points in (]/[)from,to(]/[) will be updated
	 * @param curve
	 * @param from
	 * @param to
	 * @param includeFrom
	 * @param includeTo
	 */
	protected void calculatePointsForSortedCurve(Curve curve, double from, double to, boolean includeFrom, boolean includeTo){
		List<XYDataEntry> dataPairs = curve.dataSet.getXYDataEntries(from, to);
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		for(XYDataEntry xyEntry : dataPairs){
			if(xyEntry.getX() > to || (!includeTo && xyEntry.getX() == to)){
				break;
			}
			if(xyEntry.getX() > from || (includeFrom && xyEntry.getX() == from)){
				DataPoint dp = createDataPoint(xyEntry);
				if(curve.unfilterableEntries.contains(xyEntry)){
					dp.unfilterable = true;
				}
				points.add(dp);
				if(curve.discontinuities.contains(xyEntry)){
					curve.discontinuitiesAsPoint.add(dp);
				}
			}
		}
		curve.dataPoints.addAll(pointFilter.filterDataPoints(points));
	}

	protected void calculatePointsForCurve(Curve curve){
		List<XYDataEntry> dataPairs = curve.dataSet.getXYDataEntries();
		curve.discontinuitiesAsPoint.clear();
		curve.dataPoints.clear();
		curve.entryPointMap.clear();
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		if(curve.dataSet.isSortable()){
			XYDataEntry last = null;
			XYDataEntry beforeVPMin = null;
			XYDataEntry beforeVPMax = null;
			XYDataEntry afterVPMax = null;
			XYDataEntry afterVPMin = null;
			boolean firstAfterVP = true;
			for(XYDataEntry xyEntry : dataPairs){
				//bigger than vp min
				if(xyEntry.getX() >= xAxis.getMin()){
					if(last != null && points.size() == 0){
						DataPoint dp = createDataPoint(last); 
						if(curve.unfilterableEntries.contains(last)){
							dp.unfilterable = true;
						}
						points.add(dp);
						if(curve.discontinuities.contains(last)){
							curve.discontinuitiesAsPoint.add(dp);
						}
					}
					DataPoint dp = createDataPoint(xyEntry); 
					if(curve.unfilterableEntries.contains(xyEntry)){
						dp.unfilterable = true;
					}
					points.add(dp);
					if(curve.discontinuities.contains(xyEntry)){
						curve.discontinuitiesAsPoint.add(dp);
					}
				}
				//after vp
				else if(xyEntry.getX() > xAxis.getMax()){
					if(firstAfterVP){
						DataPoint dp = createDataPoint(xyEntry); 
						if(curve.unfilterableEntries.contains(xyEntry)){
							dp.unfilterable = true;
						}
						points.add(dp);
						if(curve.discontinuities.contains(xyEntry)){
							curve.discontinuitiesAsPoint.add(dp);
						};
						firstAfterVP = false;
					}
					if(afterVPMin == null){
						afterVPMax = afterVPMin = xyEntry;
					}
					else{
						if(afterVPMin.getX() > xyEntry.getX()){
							afterVPMin = xyEntry;
						}
						if(afterVPMax.getX() < xyEntry.getX()){
							afterVPMax = xyEntry;
						}
					}
				}
				//before vp
				else{
					if(beforeVPMin == null){
						beforeVPMax = beforeVPMin = xyEntry;
					}
					else{
						if(beforeVPMin.getX() > xyEntry.getX()){
							beforeVPMin = xyEntry;
						}
						if(beforeVPMax.getX() < xyEntry.getX()){
							beforeVPMax = xyEntry;
						}
					}
				}
				last = xyEntry;
			}
			points = pointFilter.filterDataPoints(points);
			if(beforeVPMin != null){
				DataPoint dp = createDataPoint(beforeVPMin);
				if(points.size() == 0 || DataPoint.canvasXComparator.compare(dp, points.get(0)) != 0){
					curve.dataPoints.add(dp);
					curve.entryPointMap.put(beforeVPMin, dp);
				}
			}
			if(beforeVPMax != null && beforeVPMax != beforeVPMin){
				DataPoint dp = createDataPoint(beforeVPMax);
				if(points.size() == 0 || DataPoint.canvasXComparator.compare(dp, points.get(0)) != 0){
					curve.dataPoints.add(dp);
					curve.entryPointMap.put(beforeVPMax, dp);
				}
			}
			curve.dataPoints.addAll(points);
			if(afterVPMin != null){
				DataPoint dp = createDataPoint(afterVPMin);
				if(points.size() == 0 || DataPoint.canvasXComparator.compare(dp, points.get(points.size() - 1)) != 0){
					curve.dataPoints.add(dp);
					curve.entryPointMap.put(afterVPMin, dp);
				}
			}
			if(afterVPMax != null && afterVPMax != afterVPMin){
				DataPoint dp = createDataPoint(afterVPMax);
				if(points.size() == 0 || DataPoint.canvasXComparator.compare(dp, points.get(points.size() - 1)) != 0){
					curve.dataPoints.add(dp);
					curve.entryPointMap.put(afterVPMax, dp);
				}
			}
		}
		else{
			for(XYDataEntry entry : dataPairs){
				DataPoint dp = createDataPoint(entry); 
				if(curve.unfilterableEntries.contains(entry)){
					dp.unfilterable = true;
				}
				points.add(dp);			
			}
			points = pointFilter.filterDataPoints(points);
			curve.dataPoints.addAll(points);
		}
		for(DataPoint dp : points){
			if(dp.containsHiddenData()){
				for(DataPoint dp2 : dp.filteredPoints){
					curve.entryPointMap.put(dp2.data, dp);
				}
			}
			else{
				curve.entryPointMap.put(dp.data, dp);
			}
		}
	}

	private DataPoint createDataPoint(XYDataEntry map){
		DataPoint dp = new DataPoint(map);
		setDataPoint(dp);
		return dp;
	}

	protected boolean shouldCalculateFillPathForCurve(Curve curve){
		if( curve.autoFill  ||
				(curve.toCurveFills != null && curve.toCurveFills.size() > 0) || 
				(curve.toYFills != null && curve.toYFills.size() > 0)){
			return true;
		}
		else{
			for(Curve c : curves){
				if(c == curve)
					continue;
				if(c.toCurveFills != null && c.toCurveFills.containsKey(curve))
					return true;
			}
		}
		return false;
	}

	protected Path createFillPathForCurve(Curve curve, double fromCanvasX, double toCanvasX){
		Path path = null;
		double[] lastPair = null;
		double[] lastLineEnd = null;
		double rightEnd = toCanvasX;
		int botEnd = getBottomEnd();
		double width = toCanvasX - fromCanvasX;
		int height = getHeight();
		for(DataPoint dataPoint : curve.dataPoints){
			double x = dataPoint.canvasX > rightEnd ? rightEnd : (dataPoint.canvasX < fromCanvasX ? fromCanvasX : dataPoint.canvasX);
			double y = dataPoint.canvasY > botEnd ? botEnd : (dataPoint.canvasY < topPadding ? topPadding : dataPoint.canvasY);
			if(path == null){
				path = new Path(x, y, curve.zIndex, null, false, true);
			}
			else{
				double[] intersection = DrawingAreaAssist.getIntersection(
						lastPair[0], lastPair[1],
						dataPoint.canvasX, dataPoint.canvasY,
						fromCanvasX,
						topPadding,
						width,
						height);
				if(intersection != null){
					if(lastLineEnd[0] != intersection[0] || lastLineEnd[1] != intersection[1]){
						path.lineTo(intersection[0], intersection[1], false);
					}
					path.lineTo(intersection[2], intersection[3], false);
				}
				path.lineTo(x, y, false);
			}
			lastPair = new double[]{dataPoint.canvasX, dataPoint.canvasY};
			lastLineEnd = new double[]{x,y};
		}
		return path;
	}

	protected Path createStrokePathForCurve(Curve curve, double fromCanvasX, double toCanvasX){
		double[] lastDataPair = null;
		double[] lastLineEnd = null;
		boolean ready = false;
		Path path = null;
		double rightEnd = toCanvasX;
		double width = toCanvasX - fromCanvasX;
		int height = getHeight();
		for(DataPoint dataPoint : curve.dataPoints){
			if(ready){
				break;
			}
			if(curve.dataSet.isSortable() && dataPoint.canvasX > rightEnd){
				ready =  true;
			}
			if(curve.discontinuitiesAsPoint.contains(dataPoint)){
				lastDataPair = null;
			}
			else{
				if(lastDataPair != null){
					double[] intersection = DrawingAreaAssist.getIntersection(
							lastDataPair[0], lastDataPair[1],
							dataPoint.canvasX, dataPoint.canvasY,
							fromCanvasX,
							topPadding,
							width,
							height);
					if(intersection != null){
						if(path == null){
							path = new Path(intersection[0], intersection[1], curve.zIndex, null, true, false);
						}
						else if(lastLineEnd[0] != intersection[0] || lastLineEnd[1] != intersection[1]){
							path.moveTo(intersection[0], intersection[1], false);
						}
						path.lineTo(intersection[2], intersection[3], false);
						lastLineEnd = new double[]{intersection[2], intersection[3]};
					}
				}
				lastDataPair = new double[]{dataPoint.canvasX, dataPoint.canvasY};
			}

		}
		return path;
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
	 * 
	 * @param curve
	 */
	protected void createPointChartGOs(Curve curve) {
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
		Shape normal = curve.pointShape;
		if(normal == null){
			//use defaults
			normal = Defaults.normalPoint();	
			normal.getProperties().getLineProperties().setLineColor(curve.dataSet.getColor());
		}
		if(normal != null){
			normal.setZIndex(curve.zIndex);
		}

		if(curve.hasShadow){
			if(normal != null){
				normal.setShadowColor(curve.shadowColor);
				normal.setShadowOffsetX(curve.shadowOffsetX);
				normal.setShadowOffsetY(curve.shadowOffsetY);
			}
		}

		for(DataPoint point : curve.dataPoints){
			if(!point.isInViewport){
				continue;
			}
			goc.addAllGraphicalObject(createPoint(curve, point, normal, true, null));
		}
	}

	/**
	 * 
	 * @param curve
	 * @param point
	 * @param shape
	 * @param interactive
	 * @param registeredInteractivePoints puts the interactive GO into this container
	 * @return
	 */
	protected GraphicalObjectContainer createPoint(Curve curve, DataPoint point, Shape shape, boolean interactive, ArrayList<GraphicalObject> registeredInteractivePoints){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		if(shape != null){
			for(GraphicalObject go : shape.toGraphicalObjects()){
				if (shape instanceof Circle) {
					go.setBasePointX(point.canvasX);
					go.setBasePointY(point.canvasY);
				}
				else if (shape instanceof Rectangle) {
					go.setBasePointX(point.canvasX - ((Rectangle) shape).getWidth() / 2);
					go.setBasePointY(point.canvasY - ((Rectangle) shape).getHeight() / 2);
				}
				goc.addGraphicalObject(go);
			}
			if(interactive){
				createInteractivePoint(curve, point, shape, registeredInteractivePoints);
			}
		}
		return goc;
	}

	/**
	 * Creates an invisible {@link GraphicalObject} representing the mouseOver area of the given {@link DataPoint}
	 * @param curve
	 * @param point
	 * @param shape
	 * @param registeredInteractivePoints
	 */
	protected void createInteractivePoint(Curve curve, DataPoint point, Shape shape, ArrayList<GraphicalObject> registeredInteractivePoints){
		GraphicalObject go;
		if(shape != null){
			go = shape.toInteractiveGraphicalObject(pointMouseOverRadius);
		}
		else{
			shape = new Circle(1);
			go = shape.toInteractiveGraphicalObject(pointMouseOverRadius);
		}
		if (shape instanceof Circle) {
			go.setBasePointX(point.canvasX);
			go.setBasePointY(point.canvasY);
		}
		else if (shape instanceof Rectangle) {
			go.setBasePointX(point.canvasX - ((Rectangle) shape).getWidth() / 2);
			go.setBasePointY(point.canvasY - ((Rectangle) shape).getHeight() / 2);
		}
		
		GraphicalObjectContainer gos = interactiveGOsPerCurve.get(curve);
		if(gos == null){
			gos = new GraphicalObjectContainer();
			interactiveGOsPerCurve.put(curve, gos);
		}
		//single interactive GO for a datapoint
		if(interactivePoints.containsValue(point)){
			GraphicalObject oldInteractiveGO = null;
			for(GraphicalObject igo:interactivePoints.keySet()){
				if(point == interactivePoints.get(igo)){
					oldInteractiveGO = igo;
					break;
				}
			}
			interactivePoints.remove(oldInteractiveGO);
			gos.removeGraphicalObject(oldInteractiveGO);
		}
		interactivePoints.put(go, point);
		gos.addGraphicalObject(go);
		if(registeredInteractivePoints != null){
			registeredInteractivePoints.add(go);
		}
	}
	
	protected void resetInteractivePoint(Curve curve, GraphicalObject go){
		if(interactivePoints.containsKey(go)){
			DataPoint dp = interactivePoints.get(go);
			interactivePoints.remove(go);
			interactiveGOsPerCurve.get(curve).removeGraphicalObject(go);
			//add default interactive go
			Shape normal = curve.pointShape;
			if(normal == null && curve.hasPoint){
				//use defaults
				normal = Defaults.normalPoint();	
				normal.getProperties().getLineProperties().setLineColor(curve.dataSet.getColor());
			}
			createInteractivePoint(curve, dp, normal, null);
		}
	}

	/**
	 * Removes all {@link GraphicalObject} from the related {@link GraphicalObjectContainer} based on {@link #lineChartGOsPerCurve} and
	 * {@link #pointChartGOsPerCurve} containers.
	 * 
	 * @param curve
	 */
	protected void removeAllGORelatedToCurve(Curve curve) {
		if (lineChartGOsPerCurve.containsKey(curve)) {
			for (GraphicalObject go : lineChartGOsPerCurve.get(curve).getGraphicalObjects()) {
				getCanvas(curve).removeGraphicalObject(go);
			}
			lineChartGOsPerCurve.remove(curve);
		}
		if (pointChartGOsPerCurve.containsKey(curve)) {
			for (GraphicalObject go : pointChartGOsPerCurve.get(curve).getGraphicalObjects()) {
				getCanvas(curve).removeGraphicalObject(go);
			}
			pointChartGOsPerCurve.remove(curve);
		}
		if (interactiveGOsPerCurve.containsKey(curve)) {
			for (GraphicalObject go : interactiveGOsPerCurve.get(curve).getGraphicalObjects()){
				interactivePoints.remove(go);
			}
			interactiveGOsPerCurve.remove(curve);
		}
	}

	protected static Context createFillContext(Color fillColor) {
		return new Context(fillColor.getAlpha(), Defaults.colorString, 0,
				fillColor.getColor(), 0, 0, Defaults.alpha,
				Defaults.colorString);
	}

	protected static Context createLineContext(Curve curve) {
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
				entries.put(c.getDataSet().getTitle(),
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

	@Override
	protected void onDataSetChange(DataSetChangeEvent event) {
		//TODO
	}

	@Override
	protected void onSelect(DataEntrySelectionEvent event) {
		dataEntrySelected(event);
	}

	@Override
	protected void onDeselect(DataEntrySelectionEvent event) {
		dataEntrySelected(event);
	}

	protected void dataEntrySelected(DataEntrySelectionEvent event){
		if(event.getDataEntry().getContainer() == null || !(event.getDataEntry() instanceof XYDataEntry)){
			return;
		}
		for(Curve c : curves){
			if(event.getDataEntry().getContainer() == c.dataSet && c.dataSet.containsXYDataEntry((XYDataEntry) event.getDataEntry())){
				TreeMap<Curve, ArrayList<DataPoint>> selected = new TreeMap<Curve, ArrayList<DataPoint>>();
				ArrayList<DataPoint> selectedList = new ArrayList<DataPoint>();
				DataPoint dp = createDataPoint((XYDataEntry) event.getDataEntry());
				selectedList.add(dp);
				selected.put(c, selectedList);
				interactPointEvent(selected, !event.isSelect(), event.isSelect());
				break;
			}
		}
	}
	
	@Override
	protected void onClick(ClickEvent event) {
		handleMouseEvents(event);
		for(LineChartInteractiveModule im : interactiveModules){
			im.onClick(event);
		}
	}

	@Override
	protected void onMouseUp(MouseUpEvent event) {
		for(LineChartInteractiveModule im : interactiveModules){
			im.onMouseUp(event);
		}
	}

	@Override
	protected void onMouseOver(MouseEvent<?> event) {
		for(LineChartInteractiveModule im : interactiveModules){
			im.onMouseOver(event);
		}
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		for(LineChartInteractiveModule im : interactiveModules){
			im.onMouseDown(event);
		}
	}

	@Override
	protected void onMouseOut(MouseEvent<?> event) {
		handleMouseEvents(event);
		for(LineChartInteractiveModule im : interactiveModules){
			im.onMouseOut(event);
		}
	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		handleMouseEvents(event);
		for(LineChartInteractiveModule im : interactiveModules){
			im.onMouseMove(event);
		}
	}

	protected void handleMouseEvents(MouseEvent<?> event){
		if(!moduleAssist.isClientSide()){
			return;
		}
		TreeMap<Curve, ArrayList<DataPoint>> overedPointsPerCurve;
		if( (pointSelectionMode == PointSelectionMode.On_Click && event instanceof ClickEvent && event.getNativeButton() == Event.BUTTON_LEFT) 
				|| (pointSelectionMode == PointSelectionMode.On_Right_Click && event instanceof ClickEvent && event.getNativeButton() == Event.BUTTON_RIGHT) ||
				(pointSelectionMode == PointSelectionMode.On_Over && event instanceof MouseMoveEvent)){
			overedPointsPerCurve = new TreeMap<Curve, ArrayList<DataPoint>>();

			int[] coords = getCoords(event);
			for(Curve c : interactiveGOsPerCurve.keySet()){
				if(!c.hasPoint && pointMouseOverRadius <= 0){
					continue;
				}
				for(GraphicalObject go : interactiveGOsPerCurve.get(c).getGraphicalObjects()){
					if(MouseAssist.isMouseOver(coords, go) && interactivePoints.containsKey(go)){
						ArrayList<DataPoint> list = overedPointsPerCurve.get(c);
						if(list == null){
							list = new ArrayList<DataPoint>();
							overedPointsPerCurve.put(c, list);
						}
						list.add(interactivePoints.get(go));
					}
				}
			}
			interactPointEvent(overedPointsPerCurve, true, true);
		}
		else if( pointSelectionMode == PointSelectionMode.Closest_To_Cursor &&
				(event instanceof MouseMoveEvent || event instanceof MouseOutEvent || event instanceof MouseOverEvent)){
			overedPointsPerCurve = getClosestToMousePoints(event);
			interactPointEvent(overedPointsPerCurve, true, true);
		}
	}

	/**
	 * Returns a Curve2 - closest point to cursor mapping (inside module)
	 * @param e
	 * @return
	 */
	protected TreeMap<Curve, ArrayList<DataPoint>> getClosestToMousePoints(MouseEvent<?> e){
		TreeMap<Curve, ArrayList<DataPoint>> mouseOver = new TreeMap<Curve, ArrayList<DataPoint>>();
		int[] eventLocation = getCoords(e);
		if(!isInsideModul(eventLocation[0], eventLocation[1])){
			return mouseOver;
		}
		for(Curve c : curves){
			if(!c.hasPoint && pointMouseOverRadius <= 0){
				continue;
			}
			DataPoint overed = null;
			double closestDiff = Double.MAX_VALUE;
			for(DataPoint point : c.dataPoints){
				if(!point.isInViewport){
					continue;
				}
				if(Math.abs(point.canvasX - eventLocation[0]) < closestDiff){
					closestDiff = Math.abs(point.canvasX - eventLocation[0]);
					overed = point;
				}
			}
			if(overed != null){
				mouseOver.put(c, new ArrayList<DataPoint>());
				mouseOver.get(c).add(overed);
			}
		}
		return mouseOver;
	}

	/**
	 * returns a {@link DataPoint} which is closest to [x,y] on canvas
	 * @param point
	 * @param curve
	 * @return null if the curve has no point inside viewport
	 */
	public DataPoint getClosestDataToPoint(int[] point, Curve curve){
		DataPoint closest = null;
		double minDiff = Double.MAX_VALUE;
		for(DataPoint dp : curve.dataPoints){
			if(!dp.isInViewport){
				continue;
			}
			if(Math.abs(dp.canvasX - point[0]) < minDiff){
				minDiff = Math.abs(dp.canvasX - point[0]);
				closest = dp;
			}
		}
		return closest;		
	}
	/**
	 * Updates selected points in curves, fires events and updates interactive modules
	 * @param interactedPoints
	 * @param fireSelect
	 * @param fireDeselect
	 */
	protected void interactPointEvent(TreeMap<Curve, ArrayList<DataPoint>> interactedPoints, boolean fireSelect, boolean fireDeselect){
		TreeMap<Curve, ArrayList<DataPoint>> justSelected = new TreeMap<Curve, ArrayList<DataPoint>>();
		TreeMap<Curve, ArrayList<DataPoint>> justDeselected = new TreeMap<Curve, ArrayList<DataPoint>>();

		for(Curve c : interactedPoints.keySet()){
			justDeselected.put(c, new ArrayList<DataPoint>());
			justSelected.put(c, new ArrayList<DataPoint>());
			interactedPoints(c, interactedPoints.get(c), justSelected.get(c), justDeselected.get(c));
		}
		//update model 
		for(Curve c : justSelected.keySet()){
			ArrayList<DataPoint> selected = justSelected.get(c);
			if(selected.size() == 0){
				continue;
			}
			if(singlePointSelection || pointSelectionMode == PointSelectionMode.Closest_To_Cursor){
				if(justDeselected.get(c) == null){
					justDeselected.put(c, new ArrayList<DataPoint>());
				}
				justDeselected.get(c).addAll(c.singleSelect(selected.get(0)));
				for(int i = 1; i < selected.size(); i++){
					selected.remove(i);
				}
			}
			else{
				for(DataPoint dp : selected){
					c.select(dp);
				}
			}
		}
		for(Curve c : justDeselected.keySet()){
			ArrayList<DataPoint> deselected = justDeselected.get(c);
			for(DataPoint dp : deselected){
				c.deselect(dp);
			}
		}

		for(LineChartInteractiveModule im : interactiveModules){
			im.pointSelection(justSelected, justDeselected);
		}

		//fire events
		if(fireSelect){
			for(Curve c : justSelected.keySet()){
				ArrayList<DataPoint> selected = justSelected.get(c);
				for(DataPoint dp : selected){
					PointSelectionEvent e = new PointSelectionEvent(true, dp, c);
					moduleAssist.getEventManager().fireEvent(e);
					XYDataEntry entry = dp.data;
					if(dp.containsHiddenData()){
						entry = dp.filteredPoints.get(0).data;
					}
					DataEntrySelectionEvent de = new DataEntrySelectionEvent((AbstractDataEntry) entry, this, true);
					moduleAssist.getEventManager().fireEvent(de);
				}
			}
		}
		if(fireDeselect){
			for(Curve c : justDeselected.keySet()){
				ArrayList<DataPoint> deselected = justDeselected.get(c);
				for(DataPoint dp : deselected){
					PointSelectionEvent e = new PointSelectionEvent(false, dp, c);
					moduleAssist.getEventManager().fireEvent(e);
					XYDataEntry entry = dp.data;
					if(dp.containsHiddenData()){
						entry = dp.filteredPoints.get(0).data;
					}
					DataEntrySelectionEvent de = new DataEntrySelectionEvent((AbstractDataEntry) entry, this, false);
					moduleAssist.getEventManager().fireEvent(de);
				}
			}
		}
	}

	/**
	 * Determines the state of the given interacted points
	 * @param curve
	 * @param interactedPoints
	 * @param selected 
	 * @param deselected
	 */
	private void interactedPoints(Curve curve, ArrayList<DataPoint> interactedPoints, ArrayList<DataPoint> selected, ArrayList<DataPoint> deselected){
		//if a point was selected and still selected: no state change, any other way: state change
		if(pointSelectionMode == PointSelectionMode.Closest_To_Cursor || pointSelectionMode == PointSelectionMode.On_Over){
			for(DataPoint dp : interactedPoints){
				if(!curve.isPointSelected(dp)){
					selected.add(dp);					
				}
			}
			for(DataPoint dp : curve.getSelectedPoints()){
				if(!interactedPoints.contains(dp)){
					deselected.add(dp);
				}
			}
		}
		//if interacted -> state change
		else{
			for(DataPoint dp : interactedPoints){
				if(curve.isPointSelected(dp)){
					deselected.add(dp);
				}
				else{
					selected.add(dp);
				}
			}
		}
	}

	protected void setDataPoint(DataPoint dataPoint) {
		double[] canvasPos = getCanvasPosition(dataPoint.data.getX(), dataPoint.data.getY());
		dataPoint.canvasX = canvasPos[0];
		dataPoint.canvasY = canvasPos[1];
		dataPoint.isInViewport = dataPoint.data.getX() >= xAxis.getMin() && dataPoint.data.getX() <= xAxis.getMax() &&
				dataPoint.data.getY() >= yAxis.getMin() && dataPoint.data.getY() <= yAxis.getMax();
	}

	public boolean isSinglePointSelection() {
		return singlePointSelection;
	}

	public void setSinglePointSelection(boolean singlePointSelection) {
		this.singlePointSelection = singlePointSelection;
	}

	public PointFilter getPointFilter() {
		return pointFilter;
	}

	public void setPointFilter(PointFilter pointFilter) {
		this.pointFilter = pointFilter;
		pointFilter.setLineChart(this);
	}

	public void addInteractiveModule(LineChartInteractiveModule module){
		interactiveModules.add(module);
		module.attach(moduleAssist, this);
	}

	/**
	 * @return the Curves
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
		return pointSelectionMode;
	}

	/**
	 * @param selectPoint the selectPoint to set
	 */
	public void setPointSelectionMode(PointSelectionMode selectPoint) {
		this.pointSelectionMode = selectPoint;
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

	
	@Override
	public void onSelect(PointSelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onDeselect(PointSelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

}