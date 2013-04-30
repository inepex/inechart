package com.inepex.inechart.awtchart;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.inepex.inechart.chartwidget.axes.Axis.AxisDataType;
import com.inepex.inechart.chartwidget.axes.Tick;
import com.inepex.inechart.chartwidget.axes.TickFactory;

public class AwtTickFactory extends TickFactory {

	@Override
	public String formatTickText(Tick tick, AxisDataType dataType) {
		String formatted = "";
		try{
			switch (dataType) {
			case Number:
				NumberFormat df = new DecimalFormat(tick.getFormatString());
				formatted = df.format(tick.getPosition());
				break;
			case Time:
				DateFormat sdf = new SimpleDateFormat(tick.getFormatString());
				formatted = sdf.format(new Date((long) tick.getPosition()));
				break;
			}
		}
		catch (Exception e) {
			formatted = ((Double)tick.getPosition()).toString();
		}
		return formatted;
	}

	@Override
	public String formatValue(AxisDataType axisDataType, double value, String format) {
		throw new UnsupportedOperationException("not implemented yet");
	}

}
