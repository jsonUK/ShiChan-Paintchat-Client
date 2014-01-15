package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.swing.JPanel;

import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiPanel;

public class ColorWheel extends SaiPanel implements ColorListener {	
	private static final float COLOR_WHEEL_MIN = 0.75F;	
	
	private static final int DRAW_WHEEL = 0x1;
	private static final int DRAW_SB = 0x2;
	private static final int DRAW_ALL = DRAW_SB | DRAW_WHEEL;
	
	// buffered image segments
	private static final int SEG_LEFT = 0;
	private static final int SEG_RIGHT = 1;
	private static final int SEG_TOP = 2;
	private static final int SEG_BOT = 3;
	
	private ColorToolManager parent;
	private BufferedImage imColorWheel;	
	private BufferedImage imColorWheelSB;
	private BufferedImage [] imColorWheelSegs;

	private Rectangle [] rColorWheelSegs;
	private Rectangle rColorWheel;
	private Rectangle rColorWheelSB;	
	private float [] fBuffer;			
	
	// points for where the highlight circle should be
	private Point pSelectColorWheel;
	private Point pSelectColorWheelSB;
	private int rMid;
	
	private DragState dragState;
	
	public ColorWheel() {
		
	}
	
	protected void init(ColorToolManager parent, int width) {
		this.parent = parent;
		setSizes(new Dimension(width,width));
		
		fBuffer = new float[3];		
		pSelectColorWheel = new Point();
		pSelectColorWheelSB = new Point();
		dragState = new DragState();
				
		// calculate radius of wheel
		int rMax = width/ 2;
		int rMin = (int) ((float) rMax * COLOR_WHEEL_MIN);
		rMid = (rMax + rMin) / 2;
		createColorWheel(rMax, rMin);
		rColorWheel = new ColorWheelCircle(0, 0, rMin*2, rMax*2);
		
		// calculate size of SB slider
		float sqrt2 =  0.7071067811865476F;
		int colorWheelHBSize = (int)((float)rMin * sqrt2 * 2);
		createColorWheelSB(colorWheelHBSize);
		// inital point of SB slider
		int offset = rMax - (int)((float)rMin * sqrt2);
		rColorWheelSB = new Rectangle(rColorWheel.x + offset, rColorWheel.y + offset, colorWheelHBSize, colorWheelHBSize);
				
		// create segments to draw color wheel
		imColorWheelSegs = new BufferedImage[4];
		rColorWheelSegs = new Rectangle[4];
		
		imColorWheelSegs[SEG_LEFT] 	= imColorWheel.getSubimage(0, 0, rColorWheelSB.x, rColorWheel.height);
		rColorWheelSegs[SEG_LEFT] 	= new Rectangle(0, 0, rColorWheelSB.x, rColorWheel.height);
		
		imColorWheelSegs[SEG_RIGHT] = imColorWheel.getSubimage(rColorWheelSB.x + rColorWheelSB.width, 0, rColorWheel.width - (rColorWheelSB.x + rColorWheelSB.width), rColorWheel.height);
		rColorWheelSegs[SEG_RIGHT] 	= new Rectangle(rColorWheelSB.x + rColorWheelSB.width, 0, rColorWheel.width - (rColorWheelSB.x + rColorWheelSB.width), rColorWheel.height);
		
		imColorWheelSegs[SEG_TOP] 	= imColorWheel.getSubimage(rColorWheelSB.x, 0, rColorWheelSB.width, rColorWheelSB.y);
		rColorWheelSegs[SEG_TOP] 	= new Rectangle(rColorWheelSB.x, 0, rColorWheelSB.width, rColorWheelSB.y);
		
		imColorWheelSegs[SEG_BOT] 	= imColorWheel.getSubimage(rColorWheelSB.x, rColorWheelSB.y + rColorWheelSB.height, rColorWheelSB.width, rColorWheel.height - (rColorWheelSB.y + rColorWheelSB.height));
		rColorWheelSegs[SEG_BOT] 	= new Rectangle(rColorWheelSB.x, rColorWheelSB.y + rColorWheelSB.height, rColorWheelSB.width, rColorWheel.height - (rColorWheelSB.y + rColorWheelSB.height));
		
		setPoints(new float[] {0, 0, 0});
		
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}
	
	private final MouseAdapter mouseListener = new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			if(inWheel(e)) {
				dragState.dragOwner = DragState.OWNER_WHEEL;
				moveCWPoint(e);				
			}
			else if(inSB(e)) {
				dragState.dragOwner = DragState.OWNER_SB;
				moveSBPoint(e);				
			}
		}

		public void mouseReleased(MouseEvent e) {
			dragState.dragOwner = DragState.OWNER_NONE;
		}
		
		public void mouseDragged(MouseEvent e) {
			if(dragState.isSB()) {
				moveSBPoint(e);
			}
			else if(dragState.isWheel()) {
				moveCWPoint(e);
			}
			
		}
		
	};


	public void paint2(Graphics g, int drawTarget) {		
		Graphics2D g2 = (Graphics2D) g;		
		
		// draw 4 chunks for the wheel to surround
		if((drawTarget & DRAW_WHEEL) != 0) {
			// left
			//g2.drawImage(imColorWheel, rColorWheel.x, rColorWheel.y, rColorWheelSB.x - rColorWheel.x, rColorWheel.height, Color.white, this);
			for(int i=0; i < 4; i++) {
				g.drawImage(imColorWheelSegs[i], rColorWheelSegs[i].x, rColorWheelSegs[i].y, Color.white, this); 
			}
			g2.drawOval(pSelectColorWheel.x-4, pSelectColorWheel.y-4, 8, 8);
		}
		if((drawTarget & DRAW_SB) != 0) {
			g2.drawImage(imColorWheelSB,rColorWheelSB.x, rColorWheelSB.y, Color.white, this);				
			g2.drawOval(pSelectColorWheelSB.x-4, pSelectColorWheelSB.y-4, 8, 8);
		}		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		paint2(g, DRAW_ALL);
	}
	
	public void fastPaint(int drawTarget) {
		Graphics g = getGraphics();
		paint2(g, drawTarget);
		g.dispose();
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	private boolean inWheel(MouseEvent e) {		
		return rColorWheel.contains(e.getX(), e.getY());
	}
	
	private boolean inSB(MouseEvent e) {
		return rColorWheelSB.contains(e.getX(), e.getY());
	}
	
	/**
	 * Convert the HSB coordinates into points for the color
	 * @param hsb
	 */
	private void setPoints(float [] hsb) {
		pSelectColorWheelSB.x = rColorWheelSB.x + (int)((1 - hsb[1]) * rColorWheelSB.width);
		pSelectColorWheelSB.y = rColorWheelSB.y + (int)(hsb[2] * rColorWheelSB.width);
		
		int halfWidth = rColorWheel.width / 2;  
		pSelectColorWheel.x = rColorWheel.x + halfWidth + (int)(rMid * Math.cos(2 * Math.PI * hsb[0]));
		pSelectColorWheel.y = rColorWheel.y + halfWidth - (int)(rMid * Math.sin(2 * Math.PI * hsb[0]));
	}
	
	/**
	 * Get point, do this after we have limitted bounds
	 * @return
	 */
	private float [] getPoints() {		
		
		int xWheel = pSelectColorWheel.x - rColorWheel.x;
		int yWheel = pSelectColorWheel.y - rColorWheel.y;		
		
		
		
		 int rgbWheel = imColorWheel.getRGB(xWheel, yWheel);
		 Color c = new Color(rgbWheel);
		 Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), fBuffer);
		 
		 float sat = 1F - (float)(pSelectColorWheelSB.x - rColorWheelSB.x) / (float)rColorWheelSB.width;
		 float bri = (float)(pSelectColorWheelSB.y - rColorWheelSB.y) / (float)rColorWheelSB.height;		 
		 // merge into 1 and return it
		 fBuffer[1] = sat;
		 fBuffer[2] = bri;
		 
		 //System.out.println(fBuffer[0] + ","+fBuffer[1]+","+fBuffer[2]);
		 return fBuffer;
	}
	
	/**
	 * Handle boundries of moving the SB point
	 * @param e
	 */
	private void moveSBPoint(MouseEvent e) {
		int x  = e.getX();
		int y = e.getY();
		
		x = x < rColorWheelSB.x? rColorWheelSB.x : x;
		x = x > (rColorWheelSB.x + rColorWheelSB.width)? (rColorWheelSB.x + rColorWheelSB.width) : x;
		y = y < rColorWheelSB.y? rColorWheelSB.y : y;
		y = y > (rColorWheelSB.y + rColorWheelSB.height)? (rColorWheelSB.y + rColorWheelSB.height) : y;
		
		pSelectColorWheelSB.x = x;
		pSelectColorWheelSB.y = y;
		
		parent.fireColorEvent(getPoints(), this);
		fastPaint(DRAW_ALL);
	}
	
	private void moveCWPoint(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		// calculate distance multiply sin and cos
		int midX = (rColorWheel.x + (rColorWheel.width / 2));
		int midY = (rColorWheel.y + (rColorWheel.height / 2));
		int deltaX = x - midX;
		int deltaY = y - midY;
		
		double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		
		 pSelectColorWheel.x = midX + (int)((double)deltaX * (double)rMid / dist);
		 pSelectColorWheel.y = midY + (int)((double)deltaY * (double)rMid / dist);
		
		 float [] hsb = getPoints();
		 parent.fireColorEvent(hsb, this);
		 updateWheelSB(hsb[0]);
		 fastPaint(DRAW_ALL);
	}
	
	/**
	 * 
	 * @param colorWheelSize the size of the color wheel to determine the size of the inside
	 * @return
	 */
	private void createColorWheelSB(int size) {		
		imColorWheelSB = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);		
		updateWheelSB(0);
	}
	
	/**
	 * Redraw the SBwheel
	 */
	private void updateWheelSB(float hue) {
		int size = imColorWheelSB.getWidth();
		DataBuffer db = imColorWheelSB.getRaster().getDataBuffer();
		int lineThickness = 1;
		int linePadding = 1;
		int lineTest = lineThickness + linePadding;
		
		int bufferI = 0;
		for(int y = 0; y < size; y++) {
			for(int x= 0; x < size; x++) {
				if(x < lineThickness || x >= size - lineThickness ||
					y < lineThickness || y >= size - lineThickness) {
					db.setElem(bufferI, Tools.I_LINE_COLOR);				
				}
				else if(x >= lineTest && y >= lineTest &&
						x < size - lineTest && y < size - lineTest) {
					float sat = 1.F - ((float)x /(float)(size));
					float brightness = (float)y /(float)(size);
					db.setElem(bufferI, Color.HSBtoRGB(hue, sat, brightness));
				}
				else
				{
					db.setElem(bufferI, Tools.I_BACKGROUND_COLOR);
				}
				bufferI++;
			}
		}
	}
	
	/**
	 * Create the color wheel that has the HUE ring
	 * @param size
	 * @return
	 */
	private void createColorWheel(int rMax, int rMin) {
		final int ANTI_RATIO = 2;
		int size = rMax * 2;
		int tmpSize = size * ANTI_RATIO;		
		BufferedImage im = new BufferedImage(tmpSize, tmpSize, BufferedImage.TYPE_INT_RGB);		
		DataBuffer db = im.getRaster().getDataBuffer();		
		
		int midpoint = rMax * ANTI_RATIO;		
		int linePadding = 2;
		int lineWidth = 1;
		
		// radius line outer max
		int rloMax = rMax * rMax * ANTI_RATIO * ANTI_RATIO;
		int rloMin = (rMax - lineWidth) * (rMax - lineWidth) * ANTI_RATIO * ANTI_RATIO;
		int rcMax = (rMax - (lineWidth + linePadding)) * (rMax - (lineWidth + linePadding)) * ANTI_RATIO * ANTI_RATIO;
		int rcMin = (rMin + lineWidth + linePadding) * (rMin + lineWidth + linePadding) * ANTI_RATIO * ANTI_RATIO;
		int rliMax = (rMin + linePadding) * (rMin + linePadding) * ANTI_RATIO * ANTI_RATIO;
		int rliMin = rMin * rMin * ANTI_RATIO * ANTI_RATIO;			
		
		int bufferI = 0;
		for(int y = -midpoint; y < midpoint; y++) {		
			for(int x = -midpoint; x < midpoint; x++) {
				int curR = (x * x) + (y * y); 
				// check if inside color wheel				
				if(rcMin < curR && curR <= rcMax) {					
					double rad = Math.asin(Math.abs((double)y) /(double)Math.sqrt(curR));
					// assume quad 1 
					// quad 2 test
					if(x < 0 && y < 0) {
						rad = Math.PI - rad;
					}
					else if(x < 0 && y >= 0) {
						rad = Math.PI + rad;
					}
					else if(x >= 0 && y >= 0) {
						rad = (2 * Math.PI) - rad; 
					}
					db.setElem(bufferI, Color.HSBtoRGB((float)(rad / (2D * Math.PI)), 1F, 1F));
				}		
				// draw ring
				else if((rloMax > curR && rloMin < curR) ||
						(rliMax > curR && rliMin < curR)) {
					db.setElem(bufferI, Tools.I_LINE_COLOR);
				}
				else {
					db.setElem(bufferI, Tools.I_BACKGROUND_COLOR);
				}
				
				bufferI++;
			}
		}
		
		Graphics2D g2 = im.createGraphics();
		BufferedImage bf = new BufferedImage(size,size, BufferedImage.TYPE_INT_BGR);
		g2 = bf.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(im, AffineTransform.getScaleInstance(1/2D, 1/2D), null);
		g2.dispose();
		imColorWheel = bf;
	}

	public void colorChanged(int rgb, float[] hsb, ColorListener source) {
		if(source != this) {
			updateWheelSB(hsb[0]);
			setPoints(hsb);
			repaint();
		}
	}
	
	class ColorWheelCircle extends Rectangle {
		private Ellipse2D ellipseOuter;
		private Ellipse2D ellipseInner;
		
		
		/**
		 * start x
		 * @param x
		 * @param y
		 * @param dMin diameter min
		 * @param dMax diameter max
		 */
		public ColorWheelCircle(int x, int y, int dMin, int dMax) {
			super(x,y,dMax,dMax);
			
			//Outer
			ellipseOuter = new Ellipse2D.Double(x, y, dMax, dMax);
			ellipseInner = new Ellipse2D.Double(x + (dMax - dMin)/2, y + (dMax - dMin)/2, dMin, dMin);
		}
		
		public boolean contains(int x, int y) {
			return ellipseOuter.contains(x,y) && !ellipseInner.contains(x,y);
		}
	}
	
	private static class DragState {
		private final static int OWNER_NONE = -1;
		private final static int OWNER_WHEEL = 0;
		private final static int OWNER_SB = 1;
		
		int dragOwner;
		boolean drag;
		
		DragState() {
			dragOwner = -1;
			drag = false;
		}
		
		boolean isWheel() {
			return dragOwner == OWNER_WHEEL;
		}
		
		boolean isSB() {
			return dragOwner == OWNER_SB;
		}		
	}

	@Override
	public void paint2(Graphics2D g) {
		paint2(g, DRAW_ALL);
		
	}
}
