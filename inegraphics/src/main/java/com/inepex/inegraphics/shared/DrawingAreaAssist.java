package com.inepex.inegraphics.shared;

import java.util.ArrayList;

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

public class DrawingAreaAssist {
	
	public static void dropGraphicalObjectsOutsideRectangle(GraphicalObjectContainer goc, double x, double y, double width, double height){
		ArrayList<GraphicalObject> toDrop = new ArrayList<GraphicalObject>();
		for(GraphicalObject go : goc.graphicalObjects){
			if(go.getBasePointX() < x ||
				go.getBasePointY() < y ||
				go.getBasePointX() > x + width ||
				go.getBasePointY() > y + height)
				toDrop.add(go);
		}
		for(GraphicalObject go : toDrop){
			goc.removeGraphicalObject(go);
		}
	}

	public static double getYIntercept(double x, double x1, double y1, double x2, double y2){
		return (y2 - y1) / (x2 - x1) * (x - x1) + y1;
	}
	
	public static double getXIntercept(double y, double x1, double y1, double x2, double y2){
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
		if(isPointInRectangle(pathToBeClipped.getBasePointX(), pathToBeClipped.getBasePointY(), x, y, width, height)){
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
	
	public static GraphicalObjectContainer clipRectanglesWithRectangle(GraphicalObjectContainer container, double x, double y, double width, double height){
		GraphicalObjectContainer filtered = new GraphicalObjectContainer();
		for(GraphicalObject go : container.graphicalObjects){
			if(go instanceof Rectangle){
				Rectangle r = clipRectangleWithRectangle((Rectangle) go, x, y, width, height);
				if(r != null){
					filtered.addGraphicalObject(r);
				}
			}
			else{
				filtered.addGraphicalObject(go);
			}
		}
		return filtered;
	}
	
	/**
	 * 
	 * @param rectangleToClip
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return null if the two rectangles have no common points.
	 */
	public static Rectangle clipRectangleWithRectangle(Rectangle rectangleToClip, double x, double y, double width, double height){
		if(rectangleToClip.getBasePointX() > x && rectangleToClip.getBasePointY() > y &&
				rectangleToClip.getBasePointX() + rectangleToClip.getWidth() < x + width && 
				rectangleToClip.getBasePointY() + rectangleToClip.getHeight() < y + height)
			return rectangleToClip;
		double[] clipTop = getIntersection(
				rectangleToClip.getBasePointX(), rectangleToClip.getBasePointY(),
				rectangleToClip.getBasePointX()+rectangleToClip.getWidth(), rectangleToClip.getBasePointY(),
				x, y, width, height);
		double[] clipBottom = getIntersection(
				rectangleToClip.getBasePointX(), rectangleToClip.getBasePointY()+ rectangleToClip.getHeight(),
				rectangleToClip.getBasePointX()+rectangleToClip.getWidth(), rectangleToClip.getBasePointY() + rectangleToClip.getHeight(),
				x, y, width, height);
		double[] clipLeft = getIntersection(
				rectangleToClip.getBasePointX(), rectangleToClip.getBasePointY(),
				rectangleToClip.getBasePointX(), rectangleToClip.getBasePointY() + rectangleToClip.getHeight(),
				x, y, width, height);
		double[] clipRight = getIntersection(
				rectangleToClip.getBasePointX()+rectangleToClip.getWidth(), rectangleToClip.getBasePointY(),
				rectangleToClip.getBasePointX()+rectangleToClip.getWidth(), rectangleToClip.getBasePointY() + rectangleToClip.getHeight(),
				x, y, width, height);
		
		if(clipTop == null && clipBottom == null && clipLeft == null && clipRight == null){
			//the viewport is inside the rectangle
			if(rectangleToClip.getBasePointX() < x && rectangleToClip.getBasePointY() < y &&
					rectangleToClip.getBasePointX() + rectangleToClip.getWidth() > x + width && 
					rectangleToClip.getBasePointY() + rectangleToClip.getHeight() > y + height)
				return new Rectangle(x, y, width, height, rectangleToClip.getRoundedCornerRadius(), rectangleToClip.getzIndex(), rectangleToClip.getContext(), rectangleToClip.hasStroke(),rectangleToClip.hasFill());
			else
				return null;
		}
		Double clippedX=null, clippedY=null, clippedW=null, clippedH=null;
		if(clipTop != null){
			clippedX = clipTop[0];
			clippedY = clipTop[1];
			clippedW = clipTop[2] - clipTop[0];
		}
		if(clipLeft != null){
			clippedX = clipLeft[0];
			clippedY = clipLeft[1];
			clippedH = clipLeft[3] - clipLeft[1];
		}
		if(clipBottom != null){
			clippedX = clipBottom[0];
			clippedW = clipBottom[2] - clipBottom[0];
		}
		if(clipRight != null){
			clippedY = clipRight[1];
			clippedH = clipRight[3] - clipRight[1];
		}
		
		if(clippedX == null){
			clippedX = clipRight[0];
			clippedW = 0d;
		}
		if(clippedY == null){
			clippedY = clipBottom[1];
			clippedH = 0d;
		}
		if(clippedW == null){
			clippedW = 0d;
		}
		if(clippedH == null){
			clippedH = 0d;
		}
		
		return new Rectangle(clippedX, clippedY, clippedW, clippedH, rectangleToClip.getRoundedCornerRadius(), rectangleToClip.getzIndex(), rectangleToClip.getContext(), rectangleToClip.hasStroke(), rectangleToClip.hasFill());
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
		if(isPointInRectangle(x1, y1, rX, rY, width, height) && isPointInRectangle(x2, y2, rX, rY, width, height))
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

	public static boolean isPointInRectangle(double pointX, double pointY, double x, double y, double width, double height){
		if(pointX >= x && pointX <= x + width && pointY >= y && pointY <= y+height)
			return true;
		return false;
	}

	/**
	 * Creates a path with dashed stroke from the given path
	 * @param solidLinePath
	 * @param dashLength
	 * @param dashDistance
	 * @return
	 */
	public static Path createDashedLine(Path solidLinePath, double dashLength, double dashDistance){
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
	
	public static Path createDashedLine(Line solidLine, double dashLength, double dashDistance){
		Path dashedPath = new Path(
				solidLine.getBasePointX(),
				solidLine.getBasePointY(),
				solidLine.getzIndex(),
				solidLine.getContext(),
				true,
				false);
		final double length = calculateDistance(solidLine.getBasePointX(),solidLine.getBasePointY(),solidLine.getEndPointX(), solidLine.getEndPointY());
		final double theta = calculateAngle(solidLine.getBasePointX(),solidLine.getBasePointY(), solidLine.getEndPointX(), solidLine.getEndPointY());
		int count =  (int) (length / (dashLength + dashDistance)*2);
		if(length > (dashLength + dashDistance) * count + dashLength)
			count++;
		double lineDX =  (dashLength * Math.cos(theta));
		double lineDY =  (dashLength * Math.sin(theta));
		double moveDX = (dashDistance * Math.cos(theta));
		double moveDY =  (dashDistance * Math.sin(theta));
		double x = solidLine.getBasePointX(), y = solidLine.getBasePointY();
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
		dashedPath.moveTo(solidLine.getEndPointX(), solidLine.getEndPointY(), false);

		return dashedPath;	
	}

 	public static double calculateAngle(double startX, double startY, double endX, double endY){
		return Math.atan2(endY - startY, endX - startX);
	}
	
	public static double calculateDistance(double startX, double startY, double endX, double endY){
		return Math.sqrt(Math.pow((endX - startX),2) + Math.pow((endY - startY),2));
	}

	/**
	 * @param goc
	 * @return [x,y,width,height]
	 */
	public static double[] getBoundingBox(GraphicalObjectContainer goc){
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for(GraphicalObject go:goc.getGraphicalObjects()){
			double[] bb = getBoundingBox(go);
			if(bb[0] < minX)
				minX = bb[0];
			if(bb[1] < minY) 
				minY = bb[1];
			if(bb[0] + bb[2] > maxX)
				maxX = bb[0] + bb[2];
			if(bb[1] + bb[3] > maxY)
				maxY = bb[1] + bb[3];			
		}
		return new double[]{
				minX,
				minY,
				maxX - minX,
				maxY - minY};
	}
	
	/**
	 * Returns a bounding box for a GO, works with instances of:
	 *  {@link Rectangle},
	 *  {@link Circle}, 
	 *  {@link Path} (quadraticCurveTo, and moveTo interpreted as LineTo)
	 * @param go
	 * @return [x, y, width, height]
	 */
	public static double[] getBoundingBox(GraphicalObject go){
		double[] bb = new double[4];
		if(go instanceof Path){
			double minX = go.getBasePointX();
			double minY = go.getBasePointY();
			double maxX = go.getBasePointX();
			double maxY = go.getBasePointY();
			for(PathElement e : ((Path) go).getPathElements()){
				if(e.getEndPointX() < minX)
					minX = e.getEndPointX();
				if(e.getEndPointY() < minY)
					minY = e.getEndPointY();
				if(e.getEndPointX() > maxX)
					maxX = e.getEndPointX();
				if(e.getEndPointY() > maxY)
					maxY = e.getEndPointY();
			}
			bb[0] = minX;
			bb[1] = minY;
			bb[2] = maxX-minX;
			bb[3] = maxY-minY;
		}
		else if(go instanceof Circle){
			bb[2] = ((Circle) go).getRadius() * 2;
			bb[3] = ((Circle) go).getRadius() * 2;
			bb[0] = go.getBasePointX() - ((Circle) go).getRadius();
			bb[1] = go.getBasePointY() - ((Circle) go).getRadius();
		}
		else if(go instanceof Rectangle){
			bb[0] =  go.getBasePointX();
			bb[1] =  go.getBasePointY();
			bb[2] = ((Rectangle) go).getWidth();
			bb[3] = ((Rectangle) go).getHeight();
		}
		else if(go instanceof Text){
			Text text = (Text) go;
			switch(text.getBasePointXPosition()){
			case LEFT:
				bb[0] = text.getBasePointX() + text.getLeftPadding();
				break;
			case MIDDLE:
				bb[0] = text.getBasePointX() - (text.getWidth() + text.getLeftPadding() + text.getRightPadding())/2;
				break;
			case RIGHT: 
				bb[0] = text.getBasePointX() - (text.getWidth() + text.getLeftPadding() + text.getRightPadding());
				break;
			}
			switch(text.getBasePointYPosition()){
			case TOP:
				bb[1] = text.getBasePointY() + text.getTopPadding();
				break;
			case MIDDLE:
				bb[1] = text.getBasePointY() - (text.getHeight() + text.getTopPadding() + text.getBottomPadding())/2;
				break;
			case BOTTOM: 
				bb[1] = text.getBasePointY() - (text.getHeight()  + text.getTopPadding() + text.getBottomPadding());
				break;
			}
			bb[2] = ((Text) go).getWidth();
			bb[3] = ((Text) go).getHeight();
		}
		return bb;
	}
	
}
