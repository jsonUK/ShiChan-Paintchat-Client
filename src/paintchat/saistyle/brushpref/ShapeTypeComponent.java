package paintchat.saistyle.brushpref;

import paintchat.Mg;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiToolBar;
import paintchat.saistyle.components.SaiToolBarListener;

public class ShapeTypeComponent extends OptionComponent implements SaiToolBarListener {
	
	private ShapeTypeTool shapeTypeTool;
	
	public ShapeTypeComponent(Tools tools) {
		super(tools);
		add(shapeTypeTool = new ShapeTypeTool());		
		
		shapeTypeTool.addToolBarListener(this);
		shapeTypeTool.setSelectedIndex(0);
	}
	
		
	public void toolbarAction(SaiToolBar source, int selectedIndex) {
		int mgHint = shapeTypeTool.getMgHint();
		info.m.iHint = mgHint;
		info.setToolType(Mg.toToolType(mgHint), this);			
	}


	public void loadMg() {
		shapeTypeTool.setMgHint(info.m.iHint);
	}

}
