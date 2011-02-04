package com.inepex.inecharting.chartwidget.model;

import com.inepex.inecharting.chartwidget.model.TimeAxis.Resolution;

public class AxisCalculator {

	private final int tickDistanceInPX = 40;
	private ModelManager modelManager;
	
	private final long SECOND = 1000;
	private final long MINUTE = SECOND * 60;
	private final long HOUR = MINUTE * 60;
	private final long DAY = 24 * HOUR;
	private final long WEEK = 7 * DAY;
	

	public AxisCalculator(ModelManager modelManager) {
		this.modelManager = modelManager;
	}






	private void calculateDistanceBetweenTicks(Axis axis){
		double minDistanceBetweenTicks = modelManager.calculateDistance(tickDistanceInPX);
		double fixTick;
		double tickDistance;
		if(axis instanceof TimeAxis){
			TimeAxis tAxis = (TimeAxis)axis;
			Resolution resolution;
			if(minDistanceBetweenTicks <= SECOND){
				resolution = Resolution.SECOND;
				tickDistance = SECOND;
				fixTick = ((int)(modelManager.getxMin() / SECOND)) * SECOND;
			}
			else if (minDistanceBetweenTicks <= MINUTE){
				resolution = Resolution.MINUTE;
				tickDistance = MINUTE;
				fixTick = ((int)(modelManager.getxMin() / MINUTE)) * MINUTE;
			}
			else if (minDistanceBetweenTicks <= 30 * MINUTE){
				resolution = Resolution.MINUTE;
				tickDistance = 30 * MINUTE;
				fixTick = ((int)(modelManager.getxMin() / MINUTE)) * MINUTE;
			}
			else if (minDistanceBetweenTicks <= HOUR){
				resolution = Resolution.HOUR;
				tickDistance = HOUR;
				fixTick = ((int)(modelManager.getxMin() / HOUR)) * HOUR;
			}
			else if (minDistanceBetweenTicks <= 12 * HOUR){
				resolution = Resolution.HOUR;
				tickDistance =  12 * HOUR;
				fixTick = ((int)(modelManager.getxMin() / HOUR)) * HOUR;
			}
			else if (minDistanceBetweenTicks <= DAY){
				resolution = Resolution.DAY;
				tickDistance = DAY;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
			else if (minDistanceBetweenTicks <= WEEK){
				resolution = Resolution.DAY;
				tickDistance = WEEK;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
			else if (minDistanceBetweenTicks <= WEEK * 5){
				resolution = Resolution.DATE;
				tickDistance = WEEK * 5;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
			else{
				resolution = Resolution.DATE_W_YEAR;
				tickDistance = (((int)(minDistanceBetweenTicks / DAY)) * DAY) + DAY;
				fixTick = ((int)(modelManager.getxMin() / DAY)) * DAY;
			}
			tAxis.setResolution(resolution);
			tAxis.setFixTick(fixTick);
			tAxis.setTickDistance(tickDistance);
		}
		//simple number axis
		else{
			
		}
	}

}
