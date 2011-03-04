package com.inepex.inecharting.chartwidget.model;

import java.util.ArrayList;
import java.util.TreeMap;

import java.util.Collections;

import com.inepex.inecharting.chartwidget.event.ExtremesChangeEvent;
import com.inepex.inecharting.chartwidget.event.ExtremesChangeHandler;

public class MarkContainer implements HasViewport, ExtremesChangeHandler{

	private TreeMap<Double, Mark> marks;
	private TreeMap<Double, Mark> imaginaryMarks;
	private int overlapFilterWidthX = 38;
	private ArrayList< Mark> visibleMarks;
	private boolean isXAxisExist = false;
	
	public boolean isXAxisExist() {
		return isXAxisExist;
	}
	
	public MarkContainer() {
		marks = new TreeMap<Double, Mark>();
	}

	@Override
	public void moveViewport(double dx) {
		visibleMarks = getVisibleMarks(ModelManager.get().getViewportMin(), ModelManager.get().getViewportMax());
	}

	private void calculatexPositions(){
		for(double x : marks.keySet()){
			marks.get(x).setxPosition((int) ModelManager.get().calculateX(marks.get(x).getxValue()));
		}
	}
	
	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		calculatexPositions();
		xFilterMarks();
	}
	
	public void addMark(Mark mark){
		this.marks.put(mark.getxValue(), mark);
		if(isXAxisExist){
			mark.setxPosition((int) ModelManager.get().calculateX(mark.getxValue()));
			xFilterMarks();
		}
	}
	
	public void removeMark(Mark mark){
		marks.remove(mark.getxValue());
		xFilterMarks();
	}
	
	private void xFilterMarks(){
		imaginaryMarks = new TreeMap<Double, Mark>();
		visibleMarks = new ArrayList<Mark>();
		Double firstIndex = null;
		ArrayList<Double> problematicIndices = null;
		int willNotShowCount = 0;
		for(double x:marks.keySet()){
			if(firstIndex == null){
				firstIndex = x;
				continue;
			}
			// if it is problematic element
			else if(marks.get(x).getxPosition() - marks.get(firstIndex).getxPosition() < overlapFilterWidthX){
				if(problematicIndices == null){ 
					problematicIndices = new ArrayList<Double>();
					problematicIndices.add(firstIndex);
				}
				problematicIndices.add(x);
			}
			//not problematic
			else{
				//there were some  points need  filtering
				if(problematicIndices != null){
					willNotShowCount +=  problematicIndices.size() - 1; 
					ArrayList<Mark> problematicMarks = new ArrayList<Mark>();
					for(Double key:problematicIndices)
						problematicMarks.add( marks.get(key));
					Mark markToShow = createImaginaryMark(problematicMarks);
					for(Double key:problematicIndices)
						imaginaryMarks.put(key, markToShow);
					problematicIndices = null;
				}
				
				firstIndex = x;
			}
		}
		if(firstIndex != null){
			//there were some  points need  filtering
			if(problematicIndices != null){
				willNotShowCount +=  problematicIndices.size() - 1; 
				ArrayList<Mark> problematicMarks = new ArrayList<Mark>();
				for(Double key:problematicIndices)
					problematicMarks.add( marks.get(key));
				Mark markToShow = createImaginaryMark(problematicMarks);
				for(Double key:problematicIndices)
					imaginaryMarks.put(key, markToShow);
				problematicIndices = null;
			}
			
		}
		visibleMarks = getVisibleMarks(ModelManager.get().getViewportMin(), ModelManager.get().getViewportMax());
	}
	
	public ArrayList<Mark> getVisibleMarks(double min, double max){
		ArrayList<Mark> ret = new ArrayList<Mark>();
		for(double x:marks.keySet()){
			if(x > max)
				break;
			else if(x >= min){
				if(imaginaryMarks.containsKey(x)){
					if(!ret.contains(imaginaryMarks.get(x)))
						ret.add(imaginaryMarks.get(x));
				}
				else{
					ret.add(marks.get(x));
				}
			}
		}
		return ret;
	}
		
	private Mark createImaginaryMark(ArrayList<Mark> marks){
		Mark first = marks.get(0); 
		Mark img = new Mark(first.getxValue());
		img.setImaginaryMark(true);
		img.setxPosition(first.getxPosition());
		return img;
	}
	
	public ArrayList<Mark> getActualVisibleMarks(){
		return visibleMarks;
	}
	
	public ArrayList<Mark> getMarksForImaginaryMark(Mark imaginaryMark){
		if(imaginaryMark.isImaginaryMark()){
			 ArrayList<Mark> marksImg = new ArrayList<Mark>();
			 for(double x : marks.keySet()){
				 if(x < imaginaryMark.getxValue()){
					 continue;
				 }
				 else{
					 Mark mark = marks.get(x);
					 if(imaginaryMarks.containsKey(x) && imaginaryMarks.get(x).equals(imaginaryMark)){
						 marksImg.add(mark);
					 }else{
						 break;
					 }
				 }
			 }
			 return marksImg ;
		}
		return null;
	}

	public void addMarks(ArrayList<Mark> marks){
		for(Mark mark : marks)
			this.marks.put(mark.getxValue(), mark);
		if(isXAxisExist){
			calculatexPositions();
			xFilterMarks();
		}
	}

	@Override
	public void onExtremesChange(ExtremesChangeEvent event) {
		isXAxisExist = true;
		if(event.getAxis().equals(Axes.X)){
			calculatexPositions();
			xFilterMarks();
		}
	}
	
	
}

