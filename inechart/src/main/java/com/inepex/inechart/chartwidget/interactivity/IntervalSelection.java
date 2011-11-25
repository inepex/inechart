package com.inepex.inechart.chartwidget.interactivity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.Layer;
import com.inepex.inechart.chartwidget.event.FiresViewportChangeEvent;
import com.inepex.inechart.chartwidget.event.ViewportChangeEvent;
import com.inepex.inechart.chartwidget.properties.ShapeProperties;
import com.inepex.inechart.chartwidget.shape.Rectangle;
import com.inepex.inechart.misc.IntervalSelectionWidget;
import com.inepex.inechart.misc.ResizableInterval;
import com.inepex.inechart.misc.SpinnerPresenter;
import com.inepex.inechart.misc.SpinnerView;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;


/**
 * 
 * Horizontal range selection with spinners
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class IntervalSelection extends AbstractInteractiveModule implements FiresViewportChangeEvent{
	
	protected List<IneChart> addressedCharts;
	protected List<IneChartModule2D> addressedModuls;

	protected double minSpinnerPos;
	protected double maxSpinnerPos;
	protected InnerEventHandler innerEventHandler;
	protected SpinnerPresenter spinner1Presenter;
	protected SpinnerPresenter spinner2Presenter;
	protected SpinnerView spinner1View;
	protected SpinnerView spinner2View;
	protected ShapeProperties selectedLookout;
	protected ShapeProperties unselectedLookout;
	protected Layer layer;

	IntervalSelectionWidget spinners;
	ResizableInterval resizableInterval;


	public IntervalSelection() {
		innerEventHandler = new InnerEventHandler();
		unselectedLookout = Defaults.selectionLookout();
	}

	@Override
	protected void init(){
		layer = createLayer(Layer.ALWAYS_TOP);
					
		if(spinners != null){
			moduleAssist.getChartMainPanel().remove(spinners);
		}
		minSpinnerPos = relatedIneChartModule2D.getXAxis().getMin();
		maxSpinnerPos = relatedIneChartModule2D.getXAxis().getMax();
		spinners = new IntervalSelectionWidget(relatedIneChartModule2D.getWidth(), false, true, new ResizableInterval() {

			@Override
			public void intervalSet(double min, double max) {
				minSpinnerPos = min;
				maxSpinnerPos = max;
				drawRectangles();
			}

			@Override
			public double getMaximumSize() {
				return relatedIneChartModule2D.getXAxis().getMax() - relatedIneChartModule2D.getXAxis().getMin();
			}

			@Override
			public double getInitialMin() {
				return minSpinnerPos;
			}

			@Override
			public double getInitialMax() {
				return maxSpinnerPos;
			}

			@Override
			public void dragEnd() {
				ViewportChangeEvent event = new ViewportChangeEvent(minSpinnerPos, maxSpinnerPos, true);
				event.setAddressedCharts(addressedCharts);
				event.setAddressedModules(addressedModuls);
				moduleAssist.getEventManager().fireViewportChangedEvent(event);
			}

			@Override
			public void dragStart() {
				
			}
		});
		moduleAssist.getChartMainPanel().add(spinners, relatedIneChartModule2D.getLeftPadding() - spinners.getSpinnerWidgetWidth() / 2, relatedIneChartModule2D.getBottomEnd());
		super.init();
	}

	protected void drawRectangles(){
		layer.getCanvas().removeAllGraphicalObjects();
		double leftSpinnerX = relatedIneChartModule2D.getCanvasX(minSpinnerPos);
		double rightSpinnerX = relatedIneChartModule2D.getCanvasX(maxSpinnerPos);
		if(unselectedLookout != null){
			Rectangle rectangle = new Rectangle(leftSpinnerX - relatedIneChartModule2D.getLeftPadding(), relatedIneChartModule2D.getHeight(), unselectedLookout);
			for(GraphicalObject go : rectangle.toGraphicalObjects()){
				go.setBasePointX(relatedIneChartModule2D.getLeftPadding());
				go.setBasePointY(relatedIneChartModule2D.getTopPadding());
				layer.getCanvas().addGraphicalObject(go);
			}
			rectangle = new Rectangle(relatedIneChartModule2D.getRightEnd() - rightSpinnerX, relatedIneChartModule2D.getHeight(), unselectedLookout);
			for(GraphicalObject go : rectangle.toGraphicalObjects()){
				go.setBasePointX(rightSpinnerX);
				go.setBasePointY(relatedIneChartModule2D.getTopPadding());
				layer.getCanvas().addGraphicalObject(go);
			}
		}
		if(selectedLookout != null){
			Rectangle rectangle = new Rectangle(rightSpinnerX - leftSpinnerX, relatedIneChartModule2D.getHeight(), selectedLookout);
			for(GraphicalObject go : rectangle.toGraphicalObjects()){
				go.setBasePointX(leftSpinnerX);
				go.setBasePointY(relatedIneChartModule2D.getTopPadding());
				layer.getCanvas().addGraphicalObject(go);
			}
		}
		layer.getCanvas().update();
	}

	@Override
	protected void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseDown(MouseDownEvent event) {
		//		spinner1View.onBrowserEvent(event.get);
	}

	@Override
	protected void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		moduleAssist.getChartMainPanel().setWidgetPosition(spinners, 
				relatedIneChartModule2D.getLeftPadding() - spinners.getSpinnerWidgetWidth() / 2, 
				relatedIneChartModule2D.getBottomEnd());
		spinners.setWidth(relatedIneChartModule2D.getWidth(), false);
		spinners.setInterval(minSpinnerPos - relatedIneChartModule2D.getXAxis().getMin(), maxSpinnerPos - relatedIneChartModule2D.getXAxis().getMin());
		drawRectangles();
	}


	@Override
	public List<IneChart> getAddressedCharts() {
		return addressedCharts;
	}

	@Override
	public void setAddressedCharts(List<IneChart> addressedCharts) {
		this.addressedCharts = addressedCharts;
	}

	@Override
	public List<IneChartModule2D> getAddressedModules() {
		return addressedModuls;
	}

	@Override
	public void setAddressedModules(List<IneChartModule2D> addressedModuls) {
		this.addressedModuls = addressedModuls;
	}

	@Override
	public void addAddressedModule(IneChartModule2D addressedModule) {
		if(addressedModuls == null){
			addressedModuls = new ArrayList<IneChartModule2D>();
		}
		addressedModuls.add(addressedModule);
	}

	@Override
	public void preUpdate() {
		minSpinnerPos = relatedIneChartModule2D.getXAxis().getMin();
		maxSpinnerPos = relatedIneChartModule2D.getXAxis().getMax();
		relatedIneChartModule2D.setMinBottomPadding(spinners.getTotalHeight());
		relatedIneChartModule2D.setMinLeftPadding(spinners.getSpinnerWidgetWidth() / 2);
		relatedIneChartModule2D.setMinRightPadding(spinners.getSpinnerWidgetWidth() / 2);
	}

}

