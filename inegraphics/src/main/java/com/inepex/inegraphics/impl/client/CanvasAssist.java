package com.inepex.inegraphics.impl.client;

import java.util.ArrayList;

import com.inepex.inegraphics.impl.client.canvas.CanvasWidget;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.Circle;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.LineTo;
import com.inepex.inegraphics.shared.gobjects.MoveTo;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
import com.inepex.inegraphics.shared.gobjects.QuadraticCurveTo;
import com.inepex.inegraphics.shared.gobjects.Rectangle;

public class CanvasAssist {

	public static void applyContext(CanvasWidget canvas, Context context, boolean applyShadow){
		canvas.setGlobalAlpha(context.getAlpha());
		canvas.setFillStyle(context.getFillColor());
		canvas.setStrokeStyle(context.getStrokeColor());
		canvas.setLineWidth(context.getStrokeWidth());
		if(context.getTransformation() != null){
			canvas.setTransform(
					context.getTransformation()[0], 
					context.getTransformation()[1],
					context.getTransformation()[2], 
					context.getTransformation()[3], 
					context.getTransformation()[4], 
					context.getTransformation()[5]
			);
		}
		if(applyShadow){
			canvas.setShadowOffsetX(context.getShadowOffsetX());
			canvas.setShadowOffsetX(context.getShadowOffsetX());
			canvas.setShadowColor(context.getShadowColor());
		}
	}

	public static void clear(CanvasWidget canvas) {
		canvas.setFillStyle("white");
		canvas.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public static void drawPath(CanvasWidget canvas, Path path) {

		ArrayList<PathElement> pathElements = path.getPathElements();
		if((path.hasFill() == false && path.hasStroke() == false )||
				pathElements.size() < 1){
			return;
		}
		applyContext(canvas, path.getContext(), false);
		canvas.beginPath();
		canvas.moveTo(path.getBasePointX(), path.getBasePointY());
		for(PathElement pe : pathElements){
			if(pe instanceof QuadraticCurveTo){
				QuadraticCurveTo qTo = (QuadraticCurveTo) pe;
				canvas.quadraticCurveTo(qTo.getControlPointX(), qTo.getControlPointY(), qTo.getEndPointX(), qTo.getEndPointY());
			}
			else if(pe instanceof LineTo){
				LineTo lTo = (LineTo) pe;
				canvas.lineTo(lTo.getEndPointX(), lTo.getEndPointY());
			}
			else if(pe instanceof MoveTo){
				MoveTo mTo = (MoveTo) pe;
				canvas.moveTo(mTo.getEndPointX(), mTo.getEndPointY());
			}
		}
		if(path.hasStroke()){
			canvas.stroke();
		}
		if(path.hasFill()){
			canvas.fill();
		}
	}
	
	public static void drawPathElemnts(CanvasWidget canvas, ArrayList<PathElement> pathElements){
		for(PathElement pe : pathElements){
			if(pe instanceof QuadraticCurveTo){
				QuadraticCurveTo qTo = (QuadraticCurveTo) pe;
				canvas.quadraticCurveTo(qTo.getControlPointX(), qTo.getControlPointY(), qTo.getEndPointX(), qTo.getEndPointY());
			}
			else if(pe instanceof LineTo){
				LineTo lTo = (LineTo) pe;
				canvas.lineTo(lTo.getEndPointX(), lTo.getEndPointY());
			}
			else if(pe instanceof MoveTo){
				MoveTo mTo = (MoveTo) pe;
				canvas.moveTo(mTo.getEndPointX(), mTo.getEndPointY());
			}
		}
	}

	public static void drawRectangle(CanvasWidget canvas, Rectangle rectangle) {
		applyContext(canvas, rectangle.getContext(), false);
		double x = rectangle.getBasePointX(), y = rectangle.getBasePointY(), width = rectangle.getWidth(), height = rectangle.getHeight(), roundedCornerRadius = rectangle.getRoundedCornerRadius();
		canvas.beginPath();
		if(roundedCornerRadius == 0){
			if(rectangle.hasFill()){
				canvas.fillRect(x,y,width,height);
			}
			if(rectangle.hasStroke()){
				canvas.strokeRect(x,y,width,height);
			}
		}
		//we create a path
		else{
			//start just below (with the amount of the roundedCornerRadius) the upper-left point (GO's basePoint) of the rectangle
			//and draw lines clockwise
			canvas.moveTo(x, y + roundedCornerRadius);
			canvas.quadraticCurveTo(x, y, x + roundedCornerRadius, y);
			canvas.lineTo(x + width - roundedCornerRadius, y);
			canvas.quadraticCurveTo(x + width, y, x + width, y + roundedCornerRadius);
			canvas.lineTo(x + width, y + height - roundedCornerRadius);
			canvas.quadraticCurveTo(x + width, y + height, x + width - roundedCornerRadius, y + height);
			canvas.lineTo(x + roundedCornerRadius, y + height);
			canvas.quadraticCurveTo(x, y + height, x , y + height  - roundedCornerRadius);
			canvas.closePath();
			if(rectangle.hasFill()){
				canvas.fill();
			}
			if(rectangle.hasStroke()){
				canvas.stroke();
			}
		}
	}

	public static void drawCircle(CanvasWidget canvas, Circle circle) {
		applyContext(canvas, circle.getContext(), false);
		canvas.beginPath();
		canvas.arc(circle.getBasePointX(), circle.getBasePointY(), circle.getRadius(), 0, Math.PI * 2, false);
		if(circle.hasFill()){
			canvas.fill();
		}
		if(circle.hasStroke()){
			canvas.stroke();
		}
	}

	public static void drawLine(CanvasWidget canvas, Line line) {
		applyContext(canvas, line.getContext(), false);
		canvas.beginPath();
		canvas.moveTo(line.getBasePointX(), line.getBasePointY());
		canvas.lineTo(line.getEndPointX(), line.getEndPointY());
		canvas.stroke();
	}

}
