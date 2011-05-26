package com.inepex.inegraphics.shared.gobjects;

import java.util.ArrayList;

import com.inepex.inegraphics.shared.Context;

public class Path extends GraphicalObject {
	
	protected ArrayList<PathElement> elements;
	protected boolean alignElementsWithBasePoint;

	/**
	 * Deep copy a {@link Path} object.
	 * @param copy
	 */
	public Path(Path copy){
		super(copy.basePointX, copy.basePointY, copy.zIndex, copy.context, copy.stroke, copy.fill);
		this.elements = new ArrayList<PathElement>();
		for(Object e : copy.elements.toArray()){
			elements.add((PathElement) e);
		}
		alignElementsWithBasePoint = copy.alignElementsWithBasePoint;
	}
	
	public Path(double basePointX, double basePointY, int zIndex, Context context,
			boolean stroke, boolean fill) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.elements = new ArrayList<PathElement>();
		alignElementsWithBasePoint = true;
	}

	public Path moveTo(double x, double y, boolean relativeCoords){
		if(relativeCoords){
			if( getLastPathElement() != null){
				x += getLastPathElement().endPointX;
				y += getLastPathElement().endPointY;
			}
			else{
				x += basePointX;
				y += basePointY;
			}
		}
		elements.add(new MoveTo(x, y));
		return this;
	}
	
	public Path moveTo(double theta, double r){
		double x,y;
		x = r * Math.cos(theta);
		y = r * Math.sin(theta);
		if( getLastPathElement() != null){
			x += getLastPathElement().endPointX;
			y += getLastPathElement().endPointY;
		}
		else{
			x += basePointX;
			y += basePointY;
		}
		elements.add(new MoveTo((int)x, (int)y));	
		return this;
	}
	
	public Path lineTo(double theta, double r){
		double x,y;
		x = r * Math.cos(theta);
		y = r * Math.sin(theta);
		if( getLastPathElement() != null){
			x += getLastPathElement().endPointX;
			y += getLastPathElement().endPointY;
		}
		else{
			x += basePointX;
			y += basePointY;
		}
		elements.add(new LineTo((int)x, (int)y));	
		
		
		return this;
	}
	
	public PathElement getLastPathElement(){
		if(elements.size() > 0)
			return elements.get(elements.size()-1);
		else
			return null;
	}
	
	public Path lineTo(double x, double y, boolean relativeCoords){
		if(relativeCoords){
			if( getLastPathElement() != null){
				x += getLastPathElement().endPointX;
				y += getLastPathElement().endPointY;
			}
			else{
				x += basePointX;
				y += basePointY;
			}
		}
		elements.add(new LineTo(x, y));
		return this;
	}
	
	public Path quadraticCurveTo(double x, double y, double cpX, double cpY, boolean relativeCoords){
		if(relativeCoords && getLastPathElement() != null){
			x += getLastPathElement().endPointX;
			y += getLastPathElement().endPointY;
		}
		elements.add(new QuadraticCurveTo(x, y, cpX, cpY));
		return this;
	}
	
	public Path moveToBasePoint(){
		elements.add(new MoveTo(basePointX, basePointY));
		return this;
	}
	
	public Path lineToBasePoint(){
		elements.add(new LineTo(basePointX, basePointY));
		return this;
	}
	
	public final ArrayList<PathElement> getPathElements() {
		return elements;
	}

	/**
	 * Adds the given paths's elements to this path's {@link PathElement} container
	 * @param otherPath
	 */
	public void concatenatePath(Path otherPath){
		for(PathElement element : otherPath.elements){
			elements.add(element);
		}
	}

	/**
	 * @return the alignElementsWithBasePoint
	 */
	public boolean isAlignElementsWithBasePoint() {
		return alignElementsWithBasePoint;
	}

	/**
	 * @param alignElementsWithBasePoint the alignElementsWithBasePoint to set
	 */
	public void setAlignElementsWithBasePoint(boolean alignElementsWithBasePoint) {
		this.alignElementsWithBasePoint = alignElementsWithBasePoint;
	}

	
	/* (non-Javadoc)
	 * @see com.inepex.inegraphics.shared.gobjects.GraphicalObject#setBasePointX(double)
	 */
	@Override
	public void setBasePointX(double basePointX) {
		if(alignElementsWithBasePoint){
			double dx = basePointX - this.basePointX;
			for(PathElement element : elements){
				element.setEndPointX(element.getEndPointX() + dx);
				if(element instanceof QuadraticCurveTo){
					((QuadraticCurveTo) element).setControlPointX(((QuadraticCurveTo) element).getControlPointX() + dx);
				}
			}
		}
		super.setBasePointX(basePointX);
	}


	/* (non-Javadoc)
	 * @see com.inepex.inegraphics.shared.gobjects.GraphicalObject#setBasePointY(double)
	 */
	@Override
	public void setBasePointY(double basePointY) {
		if(alignElementsWithBasePoint){
			double dy = basePointY - this.basePointY;
			for(PathElement element : elements){
				element.setEndPointY(element.getEndPointY() + dy);
				if(element instanceof QuadraticCurveTo){
					((QuadraticCurveTo) element).setControlPointY(((QuadraticCurveTo) element).getControlPointY() + dy);
				}
			}
		}
		super.setBasePointY(basePointY);
	}

	
}
