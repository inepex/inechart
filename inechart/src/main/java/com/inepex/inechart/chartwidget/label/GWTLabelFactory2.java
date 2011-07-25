package com.inepex.inechart.chartwidget.label;

import java.util.List;
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
import com.inepex.inegraphics.shared.DrawingArea;

/**
 * 
 * Class for positioning and organizing legends.
 * 
 * @author Miklós Süveges / Inepex Ltd.
 */
public class GWTLabelFactory2 extends LabelFactoryBase{
	AbsolutePanel chartMainPanel;
	FlowPanel mainPanel;
	TreeMap<TextContainer, Widget> textContainerWidgetMap;
	double[] paddingNeeded;
	int basezIndex;
	FlowPanel topPanel;
	FlowPanel botPanel;
	

	public GWTLabelFactory2(DrawingArea canvas, AbsolutePanel chartMainPanel) {
		super(canvas);
		this.chartMainPanel = chartMainPanel;
		textContainerWidgetMap = new TreeMap<TextContainer, Widget>();
		initLayout();
	}
	
	private void initLayout(){
		mainPanel = new FlowPanel();
		chartMainPanel.add(mainPanel, 0, 0);
		mainPanel.setPixelSize(canvas.getWidth(), canvas.getHeight());
		topPanel = new FlowPanel();
		botPanel = new FlowPanel();
		DOM.setStyleAttribute(mainPanel.getElement(), "position", "realtive");
		DOM.setStyleAttribute(topPanel.getElement(), "position", "absolute");
		DOM.setStyleAttribute(topPanel.getElement(), "top", "0px");
		DOM.setStyleAttribute(botPanel.getElement(), "position", "absolute");
		DOM.setStyleAttribute(botPanel.getElement(), "bottom", "0px");
		DOM.setStyleAttribute(topPanel.getElement(), "width", "100%");
		DOM.setStyleAttribute(botPanel.getElement(), "width", "100%");
		mainPanel.add(topPanel);
		mainPanel.add(botPanel);
	}
	
	@Override
	public void update() {
		for(Widget w : textContainerWidgetMap.values()){
			w.removeFromParent();
		}
		textContainerWidgetMap.clear();
		//first add textContainers which has true value of includeInPadding
		if(chartTitle != null && chartTitle.includeInPadding){
			createChartTitle();
		}
		for(HasLegend legendOwner:legendOwners){
			if(legendOwner.isShowLegend() && legendOwner.getLegend().includeInPadding){
				createLegend(legendOwner);
			}
		}
		measurePadding();
		
		if(chartTitle != null && !chartTitle.includeInPadding){
			createChartTitle();
		}
		for(HasLegend legendOwner:legendOwners){
			if(legendOwner.isShowLegend() && !legendOwner.getLegend().includeInPadding){
				createLegend(legendOwner);
			}
		}
	}
	
	protected void positionTextContainerWidget(TextContainer textContainer, Widget widget){
		switch (textContainer.verticalPosition) {
		case Auto:
		case Middle:
		case Top:
			topPanel.add(widget);
			break;
		case Bottom:
			botPanel.add(widget);			
			break;
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
	
	protected void createChartTitle(){
		if(chartTitle == null){
			return;
		}
		VerticalPanel vp = new VerticalPanel();
		setTextContainerStyleForWidget(chartTitle, vp);
		if(chartTitle.title != null){
			vp.add(createHTMLFromText(chartTitle.title));
		}
		if(chartTitle.description != null){
			vp.add(createHTMLFromText(chartTitle.description));
		}
		textContainerWidgetMap.put(chartTitle, vp);
		positionTextContainerWidget(chartTitle, vp);
	}
	
	protected void createLegend(HasLegend legendOwner){
		if(!legendOwner.isShowLegend() || legendOwner.getLegend() == null){
			return;
		}
		FlowPanel fp = new FlowPanel();		
		List<LegendEntry> entries = legendOwner.getLegendEntries();
		switch(legendOwner.getLegend().legendEntryLayout){
		case AUTO:
			for(LegendEntry e : entries){
				Widget entryW = createLegendEntry(e, legendOwner.getLegend());
				DOM.setStyleAttribute(entryW.getElement(), "cssFloat", "left");
//				DOM.setElementProperty(entryW.getElement(), "float", "left");
				fp.add(entryW);
			}
			break;
		case ROW:
			HorizontalPanel hp = new HorizontalPanel();
			for(LegendEntry e : entries){
				hp.add(createLegendEntry(e, legendOwner.getLegend()));
			}
			fp.add(hp);
			break;
		case COLUMN:
			VerticalPanel vp = new VerticalPanel();
			for(LegendEntry e : entries){
				vp.add(createLegendEntry(e, legendOwner.getLegend()));
			}
			fp.add(vp);
			break;
		}
		setTextContainerStyleForWidget(legendOwner.getLegend(), fp);
		textContainerWidgetMap.put(legendOwner.getLegend(), fp);
		positionTextContainerWidget(legendOwner.getLegend(), fp);
	}
	
	protected HorizontalPanel createLegendEntry(LegendEntry e, Legend legend){
		HorizontalPanel hp = new HorizontalPanel();
		Label color = new Label();
		color.setPixelSize((int)legend.legendSymbol.getWidth(), (int)legend.legendSymbol.getHeight());
		color.getElement().getStyle().setBackgroundColor(e.color.getColor());
		color.getElement().getStyle().setMarginRight(legend.paddingBetweenTextAndSymbol, Unit.PX);
		hp.add(color);
		HTML text = createHTMLFromText(e.text);
		hp.add(text);
		hp.setCellVerticalAlignment(color, HasVerticalAlignment.ALIGN_MIDDLE);
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
		html.getElement().getStyle().setFontSize(text.getTextProperties().getFontSize(), Unit.PX);
		html.getElement().getStyle().setProperty("fontFamily", text.getTextProperties().getFontFamily());
		html.getElement().getStyle().setProperty("color", text.getTextProperties().getColor().getColor());
		html.getElement().getStyle().setProperty("fontStyle", text.getTextProperties().getFontStyle());
		html.getElement().getStyle().setProperty("fontWeight", text.getTextProperties().getFontWeight());
		html.setWordWrap(false);
		return html;
	}
	
	@Override
	public double[] getPaddingNeeded() {
		return paddingNeeded;
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
		
		if(!attached){
			RootPanel.get().remove(mainPanel.getParent());
			chartMainPanel.add(mainPanel, 0, 0);
		}
		this.paddingNeeded =  new double[]{top,0,bottom,0};
	}

	
}
