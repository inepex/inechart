package com.inepex.inecharting.chartwidget.graphics.canvas;

import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.inepex.inecharting.chartwidget.IneChartProperties;
import com.inepex.inecharting.chartwidget.model.Axis;
import com.inepex.inecharting.chartwidget.model.ModelManager;
import com.inepex.inecharting.chartwidget.properties.HorizontalAxisDrawingInfo;
import com.inepex.inecharting.chartwidget.properties.VerticalAxisDrawingInfo;

public class DrawingFactoryImplCanvasIE extends DrawingFactoryImplCanvas {

	protected class CanvasIE extends FocusWidget{
		public CanvasIE(CanvasElement ce){
			setElement(ce);
		}
		 /**
		   * Returns the attached Canvas Element.
		   * 
		   * @return the Canvas Element
		   */
		  public CanvasElement getCanvasElement() {
		    return this.getElement().cast();
		  }

		  /**
		   * Gets the rendering context that may be used to draw on this canvas.
		   * 
		   * @param contextId the context id as a String
		   * @return the canvas rendering context
		   */
		  public Context getContext(String contextId) {
		    return getCanvasElement().getContext(contextId);
		  }

		  /**
		   * Returns a 2D rendering context.
		   * 
		   * This is a convenience method, see {@link #getContext(String)}.
		   * 
		   * @return a 2D canvas rendering context
		   */
		  public Context2d getContext2d() {
		    return getCanvasElement().getContext2d();
		  }

		  /**
		   * Gets the height of the internal canvas coordinate space.
		   * 
		   * @return the height, in pixels
		   * @see #setCoordinateSpaceHeight(int)
		   */
		  public int getCoordinateSpaceHeight() {
		    return getCanvasElement().getHeight();
		  }

		  /**
		   * Gets the width of the internal canvas coordinate space.
		   * 
		   * @return the width, in pixels
		   * @see #setCoordinateSpaceWidth(int)
		   */
		  public int getCoordinateSpaceWidth() {
		    return getCanvasElement().getWidth();
		  }

		  /**
		   * Sets the height of the internal canvas coordinate space.
		   * 
		   * @param height the height, in pixels
		   * @see #getCoordinateSpaceHeight()
		   */
		  public void setCoordinateSpaceHeight(int height) {
		    getCanvasElement().setHeight(height);
		  }

		  /**
		   * Sets the width of the internal canvas coordinate space.
		   * 
		   * @param width the width, in pixels
		   * @see #getCoordinateSpaceWidth()
		   */
		  public void setCoordinateSpaceWidth(int width) {
		    getCanvasElement().setWidth(width);
		  }

	}
		
	
	@Override
	public void init(AbsolutePanel chartMainPanel,
			IneChartProperties properties, ModelManager modelManager,
			Axis xAxis, Axis yAxis, Axis y2Axis) {
		this.properties = properties;
		this.chartMainPanel = chartMainPanel;
		this.modelManager = modelManager;

		CanvasElement ce = createExcanvas();
		chartCanvas = new CanvasIE(ce);
		chartCanvas.setPixelSize(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		((CanvasIE) chartCanvas).setCoordinateSpaceHeight(properties.getChartCanvasHeight());
		((CanvasIE) chartCanvas).setCoordinateSpaceWidth(properties.getChartCanvasWidth());
		curveCanvasCtx = ((CanvasIE) chartCanvas).getContext2d();
		curveCanvasCtx.save();
		curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
		curveCanvasCtx.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
		curveCanvasCtx.restore();
		axes = new Axes(curveCanvasCtx, modelManager, properties);
	
		if(xAxis != null){
			ce = createExcanvas();
			xAxisCanvas = new CanvasIE(ce);
			xAxisCanvas.setPixelSize(properties.getChartCanvasWidth(), ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
			((CanvasIE) xAxisCanvas).setCoordinateSpaceHeight( ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
			((CanvasIE) xAxisCanvas).setCoordinateSpaceWidth(properties.getChartCanvasWidth());
			
			axes.addXAxis(xAxis, ((CanvasIE) xAxisCanvas).getContext2d());
		}
		if(y2Axis != null && ((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
			ce = createExcanvas();
			y2AxisCanvas = new CanvasIE(ce);
			y2AxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth(),  properties.getChartCanvasHeight());
			((CanvasIE) y2AxisCanvas).setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
			((CanvasIE) y2AxisCanvas).setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth());
			
			axes.addY2Axis(y2Axis, ((CanvasIE) y2AxisCanvas).getContext2d());
		}
		else
			axes.addY2Axis(y2Axis, null);
		
		if(yAxis != null &&((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
			ce = createExcanvas();
			yAxisCanvas = new CanvasIE(ce);
			yAxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth(), properties.getChartCanvasHeight());
			((CanvasIE) yAxisCanvas).setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
			((CanvasIE) yAxisCanvas).setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth());
		
			axes.addYAxis(yAxis, ((CanvasIE) yAxisCanvas).getContext2d());
		}
		else
			axes.addYAxis(yAxis,null);
		marks = new Marks(curveCanvasCtx, (HorizontalAxisDrawingInfo) xAxis.getDrawingInfo());
		
	}
	
	private native void setCanvasElementForExcanvas(CanvasElement ce) /*-{
		$wnd.G_vmlCanvasManager.initElement(ce);
	}-*/;
    
    
    private native CanvasElement createExcanvas() /*-{
    	var element = document.createElement('canvas');
    	$wnd.G_vmlCanvasManager.initElement(element);
    	return element;
	}-*/;
    
    public final native Context2d getContext2d(CanvasElement canvas) /*-{
	    return canvas.getContext('2d');
	}-*/;


}



