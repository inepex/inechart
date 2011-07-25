package com.inepex.inechart.chartwidget.axes;

import java.util.ArrayList;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.Defaults;
import com.inepex.inechart.chartwidget.IneChart;
import com.inepex.inechart.chartwidget.IneChartModule;
import com.inepex.inechart.chartwidget.IneChartModule2D;
import com.inepex.inechart.chartwidget.axes.Axis.AxisDirection;
import com.inepex.inechart.chartwidget.axes.Axis.AxisPosition;
import com.inepex.inechart.chartwidget.label.LabelFactoryBase;
import com.inepex.inechart.chartwidget.properties.Color;
import com.inepex.inechart.chartwidget.properties.LineProperties;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.DrawingAreaAssist;
import com.inepex.inegraphics.shared.GraphicalObjectContainer;
import com.inepex.inegraphics.shared.gobjects.GraphicalObject;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.Rectangle;
import com.inepex.inegraphics.shared.gobjects.Text;
import com.inepex.inegraphics.shared.gobjects.Text.BasePointXPosition;
import com.inepex.inegraphics.shared.gobjects.Text.BasePointYPosition;

/**
 * 
 * Should be singleton per {@link IneChart}.
 * 
 * @author Miklós Süveges, Tibor Somodi / Inepex Ltd.
 * 
 */
public class Axes extends IneChartModule {
	private LabelFactoryBase labelFactory;
	private ArrayList<Axis> axes;
	private TickFactory tickFactory;

	private TreeMap<Axis, GraphicalObjectContainer> gosPerAxis;
	private TreeMap<Axis, ArrayList<Text>> labelsPerAxis;
	/**
	 * helps in padding calculation
	 */
	private TreeMap<Axis, Double> axisLinePosition;
	private TreeMap<Axis, double[]> paddingAroundAxes;
	private static final int tickFillZIndex = Integer.MIN_VALUE;
	private static final int gridLineZIndex = tickFillZIndex + 1;	
	private static final int axisLineZIndex = gridLineZIndex + 1;	
	private static final int tickLineZIndex = axisLineZIndex + 1;
	
	
	public Axes(DrawingArea canvas,  LabelFactoryBase labelFactory) {
		super(canvas);
		axes = new ArrayList<Axis>();
		gosPerAxis = new TreeMap<Axis, GraphicalObjectContainer>();
		labelsPerAxis = new TreeMap<Axis, ArrayList<Text>>();
		axisLinePosition = new TreeMap<Axis, Double>();
		paddingAroundAxes = new TreeMap<Axis, double[]>();
		tickFactory = new TickFactory();
	}

	public Axis createAxis(AxisDirection direction, IneChartModule2D modulToAlign) {
		Axis a = new Axis(Defaults.solidLine());
		a.isVisible = true;
		a.modulToAlign = modulToAlign;
		a.axisDirection = direction;
		addAxis(a);
		return a;
	}

	public void addAxis(Axis axis) {
		axes.add(axis);
	}

	public void removeAxis(Axis axis) {
		removeAllGOAndLabelRelatedToAxis(axis);
		gosPerAxis.remove(axis);
		labelsPerAxis.remove(axis);
		axes.remove(axis);
	}

	@Override
	public void update() {
		for (Axis axis : axes) {
			if (axis.autoCreateTicks){
				tickFactory.autoCreateTicks(axis);
				createDefaultTickAndLabelForAxis(axis);
			}
			removeAllGOAndLabelRelatedToAxis(axis);
			createGOsAndLabelsForAxis(axis);
		}
	}

	public void updateWithOutAutoTickCreation(){
		for (Axis axis : axes) {
			removeAllGOAndLabelRelatedToAxis(axis);
			createGOsAndLabelsForAxis(axis);
		}
	}
	
	void createAxisLine(Axis axis, GraphicalObjectContainer goc, double startX, double startY, double endX, double endY){
		// TODO upper-, lower End
		if (axis.getLineProperties() != null) {
			Line axisLine = new Line(startX, startY, endX, endY, axisLineZIndex, createContext(axis.lineProperties));
			goc.addGraphicalObject(axisLine);
		}
	}
	
	void createTickLinesLabelsGrids(Axis axis, Axis perpAxis, GraphicalObjectContainer goc, GraphicalObjectContainer gridGOs, double startX, double startY){
		// ticks
		ArrayList<Tick> filtered = axis.getVisibleTicks();
		if (axis.isFilterFrequentTicks()){
			filtered = filterFequentTicks(axis, axis.getVisibleTicks());
		}
		for (Tick tick : filtered) {
			// calculate the position
			double tickStartX = 0, tickStartY = 0, tickEndX = 0, tickEndY = 0, gridStartX, gridStartY, gridEndX, gridEndY;
			if (axis.isHorizontal()) {
				gridEndX = gridStartX = tickStartX = tickEndX = axis.getModulToAlign().getCanvasX(tick.position);
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
			} 
			else {
				gridEndY = gridStartY = tickStartY = tickEndY = axis.getModulToAlign().getCanvasY(tick.position);
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
				if(tick.gridLine.getDashDistance() > 0){
					gridGOs.addGraphicalObject(DrawingAreaAssist.createDashedLine(gridLine, tick.gridLine.getDashStrokeLength(), tick.gridLine.getDashDistance()));
				}
				else{
					gridGOs.addGraphicalObject(gridLine);
				}
			}
			createTickText(axis, perpAxis, goc, tick, tickStartX, tickStartY);
		}	
	}
	
	void createTickText(Axis axis, Axis perpAxis, GraphicalObjectContainer goc, Tick tick, double tickStartX, double tickStartY){
		if (tick.tickText != null && tick.tickText.length() > 0) {
			//calculate the text position relative to tick
			BasePointXPosition h = null;
			BasePointYPosition v = null;
			switch (tick.tickTextHorizontalPosition) {
			case Auto:
				if (axis.isHorizontal()) {
					h = BasePointXPosition.MIDDLE;
				} 
				else {
					if (axis.getAxisPosition() == AxisPosition.Maximum && perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left ||
							axis.getAxisPosition() == AxisPosition.Minimum && perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
						h = BasePointXPosition.RIGHT;
						tickStartX -= 4;
					}
					else {
						h = BasePointXPosition.LEFT;
						tickStartX += tick.tickLength + 4;
					}
//					tickStartY += 2;
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
				}
				else {
					if (axis.getAxisPosition() == AxisPosition.Maximum && perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom ||
							axis.getAxisPosition() == AxisPosition.Minimum && perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
						v = BasePointYPosition.TOP;
						tickStartY += tick.tickLength + 2;
					} 
					else {
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
			text.setFontFamily(tick.getTextProperties().getFontFamily());
			text.setFontStyle(tick.getTextProperties().getFontStyle());
			text.setFontWeight(tick.getTextProperties().getFontWeight());
			text.setColor(tick.getTextProperties().getColor().getColor());
			text.setFontSize(tick.getTextProperties().getFontSize());
			canvas.measureText(text);
			goc.addGraphicalObject(text);
		}
	}
	
	void createFillBetweenTicks(Axis axis, GraphicalObjectContainer goc){
		double x, y, width, height;
		for (Object[] tickPair : axis.gridFills) {
			if(((Tick) tickPair[0]).position >= axis.max || ((Tick) tickPair[1]).position <= axis.min)
				continue;
			double tick1,tick2;
			if (axis.isHorizontal()) {
				tick1 = axis.getModulToAlign().getCanvasX(((Tick) tickPair[0]).position);
				tick2 = axis.getModulToAlign().getCanvasX(((Tick) tickPair[1]).position);
				x = Math.max(axis.modulToAlign.getLeftPadding(), Math.min(tick1, tick2));
				y = axis.getModulToAlign().getTopPadding();
				height = axis.getModulToAlign().getHeight();
				width = Math.min(Math.abs(tick1 - tick2), canvas.getWidth() - axis.modulToAlign.getRightPadding() - x);
			}
			else {
				tick1 = axis.getModulToAlign().getCanvasY(((Tick) tickPair[0]).position);
				tick2 = axis.getModulToAlign().getCanvasY(((Tick) tickPair[1]).position);
				x = axis.modulToAlign.getLeftPadding();
				y = Math.max(axis.modulToAlign.getTopPadding(), Math.min(tick1, tick2));
				width = axis.modulToAlign.getWidth();
				height = Math.min(Math.abs(tick1 - tick2), canvas.getHeight() - axis.modulToAlign.getBottomPadding() - y);
			}
			
			Rectangle fill = new Rectangle(
					x,
					y,
					width,
					height,
					0,
					tickFillZIndex,
					createFillContext((Color) tickPair[2]),
					false,
					true);
			goc.addGraphicalObject(fill);
		}
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
				startX = endX = axis.modulToAlign.getWidth() / 2 + axis.modulToAlign.getLeftPadding();
			} else {
				startY = endY = axis.modulToAlign.getHeight() / 2 + axis.modulToAlign.getTopPadding();
			}
			break;
		case Fixed:
			if (axis.fixedPosition >= perpAxis.min && axis.fixedPosition <= perpAxis.max) {
				if (perpAxis.isHorizontal()) {
					startX = endX = axis.modulToAlign.getCanvasX(axis.fixedPosition);
				} else {
					startY = endY = axis.modulToAlign.getCanvasY(axis.fixedPosition);
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
				startX = endX = axis.modulToAlign.getCanvasX(pos);
			} else {
				startY = endY = axis.modulToAlign.getCanvasY(pos);
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
		
		createAxisLine(axis, goc, startX, startY, endX, endY);
		
		GraphicalObjectContainer gridGOs = new GraphicalObjectContainer();
		createTickLinesLabelsGrids(axis, perpAxis, goc, gridGOs, startX, startY);
		
		createFillBetweenTicks(axis, gridGOs);
		
		//add axis line to get a valid bounding box
		GraphicalObjectContainer gocToMeasure = new GraphicalObjectContainer();
		gocToMeasure.addAllGraphicalObject(goc);
		gocToMeasure.addGraphicalObject(new Line(startX, startY, endX, endY, 0, null));
		
		double[] paddingAroundAxis = new double[]{0,0,0,0};
		double[] axisBB = DrawingAreaAssist.getBoundingBox(gocToMeasure);
		if(axis.isHorizontal()){
			paddingAroundAxis[0] = startY - axisBB[1];
			paddingAroundAxis[2] = axisBB[1] + axisBB[3] - startY;
			if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) {
				paddingAroundAxis[1] = axisBB[0] + axisBB[2] - startX;
				paddingAroundAxis[3] = endX - axisBB[0];
			}
			else if (axis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) {
				paddingAroundAxis[1] = axisBB[0] + axisBB[2] - endX;
				paddingAroundAxis[3] = startX - axisBB[0];
			}
		}
		else{
			paddingAroundAxis[1] = axisBB[0] + axisBB[2] - startX;
			paddingAroundAxis[3] = startX - axisBB[0];
			if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) {
				paddingAroundAxis[0] = startY - axisBB[1];
				paddingAroundAxis[2] = axisBB[1] + axisBB[3] - endY;
			}
			else if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
				paddingAroundAxis[0] = endY - axisBB[1];
				paddingAroundAxis[2] = axisBB[1] + axisBB[3] - startY;
			}
		}
		goc.addAllGraphicalObject(gridGOs);
		paddingAroundAxes.put(axis, paddingAroundAxis);
		gosPerAxis.put(axis, goc);
		axisLinePosition.put(axis, axis.isHorizontal() ? startY : startX);
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
				sum += (axis.getModulToAlign().getCanvasX(nextX) 
						- axis.getModulToAlign().getCanvasX(x));
			} else if (axis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) {
				double y = visibleTicks.get(i).getPosition();
				double nextY =  visibleTicks.get(i + 1).getPosition();
				sum += (axis.getModulToAlign().getCanvasY(y) 
						- axis.getModulToAlign().getCanvasY(nextY));
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
				Defaults.colorString,
				0,
				0,
				Defaults.alpha,
				Defaults.colorString);
	}
	
	Context createFillContext(Color color) {
		return new Context(
				Defaults.alpha,
				color.getColor(),
				0,
				color.getColor(),
				0,
				0,
				Defaults.alpha,
				Defaults.colorString);
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

	public double[] getActualModulPaddingForAxis(Axis axis){
		Axis perpAxis;
		if (AxisDirection.isPerpendicular(axis, axis.getModulToAlign().getYAxis()))
			perpAxis = axis.getModulToAlign().getYAxis();
		else
			perpAxis = axis.getModulToAlign().getXAxis();

		double[] paddingAroundAxis = paddingAroundAxes.get(axis);
		if(paddingAroundAxis == null){
			return new double[]{0,0,0,0};
		}
		double top = 0;
		double right = 0;
		double bottom = 0;
		double left = 0;
		boolean visible = true;
		if(axis.isHorizontal()){
			if(axis.axisPosition == AxisPosition.Fixed || axis.axisPosition == AxisPosition.Fixed_Dock_If_Not_Visible){
				double axisLinePos = axis.modulToAlign.getCanvasY(axis.fixedPosition);
				if(axisLinePos < axis.modulToAlign.getTopPadding()){
					if(axis.axisPosition == AxisPosition.Fixed_Dock_If_Not_Visible){
						top = paddingAroundAxis[0];
					}
					visible = false;
				}
				else if(axisLinePos > axis.modulToAlign.getHeight() + axis.modulToAlign.getTopPadding()){
					if(axis.axisPosition == AxisPosition.Fixed_Dock_If_Not_Visible){
						bottom = paddingAroundAxis[2];
					}
					visible = false;
				}
				else{
					top = Math.max(0, axis.modulToAlign.getTopPadding() - axisLinePos + paddingAroundAxis[0]);
					bottom = Math.max(0, axisLinePos + paddingAroundAxis[2] - axis.modulToAlign.getHeight() - axis.modulToAlign.getTopPadding()); 
				}
			}
			//bottom
			else if( (axis.axisPosition == AxisPosition.Maximum && perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) ||
					(axis.axisPosition == AxisPosition.Minimum && perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) ){
				bottom = paddingAroundAxis[2];
			}
			//top
			else if( (axis.axisPosition == AxisPosition.Maximum && perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Top) ||
					(axis.axisPosition == AxisPosition.Minimum && perpAxis.getAxisDirection() == AxisDirection.Vertical_Ascending_To_Bottom) ){
				top = paddingAroundAxis[0];
			}
			if(visible){
				left = paddingAroundAxis[3];
				right = paddingAroundAxis[1];
			}
		}
		else{
			if(axis.axisPosition == AxisPosition.Fixed || axis.axisPosition == AxisPosition.Fixed_Dock_If_Not_Visible){
				double axisLinePos = axis.modulToAlign.getCanvasX(axis.fixedPosition);
				if(axisLinePos < axis.modulToAlign.getLeftPadding()){
					if(axis.axisPosition == AxisPosition.Fixed_Dock_If_Not_Visible){
						left = paddingAroundAxis[3];
					}
					visible = false;
				}
				else if(axisLinePos > axis.modulToAlign.getWidth() + axis.modulToAlign.getLeftPadding()){
					if(axis.axisPosition == AxisPosition.Fixed_Dock_If_Not_Visible){
						right = paddingAroundAxis[1];
					}
					visible = false;
				}
				else{
					left = Math.max(0, axis.modulToAlign.getLeftPadding() - axisLinePos + paddingAroundAxis[3]);
					right = Math.max(0, axisLinePos + paddingAroundAxis[1] - axis.modulToAlign.getWidth() - axis.modulToAlign.getLeftPadding()); 
				}
			}
			//left
			else if( (axis.axisPosition == AxisPosition.Maximum && perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) ||
					(axis.axisPosition == AxisPosition.Minimum && perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) ){
				left = paddingAroundAxis[3];
			}
			//right
			else if( (axis.axisPosition == AxisPosition.Maximum && perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Right) ||
					(axis.axisPosition == AxisPosition.Minimum && perpAxis.getAxisDirection() == AxisDirection.Horizontal_Ascending_To_Left) ){
				right = paddingAroundAxis[1];
			}
			if(visible){
				top = paddingAroundAxis[0];
				bottom = paddingAroundAxis[2];
			}
		}
//		if(top < 0)
//			top = 0;
//		if(right < 0)
//			right = 0;
//		if(bottom < 0)
//			bottom = 0;
//		if(left < 0)
//			left = 0;
		return new double[] {top, right, bottom, left};
	}
	
	public double[] getPaddingNeededForAxis(Axis axis){
		Axis perpAxis;
		if (AxisDirection.isPerpendicular(axis, axis.getModulToAlign().getYAxis()))
			perpAxis = axis.getModulToAlign().getYAxis();
		else
			perpAxis = axis.getModulToAlign().getXAxis();
		double top = 0;
		double right = 0;
		double bottom = 0;
		double left = 0;
		
		
		//TODO
		
		
		
		
		
		
		return null;
	}
	
	private void createDefaultTickAndLabelForAxis(Axis axis) {
		for(Tick t : axis.getTicks()){
			if(axis.autoCreateGrids && t.getPosition() != axis.getMin() && t.getPosition() != axis.getMax()){
				t.setGridLine(Defaults.gridLine());
			}
			t.setTickText(t.getPosition() + "");
		}
	}
	
}
