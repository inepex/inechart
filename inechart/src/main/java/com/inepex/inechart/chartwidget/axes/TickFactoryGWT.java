package com.inepex.inechart.chartwidget.axes;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;

public class TickFactoryGWT extends TickFactory {
	DateTimeFormat dtf;
	NumberFormat nf;

	@Override
	public String formatTickText(Tick tick, AxisDataType dataType) {
//		if(format.length() == 0){
//			return ((Double)value).toString();
//		}
		String formatted = "";
		try{
			switch (dataType) {
			case Number:
				nf = NumberFormat.getFormat(tick.formatString);
				formatted = nf.format(tick.position);
				break;
			case Time:
				dtf = DateTimeFormat.getFormat(tick.formatString);
				formatted = dtf.format(new Date((long) tick.position));
				break;
			}
		}
		catch (Exception e) {
			formatted = ((Double)tick.position).toString();
		}
		return formatted;
	}
	
	public static String formatValue(Axis axis, double value, String format){
		if(format.length() == 0){
			return ((Double)value).toString();
		}
		String formatted = "";
		try{
			switch (axis.axisDataType) {
			case Number:
				NumberFormat nf = NumberFormat.getFormat(format);
				formatted = nf.format(value);
				break;
			case Time:
				DateTimeFormat dtf = DateTimeFormat.getFormat(format);
				formatted = dtf.format(new Date((long)value));
				break;
			}
		}
		catch (Exception e) {
			formatted = ((Double)value).toString();
		}
		return formatted;
	}

}
