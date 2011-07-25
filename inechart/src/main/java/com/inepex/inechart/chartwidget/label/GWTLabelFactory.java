package com.inepex.inechart.chartwidget.label;


import java.util.List;
import java.util.TreeMap;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
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
public class GWTLabelFactory extends LabelFactoryBase{
	AbsolutePanel chartMainPanel;
	VerticalPanel mainPanel;
	Grid topTable;
	Grid midTable;
	Grid botTable;
	TreeMap<TextContainer, Widget> textContainerWidgetMap;
	double[] paddingNeeded;
	int basezIndex;

	public GWTLabelFactory(DrawingArea canvas, AbsolutePanel chartMainPanel) {
		super(canvas);
		this.chartMainPanel = chartMainPanel;
		textContainerWidgetMap = new TreeMap<TextContainer, Widget>();
		initLayout();
	}
	
	private void initLayout(){
		mainPanel = new VerticalPanel();
		chartMainPanel.add(mainPanel, 0, 0);
		mainPanel.setPixelSize(canvas.getWidth(), canvas.getHeight());
//		basezIndex = DOM.getIntStyleAttribute(chartMainPanel.getElement(), "zIndex");
		basezIndex = 100;
		DOM.setIntStyleAttribute(mainPanel.getElement(), "zIndex", basezIndex-1);
		topTable = new Grid(1, 3);
		midTable = new Grid(1, 3);
		botTable = new Grid(1, 3);
		initGrid(topTable);
		initGrid(midTable);
		initGrid(botTable);
		midTable.setHeight("100%");
		mainPanel.setCellHeight(midTable, "100%");
	}
	
	private void initGrid(Grid grid){
		grid.setWidth("100%");
//		grid.getElement().getStyle().setPadding(2, Unit.PX);
		grid.setCellSpacing(0);
		grid.setCellPadding(1);
		grid.getCellFormatter().setWidth(0, 1, "100%");
		grid.setBorderWidth(0);
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		for(int i=0;i<3;i++){
			grid.setWidget(0, i, new FlowPanel());
		}
		mainPanel.add(grid);
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
	
	public void update(boolean forced){
		if(forced){
			
		}
		if(chartTitle != null && !textContainerWidgetMap.containsKey(chartTitle)){
			createChartTitle();
		}
		for(HasLegend legendOwner:legendOwners){
			if(legendOwner.isShowLegend() && !textContainerWidgetMap.containsKey(legendOwner.getLegend())){
				createLegend(legendOwner);
			}
			if(!legendOwner.isShowLegend() && textContainerWidgetMap.containsKey(legendOwner.getLegend())){
				textContainerWidgetMap.get(legendOwner.getLegend()).removeFromParent();
				textContainerWidgetMap.remove(legendOwner.getLegend());
			}
		}
	}
	
	protected void positionTextContainerWidget(TextContainer textContainer, Widget widget){
		Grid grid = null;
		switch (textContainer.verticalPosition) {
		case Auto:
		case Top:
			grid = topTable;
			break;
		case Bottom:
			grid = botTable;
			break;
		case Middle:
			grid = midTable;
			break;
		}
		switch (textContainer.horizontalPosition) {
		case Auto:
		case Left:
			insertElementInGrid(grid, 0, widget);
			break;
		case Middle:
			insertElementInGrid(grid, 1, widget);
			break;
		case Right:
			insertElementInGrid(grid, 2, widget);
			break;
		}
	}
	
	protected void insertElementInGrid(Grid grid, int column, Widget widgetToInsert){
		DOM.setIntStyleAttribute(widgetToInsert.getElement(), "zIndex", basezIndex+1);
		Widget w = grid.getWidget(0, column);
		if(w != null){
			((Panel)w).add(widgetToInsert);
		}
		else{
			FlowPanel fp = new FlowPanel();
			fp.add(widgetToInsert);
			grid.setWidget(0, column, fp);
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
		fp.setWidth("100%");
		
		List<LegendEntry> entries = legendOwner.getLegendEntries();
		switch(legendOwner.getLegend().legendEntryLayout){
		case AUTO:
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
		hp.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_BOTTOM);
//		hp.getElement().getStyle().setMargin(legend.paddingBetweenEntries, Unit.PX);
		DOM.setStyleAttribute(hp.getElement(), "margin", legend.paddingBetweenEntries/2+"px");
		return hp;
	}
	
	protected void setTextContainerStyleForWidget(TextContainer textContainer, Widget widget){
		widget.getElement().getStyle().setProperty("filter", "'progid:DXImageTransform.Microsoft.Alpha(Opacity="+textContainer.getBackground().getFillColor().getAlpha()+")'");
//		widget.getElement().getStyle().setProperty("filter", "alpha(opacity="+textContainer.getBackground().getFillColor().getAlpha()+")");
//		widget.getElement().getStyle().setProperty("msFilter", "progid:DXImageTransform.Microsoft.Alpha(Opacity="+textContainer.getBackground().getFillColor().getAlpha()*100+")");
//		widget.getElement().setPropertyString("-ms-filter", "progid:DXImageTransform.Microsoft.Alpha(Opacity="+textContainer.getBackground().getFillColor().getAlpha()*100+")");
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
			RootPanel.get().add(mainPanel);
		}
		int top = midTable.getElement().getAbsoluteTop() - mainPanel.getAbsoluteTop();
		int left = midTable.getElement().getAbsoluteLeft() - mainPanel.getAbsoluteLeft();
		int width = midTable.getElement().getClientWidth();
		int height = midTable.getElement().getClientHeight();
		
		if(!attached){
			RootPanel.get().remove(mainPanel);
			chartMainPanel.add(mainPanel, 0, 0);
		}
		this.paddingNeeded =  new double[]{
				top,
				canvas.getWidth()-left-width,
				canvas.getHeight()-top-height,
				left};
	}

	
}
