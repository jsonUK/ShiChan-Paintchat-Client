package paintchat.saistyle.brushpref;

import java.awt.Dimension;
import java.awt.Panel;
import java.util.HashMap;

import paintchat.Mg;
import paintchat.Mg.Info;
import paintchat.saistyle.ToolTypeListener;
import paintchat.saistyle.Tools;

public class OptionPanelManager extends Panel implements ToolTypeListener {

	private Tools tools;
	private HashMap<OptionPanelType, OptionPanel> panelMap;
	
	enum OptionPanelType {
		PEN,
		PENCIL,
		RETOUCH,
		DRAGMOVE,
		SHAPE,
	}
	
	public OptionPanelManager(Tools parent) {
		super();
		this.tools = parent;
		this.tools.getInfo().addToolTypeListener(this);
		
	//	setMinimumSize(new Dimension(parent.getWidth(), 100));
		panelMap = new HashMap<OptionPanelType, OptionPanel>();
	}		
	
	private OptionPanel getPanel(Info info) {
		switch(info.getToolType()){
			case Mg.TT_PEN:
				switch(info.m.iPen) {
					case Mg.P_PEN:
						return getPenPanel();						
					case Mg.P_SOLID:	// pencil
					default:	
						return getPencilPanel();
				}
				
			case Mg.TT_SHAPE_OVAL:
			case Mg.TT_SHAPE_RECTANGLE:
			case Mg.TT_SHAPE_STRAIGHTLINE:
				return getShapePanel();
				
			case Mg.TT_RETOUCH:
				return getRetouchPanel();
			case Mg.TT_DRAGMOVE:		
			default:
				return getDragMovePanel();			
		}
	}		
	
	private OptionPanel getPenPanel() {
		OptionPanel panel = panelMap.get(OptionPanelType.PEN);
		if(panel == null) {
			panel = new OptionPanel(tools);			
			panel.addOptionComponent(new BrushTypeComponent(tools));
			panel.addOptionComponent(new PressureComponent(tools));
			panelMap.put(OptionPanelType.PEN, panel);
		}
		return panel;
	}
	
	private OptionPanel getShapePanel() {
		OptionPanel panel = panelMap.get(OptionPanelType.SHAPE);
		if(panel == null) {
			panel = new OptionPanel(tools);
			panel.addOptionComponent(new ShapeTypeComponent(tools));
			panel.addOptionComponent(new AlphaComponent(tools));
			panelMap.put(OptionPanelType.SHAPE, panel);
		}
		return panel;
	}
	
	private OptionPanel getPencilPanel() {
		OptionPanel panel = panelMap.get(OptionPanelType.PENCIL);
		if(panel == null) {
			panel = new OptionPanel(tools);			
			panel.addOptionComponent(new BrushTypeComponent(tools));
			panel.addOptionComponent(new PressureComponent(tools));	
			panelMap.put(OptionPanelType.PENCIL, panel);
		}
		return panel;
	}
	
	private OptionPanel getDragMovePanel() {
		OptionPanel panel = panelMap.get(OptionPanelType.DRAGMOVE);
		if(panel == null) {
			panel = new OptionPanel(tools);
			panel.addOptionComponent(new MoveComponent(tools));
			panelMap.put(OptionPanelType.DRAGMOVE, panel);
		}
		return panel;		
	}
	
	private OptionPanel getRetouchPanel() {
		OptionPanel panel = panelMap.get(OptionPanelType.RETOUCH);
		if(panel == null) {
			panel = new OptionPanel(tools);
			panel.addOptionComponent(new RetouchComponent(tools));
			panel.addOptionComponent(new AlphaComponent(tools));
			panelMap.put(OptionPanelType.RETOUCH, panel);
		}
		return panel;
	}

	public void toolTypeChanged(Info info, Object source) {
		if(source instanceof OptionComponent) {
			return;
		}

		removeAll();		
		OptionPanel panel = getPanel(info);
		add(panel);
		panel.loadMg();
		tools.validate();
	}
	
}
