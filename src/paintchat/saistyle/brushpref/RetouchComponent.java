package paintchat.saistyle.brushpref;

import java.awt.Color;
import java.awt.Dimension;

import paintchat.Mg;
import paintchat.saistyle.Tools;
import paintchat.saistyle.brushactions.AbstractBrushAction;
import paintchat.saistyle.brushactions.AbstractSelectBrushAction;
import paintchat.saistyle.brushactions.RetouchBrushAction;
import paintchat.saistyle.components.SaiToolBar;
import paintchat.saistyle.components.SaiToolBarListener;
import paintchat.saistyle.components.VerticalLayout;

public class RetouchComponent extends OptionComponent implements SaiToolBarListener {

	private SaiToolBar singleClickToolbar;		
	private Mg retouchMg;
	
	private static final int S_MERGE = 0;
	private static final int S_FLIPHOR = 1;
	private static final int S_FLIPVER = 2;
	private static final int S_ERASER = 3;
	
	
	public RetouchComponent(Tools tools) {
		super(tools);
		// Size entry
		
		retouchMg = new Mg();
		retouchMg.iHint = Mg.H_RECT;
				
		setSize(new Dimension(tools.getWidth(), 100));
		setLayout(new VerticalLayout(4));
		
		setBackground(Tools.C_BACKGROUND);
		
		setFont(Tools.F_LABEL_FONT);
		setForeground(Color.black);	

		singleClickToolbar = new SaiToolBar(new Dimension(20,20));		
		singleClickToolbar.addTool("merge", "Merge", S_MERGE);
		singleClickToolbar.addTool("flip_hor", "Flip Horizontal", S_FLIPHOR);
		singleClickToolbar.addTool("flip_ver", "Flip Vertical", S_FLIPVER);
		singleClickToolbar.addTool("eraser01", "Erase Selected", S_ERASER);
		singleClickToolbar.addToolBarListener(this);
		add(singleClickToolbar);
	}
	
	public void loadMg() {
		info.setMg(retouchMg);
	}
	
	public void toolbarAction(SaiToolBar source, int selectedIndex) {
		if(source == singleClickToolbar) {
			source.setSelectedIndex(-1); // since all single clicks = happen once, it should be done
			AbstractBrushAction o = tools.getBrush();
			if(o instanceof RetouchBrushAction) {
				RetouchBrushAction brush = (RetouchBrushAction)o;
				switch(selectedIndex) {
					case S_FLIPHOR:
						brush.applyFlipHorizontal();
						break;
					case S_FLIPVER:
						brush.applyFlipVertical();
						break;
					case S_ERASER:
						brush.applyErase();
						break;
					case S_MERGE:
						brush.applyMerge();
						break;
				}
			}
		}							
	}
	
}
