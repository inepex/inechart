package com.inepex.inegraphics.impl.client.ishapes;

import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;

public class Circle extends com.inepex.inegraphics.shared.gobjects.Circle implements InteractiveGraphicalObject {


	public Circle(double basePointX, double basePointY, int zIndex, Context context,
			boolean stroke, boolean fill, double radius) {
		super(basePointX, basePointY, zIndex, context, stroke, fill, radius);
	}

	
	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		double d = radius;
		if(stroke){
			d+=context.getStrokeWidth();
		}
		if( d <= (Math.pow((mouseX - basePointX),2) + Math.pow((mouseY - basePointY),2))){
			return true;
		}
		else
			return false;
	}

}
