package com.inepex.inechart.chartwidget.axes;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModul;
import com.inepex.inechart.chartwidget.IneChartModul2D;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.axes.Axis.AxisPosition;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.impl.client.ishapes.Rectangle;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.Text;
import com.inepex.inegraphics.shared.gobjects.Text.BasePointXPosition;
import com.inepex.inegraphics.shared.gobjects.Text.BasePointYPosition;

/**
 * 
 * A singleton modul per {@link IneChart}
 * 
 * @author Miklós Süveges / Inepex Ltd.
 * 
 */
public class Axes extends IneChartModul {

	private ArrayList<Axis> axes;

	private TreeMap<Axis, GraphicalObjectContainer> gosPerAxis;
	private TreeMap<Axis, ArrayList<Text>> labelsPerAxis;
	private boolean redrawNeeded;

	private static final int tickFillZIndex = Integer.MIN_VALUE;
	private static final int gridLineZIndex = tickFillZIndex + 1;	
	private static final int axisLineZIndex = gridLineZIndex + 1;	
	private static final int tickLineZIndex = axisLineZIndex + 1;
	
	
	public Axes(DrawingArea canvas) {
		super(canvas);
		axes = new ArrayList<Axis>();
		gosPerAxis = new TreeMap<Axis, GraphicalObjectContainer>();
		labelsPerAxis = new TreeMap<Axis, ArrayList<Text>>();
		redrawNeeded = false;
	}

	public Axis createAxis(AxisDirection direction, IneChartModul2D modulToAlign) {
		Axis a = new Axis(LineProperties.getDefaultSolidLine());
		a.isVisible = true;
		a.modulToAlign = modulToAlign;
		a.axisDirection = direction;
		addAxis(a);
		return a;
	}

	public void addAxis(Axis axis) {
		axes.add(axis);
		if (axis.isVisible)
			redrawNeeded = true;
	}

	public void removeAxis(Axis axis) {
		removeAllGOAndLabelRelatedToAxis(axis);
		gosPerAxis.remove(axis);
		labelsPerAxis.remove(axis);
		axes.remove(axis);
		if (axis.isVisible)
			redrawNeeded = true;
	}

	@Override
	public void update() {
		for (Axis axis : axes) {
			if (axis.changed) {
				removeAllGOAndLabelRelatedToAxis(axis);
				createGOsAndLabelsForAxis(axis);
				axis.changed = false;
			}
		}
		redrawNeeded = false;
	}

	void createGOsAndLabelsForAxis(Axis axis) {
		GraphicalObjectContainer goc = new GraphicalObjectContainer();
		double startX = 0, startY = 0, endX = 0, endY = 0;
		Axis perpAxis;
		if (AxisDirection.isPerpendicular(axis, axis.getModulToAlign().getYAxis()))
			perpAxis = axis.getModulToAlign().getYAxis();
		else
			perpAxis = axis.getModulToAlign().getXAxis();
		// determine the axis main position
		switch (axis.axisPosition) {
		case Minimum:
			if (perpAxis.axisDirection == AxisDirection.Horizontal_Ascending_To_Left) {
				startX = endX = canvas.getWidth() - axis.modulToAlign.getRightPadding();
			} else if (perpAxis.axisDirection == AxisDirection.Horizontal_Ascending_To_Right) {
				startX = endX = axis.modulToAlign.getLeftPadding();
			} else if (perpAxis.axisDirection == AxisDirection.Vertical_Ascending_To_Bottom) {
				startY = endY = axis.modulToAlign.getTopPadding();
			} else if (perpAxis.axisDirection == AxisDirection.Vertical_Ascending_To_Top) {
				startY = endY = canvas.getHeight() - axis.modulToAlign.getBottomPadding();
			}
			break;
		case Maximum:
			if (perpAxis.axisDirection == AxisDirection.Horizontal_Ascending_To_Right) {
				startX = endX = canvas.getWidth() - axis.modulToAlign.getRightPadding();
			} else if (perpAxis.axisDirection == AxisDirection.Horizontal_Ascending_To_Left) {
				startX = endX = axis.modulToAlign.getLeftPadding();
			} else if (perpAxis.axisDirection == AxisDirection.Vertical_Ascending_To_Top) {
				startY = endY = axis.modulToAlign.getTopPadding();
			} else if (perpAxis.axisDirection == AxisDirection.Vertical_Ascending_To_Bottom) {
				startY = endY = canvas.getHeight() - axis.modulToAlign.getBottomPadding();
			}
			break;
		case Middle:
			if (perpAxis.isHorizontal()) {
				startX = endX = (canvas.getWidth() - leftPadding - rightPadding) / 2 + leftPadding;
			} else {
				startY = endY = (canvas.getHeight() - topPadding - bottomPadding) / 2 + topPadding;
			}
			break;
		case Fixed:
			if (axis.fixedPosition >= perpAxis.min && axis.fixedPosition <= perpAxis.max) {
				if (perpAxis.isHorizontal()) {
					startX = endX = axis.modulToAlign.getCanvasX(axis.fixedPosition, perpAxis);
				} else {
					startY = endY = axis.modulToAlign.getCanvasY(axis.fixedPosition, perpAxis);
				}
			} else
				isVisible = false;
			break;
		case Fixed_Dock_If_Not_Visible:
			double pos = axis.fixedPosition;
			if (axis.fixedPosition < perpAxis.min) {
				pos = perpAxis.min;
			} else if (axis.fixedPosition > perpAxis.max) {
				pos = perpAxis.max;
			}
			if (perpAxis.isHorizontal()) {
				startX = endX = axis.modulToAlign.getCanvasX(pos, perpAxis);
			} else {
				startY = endY = axis.modulToAlign.getCanvasY(pos, perpAxis);
			}
			break;
		}
		if (perpAxis.isHorizontal()) {
			if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) {
				startY = axis.modulToAlign.getTopPadding();
				endY = canvas.getHeight() - axis.modulToAlign.getBottomPadding();
			} else if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
				endY = axis.modulToAlign.getTopPadding();
				startY = canvas.getHeight() - axis.modulToAlign.getBottomPadding();
			}
		} else {
			if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) {
				startX = canvas.getWidth() - axis.modulToAlign.getRightPadding();
				endX = axis.modulToAlign.getLeftPadding();
			} else if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
				endX = canvas.getWidth() - axis.modulToAlign.getRightPadding();
				startX = axis.modulToAlign.getLeftPadding();
			}
		}
		// TODO upper-, lower End
		if (axis.getLineProperties() != null) {
			Line axisLine = new Line(startX, startY, endX, endY, axisLineZIndex, createContext(axis.lineProperties));
			goc.addGraphicalObject(axisLine);
		}
		// ticks
		ArrayList<Tick> filtered = axis.getVisibleTicks();
		if (axis.isFilterFrequentTicks()){
			filtered = filterFequentTicks(axis, axis.getVisibleTicks());
		}
		for (Tick tick : filtered) {
			// calculate the position
			double tickStartX = 0, tickStartY = 0, tickEndX = 0, tickEndY = 0, gridStartX, gridStartY, gridEndX, gridEndY;
			if (axis.isHorizontal()) {
				gridEndX = gridStartX = tickStartX = tickEndX = axis.getModulToAlign().getCanvasX(tick.position, axis);
				gridStartY = axis.getModulToAlign().getTopPadding();
				gridEndY = canvas.getHeight() - axis.getModulToAlign().getBottomPadding();
				switch (tick.tickPosition) {
				case Cross:
					tickStartY = startY - tick.tickLength / 2;
					tickEndY = tickStartY + tick.tickLength;
					break;
				case To_Lower_Values:
					tickStartY = startY;
					if (perpAxis.axisDirection == AxisDirection.Vertical_Ascending_To_Bottom) {
						tickEndY = tickStartY - tick.tickLength;
					} else {
						tickEndY = tickStartY + tick.tickLength;
					}
					break;
				case To_Upper_Values:
					tickStartY = startY;
					if (perpAxis.axisDirection == AxisDirection.Vertical_Ascending_To_Bottom) {
						tickEndY = tickStartY + tick.tickLength;
					} else {
						tickEndY = tickStartY - tick.tickLength;
					}
					break;
				}
			} else {
				gridEndY = gridStartY = tickStartY = tickEndY = axis.getModulToAlign().getCanvasY(tick.position, axis);
				gridStartX = axis.getModulToAlign().getLeftPadding();
				gridEndX = canvas.getWidth() - axis.getModulToAlign().getRightPadding();
				switch (tick.tickPosition) {
				case Cross:
					tickStartX = startX - tick.tickLength / 2;
					tickEndX = tickStartX + tick.tickLength;
					break;
				case To_Lower_Values:
					tickStartX = startX;
					if (perpAxis.axisDirection == AxisDirection.Horizontal_Ascending_To_Left) {
						tickEndX = tickStartX + tick.tickLength;
					} else {
						tickEndX = tickStartX - tick.tickLength;
					}
					break;
				case To_Upper_Values:
					tickStartX = startX;
					if (perpAxis.axisDirection == AxisDirection.Horizontal_Ascending_To_Left) {
						tickEndX = tickStartX - tick.tickLength;
					} else {
						tickEndX = tickStartX + tick.tickLength;
					}
					break;
				}
			}
			// Tick line
			if (tick.tickLine != null) {
				Line tickLine = new Line(tickStartX, tickStartY, tickEndX, tickEndY, tickLineZIndex, createContext(tick.tickLine));
				goc.addGraphicalObject(tickLine);
			}
			// Grid line
			if (tick.gridLine != null) {
				Line gridLine = new Line(gridStartX, gridStartY, gridEndX, gridEndY, gridLineZIndex, createContext(tick.gridLine));
				goc.addGraphicalObject(gridLine);
			}
			// Text
			if (tick.tickText != null && tick.tickText.length() > 0) {
				BasePointXPosition h = null;
				BasePointYPosition v = null;
				switch (tick.tickTextHorizontalPosition) {
				case Auto:
					if (axis.isHorizontal()) {
						h = BasePointXPosition.MIDDLE;
					} else {
						if (axis.getAxisPosition() == AxisPosition.Maximum
								&& perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left
								|| axis.getAxisPosition() == AxisPosition.Minimum
								&& perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
							h = BasePointXPosition.RIGHT;
							tickStartX -= 4;

						} else {
							h = BasePointXPosition.LEFT;
							tickStartX += tick.tickLength + 4;
						}
						tickStartY += 2;
					}
					break;
				case Left:
					h = BasePointXPosition.LEFT;
					break;
				case Right:
					h = BasePointXPosition.RIGHT;
					break;
				case Middle:
					h = BasePointXPosition.MIDDLE;
					break;
				}
				switch (tick.tickTextVerticalPosition) {
				case Auto:
					if (!axis.isHorizontal()) {
						v = BasePointYPosition.MIDDLE;
					} else {
						if (axis.getAxisPosition() == AxisPosition.Maximum
								&& perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom
								|| axis.getAxisPosition() == AxisPosition.Minimum
								&& perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
							v = BasePointYPosition.TOP;
							tickStartY += tick.tickLength + 3;
						} else {
							v = BasePointYPosition.BOTTOM;
							tickStartY += 1;
						}
					}
					break;
				case Bottom:
					v = BasePointYPosition.BOTTOM;
					break;
				case Middle:
					v = BasePointYPosition.MIDDLE;
					break;
				case Top:
					v = BasePointYPosition.TOP;
					break;
				}
				Text text = new Text(tick.tickText, tickStartX, tickStartY, h, v);
				goc.addGraphicalObject(text);
			}
			// TODO DASHed line!!!
		}

		//fills between ticks
		double x, x2, y, y2;
		for (Object[] tickPair : axis.gridFills) {
			if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
				x = axis.getModulToAlign().getCanvasX(((Tick) tickPair[0]).position, axis);
				x2 = axis.getModulToAlign().getCanvasX(((Tick) tickPair[1]).position, axis);;
				y = axis.getModulToAlign().getTopPadding();
				y2 = canvas.getHeight() - axis.getModulToAlign().getBottomPadding();
			} else {
				x = axis.getModulToAlign().getLeftPadding();
				x2 = canvas.getWidth() - axis.getModulToAlign().getRightPadding();
				y = axis.getModulToAlign().getCanvasY(((Tick) tickPair[0]).position, axis);
				y2 = Math.max(0.0 + axis.getModulToAlign().getTopPadding(), axis.getModulToAlign().getCanvasY(((Tick) tickPair[1]).position, axis));
			}
			Rectangle fill = new Rectangle(
					x,
					y,
					x2 - x,
					y2 - y,
					0,
					tickFillZIndex,
					createFillContext((Color) tickPair[2]),
					false,
					true);
			goc.addGraphicalObject(fill);
		}
		gosPerAxis.put(axis, goc);
		graphicalObjectContainer.addAllGraphicalObject(goc);
	}

	private ArrayList<Tick> filterFequentTicks(Axis axis, ArrayList<Tick> visibleTicks) {
		if (visibleTicks.size() <= 1) return visibleTicks;
		double avgTextLength = 0.0;
		double sum = 0.0;
		for (Tick tick : visibleTicks){
			sum += tick.getTickText().length();
		}
		avgTextLength = sum / new Double(visibleTicks.size());
		double avgTextWidth = avgTextLength * 10;
		
		sum = 0.0;
		double avgDistanceBetweenTicks = 0.0;
		for (int i = 0; i<visibleTicks.size()-1; i++){
			if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
				double x = visibleTicks.get(i).getPosition();
				double nextX =  visibleTicks.get(i + 1).getPosition();
				sum += (axis.getModulToAlign().getCanvasX(nextX, axis) 
						- axis.getModulToAlign().getCanvasX(x, axis));
			} else if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
				double y = visibleTicks.get(i).getPosition();
				double nextY =  visibleTicks.get(i + 1).getPosition();
				sum += (axis.getModulToAlign().getCanvasY(y, axis) 
						- axis.getModulToAlign().getCanvasY(nextY, axis));
			}	
		}
		avgDistanceBetweenTicks = sum / new Double(visibleTicks.size());
		
		if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
			avgTextWidth = 10;
		}
		
		ArrayList<Tick> filteredTicks = new ArrayList<Tick>();
		if (avgDistanceBetweenTicks < avgTextWidth) {
			Long ratio = Math.round(avgTextWidth / avgDistanceBetweenTicks);
			int counter = ratio.intValue();
			for (int i = 0; i<visibleTicks.size(); i++){
				if (visibleTicks.get(i).isUnfiltereble() || counter == ratio.intValue()) {
					filteredTicks.add(visibleTicks.get(i));
					counter = 0;
				}
				counter++;	
			}
		} else {
			filteredTicks.addAll(visibleTicks);
		}
		return filteredTicks;
	}

	Context createContext(LineProperties lineProperties) {
		return new Context(
				lineProperties.getLineColor().getAlpha(),
				lineProperties.getLineColor().getColor(),
				lineProperties.getLineWidth(),
				Color.DEFAULT_COLOR,
				0,
				0,
				Color.DEFAULT_ALPHA,
				Color.DEFAULT_COLOR);
	}
	
	Context createFillContext(Color color) {
		return new Context(
				Color.DEFAULT_ALPHA,
				color.getColor(),
				0,
				color.getColor(),
				0,
				0,
				Color.DEFAULT_ALPHA,
				Color.DEFAULT_COLOR);
	}

	void removeAllGOAndLabelRelatedToAxis(Axis axis) {
		if (gosPerAxis.get(axis) != null) {
			for (GraphicalObject go : gosPerAxis.get(axis).getGraphicalObjects()) {
				graphicalObjectContainer.removeGraphicalObject(go);
			}
		}
		gosPerAxis.remove(axis);
		labelsPerAxis.remove(axis);
	}

	@Override
	public boolean redrawNeeded() {
		if (!redrawNeeded) {
			for (Axis axis : axes) {
				if (axis.changed) {
					redrawNeeded = true;
					break;
				}
			}
		}
		return redrawNeeded;
	}	

}