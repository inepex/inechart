package com.inepex.inechart.misc;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CustomScrollBar extends Composite{



	private AbsolutePanel mainPanel = new AbsolutePanel();
	private FocusWidget scrollBarLeftArrow;
	private FocusWidget scrollBarRightArrow;
	private FocusWidget scrollBarSlider;
	private Widget scrollBarSlidingArea;



	//data fields
	private int height;
	private double slidePosition;
	private double slideWidth; 
	private int arrowButtonWidth;
	private int slideAreaWidth;
	private final int SLIDER_MIN_WIDTH = 10;
	public final static int DEFAULT_PIXEL_STEP = 3;
	private final static int SHIFT_DELAY = 70;
	private int pixelStep;
	boolean mouseDown = false;
	boolean mouseResize = false;
	boolean mouseOver = false;
	private int mouseDownOnSlidePosition;
	private int mouseDownAbsolutePosition;
	private Scrollable parent;
	private boolean resizable;


	/**
	 * Creates a scrollbar with the given parameters
	 * The widht of this widget is: 2 * arrowButtonWidth + slideAreaWidth
	 * @param scrollBarLeftArrow a widget for the left button (can be clicked)
	 * @param scrollBarRightArrow a widget for the right button (can be clicked)
	 * @param scrollBarSlider a widget that slides between the arrows
	 * @param scrollBarSlidingArea a widget representing background for slider
	 * @param height height of the scrollbar widget in pixels
	 * @param slidePosition initial position of the slide
	 * @param slideWidth initial width of the slide
	 * @param arrowButtonWidth the width of the arrow (button) widgets in both sides of this widget
	 * @param slideAreaWidth the distance between the two arrow (button) widgets.
	 * @param pixelStep when an arrow button clicked, the slide shifts width this value (in pixels).
	 * @param parent the 'scrollable content'
	 * @param resizable tells whether the slide should be resized when the (right) side of it being dragged
	 */
	public CustomScrollBar(FocusWidget scrollBarLeftArrow,
			FocusWidget scrollBarRightArrow, FocusWidget scrollBarSlider,
			Widget scrollBarSlidingArea, int height, int slidePosition,
			int slideWidth, int arrowButtonWidth, int slideAreaWidth,
			int pixelStep, Scrollable parent, boolean resizable) {
		super();
		this.scrollBarLeftArrow = scrollBarLeftArrow;
		this.scrollBarRightArrow = scrollBarRightArrow;
		this.scrollBarSlider = scrollBarSlider;
		this.scrollBarSlidingArea = scrollBarSlidingArea;
		this.height = height;
		this.resizable = resizable;
		this.arrowButtonWidth = arrowButtonWidth;
		this.slideAreaWidth = slideAreaWidth;
		this.pixelStep = pixelStep;
		this.parent = parent;
		makeLayout(slidePosition, this.slideWidth);
		initEventHandlers();
	}

	public CustomScrollBar(int height, int slidePosition, int slideWidth, int slideAreaWidth,Scrollable parent){
		this(new Button("<"), new Button(">"), new Button(), new Label(),
				height, slidePosition, slideWidth, height, slideAreaWidth, DEFAULT_PIXEL_STEP, parent, false);
	}
	
	private void makeLayout(double slidePos, double slideWidth){
		mainPanel.setPixelSize(slideAreaWidth+2*arrowButtonWidth, height);
		scrollBarLeftArrow.setPixelSize(arrowButtonWidth, height);
		scrollBarSlidingArea.setPixelSize(slideAreaWidth, height);
		scrollBarRightArrow.setPixelSize(arrowButtonWidth, height);
		scrollBarSlider.setPixelSize((int) slideWidth, height);
		mainPanel.add(scrollBarLeftArrow, 0, 0);
		mainPanel.add(scrollBarSlidingArea, arrowButtonWidth, 0);
		mainPanel.add(scrollBarRightArrow, slideAreaWidth+arrowButtonWidth, 0);
		mainPanel.add(scrollBarSlider,(int) (arrowButtonWidth+slidePosition),0);
		DOM.setIntStyleAttribute( scrollBarSlidingArea.getElement(), "zIndex", -1 );
		setSlidePosition(slidePos);
		setSlideWidth(slideWidth);
		this.initWidget(mainPanel) ;
	}


	private void initEventHandlers () {
		final RepeatingCommand shiftLeft = new RepeatingCommand() {

			@Override
			public boolean execute() {
				double pos = getSlidePosition();
				moveSlide((int) (pos-pixelStep));
				if(!mouseOver || !mouseDown || pos-pixelStep  <= 0)
					return false;
				return true;
			}
		};

		final RepeatingCommand shiftRight = new RepeatingCommand() {
			@Override
			public boolean execute() {
				double pos = getSlidePosition();
				moveSlide((int) (pos+pixelStep));
				if(!mouseOver || !mouseDown || pos+pixelStep+slideWidth  >= slideAreaWidth)
					return false;
				return true;
			}
		};
		
		scrollBarSlider.addMouseMoveHandler(new MouseMoveHandler() {
			Timer t = new Timer() {

				@Override
				public void run() {
					parent.scrollBarResized(slidePosition, slideWidth);
				}
			};
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(resizable && mouseDown && mouseResize){
					setSlideWidth(event.getClientX() - scrollBarSlidingArea.getAbsoluteLeft() - slidePosition);	
					t.schedule(500);
				}
				else if(mouseDown){
					moveSlide(event.getClientX()- mouseDownOnSlidePosition - scrollBarSlidingArea.getAbsoluteLeft());
				}
			}
		});

		scrollBarSlider.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				mouseDown = false;
				mouseResize = false;
				DOM.releaseCapture(scrollBarSlider.getElement());
			}
		});

		scrollBarSlider.addMouseDownHandler(new MouseDownHandler() {



			@Override
			public void onMouseDown(MouseDownEvent event) {
				mouseDownOnSlidePosition = event.getRelativeX(scrollBarSlider.getElement());
				if( slideWidth - mouseDownOnSlidePosition < 10){
					mouseResize = true;
				}
				mouseDown = true;
				DOM.setCapture(scrollBarSlider.getElement());


			}
		});

		scrollBarLeftArrow.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				Scheduler.get().scheduleFixedPeriod(shiftLeft, SHIFT_DELAY);
			};
		});

		scrollBarRightArrow.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				Scheduler.get().scheduleFixedPeriod(shiftRight, SHIFT_DELAY);

			}
		});

		scrollBarLeftArrow.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				mouseDown = false;
			}
		});

		scrollBarRightArrow.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				mouseDown = false;
			}
		});

		scrollBarLeftArrow.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				mouseOver = true;
			}
		});

		scrollBarLeftArrow.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				mouseOver = false;
				mouseDown = false;
			}
		});

		scrollBarRightArrow.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				mouseOver = true;
			}
		});

		scrollBarRightArrow.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				mouseOver = false;
				mouseDown = false;
			}
		});

	}

	/**
	 * Moves the slide to the given location.
	 * @param x 0 < x < slideAreaWidth
	 * @return false if doesn't move
	 */
	public boolean setSlidePosition(double x){
		boolean slideMoved = true;
		if(x+slideWidth > slideAreaWidth) 
			x = slideAreaWidth - slideWidth;
		else if(x < 0) 
			x = 0;
		if(x == slidePosition)
			slideMoved = false;
		if(slideMoved) {
			this.slidePosition = x ;
			mainPanel.setWidgetPosition(scrollBarSlider, (int) (x+arrowButtonWidth), 0);
		}	
		return slideMoved;
	}

	/**
	 * Sets the width of the slide
	 * @param width size in pixels
	 */
	public void setSlideWidth(double width){

		width = (width < SLIDER_MIN_WIDTH) 
		? SLIDER_MIN_WIDTH 
				: ( (slidePosition + width > slideAreaWidth) 
						? slideAreaWidth - slidePosition 
								: width ); 

		this.slideWidth = width;
		scrollBarSlider.setPixelSize((int) width, height);
	}

	private void moveSlide(int x){
		if(setSlidePosition(x)){
			try{
				parent.scrollBarMoved(slidePosition);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public double getSlidePosition() {
		return slidePosition;
	}

	public double getSlideWidth() {
		return slideWidth;
	}

	public void unsafeSetSlide(int pos, int width) {
		mainPanel.setWidgetPosition(scrollBarSlider, pos+arrowButtonWidth, 0);
		scrollBarSlider.setPixelSize(width, height);
	}



}
