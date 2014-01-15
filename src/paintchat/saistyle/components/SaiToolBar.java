package paintchat.saistyle.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import paintchat.saistyle.IconFactory;
import paintchat.saistyle.Tools;

public class SaiToolBar extends SaiPanel implements SaiCheckboxListener {
	private SaiCheckbox [] toolComponents;		
	private boolean hover;
	
	private ArrayList<SaiToolBarListener> listeners;
	private Dimension modifiedIconSize;
	
	
	public SaiToolBar(Dimension iconSize) {
		modifiedIconSize = new Dimension(iconSize.width + 4, iconSize.height + 4);
		listeners = new ArrayList<SaiToolBarListener>();
		toolComponents = null;
				
		setBackground(Tools.C_GRID_LINE);							
		setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));										
		addMouseListener(mouseListener);
	}
	
	public void addTool(String iconName, int index) {
		addTool(iconName, null, index);
	}
	
	public void addTool(String iconName, String toolTip, int index) {
		// lazy create toolComponents		
		if(toolComponents == null || toolComponents.length <= index) {
			synchronized (this) {
				SaiCheckbox [] arr = new SaiCheckbox[index + 1];
				if(toolComponents != null) {
					System.arraycopy(toolComponents, 0, arr, 0, toolComponents.length);
				}
				toolComponents = arr;
			}
		}
		
		// add tool
		toolComponents[index] = createTool(iconName);
		if(toolTip != null) {
			new ToolTip(toolTip, toolComponents[index]);
		}
		toolComponents[index].addCheckboxListener(this);
		add(toolComponents[index]);
		
		// modify size of app to handle tool
		setSizes(new Dimension((modifiedIconSize.width + 1)*toolComponents.length+3, modifiedIconSize.height + 4));	// plus 1 cause it crops	
	}
	
	
	public void addToolBarListener(SaiToolBarListener listener) {
		listeners.add(listener);
	}
	
	public void removeToolBarListener(SaiToolBarListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireToolBarAction(int selected) {
		for(SaiToolBarListener listener : listeners) {
			listener.toolbarAction(this, selected);
		}
	}
	
	private SaiCheckbox createTool(final String iconfile) {
		return new SaiCheckbox(modifiedIconSize, true){										

			@Override
			protected void initImageStates(BufferedImage[] imageState) {
				Dimension d = modifiedIconSize;
				imageState[STATE_C0H0] = IconFactory.createIcon(iconfile, null, null, true, Color.WHITE, d.width, d.height, 2);
				imageState[STATE_C0H1] = IconFactory.createIcon(iconfile, null, Tools.C_ICON_HOVER_BORDER_1, true, Color.WHITE, d.width, d.height, 2);
				imageState[STATE_C1H0] = IconFactory.createIcon(iconfile, null, Tools.C_ICON_HOVER_BORDER_1, true, Tools.C_ICON_BACKGROUND_SELECTED, d.width, d.height, 2);
				imageState[STATE_C1H1] = IconFactory.createIcon(iconfile, null, Tools.C_ICON_HOVER_BORDER_2, true, Tools.C_ICON_BACKGROUND_SELECTED_HOVER, d.width, d.height, 2);
			}
			
		};	
	
	}
	
	private MouseAdapter mouseListener = new MouseAdapter() {

		@Override
		public void mouseExited(MouseEvent e) {
			if(contains(e.getPoint())) {
				return;
			}
			hover = false;				
			fastPaint();
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			hover = true;
			fastPaint();
		}						
		
	};

	@Override
	public void paint2(Graphics2D g) {		
		g.setColor(!hover? Tools.C_LINE : Tools.C_LINE_HIGHLIGHT);
		g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 2, 2);
	}

	public void actionChecked(SaiCheckbox source) {
		int selectedIndex = 0;
		for(int i=0; i < toolComponents.length; i++) {
			if(source == toolComponents[i]) {
				selectedIndex = i;
			}
			toolComponents[i].setChecked(source == toolComponents[i]);
		}
		fireToolBarAction(selectedIndex);
	}
	
	/**
	 * Select the index, if the index is -1, deselect all
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		for(int i=0; i < toolComponents.length; i++) {
			toolComponents[i].setChecked(i == index);
		}
	}
	
	/**
	 * Get the selected tool index, -1 if nothing is selected
	 * @return
	 */
	public int getSelectedIndex() {
		for(int i=0; i < toolComponents.length; i++) {
			if(toolComponents[i].isChecked()) {
				return i;
			}			
		}
		return -1;
	}
}
