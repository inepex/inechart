package com.inepex.inecharttest.client;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.inepex.inechart.misc.IneScrollPanel;
import com.inepex.inechart.misc.ScrollBarPresenter;
import com.inepex.inechart.misc.ScrollBarView;
import com.inepex.inechart.misc.Scrollable;

public class ScrollBarTest extends FlowPanel {

	HTML lorem = new HTML("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam ante ligula, euismod vitae mattis sed, dictum sed nisi." +
			" Quisque posuere rhoncus viverra. Fusce eget elit id dolor euismod luctus. Nunc a arcu eu neque faucibus facilisis. Nulla aliquet mi eget velit " +
			"convallis accumsan et eget quam. Duis odio libero, egestas sed faucibus at, feugiat ut justo. Fusce ultrices scelerisque justo, sit amet consequat" +
			" lectus convallis ac. Fusce aliquam, nunc a cursus mattis, velit lorem aliquet tortor, aliquam lacinia mi nibh sollicitudin diam. Vestibulum varius" +
			" molestie massa, vitae fringilla sapien blandit dapibus. Cras ac mauris quis turpis volutpat tempus. Donec id elit odio. Aenean ac nibh vitae diam" +
			" imperdiet ornare. Vestibulum odio est, dignissim vel tristique id, luctus vel sem. Curabitur dui urna, placerat a lacinia vitae, rutrum at justo." +
			" Nunc id libero eget tellus lacinia scelerisque id sed nunc. In odio leo, varius quis dapibus vel, placerat eu justo. Nunc elementum, elit vel ultricies " +
			"blandit, purus leo varius odio, a venenatis felis massa in mi. Pellentesque eget tristique nisi. Phasellus quis eros sed tellus tempus luctus. usce pharetra interdum venenatis. " +
			"Aenean faucibus interdum sem quis suscipit. Nunc lacinia pretium placerat. Nulla sagittis consectetur diam, non pellentesque risus congue vel. Ut quis dolor vel leo mollis" +
			" dignissim at a arcu. Morbi non velit ligula. Mauris massa dolor, congue at pharetra placerat, vehicula at felis. Maecenas pretium placerat sem, ut faucibus leo porttitor " +
			"nec. Praesent in erat lacus. Integer molestie, enim et rutrum adipiscing, lacus urna mattis velit, molestie vulputate nulla eros molestie lorem. Aenean id consequat velit." +
			" Donec in dolor id eros ultricies feugiat et vulputate augue. Fusce pharetra sodales tincidunt. Nulla sed libero sapien. Aliquam fermentum pulvinar mauris, nec venenatis arcu " +
			"feugiat ut. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Suspendisse potenti. Mauris sapien arcu, rhoncus non porttitor et, rutrum ut nisi. " +
			"Maecenas sollicitudin pellentesque metus sed viverra. Donec sed mauris vel nibh mattis ultricies vitae eget leo. Suspendisse a dolor non erat congue vulputate. " +
			"In luctus blandit elementum. Aliquam erat volutpat. Suspendisse sed lectus sapien. Donec pharetra, nisi dapibus feugiat interdum, ipsum felis sodales augue," +
			" nec mollis tortor tellus sed leo. Fusce luctus dapibus quam, nec fermentum risus bibendum id. Mauris tempus placerat gravida. Suspendisse et lorem lectus." +
			" Nulla facilisi. Praesent tellus lorem, cursus vitae posuere eget, cursus vel nulla. Aenean pharetra metus quis lorem feugiat eget porta magna pharetra. " +
			"Pellentesque arcu velit, venenatis quis porttitor nec, sodales sed orci. Suspendisse potenti. Quisque enim elit, imperdiet at laoreet at, luctus nec nibh." +
			"Vivamus orci metus, feugiat at venenatis vel, volutpat vitae nunc. Nulla facilisi. Morbi non dui et nisi faucibus vehicula ac feugiat libero. Phasellus eleifend risus id" );
	
	IneScrollPanel sp;
	public ScrollBarTest() {
		sp = new IneScrollPanel();
		sp.setWidget(lorem);
		this.add(sp);
		sp.setSize("300px", "300px");
		sp.getElement().getStyle().setMargin(50, Unit.PX);
	}
	
	
}
