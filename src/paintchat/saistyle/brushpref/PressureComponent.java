package paintchat.saistyle.brushpref;

import info.clearthought.layout.TableLayout;

import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import paintchat.Mg.Info;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiLabel;
import paintchat.saistyle.components.VerticalLayout;

public class PressureComponent extends OptionComponent {
	
	private SizeSlider sizeSlider;
	private BrushPrefSlider minSizeSlider;
	private BrushPrefSlider alphaSlider;
	private BrushPrefSlider minAlphaSlider;
	
	private Checkbox sensSizeCB;
	private Checkbox sensAlphaCB;
	
	public PressureComponent(Tools tools) {
		super(tools);
		
		setLayout(new VerticalLayout(4));	
		Panel formPanel;
		
		formPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		formPanel.add(new SaiLabel("Pressure:"));
		formPanel.add(sensSizeCB = new Checkbox("Size", true));
		formPanel.add(sensAlphaCB = new Checkbox("Alpha", true));			
		add(formPanel);
		
		double p = TableLayout.PREFERRED;
		double sp = 6;		
		formPanel = new Panel(new TableLayout(new double[][] { 		                                  
				{50, TableLayout.FILL},
				{p, sp, p, sp, p, sp, p} 				
				}));//new GridLayout(4,2, 4,4));		
		formPanel.add(new SaiLabel("Size"), 		"0,0");
		formPanel.add(new SaiLabel("Min Size"),		"0,2");
		formPanel.add(new SaiLabel("Alpha"),		"0,4");
		formPanel.add(new SaiLabel("Min alpha"),	"0,6");
		
		formPanel.add(sizeSlider = new SizeSlider(info),   "1,0");				
		formPanel.add(minSizeSlider = createSlider(50, 100, true),   "1,2");			
		formPanel.add(alphaSlider = createSlider(100, 100, true),   "1,4");				
		formPanel.add(minAlphaSlider = createSlider(0, 100, true), "1,6");				
		add(formPanel);				
		
		// set listeners
		sizeSlider.addSliderListener(sliderListener);
		minSizeSlider.addSliderListener(sliderListener);
		alphaSlider.addSliderListener(sliderListener);
		minAlphaSlider.addSliderListener(sliderListener);
		
		sensAlphaCB.addItemListener(checkboxListener);
		sensSizeCB.addItemListener(checkboxListener);
		
	}

	private PrefSliderListener sliderListener = new PrefSliderListener() {

		public void sliderAction(BrushPrefSlider source) {
			if(source == sizeSlider) {
				// all done within the class				
				return;
			}
			
			float perc = source.getPerc();
			if(source == minSizeSlider) {
				updateMinSizeSlider();
			}
			else if(source == alphaSlider) {
				info.m.iAlpha = (int)(255F * perc);
				info.m.setSensitivity((int)(255F * perc), false, false);
				// modify  min %
				info.m.setSensitivity((int)(255F * perc * minAlphaSlider.getPerc()), true, false);
			}
			else if(source == minAlphaSlider) {
				info.m.setSensitivity((int)(255F * perc * alphaSlider.getPerc()), true, false);
			}
		}
		
	};
	
	private ItemListener checkboxListener = new ItemListener() {

		public void itemStateChanged(ItemEvent e) {
			Checkbox source = (Checkbox) e.getSource();
			if(source == sensAlphaCB) {
				// do something to MG
				minAlphaSlider.setEnabled(source.getState());
				info.m.enableSensitivity(false, source.getState());
				System.out.println(info.m.hasSensitivity(false));
			}
			else if(source == sensSizeCB) {
				minSizeSlider.setEnabled(source.getState());
				info.m.enableSensitivity(true, source.getState());
			}
		}
		
	};	
	
	public void loadMg() {
		if(!tools.hasDrivers()) {
			info.m.enableSensitivity(true, false);
			info.m.enableSensitivity(false, false);
		}
		sensAlphaCB.setState(info.m.hasSensitivity(false));
		sensSizeCB.setState(info.m.hasSensitivity(true));
		
		sizeSlider.fastPaint();	// paints the values itself
		
		// convert the MG to float percent
		float alpha = info.m.getSensitivity(false, false) / 255F;
		float minAlpha = info.m.getSensitivity(true, false) / (255 * alpha);
		// calculate size
		float curSize = info.getPenSize();
		float minSize = info.getPenSize((int)(info.m.getSensitivity(true, true) / 255F) * (info.getPenSizeIndexLength() - 1));
		
		alphaSlider.setPerc(alpha);		
		minAlphaSlider.setPerc(minAlpha);
		minSizeSlider.setPerc(minSize / curSize);			
		
		// disable or enable sliders accordingly
		minAlphaSlider.setEnabled(sensAlphaCB.getState());
		minSizeSlider.setEnabled(sensSizeCB.getState());	
	}
	
	private void updateMinSizeSlider() {
		//info.m.iSS; 
		// figure out the actual size of currently selected size / maxsize
		float perc = minSizeSlider.getPerc();
		float curSize = info.getPenSize();	// gets iSize
		float targetSize = curSize * perc;
		int selectedIndex = 0;
		for(int i=0; i < info.m.iSize; i++) {
			if(info.getPenSize(selectedIndex) > targetSize) {
				break;
			}
			selectedIndex = i;
		}
		// found selected index, now lets calculate the sensitivity
		int sens = (int)(((float) selectedIndex / ((float)info.getPenSizeIndexLength() - 1)) * 255F);
		info.m.setSensitivity(sens, true, true);
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
	
	class SizeSlider extends BrushPrefSlider {

		int maxIndexValue;
		Info info;		
		DecimalFormat nf;
		
		public SizeSlider(Info info) {
			super();	
			this.info = info;
			nf = (DecimalFormat) NumberFormat.getInstance();
			nf.applyPattern("##.#");
			init(100);			
		}				
		
		@Override
		public void actionValueSet(float perc) {
			// do nothing
			// snap to value
			int sizeIndexMax = info.getPenSizeIndexLength() - 1;
			info.m.iSize = (int) ((0.5 + sizeIndexMax) * perc);
			info.m.setSensitivity((int)(255F * perc), false, true);
			
			updateMinSizeSlider();			
		}

		@Override
		public String getDisplayValue() {
			return nf.format(info.getPenSize()) + "px";
		}

		@Override
		public float getPerc() {
			int sizeIndexLength = info.getPenSizeIndexLength();
			return (float)info.m.iSize / (float)(sizeIndexLength - 1);
		}
		
	}
}
