package com.inepex.inegraphics.impl.client.ishapes;

import com.inepex.inegraphics.impl.client.InteractiveGraphicalObject;
import com.inepex.inegraphics.shared.Context;

public class Circle extends com.inepex.inegraphics.shared.gobjects.Circle implements InteractiveGraphicalObject {


	public Circle(int basePointX, int basePointY, int zIndex, Context context,
			boolean stroke, boolean fill, int radius) {
		super(basePointX, basePointY, zIndex, context, stroke, fill, radius);
	}

	
	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		int d = radius;
		if(stroke){
			d+=context.getStrokeWidth();
		}
		if( d <= ((mouseX - basePointX)^2 + (mouseY - basePointY)^2)){
			return true;
		}
		else
			return false;
	}

}
