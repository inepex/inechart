package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;

import com.inepex.inecharting.chartwidget.model.HorizontalTimeAxis.Resolution;

public class AxisCalculator {

	public final int horizontal_minTickDistanceInPX = 90;
	private final int vertical_minTickDistanceInPX = 20;
	private ModelManager modelManager;
	
	private final long SECOND = 1000;
	private final long MINUTE = SECOND * 60;
	private final long HOUR = MINUTE * 60;
	private final long DAY = 24 * HOUR;
	private final long WEEK = 7 * DAY;
	

	public AxisCalculator(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public void setHorizontalAxis(Axis axis, int minDistanceBetweenTicks){
		double minDistanceBetweenTicksPX = modelManager.calculateDistance(minDistanceBetweenTicks);
		double fixTick;
		double tickDistance;
		if(axis instanceof HorizontalTimeAxis){
			HorizontalTimeAxis tAxis = (HorizontalTimeAxis)axis;
			Resolution resolution;
			if(minDistanceBetweenTicksPX <= SECOND){
				resolution = Resolution.SECOND;
				tickDistance = SECOND;
				fixTick = ((int)(modelManager.getxMin() / SECOND)) * SECOND;
			}
			else if (minDistanceBetweenTicksPX <= MINUTE){
				resolution = Resolution.MINUTE;
				tickDistance = MINUTE;
				fixTick = ((int)(modelManager.getxMin() / MINUTE)) * MINUTE;
			}
			else if (minDistanceBetweenTicksPX <= 30 * MINUTE){
				resolution = Resolution.MINUTE;
				tickDistance = 30 * MINUTE;
				fixTick = ((int)(modelManager.getxMin() / (30*MINUTE))) * 30 * MINUTE;
			}
			else if (minDistanceBetweenTicksPX <= HOUR){
				resolution = Resolution.HOUR;
				tickDistance = HOUR;
				fixTick = ((int)(modelManager.getxMin() / HOUR)) * HOUR;
			}
			else if (minDistanceBetweenTicksPX <= 12 * HOUR){
				resolution = Resolution.HOUR;
				tickDistance =  12 * HOUR;
				fixTick = ((int)(modelManager.getxMin() / ( 12 * HOUR) )) * 12 * HOUR;
			}
			else if (minDistanceBetweenTicksPX <= DAY){
				resolution = Resolution.DAY;
				tickDistance = DAY;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
			else if (minDistanceBetweenTicksPX <= WEEK){
				resolution = Resolution.DAY;
				tickDistance = WEEK;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
			else /*if (minDistanceBetweenTicks <= WEEK * 5)*/{
				resolution = Resolution.DATE;
				tickDistance = WEEK * 5;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
//			else{
//				resolution = Resolution.DATE_W_YEAR;
//				tickDistance = (((int)(minDistanceBetweenTicks / DAY)) * DAY) + DAY;
//				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
//			}
			tAxis.setResolution(resolution);
			tAxis.setFixTick(fixTick);
			tAxis.setTickDistance(tickDistance);
		}
		//simple number axis
		else{
			axis.setFixTick(modelManager.getxMin());
			axis.setTickDistance(minDistanceBetweenTicksPX); //TODO
		}
	}

	public void setVerticalAxis(Axis axis, Curve.Axis y){
		double  min,max;
		if(y.equals(com.inepex.inecharting.chartwidget.model.Curve.Axis.Y)){
			min = modelManager.getyMin();
			max = modelManager.getyMax();
		}
		else if(y.equals(com.inepex.inecharting.chartwidget.model.Curve.Axis.Y2)){
			min = modelManager.getY2Min();
			max = modelManager.getY2Max();
		}
		else{
			return;
		}
		double minDistBetweenTicks = modelManager.calculateYDistance(vertical_minTickDistanceInPX, min, max);
		axis.setFixTick(min);
		axis.setTickDistance(minDistBetweenTicks);
	}

	public void calculateAxes(ArrayList<Curve> curves, Axis xAxis, Axis yAxis, Axis y2Axis){
//		boolean redrawNecessary = false;
		Double xMin = null, xMax = null,
			y1Min = null, y1Max = null,
			y2Min = null, y2Max = null;
		for(Curve curve:curves){
			if(xMin == null || curve.getDataMap().firstKey() < xMin){
				xMin = curve.getDataMap().firstKey();
			}
			if(xMax == null || curve.getDataMap().lastKey() > xMax){
				xMax = curve.getDataMap().lastKey();
			}
			switch (curve.getCurveAxis()) {
			case Y:
				if(y1Min == null || curve.getMinValue() < y1Min){
					y1Min = curve.getMinValue();
				}
				if(y1Max == null || curve.getMaxValue() > y1Max){
					y1Max = curve.getMaxValue();
				}
				break;
			case Y2:
				if(y2Min == null || curve.getMinValue() < y2Min){
					y2Min = curve.getMinValue();
				}
				if(y2Max == null || curve.getMaxValue() > y2Max){
					y2Max = curve.getMaxValue();
				}
				break;
			default:
				break;
			}
		}
		if(xMin == null || xMax == null)
			xMin = xMax = null;
		if(y1Min == null || y1Max == null)
			y1Min = y1Max = null;
		if(y2Min == null || y2Max == null)
			y2Min = y2Max = null;
	
		/** extreme related notifications  **/
		Double oldxMin = modelManager.getxMin(),
			oldy1Min = modelManager.getyMin(), oldy1Max = modelManager.getyMax(),
			oldy2Min = modelManager.getY2Min(), oldy2Max = modelManager.getY2Max();
			
		modelManager.setxMax(xMax);
		modelManager.setxMin(xMin);
		modelManager.setyMax(y1Max);
		modelManager.setyMin(y1Min);
		modelManager.setY2Max(y2Max);
		modelManager.setY2Min(y2Min);
		
		if(xMin != null){
			//new xMin -> Points should shifted
			if(oldxMin != null && xMin != oldxMin){
				double dx = oldxMin - xMin;
				for(Curve curve:curves){
					modelManager.movePoints(curve, dx);
				}
			}
			//calc xAxis
			if(xAxis != null && xAxis.getTickDistance() == 0) {
				setHorizontalAxis(xAxis,horizontal_minTickDistanceInPX);
			}
		}
		else{
			if(xAxis != null){
				xAxis.setFixTick(0);
				xAxis.setTickDistance(0);
			}	
		}
		if(y1Min != null){
			if(oldy1Min != null && (y1Min != oldy1Min || y1Max != oldy1Max)){
				for(Curve curve:curves)
					if(curve.getCurveAxis().equals(Curve.Axis.Y))
						modelManager.rescaleYPositions(curve, y1Min, y1Max);
				//calc yAxis
			}
			if(yAxis != null){
				setVerticalAxis(yAxis, Curve.Axis.Y);
			}
		}
		else{
			if(yAxis != null){
				yAxis.setFixTick(0);
				yAxis.setTickDistance(0);
			}
		}
		if(y2Min != null){
			if(oldy2Min != null && (y2Min != oldy2Min || y2Max != oldy2Max)){
				for(Curve curve:curves)
					if(curve.getCurveAxis().equals(Curve.Axis.Y2))
						modelManager.rescaleYPositions(curve, y2Min, y2Max);
				//calc y2Axis
				if(y2Axis != null){
					setVerticalAxis(y2Axis, Curve.Axis.Y2);
				}
			}
		}
		else{
			if(y2Axis != null){
				y2Axis.setFixTick(0);
				y2Axis.setTickDistance(0);
			}
		}
	}
}
