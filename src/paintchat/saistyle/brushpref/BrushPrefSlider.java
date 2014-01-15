package paintchat.saistyle.brushpref;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import paintchat.Mg.Info;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiPanel;

public abstract class BrushPrefSlider extends SaiPanel {
	

	private boolean enabled;
	private boolean drag;
	private boolean hover;		
	
	private ArrayList<PrefSliderListener> listeners;
	
	public static final int SLIDER_HEIGHT = 19;		
	
	public BrushPrefSlider() {
		drag = false;	
		enabled = true;
		listeners = new ArrayList<PrefSliderListener>();
	}
	
	public void init(int width) {
		setSizes(new Dimension(width, SLIDER_HEIGHT));
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}
	
	public void addSliderListener(PrefSliderListener l) {
		listeners.add(l);		
	}
	
	public void removeSliderListener(PrefSliderListener l) {
		listeners.remove(l);
	}
	
	protected void fireListenerEvent() {
		for(PrefSliderListener l : listeners) {
			l.sliderAction(this);
		}
	}
	
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
	
	@Override
	public void paint2(Graphics2D g) {
		int width = getWidth() - 1;
		int height = getHeight() - 1;

		g.setColor(enabled? (isHover()? Tools.C_LINE_HIGHLIGHT : Tools.C_LINE) : Tools.C_DISABLED_TOOL_LINE);
		g.drawRoundRect(0, 0, width , height, 3, 3);
		
		g.setColor(enabled? Tools.C_TOOL_BACKGROUND : Tools.C_DISABLED_TOOL_BACKGROUND);
		g.drawRoundRect(1, 1, width - 2, height - 2, 3, 3);
				
		int sliderWidth = (int)(getPerc() * (float)(width - 4));
		if(sliderWidth > 0) {
			g.setColor(enabled? Tools.C_LINE_DIVIDER : Tools.C_DISABLED_TOOL_FILL);
			g.fillRect(2, 2, sliderWidth, height -4);
		}
		
		if(sliderWidth < width - 4) {
			g.setColor(enabled? Tools.C_TOOL_BACKGROUND : Tools.C_DISABLED_TOOL_BACKGROUND);
			g.fillRect(2 + sliderWidth, 2, width - (4 + sliderWidth), height -4);
		}
		
		// draw string inside
		g.setFont(Tools.F_LABEL_FONT);
		g.setColor(enabled? (isHover()? Tools.C_FONT_HOVER : Tools.C_FONT) : Tools.C_DISABLED_TOOL_FONT);
		String text = getDisplayValue();
		Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
		g.drawString(text, (int)(width - (5 + rect.getWidth())), height - 5);	
	}

	private final MouseAdapter mouseListener = new MouseAdapter() {

		@Override
		public void mouseDragged(MouseEvent e) {			
			updateValue(e);
			fastPaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			hover = true;
			fastPaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {			
			hover = false;
			fastPaint();			
		}	
		
		public void mousePressed(MouseEvent e) {
			drag = true;
			updateValue(e);
			fastPaint();
		}
				
		public void mouseReleased(MouseEvent e) {
			drag = false;
			fastPaint();
		}
	};
	
	private boolean isHover() {
		return drag || hover;
	}
	
	/**
	 * Set the percent of this object, should be between 0 - 1
	 * @param perc
	 */
	public void setPerc(float perc) {
		perc = perc < 0F? 0F : perc > 1F? 1F : perc;		
		actionValueSet(perc);
		fastPaint();
	}
	
	private void updateValue(MouseEvent e) {
		if(enabled) {
			float perc = (float)(e.getX() - 2) / (float)(getWidth() - 4);
			perc = perc < 0F? 0F : perc > 1F? 1F : perc;		
			actionValueSet(perc);			
			fireListenerEvent();
		}
	}
	
	/**
	 * Set this slider to be enabled
	 */
	public void setEnabled(boolean enable) {
		enabled = enable;
		fastPaint();
	}
	
	/**
	 * set the slider percent here
	 * @param perc
	 */
	public void setSlider(float perc) {
		perc = perc < 0F? 0F : perc > 1F? 1F : perc;
		actionValueSet(perc);
		fastPaint();
	}
	
	public static class PercentSlider extends BrushPrefSlider {

		int val = 50;
		
		public PercentSlider() {
			super();
			init(100);
		}
		
		@Override
		public void actionValueSet(float perc) {
			val = (int) (perc * 100F);
		}

		@Override
		public String getDisplayValue() {
			return "" + val;
		}

		@Override
		public float getPerc() {
			return (float)val / 100F;
		}		
	}		
	
	public static class TestSlider extends BrushPrefSlider {

		int val = 50;
		
		public TestSlider() {
			super();
			init(100);
		}
		
		@Override
		public void actionValueSet(float perc) {
			val = (int) (perc * 100F);
		}

		@Override
		public String getDisplayValue() {
			return "" + val;
		}

		@Override
		public float getPerc() {
			return (float)val / 100F;
		}
		
	}
}
