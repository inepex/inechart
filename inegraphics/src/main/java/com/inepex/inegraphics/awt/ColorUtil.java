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
	
	public static int getRed(String colorString){
		String r = normalizeColorString(colorString).substring(0,2);
		return convertToDec(r);
	}
	
	public static int getGreen(String colorString){
		String r = normalizeColorString(colorString).substring(2,4);
		return convertToDec(r);
	}
	
	public static int getBlue(String colorString){
		String r = normalizeColorString(colorString).substring(4,6);
		return convertToDec(r);
	}
	
	/**
	 * Parses a color from the given string and alpha
	 * colorString can be in one of the following formats:
	 * 	"#ffffff",
	 * 	"#fff",
	 * 	"fff",
	 * 	"ffffff"
	 * while alpha is in [0,1] interval.
	 * 
	 * Warning: this method will not throw any exception!
	 * 
	 * @param colorString
	 * @param alpha
	 * @return the desired Color, or {@link Color#black} if parsing fails
	 */
	public static Color getColorFromStringWithAlpha(String colorString, double alpha){
		try{
			int[] rgb = getRGB(colorString);
			return new Color(
					rgb[0],
					rgb[1],
					rgb[2],
					getAlpha(alpha));
		}
		catch (Exception e) {
			try{
				return getColorFromString(colorString);
			}
			catch (Exception e1) {
				return Color.black;
			}
		}
	}
	
	private static int[] getRGB(String colorString){
		String normalized = normalizeColorString(colorString);
		int r = convertToDec(normalized.substring(0, 2));
		int g = convertToDec(normalized.substring(2, 4));
		int b = convertToDec(normalized.substring(4, 6));
		return new int[]{r,g,b};
	}
	
	private static int getAlpha(double alpha){
		return (int) (alpha * 255);
	}
	
	private static String normalizeColorString(String colorString){
		String normalized = colorString;
		if(normalized.startsWith("#"))
			normalized = normalized.substring(1);
		if(normalized.length() == 3){
			String r = normalized.substring(0, 1);
			String g = normalized.substring(1, 2);
			String b = normalized.substring(2, 3);
			normalized = r + r + g + g + b + b;
		}
		return normalized;
	}
	
	
	private static int convertToDec(String hex){
		return Integer.parseInt(hex, 16);
	}
}
