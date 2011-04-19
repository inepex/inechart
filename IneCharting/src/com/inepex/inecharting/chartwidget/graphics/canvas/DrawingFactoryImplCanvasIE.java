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

		  public CanvasElement getCanvasElement() {
		    return this.getElement().cast();
		  }


		  public Context getContext(String contextId) {
		    return getCanvasElement().getContext(contextId);
		  }


		  public Context2d getContext2d() {
		    return getCanvasElement().getContext2d();
		  }

	
		  public int getCoordinateSpaceHeight() {
		    return getCanvasElement().getHeight();
		  }

		
		  public int getCoordinateSpaceWidth() {
		    return getCanvasElement().getWidth();
		  }

		  public void setCoordinateSpaceHeight(int height) {
		    getCanvasElement().setHeight(height);
		  }

		  public void setCoordinateSpaceWidth(int width) {
		    getCanvasElement().setWidth(width);
		  }

	}
		
//	
//	@Override
//	public void init(AbsolutePanel chartMainPanel,
//			IneChartProperties properties, ModelManager modelManager,
//			Axis xAxis, Axis yAxis, Axis y2Axis) {
//		this.properties = properties;
//		this.chartMainPanel = chartMainPanel;
//		this.modelManager = modelManager;
//
////		CanvasElement ce = createExcanvas();
//		CanvasElement ce = createFlashCanvas();
//		chartCanvas = new CanvasIE(ce);
//		chartCanvas.setPixelSize(properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
//		((CanvasIE) chartCanvas).setCoordinateSpaceHeight(properties.getChartCanvasHeight());
//		((CanvasIE) chartCanvas).setCoordinateSpaceWidth(properties.getChartCanvasWidth());
//		curveCanvasCtx = ((CanvasIE) chartCanvas).getContext2d();
//		curveCanvasCtx.save();
//		curveCanvasCtx.setFillStyle(properties.getChartCanvasBackgroundColor());
//		curveCanvasCtx.fillRect(0, 0, properties.getChartCanvasWidth(), properties.getChartCanvasHeight());
//		curveCanvasCtx.restore();
//		axes = new Axes(curveCanvasCtx, modelManager, properties);
//	
//		if(xAxis != null){
////			ce = createExcanvas();
//			ce = createFlashCanvas();
//			xAxisCanvas = new CanvasIE(ce);
//			xAxisCanvas.setPixelSize(properties.getChartCanvasWidth(), ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
//			((CanvasIE) xAxisCanvas).setCoordinateSpaceHeight( ((HorizontalAxisDrawingInfo)xAxis.getDrawingInfo()).getAxisPanelHeight());
//			((CanvasIE) xAxisCanvas).setCoordinateSpaceWidth(properties.getChartCanvasWidth());
//			
//			axes.addXAxis(xAxis, ((CanvasIE) xAxisCanvas).getContext2d());
//		}
//		if(y2Axis != null && ((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
////			ce = createExcanvas();
//			ce = createFlashCanvas();
//			y2AxisCanvas = new CanvasIE(ce);
//			y2AxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth(),  properties.getChartCanvasHeight());
//			((CanvasIE) y2AxisCanvas).setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
//			((CanvasIE) y2AxisCanvas).setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)y2Axis.getDrawingInfo()).getOffChartCanvasWidth());
//			
//			axes.addY2Axis(y2Axis, ((CanvasIE) y2AxisCanvas).getContext2d());
//		}
//		else
//			axes.addY2Axis(y2Axis, null);
//		
//		if(yAxis != null &&((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth() > 0){
////			ce = createExcanvas();
//			ce = createFlashCanvas();
//			yAxisCanvas = new CanvasIE(ce);
//			yAxisCanvas.setPixelSize(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth(), properties.getChartCanvasHeight());
//			((CanvasIE) yAxisCanvas).setCoordinateSpaceHeight(  properties.getChartCanvasHeight());
//			((CanvasIE) yAxisCanvas).setCoordinateSpaceWidth(((VerticalAxisDrawingInfo)yAxis.getDrawingInfo()).getOffChartCanvasWidth());
//		
//			axes.addYAxis(yAxis, ((CanvasIE) yAxisCanvas).getContext2d());
//		}
//		else
//			axes.addYAxis(yAxis,null);
//		marks = new Marks(curveCanvasCtx, xAxis);
//		
//	}
//	
	private native void setCanvasElementForExcanvas(CanvasElement ce) /*-{
		$wnd.G_vmlCanvasManager.initElement(ce);
	}-*/;
    
    
    private native CanvasElement createExcanvas() /*-{
    	var element = document.createElement('canvas');
    	$wnd.G_vmlCanvasManager.initElement(element);
    	return element;
	}-*/;
    
    private final native Context2d getContext2d(CanvasElement canvas) /*-{
	    return canvas.getContext('2d');
	}-*/;

    private native CanvasElement createFlashCanvas() /*-{
    	var canvas = document.createElement('canvas');
		if (typeof $wnd.FlashCanvas != 'undefined') {
		   $wnd.FlashCanvas.initElement(canvas);
		}
		return canvas;
    }-*/;

}



