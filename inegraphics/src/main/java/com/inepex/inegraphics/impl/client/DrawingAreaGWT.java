package com.inepex.inegraphics.impl.client;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inegraphics.impl.client.canvas.CanvasWidget;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.gobjects.Arc;
import com.inepex.inegraphics.shared.gobjects.Circle;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.LineTo;
import com.inepex.inegraphics.shared.gobjects.MoveTo;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
import com.inepex.inegraphics.shared.gobjects.QuadraticCurveTo;
import com.inepex.inegraphics.shared.gobjects.Rectangle;
import com.inepex.inegraphics.shared.gobjects.Text;

/**
 * 
 * A client-side {@link DrawingArea} implementation.
 * Can be used with both HTML5 canvas implementation:
 * 		- GWT 2.2+ / {@link Canvas} ('modern' browser support)
 * 		- IneGraphics / {@link CanvasWidget} (supports IE6-IE8 browsers too)
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class DrawingAreaGWT extends DrawingArea implements ClickHandler, MouseDownHandler, MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseUpHandler, MouseWheelHandler{
	
	public static boolean isHTML5Compatible(){
		return Canvas.isSupported();		
	}
	
	protected CanvasWidget canvas;
	protected Canvas canvasGWT;
	protected AbsolutePanel panel;
	protected TextPositioner textPositioner;
	protected boolean mouseOverCanvas = false;
	protected int mouseX, mouseY;
	protected ArrayList<GraphicalObject> interactiveGOs;
	protected ArrayList<GraphicalObject> mouseOverGOs;
	protected HandlerManager hm;
	protected boolean singleMouseSelection = false;

	
	/**
	 * Creates a {@link DrawingArea} with the given dimensions
	 * @param width px
	 * @param height px
	 * @param useCanvas TODO
	 */
	public DrawingAreaGWT(int width, int height, boolean useCanvas) {
		super(width, height);
		this.panel = new AbsolutePanel();
		panel.setPixelSize(width, height);
		if(useCanvas && Canvas.isSupported()){
			canvasGWT = Canvas.createIfSupported();
			//TODO
		}
		else{
			CanvasWidget canvas = new CanvasWidget(width, height);
			this.canvas = canvas;
			textPositioner = new TextPositioner(panel);
		}
		
		panel.add(canvas, 0, 0);
		initEvents();
		clear();
	}
	
	/**
	 * Creates a {@link DrawingArea}
	 * @param canvas {@link CanvasWidget} used for drawing
	 */
	public DrawingAreaGWT(CanvasWidget canvas) {
		super(canvas.getWidth(), canvas.getHeight());
		this.canvas = canvas;
		this.panel = new AbsolutePanel();
		panel.setPixelSize(width, height);
		panel.add(canvas, 0, 0);
		textPositioner = new TextPositioner(panel);
		initEvents();
		clear();
	}

	public DrawingAreaGWT(Canvas canvas){
		super(canvas.getCanvasElement().getWidth(), canvas.getCanvasElement().getHeight());
		this.canvasGWT = canvas;
		this.panel = new AbsolutePanel();
		panel.setPixelSize(width, height);
		panel.add(canvas, 0, 0);
		initEvents();
		clear();
	}
	
	/**
	 * Use this method to add this to a panel
	 * if it uses {@link Canvas} 
	 * else an {@link AbsolutePanel} containing a {@link CanvasWidget} 
	 * @return 
	 */
	public Widget getWidget(){
		if(canvasGWT != null){
			return canvasGWT;
		}
		else{
			return panel;
		}
	}
	
	/**
	 * Use this method for explicitly draw on the canvas.
	 * Use getWidget() method for adding it to the document.
	 * @return the {@link CanvasWidget} used for drawing or null
	 */
	public CanvasWidget getCanvasWidget() {

		return canvas;
	}
	
	/**
	 * If {@link Canvas} is used as view
	 * return its {@link Context2d} 
	 * @return
	 */
	public Context2d getContext2d(){
		return null;
		
	}
	
	@Override
	protected void clear() {
		canvas.setWidth(width);
		canvas.setHeight(height);
		textPositioner.removeAllText();
	}

	@Override
	protected void drawPath(Path path) {
		applyContext(path.getContext());
		ArrayList<PathElement> pathElements = path.getPathElements();
		if((path.hasFill() == false && path.hasStroke() == false )||
				pathElements.size() < 1)
			return;
		
		canvas.beginPath();
		canvas.moveTo(path.getBasePointX(), path.getBasePointY());
		for(PathElement pe : pathElements){
			if(pe instanceof QuadraticCurveTo){
				QuadraticCurveTo qTo = (QuadraticCurveTo) pe;
				canvas.quadraticCurveTo(qTo.getControlPointX(), qTo.getControlPointY(), qTo.getEndPointX(), qTo.getEndPointY());
			}
			else if(pe instanceof LineTo){
				LineTo lTo = (LineTo) pe;
				canvas.lineTo(lTo.getEndPointX(), lTo.getEndPointY());
			}
			else if(pe instanceof MoveTo){
				MoveTo mTo = (MoveTo) pe;
				//we have to stroke
//				if(path.hasStroke())
//					canvas.stroke();
				canvas.moveTo(mTo.getEndPointX(), mTo.getEndPointY());
			}
		}
		if(path.hasStroke())
			canvas.stroke();
		//if its a closed path then we can fill it
		if(path.hasFill() && pathElements.get(pathElements.size()-1).getEndPointX() == path.getBasePointX() &&
				pathElements.get(pathElements.size()-1).getEndPointY() == path.getBasePointY()){
			canvas.fill();
		}
	}

	@Override
	protected void drawRectangle(Rectangle rectangle) {
		applyContext(rectangle.getContext());
		double x = rectangle.getBasePointX(), y = rectangle.getBasePointY(), width = rectangle.getWidth(), height = rectangle.getHeight(), roundedCornerRadius = rectangle.getRoundedCornerRadius();
		canvas.beginPath();
		if(roundedCornerRadius == 0){
			if(rectangle.hasFill()){
				canvas.fillRect(x,y,width,height);
			}
			if(rectangle.hasStroke()){
				canvas.strokeRect(x,y,width,height);
			}
		}
		//we create a path
		else{
			//start just below (with the amount of the roundedCornerRadius) the upper-left point (GO's basePoint) of the rectangle
			//and draw lines clockwise
			canvas.moveTo(x, y + roundedCornerRadius);
			canvas.quadraticCurveTo(x, y, x + roundedCornerRadius, y);
			canvas.lineTo(x + width - roundedCornerRadius, y);
			canvas.quadraticCurveTo(x + width, y, x + width, y - roundedCornerRadius);
			canvas.lineTo(x + width, y + height - roundedCornerRadius);
			canvas.quadraticCurveTo(x + width, y + height, x + width - roundedCornerRadius, y + height);
			canvas.lineTo(x + roundedCornerRadius, y + height);
			canvas.quadraticCurveTo(x, y + height, x , y + height  - roundedCornerRadius);
			canvas.closePath();
		}
		if(rectangle.hasFill()){
			canvas.fill();
		}
		if(rectangle.hasStroke()){
			canvas.stroke();
		}
	}

	@Override
	protected void drawCircle(Circle circle) {
		applyContext(circle.getContext());
		canvas.beginPath();
		canvas.arc(circle.getBasePointX(), circle.getBasePointY(), circle.getRadius(), 0, Math.PI * 2, false);
		if(circle.hasFill()){
			canvas.fill();
		}
		if(circle.hasStroke()){
			canvas.stroke();
		}
	}

	@Override
	protected void drawLine(Line line) {
		applyContext(line.getContext());
		canvas.beginPath();
		canvas.moveTo(line.getBasePointX(), line.getBasePointY());
		canvas.lineTo(line.getEndPointX(), line.getEndPointY());
		canvas.stroke();
	}
	
	/**
	 * Sets the context variables of the canvas.
	 * @param context
	 */
	protected void applyContext(Context context){
		if(canvas != null){
			canvas.setLineJoin("round");
			canvas.setLineCap("square");
			canvas.setGlobalAlpha(context.getAlpha());
			canvas.setFillStyle(context.getFillColor());
			canvas.setStrokeStyle(context.getStrokeColor());
			canvas.setLineWidth(context.getStrokeWidth());
			if(!createShadows && (context.getShadowOffsetX() != 0 || context.getShadowOffsetY() != 0)){
				canvas.setShadowColor(context.getShadowColor());
				canvas.setShadowOffsetX(context.getShadowOffsetX());
				canvas.setShadowOffsetY(context.getShadowOffsetY());
				
			}
		}
		else{
			canvasGWT.getContext2d().setLineJoin("round");
			canvasGWT.getContext2d().setLineCap("square");
			canvasGWT.getContext2d().setGlobalAlpha(context.getAlpha());
			canvasGWT.getContext2d().setFillStyle(context.getFillColor());
			canvasGWT.getContext2d().setStrokeStyle(context.getStrokeColor());
			canvasGWT.getContext2d().setLineWidth(context.getStrokeWidth());
		}
		
	}

	/*Mouse event handling*/
	protected void initEvents(){
		hm = new HandlerManager(this);
		interactiveGOs = new ArrayList<GraphicalObject>();
		this.mouseOverGOs = new ArrayList<GraphicalObject>();
		canvas.addDomHandler(this, MouseDownEvent.getType());
		canvas.addDomHandler(this, MouseUpEvent.getType());
		canvas.addDomHandler(this, MouseMoveEvent.getType());
		canvas.addDomHandler(this, MouseOutEvent.getType());
		canvas.addDomHandler(this, MouseOverEvent.getType());
		canvas.addDomHandler(this, MouseWheelEvent.getType());
		canvas.addDomHandler(this, ClickEvent.getType());
	}
	
	/**
	 * Adds a {@link GraphicalObject} which must implement {@link InteractiveGraphicalObject},
	 *  so when an event occurs on this element the registered and related handlers will be notified.
	 *  This method itself will NOT display the {@link InteractiveGraphicalObject}. 
	 * @param igo
	 */
	public void addInteractiveGO(GraphicalObject igo){
		if(igo instanceof InteractiveGraphicalObject){
			interactiveGOs.add(igo);
		}
	}
	
	/**
	 * Removes the given {@link GraphicalObject} from the related list
	 * @param igo
	 */
	public void removeInteractiveGO(GraphicalObject igo){
		this.interactiveGOs.remove(igo);
		
	}
	
	/**
	 * @return the singleMouseOverGO
	 */
	public boolean isSingleMouseSelection() {
		return singleMouseSelection;
	}
	
	/**
	 * @param if set true only one {@link GraphicalObject} will be 'selected' (the topmost)
	 */
	public void setSingleMouseSelection(boolean singleMouseSelection) {
		this.singleMouseSelection = singleMouseSelection;
	}
	
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		event.preventDefault();
	}	

	@Override
	public void onMouseUp(MouseUpEvent event) {
	
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		event.preventDefault();
		mouseOverCanvas = true;
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		mouseOverCanvas = false;
		hm.fireEvent(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		mouseX = event.getRelativeX(canvas.getElement());
		mouseY = event.getRelativeY(canvas.getElement());
		
		if(mouseOverCanvas){
			updateMouseOverIGOsAndFireMouseOverEvents();
		}
		hm.fireEvent(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
	}

	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		if(mouseOverGOs.size() > 0)
			hm.fireEvent(new GraphicalObjectEvent(mouseOverGOs));
	}
	
	/**
	 * Checks the the positions of the {@link GraphicalObject}s and updates the related container
	 * @return the newly mouseovered {@link GraphicalObject}s
	 */
	protected void updateMouseOverIGOsAndFireMouseOverEvents(){
		ArrayList<GraphicalObject> mouseOver = new ArrayList<GraphicalObject>();
		ArrayList<GraphicalObject> justMouseOvered = new ArrayList<GraphicalObject>();
		ArrayList<GraphicalObject> justMouseOut = new ArrayList<GraphicalObject>();
		if(singleMouseSelection){
			Collections.sort((ArrayList<GraphicalObject>)interactiveGOs, GraphicalObject.getZXComparator());
			Collections.reverse(interactiveGOs);
		}
		for(GraphicalObject igo : interactiveGOs){
			if(((InteractiveGraphicalObject) igo).isMouseOver(mouseX, mouseY)){
				mouseOver.add(igo);
				if(!mouseOverGOs.contains(igo))
					justMouseOvered.add(igo);
				if(singleMouseSelection)
					break;
			}
		}
		for(GraphicalObject go : mouseOverGOs){
			if(!mouseOver.contains(go)){
				justMouseOut.add(go);
			}
		}
		if(justMouseOut.size() > 0 || justMouseOvered.size() > 0)
			hm.fireEvent(new GraphicalObjectEvent(justMouseOvered, justMouseOut));
		this.mouseOverGOs = mouseOver;
		
	}
	
	public HandlerRegistration addGraphicalObjectEventHandler(GraphicalObjectEventHandler handler){
		
		return hm.addHandler(GraphicalObjectEvent.TYPE, handler);
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler){
		return hm.addHandler(MouseMoveEvent.getType(), handler);
	}
	
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler){
		return hm.addHandler(MouseOutEvent.getType(), handler);
	}

	@Override
	protected void drawArc(Arc arc) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void drawText(Text text) {
		if(textPositioner != null)
			textPositioner.addText(text);
		//TODO gwtcanvas impl
	}

	@Override
	public void update() {
		textPositioner.removeAllText();
		super.update();
	}

	@Override
	public void measureText(Text text) {
		textPositioner.measureText(text);
	}
}