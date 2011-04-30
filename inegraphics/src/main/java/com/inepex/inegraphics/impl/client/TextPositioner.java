package com.inepex.inegraphics.impl.client;


import java.util.TreeMap;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.inepex.inegraphics.shared.gobjects.Text;

public class TextPositioner {
	protected AbsolutePanel panel;
	protected TreeMap<Text, InlineLabel> texts;

	public TextPositioner(AbsolutePanel panel) {
		this.panel = panel;
		this.texts = new TreeMap<Text, InlineLabel>();
	}
	
	/**
	 * The added text will be displayed at the next update() call
	 * @param text
	 */
	public void lazyAddText(Text text){
		if(text != null)
			texts.put(text, null);
	}
	
	/**
	 * Adds and instantly displays the given text
	 * @param text
	 */
	public void addText(Text text){
		if(text != null){
			InlineLabel lbl = createLabel(text);
			panel.add(lbl, text.getBasePointX(), text.getBasePointY());
			texts.put(text, lbl);
		}
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
			if(texts.get(text) != null){
				panel.remove(texts.get(text));
			}
			InlineLabel lbl = createLabel(text);
			panel.add(lbl, text.getBasePointX(), text.getBasePointY());
			texts.put(text, lbl);
		}
	}
	
	private static InlineLabel createLabel(Text label){
		InlineLabel lbl = new InlineLabel(label.getText());
		//TODO
		return lbl;
	}
	
}
