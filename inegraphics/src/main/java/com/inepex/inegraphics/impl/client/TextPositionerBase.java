package com.inepex.inegraphics.impl.client;

import com.inepex.inegraphics.shared.gobjects.Text;

public class TextPositionerBase {

	/**
	 * calc height and width before use!
	 * @param text
	 */
	public static void calcTextPosition(Text text){
		
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
		text.setBasePointX(x);
		text.setBasePointY(y);
	}
	
}
