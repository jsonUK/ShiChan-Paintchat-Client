package paintchat.saistyle.brushpref;

import info.clearthought.layout.TableLayout;

import java.awt.Panel;

import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiLabel;
import paintchat.saistyle.components.VerticalLayout;

public class AlphaComponent extends OptionComponent {

	private BrushPrefSlider alphaSlider;
	
	public AlphaComponent(Tools tools) {
		super(tools);
		
		
		setLayout(new VerticalLayout(4));	
		Panel formPanel;
		double p = TableLayout.PREFERRED;
		double sp = 6;		
		formPanel = new Panel(new TableLayout(new double[][] { 		                                  
				{50, TableLayout.FILL},
				{p} 				
				}));//new GridLayout(4,2, 4,4));		
		formPanel.add(new SaiLabel("Alpha"),		"0,0");			
		formPanel.add(alphaSlider = createSlider(100, 100, true),   "1,0");								
		add(formPanel);				
		
		alphaSlider.addSliderListener(sliderListener);
	}

	private PrefSliderListener sliderListener = new PrefSliderListener() {

		public void sliderAction(BrushPrefSlider source) {			
			info.m.iAlpha = (int)(255F * source.getPerc());						
		}
		
	};

	public void loadMg() {
		if(!tools.hasDrivers()) {
			info.m.enableSensitivity(true, false);
			info.m.enableSensitivity(false, false);
		}
		
		// convert the MG to float percent
		float alpha = info.m.getSensitivity(false, false) / 255F;		
		alphaSlider.setPerc(alpha);		
	}		
	
	private BrushPrefSlider createSlider(final int initVal, final int maxVal, final boolean percent) {
		BrushPrefSlider prs = new BrushPrefSlider() {
			private int val = initVal;
			
			@Override
			public void actionValueSet(float perc) {
				val = (int) (perc * maxVal);
			}

			@Override
			public String getDisplayValue() {
				return val + (percent? "%" : "");
			}

			@Override
			public float getPerc() {
				return (float)val / (float)maxVal;
			}			
		};
		prs.init(90);
		return prs;
	}		
}
