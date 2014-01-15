package paintchat.saistyle.layer;

import java.awt.Panel;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.BoxLayout;

import paintchat.Mg.Info;
import paintchat.saistyle.Tools;
import paintchat_client.SaiMi;
import sun.awt.image.ToolkitImage;

public class LayerManager extends Panel {

	private ArrayList<LayerProperties> layerProps;
	private LayerEditorPanel layerEditorUI; 
	
	private Tools tools;
	private final SaiMi mi;	
	
	
	public LayerManager(Tools parent, SaiMi saimi) {
		tools = parent;
		this.mi = saimi;
		
		// calculate height of components
		int height = LayerEditorPanel.LAYEREDITOR_HEIGHT + (LayerPanel.LAYERPANEL_HEIGHT * mi.info.L);
		setSize(parent.getWidth(), height);
		
		// create components now that size has been initialized
		layerEditorUI = new LayerEditorPanel(this);
		layerProps = new ArrayList<LayerProperties>();
		
		for(int layerIndex=0; layerIndex < mi.info.L; layerIndex++) {		
			layerProps.add(new LayerProperties(new LayerPanel(this, layerIndex)));
		}
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));			
		// top layer is editor
		add(layerEditorUI);
		// add layers top down
		for(int i = layerProps.size() -1; i >=0; i--) {
			add(layerProps.get(i).ui);
		}					
		
		// intialize component
		setCurrentLayer(0);
		
		// launch an infinite thread to update the image panel
		new Thread(){
			public void run() {
				Info info = mi.info;
				try {						
					while(true) {
						sleep(5000);	
						for(LayerProperties props : layerProps) {
							LayerPanel p = props.ui;
							int [] indexMap = info.getOffset()[p.getLayerIndex()];
					       // ToolkitImage image = (ToolkitImage) getToolkit().createImage(new MemoryImageSource(info.W, info.H, new DirectColorModel(24, 0xff0000, 65280, 255), indexMap, 0, info.W));

					        // following steps from http://forums.sun.com/thread.jspa?threadID=657079
					        DirectColorModel colormodel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
					        
					        MemoryImageSource source = new MemoryImageSource(info.W, info.H, colormodel, indexMap, 0, info.W);
					        
					        SampleModel sample = colormodel.createCompatibleSampleModel(info.W, info.H);
					        DataBufferInt data = new DataBufferInt(indexMap, info.W * info.H);
					        WritableRaster raster = WritableRaster.createWritableRaster(sample, data, new Point(0,0));					        
					        BufferedImage img = new BufferedImage(colormodel, raster, false, null);
							p.updateImage(img);
							img.flush();
						}															
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void setCurrentLayer(int index) {
		if(index < 0 || index >= mi.info.L) {
			return;
		}
		
		mi.info.m.iLayer = index;
		layerEditorUI.loadLayerProps(layerProps.get(index).opacity);
		for(LayerProperties p : layerProps) {
			p.ui.setCurrentLayer(p.layerIndex == index);
		}
	}
	
	public void setCurrentOpacity(float perc) {
		setOpacity(mi.info.m.iLayer, perc);
	}
	
	public void setOpacity(int layerIndex, float perc) {
		perc = Math.max(0, Math.min(perc, 1.0F));
		LayerProperties lp = layerProps.get(layerIndex);
		lp.opacity = perc;
		if(lp.isVisible) {
			mi.info.visit[layerIndex] = perc;
			mi.repaint();
		}
		lp.ui.setOpacity(perc);		
	}		
	
	public void setVisible(int layerIndex, boolean visible) {
		LayerProperties lp = layerProps.get(layerIndex);
		lp.isVisible = visible;
		mi.info.visit[layerIndex] = (visible)? lp.opacity : 0;
		mi.repaint();
	}

	private LayerProperties getCurrentLayerProps() {
		return layerProps.get(mi.info.m.iLayer);
	}
	
	private static class LayerProperties {
		final int layerIndex;
		final LayerPanel ui;
		
		float opacity;
		boolean isVisible;
		
		
		
		public LayerProperties(LayerPanel lp) {
			ui = lp;
			layerIndex = lp.getLayerIndex();
			opacity = 1;
			isVisible = true;
		}
	}
}
