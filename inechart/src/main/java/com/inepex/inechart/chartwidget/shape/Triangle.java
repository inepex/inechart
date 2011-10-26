package com.inepex.inechart.chartwidget.shape;

import java.util.ArrayList;

import com.inepex.inegraphics.shared.gobjects.GraphicalObject;

public class Triangle extends Shape {
	
	int width, height;
	boolean flipVertical;

	@Override
	public ArrayList<GraphicalObject> toGraphicalObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphicalObject toInteractiveGraphicalObject(int distance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setSideLength(int sideLength){
		width = sideLength;
		height = sideLength; //TODO
	}

	public boolean isFlipVertical() {
		return flipVertical;
	}

	public void setFlipVertical(boolean flipVertical) {
		this.flipVertical = flipVertical;
	}
	
}
