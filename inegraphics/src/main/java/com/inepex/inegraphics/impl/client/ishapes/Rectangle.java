package com.inepex.inegraphics.impl.client.ishapes;


import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;

public class Rectangle extends com.inepex.inegraphics.shared.gobjects.Rectangle implements InteractiveGraphicalObject {
		

	
	public Rectangle(double basePointX, double basePointY, double width, double height, double roundedCornerRadius, int zIndex, Context context,
			boolean stroke, boolean fill) {
		super(basePointX, basePointY, width, height, roundedCornerRadius, zIndex, context, stroke, fill);
		
	}


	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		// TODO roundedcorner
		if(mouseX < basePointX ||
			mouseX > basePointX + width ||
			mouseY < basePointY ||
			mouseY > basePointY + height)
			return false;
		else 
			return true ;
	}
	

}
