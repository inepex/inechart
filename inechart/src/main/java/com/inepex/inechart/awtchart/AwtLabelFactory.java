package com.inepex.inechart.awtchart;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.inepex.inechart.chartwidget.label.HasLegendEntries;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.label.StyledLabel;
import com.inepex.inechart.chartwidget.label.Text;
import com.inepex.inechart.chartwidget.label.TextContainer;
import com.inepex.inechart.chartwidget.misc.HorizontalPosition;
import com.inepex.inegraphics.awt.ColorUtil;
import com.inepex.inegraphics.awt.DrawingAreaAwt;
import com.inepex.inegraphics.shared.DrawingArea;

public class AwtLabelFactory extends LabelFactoryBase {
	
	JPanel mainPanel;
	JPanel topPanel;
	JPanel botPanel;
	JPanel leftPanel;
	JPanel rightPanel;
	
	public AwtLabelFactory(DrawingArea canvas, JPanel labelPanel) {
		super(canvas);
		mainPanel = labelPanel;
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
		rightPanel = new JPanel();
		leftPanel = new JPanel();
		BoxLayout layout2 = new BoxLayout(topPanel, BoxLayout.Y_AXIS);
		topPanel.setLayout(layout2);
		BoxLayout layout3 = new BoxLayout(botPanel, BoxLayout.Y_AXIS);
		botPanel.setLayout(layout3);
		BoxLayout layout4 = new BoxLayout(leftPanel, BoxLayout.X_AXIS);
		topPanel.setLayout(layout4);
		BoxLayout layout5 = new BoxLayout(rightPanel, BoxLayout.X_AXIS);
		botPanel.setLayout(layout5);
		topPanel.setOpaque(false);
		botPanel.setOpaque(false);
		leftPanel.setOpaque(false);
		rightPanel.setOpaque(false);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(botPanel, BorderLayout.SOUTH);
		mainPanel.add(rightPanel, BorderLayout.EAST);
		mainPanel.add(leftPanel, BorderLayout.WEST);
	}
	
	
	protected void positionTextContainerWidget(TextContainer textContainer, JComponent component){
		JPanel wrapper = createTextContainerJPanel(textContainer);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT,(textContainer.getLeftPadding() + textContainer.getRightPadding())/2, (textContainer.getBottomPadding() + textContainer.getTopPadding())/2);
		wrapper.setLayout(layout);
		wrapper.add(component);
		if(isFixedPosition(textContainer)){
			mainPanel.add(wrapper);
			wrapper.setLocation(textContainer.getTop(), textContainer.getLeft());
		}
		switch (textContainer.getVerticalPosition()) {
		case Auto:
		case Top:
			topPanel.add(wrapper);
			break;
		case Bottom:
			botPanel.add(wrapper);			
			break;
		case Middle:
			if(textContainer.getHorizontalPosition() == HorizontalPosition.Left){
				leftPanel.add(wrapper);
			}
			else{
				rightPanel.add(wrapper);
			}
			return;
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
		
		panel.setOpaque(false);
		positionTextContainerWidget(chartTitle, panel);
	}
	
	protected void createLegend(){
		if(legend == null){
			return;
		}
		JPanel panel = new JPanel();		
		for(HasLegendEntries legendEntryOwner : legendOwners){
			if(legendEntryOwner.isDisplayEntries()){
				TreeMap<String, com.inepex.inechart.chartwidget.properties.Color> legendEntries = legendEntryOwner.getLegendEntries();
				if(legendEntries == null || legendEntries.size() == 0){
					continue;
				}
				for(String name : legendEntries.keySet()){
					panel.add(createLegendEntry(name, legendEntries.get(name)));
				}
			}
		}
		switch(legend.getLegendEntryLayout()){
		case AUTO:
			panel.setLayout(new FlowLayout());
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
		panel.setOpaque(false);
		positionTextContainerWidget(chartTitle, panel);
	}
	
	protected JLabel createLegendEntry(String name, com.inepex.inechart.chartwidget.properties.Color color){
		Image img = new BufferedImage((int)legend.getLegendSymbol().getWidth(), (int) legend.getLegendSymbol().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(ColorUtil.getColor(color.getColor()));
		g.fillRect(0, 0, (int)legend.getLegendSymbol().getWidth(), (int) legend.getLegendSymbol().getHeight());
		ImageIcon i = new ImageIcon(img);
		JLabel entry = new JLabel(name);
		entry.setFont(DrawingAreaAwt.getFont(legend.getTextProperties().getFontFamily(), legend.getTextProperties().getFontStyle(), legend.getTextProperties().getFontSize()));
		entry.setForeground(ColorUtil.getColor(legend.getTextProperties().getColor().getColor()));
		entry.setOpaque(false);
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
		return new double[]{
				topPanel.getHeight(),
				rightPanel.getWidth(),
				botPanel.getHeight(),
				leftPanel.getWidth()
			};
	}

	@Override
	protected void clear() {
		topPanel.removeAll();
		botPanel.removeAll();
		rightPanel.removeAll();
		leftPanel.removeAll();
		ArrayList<Component> toRemove = new ArrayList<Component>();
		for(int i=0; i<mainPanel.getComponentCount();i ++){
			Component c = mainPanel.getComponent(i);
			if(c != topPanel && c != botPanel && c != rightPanel && c != leftPanel){
				toRemove.add(c);
			}
		}
		for(Component c : toRemove){
			mainPanel.remove(c);
		}
	}


	@Override
	protected void createStyledLabel(StyledLabel label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStyledLabel(StyledLabel label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void measurePadding() {}	
}
