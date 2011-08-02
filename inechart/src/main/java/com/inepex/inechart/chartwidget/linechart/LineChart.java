package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.axes.Axes;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.label.LegendEntry;
import com.inepex.inechart.chartwidget.label.Text;
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

public class LineChart extends IneChartModule2D {

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

	/**
	 * Overlay canvas for speed up point selection.
	 * All selected points are drawn over this area,
	 * while all normal state points drawn on inherited canvas.
	 */
	DrawingArea overlay;
	boolean useOverlay;
	TreeMap<Curve, GraphicalObjectContainer> overlayGosPerCurve; 
	// model fields
	ArrayList<Curve> curves = new ArrayList<Curve>();
	int highestZIndex = 1;

	PointSelectionMode pointSelectionMode;

	/**
	 * stores the calculated points inside viewport
	 */
	TreeMap<Curve, ArrayList<double[]>> calculatedPointsPerCurve;
	TreeMap<Curve, ArrayList<double[]>> selectedPointsPerCurve;
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
	
	public LineChart(DrawingArea canvas, LabelFactoryBase labelFactory, Axes axes) {
		this(canvas, labelFactory, axes, null);
		useOverlay = false;
	}
	
	public LineChart(DrawingArea canvas, LabelFactoryBase labelFactory, Axes axes, DrawingArea overLay) {
		super(canvas,labelFactory, axes);
		this.overlay = overLay;
		calculatedPointsPerCurve = new TreeMap<Curve, ArrayList<double[]>>();
		selectedPointsPerCurve = new TreeMap<Curve, ArrayList<double[]>>();
		visiblePathPerCurve = new TreeMap<Curve, Path>();
		fillPathPerCurve = new TreeMap<Curve, Path>();
		lineChartGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		pointChartGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		useOverlay = true;
		// defaults
		pointSelectionMode = PointSelectionMode.Closest_To_Cursor;
		autoScaleViewport = true;
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
	public void updateModulesAxes() {
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
		super.updateModulesAxes();
	}

	@Override
	public void update() {
		if (curves == null || curves.size() == 0)
			return;
		graphicalObjectContainer.removeAllGraphicalObject();
		//do model to canvas calculations
		for (Curve curve : curves) {
			if(curve.hasLine){
				createVisiblePathForCurve(curve);
			}
			if(shouldCalculateFillPathForCurve(curve)){
				createFillPathForCurve(curve);
			}
			if(curve.hasPoint){
				calculatePointsForCurve(curve);
			}
		}
		//remove previous and create new GOs
		for (Curve curve : curves) {
			if(curve.hasLine){
				createLineChartGOs(curve);
			}
			if(curve.hasPoint){
				createPointChartGOs(curve);
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
		overlay.removeAllGraphicalObject();
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
		curve.dataSet.update();
		List<double[]> dataPairs = curve.dataSet.getDataPairs();
		ArrayList<double[]> calculatedPoints = new ArrayList<double[]>();
		ArrayList<double[]> selectedPoints = new ArrayList<double[]>();
		for(double[] selected : curve.selectedPoints){
			selectedPoints.add(getCanvasPosition(selected[0], selected[1]));
		}

		for(double[] dataPair : dataPairs){
			double[] calculated = getCanvasPosition(dataPair[0], dataPair[1]);	
			if(dataPair[0] > xAxis.getMax()){
				if(curve.dataSet.isSortable()){
					break;
				}
			}
			else if(dataPair[0] >= xAxis.getMin() && dataPair[1] >= yAxis.getMin() && dataPair[1] <= yAxis.getMax()){
				calculatedPoints.add(calculated);
			}
		}
		selectedPointsPerCurve.put(curve, selectedPoints);
		calculatedPointsPerCurve.put(curve, calculatedPoints);
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
			if(lastDataPair != null && !curve.discontinuities.contains(dataPair)){
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
		visiblePathPerCurve.put(curve, path);
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
		GraphicalObjectContainer gos = new GraphicalObjectContainer();
		lineChartGOsPerCurve.put(curve, gos);
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
	 */
	protected void createPointChartGOs(Curve curve) {
		ArrayList<double[]> calculatedPoints = calculatedPointsPerCurve.get(curve);
		ArrayList<double[]> selectedPoints = selectedPointsPerCurve.get(curve);
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		pointChartGOsPerCurve.put(curve, goc);
		if(calculatedPoints == null || calculatedPoints.size() == 0)
			return;
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
				if(selected != null){
					selected.setShadowColor(curve.shadowColor);
					selected.setShadowOffsetX(curve.shadowOffsetX);
					selected.setShadowOffsetY(curve.shadowOffsetY);
				}
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
		if(curve.useCurveLineForNormalPoint){
			if(normal != null){
				normal.getProperties().setLineProperties(curve.lineProperties != null ? curve.lineProperties : new LineProperties(Defaults.lineWidth, curve.dataSet.getColor()));
			}
		}
		for(double[] point : calculatedPoints){
			//selected
			if(selectedPoints.contains(point)){
				if(selected != null){
					if(useOverlay){
						//we put normal point on backing canvas
						if(normal != null){
							goc.addAllGraphicalObject(createPoint(point, normal));
						}
						overlayGosPerCurve.get(curve).addAllGraphicalObject(createPoint(point, selected));
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

	protected GraphicalObjectContainer createPoint(double[] point, Shape shape){
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		for(GraphicalObject go : shape.toGraphicalObjects()){
			if (shape instanceof Circle) {
				go.setBasePointX(point[0]);
				go.setBasePointY(point[1]);
			}
			else if (shape instanceof Rectangle) {
				go.setBasePointX(point[0] - ((Rectangle) shape).getWidth() / 2);
				go.setBasePointY(point[1] - ((Rectangle) shape).getHeight() / 2);
			}
			goc.addGraphicalObject(go);
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
	public List<LegendEntry> getLegendEntries() {
		if(legendEntries == null){
			ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
			for(Curve c : curves){
				entries.add(new LegendEntry(new Text(c.dataSet.getName()), c.dataSet.getColor()));
			}
			return entries;
		}
		else return legendEntries;
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
	
	@Override
	protected void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the curves
	 */
	public List<Curve> getCurves() {
		return curves;
	}

}
