package paintchat.saistyle;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

import paintchat.Mg.Info;



public class OverlayGraphics extends Graphics {
	
	private Graphics g;
	private Info info;
	
		
	private static final int ACTION_NONE = -1;
	private static final int ACTION_DRAWLINE = 0;
	private static final int ACTION_DRAWRECT = 1;
	private static final int ACTION_FILLRECT = 2;
	private static final int ACTION_DRAWOVAL = 3;
	private static final int ACTION_DRAWIMAGE = 4;
	
	public static int STORE_DEFAULT = 0;
	public static int STORE_PERM1 = 1;
	public static int STORE_PERM2 = 2;
	
	private static final int COUNT = 3;

	private int [] lastAction;
	private int [] p1, p2, p3, p4, q, scale;
	private Image pImage;
	
	public void repaint() {
		g.setPaintMode();
		for(int i=0; i < COUNT; i++) {
			// on scale change, a repaint is called, so 
			repaintXOR(i);
		}
		g.setXORMode(Color.white);
	}
	
	public void setGraphics(Graphics g) {
		this.g = g;
	}
	
	public OverlayGraphics(Graphics g, Info info) {
		this.g = g;
		this.info = info;
		
		lastAction = new int[COUNT];
		p1 = new int[COUNT];
		p2 = new int[COUNT];
		p3 = new int[COUNT];
		p4 = new int[COUNT];	
		q = new int [COUNT];
		scale = new int[COUNT];
		
				
		for(int i=0; i < COUNT; i++) {
			lastAction[i] = ACTION_NONE; 			
		}
		
	}
	
	public void clearEchoXOR() {		
		clearEchoXOR(STORE_DEFAULT);		
	}
	
	private void repaintXOR(int storeID) {
		switch(lastAction[storeID]) {			
			case ACTION_NONE:			
				break;				
			case ACTION_DRAWLINE:
				undoDrawLine(p1[storeID],p2[storeID],p3[storeID],p4[storeID], q[storeID], scale[storeID]);
				break;
			case ACTION_DRAWRECT:
				undoDrawRect(p1[storeID],p2[storeID],p3[storeID],p4[storeID], q[storeID], scale[storeID]);
				break;
			case ACTION_FILLRECT:
				undoFillRect(p1[storeID],p2[storeID],p3[storeID],p4[storeID], q[storeID], scale[storeID]);
				break;
			case ACTION_DRAWOVAL:
				undoDrawOval(p1[storeID],p2[storeID],p3[storeID],p4[storeID], q[storeID], scale[storeID]);
				break;				
			case ACTION_DRAWIMAGE:
				undoDrawImage(pImage, p1[storeID],p2[storeID],p3[storeID],p4[storeID], q[storeID], scale[storeID]);
				break;
		}
	}
	
	public void clearEchoXOR(int storeID) {		
		repaintXOR(storeID);
		lastAction[storeID] = ACTION_NONE;

	}
	


	@Override
	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
		
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g.copyArea(x, y, width, height, dx, dy);
	}

	@Override
	public Graphics create() {
		return g.create();
	}

	@Override
	public void drawRect(final int x,final  int y, final int width, final int height) {
		drawRect2(x,y,width,height,STORE_DEFAULT);
	}
	
	public void drawRect2(int x, int y, int width, int height, int storageID) {
		g.drawRect(x, y, width, height);
		lastAction[storageID] = ACTION_DRAWRECT;		
		p1[storageID] = x;
		p2[storageID] = y;
		p3[storageID] = width;
		p4[storageID] = height;
		q[storageID] = info.Q;
		scale[storageID] = info.scale;		
	}
		
	private void undoDrawRect(int x, int y, int width, int height, int q, int scale) {
		if(info.Q != q || info.scale != scale) {
			x = info.translateX(x, q, scale);
			y = info.translateY(y, q, scale);
			width = info.translateSize(width, q, scale);
			height = info.translateSize(height, q, scale);			
		}
		
		g.drawRect(x, y, width, height);
	}
	

	
	public void drawRectPerm(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return g.drawImage(img, x, y, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x, y, bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		return g.drawImage(img, x, y, width, height, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x, y, width, height, bgcolor, observer);
	}
	
	public boolean drawImage2(Image img, int x, int y, int width, int height) {
		int storageID = STORE_PERM2;
		lastAction[storageID] = ACTION_DRAWIMAGE;
		p1[storageID] = x;
		p2[storageID] = y;
		p3[storageID] = width;
		p4[storageID] = height;
		q[storageID] = info.Q;
		scale[storageID] = info.scale;	
		pImage = img;
				
		setPaintMode();
		boolean ret = g.drawImage(img, x, y, width, height, Color.white, null);
		setXORMode(Color.white);
		return ret;
	}
	
	public void removeImage() {
		int storageID = STORE_PERM2;
		lastAction[storageID] = ACTION_NONE;
		int x = p1[storageID];
		int y = p2[storageID];
		int width = p3[storageID];
		int height = p4[storageID];
		int q = this.q[storageID];
		int scale = this.scale[storageID];
		
		if(info.Q != q || info.scale != scale) {
			x = info.translateX(x, q, scale);
			y = info.translateY(y, q, scale);
			width = info.translateSize(width, q, scale);
			height = info.translateSize(height, q, scale);			
		}
		
		
		g.clearRect(x, y, width-1, height-1);
	}
	
	private void undoDrawImage(Image img, int x, int y, int width, int height, int q, int scale) {
		if(info.Q != q || info.scale != scale) {
			x = info.translateX(x, q, scale);
			y = info.translateY(y, q, scale);
			width = info.translateSize(width, q, scale);
			height = info.translateSize(height, q, scale);			
		}
		
		g.clearRect(x, y, width-1, height-1);
		setPaintMode();
		g.drawImage(img, x, y, width, height, null);
		setXORMode(Color.white);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		drawLine2(x1, y1, x2, y2, STORE_DEFAULT);
	}

	public void drawLine2(int x1, int y1, int x2, int y2, int storageID) {
		g.drawLine(x1, y1, x2, y2);
		lastAction[storageID] = ACTION_DRAWLINE;
		p1[storageID] = x1;
		p2[storageID] = y1;
		p3[storageID] = x2;
		p4[storageID] = y2;
		q[storageID] = info.Q;
		scale[storageID] = info.scale;	
	}
	
	private void undoDrawLine(int x1, int y1, int x2, int y2, int q, int scale) {
		if(info.Q != q || info.scale != scale) {
			x1 = info.translateX(x1, q, scale);
			y1 = info.translateY(y1, q, scale);
			x2 = info.translateX(x2, q, scale);
			y2 = info.translateY(y2, q, scale);						
		}
		
		g.drawLine(x1, y1, x2, y2);
	}
	
	@Override
	public void drawOval(final int x, final int y, final int width, final int height) {
		drawOval2(x, y, width,height, STORE_DEFAULT);
	}
	
	public void drawOval2(int x, int y, int width, int height, int storageID) {
		g.drawOval(x, y, width, height);		
		lastAction[storageID] = ACTION_DRAWOVAL;
		p1[storageID] = x;
		p2[storageID] = y;
		p3[storageID] = width;
		p4[storageID] = height;
		q[storageID] = info.Q;
		scale[storageID] = info.scale;		
	}
	
	private void undoDrawOval(int x, int y, int width, int height, int q, int scale) {
		if(info.Q != q || info.scale != scale) {
			x = info.translateX(x, q, scale);
			y = info.translateY(y, q, scale);
			width = info.translateSize(width, q, scale);
			height = info.translateSize(height, q, scale);			
		}		
		
		g.drawOval(x, y, width, height);	
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolyline(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g.drawString(iterator, x, y);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void fillRect(final int x, final int y, final int width, final int height) {
		fillRect2(x,y,width,height,STORE_DEFAULT);
	}
	
	public void fillRect2(int x, int y, int width, int height, int storageID) {
		g.fillRect(x, y, width, height);
		lastAction[storageID] = ACTION_FILLRECT;
		p1[storageID] = x;
		p2[storageID] = y;
		p3[storageID] = width;
		p4[storageID] = height;
		q[storageID] = info.Q;
		scale[storageID] = info.scale;	
			
	}
	
	private void undoFillRect(int x, int y, int width, int height, int q, int scale) {		
		if(info.Q != q || info.scale != scale) {
			x = info.translateX(x, q, scale);
			y = info.translateY(y, q, scale);
			width = info.translateSize(width, q, scale);
			height = info.translateSize(height, q, scale);			
		}
		
		g.fillRect(x, y, width, height);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public Font getFont() {
		return g.getFont();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	@Override
	public void setClip(Shape clip) {
		g.setClip(clip);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
	}

	@Override
	public void setColor(Color c) {
		g.setColor(c);
	}

	@Override
	public void setFont(Font font) {
		g.setFont(font);	
	}

	@Override
	public void setPaintMode() {
		g.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}

	@Override
	public void translate(int x, int y) {
		g.translate(x, y);
	}

}
