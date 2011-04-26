package com.inepex.inecharting.chartwidget.newimpl.axes;

import java.util.ArrayList;

import com.inepex.inecharting.chartwidget.newimpl.IneChartModul;
import com.inepex.inegraphics.shared.DrawingArea;

public class Axes extends IneChartModul {
	
	private ArrayList<Axis> axes;
	
	public Axes(DrawingArea canvas) {
		super(canvas);
		 axes = new ArrayList<Axis>();
	}

	
	
	public void addAxis(Axis axis){
		axes.add(axis);
	}
	
	public void removeAxis(Axis axis){
		axes.remove(axis);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setViewport(double startX, double stopX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveViewport(double dX) {
		// TODO Auto-generated method stub
		
	}

}
