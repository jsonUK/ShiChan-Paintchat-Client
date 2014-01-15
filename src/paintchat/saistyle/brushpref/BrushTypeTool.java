package paintchat.saistyle.brushpref;

import java.awt.Dimension;

import paintchat.saistyle.components.SaiToolBar;

public abstract class BrushTypeTool extends SaiToolBar {
	
	public BrushTypeTool(Dimension iconSize) {
		super(iconSize);
	}
	public abstract void setPenMode(int iPenM);				
	public abstract int getPenMode();
}
