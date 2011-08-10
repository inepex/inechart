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
	DateFormat sdf;
	NumberFormat df;

	@Override
	public String formatTickText(Tick tick, AxisDataType dataType) {
		String formatted = "";
		try{
			switch (dataType) {
			case Number:
				df = new DecimalFormat(tick.getFormatString());
				formatted = df.format(tick.getPosition());
				break;
			case Time:
				sdf = new SimpleDateFormat(tick.getFormatString());
				formatted = sdf.format(new Date((long) tick.getPosition()));
				break;
			}
		}
		catch (Exception e) {
			formatted = ((Double)tick.getPosition()).toString();
		}
		return formatted;
	}

}
