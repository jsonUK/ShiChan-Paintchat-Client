package paintchat.saistyle.brushpref;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiToolBar;
import paintchat.saistyle.components.SaiToolBarListener;
import paintchat.saistyle.components.VerticalLayout;

public class OptionPanel extends OptionComponent {

	private List<OptionComponent> components;
	
	public OptionPanel(Tools tools) {		
		super(tools);
		
		components = new LinkedList<OptionComponent>();
		
		setLayout(new VerticalLayout(4));				
		setSize(new Dimension(tools.getWidth(), 200));		
		setBackground(Tools.C_BACKGROUND);		
		setFont(Tools.F_LABEL_FONT);
		setForeground(Color.black);				
	}	
	
	/**
	 * The layout manager is set to vertical so each component added will be added in the list
	 * @param c
	 */
	void addOptionComponent(OptionComponent c) {
		components.add(c);
		c.setParent(this);
		add(c);		
	}				
		
	public void loadMg() {
		for(OptionComponent c : components) {
			c.loadMg();
		}
	}	
	
	protected void setParent(OptionPanel op) {
		// this should be the root, so we have no parent
	}
	
	/**
	 * update children
	 */
	protected void updateOptions() {
		loadMg();	
	}
	
	
}
