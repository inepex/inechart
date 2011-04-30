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
	
	/**
	 * 
	 * the newly added {@link GraphicalObject}s stored with this context's No.
	 */
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
		Collections.sort(graphicalObjects, GraphicalObject.getZXComparator());
		clear();
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
					(go.getContext().shadowOffsetX != 0 || go.getContext().shadowOffsetY != 0))
				drawGraphicalObjectShadow(go);
			gos.add(go);
		}
		for(GraphicalObject go2 : gos){
			drawGraphicalObject(go2);
		}
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
					go.getBasePointX() + (int)go.getContext().getShadowOffsetX(),
					go.getBasePointY() + (int)go.getContext().getShadowOffsetY(),
					((Line) go).getEndPointX() + (int)go.getContext().getShadowOffsetX(),
					((Line) go).getEndPointX() + (int)go.getContext().getShadowOffsetY(),
					go.getzIndex(), context);
			drawLine(lineShadow);
		}
		else if(go instanceof Circle){
			Circle circleShadow = new Circle(
					(int)go.getBasePointX() + (int)go.getContext().getShadowOffsetX(),
					(int)go.getBasePointY() + (int)go.getContext().getShadowOffsetY(),
					go.getzIndex(), context,
					go.hasStroke(), go.hasFill(),
					((Circle) go).getRadius());
			drawCircle(circleShadow);
		}
		else if(go instanceof Rectangle){
			Rectangle rectangleShadow = new Rectangle(
					(int)go.getBasePointX() + (int)go.getContext().getShadowOffsetX(),
					(int)go.getBasePointY() + (int)go.getContext().getShadowOffsetY(),
					((Rectangle) go).getWidth(), ((Rectangle) go).getHeight(),
					((Rectangle) go).getRoundedCornerRadius(),
					go.getzIndex(), context,	go.hasStroke(), go.hasFill());
			drawRectangle(rectangleShadow);
		}
		else if(go instanceof Path){
			Path pathShadow = new Path(
					(int)go.getBasePointX() + (int)go.getContext().getShadowOffsetX(),
					(int)go.getBasePointY() + (int)go.getContext().getShadowOffsetY(),
					go.getzIndex(), context,
					go.hasStroke(), go.hasFill());
			for(PathElement pe :((Path) go).getPathElements()){
				PathElement pathElementShadow = null;
				if(pe instanceof QuadraticCurveTo){
					QuadraticCurveTo qTo = (QuadraticCurveTo) pe;
					pathElementShadow = new QuadraticCurveTo(
							qTo.getEndPointX() + (int)go.getContext().getShadowOffsetX(),
							qTo.getEndPointY() + (int)go.getContext().getShadowOffsetY(),
							qTo.getControlPointX() + (int)go.getContext().getShadowOffsetX(),
							qTo.getControlPointY() + (int)go.getContext().getShadowOffsetY());
				}
				else if(pe instanceof LineTo){
					LineTo lTo = (LineTo) pe;
					pathElementShadow = new LineTo(
							lTo.getEndPointX() + (int)go.getContext().getShadowOffsetX(),
							lTo.getEndPointY() + (int)go.getContext().getShadowOffsetY());
				}
				else if(pe instanceof MoveTo){
					MoveTo mTo = (MoveTo) pe;
					pathElementShadow = new MoveTo(
							mTo.getEndPointX() + (int)go.getContext().getShadowOffsetX(),
							mTo.getEndPointY() + (int)go.getContext().getShadowOffsetY());
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
	 * Creates a path with dashed stroke from the given path
	 * @param solidLinePath
	 * @param dashLength
	 * @param dashDistance
	 * @return
	 */
	public static Path createDashedLinePath(Path solidLinePath, double dashLength, double dashDistance){
		Path dashedPath = new Path(
				solidLinePath.getBasePointX(),
				solidLinePath.getBasePointY(),
				solidLinePath.getzIndex(),
				solidLinePath.getContext(),
				solidLinePath.hasStroke(),
				solidLinePath.hasFill());
		int lastX = solidLinePath.getBasePointX(), lastY = solidLinePath.getBasePointY();
		for(PathElement e : solidLinePath.getPathElements()){
			
			if(e instanceof LineTo){
				final double length = calculateDistance(lastX, lastY, e.getEndPointX(), e.getEndPointY());
				final double theta = calculateAngle(lastX, lastY, e.getEndPointX(), e.getEndPointY());
				int count = (int) (length / (dashLength + dashDistance)*2);
				if(length > (dashLength + dashDistance) * count + dashLength)
					count++;
				double lineDX =  (dashLength * Math.cos(theta));
				double lineDY =  (dashLength * Math.sin(theta));
				double moveDX = (dashDistance * Math.cos(theta));
				double moveDY =  (dashDistance * Math.sin(theta));
				double x = lastX, y = lastY;
				for(int i=0;i<count;i++){
					//stroke
					if(i % 2 == 0){
						x += lineDX;
						y += lineDY;
						dashedPath.lineTo((int)x,(int)y, false);
					}
					else{
						x += moveDX;
						y +=  moveDY;
						dashedPath.moveTo((int)x,(int)y, false);
					}
				
				}
				dashedPath.moveTo(e.getEndPointX(), e.getEndPointY(), false);
			}
			else if(e instanceof QuadraticCurveTo){
				//TODO create dashed quad.c.
				dashedPath.quadraticCurveTo(e.getEndPointX(), e.getEndPointY(), ((QuadraticCurveTo) e).getControlPointX(), ((QuadraticCurveTo) e).getControlPointY(), false);
			}
			else if(e instanceof MoveTo)
				dashedPath.moveTo(e.getEndPointX(), e.getEndPointY(), false);
			lastX = e.getEndPointX();
			lastY = e.getEndPointY();
		}
		return dashedPath;	
	}

 	protected static double calculateAngle(double startX, double startY, double endX, double endY){
		return Math.atan2(endY - startY, endX - startX);
	}
	
	protected static double calculateDistance(double startX, double startY, double endX, double endY){
		return Math.sqrt(Math.pow((endX - startX),2) + Math.pow((endY - startY),2));
	}

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

	protected static double getYIntercept(double x, double x1, double y1, double x2, double y2){
		return (y2 - y1) / (x2 - x1) * (x - x1) + y1;
	}
	
	/**
	 * The given path must have only {@link LineTo}, and {@link MoveTo} elements with ascending endPointXes.
	 * @param x
	 * @param clipLeft
	 * @param path 
	 * @return null if the entire path is clipped
	 */
	public static Path clipPathAtX(double x, boolean clipLeft, Path path){
		if(path == null)
			return null;
		if(clipLeft && path.getBasePointX() > x)
			return path;
		if(!clipLeft && path.getBasePointX() > x)
			return null;
		Path clippedPath = null;
		int lastX = path.getBasePointX(), lastY = path.getBasePointY();
		boolean clipped = false;
		if(clipLeft){
			for(PathElement e : path.getPathElements()){
				if(clipped){
					clippedPath.getPathElements().add(e);
				}
				else if(e.getEndPointX() >= x){
					clippedPath = new Path((int)x, (int)getYIntercept(x, lastX, lastY, e.getEndPointX(), e.getEndPointY()), path.getzIndex(),  path.getContext(), path.hasStroke(), path.hasFill());
					clippedPath.getPathElements().add(e);
					clipped = true;
				}
				lastX = e.getEndPointX();
				lastY = e.getEndPointY();
			}
		}
		else{
			clippedPath = new Path(path.getBasePointX(), path.getBasePointY(), path.getzIndex(),  path.getContext(), path.hasStroke(), path.hasFill());
			for(PathElement e : path.getPathElements()){
				if(e.getEndPointX() >= x){
					PathElement clippedElement;
					if(e instanceof LineTo){
						clippedElement = new LineTo((int)x, (int)getYIntercept(x,lastX, lastY, e.getEndPointX(), e.getEndPointY()));
					}
					else{
						clippedElement = new MoveTo((int)x, (int)getYIntercept(x,lastX, lastY, e.getEndPointX(), e.getEndPointY()));
					}
					clippedPath.getPathElements().add(clippedElement);
					break;
				}
				else{
					clippedPath.getPathElements().add(e);
				}
				lastX = e.getEndPointX();
				lastY = e.getEndPointY();
			}
		}
		return clippedPath;
	}
}
