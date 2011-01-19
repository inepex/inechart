package com.inepex.inecharting.chartwidget.graphics;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;

/**
 * A simple timer implementation for delayed drawing,
 * typically used when a curve's drawing policy was set to draw Point by Point
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class DrawingJobScheduler {
	public static final ArrayList<DrawingJobScheduler> instances = new ArrayList<DrawingJobScheduler>();
	public static void cancelAllJobs() {
		for(DrawingJobScheduler s : instances)
			s.stop();
		instances.clear();
	}
	private HasDrawingJob visualizer;
	private long delayBetweenDrawingPoints;
	private boolean jobInProgress;
	private Timer timer;
	
	public DrawingJobScheduler(final HasDrawingJob visualizer, long delay) {
		this.delayBetweenDrawingPoints = delay;
		this.visualizer = visualizer;
		jobInProgress = false;	
		instances.add(this);
	}
	
	public void start(){
		jobInProgress = true ;
		timer = new Timer() {
			
			@Override
			public void run() {
				if(jobInProgress)
					visualizer.drawNextPoint();	
			}
		};
		timer.scheduleRepeating((int) delayBetweenDrawingPoints);
	}
	
	public void stop(){
		jobInProgress = false;
		timer.cancel();
	}
	
	public boolean isJobInProgress() {
		return jobInProgress;
	}
}
