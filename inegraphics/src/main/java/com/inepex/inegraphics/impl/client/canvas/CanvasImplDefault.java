package com.inepex.inegraphics.impl.client.canvas;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Element;

public class CanvasImplDefault implements Canvas {

	private JavaScriptObject canvas = null;
	private JavaScriptObject context = null;

	@Override
	public native Element createElement() /*-{
		this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas  = $doc.createElement("canvas");
		this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context = (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas).getContext("2d");
		return this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas;
	}-*/;

	@Override
	public native void setWidth(int width) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas).width = width;
	}-*/;

	@Override
	public native int getWidth() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas).width;
	}-*/;

	@Override
	public native void setHeight(int height)/*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas).height = height;
	}-*/;

	@Override
	public native int getHeight() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::canvas).height;
	}-*/;

	@Override
	public native void save() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).save();
	}-*/;
	@Override
	public native void restore() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).restore();
	}-*/;
	@Override
	public native void scale(double x, double y) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).scale(x, y);

	}-*/;
	@Override
	public native void rotate(double angle) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).rotate(angle);

	}-*/;
	@Override
	public native void translate(double x, double y) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).translate(x, y);
	}-*/;
	@Override
	public native void transform(double a, double b, double c, double d, double e,
			double f) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).transform(a, b, c, d, e, f);
	}-*/;
	@Override
	public native void setTransform(double a, double b, double c, double d, double e,
			double f) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).setTransform(a, b, c, d, e, f);
	}-*/;
	@Override
	public native double getGlobalAlpha() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).globalAlpha;
	}-*/;
	@Override
	public native void setGlobalAlpha(double globalAlpha) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).globalAlpha = globalAlpha;
	}-*/;
	@Override
	public native String getGlobalCompositeOperation() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).globalCompositeOperation;
	}-*/;
	@Override
	public native void setGlobalCompositeOperation(String globalCompositeOperation) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).globalCompositeOperation = globalCompositeOperation;
	}-*/;
	@Override
	public native FillStrokeStyle getStrokeStyle() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).strokeStyle;
	}-*/;
	@Override
	public native void setStrokeStyle(String strokeStyle) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).strokeStyle = strokeStyle;
	}-*/;
	@Override
	public native FillStrokeStyle getFillStyle() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).fillStyle;
	}-*/;
	@Override
	public native void setFillStyle(String fillStyle) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).fillStyle = fillStyle;
	}-*/;
	@Override
	public native double getLineWidth() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineWidth;
	}-*/;
	@Override
	public native void setLineWidth(double lineWidth) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineWidth = lineWidth;
	}-*/;
	@Override
	public native String getLineCap() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineCap;
	}-*/;
	@Override
	public native void setLineCap(String lineCap)/*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineCap = lineCap;
	}-*/;
	@Override
	public native void setLineJoin(String lineJoin) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineJoin = lineJoin;
	}-*/;
	@Override
	public native String getLineJoin() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineJoin;
	}-*/;
	@Override
	public native double getMiterLimit() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).miterLimit;
	}-*/;
	@Override
	public native void setMiterLimit(double miterLimit) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).miterLimit = miterLimit;
	}-*/;
	@Override
	public native double getShadowOffsetX() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowOffsetX;
	}-*/;
	@Override
	public native void setShadowOffsetX(double shadowOffsetX) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowOffsetX = shadowOffsetX;
	}-*/;
	@Override
	public native double getShadowOffsetY() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowOffsetY = shadowOffsetY;
	}-*/;
	@Override
	public native void setShadowOffsetY(double shadowOffsetY) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowOffsetY = shadowOffsetY;
	}-*/;
	@Override
	public native double getShadowBlur() /*-{

		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowBlur;
	}-*/;
	@Override
	public native void setShadowBlur(double shadowBlur) /*-{	
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowBlur = shadowBlur;
	}-*/;
	@Override
	public native String getShadowColor() /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowColor;
	}-*/;
	@Override
	public native void setShadowColor(String shadowColor) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).shadowColor = shadowColor;
	}-*/;
	@Override
	public native void clearRect(double x, double y, double w, double h) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).clearRect(x, y, w, h);
	}-*/;
	@Override
	public native void fillRect(double x, double y, double w, double h) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).fillRect(x, y, w, h);
	}-*/;
	@Override
	public native void strokeRect(double x, double y, double w, double h) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).strokeRect(x, y, w, h);
	}-*/;
	@Override
	public native void beginPath() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).beginPath();
	}-*/;
	@Override
	public native void closePath() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).closePath();
	}-*/;
	@Override
	public native void moveTo(double x, double y) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).moveTo(x, y);
	}-*/;
	@Override
	public native void lineTo(double x, double y) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).lineTo(x, y);
	}-*/;
	@Override
	public native void quadraticCurveTo(double cpx, double cpy, double x, double y) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).quadraticCurveTo(cpx, cpy, x, y);
	}-*/;
	@Override
	public native void bezierCurveTo(double cp1x, double cp1y, double cp2x,
			double cp2y, double x, double y) /*-{
				(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).quadraticCurveTo(cp1x, cp1y, cp2x, cp2y,x, y);
	}-*/;
	@Override
	public native void arcTo(double x1, double y1, double x2, double y2, double radius) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).arcTo(x1, y1, x2, y2, radius);
	}-*/;
	@Override
	public native void rect(double x, double y, double w, double h) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).rect(x, y, w, h);
	}-*/;
	@Override
	public native void arc(double x, double y, double radius, double startAngle,
			double endAngle, boolean anticlockwise) /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).arc(x, y, radius, startAngle,endAngle,anticlockwise);
	}-*/;
	@Override
	public native void fill() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).fill();
	}-*/;
	@Override
	public native void stroke() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).stroke();
	}-*/;
	@Override
	public native void clip() /*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).clip();
	}-*/;
	@Override
	public native boolean isPointInPath(double x, double y) /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).isPointInPath(x,y);
	}-*/;

	@Override
	public native void setStrokeStyle(FillStrokeStyle strokeStyle)/*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).strokeStyle = strokeStyle;
	}-*/;

	@Override
	public native void setFillStyle(FillStrokeStyle fillStyle)/*-{
		(this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).fillStyle = fillStyle;
	}-*/;

	@Override
	public native CanvasGradient createLinearGradient(double x0, double y0, double x1,
			double y1) /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).createLinearGradient(x0, y0, x1, y1);
	}-*/;

	@Override
	public native CanvasGradient createRadialGradient(double x0, double y0, double r0,
			double x1, double y1, double r1) /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).createRadialGradient(x0, y0, r0, x1, y1, r1);
	}-*/;


	@Override
	public native CanvasPattern createPattern(ImageElement image, String repetition) /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).createPattern(image, repetition);
	}-*/;

	@Override
	public native CanvasPattern createPattern(CanvasElement image, String repetition) /*-{
		return (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).createPattern(image, repetition);
	}-*/;

	@Override
	public final native void drawImage(CanvasElement image, double dx, double dy) /*-{
	     (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).drawImage(image, dx, dy);
	  }-*/;

	@Override
	public final native void drawImage(CanvasElement image, double dx, double dy, double dw,
			double dh) /*-{
	     (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).drawImage(image, dx, dy, dw, dh);
	  }-*/;

	@Override
	public final native void drawImage(CanvasElement image, double sx, double sy, double sw, double sh,
			double dx, double dy, double dw, double dh) /*-{
	    (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
	  }-*/;
	
	@Override
	public final native void drawImage(ImageElement image, double dx, double dy) /*-{
	     (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).drawImage(image, dx, dy);
	  }-*/;

	@Override
	public final native void drawImage(ImageElement image, double dx, double dy, double dw,
			double dh) /*-{
	     (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).drawImage(image, dx, dy, dw, dh);
	  }-*/;

	@Override
	public final native void drawImage(ImageElement image, double sx, double sy, double sw, double sh,
			double dx, double dy, double dw, double dh) /*-{
	     (this.@com.inepex.inegraphics.impl.client.canvas.CanvasImplDefault::context).drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
	  }-*/;


}
