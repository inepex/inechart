package com.inepex.inecharting.chartwidget.model;

public enum State {
	/**
	 * is not on the viewport
	 */
	INVISIBLE,
	/**
	 * is on the viewport
	 */
	VISIBLE,
	/**
	 * selected, mouseover, etc
	 */
	ACTIVE,
	/**
	 * clicked
	 */
	FOCUSED
}
