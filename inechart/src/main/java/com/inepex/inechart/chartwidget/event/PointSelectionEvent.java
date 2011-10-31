package com.inepex.inechart.chartwidget.event;

import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.DataPoint;

public class PointSelectionEvent extends IneChartEvent<PointSelectionHandler> {
	
	public static final Type<PointSelectionHandler> TYPE = new Type<PointSelectionHandler>();
	
	boolean selected;
	DataPoint point;
	Curve curve;
	
	public PointSelectionEvent() {
		this(false, null, null);
	}
	
	public PointSelectionEvent(boolean selected,
			DataPoint point){
		this(selected, point, null);
	}
	
	public PointSelectionEvent(boolean selected,
			DataPoint point, Curve curve) {
		super(null);
		this.selected = selected;
		this.point = point;
		this.curve = curve;
	}

	@Override
	public Type<PointSelectionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PointSelectionHandler handler) {
		if(selected){
			handler.onSelect(this);
		}
		else{
			handler.onDeselect(this);
		}
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the point
	 */
	public DataPoint getPoint() {
		return point;
	}

	/**
	 * @param point the point to set
	 */
	public void setPoint(DataPoint point) {
		this.point = point;
	}

	/**
	 * @return the curve
	 */
	public Curve getCurve() {
		return curve;
	}

	/**
	 * @param curve the curve to set
	 */
	public void setCurve(Curve curve) {
		this.curve = curve;
	}

}
