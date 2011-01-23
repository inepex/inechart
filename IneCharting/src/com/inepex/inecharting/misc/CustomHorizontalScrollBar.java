package com.inepex.inecharting.misc;

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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;


public class CustomHorizontalScrollBar extends Composite {
	//ui
	
	private AbsolutePanel mainPanel = new AbsolutePanel();
	private FocusWidget scrollBarLeftArrow;
	private FocusWidget scrollBarRightArrow;
	private FocusWidget scrollBarSlider;
	private Widget scrollBarSlidingArea;

	
	
	//data fields
	
	private int height;
	private int slidePosition;
	private int slideWidth; 
	private int arrowButtonWidth;
	private int slideAreaWidth;
	private final int SLIDER_MIN_WIDTH = 10;
	public final static int DEFAULT_PIXEL_STEP = 3;
	private final static int SHIFT_DELAY = 70;
	private int pixelStep;
	boolean mouseDown = false;
	boolean mouseOver = false;
	private int mouseDownOnSlidePosition;
	private CustomHorizontalScrollBarParent parent;
	
//	/**
//	 * Creates a scrollbar with the given parameters.
//	 * @param height the height of the FocusWidget in px
//	 * @param slideAreaWidth the width of the scrolling area (between the two arrows at the ends) in px
//	 * @param arrowButtonWidth the arrow buttons width in px
//	 * @param slidePosition	the default location for the slide in px, measured from the left of the scrolling area
//	 * @param slideWidth the default width of the slide
//	 * @param pixelStep when an arrow pressed the slide moves toward the arrow, with this quantity (in pixels)
//	 * @param parent the FocusWidget which uses the scrollbar must implent the {@link CustomHorizontalScrollBarParent} interface
//	 */

	
	public CustomHorizontalScrollBar(FocusWidget scrollBarLeftArrow,
			FocusWidget scrollBarRightArrow, FocusWidget scrollBarSlider,
			Widget scrollBarSlidingArea, int height, int slidePosition,
			int slideWidth, int arrowButtonWidth, int slideAreaWidth,
			int pixelStep, CustomHorizontalScrollBarParent parent) {
		super();
		this.scrollBarLeftArrow = scrollBarLeftArrow;
		this.scrollBarRightArrow = scrollBarRightArrow;
		this.scrollBarSlider = scrollBarSlider;
		this.scrollBarSlidingArea = scrollBarSlidingArea;
		this.height = height;
		
		this.arrowButtonWidth = arrowButtonWidth;
		this.slideAreaWidth = slideAreaWidth;
		this.pixelStep = pixelStep;
		this.parent = parent;
		makeLayout(slidePosition,slideWidth);
		initEventHandlers();
	}
	private void makeLayout(int slidePos, int slideWidth){
		mainPanel.setPixelSize(slideAreaWidth+2*arrowButtonWidth, height);
		scrollBarLeftArrow.setPixelSize(arrowButtonWidth, height);
		scrollBarSlidingArea.setPixelSize(slideAreaWidth, height);
		scrollBarRightArrow.setPixelSize(arrowButtonWidth, height);
		scrollBarSlider.setPixelSize(slideWidth, height);
		mainPanel.add(scrollBarLeftArrow, 0, 0);
		mainPanel.add(scrollBarSlidingArea, arrowButtonWidth, 0);
		mainPanel.add(scrollBarRightArrow, slideAreaWidth+arrowButtonWidth, 0);
		mainPanel.add(scrollBarSlider,arrowButtonWidth+slidePosition,0);
		
		DOM.setIntStyleAttribute( scrollBarSlidingArea.getElement(), "zIndex", -1 );
		
		setSlidePosition(slidePos);
		setSlideWidth(slideWidth);

		this.initWidget(mainPanel) ;
	}


	private void initEventHandlers () {
		final RepeatingCommand shiftLeft = new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				int pos = getSlidePosition();
				moveSlide(pos-pixelStep);
				if(!mouseOver || !mouseDown || pos-pixelStep  <= 0)
					return false;
				return true;
				
			}
		};
	
		final RepeatingCommand shiftRight = new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				int pos = getSlidePosition();
				moveSlide(pos+pixelStep);
				if(!mouseOver || !mouseDown || pos+pixelStep+slideWidth  >= slideAreaWidth)
					return false;
				return true;
			}
		};
		
		
		scrollBarSlider.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				
				if(mouseDown)
					moveSlide(event.getClientX()- mouseDownOnSlidePosition - scrollBarSlidingArea.getAbsoluteLeft());
			}
		});
		
		scrollBarSlider.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mouseDown = false;
				DOM.releaseCapture(scrollBarSlider.getElement());
			}
		});
		
		scrollBarSlider.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				mouseDownOnSlidePosition = event.getRelativeX(scrollBarSlider.getElement());
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
	public boolean setSlidePosition(int x){
		boolean slideMoved = true;
		if(x+slideWidth > slideAreaWidth) 
			x = slideAreaWidth - slideWidth;
		else if(x < 0) 
			x = 0;
		if(x == slidePosition)
			slideMoved = false;
		if(slideMoved) {
			this.slidePosition = x ;
			mainPanel.setWidgetPosition(scrollBarSlider, x+arrowButtonWidth, 0);
		}	
		return slideMoved;
	}
	
	/**
	 * Sets the width of the slide
	 * @param width size in pixels
	 */
	public void setSlideWidth(int width){
	
		width = (width<SLIDER_MIN_WIDTH) ? SLIDER_MIN_WIDTH : ( (slidePosition + width>slideAreaWidth) ? slideAreaWidth - slidePosition : width ); // java is fun like C =)
		this.slideWidth = width;
		scrollBarSlider.setPixelSize(width, height);
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

	public int getSlidePosition() {
		return slidePosition;
	}

	public int getSlideWidth() {
		return slideWidth;
	}

	public void unsafeSetSlide(int pos, int width) {
		mainPanel.setWidgetPosition(scrollBarSlider, pos+arrowButtonWidth, 0);
		scrollBarSlider.setPixelSize(width, height);
	}
	
}
