package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Event;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.data.XYDataEntry;
import com.inepex.inechart.chartwidget.event.DataEntrySelectionEvent;
import com.inepex.inechart.chartwidget.event.DataSetChangeEvent;
import com.inepex.inechart.chartwidget.event.PointSelectionEvent;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties.LineStyle;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.impl.client.MouseAssist;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;

public class LineChart2 extends IneChartModule2D{


	int pointMouseOverRadius;

	PointSelectionMode selectPoint;
	boolean singlePointSelection;

	// model fields
	ArrayList<Curve2> curves;
	int highestZIndex = 1;
	int zIndexDiffBetweenCurves = 3;

	TreeMap<Curve2, Path> visiblePathPerCurve;
	TreeMap<Curve2, Path> fillPathPerCurve;

	PointFilter pointFilter;

	/**
	 * all {@link GraphicalObject}s related to line chart per Curve2
	 */
	TreeMap<Curve2, GraphicalObjectContainer> lineChartGOsPerCurve;

	/**
	 * all {@link GraphicalObject}s related to point chart per Curve2
	 */
	TreeMap<Curve2, GraphicalObjectContainer> pointChartGOsPerCurve;

	TreeMap<Curve2, GraphicalObjectContainer> interactiveGOsPerCurve;
	TreeMap<GraphicalObject, DataPoint2> interactivePoints;

	TreeMap<Curve2, DrawingArea> canvasPerCurve;

	public LineChart2(ModuleAssist moduleAssist) {
		super(moduleAssist);

		curves = new ArrayList<Curve2>();
		visiblePathPerCurve = new TreeMap<Curve2, Path>();
		fillPathPerCurve = new TreeMap<Curve2, Path>();
		lineChartGOsPerCurve = new TreeMap<Curve2, GraphicalObjectContainer>();
		pointChartGOsPerCurve = new TreeMap<Curve2, GraphicalObjectContainer>();
		interactiveGOsPerCurve = new TreeMap<Curve2, GraphicalObjectContainer>();
		interactivePoints = new TreeMap<GraphicalObject, DataPoint2>();
		canvasPerCurve = new TreeMap<Curve2, DrawingArea>();

		// defaults
		selectPoint = Defaults.selectPoint;
		autoScaleViewport = true;
		pointMouseOverRadius = Defaults.pointMouseOverRadius;
		singlePointSelection = true;
		pointFilter = new PointFilter();
	}

	public void addCurve2(Curve2 curve) {
		if (curve == null){
			return;
		}
		if (curves == null){
			curves = new ArrayList<Curve2>();
		}
		curves.add(curve);
		if (curve.zIndex == Integer.MIN_VALUE){
			highestZIndex += zIndexDiffBetweenCurves;
			curve.zIndex = highestZIndex;
		}
		else if (curve.zIndex > highestZIndex){
			highestZIndex = curve.zIndex;
		}

		canvasPerCurve.put(curve, moduleAssist.isClientSide() ? moduleAssist.createLayer(this, highestZIndex) : canvas);

	}

	public void removeCurve2(Curve2 curve) {
		if (curve == null)
			return;
		removeAllGORelatedToCurve2(curve);
		if(moduleAssist.isClientSide()){
			moduleAssist.destroyLayer((DrawingAreaGWT) canvasPerCurve.get(curve));
		}
		canvasPerCurve.remove(curve);
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

			for (Curve2 c : curves) {
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
		for (Curve2 curve : curves) {
			calculatePointsForCurve2(curve);
			if(curve.hasLine){
				createVisiblePathForCurve2(curve);
			}
			if(shouldCalculateFillPathForCurve2(curve)){
				createFillPathForCurve2(curve);
			}
		}
		//remove previous and create new GOs
		for (Curve2 curve : curves) {
			if(curve.hasLine){
				createLineChartGOs(curve);
			}
			if(curve.hasPoint){
				createPointChartGOs(curve);
			}
		}
		for(Curve2 curve:curves){
			if(lineChartGOsPerCurve.get(curve) != null){
				canvasPerCurve.get(curve).addAllGraphicalObject(lineChartGOsPerCurve.get(curve));
			}
			if(pointChartGOsPerCurve.get(curve) != null){
				canvasPerCurve.get(curve).addAllGraphicalObject(pointChartGOsPerCurve.get(curve));
			}
			if(moduleAssist.isClientSide()){
				canvasPerCurve.get(curve).update();
			}
		}

		super.update();
	}	

	protected boolean shouldCalculateFillPathForCurve2(Curve2 curve){
		if( curve.autoFill  ||
				(curve.toCurveFills != null && curve.toCurveFills.size() > 0) || 
				(curve.toYFills != null && curve.toYFills.size() > 0)){
			return true;
		}
		else{
			for(Curve2 c : curves){
				if(c == curve)
					continue;
				if(c.toCurveFills != null && c.toCurveFills.containsKey(curve))
					return true;
			}
		}
		return false;
	}

	protected void calculatePointsForCurve2(Curve2 curve){
		List<XYDataEntry> dataPairs = curve.dataSet.getXYDataEntries();
		List<DataPoint2> toRemove = new ArrayList<DataPoint2>();
		for(DataPoint2 selected : curve.selectedPoints){
			if(curve.dataSet.containsXYDataEntry(selected.data)){
				setDataPoint(selected);
			}
			else{
				toRemove.add(selected);
			}
		}
		for(DataPoint2 dp : toRemove){
			curve.selectedPoints.remove(dp);
		}
		curve.discontinuitiesAsPoint.clear();
		curve.dataPoints.clear();
		ArrayList<DataPoint2> points = new ArrayList<DataPoint2>();
		if(curve.dataSet.isSortable()){
			XYDataEntry last = null;
			XYDataEntry beforeVPMin = null;
			XYDataEntry beforeVPMax = null;
			XYDataEntry afterVPMax = null;
			XYDataEntry afterVPMin = null;
			boolean firstAfterVP = true;
			for(XYDataEntry map : dataPairs){
				//bigger than vp min
				if(map.getX() >= xAxis.getMin()){
					if(last != null){
						DataPoint2 dp = createDataPoint(last); 
						points.add(dp);
						if(curve.discontinuities.contains(last.getX())){
							curve.discontinuitiesAsPoint.add(dp);
						}
					}
					DataPoint2 dp = createDataPoint(map); 
					points.add(dp);
					if(curve.discontinuities.contains(map.getX())){
						curve.discontinuitiesAsPoint.add(dp);
					}
				}
				//after vp
				else if(map.getX() > xAxis.getMax()){
					if(firstAfterVP){
						DataPoint2 dp = createDataPoint(map); 
						points.add(dp);
						if(curve.discontinuities.contains(map.getX())){
							curve.discontinuitiesAsPoint.add(dp);
						};
						firstAfterVP = false;
					}
					if(afterVPMin == null){
						afterVPMax = afterVPMin = map;
					}
					else{
						if(afterVPMin.getX() > map.getX()){
							afterVPMin = map;
						}
						if(afterVPMax.getX() < map.getX()){
							afterVPMax = map;
						}
					}
				}
				//before vp
				else{
					if(beforeVPMin == null){
						beforeVPMax = beforeVPMin = map;
					}
					else{
						if(beforeVPMin.getX() > map.getX()){
							beforeVPMin = map;
						}
						if(beforeVPMax.getX() < map.getX()){
							beforeVPMax = map;
						}
					}
				}
				last = map;
			}
			points = pointFilter.filterDataPoints(points);
			if(beforeVPMin != null){
				DataPoint2 dp = createDataPoint(beforeVPMin);
				if(points.size() == 0 || DataPoint2.canvasXComparator.compare(dp, points.get(0)) != 0){
					curve.dataPoints.add(dp);
				}
			}
			if(beforeVPMax != null && beforeVPMax != beforeVPMin){
				DataPoint2 dp = createDataPoint(beforeVPMax);
				if(points.size() == 0 || DataPoint2.canvasXComparator.compare(dp, points.get(0)) != 0){
					curve.dataPoints.add(dp);
				}
			}
			curve.dataPoints.addAll(points);
			if(afterVPMin != null){
				DataPoint2 dp = createDataPoint(afterVPMin);
				if(points.size() == 0 || DataPoint2.canvasXComparator.compare(dp, points.get(points.size() - 1)) != 0){
					curve.dataPoints.add(dp);
				}
			}
			if(afterVPMax != null && afterVPMax != afterVPMin){
				DataPoint2 dp = createDataPoint(afterVPMax);
				if(points.size() == 0 || DataPoint2.canvasXComparator.compare(dp, points.get(points.size() - 1)) != 0){
					curve.dataPoints.add(dp);
				}
			}
		}
		else{
			for(XYDataEntry map : dataPairs){
				points.add(createDataPoint(map));
			}
			points = pointFilter.filterDataPoints(points);
			curve.dataPoints.addAll(points);
		}
	}

	private DataPoint2 createDataPoint(XYDataEntry map){
		DataPoint2 dp = new DataPoint2(map);
		setDataPoint(dp);
		return dp;
	}

	protected void createFillPathForCurve2(Curve2 curve){
		Path path = null;
		double[] lastPair = null;
		double[] lastLineEnd = null;
		int rightEnd = getRightEnd();
		int botEnd = getBottomEnd();
		int width = getWidth();
		int height = getHeight();
		for(DataPoint2 dataPoint : curve.dataPoints){
			double x = dataPoint.canvasX > rightEnd ? rightEnd : (dataPoint.canvasX < leftPadding ? leftPadding : dataPoint.canvasX);
			double y = dataPoint.canvasY > botEnd ? botEnd : (dataPoint.canvasY < topPadding ? topPadding : dataPoint.canvasY);
			if(path == null){
				path = new Path(x, y, curve.zIndex, null, false, true);
			}
			else{
				double[] intersection = DrawingAreaAssist.getIntersection(
						lastPair[0], lastPair[1],
						dataPoint.canvasX, dataPoint.canvasY,
						leftPadding,
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
		fillPathPerCurve.put(curve, path);
	}

	protected void createVisiblePathForCurve2(Curve2 curve){
		double[] lastDataPair = null;
		double[] lastLineEnd = null;
		boolean ready = false;
		Path path = null;
		int rightEnd = getRightEnd();
		int width = getWidth();
		int height = getHeight();
		for(DataPoint2 dataPoint : curve.dataPoints){
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
							leftPadding,
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
	protected void createLineChartGOs(Curve2 curve) {
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
		//		if (curve.toCanvasYFills != null && curve.toCanvasYFills.size() > 0) {
		//			// TODO axis direction
		//			for (int i : curve.toCanvasYFills.keySet()) {
		//				Path fill = new Path(fillPath);
		//				fill.lineTo(fill.getLastPathElement().getEndPointX(), i, false);
		//				fill.lineTo(fill.getBasePointX(), i, false);
		//				fill.lineToBasePoint();
		//				fill.setFill(true);
		//				fill.setzIndex(curve.getZIndex());
		//				fill.setContext(createFillContext(curve.toCanvasYFills.get(i)));
		//				gos.addGraphicalObject(fill);
		//			}
		//		}
		if (curve.toCurveFills != null && curve.toCurveFills.size() > 0) {
			for (Curve2 toCurve2 : curve.toCurveFills.keySet()) {
				Path fill = new Path(fillPath.getBasePointX(),
						fillPath.getBasePointY(), curve.getZIndex(),
						createFillContext(curve.toCurveFills.get(toCurve2)),
						false, true);
				Path otherPath = visiblePathPerCurve.get(toCurve2);

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
	protected void createPointChartGOs(Curve2 curve) {
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

		//		if(curve.useCurve2LinePropertiesForShape){
		//			if(normal != null){
		//				normal.getProperties().setLineProperties(curve.lineProperties != null ? curve.lineProperties : new LineProperties(Defaults.lineWidth, curve.dataSet.getColor()));
		//			}
		//		}


		for(DataPoint2 point : curve.dataPoints){
			if(!point.isInViewport){
				continue;
			}
			goc.addAllGraphicalObject(createPoint(point, normal));
		}
	}

	protected GraphicalObjectContainer createPoint(DataPoint2 point, Shape shape){
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
	protected void removeAllGORelatedToCurve2(Curve2 curve) {
		if (lineChartGOsPerCurve.containsKey(curve)) {
			for (GraphicalObject go : lineChartGOsPerCurve.get(curve).getGraphicalObjects()) {
				canvasPerCurve.get(curve).removeGraphicalObject(go);
			}
		}
		if (pointChartGOsPerCurve.containsKey(curve)) {
			for (GraphicalObject go : pointChartGOsPerCurve.get(curve).getGraphicalObjects()) {
				canvasPerCurve.get(curve).removeGraphicalObject(go);
			}
		}
	}

	static Context createFillContext(Color fillColor) {
		return new Context(fillColor.getAlpha(), Defaults.colorString, 0,
				fillColor.getColor(), 0, 0, Defaults.alpha,
				Defaults.colorString);
	}

	static Context createLineContext(Curve2 Curve2) {
		return new Context(
				Curve2.lineProperties != null ? Curve2.lineProperties.getLineColor().getAlpha() :	Defaults.alpha,
						Curve2.lineProperties != null ? Curve2.lineProperties.getLineColor().getColor() : Curve2.dataSet.getColor().getColor(),
								Curve2.lineProperties != null ? Curve2.lineProperties.getLineWidth() : Defaults.lineWidth,
										Defaults.colorString,
										Curve2.hasShadow ? Curve2.shadowOffsetX : 0,
												Curve2.hasShadow ? Curve2.shadowOffsetY : 0,
														Curve2.shadowColor == null ? Defaults.alpha : Curve2.shadowColor.getAlpha(),
																Curve2.shadowColor == null ? Defaults.colorString : Curve2.shadowColor.getColor());
	}

	@Override
	public TreeMap<String, Color> getLegendEntries() {
		if(legendEntries == null){
			TreeMap<String, Color> entries = new TreeMap<String, Color>();
			for(Curve2 c : curves){
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

	/**
	 * @return the Curve2s
	 */
	public List<Curve2> getCurve2s() {
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
		handleMouseEvents(event);
	}

	@Override
	protected void onMouseUp(MouseUpEvent event) {

	}

	@Override
	protected void onMouseOver(MouseEvent<?> event) {
		
	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {

	}

	@Override
	protected void onMouseOut(MouseEvent<?> event) {
		handleMouseEvents(event);
	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		handleMouseEvents(event);
	}

	protected void handleMouseEvents(MouseEvent<?> event){
		if(!moduleAssist.isClientSide() || !canHandleEvents){
			return;
		}
		TreeMap<Curve2, ArrayList<DataPoint2>> overedPointsPerCurve;
		if( (selectPoint == PointSelectionMode.On_Click && event instanceof ClickEvent && event.getNativeButton() == Event.BUTTON_LEFT) 
				|| (selectPoint == PointSelectionMode.On_Right_Click && event instanceof ClickEvent && event.getNativeButton() == Event.BUTTON_RIGHT) ||
				(selectPoint == PointSelectionMode.On_Over && event instanceof MouseMoveEvent)){
			overedPointsPerCurve = new TreeMap<Curve2, ArrayList<DataPoint2>>();
			
			for(Curve2 c : interactiveGOsPerCurve.keySet()){
				if(!c.hasPoint){
					continue;
				}
				ArrayList<GraphicalObject> over = new ArrayList<GraphicalObject>();
				for(GraphicalObject go : interactiveGOsPerCurve.get(c).getGraphicalObjects()){
					//TODO mouseoverradius
					if(MouseAssist.isMouseOver(getCoords(event), go)){
						over.add(go);
					}
				}
				for(GraphicalObject go : over){
					if(interactivePoints.containsKey(go)){
						ArrayList<DataPoint2> list = overedPointsPerCurve.get(c);
						if(list == null){
							list = new ArrayList<DataPoint2>();
							overedPointsPerCurve.put(c, list);
						}
						list.add(interactivePoints.get(go));
					}
				}
			}
		}
		else if( selectPoint == PointSelectionMode.Closest_To_Cursor && event instanceof MouseMoveEvent){
			overedPointsPerCurve = getClosestToMousePoints(event);
		}
		else{
			return;
		}
		selectPointEvent(overedPointsPerCurve, true, true);
	}

	/**
	 * Returns a Curve2 - closest point to cursor mapping
	 * @param e
	 * @return
	 */
	public TreeMap<Curve2, ArrayList<DataPoint2>> getClosestToMousePoints(MouseEvent<?> e){
		TreeMap<Curve2, ArrayList<DataPoint2>> mouseOver = new TreeMap<Curve2, ArrayList<DataPoint2>>();
		int[] eventLocation = getCoords(e);
		for(Curve2 c : curves){
			if(!c.hasPoint){
				continue;
			}
			DataPoint2 overed = null;
			double closestDiff = Double.MAX_VALUE;
			for(DataPoint2 point : c.dataPoints){
				if(!point.isInViewport){
					continue;
				}
				if(Math.abs(point.canvasX - eventLocation[0]) < closestDiff){
					closestDiff = Math.abs(point.canvasX - eventLocation[0]);
					overed = point;
				}
			}
			if(overed != null){
				mouseOver.put(c, new ArrayList<DataPoint2>());
				mouseOver.get(c).add(overed);
			}
		}
		return mouseOver;
	}

	/**
	 * Updates selected points in curves, fires events and updates interactiva modules
	 * @param selectedPoints
	 * @param fireSelect
	 * @param fireDeselect
	 */
	protected void selectPointEvent(TreeMap<Curve2, ArrayList<DataPoint2>> selectedPoints, boolean fireSelect, boolean fireDeselect){
		TreeMap<Curve2, ArrayList<DataPoint2>> justSelected = new TreeMap<Curve2, ArrayList<DataPoint2>>();
		TreeMap<Curve2, ArrayList<DataPoint2>> justDeselected = new TreeMap<Curve2, ArrayList<DataPoint2>>();

		for(Curve2 c : selectedPoints.keySet()){
			for(DataPoint2 actual:selectedPoints.get(c)){
				if(actual.containsHiddenData()){
					for(DataPoint2 hidden : actual.filteredPoints){
						//currently selected point isn't selected
						if(!c.isPointSelected(hidden)){
							if(justSelected.get(c) == null){
								justSelected.put(c, new ArrayList<DataPoint2>());
							}
							justSelected.get(c).add(hidden);
						}
						//currently selected was previously selected
						else{
							if(justDeselected.get(c) == null){
								justDeselected.put(c, new ArrayList<DataPoint2>());
							}
							justDeselected.get(c).add(hidden);
						}
					}
				}
				else{
					//currently selected point isn't selected
					if(!c.isPointSelected(actual)){
						if(justSelected.get(c) == null){
							justSelected.put(c, new ArrayList<DataPoint2>());
						}
						justSelected.get(c).add(actual);
					}
					//currently selected was previously selected
					else{
						if(justDeselected.get(c) == null){
							justDeselected.put(c, new ArrayList<DataPoint2>());
						}
						justDeselected.get(c).add(actual);
					}
				}
			}
		}
		//update model 
		for(Curve2 c : justSelected.keySet()){
			ArrayList<DataPoint2> selected = justSelected.get(c);
			if(singlePointSelection){
				if(c.selectedPoints.size() > 0){
					for(DataPoint2 dp : c.selectedPoints){
						if(justDeselected.get(c) == null){
							justDeselected.put(c, new ArrayList<DataPoint2>());
						}
						justDeselected.get(c).add(dp);
					}
					c.selectedPoints.clear();
				}
				c.selectedPoints.add(selected.get(0));
			}
			else{
				c.selectedPoints.addAll(selected);
			}
		}
		for(Curve2 c : justDeselected.keySet()){
			ArrayList<DataPoint2> deselected = justDeselected.get(c);
			for(DataPoint2 dp : deselected){
				c.selectedPoints.remove(dp);
			}
		}

		//fire events
		if(fireSelect){
			for(Curve2 c : justSelected.keySet()){
				ArrayList<DataPoint2> selected = justSelected.get(c);
				for(DataPoint2 dp : selected){
					PointSelectionEvent e = new PointSelectionEvent(true, dp, c);
					moduleAssist.getEventManager().fireEvent(e);
				}
			}
		}
		if(fireDeselect){
			for(Curve2 c : justDeselected.keySet()){
				ArrayList<DataPoint2> selected = justSelected.get(c);
				for(DataPoint2 dp : selected){
					PointSelectionEvent e = new PointSelectionEvent(false, dp, c);
					moduleAssist.getEventManager().fireEvent(e);
				}
			}
		}
	}

	private void setDataPoint(DataPoint2 dataPoint) {
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
		for(Curve2 c : curves){
			if(c.hasPoint && event.getDataEntry().getContainer() == c.dataSet && c.dataSet.containsXYDataEntry((XYDataEntry) event.getDataEntry())){
				TreeMap<Curve2, ArrayList<DataPoint2>> selected = new TreeMap<Curve2, ArrayList<DataPoint2>>();
				ArrayList<DataPoint2> selectedList = new ArrayList<DataPoint2>();
				DataPoint2 dp = createDataPoint((XYDataEntry) event.getDataEntry());
				selectedList.add(dp);
				selected.put(c, selectedList);
				selectPointEvent(selected, !event.isSelect(), event.isSelect());
				break;
			}
		}
	}

}