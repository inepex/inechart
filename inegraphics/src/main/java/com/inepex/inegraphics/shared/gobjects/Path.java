package com.inepex.inegraphics.shared.gobjects;

import java.util.ArrayList;

import com.inepex.inegraphics.shared.Context;

public class Path extends GraphicalObject {
	
	protected ArrayList<PathElement> elements;

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
	}
	
	public Path(int basePointX, int basePointY, int zIndex, Context context,
			boolean stroke, boolean fill) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		this.elements = new ArrayList<PathElement>();
	}

	public Path moveTo(int x, int y, boolean relativeCoords){
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
	
	public Path lineTo(int x, int y, boolean relativeCoords){
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
	
	public Path quadraticCurveTo(int x, int y, int cpX, int cpY, boolean relativeCoords){
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

	public void concatenatePath(Path otherPath){
		for(PathElement element : otherPath.elements){
			elements.add(element);
		}
	}
}
