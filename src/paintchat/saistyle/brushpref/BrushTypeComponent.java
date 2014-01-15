package paintchat.saistyle.brushpref;

import java.awt.Panel;

import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiLabel;
import paintchat.saistyle.components.SaiToolBar;
import paintchat.saistyle.components.SaiToolBarListener;
import paintchat.saistyle.components.VerticalLayout;

public class BrushTypeComponent extends OptionComponent implements SaiToolBarListener {

	private BrushTypeTool brushTypeTool1;
	private BrushTypeTool brushTypeTool2;	
	
	
	public BrushTypeComponent(Tools tools) {
		super(tools);

		add(new SaiLabel("B.Edge:"));
		//add(brush)
		Panel p = new Panel(new VerticalLayout(4));
		p.add(brushTypeTool1 = new BrushTypeToolOld());
		p.add(brushTypeTool2 = new BrushTypeToolNew());
		add(p);
		
		brushTypeTool2.addToolBarListener(this);
		brushTypeTool1.addToolBarListener(this);					
	}	

	@Override
	public void loadMg() {
		brushTypeTool1.setPenMode(info.m.iPenM);
		brushTypeTool2.setPenMode(info.m.iPenM);		
	}

	public void toolbarAction(SaiToolBar source, int selectedIndex) {	
		BrushTypeTool tb = (BrushTypeTool)source;
		int penMode = tb.getPenMode();
		
		brushTypeTool1.setPenMode(penMode);
		brushTypeTool2.setPenMode(penMode);
		
		info.m.iPenM = penMode;
		info.m.iSize = Math.min(info.getPenMask()[info.m.iPenM].length -1, info.m.iSize);
		updateOptions();
	}

}
