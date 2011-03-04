package com.inepex.inecharting.chartwidget.properties;

import java.util.TreeMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.inepex.inecharting.chartwidget.model.State;

public class MarkInfo{
	
	public static MarkInfo getDefaultMarkInfo(){
		return new MarkInfo(
				new TextBoxDrawingInfo(
						"#CCCC00",
						2, 
						"#CC3300",
						0.35,
						true,
						"white",
						FontStyle.NORMAL,
						FontWeight.BOLD,
						"Calibri",
						13),
				new TextBoxDrawingInfo(
						"#CCCC00",
						2, 
						"#CC3300",
						0.8,
						true,
						"white",
						FontStyle.NORMAL,
						FontWeight.BOLD,
						"Calibri",
						13),
				new TextBoxDrawingInfo(
						"#CCCC00",
						2, 
						"#CC0000",
						0.9,
						true,
						"white",
						FontStyle.NORMAL,
						FontWeight.BOLD,
						"Calibri",
						13));
	}
	
	protected TreeMap<State, TextBoxDrawingInfo> infoPerState;
	
	public MarkInfo(TextBoxDrawingInfo tbdi) {
		infoPerState = new TreeMap<State, TextBoxDrawingInfo>();
		addTextBoxDrawingInfo(tbdi, State.NORMAL);
		addTextBoxDrawingInfo(tbdi, State.ACTIVE);
		addTextBoxDrawingInfo(tbdi, State.FOCUSED);
	}
	
	public MarkInfo(TextBoxDrawingInfo normal, TextBoxDrawingInfo active, TextBoxDrawingInfo focused) {
		infoPerState = new TreeMap<State, TextBoxDrawingInfo>();
		addTextBoxDrawingInfo(normal, State.NORMAL);
		addTextBoxDrawingInfo(active, State.ACTIVE);
		addTextBoxDrawingInfo(focused, State.FOCUSED);
	}
	
	/**
	 * @return the infoPerState
	 */
	TreeMap<State, TextBoxDrawingInfo> getInfoPerState() {
		return infoPerState;
	}

	public void addTextBoxDrawingInfo(TextBoxDrawingInfo info, State state){
		infoPerState.put(state, info);
	}
	
	public TextBoxDrawingInfo getTextBoxDrawingInfo(State state){
		return infoPerState.get(state);
	}

	
}
