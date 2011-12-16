package com.inepex.inechart.chartwidget.event;

import com.inepex.inechart.chartwidget.linechart.Curve;
import com.inepex.inechart.chartwidget.linechart.DataPoint;

public class PointInteractionEvent extends IneChartEvent<PointSelectionHandler> {
	
	public static final Type<PointSelectionHandler> TYPE = new Type<PointSelectionHandler>();
	
	InteractionType interactionType;
	DataPoint point;
	Curve curve;
	
	public PointInteractionEvent() {
		this(InteractionType.Selected, null, null);
	}
	
	public PointInteractionEvent(InteractionType interactionType,
			DataPoint point){
		this(interactionType, point, null);
	}
	
	public PointInteractionEvent(InteractionType interactionType,
			DataPoint point, Curve curve) {
		super(null);
		this.interactionType = interactionType;
		this.point = point;
		this.curve = curve;
	}

	@Override
	public Type<PointSelectionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PointSelectionHandler handler) {
		switch (interactionType) {
		case Deselected:
			handler.onDeselect(this);
			break;
		case Selected:
			handler.onSelect(this);
			break;
		default:
			handler.onTouch(this);
			break;
		}
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

	public InteractionType getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(InteractionType interactionType) {
		this.interactionType = interactionType;
	}

}
