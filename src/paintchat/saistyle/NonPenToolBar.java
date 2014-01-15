package paintchat.saistyle;

import java.awt.Dimension;

import paintchat.Mg;
import paintchat.Mg.Info;
import paintchat.saistyle.components.SaiToolBar;
import paintchat.saistyle.components.SaiToolBarListener;

public class NonPenToolBar extends SaiToolBar implements SaiToolBarListener, ToolTypeListener {	

	private static final int T_RETOUCH = 0;
	private static final int T_DRAGMOVE = 1;
	private static final int T_EYEDROPPER = 2;
	private static final int T_SHAPE = 3;
	
	private static Dimension ICON_SIZE = new Dimension(20, 20);
	private Tools parent;
	private Mg shapeMg;
	private Info info;
	
	public NonPenToolBar(Tools parent) {
		super(ICON_SIZE);		
		
		this.parent = parent;
		this.info = parent.getInfo();
		info.addToolTypeListener(this);
				
		addTool("selection_rec", T_DRAGMOVE);
		addTool("retouch", T_RETOUCH);
		addTool("eye", T_EYEDROPPER);
		addTool("shapes", T_SHAPE);
		
		shapeMg = new Mg();
		shapeMg.iPen = 0;	// pencil
		shapeMg.iHint = Mg.H_LINE;	// set default as line
		
		addToolBarListener(this);		
	}
	

	public void toolbarAction(SaiToolBar source, int selectedIndex) {		
		switch(selectedIndex) {
			case T_RETOUCH:
				if(info.getToolType() != Mg.TT_RETOUCH) {
					info.setToolType(Mg.TT_RETOUCH, this);	
				}
				break;
			case T_DRAGMOVE:
				if(info.getToolType() != Mg.TT_DRAGMOVE) {
					info.setToolType(Mg.TT_DRAGMOVE, this);	
				}				
				break;
			case T_EYEDROPPER:
				info.setToolType(Mg.TT_EYEDROPPER, this);
				break;
			case T_SHAPE:
				// TODO move this over to component
				info.setMg(shapeMg);
				info.setToolType(Mg.toToolType(shapeMg.iHint), this);				
				break;				
		}
	}

	public void toolTypeChanged(Info info, Object source) {
		setSelectedIndex(-1);
	}
}
