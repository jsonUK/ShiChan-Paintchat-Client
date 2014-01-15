package paintchat.saistyle;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

import nanoxml.XMLElement;
import paintchat.Res;
import paintchat.ToolBox;
import paintchat.Mg.Info;
import paintchat.saistyle.brushactions.AbstractBrushAction;
import paintchat.saistyle.brushpref.OptionPanelManager;
import paintchat.saistyle.colors.ColorToolManager;
import paintchat.saistyle.components.VerticalLayout;
import paintchat.saistyle.layer.LayerManager;
import paintchat_client.Mi;
import paintchat_client.SaiMi;
import syi.awt.LComponent;

public class Tools extends Panel implements ToolBox {
	
	private SaiMi mi;
	private Applet applet;
		
	public static final int I_LINE_COLOR = Color.HSBtoRGB(0, 0, 0.625F);
	public static final int I_BACKGROUND_COLOR = Color.HSBtoRGB(0, 0, 0.95F);
	public static final Color C_LINE = new Color(115, 115, 115);
	public static final Color C_LINE_DIVIDER = new Color(200,200,200);
	
	public static final Color C_TOOL_LINE_HOVER = new Color(223, 123, 39);
	public static final Color C_TOOL_BACKGROUND = new Color(250, 250, 250);
	
	public static final Color C_LINE_HIGHLIGHT = new Color(195,118,061);
	
	public static final Color C_ICON_BACKGROUND_SELECTED = new Color(220,220,255);
	public static final Color C_ICON_BACKGROUND_SELECTED_HOVER = new Color(170, 170, 255);
	
	public static final Color C_ICON_LINE_HOVER = new Color(145, 95, 45);
	public static final Color C_ICON_HOVER_BORDER_1 = new Color(96, 64, 255);
	public static final Color C_ICON_HOVER_BORDER_2 = new Color(64, 16, 192);
		
	public static final Color C_DISABLED_TOOL_LINE = new Color(205, 205, 205);
	public static final Color C_DISABLED_TOOL_FONT = new Color(192, 192, 192);
	public static final Color C_DISABLED_TOOL_FILL = new Color(240,240,240);
	public static final Color C_DISABLED_TOOL_BACKGROUND = C_TOOL_BACKGROUND;
	
	public static final Color C_GRID_LINE = new Color(224,224,224);
	
	public static final Color C_BACKGROUND = new Color(I_BACKGROUND_COLOR);
	public static final Color C_FONT = new Color(20, 20, 20);
	public static final Color C_FONT_HOVER = new Color(69, 49, 178);
	
	public static final Font F_TOOL = new Font("Tahoma", Font.PLAIN, 9);
	public static final Font F_TOOL2 = new Font("Tahoma", Font.PLAIN, 10);
	public static final Font F_LABEL_FONT = new Font("Tahoma", Font.PLAIN, 11);
	
	private ColorToolManager toolColor;
	private PenToolBar penToolBar;
	private NonPenToolBar selectToolBar;
	private OptionPanelManager currentOptionsPanel;	
	private SaiTablet tablet;
	
	public Tools() {
		
	}

	protected void fireToolChangeEvent() {
		
	}
		
	public void pMouse(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		
	}
		
	public void paint2(Graphics g) {
		// TODO Auto-generated method stub
	//	toolColor.paint(g);
	}
	
	public void paint(Graphics g) {
		//super.paint(g);
		paint2(g);
	}

	public String getC() {
		// TODO Auto-generated method stub
		System.err.println("getC()");
		return null;
	}

	public LComponent[] getCs() {
		// TODO Auto-generated method stub
		return new LComponent[] {};
	}

	public int getW() {
		// TODO Auto-generated method stub
		return this.getWidth();
	}

	public void init(Container container, Applet applet, Res res, Res res1, Mi saimi) {
		this.applet = applet;
		this.mi = (SaiMi)saimi;
		
		// load jtablet2 drivers
		try {
			tablet = new SaiTablet(this.mi, this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		setBackground(new Color(I_BACKGROUND_COLOR));
		
		// set size, must be done before initializing other components	
		Dimension d = new Dimension(150,container.getHeight());		
		setSize(d);
		
		toolColor = new ColorToolManager();
		penToolBar = new PenToolBar();				
		selectToolBar = new NonPenToolBar(this);
		currentOptionsPanel = new OptionPanelManager(this);
		
		container.add(this);
		// load XMLRoot
	
		toolColor.init(this, getWidth());
		penToolBar.init(this, SaiUtil.getToolsXML(), getWidth());		
		
		Panel selectAndColorPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 2, 1));
		selectAndColorPanel.add(selectToolBar);
		selectAndColorPanel.add(toolColor.getCurrentColorComponent());
		selectAndColorPanel.setBackground(new Color(I_BACKGROUND_COLOR));								
		
		setLayout(new VerticalLayout(2));		
		add(toolColor);
		add(new LayerManager(this, mi));
		add(selectAndColorPanel);
		add(penToolBar);
		add(currentOptionsPanel);		
	}
	
	public Info getInfo() {
		return mi.info;
	}	
	
	public boolean hasDrivers() {
		return tablet.hasDrivers();
	}
	/*
	
	public void mgChange(Object source) {
		// TODO create interface for options panel and call .loadMg if we select the right one
		// 
		//currentOptionsPanel
		if(source == selectToolBar) { 
			selectOptionsPanel.loadMg();			
		}
		else if(source == penToolBar) {
			
		}
	}
*/	
	/**
	 * Try to load the root
	 * @return
	 */
	private XMLElement loadRoot() throws Exception{		
		URL url = getClass().getClassLoader().getResource("settings.xml");
		XMLElement e = new XMLElement();
		e.parseFromReader(new InputStreamReader(url.openConnection().getInputStream()));
		return e;
	}
	
	/**
	 * Get the tools out of the root element
	 * @param root
	 * @return
	 */
	private XMLElement parseTools(XMLElement root) {
		Vector<XMLElement> children = root.getChildren();		
		for(XMLElement curElement : children) {
			if(curElement.getName().equalsIgnoreCase("TOOLS")) {
				return curElement;
			}
		}
		return null;
	}
	
	public void lift() {
		// TODO Auto-generated method stub
		
	}

	public void mgChange() {
		// TODO Auto-generated method stub
		
	}

	public void pack() {
		// TODO Auto-generated method stub
		
	}

	public void selPix(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	public void setARGB(int color) {
		toolColor.setColor(color);
	}

	public void setC(String s) {
		// TODO Auto-generated method stub
		
	}

	public void setLineSize(int i) {
		// TODO Auto-generated method stub
		
	}

	public void up() {
		// TODO Auto-generated method stub
		
	}

	public AbstractBrushAction getBrush() {
		return mi.getCurrentBrush();				
	}	
}
