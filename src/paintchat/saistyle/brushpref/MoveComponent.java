package paintchat.saistyle.brushpref;

import java.awt.Color;
import java.awt.Dimension;

import paintchat.Mg;
import paintchat.saistyle.Tools;
import paintchat.saistyle.brushactions.AbstractBrushAction;
import paintchat.saistyle.brushactions.MoveBrushAction;
import paintchat.saistyle.brushactions.RetouchBrushAction;
import paintchat.saistyle.components.SaiToolBar;
import paintchat.saistyle.components.SaiToolBarListener;
import paintchat.saistyle.components.VerticalLayout;

public class MoveComponent extends OptionComponent implements SaiToolBarListener {

	private SaiToolBar actionToolbar;
	
	private static final int A_MOVE = 0;
	private static final int A_COPY = 1;
	
	
	public MoveComponent(Tools tools) {
		super(tools);
		// Size entry
				
		setSize(new Dimension(tools.getWidth(), 100));
		setLayout(new VerticalLayout(4));
		
		setBackground(Tools.C_BACKGROUND);
		
		setFont(Tools.F_LABEL_FONT);
		setForeground(Color.black);	
		
		actionToolbar = new SaiToolBar(new Dimension(19,19));		
		actionToolbar.addTool("drag", "move selected area", A_MOVE);
		actionToolbar.addTool("copy", "Copy Area", A_COPY);
		actionToolbar.addToolBarListener(this);
		actionToolbar.setSelectedIndex(A_MOVE);
		add(actionToolbar);		
	}
	
	public void loadMg() {
		
	}
	
	public void toolbarAction(SaiToolBar source, int selectedIndex) {
		source.setSelectedIndex(-1); // since all single clicks = happen once, it should be done
		AbstractBrushAction o = tools.getBrush();
		if(o instanceof MoveBrushAction) {
			MoveBrushAction brush = (MoveBrushAction)o;
			switch(selectedIndex) {
				case A_COPY: 
					brush.setCopyMode(true);
					break;
				case A_MOVE:
					brush.setCopyMode(false);
					break;
			}
		}		
	}
	
}
