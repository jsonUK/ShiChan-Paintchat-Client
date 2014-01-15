package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiPanel;

public abstract class ColorSlider extends SaiPanel implements ColorListener {

	private static final int SLIDER_THICKNESS = 12;
	private static final int SLIDER_ARROW_SIZE = 5;	
	
	private BufferedImage imColorSlider;
	private Rectangle rColorSlider;
	private Rectangle rSliderBounds;
	
	private String label;	
	private SliderPoint sliderPoint;
	
	private boolean drag;
	
	protected ColorToolManager parent;
	private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 10); 
	
	public ColorSlider(String label) {
		this.label = label;
		drag = false;		
	}
	
	public void init(ColorToolManager parent, int width) {
		Dimension d = new Dimension(width, 25);
		setSizes(d);			
		this.parent = parent;
		sliderPoint = new SliderPoint();
		createSliderBuffer();		
		setValue(parent.getRGB(), parent.getHSB());
		updateColorSlider();
		updateSliderPoint();
		
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}
	
	public void paint2(Graphics2D g) {
		// paint left
		int height = getHeight();
		int width = getWidth();
						
		Color oldColor = g.getColor();
		g.setColor(Tools.C_BACKGROUND);		
		
		// fill in around the slider
		g.fillRect(0, 0, rColorSlider.x, height);
		//Rectangle r = new Rectangle(rColorSlider.x, (int) rColorSlider.getMaxY(), rColorSlider.width, height - (int)rColorSlider.getMaxY());		
		g.fillRect((int) rColorSlider.getMaxX(), 0, width - (int) rColorSlider.getMaxX(), height);		
		g.fillRect(rColorSlider.x, (int) rColorSlider.getMaxY(), rColorSlider.width, height - (int)rColorSlider.getMaxY());
		
		g.setColor(Tools.C_FONT);
		g.setFont(LABEL_FONT);
		g.drawString(label, 2, 12);
		g.drawString("" + getDisplayValue(), 2 + (int) rColorSlider.getMaxX(), 12);
		// draw bottom slider
		g.setColor((drag)? Tools.C_LINE_HIGHLIGHT : Tools.C_LINE);
		g.drawRoundRect(rColorSlider.x, rColorSlider.y, rColorSlider.width, rColorSlider.height, 2, 2);
		sliderPoint.drawSlider(g);
		
		g.drawImage(imColorSlider, rColorSlider.x + 2, rColorSlider.y + 2, Color.white, this);		 		
		g.setColor(oldColor);
	}
	
	private void createSliderBuffer() {
		//Graphics g = getGraphics();
		int charPadding = 18;				
		
		imColorSlider = new BufferedImage(getWidth() - (charPadding * 2) - 2, SLIDER_THICKNESS - 2, BufferedImage.TYPE_INT_RGB);
		rColorSlider = new Rectangle((int)(charPadding *(0.75)), 2, imColorSlider.getWidth() + 3, imColorSlider.getHeight() + 3);		
		rSliderBounds = new Rectangle(rColorSlider.x, rColorSlider.y, rColorSlider.width, rColorSlider.height + SLIDER_ARROW_SIZE);		
	}
	
	// draw in color buffer
	public abstract void updateColorSlider(DataBuffer buffer, int width, int height);
	
	private void updateColorSlider() {
		// update color slider
		DataBuffer db = imColorSlider.getRaster().getDataBuffer();
		updateColorSlider(db, imColorSlider.getWidth(), imColorSlider.getHeight());
		
		//Graphics g = imColorSlider.getGraphics();
		//g.setColor(Tools.C_LINE);
		//g.drawRect(0, 0, rColorSlider.width, rColorSlider.height);
		//g.setColor(Tools.C_LINE);
//		g.drawRect(1, 1, rColorSlider.width-2, rColorSlider.height-2);		
		//g.dispose();
	}
	
	private void updateSliderPoint() {
		sliderPoint.updatePoint(getPerc());
	}
	
	/**
	 * Set the value of interest
	 * @param rgb
	 * @param hsb
	 */
	public abstract void setValue(int rgb, float [] hsb);
	
	/**
	 * Get the percentage of where the slider should be on this bar
	 * @return
	 */
	public abstract float getPerc();
	
	/**
	 * 
	 * @return return value to display as string
	 */
	public abstract String getDisplayValue();
	
	/**
	 * Set the value of this object to the percentage of this bar
	 * @param perc
	 */
	public abstract void actionValueSet(float perc);
	
	public void colorChanged(int rgb, float[] hsb, ColorListener source) {
		if(source != this) {			
			setValue(rgb, hsb);
			updateColorSlider();			
		}
		updateSliderPoint();
		fastPaint();
	}
	
	class SliderPoint {		
		private int [] xPoints;
		private int [] yPoints;		
		
		SliderPoint(){
			xPoints = new int[4];
			yPoints = new int[4];
		}
		
		void updatePoint(float perc) {			
			int xStart = (int) (perc * (float) (rColorSlider.width - 1)) + rColorSlider.x;		
			int yStart = (int) rColorSlider.getMaxY();
			xPoints[0] = xStart;
			xPoints[1] = xStart - SLIDER_ARROW_SIZE;
			xPoints[2] = xStart + SLIDER_ARROW_SIZE;
			xPoints[3] = xStart;
			
			yPoints[0] = yStart;
			yPoints[1] = yStart + SLIDER_ARROW_SIZE;
			yPoints[2] = yStart + SLIDER_ARROW_SIZE;
			yPoints[3] = yStart;			
		}
		
		void drawSlider(Graphics2D g) {
			g.drawPolyline(xPoints, yPoints, 4);
		}
	}
	
	private final MouseAdapter mouseListener = new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			if(inSlider(e)) {
				drag = true;
				moveSlider(e);
			}
		}
		
		public void mouseDragged(MouseEvent e) {
			if(drag) {
				moveSlider(e);
			}			
		}		
		
		public void mouseReleased(MouseEvent e) {
			drag = false;
			fastPaint();
		}
	};
		
	/**
	 * 
	 * @param e
	 */
	private void moveSlider(MouseEvent e) {
		int x = e.getX();
		
		// bind x within slider area
		x -= rSliderBounds.x;
		x = (x < 0) ? 0 : x;
		x = (x > rSliderBounds.width - 1) ? rSliderBounds.width -1: x;
		
		actionValueSet((float) x / (float)(rSliderBounds.width - 1));
	}
	
	private boolean inSlider(MouseEvent e) {
		return rSliderBounds.contains(e.getX(), e.getY());
	}	
}
