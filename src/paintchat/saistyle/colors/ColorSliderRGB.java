package paintchat.saistyle.colors;

import java.awt.image.DataBuffer;

public class ColorSliderRGB extends ColorSlider {

	public static int SLIDER_RED = 16;
	public static int SLIDER_GREEN = 8;
	public static int SLIDER_BLUE = 0;
	
	private int shiftMask;
	private int colorInt = 0;
	
	public ColorSliderRGB(int sliderColor) {
		super(sliderColor == SLIDER_RED? "R" : sliderColor == SLIDER_GREEN? "G" : "B");
		this.shiftMask = sliderColor;
	}
	
	@Override
	public void updateColorSlider(DataBuffer buffer, int width, int height) {
		int rgb = parent.getRGB();
		int bufferI = 0;
		float xStepSize = 255F/(float)width;
		
		int mask = 0xFFFFFFFF - (0xFF << shiftMask);
		int emptyRGB = rgb & mask;
		
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				// extract the 
				int color = emptyRGB | ((int)(x * xStepSize) << shiftMask);
				buffer.setElem(bufferI, color);
				bufferI++;
			}
		}
	}

	@Override
	public void setValue(int rgb, float[] hsb) {		
		colorInt  = ((0xFF << shiftMask) & rgb) >>> shiftMask ;
	}

	@Override
	public String getDisplayValue() {
		// TODO Auto-generated method stub
		return "" + colorInt;
	}

	@Override
	public float getPerc() {
		return (float)colorInt / 255F;
	}

	@Override
	public void actionValueSet(float perc) {
		colorInt = (int) (255F * perc);
		int mask = 0xFFFFFFFF - (0xFF << shiftMask); 
		int rgb = mask & parent.getRGB();
		parent.fireColorEvent(rgb | (colorInt << shiftMask), this);
	}
	
}
