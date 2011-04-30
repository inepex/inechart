package com.inepex.inechart;

import com.inepex.inegraphics.awt.DrawingAreaAwt;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.gobjects.Arc;

public class ArcTest {

	public static void main(String[] args) {
		DrawingAreaAwt daa = new DrawingAreaAwt(400, 400);
		Context context = new Context(1.0, "black", 5, "red", 0.0, 0.0, 0.0, "white");
		Arc arc = new Arc(200, 200, 1, context, true, true,
				170, new Double(0).intValue(), new Double(60).intValue());
		
		daa.addGraphicalObject(arc);
		
		daa.update();
		daa.saveToFile("chart.png");
	}
	
	
}
