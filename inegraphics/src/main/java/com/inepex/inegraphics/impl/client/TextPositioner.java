package com.inepex.inegraphics.impl.client;


import java.util.TreeMap;

import com.google.gwt.dom.client.Style.BorderStyle;
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
//	protected static TreeMap<String, Integer[]> fontDimensionsPerFamily = new TreeMap<String, Integer[]>();
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
		if(text != null && text.getText().length() > 0){
			texts.put(text, null);
		}
	}
	
	/**
	 * Adds and instantly displays the given text
	 * @param text
	 */
	public void addText(Text text){
		if(text != null && text.getText().length() > 0){
			displayTextWithMeasure(text);
		}
	}
	
	public void measureText(Text text){
		InlineLabel lbl = createLabel(text);
		lbl.getElement().getStyle().setPadding(0, Unit.PX);
		lbl.getElement().getStyle().setMargin(0, Unit.PX);
		RootPanel.get().add(lbl);
		text.setWidth(lbl.getOffsetWidth());
		text.setHeight(lbl.getOffsetHeight() + DEFAULT_PADDING_Y);
		RootPanel.get().remove(lbl);
		text.setChanged(false);
	}
	
	protected void displayTextWithMeasure(Text text){
		if(texts.get(text) != null){
			panel.remove(texts.get(text));
		}
		if(text.isChanged()){
			measureText(text);
		}
		
		TextPositionerBase.calcTextPosition(text);
		InlineLabel lbl = createLabel(text);
//		lbl.getElement().getStyle().setBorderWidth(1, Unit.PX);
//		lbl.getElement().getStyle().setBorderColor("green");
//		lbl.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		panel.add(lbl,(int) text.getBasePointX(), (int) text.getBasePointY());
		texts.put(text, lbl);
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
	
	
	public void update(){
		for(Text text : texts.keySet()){
//			displayText(text);
			displayTextWithMeasure(text);
		}
	}
	
	private static InlineLabel createLabel(Text label){
		InlineLabel lbl = new InlineLabel(label.getText());
		lbl.getElement().getStyle().setFontSize(label.getFontSize(), Unit.PX);
		lbl.getElement().getStyle().setProperty("fontFamily", label.getFontFamily());
		lbl.getElement().getStyle().setProperty("color", label.getColor());
		lbl.getElement().getStyle().setProperty("fontStyle", label.getFontStyle());
		lbl.getElement().getStyle().setProperty("fontWeight", label.getFontWeight());
		return lbl;
	}
	
}
