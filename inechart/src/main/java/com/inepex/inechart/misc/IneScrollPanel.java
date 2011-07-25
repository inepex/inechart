package com.inepex.inechart.misc;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.user.client.ui.Widget;

public class IneScrollPanel extends Panel implements HasOneWidget{
	
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

	//sizes
	protected int height;
	protected int width;
	protected int innerHeight;
	protected int innerWidth;
	protected int contentHeight;
	protected int contentWidth;
	protected int hBarHeight;
	protected int vBarWidth;
	protected int scrollTop;
	protected int scrollLeft;

	public IneScrollPanel(){
		this(null, false);
	}

	public IneScrollPanel(Widget contentPanel, boolean alwaysShowScrollbars) {
		this.alwaysShowScrollbars = alwaysShowScrollbars;
		setElement(DOM.createDiv());
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(getElement(), "position", "relative");
		addHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateScrollbars();
			}
		}, ResizeEvent.getType());

		setWidget(contentPanel);
		verticalScrollBarListener = new VerticalScrollBarListener();
		horizontalScrollBarListener = new HorizontalScrollBarListener();
		verticalScrollBarView = new ScrollBarView(false);
		add(verticalScrollBarView);
		horizontalScrollBarView = new ScrollBarView(true);
		add(horizontalScrollBarView);
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
	public void setWidget(Widget w) {
		if(w == contentWidget || w == null)
			return;
		if(contentWidget != null){
			removeHandlers();
			remove(contentWidget);
		}
		add(w);
		contentWidget = w;
	}
	
	@Override
	public void add(Widget child) {
		if(contentWidget != null){
			return;
		}
		// Detach new child.
		child.removeFromParent();
		// Physical attach.
//		DOM.appendChild(getElement(), child.getElement());
		DOM.insertChild(getElement(), child.getElement(), 0);
		// Adopt.
		adopt(child);
	}
	
	@Override
	public void add(IsWidget child) {
		this.add(child.asWidget());
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
		setContentWidgetPosition();
		updateScrollbars();
	}

	
	protected void updateScrollbars(){
		measureAndSetSizes();
		boolean verticalNeeded = false, horizontalNeeded = false;
		if(alwaysShowScrollbars || contentHeight > height){
			verticalNeeded = true;
		}
		if(alwaysShowScrollbars || contentWidth > width){
			horizontalNeeded = true;
		}
		if(verticalNeeded && !horizontalNeeded && contentWidth > width - vBarWidth){
			horizontalNeeded = true;
		}
		if(horizontalNeeded && !verticalNeeded && contentHeight > height - hBarHeight){
			verticalNeeded = true;
		}
		innerHeight =  alwaysShowScrollbars || horizontalNeeded ? height - hBarHeight : height;
		innerWidth = alwaysShowScrollbars || verticalNeeded ? width - vBarWidth : width;
		if(verticalScrollBarListener.initialIntervalMax == 0)
			verticalScrollBarListener.initialIntervalMax = verticalScrollBarListener.initialIntervalMin + innerHeight;
		if(horizontalScrollBarListener.initialIntervalMax == 0)
			horizontalScrollBarListener.initialIntervalMax = horizontalScrollBarListener.initialIntervalMin + innerWidth;
		int vBarHeight = innerHeight;
		int hBarWidth= innerWidth;
		
		horizontalScrollBarView.setWidth(hBarWidth);
		verticalScrollBarView.setHeight(vBarHeight);
		
		DOM.setStyleAttribute(verticalScrollBarView.getElement(), "position", "absolute");
		DOM.setStyleAttribute(verticalScrollBarView.getElement(), "left", width - vBarWidth + "px");
		DOM.setStyleAttribute(verticalScrollBarView.getElement(), "top", 0 + "px");
		DOM.setStyleAttribute(horizontalScrollBarView.getElement(), "position", "absolute");
		DOM.setStyleAttribute(horizontalScrollBarView.getElement(), "left", 0 + "px");
		DOM.setStyleAttribute(horizontalScrollBarView.getElement(), "top", height - hBarHeight + "px");
		
		horizontalScrollBarPresenter.setView(horizontalScrollBarView);
		verticalScrollBarPresenter.setView(verticalScrollBarView);
		
		verticalScrollBarView.setVisible(alwaysShowScrollbars || verticalNeeded);
		horizontalScrollBarView.setVisible(alwaysShowScrollbars || horizontalNeeded);
//		verticalScrollBarPresenter.setEnabled(verticalNeeded);
//		horizontalScrollBarPresenter.setEnabled(horizontalNeeded);
		verticalScrollBarPresenter.setSlider(scrollTop, innerHeight);
		horizontalScrollBarPresenter.setSlider(scrollLeft, innerWidth);
		
	}

	public int getScrollTop(){
		return parseCSSPositionString(contentWidget.getElement().getStyle().getTop());
	}

	public int getScrollLeft(){
		return parseCSSPositionString(contentWidget.getElement().getStyle().getLeft());
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
		DOM.setStyleAttribute(contentWidget.getElement(), "left", scrollLeft + "px");
		DOM.setStyleAttribute(contentWidget.getElement(), "top", scrollTop + "px");
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

	
	@Override
	public void setWidget(IsWidget w) {
		setWidget(w.asWidget());
	}

	@Override
	public Iterator<Widget> iterator() {
		ArrayList<Widget> wC = new ArrayList<Widget>();
		wC.add(contentWidget);
		return wC.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		// Validate.
		if (w.getParent() != this) {
			return false;
		}
		// Orphan.
		try {
			orphan(w);
		} finally {
			// Physical detach.
			com.google.gwt.user.client.Element elem = w.getElement();
			DOM.removeChild(DOM.getParent(elem), elem);

			// Logical detach.
			contentWidget = null;
		}
		return true;
	}
}
