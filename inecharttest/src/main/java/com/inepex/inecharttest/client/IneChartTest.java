package com.inepex.inecharttest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inecharttest.client.showcase.ApfLikeChartTest;
import com.inepex.inecharttest.client.showcase.BarChartTest;
import com.inepex.inecharttest.client.showcase.MultiLineChart;
import com.inepex.inecharttest.client.showcase.SpeedTest;
import com.inepex.inecharttest.client.showcase.ViewportSelectorChartTest;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IneChartTest implements EntryPoint {
	
	HorizontalPanel menu = new HorizontalPanel();
	SimplePanel panel = new SimplePanel();

	@Override
	public void onModuleLoad() {
		RootPanel.get("loading").setVisible(false);
		RootPanel.get().add(menu);
		RootPanel.get().add(panel);
		panel.getElement().getStyle().setMarginLeft(50, Unit.PX);
		
//		addTest("linechartTest", new LineChartTest());
		addTest("barchartTest", new BarChartTest());
		addTest("speedTest", new SpeedTest());
//		addTest("lineAndBar", new LineAndBarChartTest());
		addTest("multiLineChart", new MultiLineChart());
		addTest("viewportTest", new ViewportSelectorChartTest());
		addTest("apfLikeChartTest",new ApfLikeChartTest());

	}
	
	private void addTest(String name, final Widget test){
		Button btn = new Button(name);
		btn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				panel.setWidget(test);
			}
		});
		menu.add(btn);
	}
	
}
