package com.inepex.inegraphics.impl.client;


import java.util.TreeMap;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.inepex.inegraphics.shared.gobjects.Text;
import com.inepex.inegraphics.shared.gobjects.Text.BasePointXPosition;
import com.inepex.inegraphics.shared.gobjects.Text.BasePointYPosition;

public class TextPositioner {
	protected AbsolutePanel panel;
	protected TreeMap<Text, InlineLabel> texts;
	protected static TreeMap<String, Integer[]> fontDimensionsPerFamily = new TreeMap<String, Integer[]>();
	protected static final int DEFAULT_PADDING_X = 0;
	protected static final int DEFAULT_PADDING_Y = 3;
	protected static final Text defaultTextToMeasure = new Text(0, 0, "1234567890", "", 16, "normal", "normal", BasePointXPosition.LEFT, BasePointYPosition.TOP);
	
	public TextPositioner(AbsolutePanel panel) {
		this.panel = panel;
		this.texts = new TreeMap<Text, InlineLabel>();
	}
	
	/**
	 * The added text will be displayed at the next update() call
	 * @param text
	 */
	public void lazyAddText(Text text){
		if(text != null){
			texts.put(text, null);
			if(text.getFontFamily()!= null && text.getFontFamily().length() > 0 && !fontDimensionsPerFamily.containsKey(text.getFontFamily())){
				fontDimensionsPerFamily.put(text.getFontFamily(), null);
			}
		}
	}
	
	/**
	 * Adds and instantly displays the given text
	 * @param text
	 */
	public void addText(Text text){
		if(text != null && text.getText().length() > 0){
//			displayText(text);
			displayTextWithMeasure(text);
		}
	}
	
	protected void displayTextWithMeasure(Text text){
		if(texts.get(text) != null){
			panel.remove(texts.get(text));
		}
		if(text.getBasePointXPosition() != BasePointXPosition.LEFT || text.getBasePointYPosition() != BasePointYPosition.TOP){
			InlineLabel lbl = createLabel(text);
			lbl.getElement().getStyle().setPadding(0, Unit.PX);
			lbl.getElement().getStyle().setMargin(0, Unit.PX);
			RootPanel.get().add(lbl);
			text.setWidth(lbl.getOffsetWidth());
			text.setHeight(lbl.getOffsetHeight() + DEFAULT_PADDING_Y);
			RootPanel.get().remove(lbl);
		}
		
		double x = 1;
		double y = 1;
		switch(text.getBasePointXPosition()){
		case LEFT:
			x = text.getBasePointX() + text.getLeftPadding();
			break;
		case MIDDLE:
			x = text.getBasePointX() - (text.getWidth() + text.getLeftPadding() + text.getRightPadding())/2;
			break;
		case RIGHT: 
			x = text.getBasePointX() - (text.getWidth() + text.getLeftPadding() + text.getRightPadding());
			break;
		}
		switch(text.getBasePointYPosition()){
		case TOP:
			y = text.getBasePointY() + text.getTopPadding();
			break;
		case MIDDLE:
			y = text.getBasePointY() - (text.getHeight() + text.getTopPadding() + text.getBottomPadding())/2;
			break;
		case BOTTOM: 
			y = text.getBasePointY() - (text.getHeight()  + text.getTopPadding() + text.getBottomPadding());
			break;
		}
		InlineLabel lbl = createLabel(text);
		panel.add(lbl,(int) x, (int) y);
		texts.put(text, lbl);
	}
	
	protected void displayText(Text text){
		if(text.getWidth() == 0 || text.getHeight() == 0)
			updateTextDimensions(text, true);
		if(texts.get(text) != null){
			panel.remove(texts.get(text));
		}
		double x = 1,y = 1;
		switch(text.getBasePointXPosition()){
		case LEFT:
			x = text.getBasePointX() + text.getLeftPadding();
			break;
		case MIDDLE:
			x = text.getBasePointX() - (text.getWidth() + text.getLeftPadding() + text.getRightPadding())/2;
			break;
		case RIGHT: 
			x = text.getBasePointX() - (text.getWidth() + text.getLeftPadding() + text.getRightPadding());
			break;
		}
		switch(text.getBasePointYPosition()){
		case TOP:
			y = text.getBasePointY() + text.getTopPadding();
			break;
		case MIDDLE:
			y = text.getBasePointY() - (text.getHeight() + text.getTopPadding() + text.getBottomPadding())/2;
			break;
		case BOTTOM: 
			y = text.getBasePointY() - (text.getHeight()  + text.getTopPadding() + text.getBottomPadding());
			break;
		}
		InlineLabel lbl = createLabel(text);
		panel.add(lbl, (int)x, (int) y);
		texts.put(text, lbl);
	}
	
	protected boolean updateTextDimensions(Text text, boolean measureIfFontFamilyNotPresent){
		if(measureIfFontFamilyNotPresent && (!fontDimensionsPerFamily.containsKey(text.getFontFamily()) || fontDimensionsPerFamily.get(text.getFontFamily()) == null))
			measureFontFamily(text.getFontFamily());
		if(fontDimensionsPerFamily.containsKey(text.getFontFamily())){
			Integer[] ffd = fontDimensionsPerFamily.get(text.getFontFamily());
			if(ffd == null)
				return false;
			text.setWidth( (int) ((((ffd[0]-DEFAULT_PADDING_X) / 10d) / 16d) * text.getFontSize() * text.getText().length()));
//			text.setHeight((ffd[1]-DEFAULT_PADDING_Y) / 16 * text.getFontSize() + DEFAULT_PADDING_Y);
			text.setHeight((int) ((ffd[1]-DEFAULT_PADDING_Y) / 16d * text.getFontSize() + DEFAULT_PADDING_Y));
			return true;
		}
		else
			return false;
	}
	
	public void removeAllText(){
		for(Text text : texts.keySet()){
			if(texts.get(text) != null){
				panel.remove(texts.get(text));
			}
		}
		texts.clear();
	}
	
	public void removeText(Text text){
		if(texts.get(text) !=  null)
			panel.remove(texts.get(text));
		texts.remove(text);
	}
	
	protected void updateFontFamilyDimensions(boolean measureAll){
		for(String ff : fontDimensionsPerFamily.keySet()){
			if(measureAll || fontDimensionsPerFamily.get(ff) == null){
				measureFontFamily(ff);
			}
		}
	}
	
	protected void measureFontFamily(String ff){
		if(ff != null && ff.length() > 0){
			defaultTextToMeasure.setFontFamily(ff);
			InlineLabel testLabel = createLabel(defaultTextToMeasure);
			testLabel.getElement().getStyle().setPadding(0, Unit.PX);
			testLabel.getElement().getStyle().setMargin(0, Unit.PX);
			RootPanel.get().add(testLabel);
			Integer[] dimensions = new Integer[]{testLabel.getOffsetWidth(), testLabel.getOffsetHeight()}; 
			RootPanel.get().remove(testLabel);
			fontDimensionsPerFamily.put(ff, dimensions);
		}
	}
	
	public void update(){
		updateFontFamilyDimensions(false);
		for(Text text : texts.keySet()){
//			displayText(text);
			displayTextWithMeasure(text);
		}
	}
	
	private static InlineLabel createLabel(Text label){
		InlineLabel lbl = new InlineLabel(label.getText());
		lbl.getElement().getStyle().setFontSize(label.getFontSize(), Unit.PX);
		lbl.getElement().setAttribute("fontFamily", label.getFontFamily());
		lbl.getElement().setAttribute("color", label.getColor());
		lbl.getElement().setAttribute("fontStyle", label.getFontStyle());
		lbl.getElement().setAttribute("fontWeight", label.getFontWeight());
		return lbl;
	}
	
}
