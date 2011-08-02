package com.inepex.inechart.awtchart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
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
import com.inepex.inechart.chartwidget.label.HasLegend;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.label.Legend;
import com.inepex.inechart.chartwidget.label.LegendEntry;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.misc.VerticalPosition;
import com.inepex.inegraphics.awt.ColorUtil;
import com.inepex.inegraphics.awt.DrawingAreaAwt;
import com.inepex.inegraphics.shared.DrawingArea;

public class AwtLabelFactory extends LabelFactoryBase {
	
	double[] paddingNeeded;
	JPanel mainPanel;
	JPanel topPanel;
	JPanel botPanel;
	ArrayList<JComponent> topPaddingComponents;
	ArrayList<JComponent> botPaddingComponents;

	public AwtLabelFactory(DrawingArea canvas, JPanel labelPanel) {
		super(canvas);
		mainPanel = labelPanel;
		topPaddingComponents = new ArrayList<JComponent>();
		botPaddingComponents = new ArrayList<JComponent>();
		initLayout();
	}
	
	private void initLayout(){
		Dimension dimension = new Dimension(canvas.getWidth(),canvas.getHeight());
		mainPanel.setMaximumSize(dimension);
		mainPanel.setPreferredSize(dimension);
		mainPanel.setMinimumSize(dimension);
		mainPanel.setSize(dimension);
		BorderLayout layout = new BorderLayout();
		mainPanel.setLayout(layout);
		topPanel = new JPanel();
		botPanel = new JPanel();
		BoxLayout layout2 = new BoxLayout(topPanel, BoxLayout.Y_AXIS);
		topPanel.setLayout(layout2);
		BoxLayout layout3 = new BoxLayout(botPanel, BoxLayout.Y_AXIS);
		botPanel.setLayout(layout3);
		topPanel.setOpaque(false);
		botPanel.setOpaque(false);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(botPanel, BorderLayout.SOUTH);
//		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
//		botPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	@Override
	public void update() {
		topPaddingComponents.clear();
		botPaddingComponents.clear();
		topPanel.removeAll();
		botPanel.removeAll();
		createChartTitle();
		for(HasLegend legendOwner : legendOwners){
			if(legendOwner.isShowLegend()){
				createLegend(legendOwner);
			}
		}
		JLabel l = new JLabel("JLABEL");
		l.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
		topPanel.add(l);
	}
	
	protected void positionTextContainerWidget(TextContainer textContainer, JComponent component){
		JPanel wrapper = createTextContainerJPanel(textContainer);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT,(textContainer.getLeftPadding() + textContainer.getRightPadding())/2, (textContainer.getBottomPadding() + textContainer.getTopPadding())/2);
		wrapper.setLayout(layout);
		wrapper.add(component);
		switch (textContainer.getVerticalPosition()) {
		case Auto:
		case Middle:
		case Top:
			topPanel.add(wrapper);
			break;
		case Bottom:
			botPanel.add(wrapper);			
			break;
		}
		switch (textContainer.getHorizontalPosition()) {
		case Auto:
		case Left:
			break;
		case Middle:
			layout.setAlignment(FlowLayout.CENTER);
			break;
		case Right:
			layout.setAlignment(FlowLayout.RIGHT);
			break;
		}
	}
	
	protected void createChartTitle(){
		if(chartTitle == null){
			return;
		}
		JPanel panel = new JPanel();	
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		if(chartTitle.getTitle() != null){
			panel.add(createJLabelFromText(chartTitle.getTitle()));
		}
		if(chartTitle.getDescription() != null){
			panel.add(createJLabelFromText(chartTitle.getDescription()));
		}
		if(chartTitle.isIncludeInPadding()){
			if(chartTitle.getVerticalPosition() == VerticalPosition.Bottom){
				botPaddingComponents.add(panel);
			}
			else{
				topPaddingComponents.add(panel);
			}
		}
		panel.setOpaque(false);
		positionTextContainerWidget(chartTitle, panel);
	}
	
	protected void createLegend(HasLegend legendOwner){
		if(!legendOwner.isShowLegend() || legendOwner.getLegend() == null){
			return;
		}
		JPanel panel = new JPanel();		
		List<LegendEntry> entries = legendOwner.getLegendEntries();
		for(LegendEntry e : entries){
			panel.add(createLegendEntry(e, legendOwner.getLegend()));
		}
		switch(legendOwner.getLegend().getLegendEntryLayout()){
		case AUTO:
			break;
		case ROW:
			BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
			panel.setLayout(layout);
			break;
		case COLUMN:
			BoxLayout layout1 = new BoxLayout(panel, BoxLayout.Y_AXIS);
			panel.setLayout(layout1);
			break;
		}
		if(legendOwner.getLegend().isIncludeInPadding()){
			if(legendOwner.getLegend().getVerticalPosition() == VerticalPosition.Bottom){
				botPaddingComponents.add(panel);
			}
			else{
				topPaddingComponents.add(panel);
			}
		}
		panel.setOpaque(false);
		positionTextContainerWidget(chartTitle, panel);
	}
	
	protected JLabel createLegendEntry(LegendEntry e, Legend legend){
		Image img = new BufferedImage((int)legend.getLegendSymbol().getWidth(), (int) legend.getLegendSymbol().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(ColorUtil.getColor(e.getColor().getColor()));
		g.fillRect(0, 0, (int)legend.getLegendSymbol().getWidth(), (int) legend.getLegendSymbol().getHeight());
		ImageIcon i = new ImageIcon(img);
		JLabel entry = createJLabelFromText(e.getText());
		entry.setIcon(i);
		entry.setIconTextGap(legend.getPaddingBetweenTextAndSymbol());
		return entry;
	}
	
	protected JPanel createTextContainerJPanel(TextContainer textContainer){
		JPanel tcjp = new JPanel();
		tcjp.setBackground(ColorUtil.getColor(textContainer.getBackground().getFillColor().getColor()));
		tcjp.setBorder(BorderFactory.createLineBorder(ColorUtil.getColor(textContainer.getBackground().getLineProperties().getLineColor().getColor()),
				(int) textContainer.getBackground().getLineProperties().getLineWidth()));
		return tcjp;
	}

	protected JLabel createJLabelFromText(Text text){
		JLabel label = new JLabel(text.getText());
		label.setFont(DrawingAreaAwt.getFont(text.getTextProperties().getFontFamily(), text.getTextProperties().getFontStyle(), text.getTextProperties().getFontSize()));
		label.setForeground(ColorUtil.getColor(text.getTextProperties().getColor().getColor()));
		label.setOpaque(false);
		return label;
	}
	
	@Override
	public double[] getPaddingNeeded() {
		int top = 0;
//		for(JComponent c : topPaddingComponents){
//			if(c.getHeight() > top)
//				top = c.getHeight();
//		}
		top = topPanel.getHeight();
		int bot = 0;
		for(JComponent c : botPaddingComponents){
			if(c.getHeight() > bot)
				bot = c.getHeight();
		}

		return new double[]{top,0,bot,0};
	}	
}
