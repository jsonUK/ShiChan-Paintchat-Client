package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.image.DataBuffer;

public class ColorSliderHSB extends ColorSlider {
	
	public static final int SLIDER_HUE = 0;
	public static final int SLIDER_SAT = 1;
	public static final int SLIDER_BRI = 2;
	
	private int hsbIndex;
	private float hsbVal = 0F;
	
	public ColorSliderHSB(int hsbType) {
		super(hsbType == SLIDER_HUE? "H" : hsbType == SLIDER_SAT? "S" : "B");
		hsbIndex = hsbType;
	}
	
	@Override
	public void updateColorSlider(DataBuffer buffer, int width, int height) {
		float [] hsbOrig = parent.getHSB();
		// copy over so we dont use original float pointers
		float [] hsb = new float[3];
		for(int i=0; i < 3; i++) {
			hsb[i] = hsbOrig[i];
		}
		int bufferI = 0;
		float xStepSize = 1F/(float)width;
		
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				// extract the 
				hsb[hsbIndex] = (float)x * xStepSize;
				int color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
				buffer.setElem(bufferI, color);
				bufferI++;
			}
		}
	}

	@Override
	public void setValue(int rgb, float[] hsb) {	
		hsbVal = hsb[hsbIndex];
	}

	@Override
	public String getDisplayValue() {
		// TODO Auto-generated method stub
		return "" + (int)(hsbVal * 255F);
	}

	@Override
	public float getPerc() {
		return hsbVal;
	}

	@Override
	public void actionValueSet(float perc) {
		hsbVal = perc;
		float [] hsb = parent.getHSB();
		hsb[hsbIndex] = hsbVal;	// we can do this since it'll copy over this value to itself
		parent.fireColorEvent(hsb, this);
	}

}
