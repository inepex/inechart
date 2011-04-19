package com.inepex.inecharting.chartwidget.newimpl.misc;

import java.util.Comparator;

public class ZIndexComparator implements Comparator<HasZIndex> {

	@Override
	public int compare(HasZIndex arg0, HasZIndex arg1) {
		
		return arg0.getZIndex() - arg1.getZIndex();
	}

}
