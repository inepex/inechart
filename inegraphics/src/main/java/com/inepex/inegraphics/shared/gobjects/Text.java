package com.inepex.inegraphics.shared.gobjects;

import com.inepex.inegraphics.shared.Context;

public class Text extends GraphicalObject implements Comparable<Text>{
	
	//TODO font style, size, etc
	protected String text;
	private static int highestComparatorID = Integer.MIN_VALUE;
	private int comparatorID;
	
	public Text(String text,int basePointX, int basePointY){
		this(text, basePointX, basePointY, 100000, Context.getDefaultContext(), false, false);
	}
	
	protected Text(String text,int basePointX, int basePointY, int zIndex, Context context,
			boolean stroke, boolean fill) {
		super(basePointX, basePointY, zIndex, context, stroke, fill);
		comparatorID = highestComparatorID++;
		this.text = text;
	}

	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int compareTo(Text other) {
		return comparatorID - other.comparatorID;
	}

}
