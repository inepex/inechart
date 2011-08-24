package com.inepex.inechart.chartwidget.label;

import java.util.TreeMap;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.TextProperties;
import com.inepex.inegraphics.shared.DrawingArea;

/**
 * 
 * Class for positioning and organizing legends.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class GWTLabelFactory extends LabelFactoryBase{
	AbsolutePanel chartMainPanel;
	FlowPanel mainPanel;
	TreeMap<TextContainer, Widget> textContainerWidgetMap;
	TreeMap<TextContainer, Widget> detachedWidgetMap;
	int basezIndex;
	FlowPanel topPanel;
	FlowPanel botPanel;
	FlowPanel leftPanel;
	FlowPanel rightPanel;
	FlowPanel rightWrapper;
	FlowPanel leftWrapper;
	

	public GWTLabelFactory(DrawingArea canvas, AbsolutePanel chartMainPanel) {
		super(canvas);
		this.chartMainPanel = chartMainPanel;
		textContainerWidgetMap = new TreeMap<TextContainer, Widget>();
		detachedWidgetMap = new TreeMap<TextContainer, Widget>();
		initLayout();
	}

	private void initLayout(){
		mainPanel = new FlowPanel();
		chartMainPanel.add(mainPanel, 0, 0);
		mainPanel.setPixelSize(canvas.getWidth(), canvas.getHeight());
		DOM.setStyleAttribute(mainPanel.getElement(), "position", "relative");
		topPanel = new FlowPanel();
		botPanel = new FlowPanel();
		
		leftPanel = new FlowPanel();
		rightPanel = new FlowPanel();
		leftWrapper = new FlowPanel();
		rightWrapper = new FlowPanel();
		leftWrapper.add(leftPanel);
		rightWrapper.add(rightPanel);
		
		DOM.setStyleAttribute(mainPanel.getElement(), "position", "realtive");
		DOM.setStyleAttribute(topPanel.getElement(), "position", "absolute");
		DOM.setStyleAttribute(topPanel.getElement(), "top", "0px");
		DOM.setStyleAttribute(botPanel.getElement(), "position", "absolute");
		DOM.setStyleAttribute(botPanel.getElement(), "bottom", "0px");
		DOM.setStyleAttribute(topPanel.getElement(), "width", "100%");
		DOM.setStyleAttribute(botPanel.getElement(), "width", "100%");
	
		DOM.setStyleAttribute(leftWrapper.getElement(), "position", "absolute");
		DOM.setStyleAttribute(leftPanel.getElement(), "display", "table-cell");
		DOM.setStyleAttribute(leftPanel.getElement(), "verticalAlign", "middle");
		DOM.setStyleAttribute(rightWrapper.getElement(), "position", "absolute");
		DOM.setStyleAttribute(rightPanel.getElement(), "display", "table-cell");
		DOM.setStyleAttribute(rightPanel.getElement(), "verticalAlign", "middle");
		DOM.setStyleAttribute(leftWrapper.getElement(), "left", "0px");
		DOM.setStyleAttribute(rightWrapper.getElement(), "right", "0px");
		
		//the position and the height must be set later of sidepanels
		mainPanel.add(topPanel);
		mainPanel.add(botPanel);
		mainPanel.add(rightWrapper);
		mainPanel.add(leftWrapper);
	}

	protected void positionTextContainerWidget(TextContainer textContainer, Widget widget){
		if(textContainer.getTop() >= 0 || textContainer.getLeft() >= 0){
			DOM.setStyleAttribute(widget.getElement(), "position", "absolute");
			DOM.setStyleAttribute(widget.getElement(), "left", textContainer.getLeft()+"px");
			DOM.setStyleAttribute(widget.getElement(), "top", textContainer.getTop()+"px");
			mainPanel.add(widget);
		}
		else{
			switch (textContainer.verticalPosition) {
			case Auto:
			case Top:
				topPanel.add(widget);
				break;
			case Bottom:
				botPanel.add(widget);			
				break;
			case Middle:
				if(textContainer.horizontalPosition == HorizontalPosition.Left){
					leftPanel.add(widget);
				}
				else{
					rightPanel.add(widget);
				}
				return;
			}
			switch (textContainer.horizontalPosition) {
			case Auto:
			case Left:
				DOM.setStyleAttribute(widget.getElement(), "cssFloat", "left");
				break;
			case Middle:
				DOM.setStyleAttribute(widget.getElement(), "clear", "both");
				DOM.setStyleAttribute(widget.getElement(), "marginLeft", "auto");
				DOM.setStyleAttribute(widget.getElement(), "marginRight", "auto");
				break;
			case Right:
				DOM.setStyleAttribute(widget.getElement(), "cssFloat", "right");
				break;
			}
		}
	}
	
	@Override
	protected void createChartTitle(){
		if(chartTitle == null){
			return;
		}
		VerticalPanel vp = null;
		if(detachedWidgetMap.containsKey(chartTitle)){
			Widget w = detachedWidgetMap.get(chartTitle);
			if(w != null){
				vp = (VerticalPanel) w;
				vp.clear();
			}
			else{
				detachedWidgetMap.put(chartTitle, vp);
			}
		}
		if(vp == null){
			vp = new VerticalPanel();
		}
		setTextContainerStyleForWidget(chartTitle, vp);
		if(chartTitle.title != null){
			vp.add(createHTMLFromText(chartTitle.title));
		}
		if(chartTitle.description != null){
			vp.add(createHTMLFromText(chartTitle.description));
		}
		if(!detachedWidgetMap.containsKey(chartTitle)){
			textContainerWidgetMap.put(chartTitle, vp);
			positionTextContainerWidget(chartTitle, vp);
		}
	}
	
	@Override
	protected void createLegend(){
		if(legend == null){
			return;
		}
		FlowPanel fp = null;
		if(detachedWidgetMap.containsKey(legend)){
			Widget w = detachedWidgetMap.get(legend);
			if(w != null){
				fp = (FlowPanel) w;
				fp.clear();
			}
			else{
				detachedWidgetMap.put(legend, fp);
			}
		}
		if(fp == null){
			fp = new FlowPanel();
		}	
		for(HasLegendEntries legendEntryOwner : legendOwners){
			if(legendEntryOwner.isDisplayEntries()){
				TreeMap<String, Color> legendEntries = legendEntryOwner.getLegendEntries();
				if(legendEntries == null || legendEntries.size() == 0){
					continue;
				}
				switch(legend.legendEntryLayout){
				case AUTO:
					for(String e : legendEntries.keySet()){
						Widget entryW = createLegendEntry(e, legendEntries.get(e), legend);
						DOM.setStyleAttribute(entryW.getElement(), "cssFloat", "left");
						fp.add(entryW);
					}
					break;
				case ROW:
					HorizontalPanel hp = new HorizontalPanel();
					for(String e : legendEntries.keySet()){
						hp.add(createLegendEntry(e, legendEntries.get(e), legend));
					}
					fp.add(hp);
					break;
				case COLUMN:
					VerticalPanel vp = new VerticalPanel();
					for(String e : legendEntries.keySet()){
						vp.add(createLegendEntry(e, legendEntries.get(e), legend));
					}
					fp.add(vp);
					break;
				}
			}
		}
		setTextContainerStyleForWidget(legend, fp);
		if(!detachedWidgetMap.containsKey(legend)){
			textContainerWidgetMap.put(legend, fp);
			positionTextContainerWidget(legend, fp);
		}
	}
	
	protected HorizontalPanel createLegendEntry(String name, Color color, Legend legend){
		HorizontalPanel hp = new HorizontalPanel();
		Label colorL = new Label();
		colorL.setPixelSize((int)legend.legendSymbol.getWidth(), (int)legend.legendSymbol.getHeight());
		colorL.getElement().getStyle().setBackgroundColor(color.getColor());
		colorL.getElement().getStyle().setMarginRight(legend.paddingBetweenTextAndSymbol, Unit.PX);
		hp.add(colorL);
		HTML text = new HTML(name);
		hp.add(text);
		applyTextProperties(legend.textProperties, text);
		hp.setCellVerticalAlignment(colorL, HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_MIDDLE);
		DOM.setStyleAttribute(hp.getElement(), "margin", legend.paddingBetweenEntries/2+"px");
		return hp;
	}
	
	protected void setTextContainerStyleForWidget(TextContainer textContainer, Widget widget){
		widget.getElement().getStyle().setProperty("filter", "'progid:DXImageTransform.Microsoft.Alpha(Opacity="+textContainer.getBackground().getFillColor().getAlpha()+")'");
		widget.getElement().getStyle().setProperty("opacity", ""+textContainer.getBackground().getFillColor().getAlpha());
		
		widget.getElement().getStyle().setBackgroundColor(textContainer.getBackground().getFillColor().getColor());
		widget.getElement().getStyle().setBorderColor(textContainer.getBackground().getLineProperties().getLineColor().getColor());
		widget.getElement().getStyle().setBorderStyle(textContainer.getBackground().getLineProperties().getDashDistance() > 0 ? BorderStyle.DASHED : BorderStyle.SOLID);
		widget.getElement().getStyle().setBorderWidth(textContainer.getBackground().getLineProperties().getLineWidth(), Unit.PX);
		widget.getElement().getStyle().setPaddingTop(textContainer.getTopPadding(), Unit.PX);
		widget.getElement().getStyle().setPaddingBottom(textContainer.getBottomPadding(), Unit.PX);
		widget.getElement().getStyle().setPaddingLeft(textContainer.getLeftPadding(), Unit.PX);
		widget.getElement().getStyle().setPaddingRight(textContainer.getRightPadding(), Unit.PX);
	}
	
	protected InlineHTML createHTMLFromText(Text text){
		InlineHTML html = new InlineHTML(text.text);
		applyTextProperties(text.textProperties, html);
		html.setWordWrap(false);
		return html;
	}
	
	protected void applyTextProperties(TextProperties tp, Widget widget){
		widget.getElement().getStyle().setFontSize(tp.getFontSize(), Unit.PX);
		widget.getElement().getStyle().setProperty("fontFamily", tp.getFontFamily());
		widget.getElement().getStyle().setProperty("color", tp.getColor().getColor());
		widget.getElement().getStyle().setProperty("fontStyle", tp.getFontStyle());
		widget.getElement().getStyle().setProperty("fontWeight", tp.getFontWeight());
	}
	
	protected void measurePadding(){
		boolean attached = mainPanel.isAttached();
		if(!attached){
			AbsolutePanel ap = new AbsolutePanel();
			ap.setPixelSize(canvas.getWidth(), canvas.getHeight());
			RootPanel.get().add(ap);
			ap.add(mainPanel);
		}
		int top = topPanel.getElement().getOffsetHeight();
		int bottom = botPanel.getElement().getOffsetHeight();
		DOM.setStyleAttribute(leftWrapper.getElement(), "top", top+"px");
		DOM.setStyleAttribute(rightWrapper.getElement(), "top", top+"px");
		DOM.setStyleAttribute(leftPanel.getElement(), "height", canvas.getHeight() - top - bottom + "px");
		DOM.setStyleAttribute(rightPanel.getElement(), "height", canvas.getHeight() - top - bottom + "px");
		int left = leftPanel.getElement().getOffsetWidth();
		int right = rightPanel.getElement().getOffsetWidth();
		if(!attached){
			RootPanel.get().remove(mainPanel.getParent());
			chartMainPanel.add(mainPanel, 0, 0);
		}
		this.paddingNeeded =  new double[]{top,right,bottom,left};
	}

	@Override
	protected void clear() {
		for(Widget w : textContainerWidgetMap.values()){
			w.removeFromParent();
		}
		textContainerWidgetMap.clear();
	}

	@Override
	protected void createStyledLabel(StyledLabel label) {
		if(label == null){
			return;
		}
		Widget labelW = createHTMLFromText(label.getText());
		setTextContainerStyleForWidget(label, labelW);
		
		textContainerWidgetMap.put(label, labelW);
		positionTextContainerWidget(label, labelW);
	}

	@Override
	public void updateStyledLabel(StyledLabel label) {
		if(isFixedPosition(label) && textContainerWidgetMap.containsKey(label)){
			Widget w = textContainerWidgetMap.get(label);
			DOM.setStyleAttribute(w.getElement(), "position", "absolute");
			DOM.setStyleAttribute(w.getElement(), "top", label.top + "px");
			DOM.setStyleAttribute(w.getElement(), "left", label.left + "px");
		}
	}

	public Widget getLegendWidget(){
		Widget w = null;
		if(textContainerWidgetMap.containsKey(legend)){
			w = textContainerWidgetMap.get(legend);
			w.removeFromParent();
			detachedWidgetMap.put(legend, w);
			textContainerWidgetMap.remove(legend);
		}
		else if(detachedWidgetMap.containsKey(legend)){
			w = detachedWidgetMap.get(legend);
		}
		else{
			w = new FlowPanel();
			detachedWidgetMap.put(legend, w);
		}
		return w;
	}
	
	public Widget getChartTitleWidget(){
		Widget w = null;
		if(textContainerWidgetMap.containsKey(chartTitle)){
			w = textContainerWidgetMap.get(chartTitle);
			w.removeFromParent();
			detachedWidgetMap.put(chartTitle, w);
			textContainerWidgetMap.remove(chartTitle);
		}
		else if(detachedWidgetMap.containsKey(chartTitle)){
			w = detachedWidgetMap.get(chartTitle);
		}
		else{
			w = new VerticalPanel(); 
			detachedWidgetMap.put(chartTitle, w);
		}
		return w;
	}

	
	@Override
	protected void removeDisplayedStyledLabel(StyledLabel label) {
		Widget w = textContainerWidgetMap.get(label);
		if(w != null){
			w.removeFromParent();
			textContainerWidgetMap.remove(label);
		}
	}
	
}
