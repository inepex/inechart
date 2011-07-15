package com.inepex.inegraphics.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import com.inepex.inegraphics.impl.client.TextPositionerBase;
import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.gobjects.Arc;
import com.inepex.inegraphics.shared.gobjects.Circle;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.LineTo;
import com.inepex.inegraphics.shared.gobjects.MoveTo;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
import com.inepex.inegraphics.shared.gobjects.QuadraticCurveTo;
import com.inepex.inegraphics.shared.gobjects.Rectangle;
import com.inepex.inegraphics.shared.gobjects.Text;

public class DrawingAreaAwt extends DrawingArea {

	BufferedImage image;
	Graphics2D g2;

	public DrawingAreaAwt(int width, int height) {
		super(width, height);

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	/**
	 * ! since the image is not present, do not use {@link #saveToFile(String)} or {@link #saveToOutputStream(OutputStream)} methods !
	 * @param graphics2D
	 * @param width
	 * @param height
	 */
	public DrawingAreaAwt(Graphics2D graphics2D,int width, int height){
		super(width, height);
		g2 = graphics2D;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	@Override
	protected void clear() {
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);
	}

	@Override
	protected void drawPath(Path path) {
		ArrayList<PathElement> pathElements = path.getPathElements();
		if((path.hasFill() == false && path.hasStroke() == false )||
				pathElements.size() < 1)
			return;
		GeneralPath p = new GeneralPath();
		
		p.moveTo(path.getBasePointX(), path.getBasePointY());
		for(PathElement pe : pathElements){
			if(pe instanceof QuadraticCurveTo){
				QuadraticCurveTo qTo = (QuadraticCurveTo) pe;
				p.quadTo(qTo.getControlPointX(), qTo.getControlPointY(), qTo.getEndPointX(), qTo.getEndPointY());
			}
			else if(pe instanceof LineTo){
				LineTo lTo = (LineTo) pe;
				p.lineTo(lTo.getEndPointX(), lTo.getEndPointY());
			}
			else if(pe instanceof MoveTo){
				MoveTo mTo = (MoveTo) pe;
				p.moveTo(mTo.getEndPointX(), mTo.getEndPointY());
			}
		}
		if(path.hasStroke()){
			applyContext(path.getContext(), true);
			g2.draw(p);
		}
		//if its a closed path then we can fill it
		if(path.hasFill() && pathElements.get(pathElements.size()-1).getEndPointX() == path.getBasePointX() &&
				pathElements.get(pathElements.size()-1).getEndPointY() == path.getBasePointY()){
			applyContext(path.getContext(), false);
			g2.fill(p);
		}

	}

	@Override
	protected void drawRectangle(Rectangle rectangle) {
		applyContext(rectangle.getContext(), true);
		g2.setColor(ColorUtil.getColor(rectangle.getContext().getFillColor()));
		double x = rectangle.getBasePointX();
		double y = rectangle.getBasePointY();
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();
		double roundedCornerRadius = rectangle.getRoundedCornerRadius();
		
		if (height < 0) {
			height = height * -1;
			y = y - height;
		}
		
		if(roundedCornerRadius > 0){
			if(rectangle.hasFill()){
				applyContext(rectangle.getContext(), false);
				g2.fillRoundRect((int)x,(int) y,(int) width, (int)height, (int)roundedCornerRadius, (int)roundedCornerRadius);
			}
			if(rectangle.hasStroke()){
				applyContext(rectangle.getContext(), true);
				g2.drawRoundRect((int)x,(int) y, (int)width, (int)height,(int) roundedCornerRadius,(int) roundedCornerRadius);
			}
			
		}
		else {
			if(rectangle.hasFill()){
				applyContext(rectangle.getContext(), false);
				g2.fillRect((int)x,(int) y, (int)width,(int) height);
			}
			if(rectangle.hasStroke()){
				applyContext(rectangle.getContext(), true);
				g2.drawRect((int)x,(int) y,(int) width,(int) height);
			}
		} 
	}

	@Override
	protected void drawCircle(Circle circle) {
		if (circle.hasFill()){
			applyContext(circle.getContext(), false);
			g2.fillOval(
					(int)circle.getBasePointX() - (int)circle.getRadius(), 
					(int)circle.getBasePointY() - (int)circle.getRadius(),
					(int)circle.getRadius()  * 2,
					(int)circle.getRadius() * 2);
		if (circle.hasStroke())
			applyContext(circle.getContext(), true);
			g2.drawOval(
					(int)circle.getBasePointX() - (int)circle.getRadius(), 
					(int)circle.getBasePointY() - (int)circle.getRadius(),
					(int)circle.getRadius()  * 2,
					(int)circle.getRadius() * 2);
		}
	}

	@Override
	protected void drawLine(Line line) {
		applyContext(line.getContext(), true);
		g2.drawLine((int)line.getBasePointX(), (int)line.getBasePointY(),(int) line.getEndPointX(), (int)line.getEndPointY());

	}
	
	/**
	 * Sets the context variables of the g2.
	 * Shadows not supported
	 * @param context
	 * @param useStrokeColor TODO
	 */
	protected void applyContext(Context context, boolean useStrokeColor){
		g2.setStroke(new BasicStroke((float) context.getStrokeWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		Color c = ColorUtil.getColorFromStringWithAlpha(useStrokeColor ? context.getStrokeColor() : context.getFillColor(), context.getAlpha());
		g2.setColor(c);
	}
	
	public void saveToOutputStream(OutputStream outputStream) {
		try {
			ImageIO.write(image, "png", outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToFile(String filename) {
		try {
			File outputfile = new File(filename);
			ImageIO.write(image, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void drawArc(Arc arc) {
		//awt uses basepoint as the top left corner of the arc
		double basepointX = arc.getBasePointX() - arc.getRadius();
		double basepointY = arc.getBasePointY() - arc.getRadius();
		Arc2D arcPath = new Arc2D.Double(
				basepointX, 
				basepointY,
				arc.getRadius() * 2, arc.getRadius() * 2,
				arc.getStartAngle(),
				arc.getArcAngle(),
				Arc2D.PIE);
		if(arc.hasFill()){
			applyContext(arc.getContext(), false);
			g2.fill(arcPath);
		}
		if(arc.hasStroke()){
			applyContext(arc.getContext(), true);
			g2.draw(arcPath);
		}		
	}

	@Override
	protected void drawText(Text text) {
		applyContext(text.getContext(), true);
		g2.setColor(Color.BLACK);
//		updateTextDimensions(text);
		measureText(text);
		TextPositionerBase.calcTextPosition(text);
		drawString(text.getText(), (int)text.getBasePointX(), (int)text.getBasePointY() + 10); 
	}
	
	private void drawString(String text, int x, int y) {
        for (String line : text.split("\n"))
        {
            g2.drawString(line, x, y);
        	y += g2.getFontMetrics().getHeight();
        }
    }

	private void updateTextDimensions(Text text) {
		text.setWidth(text.getText().length() * 7);
		text.setHeight(12);
	}

	public BufferedImage getImage(){
		return image;
	}

	@Override
	public void measureText(Text text) {
		applyFontContext(text);
		Rectangle2D r = g2.getFontMetrics().getStringBounds(text.getText(), g2);
		text.setHeight((int) r.getHeight());
		text.setWidth((int) r.getWidth());
	}
	
	private void applyFontContext(Text text){
		String fontFamily = text.getFontFamily();
		StringTokenizer st = new StringTokenizer(fontFamily," ",false);
		String t="";
		while (st.hasMoreElements()) 
			t += st.nextElement();
		String[] fontFamilies = t.split(",");
		int style;
		String tStyle = text.getFontStyle().toLowerCase();
		if(tStyle == "bold" || tStyle == "bolder"){
			style = Font.BOLD;
		}
		else if(tStyle == "italic"){
			style = Font.ITALIC;
		}
		else{
			style = Font.PLAIN;
		}
		Font font = new Font(fontFamilies[0], style, text.getFontSize());
		
		g2.setFont(font);		
	}

}
