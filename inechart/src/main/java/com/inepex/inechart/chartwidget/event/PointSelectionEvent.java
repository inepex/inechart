package com.inepex.inechart.chartwidget.event;

import com.inepex.inechart.chartwidget.linechart.Curve2;
import com.inepex.inechart.chartwidget.linechart.DataPoint2;

public class PointSelectionEvent extends IneChartEvent<PointSelectionHandler> {
	
	public static final Type<PointSelectionHandler> TYPE = new Type<PointSelectionHandler>();
	
	boolean selected;
	DataPoint2 point;
	Curve2 curve;
	
	public PointSelectionEvent() {
		this(false, null, null);
	}
	
	public PointSelectionEvent(boolean selected,
			DataPoint2 point){
		this(selected, point, null);
	}
	
	public PointSelectionEvent(boolean selected,
			DataPoint2 point, Curve2 curve) {
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
	public DataPoint2 getPoint() {
		return point;
	}

	/**
	 * @param point the point to set
	 */
	public void setPoint(DataPoint2 point) {
		this.point = point;
	}

	/**
	 * @return the curve
	 */
	public Curve2 getCurve() {
		return curve;
	}

	/**
	 * @param curve the curve to set
	 */
	public void setCurve(Curve2 curve) {
		this.curve = curve;
	}

}
