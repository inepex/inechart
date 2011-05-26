package com.inepex.inecharttest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.inepex.inecharttest.client.showcase.AxisTest;
import com.inepex.inecharttest.client.showcase.BarChartTest;
import com.inepex.inecharttest.client.showcase.SpeedTest;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IneChartTest implements EntryPoint {

	@Override
	public void onModuleLoad() {
//		new IneChartingNewImplTest();
//		RootPanel.get().add(new AxisTest());
//		RootPanel.get().add(new SpeedTest());
		RootPanel.get().add(new BarChartTest());
	}
	
}
