package com.inepex.inegraphics.impl.client;

import java.util.ArrayList;
import java.util.List;

import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.gobjects.Circle;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
import com.inepex.inegraphics.shared.gobjects.Rectangle;

public class MouseAssist {
	
	/**
	 * GOs must have same size to make it work properly
	 * The given collection will be sorted by canvasX
	 * @param coords
	 * @param gos circles or rectangles
	 * @return null if not circles or rectangles
	 */
	public static List<GraphicalObject> getMouseOverGOs(int[] coords, List<GraphicalObject> gos) {
		if(gos.size() == 0){
			return new ArrayList<GraphicalObject>();
		}
		double from, to;
		GraphicalObject first = gos.get(0);
		if(first instanceof Circle){
			from = coords[0] - ((Circle)first).getRadius() + first.getContext().getStrokeWidth() / 2;
			to = coords[0] + ((Circle)first).getRadius() + first.getContext().getStrokeWidth() / 2;
		}
		else if (first instanceof Rectangle){
			from = coords[0] + first.getContext().getStrokeWidth() / 2;
			to = coords[0] + ((Rectangle)first).getWidth() + first.getContext().getStrokeWidth() / 2;
		}
		else{
			return new ArrayList<GraphicalObject>();
		}
		List<GraphicalObject> subList = getSublist(gos, from, to);
		List<GraphicalObject> over = new ArrayList<GraphicalObject>();
		for(GraphicalObject go : subList){
			if(isMouseOver(coords, go)){
				over.add(go);
			}
		}
		return over;
	}
	
	public static List<GraphicalObject> getSublist(List<GraphicalObject> gos, double fromX, double toX){
		int i = binarySearchGOByBasePointX(fromX, gos, GraphicalObjectSearchParameter.higher);
		int j = binarySearchGOByBasePointX(toX, gos, GraphicalObjectSearchParameter.lower);
		if(i == -1){
			i = 0;
		}
		if(j == -1){
			j = gos.size() - 1;
		}
		if(i > j){
			return new ArrayList<GraphicalObject>();
		}
		return gos.subList(i, j);
	}
	
	public static enum GraphicalObjectSearchParameter{
		exact,
		lower,
		higher,
		closest
	}
	
	public static int binarySearchGOByBasePointX(double basePointX, List<GraphicalObject> gos,  GraphicalObjectSearchParameter gosp){
		int intervalLeft = 0;
		int intervalRight = gos.size() - 1;
		int intervalMid = 0;
		double midBPX = 0;
		while (intervalRight >= intervalLeft){
			intervalMid = (intervalLeft + intervalRight) / 2;
			midBPX = gos.get(intervalMid).getBasePointX();
			if (midBPX < basePointX){
				intervalLeft = intervalMid + 1;
			}
			else if (midBPX > basePointX){
				intervalRight = intervalMid - 1;
			}
			else{ //midvalue = x;
				return intervalMid;
			}
		}
		if(gosp == GraphicalObjectSearchParameter.exact){
			return -1;
		}
		else{
			int lowerIndex;
			int higherIndex;
			if(midBPX > basePointX){
				lowerIndex = intervalMid - 1;
				higherIndex = intervalMid;
			}
			else{
				lowerIndex = intervalMid;
				higherIndex = intervalMid + 1;
			}
			if(gosp == GraphicalObjectSearchParameter.higher){
				return higherIndex < gos.size() ? higherIndex : - 1;
			}
			else if(gosp == GraphicalObjectSearchParameter.lower){
				return lowerIndex >= 0 ? lowerIndex : -1;
			}
			else { //closest
				Double lowerValue = lowerIndex >= 0 ? gos.get(lowerIndex).getBasePointX() : Double.NEGATIVE_INFINITY;
				Double higherValue = higherIndex < gos.size()  ? gos.get(higherIndex).getBasePointX() : Double.POSITIVE_INFINITY;
				return Math.abs(lowerValue - basePointX) < Math.abs(higherValue - basePointX) ? lowerIndex : higherIndex;
			}
		}
	}
	
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
