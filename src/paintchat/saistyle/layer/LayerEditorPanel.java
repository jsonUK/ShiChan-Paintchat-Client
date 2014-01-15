package paintchat.saistyle.layer;

import info.clearthought.layout.TableLayout;

import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;

import paintchat.saistyle.brushpref.BrushPrefSlider;
import paintchat.saistyle.brushpref.PrefSliderListener;
import paintchat.saistyle.components.SaiLabel;
import paintchat.saistyle.components.VerticalLayout;

public class LayerEditorPanel extends Panel {

	public static final int LAYEREDITOR_HEIGHT = 30;
	
	private BrushPrefSlider alphaSlider;
	private LayerManager parent;
	
	public LayerEditorPanel(LayerManager parent) {
		this.parent = parent;	
		setLayout(new VerticalLayout(4));	
		Panel formPanel;
		double p = TableLayout.PREFERRED;
		double sp = 6;		
		formPanel = new Panel(new TableLayout(new double[][] { 		                                  
				{50, TableLayout.FILL},
				{p, p} 				
				}));//new GridLayout(4,2, 4,4));		
		
		
		Label title = new Label("Layer Controls", Label.CENTER);
		title.setFont(new Font("Arial", Font.PLAIN, 10));
		formPanel.add(title, "0,0,1,0");
		formPanel.add(new SaiLabel("Opacity"),		"0,1");			
		formPanel.add(alphaSlider = createSlider(100, 100, true),   "1,1");								
		add(formPanel);				
		
		alphaSlider.addSliderListener(sliderListener);
	}

	private PrefSliderListener sliderListener = new PrefSliderListener() {

		public void sliderAction(BrushPrefSlider source) {			
			parent.setCurrentOpacity(source.getPerc());
		}
		
	};

	public void loadLayerProps(float opacity) {		
		// convert the MG to float percent			
		alphaSlider.setPerc(opacity);		
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
