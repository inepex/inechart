package com.inepex.inegraphics.impl.client;

import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.gobjects.Circle;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
import com.inepex.inegraphics.shared.gobjects.Rectangle;

public class MouseAssist {


	public static boolean isMouseOver(int[] position, GraphicalObject go){
		if(go instanceof Path){
			return isMouseOver(position, (Path)go);
		}
		else if(go instanceof Circle){
			return isMouseOver(position, (Circle)go);
		}
		else if(go instanceof Rectangle){
			return isMouseOver(position, (Rectangle)go);
		}
		else return false;

	}

	public static boolean isMouseOver(int[] position, GraphicalObject go, int distance){
		return false;

	}

	public static boolean isMouseOver(int[] position, Circle go){
		return isMouseOver(position, go, -1);
	}

	public static boolean isMouseOver(int[] position, Circle go, int distance){
		int radius = (int) (distance > 0 ? distance : go.getRadius() + go.getContext().getStrokeWidth() / 2);
		return DrawingAreaAssist.calculateDistance(position[0], position[1], go.getBasePointX(), go.getBasePointY()) <= radius; 
	}

	public static boolean isMouseOver(int[] position, Rectangle go){
		return isMouseOver(position, go, -1);
	}

	public static boolean isMouseOver(int[] position, Rectangle go, int distance){
		int d = (int) (distance > 0 ? distance : go.getContext().getStrokeWidth() / 2);
		return DrawingAreaAssist.isPointInRectangle(position[0], position[1], go.getBasePointX() - d, go.getBasePointY() - d, go.getWidth() + 2 * d, go.getHeight() + 2 * d);
	}

	public static boolean isMouseOver(int[] position, Path go){
		int intersectCount = 0;

		boolean skipNext = false;
		for(int i = 0; i < go.getPathElements().size(); i++){
			if(skipNext){
				skipNext = false;
				continue;
			}
			PathElement actual = go.getPathElements().get(i);
			double lastX = i == 0 ? go.getBasePointX() : go.getPathElements().get(i-1).getEndPointX();
			if(lastX == actual.getEndPointX()){
				continue;
			}
			double lastY = i == 0 ? go.getBasePointY() : go.getPathElements().get(i-1).getEndPointY();
			double yIntercept = DrawingAreaAssist.getYIntercept(position[0], lastX, lastY, actual.getEndPointX(), actual.getEndPointY());
			if( yIntercept > position[1] || //only lower values
					(yIntercept < lastY && yIntercept < actual.getEndPointY()) ||
					(yIntercept > lastY && yIntercept > actual.getEndPointY())){
				continue;
			}
			if(yIntercept == actual.getEndPointY()){
				skipNext = true;
				intersectCount++;
				continue;
			}
			double xIntercept = DrawingAreaAssist.getXIntercept(position[1], lastX, lastY, actual.getEndPointX(), actual.getEndPointY());
			if(	(xIntercept < lastX && xIntercept < actual.getEndPointX()) ||
					(xIntercept > lastX && xIntercept > actual.getEndPointX())){
				continue;
			}
			intersectCount++;
		}
		return intersectCount > 0 && intersectCount % 2 == 1;
	}
}
