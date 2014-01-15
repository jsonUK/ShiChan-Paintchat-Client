package paintchat.saistyle.brushpref;

import java.awt.Dimension;

import paintchat.Mg;

public class BrushTypeToolOld extends BrushTypeTool {
	public static final int BT_NORMAL = 0;
	public static final int BT_WATERCOLOR = 1;
	public static final int BT_AIRBRUSH = 2;
	public static final int BT_EPEN = 3;
		
	private static Dimension BRUSH_OPTION_SIZE = new Dimension(15, 13);
	
	public BrushTypeToolOld() {
		super(BRUSH_OPTION_SIZE);		
		
		
		addTool("bt_n", BT_NORMAL);			
		addTool("bt_w", BT_WATERCOLOR);
		addTool("bt_a", BT_AIRBRUSH);
		addTool("bt_e", BT_EPEN);
		
	}
	
	private static int convertBrushToPenM(int brush) {
		switch(brush) {
			case BT_NORMAL:
				return Mg.PM_NORMAL;
			case BT_WATERCOLOR:
				return Mg.PM_WATERCOLOR;
			case BT_AIRBRUSH:
				return Mg.PM_AIRBRUSH;				
			case BT_EPEN:
				return Mg.PM_EPEN;
		}
		return -1;
	}
	
	private static int convertPenMToBrush(int penM) {
		switch(penM) {
			case Mg.PM_NORMAL:
				return BT_NORMAL;
			case Mg.PM_WATERCOLOR:
				return BT_WATERCOLOR;
			case Mg.PM_AIRBRUSH:
				return BT_AIRBRUSH;				
			case Mg.PM_EPEN:
				return BT_EPEN;
		}
		return -1;
	}

	public void setPenMode(int iPenM) {
		setSelectedIndex(convertPenMToBrush(iPenM));
	}			
	
	public int getPenMode() {
		return convertBrushToPenM(getSelectedIndex());
	}
}
