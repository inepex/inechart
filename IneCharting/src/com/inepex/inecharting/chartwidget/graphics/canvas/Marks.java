package com.inepex.inecharting.chartwidget.graphics.canvas;

import java.util.TreeMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.inepex.inecharting.chartwidget.model.GraphicalObject;
import com.inepex.inecharting.chartwidget.model.HasViewport;
import com.inepex.inecharting.chartwidget.model.Mark;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.model.State;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo.AxisLocation;
import com.inepex.inecharting.chartwidget.properties.MarkInfo;
import com.inepex.inecharting.chartwidget.properties.TextBoxDrawingInfo;

public class Marks extends GraphicalObject implements HasViewport{

	/**
	 * actual drawn dimensions of marks
	 * int [] = {x,y,width,height}
	 */
	private TreeMap<Mark, int[]> boundingBoxes;
	private Context2d curveCanvas;
	private HorizontalAxisDrawingInfo xAxisInfo;

	private final int arrowWidth = 7;
	private final int arrowHeight = 10;
	private final int roundedCornerR = 5;
	private final int markMinSize = 18;
	
	public Marks(Context2d curveCanvas, HorizontalAxisDrawingInfo xAxisInfo) {
		this.curveCanvas = curveCanvas;
		this.xAxisInfo = xAxisInfo;
	}
	
	@Override
	public void moveViewport(double dx) {
		setViewport(ModelManager.get().getViewportMin(), ModelManager.get().getViewportMax());
	}

	@Override
	public void setViewport(double viewportMin, double viewportMax) {
		if(ModelManager.get().getMarkContainer().isXAxisExist()){
			boundingBoxes = new TreeMap<Mark, int[]>();
			for(Mark mark : ModelManager.get().getMarkContainer().getActualVisibleMarks()){
				if(mark.isImaginaryMark() && (mark.getState().equals(State.ACTIVE) || mark.getState().equals(State.FOCUSED))){
					int dx = 0, dy = 0, lastMarksHeight;
					int[] bb;
					for(Mark hiddenMark:ModelManager.get().getMarkContainer().getMarksForImaginaryMark(mark)){
						if(hiddenMark.getText().endsWith("#22")){
							ModelManager.get();
						}
						//first hidden mark (should has an arrow)
						if(dy == 0){
							bb = drawMarkTextBox(hiddenMark,dx, dy,true,xAxisInfo.getAxisLocation());
						}
						else{
							dx = mark.getxPosition() - hiddenMark.getxPosition();
							bb = drawMarkTextBox(hiddenMark,dx, dy,false,xAxisInfo.getAxisLocation());
							
						}
						if(bb == null){
							break;
						}
						boundingBoxes.put(hiddenMark, bb);
 						lastMarksHeight = bb[3];
						if(xAxisInfo.getAxisLocation().equals(AxisLocation.BOTTOM)){
							dy -= lastMarksHeight + 1;
						}
						else if(xAxisInfo.getAxisLocation().equals(AxisLocation.TOP)){
							dy += lastMarksHeight + 1;
						}
					}
					ModelManager.get();
				}
				else{
					int[] bb = drawMarkTextBox(mark, 0, 0, true,xAxisInfo.getAxisLocation());
					if(bb != null)
						boundingBoxes.put(mark,bb);
				}
			}
			ModelManager.get();
		}
		
	}
	
	public void addMark(Mark mark){
		ModelManager.get().getMarkContainer().getActualVisibleMarks().contains(mark);
	}
	
	public void removeMark(Mark mark){
		ModelManager.get().getMarkContainer().removeMark(mark);
	}
	
	private int [] drawMarkTextBox(Mark mark, int dx, final int dy, boolean hasArrow, AxisLocation xLocation){
		boolean arrowIsOnBottom = false;
		if(xAxisInfo.getAxisLocation().equals(AxisLocation.BOTTOM)){
			arrowIsOnBottom = true;
		}
		else if(xAxisInfo.getAxisLocation().equals(AxisLocation.TOP)){
			arrowIsOnBottom = false;
		}
		TextBoxDrawingInfo info = mark.getTextBoxDrawingInfo();
		int textHeight = 0, textWidth = 0;
		if(!mark.getText().equals("")){
			curveCanvas.save();
			curveCanvas.setFont(info.getFontSizeInCssPx() + "px " + info.getTextFontFamily() + " " + info.getTextFontStyle().toString() + " " + info.getTextFontWeight().toString());
			curveCanvas.setFillStyle(info.getTextColor());
			textHeight = info.getFontSizeInCssPx();
			textWidth = (int) curveCanvas.measureText(mark.getText()).getWidth();	
			curveCanvas.restore();
		}
		if(textHeight < markMinSize )
			textHeight = markMinSize;
		if(textWidth < markMinSize )
			textWidth = markMinSize;
		int x = mark.getxPosition() -  ModelManager.get().getViewportMinInPx();
		int y = dy;
		int width = 2 * roundedCornerR + textWidth;
		int height = 2 * roundedCornerR + textHeight;
		if(arrowIsOnBottom){
			y += ModelManager.get().getChartCanvasHeight() - (arrowHeight + height); 
		}
		else{
			y += arrowHeight;
		}
		int[] boundingBox = {
				x + dx,
				y,
				width,
				height};
		if(!isBoundingBoxVisible(boundingBox))
			return null;
		curveCanvas.save();
		curveCanvas.setFillStyle(info.getFillColor());
		curveCanvas.setGlobalAlpha(info.getFillOpacity());
		curveCanvas.setStrokeStyle(info.getborderColor());
		curveCanvas.setLineWidth(info.getborderWidth());
		curveCanvas.beginPath();
		curveCanvas.moveTo(boundingBox[0] + roundedCornerR, boundingBox[1] + boundingBox[3]);
		curveCanvas.quadraticCurveTo(
				boundingBox[0],
				boundingBox[1] + boundingBox[3],
				boundingBox[0],
				boundingBox[1] + boundingBox[3] - roundedCornerR);
		curveCanvas.lineTo( 
				boundingBox[0],
				boundingBox[1] + roundedCornerR);
		curveCanvas.quadraticCurveTo(
				boundingBox[0],
				boundingBox[1],
				boundingBox[0] + roundedCornerR, 
				boundingBox[1]);
		//arrow is on top
		if(hasArrow && !arrowIsOnBottom){
			curveCanvas.lineTo(
					boundingBox[0] + roundedCornerR + arrowWidth / 2,
					boundingBox[1] - arrowHeight);
			curveCanvas.lineTo(
					boundingBox[0] + roundedCornerR + arrowWidth,
					boundingBox[1]);
		}
		curveCanvas.lineTo(
				boundingBox[0] + boundingBox[2] - roundedCornerR,
				boundingBox[1]);
		curveCanvas.quadraticCurveTo(
				boundingBox[0] + boundingBox[2],
				boundingBox[1],
				boundingBox[0] + boundingBox[2],
				boundingBox[1] + roundedCornerR);
		curveCanvas.lineTo(
				boundingBox[0] + boundingBox[2], 
				boundingBox[1] + boundingBox[3] - roundedCornerR);
		curveCanvas.quadraticCurveTo(
				boundingBox[0] + boundingBox[2],
				boundingBox[1] + boundingBox[3],
				boundingBox[0] + boundingBox[2] - roundedCornerR,
				boundingBox[1] + boundingBox[3]);
		if(hasArrow && arrowIsOnBottom){
			curveCanvas.lineTo(
					boundingBox[0] + roundedCornerR + arrowWidth,
					boundingBox[1] + boundingBox[3]);
			curveCanvas.lineTo(
					boundingBox[0] + roundedCornerR + arrowWidth / 2,
					boundingBox[1] + boundingBox[3] + arrowHeight);
		}
		curveCanvas.closePath();
		curveCanvas.stroke();
		curveCanvas.fill();
		if(!mark.getText().equals("")){
			curveCanvas.setFont(info.getFontSizeInCssPx() + "px " + info.getTextFontFamily() + " " + info.getTextFontStyle().toString() + " " + info.getTextFontWeight().toString());
			curveCanvas.setTextAlign(TextAlign.START);
			curveCanvas.setTextBaseline(TextBaseline.TOP);
			curveCanvas.setFillStyle(info.getTextColor());
			curveCanvas.fillText(mark.getText(), boundingBox[0] + roundedCornerR, boundingBox[1] + roundedCornerR);
		}
		curveCanvas.restore();
		return boundingBox;
	}

	public TreeMap<Mark, int[]> getBoundingBoxes() {
		return boundingBoxes;
	}

	public boolean isBoundingBoxVisible(int[] bb){
		if(bb[1] > ModelManager.get().getChartCanvasHeight() ||
				bb[0] > ModelManager.get().getChartCanvasWidth() ||
				bb[0] + bb[2] <= 0 ||
				bb[1] + bb[3] <= 0 ){
			return false;
		}
		else return true;
	}
}
