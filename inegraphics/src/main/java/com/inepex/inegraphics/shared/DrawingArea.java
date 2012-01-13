package com.inepex.inegraphics.shared;

import java.util.ArrayList;
import java.util.Collections;

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
 * A class that represents a drawing area/surface.
 * Contains helper methods for drawing and also stores the 
 * {@link GraphicalObject}s and the {@link Context}s used during the drawing process.
 * 
 * 
 * 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public abstract class DrawingArea extends GraphicalObjectContainer{
	protected int width;
	protected int height;
	/**
	 * Set false in implementation if it fully supports shadowDrawing
	 * If true the Shadows will be drawn before {@link GraphicalObject}s per zIndex just under the go's
	 */
	protected boolean createShadows = true;
	
	protected Context actualContext;
	protected int actualzIndex; 
		
	/**
	 * Constructs an empty surface with default {@link Context}
	 * @param width
	 * @param height
	 */
	public DrawingArea(int width, int height) {
		this.width = width;
		this.height = height;
		actualContext = Context.getDefaultContext();
		actualzIndex =  0;
	}
	
	/**
	 * Constructs an empty surface with the given initialContext (it will be the first -> 0.)
	 * @param width
	 * @param height
	 * @param initialContext
	 */
	public DrawingArea(int width, int height, Context initialContext) {
		this.width = width;
		this.height = height;
		actualzIndex = 0;
		actualContext = initialContext;
	}

	/**
	 * 
	 * Updates / (re)draws all {@link GraphicalObject}s 
	 *  
	 */
	public void update(){
//		Log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		long start = System.currentTimeMillis();
//		Collections.sort(graphicalObjects, GraphicalObject.getZXComparator());
//		Collections.sort(graphicalObjects);
		Collections.sort(graphicalObjects, GraphicalObject.getZComparator());
//		Log.debug((System.currentTimeMillis() - start) + " ms : sorting GOs");
//		start = System.currentTimeMillis();
		clear();
//		Log.debug((System.currentTimeMillis() - start) + " ms : clearing canvas");
//		start = System.currentTimeMillis();
		if(graphicalObjects.size() == 0)
			return;
		/*
		 * The shadows should be drawn before the objects for each zIndex 
		 */
		ArrayList<GraphicalObject> gos = new ArrayList<GraphicalObject>();
		int actualzIndex = graphicalObjects.get(0).getzIndex();
		for(int i = 0; i < graphicalObjects.size(); i++){
			GraphicalObject go = graphicalObjects.get(i);
			//if next z index
			if(go.getzIndex() != actualzIndex){
				actualzIndex = go.getzIndex();
				for(GraphicalObject go2 : gos){
					drawGraphicalObject(go2);
				}
				gos.clear();
			}
			if(createShadows &&
					(go.getContext().shadowOffsetX != 0 || go.getContext().shadowOffsetY != 0)){
				drawGraphicalObjectShadow(go);
			}
			gos.add(go);
		}
		for(GraphicalObject go2 : gos){
			drawGraphicalObject(go2);
		}
//		Log.debug((System.currentTimeMillis() - start) + " ms : drawing GOs");
//		Log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	
	protected void drawGraphicalObject(GraphicalObject go){
		if(go instanceof Line){
			drawLine((Line) go);
		}
		else if(go instanceof Circle){
			drawCircle((Circle) go);
		}
		else if(go instanceof Rectangle){
			drawRectangle((Rectangle) go);
		}
		else if(go instanceof Path){
			drawPath((Path) go);
		}
		else if(go instanceof Arc){
			drawArc((Arc)go);
		}
		else if(go instanceof Text){
			drawText((Text) go);
		}

	}
	
	protected void drawGraphicalObjectShadow(GraphicalObject go){
		Context context = new Context(
				go.getContext().shadowAlpha,
				go.getContext().shadowColor,
				go.getContext().strokeWidth,
				go.getContext().shadowColor,
				0, 0, 0, go.getContext().shadowColor);
		if(go instanceof Line){
			Line lineShadow = new Line(
					go.getBasePointX() + go.getContext().getShadowOffsetX(),
					go.getBasePointY() + go.getContext().getShadowOffsetY(),
					((Line) go).getEndPointX() + go.getContext().getShadowOffsetX(),
					((Line) go).getEndPointX() + go.getContext().getShadowOffsetY(),
					go.getzIndex(), context);
			drawLine(lineShadow);
		}
		else if(go instanceof Circle){
			Circle circleShadow = new Circle(
					go.getBasePointX() + go.getContext().getShadowOffsetX(),
					go.getBasePointY() + go.getContext().getShadowOffsetY(),
					go.getzIndex(), context,
					go.hasStroke(), go.hasFill(),
					((Circle) go).getRadius());
			drawCircle(circleShadow);
		}
		else if(go instanceof Rectangle){
			Rectangle rectangleShadow = new Rectangle(
					go.getBasePointX() + go.getContext().getShadowOffsetX(),
					go.getBasePointY() + go.getContext().getShadowOffsetY(),
					((Rectangle) go).getWidth(), ((Rectangle) go).getHeight(),
					((Rectangle) go).getRoundedCornerRadius(),
					go.getzIndex(), context,	go.hasStroke(), go.hasFill());
			drawRectangle(rectangleShadow);
		}
		else if(go instanceof Path){
			Path pathShadow = new Path(
					go.getBasePointX() + go.getContext().getShadowOffsetX(),
					go.getBasePointY() + go.getContext().getShadowOffsetY(),
					go.getzIndex(), context,
					go.hasStroke(), go.hasFill());
			for(PathElement pe :((Path) go).getPathElements()){
				PathElement pathElementShadow = null;
				if(pe instanceof QuadraticCurveTo){
					QuadraticCurveTo qTo = (QuadraticCurveTo) pe;
					pathElementShadow = new QuadraticCurveTo(
							qTo.getEndPointX() + go.getContext().getShadowOffsetX(),
							qTo.getEndPointY() + go.getContext().getShadowOffsetY(),
							qTo.getControlPointX() + go.getContext().getShadowOffsetX(),
							qTo.getControlPointY() + go.getContext().getShadowOffsetY());
				}
				else if(pe instanceof LineTo){
					LineTo lTo = (LineTo) pe;
					pathElementShadow = new LineTo(
							lTo.getEndPointX() + go.getContext().getShadowOffsetX(),
							lTo.getEndPointY() + go.getContext().getShadowOffsetY());
				}
				else if(pe instanceof MoveTo){
					MoveTo mTo = (MoveTo) pe;
					pathElementShadow = new MoveTo(
							mTo.getEndPointX() + go.getContext().getShadowOffsetX(),
							mTo.getEndPointY() + go.getContext().getShadowOffsetY());
				}
				pathShadow.getPathElements().add(pathElementShadow);
			}
			drawPath(pathShadow);
		}
	}
	
	/**
	 * 
	 * Clears the canvas
	 */
	protected abstract void clear();
	
	protected abstract void drawPath(Path path);
	
	protected abstract void drawRectangle(Rectangle rectangle);
	
	protected abstract void drawCircle(Circle circle);
	
	protected abstract void drawLine(Line line);
	
	protected abstract void drawArc(Arc arc);
	
	protected abstract void drawText(Text text);
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the createShadows
	 */
	public boolean isCreateShadows() {
		return createShadows;
	}

	/**
	 * @param createShadows the createShadows to set
	 */
	public void setCreateShadows(boolean createShadows) {
		this.createShadows = createShadows;
	}
	
	public abstract void measureText(Text text);
}
