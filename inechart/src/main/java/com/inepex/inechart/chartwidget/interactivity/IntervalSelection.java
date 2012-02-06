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
import com.inepex.inechart.misc.HorizontalSpinnersWidget;
import com.inepex.inechart.misc.ResizableInterval;
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
	protected List<IneChartModule2D> addressedModules;

	protected double minSpinnerPos;
	protected double maxSpinnerPos;
	protected ShapeProperties selectedLookout;
	protected ShapeProperties unselectedLookout;
	protected Layer layer;

	HorizontalSpinnersWidget spinners;
	ResizableInterval resizableInterval;

	boolean positionOverModule = true;
	
	protected double spinnerHeightRatio = 1.0;

	public IntervalSelection() {
		innerEventHandler = new InnerEventHandler();
		unselectedLookout = Defaults.selectionLookout();
	}

	@Override
	protected void init(){
		layer = createLayer(Layer.ALWAYS_TOP);
		setPositions();
		super.init();
	}
	
	protected void setPositions(){
		if(spinners != null){
			moduleAssist.getChartMainPanel().remove(spinners);
		}
		minSpinnerPos = 0;
		maxSpinnerPos = relatedIneChartModule2D.getXAxis().getMax() - relatedIneChartModule2D.getXAxis().getMin();
		spinners = new HorizontalSpinnersWidget(relatedIneChartModule2D.getWidth(), false, relatedIneChartModule2D.getHeight(),
				new ResizableInterval() {

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
				ViewportChangeEvent event = new ViewportChangeEvent(minSpinnerPos + relatedIneChartModule2D.getXAxis().getMin() , maxSpinnerPos + relatedIneChartModule2D.getXAxis().getMin(), true);
				event.setAddressedCharts(addressedCharts);
				event.setAddressedModules(addressedModules);
				moduleAssist.getEventManager().fireViewportChangedEvent(event);
			}

			@Override
			public void dragStart() {

			}
		});
		spinners.setHeight((int) (this.relatedIneChartModule2D.getHeight() * spinnerHeightRatio));
		moduleAssist.getChartMainPanel().add(spinners, relatedIneChartModule2D.getLeftPadding() - spinners.getSpinnerWidgetWidth() / 2, 
//				relatedIneChartModule2D.getBottomEnd()
				relatedIneChartModule2D.getTopPadding() - ((spinners.getHeight() - relatedIneChartModule2D.getHeight()) / 2)
				);
	}

	protected void drawRectangles(){
		layer.getCanvas().removeAllGraphicalObjects();
		double leftSpinnerX = relatedIneChartModule2D.getCanvasX(minSpinnerPos + relatedIneChartModule2D.getXAxis().getMin());
		double rightSpinnerX = relatedIneChartModule2D.getCanvasX(maxSpinnerPos + relatedIneChartModule2D.getXAxis().getMin());
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
		spinners.setHeight((int) (this.relatedIneChartModule2D.getHeight() * spinnerHeightRatio));
		moduleAssist.getChartMainPanel().setWidgetPosition(spinners, relatedIneChartModule2D.getLeftPadding() - spinners.getSpinnerWidgetWidth() / 2, 
//				relatedIneChartModule2D.getBottomEnd()
				relatedIneChartModule2D.getTopPadding() - ((spinners.getHeight() - relatedIneChartModule2D.getHeight()) / 2)
				);
		minSpinnerPos = 0;
		maxSpinnerPos = relatedIneChartModule2D.getXAxis().getMax() - relatedIneChartModule2D.getXAxis().getMin();
		spinners.setWidth(relatedIneChartModule2D.getWidth(), false);
		spinners.setInterval(minSpinnerPos, maxSpinnerPos);
		drawRectangles();
	}

	@Override
	public List<IneChart> getAddressedCharts() {
		if(addressedCharts == null){
			addressedCharts = new ArrayList<IneChart>();
		}
		return addressedCharts;
	}

	@Override
	public void setAddressedCharts(List<IneChart> addressedCharts) {
		this.addressedCharts = addressedCharts;
	}

	@Override
	public List<IneChartModule2D> getAddressedModules() {
		if(addressedModules == null){
			addressedModules = new ArrayList<IneChartModule2D>();
		}
		return addressedModules;
	}

	@Override
	public void setAddressedModules(List<IneChartModule2D> addressedModules) {
		this.addressedModules = addressedModules;
	}

	@Override
	public void addAddressedModule(IneChartModule2D addressedModule) {
		if(addressedModules == null){
			addressedModules = new ArrayList<IneChartModule2D>();
		}
		addressedModules.add(addressedModule);
	}

	@Override
	public void preUpdate() {
		if(!positionOverModule){
			relatedIneChartModule2D.setMinBottomPadding(Math.max(relatedIneChartModule2D.getMinBottomPadding(),spinners.getHeight()));
		}
		relatedIneChartModule2D.setMinLeftPadding(Math.max(relatedIneChartModule2D.getMinLeftPadding(),spinners.getSpinnerWidgetWidth() / 2));
		relatedIneChartModule2D.setMinRightPadding(Math.max(relatedIneChartModule2D.getMinRightPadding(),spinners.getSpinnerWidgetWidth() / 2));
	}

	public ShapeProperties getSelectedLookout() {
		return selectedLookout;
	}

	public void setSelectedLookout(ShapeProperties selectedLookout) {
		this.selectedLookout = selectedLookout;
	}

	public ShapeProperties getUnselectedLookout() {
		return unselectedLookout;
	}

	public void setUnselectedLookout(ShapeProperties unselectedLookout) {
		this.unselectedLookout = unselectedLookout;
	}
	
	public void setSpinnerHeightRatio(double spinnerHeightRatio) {
		this.spinnerHeightRatio = spinnerHeightRatio;
		setPositions();
	}
	
	public double getSpinnerHeightRatio() {
		return spinnerHeightRatio;
	}

}

