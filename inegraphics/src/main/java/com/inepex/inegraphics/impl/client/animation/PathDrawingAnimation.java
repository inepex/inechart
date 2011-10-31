package com.inepex.inegraphics.impl.client.animation;

import java.util.ArrayList;

import com.inepex.inegraphics.impl.client.CanvasAssist;
import com.inepex.inegraphics.impl.client.canvas.CanvasWidget;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.gobjects.LineTo;
import com.inepex.inegraphics.shared.gobjects.MoveTo;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
import com.inepex.inegraphics.shared.gobjects.QuadraticCurveTo;

public class PathDrawingAnimation extends DrawingAreaGWTAnimation {
	protected CanvasWidget canvas;
	protected Context context;
	protected Path path;
	protected int lastEnd;
	protected FPSLinstener listener;
	protected long last;

	public FPSLinstener getListener() {
		return listener;
	}

	public void setListener(FPSLinstener listener) {
		this.listener = listener;
	}

	public PathDrawingAnimation(CanvasWidget canvas, Path path) {
		super();
		this.canvas = canvas;
		this.path = path;
	}

	@Override
	protected void onStart() {
		lastEnd = 0;
		last = System.currentTimeMillis();
		super.onStart();
	}

	@Override
	protected void onComplete() {
		// TODO Auto-generated method stub
		super.onComplete();
	}

	@Override
	protected void onUpdate(double progress) {
		// 1.0 : canvas.width
		// 0.0 : 0
//		int to = (int) (progress * canvas.getWidth());
//		drawPath(lastEnd, to);
//		lastEnd = to;
		movePath((int) (-canvas.getWidth() + progress*canvas.getWidth()));
		if(listener != null){
			long actual = System.currentTimeMillis();
			listener.onFPSReport((int) (1000 / (actual - last)));
			last = actual;
		}
	}


	public void drawPath(int from, int to){
		if(this.path.hasFill()){
			Path fillPath = DrawingAreaAssist.clipFillPathWithRectangle(this.path, from, 0, to-from, canvas.getHeight());
			if(fillPath != null){
				applyContext(fillPath.getContext());
				ArrayList<PathElement> pathElements = fillPath.getPathElements();
				if((fillPath.hasFill() == false && fillPath.hasStroke() == false )||
						pathElements.size() < 1){
					return;
				}
				canvas.beginPath();
				canvas.moveTo(fillPath.getBasePointX(), fillPath.getBasePointY());
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
				canvas.fill();
			}
		}
		if(this.path.hasStroke()){
			Path strokePath = DrawingAreaAssist.clipStrokePathWithRectangle(this.path, from, 0, to-from, canvas.getHeight(), true);
			if(strokePath != null){
				applyContext(strokePath.getContext());
				ArrayList<PathElement> pathElements = strokePath.getPathElements();
				if((strokePath.hasFill() == false && strokePath.hasStroke() == false )||
						pathElements.size() < 1){
					return;
				}
				canvas.beginPath();
				canvas.moveTo(strokePath.getBasePointX(), strokePath.getBasePointY());
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
				canvas.stroke();
			}
		}
	}

	
	
	public void movePath(int dx){
		canvas.save();
		canvas.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.translate(dx, 0);
		CanvasAssist.drawPath(canvas, path);
		canvas.restore();
	}
	
	/**
	 * Sets the context variables of the canvas.
	 * @param context
	 */
	protected void applyContext(Context context){
		if(canvas != null){
			if(this.context == null){
				canvas.setLineJoin("round");
				canvas.setLineCap("butt");
			}
			if(this.context == null || context.getAlpha() != this.context.getAlpha()){
				canvas.setGlobalAlpha(context.getAlpha());
			}
			if(this.context == null || !context.getFillColor().equals(this.context.getFillColor())){
				canvas.setFillStyle(context.getFillColor());
			}
			if(this.context == null || !context.getStrokeColor().equals(this.context.getStrokeColor())){
				canvas.setStrokeStyle(context.getStrokeColor());
			}
			if(this.context == null || context.getStrokeWidth() != this.context.getStrokeWidth()){
				canvas.setLineWidth(context.getStrokeWidth());
			}
			//			if(!createShadows){
			//				if(this.context == null || this.context.getShadowOffsetX() != context.getShadowOffsetX()){
			//					canvas.setShadowOffsetX(context.getShadowOffsetX());
			//				}
			//				if(this.context == null || this.context.getShadowOffsetY() != context.getShadowOffsetY()){
			//					canvas.setShadowOffsetX(context.getShadowOffsetX());
			//				}
			//				if(this.context == null || !this.context.getShadowColor().equals(context.getShadowColor())){
			//					canvas.setShadowColor(context.getShadowColor());
			//				}
			//			}
		}
		this.context = context;
	}
}
