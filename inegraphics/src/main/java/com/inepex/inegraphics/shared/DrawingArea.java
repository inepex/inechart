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
		double lastX = solidLinePath.getBasePointX(), lastY = solidLinePath.getBasePointY();
		for(PathElement e : solidLinePath.getPathElements()){
			
			if(e instanceof LineTo){
				final double length = calculateDistance(lastX, lastY, e.getEndPointX(), e.getEndPointY());
				final double theta = calculateAngle(lastX, lastY, e.getEndPointX(), e.getEndPointY());
				int count =  (int) (length / (dashLength + dashDistance)*2);
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
						dashedPath.lineTo(x,y, false);
					}
					else{
						x += moveDX;
						y +=  moveDY;
						dashedPath.moveTo(x,y, false);
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
	
	protected static double getXIntercept(double y, double x1, double y1, double x2, double y2){
		return  (x2 - x1) / (y2 - y1) * (y - y1) + x1;
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
		double lastX = path.getBasePointX(), lastY = path.getBasePointY();
		boolean clipped = false;
		if(clipLeft){
			for(PathElement e : path.getPathElements()){
				if(clipped){
					clippedPath.getPathElements().add(e);
				}
				else if(e.getEndPointX() >= x){
					clippedPath = new Path(x, getYIntercept(x, lastX, lastY, e.getEndPointX(), e.getEndPointY()), path.getzIndex(),  path.getContext(), path.hasStroke(), path.hasFill());
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
						clippedElement = new LineTo(x, getYIntercept(x,lastX, lastY, e.getEndPointX(), e.getEndPointY()));
					}
					else{
						clippedElement = new MoveTo(x, getYIntercept(x,lastX, lastY, e.getEndPointX(), e.getEndPointY()));
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
	
	public static Path clipPathWithRectangle(Path pathToBeClipped, double x, double y, double width, double height){
		Path clippedPath = null;
		if(pathToBeClipped == null)
			return null;
		double lastX, lastY, actX, actY;
		int elementNo = -1;
		//first find a basepoint
		boolean basepointSet = false;
		if(isPointInRectengle(pathToBeClipped.getBasePointX(), pathToBeClipped.getBasePointY(), x, y, width, height)){
			clippedPath = new Path(pathToBeClipped.getBasePointX(), pathToBeClipped.getBasePointY(), pathToBeClipped.getzIndex(), pathToBeClipped.getContext(), pathToBeClipped.hasStroke(), pathToBeClipped.hasFill());
			basepointSet = true;
		}
		else{
			elementNo++;
		}
		lastX = pathToBeClipped.getBasePointX();
		lastY = pathToBeClipped.getBasePointY();
		for(PathElement e : pathToBeClipped.getPathElements()){
			actX = e.getEndPointX();
			actY = e.getEndPointY();
			double[] clipped = getIntersection(lastX, lastY, actX, actY, x, y, width, height);
			if(clipped != null){
				if(!basepointSet){
					clippedPath = new Path(clipped[0], clipped[1], pathToBeClipped.getzIndex(), pathToBeClipped.getContext(), pathToBeClipped.hasStroke(), pathToBeClipped.hasFill());
					if(e instanceof LineTo){
						clippedPath.lineTo(clipped[2],  clipped[3], false);
					}
					else if(e instanceof MoveTo){
						clippedPath.moveTo(clipped[2], clipped[3], false);
					}
					basepointSet = true;
				}
				else{
					if(e instanceof LineTo){
						clippedPath.lineTo(clipped[2], clipped[3], false);
					}
					else if(e instanceof MoveTo){
						clippedPath.moveTo(clipped[2], clipped[3], false);
					}
				}
			}
			//actual element has no point in rect, but the path is set,
			//we 'follow' the step's change with moveTo -> the path's fill will be correct only in this case
			if(clippedPath != null && (clipped == null || clipped[2] != actX || clipped[3] != actY)){
				double yTo =  (actY > y + height ? y + height : (actY < y ? y : actY));
				double xTo =  (actX > x + width ? x + width : (actX < x ? x : actX));
				//ends left to the rect
				if(x > actX){					
					clippedPath.moveTo(x, yTo, false);
				}
				//ends right to the rect
				else if(actX > x + width){
					clippedPath.moveTo( (x + width), yTo, false);
				}
				//bot to rect
				if(actY > y + height){
					clippedPath.moveTo(xTo, (y + height), false);
				}
				else if(actY < y ){
					clippedPath.moveTo(xTo, (y), false);
				}
			}
			lastX = actX;
			lastY = actY;
		}
		return clippedPath;
	}
	
	/**
	 * Clips a line to fit the given rectangle.
	 * @param x1 x of the start point of the line
	 * @param y1 y of the start point of the line
	 * @param x2 x of the end point of the line
	 * @param y2 y of the end point of the line
	 * @param rX x of the rectangle's upper-left corner
	 * @param rY y of the rectangle's upper-left corner
	 * @param width width of the rectangle
	 * @param height height of the rectangle
	 * @return null if the line has no points inside the rectangle, else the [x1, y1, x2, y2] coords of the new (clipped) line
	 */
	public static double[] getIntersection(double x1, double y1, double x2, double y2, double rX, double rY, double width, double height){
		//if both endpoint are inside, we return the line
		if(isPointInRectengle(x1, y1, rX, rY, width, height) && isPointInRectengle(x2, y2, rX, rY, width, height))
			return new double[]{x1,y1, x2, y2};
		if( (x1 < rX && x2 < rX) ||
			(x1 > rX + width && x2 > rX + width) ||
			(y1 < rY && y2 < rY) ||
			(y1 > rY + height && y2 > rY + height)){
			return null;
		}
		//vertical line
		if(x1 == x2){
			 if(y1 > rY + height)
				 y1 = rY + height;
			 if(y2 > rY + height)
				 y2 = rY + height;
			 if(y1 < rY)
				 y1 = rY;
			 if(y2 < rY)
				 y2 = rY;
			 if(y1 == y2)
				 return null;
			 else
				 return new double[]{x1, y1, x2, y2};
		}
		//horizontal
		if(y1 == y2){
			 if(x1 > rX + width)
				 x1 = rX + width;
			 if(x2 > rX + width)
				 x2 = rX + width;
			 if(x1 < rX)
				 x1 = rX;
			 if(x2 < rX)
				 x2 = rX;
			 if(x1 == x2)
				 return null;
			 else
				 return new double[]{x1, y1, x2, y2};
		}
		double left = getYIntercept(rX, x1, y1, x2, y2);
		double right = getYIntercept(rX+width, x1, y1, x2, y2);
		double top = getXIntercept(rY, x1, y1, x2, y2);
		double bottom = getXIntercept(rY+height, x1, y1, x2, y2);
		
		//if the line has no points inside the rectangle
		if( (left > rY + height && right > rY + height) ||
			(left < rY  && right < rY)	|| 
			(top < rX && bottom < rX) ||
			(top > rX + width && bottom > rX + width)){
			return null;
		}
		
		double a1,a2,b1,b2;
		
		if(x1 < x2){ 
			if(top < bottom){
				a1 = Math.max(Math.max(x1, top), rX);
				a2 = Math.min(Math.min(bottom, x2), rX+width);
			}
			else{
				a1 = Math.max(Math.max(x1, bottom), rX);
				a2 = Math.min(Math.min(top, x2), rX+width);
			}
		}
		else{
			if(top > bottom){
				a1 = Math.min(Math.min(top, x1), rX+width);
				a2 = Math.max(Math.max(bottom, x2), rX);
			}
			else{
				a1 = Math.min(Math.min(bottom, x1), rX+width);
				a2 = Math.max(Math.max(top, x2), rX);
			}
		}
		if(y1 < y2){
			if(left < right){
				b1 = Math.max(Math.max(y1, left), rY);
				b2 = Math.min(Math.min(y2, right), rY+height);
			}
			else{
				b1 = Math.max(Math.max(y1, right), rY);
				b2 = Math.min(Math.min(y2, left), rY+height);
			}
		}
		else{
			if(left > right){
				b1 = Math.min(Math.min(y1, rY+height), left);
				b2 = Math.max(Math.max(y2, right), rY);
			}
			else{
				b1 = Math.min(Math.min(rY+height, right), y1);
				b2 = Math.max(Math.max(rY, left), y2);
			}
		}
		return new double[]{a1,b1,a2,b2};
	}


	public static boolean isPointInRectengle(double pointX, double pointY, double x, double y, double width, double height){
		if(pointX >= x && pointX <= x + width && pointY >= y && pointY <= y+height)
			return true;
		return false;
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
}
