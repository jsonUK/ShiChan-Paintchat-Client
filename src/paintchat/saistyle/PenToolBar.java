package paintchat.saistyle;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import nanoxml.XMLElement;
import paintchat.Mg;
import paintchat.Mg.Info;
import paintchat.saistyle.components.SaiCheckbox;
import paintchat.saistyle.components.SaiCheckboxListener;

public class PenToolBar extends ScrollPane implements SaiCheckboxListener, ToolTypeListener {
	
	private static final int PTB_HEIGHT = 120;	
	
	private Tools parent;
	private JPanel innerPanel;
	private Vector<PenTool> penTools;
	private Info info;
	
	public PenToolBar() {
		super(ScrollPane.SCROLLBARS_AS_NEEDED);
		penTools = new Vector<PenTool>();
	}
	
	public void init(Tools parent, XMLElement penSettings, int parentWidth) {
		this.parent = parent;
		this.info = parent.getInfo();
		Dimension dOuter = new Dimension(parentWidth, PTB_HEIGHT);		
		setPreferredSize(dOuter);	
		setSize(dOuter);
		getVAdjustable().setUnitIncrement(4);
		
		
		innerPanel = new JPanel();		
		
		Vector<XMLElement> children = penSettings.getChildren();
		for(XMLElement penXML : children) {
			PenTool pt = new PenTool(penXML);
			pt.addCheckboxListener(this);
			penTools.add(pt);
			innerPanel.add(pt);
		}
		
		innerPanel.setBackground(Tools.C_GRID_LINE);		
		innerPanel.setBorder(BorderFactory.createLineBorder(Tools.C_GRID_LINE));
		
		// calculate grid and size
		int xCount = parentWidth / PenTool.PEN_TOOL_SIZE.width;
		int yCount = Math.max(10, (penTools.size() / xCount) + 1);	// do at least 10 rows of spare
		
		int emptySquareCount = (xCount * yCount) - penTools.size(); // should always be ap ositive number if not 0
		for(int i=0; i < emptySquareCount; i++) {
			innerPanel.add(new PenToolEmpty());
		}
			
		innerPanel.setLayout(new GridLayout(yCount, xCount, 1, 1));		
		
		penTools.firstElement().setSelected(true, info);		
						
		
		// avoid stretching by placing into a flowlayout panel
		//Canvas p = new Canvas();
		//p.setBackground(Color.PINK);
		//p.setSize(new Dimension(xCount * PenTool.PEN_TOOL_SIZE.width, yCount* PenTool.PEN_TOOL_SIZE.height + 100));
		//p.add(innerPanel);
		add(innerPanel);
		//setViewportView(p);
		//setBorder(BorderFactory.createLineBorder(Tools.C_LINE));

		setBackground(Tools.C_BACKGROUND);
		validate();	
		
		// add listeners
		info.addToolTypeListener(this);
	}

	public void actionChecked(SaiCheckbox source) {
		for(PenTool pt : penTools) {
			pt.setSelected(pt == source, info);			
		}		
	}

	public void toolTypeChanged(Info info, Object source) {
		if(!(source instanceof PenTool)) {
			for(PenTool pt : penTools) {
				pt.setSelected(false, null);			
			}		
		}
	}
}
