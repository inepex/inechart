package com.inepex.inegraphics.impl.client;

import java.util.ArrayList;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inegraphics.impl.client.canvas.CanvasWidget;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.gobjects.Arc;
import com.inepex.inegraphics.shared.gobjects.Circle;
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
public class DrawingAreaGWT extends DrawingArea {
	
	public static boolean isHTML5Compatible(){
		return Canvas.isSupported();		
	}
	
	protected CanvasWidget canvas;
	protected Canvas canvasGWT;
	protected AbsolutePanel panel;
	protected TextPositioner textPositioner;
	protected Context context; 
	
	public DrawingAreaGWT(int width, int height){
		this(width, height, false);
	}
	
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
		clear();
		context = null;
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
		clear();
	}

	public DrawingAreaGWT(Canvas canvas){
		super(canvas.getCanvasElement().getWidth(), canvas.getCanvasElement().getHeight());
		this.canvasGWT = canvas;
		this.panel = new AbsolutePanel();
		panel.setPixelSize(width, height);
		panel.add(canvas, 0, 0);
		clear();
	}
	
	public void setSize(int width, int height){
		if(canvasGWT == null && canvas != null){
			canvas.removeFromParent();
			canvas = new CanvasWidget(width, height);
			panel.add(canvas, 0, 0);
		}
		this.width = width;
		this.height = height;
		panel.setPixelSize(width, height);
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
//		canvas.setWidth(width);
//		canvas.setHeight(height);
		canvas.setFillStyle("white");
		canvas.clearRect(0, 0, width, height);
		textPositioner.removeAllText();
	}

	@Override
	protected void drawPath(Path path) {
		applyContext(path.getContext());
		ArrayList<PathElement> pathElements = path.getPathElements();
		if((path.hasFill() == false && path.hasStroke() == false )||
				pathElements.size() < 1){
			return;
		}
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
				canvas.moveTo(mTo.getEndPointX(), mTo.getEndPointY());
			}
		}
		if(path.hasStroke()){
			canvas.stroke();
		}
		//if its a closed path then we can fill it
		if(path.hasFill()
				&& pathElements.get(pathElements.size()-1).getEndPointX() == path.getBasePointX() &&
				pathElements.get(pathElements.size()-1).getEndPointY() == path.getBasePointY() ){
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
			canvas.quadraticCurveTo(x + width, y, x + width, y + roundedCornerRadius);
			canvas.lineTo(x + width, y + height - roundedCornerRadius);
			canvas.quadraticCurveTo(x + width, y + height, x + width - roundedCornerRadius, y + height);
			canvas.lineTo(x + roundedCornerRadius, y + height);
			canvas.quadraticCurveTo(x, y + height, x , y + height  - roundedCornerRadius);
			canvas.closePath();
			if(rectangle.hasFill()){
				canvas.fill();
			}
			if(rectangle.hasStroke()){
				canvas.stroke();
			}
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
			if(this.context == null){
				canvas.setLineJoin("round");
				canvas.setLineCap("square");
			}
			if(this.context == null || context.getAlpha() != this.context.getAlpha()){
				canvas.setGlobalAlpha(context.getAlpha());
			}
			if(this.context == null || !context.getFillColor().equals(this.context.getFillColor())){
				canvas.setFillStyle(context.getFillColor());
			}
			if(this.context == null || !context.getStrokeColor().equals(this.context.getStrokeColor())){
				canvas.setStrokeStyle(context.getStrokeColor());
			}
			if(this.context == null || context.getStrokeWidth() != this.context.getStrokeWidth()){
				canvas.setLineWidth(context.getStrokeWidth());
			}
			if(!createShadows){
				if(this.context == null || this.context.getShadowOffsetX() != context.getShadowOffsetX()){
					canvas.setShadowOffsetX(context.getShadowOffsetX());
				}
				if(this.context == null || this.context.getShadowOffsetY() != context.getShadowOffsetY()){
					canvas.setShadowOffsetX(context.getShadowOffsetX());
				}
				if(this.context == null || !this.context.getShadowColor().equals(context.getShadowColor())){
					canvas.setShadowColor(context.getShadowColor());
				}
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
		this.context = context;
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