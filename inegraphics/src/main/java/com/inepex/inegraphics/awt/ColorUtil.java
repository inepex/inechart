package com.inepex.inegraphics.awt;

import java.awt.Color;

public class ColorUtil {

	/**
	 * converts known color names or hexa rgb values to java Color
	 * @param color
	 * @return
	 */
	public static Color getColor(String colorString){
		Color color;
		try {
			color = Color.decode(colorString);
		} catch (Exception e) {
			try {
			color = Color.decode(colorString.substring(1));
			} catch (Exception e2) {
				color = getColorFromString(colorString);
			}
		}
		
		return color;
	}
	
	public static Color getColorFromString(String colorString){
		if (colorString.equals("white")) return Color.white;
		else if (colorString.equals("black")) return Color.black;
		else if (colorString.equals("red")) return Color.red;
		else if (colorString.equals("blue")) return Color.blue;
		else if (colorString.equals("grey")) return Color.gray;
		else throw new RuntimeException("Unhandled color: " + colorString);
	}
	
}
