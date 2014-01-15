package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Panel;

import paintchat.Mg.Info;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiCheckbox;
import paintchat.saistyle.components.SaiCheckboxListener;
import paintchat.saistyle.components.VerticalLayout;

public class ColorToolManager extends Panel implements SaiCheckboxListener {
	
	private int curARGB;
	private float [] curHSB;
	private float [] tmpHSB;
	private Color curColor;
	private ColorWheel colorWheel;
	private CurrentColorPanel currentPanel;
	private Panel rgbComponent;
	private Panel hsbComponent;
	
	// all toggled components will appear in this panel
	private Panel toggledComponentPanel; 
	
	private ColorSlider [] rgbSliders;	
	private ColorSlider [] hsbSliders;
	private ColorSwatches colorSwatches;	
	private ToggleCheckboxPair [] colorToggles;	
	private Info info;
	
	private static final int TOGGLE_WHEEL = 0;
	private static final int TOGGLE_RGB = 1;
	private static final int TOGGLE_HSB = 2;
	private static final int TOGGLE_SQUARES = 3;
	
	
	public ColorToolManager() {
		curHSB = new float[3];
		tmpHSB = new float[3];
		rgbSliders = new ColorSlider[3];
		hsbSliders = new ColorSlider[3];		
		curColor = Color.BLACK;
	}
	
	public void init(Tools parent, int parentWidth) {				
		info = parent.getInfo();
		colorWheel = new ColorWheel();		
		currentPanel = new CurrentColorPanel(this);		
		colorSwatches = new ColorSwatches();		
		
		rgbSliders[0] = new ColorSliderRGB(ColorSliderRGB.SLIDER_RED);
		rgbSliders[1] = new ColorSliderRGB(ColorSliderRGB.SLIDER_GREEN);
		rgbSliders[2] = new ColorSliderRGB(ColorSliderRGB.SLIDER_BLUE);
		
		hsbSliders[0] = new ColorSliderHSB(ColorSliderHSB.SLIDER_HUE);
		hsbSliders[1] = new ColorSliderHSB(ColorSliderHSB.SLIDER_SAT);
		hsbSliders[2] = new ColorSliderHSB(ColorSliderHSB.SLIDER_BRI);					
		
		// next item is the list of toggled items		
		
		rgbComponent = new Panel(new VerticalLayout(0));
		for(int i=0; i < 3; i++) {
			rgbSliders[i].init(this,parentWidth);			
			rgbComponent.add(rgbSliders[i]);
		}		
		
		hsbComponent = new Panel(new VerticalLayout(0));
		for(int i=0; i < 3; i++) {
			hsbSliders[i].init(this, parentWidth);			
			hsbComponent.add(hsbSliders[i]);
		}
		
		colorToggles = new ToggleCheckboxPair[4];
		colorToggles[TOGGLE_WHEEL] = createPair("toggle_wheel", colorWheel);		
		colorToggles[TOGGLE_RGB] = createPair("toggle_rgb", rgbComponent);	
		colorToggles[TOGGLE_HSB] = createPair("toggle_hsb", hsbComponent);		
		colorToggles[TOGGLE_SQUARES] = createPair("toggle_swatches", colorSwatches);		
		
		Panel togglePanel = new Panel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		togglePanel.setBackground(Tools.C_BACKGROUND);
		togglePanel.add(colorToggles[TOGGLE_WHEEL].checkbox);
		togglePanel.add(colorToggles[TOGGLE_RGB].checkbox);
		togglePanel.add(colorToggles[TOGGLE_HSB].checkbox);
		togglePanel.add(colorToggles[TOGGLE_SQUARES].checkbox);
		
		toggledComponentPanel = new Panel(new VerticalLayout(2));
		toggledComponentPanel.add(colorToggles[TOGGLE_WHEEL].colorComponent);
		toggledComponentPanel.add(colorToggles[TOGGLE_RGB].colorComponent);
		toggledComponentPanel.add(colorToggles[TOGGLE_HSB].colorComponent);		
		toggledComponentPanel.add(colorToggles[TOGGLE_SQUARES].colorComponent);							
		
		// add components to main this panel
		setLayout(new VerticalLayout(2));		
		add(togglePanel);
		add(toggledComponentPanel);				
		
		// initialize components
		colorSwatches.init(this, parentWidth);
		colorWheel.init(this, parentWidth);
		
		// disable the sliders
		toggleComponent(TOGGLE_RGB, false);
		toggleComponent(TOGGLE_HSB, false);
		
		setBackground(Tools.C_BACKGROUND);		
	}
	
	private ToggleCheckboxPair createPair(String toggleIcon, Component c) {
		ColorToggleCheckbox cb = new ColorToggleCheckbox(toggleIcon);
		cb.setChecked(true);
		cb.addCheckboxListener(this);
		
		return new ToggleCheckboxPair(cb, c);
	}
	
	public CurrentColorPanel getCurrentColorComponent() {
		return currentPanel;
	}
	
	protected void setSizes(Dimension d) {
		setSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
	}
	
	public int getRGB() {
		return curARGB;
	}
	
	public Color getColor() {
		return curColor;
	}
	
	public float [] getHSB() {
		return curHSB;
	}	
	
	public void paint(Graphics g) {
//		super.paint(g);
		//colorWheel.paint(g);
	}
	
	public void setColor(int color) {
		fireColorEvent(color, null);
	}
	
	protected void fireColorEvent(float [] newHSB, ColorListener source) {
		int newRGB = Color.HSBtoRGB(newHSB[0], newHSB[1], newHSB[2]);
		fireColorEvent(newRGB, newHSB, source);
	}
	
	protected void fireColorEvent(int newRGB, ColorListener source) {
		Color c = new Color(newRGB);
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), tmpHSB);
		fireColorEvent(newRGB, tmpHSB, source);
	}
	
	protected void fireColorEvent(Color newColor, ColorListener source) {
		int newRGB = newColor.getRGB();
		Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), tmpHSB);
		fireColorEvent(newRGB, tmpHSB, source);
	}

	/**
	 * Fire event
	 * @param rgb
	 * @param hsb
	 * @param source
	 */
	private void fireColorEvent(int rgb, float [] hsb, ColorListener source) {
		info.m.iColor = rgb;
		curARGB = rgb;		
		curHSB[0] = hsb[0];
		curHSB[1] = hsb[1];
		curHSB[2] = hsb[2];
		curColor = new Color(rgb);
		
		colorWheel.colorChanged(rgb, hsb, source);
		currentPanel.colorChanged(rgb, hsb, source);
		for(int i=0; i < 3; i++) {
			rgbSliders[i].colorChanged(rgb, hsb, source);
		}
		
		for(int i=0; i < 3; i++) {
			hsbSliders[i].colorChanged(rgb, hsb, source);
		}
	}	

	public void actionChecked(SaiCheckbox source) {				
		updateToggleComponents();
	}
	
	public void toggleComponent(int compID, boolean visible) {
		colorToggles[compID].checkbox.setChecked(visible);
		updateToggleComponents();
	}
	
	private void updateToggleComponents() {
		toggledComponentPanel.removeAll();
		for(int i=0; i < colorToggles.length; i++) {
			if(colorToggles[i].checkbox.isChecked()) {			
				toggledComponentPanel.add(colorToggles[i].colorComponent);				
			}			
		}		
				
		if(getParent() != null) {
			this.getParent().validate();	
		}
	}
	
	private class ToggleCheckboxPair {
		final Component colorComponent;
		final ColorToggleCheckbox checkbox;
		
		ToggleCheckboxPair(ColorToggleCheckbox cb, Component c) {
			checkbox = cb;
			colorComponent = c;
		}
	}
}
