package paintchat.saistyle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import nanoxml.XMLElement;
import paintchat.Mg;
import paintchat.Mg.Info;
import paintchat.saistyle.components.SaiCheckbox;

public class PenTool extends SaiCheckbox {
public static final Dimension PEN_TOOL_SIZE = new Dimension(40, 30);	
	
	private String name;
	private String iconName;
	private Mg mg;
	private boolean customTool;
	private int index;	
 
	public PenTool(XMLElement e) {
		this(e.getStringAttribute("NAME", "unknown"), e.getStringAttribute("ICON", "unknown"), e.getIntAttribute("INDEX", 0));				
		customTool = e.getBooleanAttribute("CUSTOMTOOL", "1", "0", false);
				
		this.mg.set(e);		
	}
	
	/***
	 * This constructor must always be called since it initializes all values
	 * @param name
	 * @param index
	 * @param parentRect
	 */
	private PenTool(String name, String iconName, int index) {
		super(PEN_TOOL_SIZE);
		this.name = name;
		this.index = index;
		this.iconName = iconName;		
				
		this.mg = new Mg();
		init();
	}
		
	/**
	 * Copy constructor here
	 * @param name
	 * @param index
	 * @param mg
	 */
	private PenTool(String name, String iconName, int index, Mg mg) {
		this(name, iconName, index);
		this.mg.set(mg);	// copy mg
		this.customTool = true;
	}
	
	/**
	 * Create a copy of this pentool with the new parameters
	 * @param newName
	 * @param newIndex
	 * @param parent
	 * @return
	 */
	public PenTool copy(String newName, int newIndex) {
		return new PenTool(newName, iconName, newIndex, mg);
	}
	
	/**
	 * Set this icon selected
	 * @param selected
	 */
	public void setSelected(boolean selected, Info info) {
		setChecked(selected);
		if(selected) {
			info.setMg(mg);
			info.setToolType(Mg.TT_PEN, this);
		}
	}
	
	public boolean isRemovable() {
		return customTool;
	}
	
	public Mg getMg() {
		return mg;
	}
	
	public XMLElement saveTool() {
		XMLElement xml = this.mg.getXML();
		xml.setAttribute("NAME", name);
		xml.setIntAttribute("INDEX", index);
		return xml;
	}

	@Override
	protected void initImageStates(BufferedImage[] imageState) {
		Dimension d = PEN_TOOL_SIZE;
		imageState[STATE_C0H0] = IconFactory.createIcon(iconName, name, null, true, Color.WHITE, d.width, d.height);
		imageState[STATE_C0H1] = IconFactory.createIcon(iconName, name, Tools.C_ICON_HOVER_BORDER_1, true, Color.WHITE, d.width, d.height);
		imageState[STATE_C1H0] = IconFactory.createIcon(iconName, name, Tools.C_ICON_HOVER_BORDER_1, true, Tools.C_ICON_BACKGROUND_SELECTED, d.width, d.height);
		imageState[STATE_C1H1] = IconFactory.createIcon(iconName, name, Tools.C_ICON_HOVER_BORDER_2, true, Tools.C_ICON_BACKGROUND_SELECTED_HOVER, d.width, d.height);
	}
}
