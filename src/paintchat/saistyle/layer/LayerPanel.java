package paintchat.saistyle.layer;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

import paintchat.saistyle.IconFactory;
import paintchat.saistyle.SaiUtil;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiCheckbox;
import paintchat.saistyle.components.SaiCheckboxListener;

public class LayerPanel extends Panel implements MouseListener, SaiCheckboxListener {

	public static final int LAYERPANEL_HEIGHT = 50;
	
	// state
	private boolean isCurrentLayer;
	private boolean isHover;
	private int layerIndex;
	
	// parent references
	private LayerManager parent;
	
	// internal references	
	private ImagePanel imgPanel;	
	private Label opacityLabel;
	private Label layerNameLabel;
	private SaiCheckbox isCurrentLayerCB;
	private SaiCheckbox isVisibleCB;
		
	public LayerPanel(LayerManager parent, int layerIndex) {
		super();
		this.parent = parent;
		this.layerIndex = layerIndex;				
		
		// set fonts for internal components to inherit
		setFont(new Font("Arial", Font.PLAIN, 10));
		
		imgPanel = new ImagePanel();
		isCurrentLayerCB = new SaiCheckbox(new Dimension(19, 19), true){

			@Override
			protected void initImageStates(BufferedImage[] imageState) {
				Dimension d = getSize();
				imageState[STATE_C0H0] = IconFactory.createIcon((String)null, null, Tools.C_ICON_HOVER_BORDER_1, false, Color.white, d.width, d.height, 1);
				imageState[STATE_C0H1] = imageState[STATE_C0H0];
				imageState[STATE_C1H0] = IconFactory.createIcon("drawing_layer", null, Tools.C_ICON_HOVER_BORDER_1, false, Color.white, d.width, d.height, 1);
				imageState[STATE_C1H1] = imageState[STATE_C1H0];
			}
			
		};
		isCurrentLayerCB.addCheckboxListener(this);
		
		isVisibleCB = new SaiCheckbox(new Dimension(19, 19), true) {

			@Override
			protected void initImageStates(BufferedImage[] imageState) {
				Dimension d = getSize();
				imageState[STATE_C0H0] = IconFactory.createIcon("unselected_layer", null, Tools.C_ICON_HOVER_BORDER_1, false, Color.white, d.width, d.height, 1);
				imageState[STATE_C0H1] = imageState[STATE_C0H0];
				imageState[STATE_C1H0] = IconFactory.createIcon("selected_layer", null, Tools.C_ICON_HOVER_BORDER_1, false, Color.white, d.width, d.height, 1);
				imageState[STATE_C1H1] = imageState[STATE_C1H0];
			}
			
		};
		isVisibleCB.addCheckboxListener(this);
		isVisibleCB.setChecked(true);

		layerNameLabel = new Label(layerIndex == 0? "Background" : ("Layer "+layerIndex));
		opacityLabel = new Label("100%");


		
		setupTableLayout(parent.getWidth(), LAYERPANEL_HEIGHT);
		add(isCurrentLayerCB, "1, 1");
		add(isVisibleCB, "1, 3");
		add(imgPanel, "3, 1, 3, 3");
		add(layerNameLabel, "5, 1");
		add(opacityLabel , "5, 3");		
		
		layerNameLabel.addMouseListener(this);
		opacityLabel.addMouseListener(this);
		imgPanel.addMouseListener(this);
		addMouseListener(this);		
	}
	
	public int getLayerIndex() {
		return layerIndex;
	}
	
	protected void updateLabels() {
		
	}			
	
	private void updateBackground() {
		setBackground((isHover || isCurrentLayer)? Tools.C_ICON_BACKGROUND_SELECTED : Color.white);
	}
	
	protected void setCurrentLayer(boolean selected) {
		isCurrentLayer = selected;
		isCurrentLayerCB.setChecked(isCurrentLayer);
		updateBackground();
		repaint();
	}
	
	protected void updateImage(BufferedImage image) {		
        imgPanel.updateImage(image);        
	}
	
	private void setupTableLayout(int width, int height) {
		// spacer
		int sp = 4;
		
		// determine widths 
		int cbW = 19;
		int usableW = width - (4 * sp) - cbW;	// 30 is for the checkbox 
		int imgW = (int)(0.45 * (float) usableW);		
		int labelW = (int)(0.55 * (float) usableW);		
		// determine height
		int vsplit = (LAYERPANEL_HEIGHT - (3 * sp)) / 2;				
		
		double [][] tlConsts = new double[][] {
				{sp, cbW, sp, imgW, sp, labelW, sp},				
				{sp, vsplit, sp, vsplit, sp}							
		};
		
		setLayout(new TableLayout(tlConsts));
	}

	public void paint(Graphics g) {						
		if(isCurrentLayer) {
			g.setColor(Tools.C_LINE_HIGHLIGHT);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		}
	}	
	
	public void setBackground(Color c) {
		super.setBackground(c);
		for(Component cmp : getComponents()) {
			cmp.setBackground(null);
		}
	}
	
	/**
	 * Simple panel to set an image into the square
	 * 
	 * @author rift
	 *
	 */
	private static class ImagePanel extends Panel {
		private Image img;
		
		public void paint(Graphics g) {					
			if(img != null) {
				g.drawImage(img, 0, 0, null);
			}
		}
		
		public void updateImage(BufferedImage newImage) {
			// rescale image to img			
			if(img == null) {
				img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);				
			}			
			
			float imgW = img.getWidth(null);
			float imgH = img.getHeight(null);
			float nimgW = newImage.getWidth(null);
			float nimgH = newImage.getHeight(null);
			
			// calculate width height
			float scale = imgW / nimgW;
			if (scale * nimgH > imgH) {
				scale = (float) imgH / nimgH;
			}
			
			// scaled dimensions
			int imgWidth = (int) (scale * nimgW);
			int imgHeight = (int) (scale * nimgH);
			
			// offsets so we can center image
			int imgX = (int)(imgW - imgWidth) / 2;
			int imgY = (int)(imgH - imgHeight) / 2;
			
			// repaint image
			Graphics g = img.getGraphics();

			g.setColor(Color.LIGHT_GRAY);
			// draw 2 sides if necessary
			if(imgX > 1) {
				g.fillRect(0, 0, imgX - 1, (int)imgH);
				g.fillRect((int)imgW - imgX - 1, 0, imgX - 1, (int)imgH);
			}
			else if(imgY > 1) {
				g.fillRect(0, 0, (int)imgW, imgY -1);
				g.fillRect(0, (int)imgH - imgY -1, (int)imgW, imgY - 1);
			}
			// old g.drawImage(newImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), imgX, imgY, imgWidth, imgHeight, null);
			g.drawImage(SaiUtil.getSmallerScaledInstance(newImage, imgWidth, imgHeight), imgX, imgY, imgWidth, imgHeight, null);
			g.setColor(Color.black);
			g.drawRect(0, 0, (int)imgW-1, (int)imgH-1);
            g.dispose();
            
            if(isVisible() && getParent() != null) {
            	g = getGraphics();
            	if(g != null) {
            		paint(g);
            	}
            }
		}
	}		

	public void setOpacity(float perc) {
		opacityLabel.setText(NumberFormat.getPercentInstance().format(perc));		
	}

	public void mousePressed(MouseEvent me) {
		Object obj = me.getSource();
		//if(obj != ) TODO put in checkbox of EYEBALL
		parent.setCurrentLayer(layerIndex);
	}
	
	public void mouseEntered(MouseEvent me) {
		
	}
	
	public void mouseExited(MouseEvent me) {	
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void actionChecked(SaiCheckbox source) {
		if(source == isCurrentLayerCB){
			parent.setCurrentLayer(layerIndex);
		}
		
		else if(source == isVisibleCB) {
			parent.setVisible(layerIndex, isVisibleCB.isChecked());
		}		
	}
}
