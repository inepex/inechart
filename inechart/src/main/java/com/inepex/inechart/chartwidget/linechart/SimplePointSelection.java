package com.inepex.inechart.chartwidget.linechart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.event.InteractionType;
import com.inepex.inechart.chartwidget.event.PointInteractionEvent;
import com.inepex.inechart.chartwidget.label.AnnotationHelper;
import com.inepex.inechart.chartwidget.label.BubbleBox;
import com.inepex.inechart.chartwidget.shape.Circle;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.chartwidget.shape.Shape;
import com.inepex.inegraphics.impl.client.DrawingAreaGWT;
import com.inepex.inegraphics.impl.client.MouseAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;


/**
 * 
 * Selected point - remains selected until unselected
 * Touched point - transient state
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class SimplePointSelection extends LineChartInteractiveModule {

	protected TreeMap<Curve, Shape> selectedPointShapes;
	protected Shape defaultSelectedPointShape;
	protected TreeMap<Curve, Shape> touchedPointShapes;
	protected Shape defaultTouchedPointShape;
	protected TreeMap<Curve, Layer> canvasPerCurve;

	/**
	 * a circle representing the interactive face of the point
	 * ignored if non-positive
	 */
	int pointMouseOverRadius;
	/**
	 * a point gets selected via this action
	 */
	InteractionMode pointSelectionMode;
	/**
	 * a point gets touched via this action
	 */
	InteractionMode pointTouchMode;
	/**
	 * only 1 point can both be selected and touched,
	 * but there can be a selected and a touched point concurrently
	 */
	boolean singlePointSelection;
	/**
	 * stores the interactive GOs per curve
	 */
	TreeMap<Curve, GraphicalObjectContainer> interactiveGOsPerCurve;
	/**
	 * stores the datapoint related to a GO
	 */
	TreeMap<GraphicalObject, DataPoint> interactivePoints;
	/**
	 * Container for touched points per curve,
	 * the selected points are stored in {@link Curve}
	 */
	TreeMap<Curve, ArrayList<DataPoint>> touchedPoints;

	protected boolean displayAnnotationTouch;
	protected boolean displayAnnotationSelect;
	protected TreeMap<Curve, BubbleBox> annotationPerCurve;
	protected BubbleBox defaultAnnotation;
	protected TreeMap<Curve, ArrayList<BubbleBox>> displayedAnnotationPerCurve;
	protected TreeMap<Curve, String> xFormats;
	protected TreeMap<Curve, String> yFormats;

	protected boolean canSelectOnlyTouchedPoint; //TODO

	public SimplePointSelection() {
		selectedPointShapes = new TreeMap<Curve, Shape>();
		touchedPointShapes = new  TreeMap<Curve, Shape>();
		touchedPoints = new  TreeMap<Curve, ArrayList<DataPoint>>();
		canvasPerCurve = new TreeMap<Curve, Layer>();
		interactiveGOsPerCurve = new TreeMap<Curve, GraphicalObjectContainer>();
		interactivePoints = new TreeMap<GraphicalObject, DataPoint>();
		displayAnnotationTouch = true;
		displayAnnotationSelect = true;
		canSelectOnlyTouchedPoint = true;
		displayedAnnotationPerCurve = new TreeMap<Curve, ArrayList<BubbleBox>>();
		annotationPerCurve = new TreeMap<Curve, BubbleBox>();
		pointSelectionMode = Defaults.selectInteractionMode;
		pointTouchMode = Defaults.touchInteractionMode;
		pointMouseOverRadius = Defaults.pointMouseOverRadius;
		singlePointSelection = true;
		xFormats = new TreeMap<Curve, String>();
		yFormats = new TreeMap<Curve, String>();
	}

	/**
	 * Creates an invisible {@link GraphicalObject} representing the mouseOver area of the given {@link DataPoint}
	 * and stores it
	 * @param curve
	 * @param point
	 * @param shape
	 * @param registeredInteractivePoints
	 */
	protected void createInteractivePoint(Curve curve, DataPoint point, Shape shape){
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
	}

	
	@Override
	public void onClick(ClickEvent event) {
		handleMouseEvents(event);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
	}

	@Override
	public void onMouseOver(MouseEvent<?> event) {
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
	}

	@Override
	public void onMouseOut(MouseEvent<?> event) {
		handleMouseEvents(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		handleMouseEvents(event);
	}

	/**
	 * Handles Click and Move events:
	 * -finds related points
	 * (-update model, fires events and redraws layers, by calling {@link #updatePointStates(TreeMap, TreeMap)})
	 * @param event
	 */
	protected void handleMouseEvents(MouseEvent<?> event){
		TreeMap<Curve, ArrayList<DataPoint>> mouseOverPointsPerCurve = new TreeMap<Curve, ArrayList<DataPoint>>();
		TreeMap<Curve,  ArrayList<DataPoint>> closestToCursorPointsPerCurve = new TreeMap<Curve, ArrayList<DataPoint>>();
		int[] coords = lineChart.getCoords(event);
		//check if we need mouse position and interactive GO related points
		if( pointSelectionMode == InteractionMode.On_Click || pointSelectionMode == InteractionMode.On_Over || pointTouchMode == InteractionMode.On_Click || pointTouchMode == InteractionMode.On_Over){
			for(Curve c : interactiveGOsPerCurve.keySet()){
				if(!c.visible || !c.hasPoint && pointMouseOverRadius <= 0){
					continue;
				}
				mouseOverPointsPerCurve.put(c, getMouseOverPoints(c, coords));
			}
		}
		//check if we need the closest to cursor points
		if( pointSelectionMode == InteractionMode.Closest_To_Cursor || pointTouchMode == InteractionMode.Closest_To_Cursor){
			for(Curve c : interactiveGOsPerCurve.keySet()){
				if(!c.visible){
					continue;
				}
				closestToCursorPointsPerCurve.put(c, new ArrayList<DataPoint>());
				closestToCursorPointsPerCurve.get(c).add(lineChart.getClosestDataToPoint(coords, c));
			}
		}	
		TreeMap<Curve, ArrayList<DataPoint>> selectInteractedPointsPerCurve = null;
		if((pointSelectionMode == InteractionMode.On_Click && event instanceof ClickEvent) || (pointSelectionMode == InteractionMode.On_Over && event instanceof MouseMoveEvent)){
			selectInteractedPointsPerCurve = mouseOverPointsPerCurve;
		}
		else{
			selectInteractedPointsPerCurve = new TreeMap<Curve, ArrayList<DataPoint>>();
		}
		TreeMap<Curve, ArrayList<DataPoint>> touchedPointsPerCurve = null;
		if(pointTouchMode == InteractionMode.Closest_To_Cursor){
			touchedPointsPerCurve = closestToCursorPointsPerCurve;
		}
		else if((pointTouchMode == InteractionMode.On_Click && event instanceof ClickEvent) || (pointTouchMode == InteractionMode.On_Over && event instanceof MouseMoveEvent)){
			touchedPointsPerCurve = mouseOverPointsPerCurve;
		}
		else{
			touchedPointsPerCurve = new TreeMap<Curve, ArrayList<DataPoint>>();
		}
		updatePointStates(coords, selectInteractedPointsPerCurve, touchedPointsPerCurve);

	}

	protected void singleSelectClosestToCoords(int[] coords, Curve curve, ArrayList<DataPoint> selectedPoints, ArrayList<DataPoint> deselectedPoints){
		if(selectedPoints.size() == 0){
			return;
		}
		//determine closest
		DataPoint selected = null;
		double dist = Double.MAX_VALUE;
		for(DataPoint dp : selectedPoints){
			double actD = getDistance(coords, dp);
			if(actD < dist){
				dist = actD;
				selected = dp;
			}
		}
		deselectedPoints.addAll(curve.singleSelect(selected));
		Iterator<DataPoint> it = selectedPoints.iterator();
		while(it.hasNext()){
			if(it.next() != selected){
				it.remove();
			}
		}
	}
	
	protected double getDistance(int[] coords, DataPoint dp){
		return Math.sqrt(Math.pow(coords[0] - dp.canvasX, 2) + Math.pow(coords[1] - dp.canvasY, 2));
	}
	
	/**
	 * Updates selection / touched point models, fires events and redraws layer
	 * @param selectInteractionPoints
	 * @param touchInteractionPoints
	 */
	protected void updatePointStates(int[] coords, TreeMap<Curve, ArrayList<DataPoint>> selectInteractionPoints, TreeMap<Curve, ArrayList<DataPoint>> touchInteractionPoints){
		/* point selection */
		TreeMap<Curve, ArrayList<DataPoint>> justSelected = new TreeMap<Curve, ArrayList<DataPoint>>();
		TreeMap<Curve, ArrayList<DataPoint>> justDeselected = new TreeMap<Curve, ArrayList<DataPoint>>();
		for(Curve c : selectInteractionPoints.keySet()){
			justDeselected.put(c, new ArrayList<DataPoint>());
			justSelected.put(c, new ArrayList<DataPoint>());
			determinePointSelectionState(c, selectInteractionPoints.get(c), justSelected.get(c), justDeselected.get(c));
		}
		//update model 
		for(Curve c : justSelected.keySet()){
			ArrayList<DataPoint> selected = justSelected.get(c);
			if(selected.size() == 0){
				continue;
			}
			if(singlePointSelection || pointSelectionMode == InteractionMode.Closest_To_Cursor){
				if(justDeselected.get(c) == null){
					justDeselected.put(c, new ArrayList<DataPoint>());
				}
				singleSelectClosestToCoords(coords, c, selected, justDeselected.get(c));
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
		/* point touch */
		TreeMap<Curve, ArrayList<DataPoint>> actualTouched = new TreeMap<Curve, ArrayList<DataPoint>>();
		TreeMap<Curve, ArrayList<DataPoint>> touchChange = new TreeMap<Curve, ArrayList<DataPoint>>();
		if(touchInteractionPoints != null){
			for(Curve c : touchInteractionPoints.keySet()){
				actualTouched.put(c, new ArrayList<DataPoint>());
				for(DataPoint dp : touchInteractionPoints.get(c)){
					if(!justSelected.containsKey(c) || !justSelected.get(c).contains(dp)){
						actualTouched.get(c).add(dp);
						if(singlePointSelection || pointTouchMode == InteractionMode.Closest_To_Cursor){
							break;
						}
					}
				}
			}
		}
		/* fire events */
		for(Curve c : justDeselected.keySet()){
			for(DataPoint dp : justDeselected.get(c)){
				moduleAssist.getEventManager().fireEvent(new PointInteractionEvent(InteractionType.Deselected, dp, c));
				lineChart.fireDataEntrySelectedEvent(c, dp);
			}
		}
		for(Curve c : justSelected.keySet()){
			for(DataPoint dp : justSelected.get(c)){
				moduleAssist.getEventManager().fireEvent(new PointInteractionEvent(InteractionType.Selected, dp, c));
				lineChart.fireDataEntrySelectedEvent(c, dp);
			}
		}
		for(Curve c : actualTouched.keySet()){
			for(DataPoint dp : actualTouched.get(c)){
				if(!touchedPoints.containsKey(c) || !touchedPoints.get(c).contains(dp)){
					moduleAssist.getEventManager().fireEvent(new PointInteractionEvent(InteractionType.Touched, dp, c));
					ArrayList<DataPoint> changed = touchChange.get(c);
					if(changed == null){
						changed = new ArrayList<DataPoint>();
						touchChange.put(c, changed);
					}
					changed.add(dp);
				}
			}
		}

		for(Curve c : touchedPoints.keySet()){
			for(DataPoint dp : touchedPoints.get(c)){
				if(!actualTouched.containsKey(c) || !actualTouched.get(c).contains(dp)){
					ArrayList<DataPoint> changed = touchChange.get(c);
					if(changed == null){
						changed = new ArrayList<DataPoint>();
						touchChange.put(c, changed);
					}
					changed.add(dp);
				}
			}
		}

		touchedPoints.clear();
		touchedPoints = actualTouched;

		for(Curve c : lineChart.curves){
			if((c.hasPoint || pointMouseOverRadius > 0 )
					//					|| pointSelectionMode == InteractionMode.Closest_To_Cursor || pointTouchMode == InteractionMode.Closest_To_Cursor)
					&& (justDeselected.containsKey(c) || justSelected.containsKey(c) || touchChange.containsKey(c))
					){
				updateLayer(c);
			}
		}
		moduleAssist.updateLayerOrder();
	}

	/**
	 * Determines the state of the given (selection) interacted points
	 * @param curve
	 * @param interactedPoints
	 * @param selected 
	 * @param deselected
	 */
	private void determinePointSelectionState(Curve curve, ArrayList<DataPoint> interactedPoints, ArrayList<DataPoint> selected, ArrayList<DataPoint> deselected){
		if(interactedPoints == null && pointSelectionMode == InteractionMode.Closest_To_Cursor && curve.getSelectedPoints().size() > 0){
			deselected.addAll(curve.getSelectedPoints());
			return;
		}
		//if a point was selected and still selected: no state change, any other way: state change
		if(pointSelectionMode == InteractionMode.Closest_To_Cursor || pointSelectionMode == InteractionMode.On_Over){
			for(DataPoint dp : interactedPoints){
				if(!curve.isPointSelected(dp) && 
					( !canSelectOnlyTouchedPoint || touchedPoints.get(curve).contains(dp) )){
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
				else if(!canSelectOnlyTouchedPoint || touchedPoints.get(curve).contains(dp)){
					selected.add(dp);
				}
			}
		}
	}
	
	protected Shape getTouchedShape(Curve curve){
		Shape shape = touchedPointShapes.get(curve);
		if(shape == null){
			shape = defaultTouchedPointShape;
		}
		if(shape == null){
			shape = Defaults.touchedPoint();
			shape.getProperties().getLineProperties().getLineColor().setColor(curve.dataSet.getColor().getColor());
		}
		return shape;
	}

	protected Shape getSelectedShape(Curve curve) {
		Shape shape = selectedPointShapes.get(curve);
		if(shape == null){
			shape = defaultSelectedPointShape;
		}
		if(shape == null){
			shape = Defaults.selectedPoint();
			shape.getProperties().getLineProperties().getLineColor().setColor(curve.dataSet.getColor().getColor());
		}
		return shape;
	}

	protected ArrayList<DataPoint> getMouseOverPoints(Curve curve, int[] coords){
		ArrayList<DataPoint> list = new ArrayList<DataPoint>();
		for(GraphicalObject go : interactiveGOsPerCurve.get(curve).getGraphicalObjects()){
			if(MouseAssist.isMouseOver(coords, go) && interactivePoints.containsKey(go)){
				list.add(interactivePoints.get(go));
			}
		}
		return list;
	}

	protected void checkLayer(Curve c){
		if(!canvasPerCurve.containsKey(c)){
			Layer lyr = new Layer(Layer.TO_TOP);
			moduleAssist.addCanvasToLayer(lyr);
			lineChart.linkedLayersPerCurve.get(c).addLayer(lyr);
			canvasPerCurve.put(c, lyr);
			moduleAssist.updateLayerOrder();
		}
	}

	@Override
	protected void update() {
		for(Curve c : lineChart.curves){
			if(c.visible){
				if(c.hasPoint || pointMouseOverRadius > 0){
					updateLayer(c);
				}
			}
			else{
				clearLayer(c);
			}
		}
	}

	protected void clearLayer(Curve curve){
		if(canvasPerCurve.containsKey(curve)){
			canvasPerCurve.get(curve).getCanvas().removeAllGraphicalObjects();
			canvasPerCurve.get(curve).getCanvas().update();
		}
		if(displayedAnnotationPerCurve.containsKey(curve)){
			for(BubbleBox bb : displayedAnnotationPerCurve.get(curve)){
				moduleAssist.getLabelFactory().removeStyledLabel(bb);
			}
			displayedAnnotationPerCurve.get(curve).clear();
		}
		if(interactiveGOsPerCurve.containsKey(curve)){
			for(GraphicalObject go : interactiveGOsPerCurve.get(curve).getGraphicalObjects()){
				if(interactivePoints.containsKey(go)){
					interactivePoints.remove(go);
				}
			}
			interactiveGOsPerCurve.get(curve).removeAllGraphicalObjects();
		}
	}

	/**
	 * Redraws the complete layer (selected and touched points)
	 * and creates interactive GOs for mouse events.
	 * @param curve
	 */
	protected void updateLayer(Curve curve){
		if(!curve.visible){
			return;
		}
		checkLayer(curve);
		DrawingAreaGWT canvas = canvasPerCurve.get(curve).getCanvas();

		clearLayer(curve);
		
		if(!interactiveGOsPerCurve.containsKey(curve)){
			interactiveGOsPerCurve.put(curve, new GraphicalObjectContainer());
		}
	
		ArrayList<DataPoint> selectedPoints = curve.getSelectedPoints();
		Shape selected = getSelectedShape(curve);
		Shape touched = getTouchedShape(curve);
				
		for(DataPoint dp : curve.dataPoints){
			if(dp.isInViewport){
				boolean displayBB = false;
				Shape shape;
				//a selected state point found
				if(selectedPoints.contains(dp)){
					shape =  selected;
					displayBB = displayAnnotationSelect;
				}
				//a touched point found
				else if(touchedPoints.containsKey(curve) && touchedPoints.get(curve).contains(dp)){
					shape = touched;
					displayBB = displayAnnotationTouch;
				}
				else{
					shape = null;
				}
				if(shape != null){
					canvas.addAllGraphicalObject(lineChart.createPoint(curve, dp, shape));
					if(displayBB){
						createAnnotationForPoint(curve, dp, shape);
					}
				}
				createInteractivePoint(curve, dp, selected);
			}
		}
		canvas.update();
	}

	protected void createAnnotationForPoint(Curve curve, DataPoint dp, Shape shape){
		BubbleBox annotation = annotationPerCurve.get(curve);
		if(annotation == null){
			annotation = defaultAnnotation;					
		}
		if(annotation == null){
			annotation = Defaults.touchedAnnotation();					
		}
		BubbleBox toDisplay = new BubbleBox(annotation);
		String xFormat = xFormats.get(curve);
		String yFormat = yFormats.get(curve);
		if(xFormat == null){
			if(lineChart.getXAxis().getAxisDataType() == AxisDataType.Time){
				xFormat = Defaults.dateFormat;
			}
			else{
				xFormat = Defaults.numberFormat;
			}
		}
		if(yFormat == null){
			if(lineChart.getYAxis().getAxisDataType() == AxisDataType.Time){
				yFormat = Defaults.dateFormat;
			}
			else{
				yFormat = Defaults.numberFormat;
			}
		}
		toDisplay.getText().setText(
				AnnotationHelper.replaceXYValues(
						annotation.getText().getText(),
						dp.getData().getX(), dp.getData().getY(), 
						xFormat, yFormat, 
						lineChart.getXAxis().getAxisDataType(), lineChart.getYAxis().getAxisDataType()));
		toDisplay.setLeft((int) dp.getCanvasX());
		toDisplay.setTop((int) dp.getCanvasY());
		int distance = (int) (shape.getProperties().getLineProperties().getLineWidth() / 2);
		if(shape instanceof Circle){
			distance += (int) ((Circle) shape).getRadius();
		}
		else{
			distance += (int) ((Rectangle) shape).getHeight();
		}
		toDisplay.setDistanceFromPoint(distance);
		moduleAssist.getLabelFactory().addAndDisplayStyledLabel(toDisplay);
		ArrayList<BubbleBox> displayed = displayedAnnotationPerCurve.get(curve);
		if(displayed == null){
			displayed = new ArrayList<BubbleBox>();
			displayedAnnotationPerCurve.put(curve, displayed);
		}
		displayed.add(toDisplay);
	}

	public Shape getDefaultSelectedPointShape() {
		return defaultSelectedPointShape;
	}

	public void setDefaultSelectedPointShape(Shape defaultSelectedPointShape) {
		this.defaultSelectedPointShape = defaultSelectedPointShape;
	}

	public TreeMap<Curve, Shape> getSelectedPointShapes() {
		return selectedPointShapes;
	}

	public void setSelectedPointShapes(TreeMap<Curve, Shape> selectedPointShapes) {
		this.selectedPointShapes = selectedPointShapes;
	}

	public TreeMap<Curve, Shape> getTouchedPointShapes() {
		return touchedPointShapes;
	}

	public void setTouchedPointShapes(TreeMap<Curve, Shape> touchedPointShapes) {
		this.touchedPointShapes = touchedPointShapes;
	}

	public Shape getDefaultTouchedPointShape() {
		return defaultTouchedPointShape;
	}

	public void setDefaultTouchedPointShape(Shape defaultTouchedPointShape) {
		this.defaultTouchedPointShape = defaultTouchedPointShape;
	}

	public boolean isSinglePointSelection() {
		return singlePointSelection;
	}

	public void setSinglePointSelection(boolean singlePointSelection) {
		this.singlePointSelection = singlePointSelection;
	}

	public TreeMap<Curve, BubbleBox> getAnnotationPerCurve() {
		return annotationPerCurve;
	}

	public void setAnnotationPerCurve(TreeMap<Curve, BubbleBox> annotationPerCurve) {
		this.annotationPerCurve = annotationPerCurve;
	}

	public BubbleBox getDefaultAnnotation() {
		return defaultAnnotation;
	}

	public void setDefaultAnnotation(BubbleBox defaultAnnotation) {
		this.defaultAnnotation = defaultAnnotation;
	}

	public TreeMap<Curve, ArrayList<BubbleBox>> getDisplayedAnnotationPerCurve() {
		return displayedAnnotationPerCurve;
	}

	public void setDisplayedAnnotationPerCurve(
			TreeMap<Curve, ArrayList<BubbleBox>> displayedAnnotationPerCurve) {
		this.displayedAnnotationPerCurve = displayedAnnotationPerCurve;
	}

	public InteractionMode getPointSelectionMode() {
		return pointSelectionMode;
	}

	public void setPointSelectionMode(InteractionMode pointSelectionMode) {
		this.pointSelectionMode = pointSelectionMode;
	}

	public InteractionMode getPointTouchMode() {
		return pointTouchMode;
	}

	public void setPointTouchMode(InteractionMode pointTouchMode) {
		this.pointTouchMode = pointTouchMode;
	}

	public boolean isDisplayAnnotationTouch() {
		return displayAnnotationTouch;
	}
	
	public void setDisplayAnnotationTouch(boolean displayAnnotationTouch) {
		this.displayAnnotationTouch = displayAnnotationTouch;
	}
	
	public boolean isDisplayAnnotationSelect() {
		return displayAnnotationSelect;
	}
	
	public void setDisplayAnnotationSelect(boolean displayAnnotationSelect) {
		this.displayAnnotationSelect = displayAnnotationSelect;
	}
	
	public boolean isCanSelectOnlyTouchedPoint() {
		return canSelectOnlyTouchedPoint;
	}
	
	public void setCanSelectOnlyTouchedPoint(boolean canSelectOnlyTouchedPoint) {
		this.canSelectOnlyTouchedPoint = canSelectOnlyTouchedPoint;
	}

}
