package com.inepex.inegraphics.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.inepex.inegraphics.shared.Context;
import com.inepex.inegraphics.shared.DrawingArea;
import com.inepex.inegraphics.shared.gobjects.Arc;
import com.inepex.inegraphics.shared.gobjects.Circle;
import com.inepex.inegraphics.shared.gobjects.Line;
import com.inepex.inegraphics.shared.gobjects.Path;
import com.inepex.inegraphics.shared.gobjects.PathElement;
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

	@Override
	protected void clear() {
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);
	}

	@Override
	protected void drawPath(Path path) {
		if (path.getPathElements().size() == 0) return;
		applyContext(path.getContext());
		
		int startX = path.getBasePointX();
		int startY = path.getBasePointY();
		
		int endX = path.getPathElements().get(0).getEndPointX();
		int endY = path.getPathElements().get(0).getEndPointY();
		
		g2.drawLine(startX, startY, endX, endY);
		
		for (int i = 0; i < path.getPathElements().size() - 1; i++) {
			PathElement start = path.getPathElements().get(i);
			PathElement end = path.getPathElements().get(i + 1);
			g2.drawLine(start.getEndPointX(), start.getEndPointY(), end.getEndPointX(), end.getEndPointY());
		}

	}

	@Override
	protected void drawRectangle(Rectangle rectangle) {
		applyContext(rectangle.getContext());
		g2.setColor(ColorUtil.getColor(rectangle.getContext().getFillColor()));
		int x = rectangle.getBasePointX();
		int y = rectangle.getBasePointY();
		int width = rectangle.getWidth();
		int height = rectangle.getHeight();
		int roundedCornerRadius = rectangle.getRoundedCornerRadius();
		
		if (height < 0) {
			height = height * -1;
			y = y - height;
		}
		
		if(roundedCornerRadius > 0 && !rectangle.hasFill()){
			g2.drawRoundRect(x, y, width, height, roundedCornerRadius, roundedCornerRadius);
		} else if (roundedCornerRadius > 0 && rectangle.hasFill()){
			g2.fillRoundRect(x, y, width, height, roundedCornerRadius, roundedCornerRadius);
		} else if (roundedCornerRadius == 0 && !rectangle.hasFill()){
			g2.drawRect(x, y, width, height);
		} else if (roundedCornerRadius == 0 && rectangle.hasFill()){
			g2.fillRect(x, y, width, height);
		}
	}

	@Override
	protected void drawCircle(Circle circle) {
		applyContext(circle.getContext());
		if (circle.hasFill()){
			g2.fillOval(circle.getBasePointX(), circle.getBasePointY(), circle.getRadius(), circle.getRadius());
		} else {
			g2.drawOval(circle.getBasePointX(), circle.getBasePointY(), circle.getRadius(), circle.getRadius());
		}
	}

	@Override
	protected void drawLine(Line line) {
		applyContext(line.getContext());
		g2.drawLine(line.getBasePointX(), line.getBasePointY(), line.getEndPointX(), line.getEndPointY());

	}
	
	/**
	 * Sets the context variables of the g2.
	 * Shadows not supported
	 * @param context
	 */
	protected void applyContext(Context context){
		g2.setStroke(new BasicStroke(context.getStrokeWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		g2.setBackground(ColorUtil.getColor(context.getFillColor()));
		g2.setColor(ColorUtil.getColor(context.getStrokeColor()));
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
		applyContext(arc.getContext());
		//awt uses basepoint as the top left corner of the arc
		int basepointX = arc.getBasePointX() - arc.getRadius();
		int basepointY = arc.getBasePointY() - arc.getRadius();
		Arc2D arcPath = new Arc2D.Double(
				basepointX, 
				basepointY,
				arc.getRadius() * 2, arc.getRadius() * 2,
				arc.getStartAngle(),
				arc.getArcAngle(),
				Arc2D.PIE);
		
		g2.draw(arcPath);
		g2.setColor(ColorUtil.getColor(arc.getContext().getFillColor()));
		g2.fill(arcPath);
		
	}

	@Override
	protected void drawText(Text text) {
		applyContext(text.getContext());
		g2.drawString(text.getText(), text.getBasePointX(), text.getBasePointY()); 
	}
	
	public BufferedImage getImage(){
		return image;
	}

}
