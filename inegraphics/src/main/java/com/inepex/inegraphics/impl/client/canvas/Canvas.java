package com.inepex.inegraphics.impl.client.canvas;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Element;

public interface Canvas {

	Element createElement();

	void setWidth(int width);
	int getWidth();
	void setHeight(int height);
	int getHeight();

	void save(); // push state on state stack
	void restore(); // pop state stack and restore state

	// transformations (default transform is the identity matrix)
	void scale(double x, double y);
	void rotate(double angle);
	void translate(double x, double y);
	void transform(double a, double b, double c, double d, double e, double f);
	void setTransform(double a, double b, double c, double d, double e, double f);

	// compositing
	double getGlobalAlpha(); // (default 1.0)
	void setGlobalAlpha(double globalAlpha);
	String getGlobalCompositeOperation(); // (default source-over)
	void setGlobalCompositeOperation(String globalCompositeOperation);

	// colors and styles
	FillStrokeStyle getStrokeStyle(); // (default black)
	void setStrokeStyle(String strokeStyle);
	void setStrokeStyle(FillStrokeStyle strokeStyle);
	FillStrokeStyle getFillStyle(); // (default black)
	void setFillStyle(String fillStyle);
	void setFillStyle(FillStrokeStyle fillStyle);

	// line caps/joins
	double getLineWidth(); // (default 1)
	void setLineWidth(double lineWidth);
	String getLineCap(); // "butt", "round", "square" (default "butt")
	void setLineCap(String lineCap);
	String getLineJoin(); // "round", "bevel", "miter" (default "miter")
	void setLineJoin(String lineJoin);
	double getMiterLimit(); // (default 10)
	void setMiterLimit(double miterLimit);

	// shadows
	double getShadowOffsetX(); // (default 0)
	void setShadowOffsetX(double shadowOffsetX);
	double getShadowOffsetY(); // (default 0)
	void setShadowOffsetY(double shadowOffsetY);
	double getShadowBlur(); // (default 0)
	void setShadowBlur(double shadowBlur);
	String getShadowColor(); // (default transparent black)
	void setShadowColor(String shadowColor);

	// rects
	void clearRect(double x, double y, double w, double h);
	void fillRect(double x, double y, double w, double h);
	void strokeRect(double x, double y, double w, double h);

	// path API
	void beginPath();
	void closePath();
	void moveTo(double x, double y);
	void lineTo(double x, double y);
	void quadraticCurveTo(double cpx, double cpy, double x, double y);
	void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);
	void arcTo(double x1, double y1, double x2, double y2, double radius);
	void rect(double x, double y, double w, double h);
	void arc(double x, double y, double radius, double startAngle, double endAngle,  boolean anticlockwise);
	void fill();
	void stroke();
	void clip();
	boolean isPointInPath(double x, double y);

	//gradients and patterns
	CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);
	CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);
	CanvasPattern createPattern(ImageElement image, String repetition);
	CanvasPattern createPattern(CanvasElement image, String repetition);
	
	//img
	void drawImage(CanvasElement image, double dx, double dy);
	void drawImage(CanvasElement image, double dx, double dy, double dw,double dh);
	void drawImage(CanvasElement image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);
	void drawImage(ImageElement image, double dx, double dy);
	void drawImage(ImageElement image, double dx, double dy, double dw, double dh);
	void drawImage(ImageElement image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);
}
