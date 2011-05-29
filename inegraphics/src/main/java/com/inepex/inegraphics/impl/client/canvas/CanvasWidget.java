package com.inepex.inegraphics.impl.client.canvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class CanvasWidget extends Widget {
	
	private Canvas impl = GWT.create(Canvas.class);
	private boolean isUsingExCanvas = false; 
	public CanvasWidget(int width, int height) {
		if(impl instanceof CanvasImplIE)
			isUsingExCanvas = true;
		//force default impl when IE9
		//TODO find better solution for not try using excanvas in ie9!
		if(com.google.gwt.canvas.client.Canvas.isSupported() && isUsingExCanvas)
			impl = new CanvasImplDefault();
		setElement(impl.createElement());
		impl.setWidth(width);
		impl.setHeight(height);
		
	}
	
	public void setWidth(int width) { impl.setWidth(width); }
	public int getWidth() { return impl.getWidth(); }
	public void setHeight(int height) { impl.setHeight(height); }
	public int getHeight() { return impl.getHeight(); }
	
	public void save() { impl.save(); } // push state on state stack
	public void restore() { impl.restore(); } // pop state stack and restore state

	// transformations (default transform is the identity matrix)
	public void scale(double x, double y) { impl.scale(x,y); }
	public void rotate(double angle) { impl.rotate(angle); }
	public void translate(double x, double y) { impl.translate(x,y); }
	public void transform(double a, double b, double c, double d, double e, double f) { impl.transform(a,b,c,d,e,f); }
	public void setTransform(double a, double b, double c, double d, double e, double f) { impl.setTransform(a,b,c,d,e,f); }

	// compositing
	public double getGlobalAlpha() { return impl.getGlobalAlpha(); } // (default 1.0)
	public void setGlobalAlpha(double globalAlpha) { impl.setGlobalAlpha(globalAlpha); }
	public String getGlobalCompositeOperation() { return impl.getGlobalCompositeOperation (); }// (default source-over)
	public void setGlobalCompositeOperation(String globalCompositeOperation) { impl.setGlobalCompositeOperation(globalCompositeOperation); }

	// colors and styles
	public String getStrokeStyle() { return impl.getStrokeStyle();  }// (default black)
	public void setStrokeStyle(String strokeStyle) { impl.setStrokeStyle(strokeStyle); }
	public String getFillStyle() { return impl.getFillStyle (); }// (default black)
	public void setFillStyle(String fillStyle) { impl.setFillStyle(fillStyle); }
	
	// line caps/joins
	public  double getLineWidth() { return impl.getLineWidth (); }// (default 1)
    public void setLineWidth(double lineWidth) { impl.setLineWidth(lineWidth); }
    public String getLineCap() { return impl.getLineCap(); } // "butt", "round", "square" (default "butt")
    public void setLineCap(String lineCap) { impl.setLineCap(lineCap); }
    public  String getLineJoin() { return impl.getLineJoin(); } // "round", "bevel", "miter" (default "miter")
    public void setLineJoin(String lineJoin) { impl.setLineJoin(lineJoin); }
    public  double getMiterLimit() { return impl.getMiterLimit (); }// (default 10)
    public void setMiterLimit(double miterLimit) { impl.setMiterLimit(miterLimit); }

	// shadows
    public double getShadowOffsetX() { return impl.getShadowOffsetX(); } // (default 0)
	public void setShadowOffsetX(double shadowOffsetX) { impl.setShadowOffsetX(shadowOffsetX); }
	public double getShadowOffsetY() { return impl.getShadowOffsetY (); }// (default 0)
	public void setShadowOffsetY(double shadowOffsetY) { impl.setShadowOffsetY(shadowOffsetY); }
	public double getShadowBlur() { return impl.getShadowBlur (); }// (default 0)
	public void setShadowBlur(double shadowBlur) { impl.setShadowBlur(shadowBlur); }
	public String getShadowColor() { return impl.getShadowColor (); }// (default transparent black)
	public void setShadowColor(String shadowColor) { impl.setShadowColor(shadowColor); }
	
	// rects
	public void clearRect(double x, double y, double w, double h) { impl.clearRect(x,y, w,h);}
	public void fillRect(double x, double y, double w, double h) { impl.fillRect(x,y, w,h);}
	public void strokeRect(double x, double y, double w, double h) { impl.strokeRect(x,y, w,h);}

	// path API
	public void beginPath() { impl.beginPath(); }
	public void closePath() { impl.closePath(); }
	public void moveTo(double x, double y) { impl.moveTo(x,y);}
	public void lineTo(double x, double y) { impl.lineTo(x,y);}
	public void quadraticCurveTo(double cpx, double cpy, double x, double y) { impl.quadraticCurveTo(cpx,cpy,x,y); }
	public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) { impl.bezierCurveTo(cp1x,cp1y,cp2x,cp2y,x,y); }
	public void arcTo(double x1, double y1, double x2, double y2, double radius) { impl.arcTo(x1,y1,x2,y2,radius); }
	public void rect(double x, double y, double w, double h) { impl.rect(x,y, w,h);}
	public void arc(double x, double y, double radius, double startAngle, double endAngle,  boolean anticlockwise) { impl.arc(x,y,radius,startAngle,endAngle,anticlockwise); }
	public void fill() { impl.fill(); }
	public void stroke() { impl.stroke(); }
	public void clip() { impl.clip(); }
	public boolean isPointInPath(double x, double y) {return impl.isPointInPath(x,y);}

	public boolean isUsingExCanvas() {
		return isUsingExCanvas;
	}
}
