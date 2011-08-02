package com.inepex.inechart.misc;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.user.client.ui.Widget;

public class IneScrollPanelFPImpl extends Composite {
	
	private final static int DEFAULT_SCROLL_STEP = 30;
	
	protected class HorizontalScrollBarListener implements Scrollable{
		protected int initialIntervalMin = 0;
		protected int initialIntervalMax;
		protected int intervalStep;

		@Override
		public void scrollBarMoved(double distance) {
			scrollLeft += distance;
			setContentWidgetPosition();
		}

		@Override
		public void scrollBarResized(double position, double width) {

		}

		@Override
		public double getScrollableDomainLength() {
			return contentWidth;
		}

		@Override
		public double getInitialIntervalMin() {
			return initialIntervalMin;
		}

		@Override
		public double getInitialIntervalMax() {
			return initialIntervalMax;
		}

		@Override
		public double getIntervalStep() {
			return intervalStep;
		}
	}

	protected class VerticalScrollBarListener implements Scrollable{
		protected int initialIntervalMin = 0;
		protected int initialIntervalMax;
		protected int intervalStep;

		@Override
		public void scrollBarMoved(double distance) {
			scrollTop += distance;
			setContentWidgetPosition();			
		}

		@Override
		public void scrollBarResized(double position, double width) {

		}

		@Override
		public double getScrollableDomainLength() {
			return contentHeight;
		}

		@Override
		public double getInitialIntervalMin() {
			return initialIntervalMin;
		}

		@Override
		public double getInitialIntervalMax() {
			return initialIntervalMax;
		}

		@Override
		public double getIntervalStep() {
			return intervalStep;
		}
	}

	protected boolean alwaysShowScrollbars = false;
	protected ScrollBarPresenter verticalScrollBarPresenter;
	protected ScrollBarView verticalScrollBarView;
	protected ScrollBarView horizontalScrollBarView;
	protected ScrollBarPresenter horizontalScrollBarPresenter;
	protected HorizontalScrollBarListener horizontalScrollBarListener;
	protected VerticalScrollBarListener verticalScrollBarListener;
	protected ArrayList<HandlerRegistration> handlerRegistrations;
	protected Widget contentWidget;
	
	protected FlowPanel mainPanel;

	//sizes
	protected int height;
	protected int width;
	protected int innerHeight;
	protected int innerWidth;
	protected int contentHeight;
	protected int contentWidth;
	protected int hBarHeight;
	protected int vBarWidth;
	protected double scrollTop;
	protected double scrollLeft;
	
	public IneScrollPanelFPImpl(){
		this(null, false);
	}

	public IneScrollPanelFPImpl(Widget contentPanel, boolean alwaysShowScrollbars) {
		this.alwaysShowScrollbars = alwaysShowScrollbars;
		mainPanel = new FlowPanel();
		DOM.setStyleAttribute(mainPanel.getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(mainPanel.getElement(), "position", "relative");
		mainPanel.addHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateScrollbars();
			}
		}, ResizeEvent.getType());

		initWidget(mainPanel);
		verticalScrollBarListener = new VerticalScrollBarListener();
		horizontalScrollBarListener = new HorizontalScrollBarListener();
		verticalScrollBarView = new ScrollBarView(false);
		mainPanel.add(verticalScrollBarView);
		horizontalScrollBarView = new ScrollBarView(true);
		mainPanel.add(horizontalScrollBarView);
		verticalScrollBarPresenter = new ScrollBarPresenter(verticalScrollBarListener, verticalScrollBarView);
		horizontalScrollBarPresenter = new ScrollBarPresenter(horizontalScrollBarListener, horizontalScrollBarView);
		setScrollStep(DEFAULT_SCROLL_STEP);
	}

	protected void registerHandlers(Widget w){
		handlerRegistrations = new ArrayList<HandlerRegistration>();
		handlerRegistrations.add(w.addHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateScrollbars();
			}
		}, ResizeEvent.getType()));
	}

	protected void removeHandlers(){
		if(handlerRegistrations != null){
			for(HandlerRegistration h : handlerRegistrations){
				h.removeHandler();
			}
		}
	}

	@Override
	public Widget getWidget() {
		return contentWidget;
	}

	public boolean isAlwaysShowScrollbars() {
		return alwaysShowScrollbars;
	}

	public void setAlwaysShowScrollbars(boolean alwaysShowScrollbars) {
		this.alwaysShowScrollbars = alwaysShowScrollbars;
		updateScrollbars();
	}

	@Override
	protected void onLoad() {
		updateScrollbars();
	}
	
	protected void updateScrollbars(){
		if(!isAttached())
			return;
		measureAndSetSizes();
		boolean verticalNeeded = alwaysShowScrollbars || contentHeight > height;
		boolean horizontalNeeded = alwaysShowScrollbars || contentWidth > width;
		
		if(verticalNeeded && !horizontalNeeded && contentWidth > width - vBarWidth){
			horizontalNeeded = true;
		}
		if(horizontalNeeded && !verticalNeeded && contentHeight > height - hBarHeight){
			verticalNeeded = true;
		}
		
		innerHeight = horizontalNeeded ? height - hBarHeight : height;
		innerWidth = verticalNeeded ? width - vBarWidth : width;
		
		if(verticalScrollBarListener.initialIntervalMax == 0)
			verticalScrollBarListener.initialIntervalMax = verticalScrollBarListener.initialIntervalMin + innerHeight;
		if(horizontalScrollBarListener.initialIntervalMax == 0)
			horizontalScrollBarListener.initialIntervalMax = horizontalScrollBarListener.initialIntervalMin + innerWidth;
		
		horizontalScrollBarView.setWidth(innerWidth);
		verticalScrollBarView.setHeight(innerHeight);
		
		DOM.setStyleAttribute(verticalScrollBarView.getElement(), "position", "absolute");
		DOM.setStyleAttribute(verticalScrollBarView.getElement(), "left", width - vBarWidth + "px");
		DOM.setStyleAttribute(verticalScrollBarView.getElement(), "top", "0px");
		DOM.setStyleAttribute(horizontalScrollBarView.getElement(), "position", "absolute");
		DOM.setStyleAttribute(horizontalScrollBarView.getElement(), "left", "0px");
		DOM.setStyleAttribute(horizontalScrollBarView.getElement(), "top", height - hBarHeight + "px");
				
		verticalScrollBarView.setVisible(verticalNeeded);
		horizontalScrollBarView.setVisible(horizontalNeeded);
		verticalScrollBarPresenter.setSlider(scrollTop, innerHeight);
		horizontalScrollBarPresenter.setSlider(scrollLeft, innerWidth);
		
		setContentWidgetPosition();
	}

	public int getScrollTop(){
//		return parseCSSPositionString(contentWidget.getElement().getStyle().getTop());
		return (int) scrollTop;
	}

	public int getScrollLeft(){
//		return parseCSSPositionString(contentWidget.getElement().getStyle().getLeft());
		return (int) scrollLeft;
	}

	protected int parseCSSPositionString(String position){
		return Integer.parseInt(position.substring(0, position.indexOf("px")));
	}

	public void setScrollStep(int scrollStep){
		horizontalScrollBarListener.intervalStep = scrollStep;
		verticalScrollBarListener.intervalStep = scrollStep;
	}
	
	protected void setContentWidgetPosition(){
		DOM.setStyleAttribute(contentWidget.getElement(), "position", "absolute");
		DOM.setStyleAttribute(contentWidget.getElement(), "left", -scrollLeft + "px");
		DOM.setStyleAttribute(contentWidget.getElement(), "top", -scrollTop + "px");
	}

	protected void measureAndSetSizes(){
		if(contentWidget == null)
			return;
		contentHeight = contentWidget.getElement().getScrollHeight();
		contentWidth = contentWidget.getElement().getScrollWidth();
		//FIXME border cannot be calculated before the element is shown
		hBarHeight = horizontalScrollBarView.getOffsetHeight() + 2;
		vBarWidth = verticalScrollBarView.getOffsetWidth() + 2;
		
		width = getElement().getClientWidth();
		height = getElement().getClientHeight();
	}
	
	public Widget getContentWidget() {
		return contentWidget;
	}

	public void setContentWidget(Widget contentWidget) {
		if(this.contentWidget != null){
			removeHandlers();
			mainPanel.remove(this.contentWidget);
		}
		mainPanel.insert(contentWidget, 0);
		this.contentWidget = contentWidget;
		scrollLeft = scrollTop = 0;
		updateScrollbars();
	}

}
