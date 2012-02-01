package com.inepex.inechart.chartwidget.label.gwt;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.chartwidget.ModuleAssist;
import com.inepex.inechart.chartwidget.label.BubbleBox;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.misc.Position;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inechart.chartwidget.properties.TextProperties;

public class WidgetFactory {
	
	protected ModuleAssist moduleAssist;

	public WidgetFactory(ModuleAssist moduleAssist){
		this.moduleAssist = moduleAssist;
	}
	
	protected void applyTextProperties(TextProperties tp, Widget widget){
		widget.getElement().getStyle().setFontSize(tp.getFontSize(), Unit.PX);
		widget.getElement().getStyle().setProperty("fontFamily", tp.getFontFamily());
		widget.getElement().getStyle().setProperty("color", tp.getColor().getColor());
		widget.getElement().getStyle().setProperty("fontStyle", tp.getFontStyle());
		widget.getElement().getStyle().setProperty("fontWeight", tp.getFontWeight());
	}
	
	protected InlineHTML createHTMLFromText(Text text){
		InlineHTML html = new InlineHTML(text.getText());
		applyTextProperties(text.getTextProperties(), html);
		html.setWordWrap(false);
		return html;
	}
	
	public int[] measureStyledLabel(StyledLabel label){
		com.inepex.inegraphics.shared.gobjects.Text text = new com.inepex.inegraphics.shared.gobjects.Text(label.getText().getText(), 1, 1);
		text.setFontFamily(label.getText().getTextProperties().getFontFamily());
		text.setFontStyle(label.getText().getTextProperties().getFontStyle());
		text.setFontWeight(label.getText().getTextProperties().getFontWeight());
		text.setColor(label.getText().getTextProperties().getColor().getColor());
		text.setFontSize(label.getText().getTextProperties().getFontSize());
		moduleAssist.getMainCanvas().measureText(text);
		return new int[]{text.getWidth() + label.getLeftPadding() + label.getRightPadding(), text.getHeight() + label.getBottomPadding() + label.getTopPadding()};
	}

	
	protected void setTextContainerStyleForWidget(TextContainer textContainer, Widget widget){
		widget.getElement().getStyle().setProperty("filter", "'progid:DXImageTransform.Microsoft.Alpha(Opacity="+textContainer.getBackground().getFillColor().getAlpha()+")'");
		widget.getElement().getStyle().setProperty("opacity", ""+textContainer.getBackground().getFillColor().getAlpha());

		widget.getElement().getStyle().setBackgroundColor(textContainer.getBackground().getFillColor().getColor());
		widget.getElement().getStyle().setBorderColor(textContainer.getBackground().getLineProperties().getLineColor().getColor());
		if(textContainer.getRoundedCornerRadius() > 0){
			widget.getElement().getStyle().setProperty("borderRadius", textContainer.getRoundedCornerRadius()+"px");
		}
		widget.getElement().getStyle().setBorderStyle(textContainer.getBackground().getLineProperties().getDashDistance() > 0 ? BorderStyle.DASHED : BorderStyle.SOLID);
		widget.getElement().getStyle().setBorderWidth(textContainer.getBackground().getLineProperties().getLineWidth(), Unit.PX);
		widget.getElement().getStyle().setPaddingTop(textContainer.getTopPadding(), Unit.PX);
		widget.getElement().getStyle().setPaddingBottom(textContainer.getBottomPadding(), Unit.PX);
		widget.getElement().getStyle().setPaddingLeft(textContainer.getLeftPadding(), Unit.PX);
		widget.getElement().getStyle().setPaddingRight(textContainer.getRightPadding(), Unit.PX);
//		DOM.setStyleAttribute(widget.getElement(), "zIndex", ""+zIndexStart);
	}

	/**
	 * Creates a Widget representing the given {@link BubbleBox},
	 * if the {@link BubbleBox#isAutoFit()} is true the Box will be positioned inside the given rectangle,
	 * if false the other parameters will be ignored.
	 * @param bb {@link BubbleBox} to create
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public Widget createBubbleBoxWidget(BubbleBox bb, int x, int y, int width, int height){
		AbsolutePanel panel = new AbsolutePanel();
		HTML bubble = createHTMLFromText(bb.getText());
		setTextContainerStyleForWidget(bb, bubble);
		int tailX = 0, tailY = 0;
		final int tailDiffFromOrig = (int) ((Math.sqrt(2) * bb.getTailSize() - bb.getTailSize()) / 2);
		final int tailSize = (int) (Math.sqrt(2) / 2 * bb.getTailSize());
		int[] labelDimensions = measureStyledLabel(bb);
		int sumH = labelDimensions[1];
		if(bb.isDisplayTail() && (bb.getTailPosition() == Position.Bottom || bb.getTailPosition() == Position.Top)){
			sumH += tailSize;
		}
		int sumW = labelDimensions[0];
		if(bb.isDisplayTail() && (bb.getTailPosition() == Position.Left || bb.getTailPosition() == Position.Right)){
			sumW += tailSize;
		}
		if(bb.isAutoFit()){
			switch(bb.getTailPosition()){
			//first we set position to opposite if it is not fit in the rect
			case Bottom:
				if(bb.getTop() - sumH < y){
					bb.setTailPosition(Position.Top);
				}
				break;
			case Top:
				if(bb.getTop() + sumH > y + height){
					bb.setTailPosition(Position.Bottom);
				}
				break;
			case Left:
				if(bb.getLeft() + sumW > x + width){
					bb.setTailPosition(Position.Right);
				}
				break;
			case Right:
				if(bb.getLeft() - sumW < x){
					bb.setTailPosition(Position.Left);
				}
				break;
			}
			//now we if we have the tail above or below the label we must check the if the box fits horizontally
			if(bb.getTailPosition() == Position.Bottom || bb.getTailPosition() == Position.Top){
				int leftFromLabel = getTailX(bb, labelDimensions, tailDiffFromOrig, tailSize) + tailDiffFromOrig + tailSize / 2;
				if(bb.getLeft() - leftFromLabel  < x){
					bb.setTailHorizontalPosition(HorizontalPosition.Left);
				}
				if(bb.getLeft() + labelDimensions[0] - leftFromLabel > x + width){
					bb.setTailHorizontalPosition(HorizontalPosition.Right);
				}
			}
			else{
				int topFromLabel = getTailY(bb, labelDimensions, tailDiffFromOrig, tailSize) + tailDiffFromOrig + tailSize / 2;
				if(bb.getTop() - topFromLabel < y){
					bb.setTailVerticalPosition(VerticalPosition.Top);
				}
				if(bb.getTop() + labelDimensions[1] - topFromLabel > y + height){
					bb.setTailVerticalPosition(VerticalPosition.Bottom);
				}
			}
		}
	
		int left = bb.getLeft(), top = bb.getTop();
		tailX = getTailX(bb, labelDimensions, tailDiffFromOrig, tailSize);
		tailY = getTailY(bb, labelDimensions, tailDiffFromOrig, tailSize);
		int leftFromLabel = tailX + tailDiffFromOrig + tailSize / 2;
		int topFromLabel = tailY + tailDiffFromOrig + tailSize / 2;
		int bubbleX = getBubbleX(bb, tailSize);
		int bubbleY = getBubbleY(bb, tailSize);
		switch(bb.getTailPosition()){
		case Bottom:
			top -= bb.getDistanceFromPoint() + sumH;
			left -= leftFromLabel; 
			break;
		case Left:
			top -= topFromLabel;
			left += bb.getDistanceFromPoint(); 
			break;
		case Right:
			top -= topFromLabel;
			left -= bb.getDistanceFromPoint() + sumW;
			break;
		case Top:
			top += bb.getDistanceFromPoint();
			left -= leftFromLabel; 
			break;
		}
		if(bb.isDisplayTail()){
			FlowPanel tailHidingPanel = new FlowPanel();
			tailHidingPanel.setPixelSize(bb.getTailSize(), bb.getTailSize());
			tailHidingPanel.getElement().getStyle().setProperty("transform", "rotate(-45deg)");
			tailHidingPanel.getElement().getStyle().setProperty("MozTransform", "rotate(-45deg)");
			tailHidingPanel.getElement().getStyle().setProperty("webkitTransform", "rotate(-45deg)");
			tailHidingPanel.getElement().getStyle().setProperty("MSTransform", "rotate(-45deg)");
			tailHidingPanel.getElement().getStyle().setProperty("opacity", "1");
			tailHidingPanel.getElement().getStyle().setBackgroundColor("white");
			tailHidingPanel.getElement().getStyle().setBorderColor("white");
			tailHidingPanel.getElement().getStyle().setBorderWidth(bb.getBackground().getLineProperties().getLineWidth(), Unit.PX);
			panel.add(tailHidingPanel, tailX, tailY);
			FlowPanel tail = new FlowPanel();
			tail.setPixelSize(bb.getTailSize(), bb.getTailSize());
			tail.getElement().getStyle().setProperty("transform", "rotate(-45deg)");
			tail.getElement().getStyle().setProperty("MozTransform", "rotate(-45deg)");
			tail.getElement().getStyle().setProperty("webkitTransform", "rotate(-45deg)");
			tail.getElement().getStyle().setProperty("MSTransform", "rotate(-45deg)");
			tail.getElement().getStyle().setProperty("filter", "'progid:DXImageTransform.Microsoft.Alpha(Opacity="+bb.getBackground().getFillColor().getAlpha()+")'");
			tail.getElement().getStyle().setProperty("opacity", ""+bb.getBackground().getFillColor().getAlpha());
			tail.getElement().getStyle().setBackgroundColor(bb.getBackground().getFillColor().getColor());
			tail.getElement().getStyle().setBorderColor(bb.getBackground().getLineProperties().getLineColor().getColor());
			tail.getElement().getStyle().setBorderStyle(bb.getBackground().getLineProperties().getDashDistance() > 0 ? BorderStyle.DASHED : BorderStyle.SOLID);
			tail.getElement().getStyle().setBorderWidth(bb.getBackground().getLineProperties().getLineWidth(), Unit.PX);
			panel.add(tail, tailX, tailY);
		}
		FlowPanel hidingPanel = new FlowPanel();
		hidingPanel.setPixelSize(labelDimensions[0], labelDimensions[1]);
		hidingPanel.getElement().getStyle().setProperty("opacity", "1");
		hidingPanel.getElement().getStyle().setBackgroundColor("white");
		hidingPanel.getElement().getStyle().setBorderColor("white");
		if(bb.getRoundedCornerRadius() > 0){
			hidingPanel.getElement().getStyle().setProperty("borderRadius", bb.getRoundedCornerRadius() + "px");
		}
		hidingPanel.getElement().getStyle().setBorderStyle(bb.getBackground().getLineProperties().getDashDistance() > 0 ? BorderStyle.DASHED : BorderStyle.SOLID);
		hidingPanel.getElement().getStyle().setBorderWidth(bb.getBackground().getLineProperties().getLineWidth(), Unit.PX);
		panel.add(hidingPanel, bubbleX, bubbleY);
		panel.add(bubble, bubbleX, bubbleY);
		panel.getElement().getStyle().setProperty("opacity", "1");
		DOM.setStyleAttribute(panel.getElement(), "overflow", "visible");
		DOM.setStyleAttribute(panel.getElement(), "position", "absolute");
		DOM.setStyleAttribute(panel.getElement(), "left", left +"px");
		DOM.setStyleAttribute(panel.getElement(), "top", top +"px");
		DOM.setStyleAttribute(panel.getElement(), "zIndex", ""+(100));	
		return panel;

	}
	
	private int getBubbleX(BubbleBox bb, int tailSize){
		int bubbleX = 0;
		if(bb.isDisplayTail() && bb.getTailPosition() == Position.Left){
			bubbleX += tailSize;
		}
		return bubbleX;
	}
	
	private int getBubbleY(BubbleBox bb, int tailSize){
		int bubbleY = 0;
		if(bb.isDisplayTail() && bb.getTailPosition() == Position.Top){
			bubbleY += tailSize;
		}
		return bubbleY;
	}
	
	private int getTailX(BubbleBox bb, int[] labelDimensions, int tailDiffFromOrig, int tailSize){
		int tailX = 0;
		if(bb.getTailPosition() == Position.Bottom || bb.getTailPosition() == Position.Top){
			switch (bb.getTailHorizontalPosition()) {
			case Auto:
			case Middle:
				tailX = labelDimensions[0] / 2 - tailSize;
				break;
			case Left:
				tailX = bb.getRoundedCornerRadius() ;
				break;
			case Right:
				tailX = labelDimensions[0] - bb.getRoundedCornerRadius() - tailSize * 2;
				break;
			}
		}
		else if(bb.getTailPosition() == Position.Right){
				tailX = labelDimensions[0] - tailSize;
		}
		return tailX + tailDiffFromOrig;
	}
	
	private int getTailY(BubbleBox bb, int[] labelDimensions, int tailDiffFromOrig, int tailSize){
		int tailY = 0;
		if(bb.getTailPosition() == Position.Left || bb.getTailPosition() == Position.Right){
			tailY -= tailDiffFromOrig * 1.5; //hack
			switch (bb.getTailVerticalPosition()) {
			case Auto:
			case Middle:
				tailY += labelDimensions[1] / 2 - tailSize / 2;
				break;
			case Bottom:
				tailY += labelDimensions[1] - bb.getRoundedCornerRadius() - tailSize * 2;
				break;
			case Top:
				tailY += bb.getRoundedCornerRadius();
				break;
			}
		}
		else if(bb.getTailPosition() == Position.Bottom){
			tailY = labelDimensions[1] - tailSize;
		}
		return tailY + tailDiffFromOrig;
	}

}
