package paintchat.saistyle.brushpref;
import java.awt.Dimension;

import paintchat.Mg;


public class BrushTypeToolNew extends BrushTypeTool {		

	public static final int BT_SOFTEST = 0;
	public static final int BT_SOFT = 1;
	public static final int BT_HARD = 2;
	public static final int BT_HARDEST = 3;
		
	private static Dimension BRUSH_OPTION_SIZE = new Dimension(15, 13);
	
	public BrushTypeToolNew() {
		super(BRUSH_OPTION_SIZE);		
		
		for(int i=0; i < 4; i++) {
			addTool("bt_"+i, i);			
		}
	}
	
	private static int convertBrushToPenM(int brush) {
		switch(brush) {
			case BT_SOFTEST:
				return Mg.PM_SOFTEST;
			case BT_SOFT:
				return Mg.PM_SOFT;
			case BT_HARD:
				return Mg.PM_HARD;				
			case BT_HARDEST:
				return Mg.PM_HARDEST;
		}
		return -1;
	}
	
	private static int convertPenMToBrush(int penM) {
		switch(penM) {
			case Mg.PM_SOFTEST:
				return BT_SOFTEST;
			case Mg.PM_SOFT:
				return BT_SOFT;
			case Mg.PM_HARD:
				return BT_HARD;
			case Mg.PM_HARDEST:
				return BT_HARDEST;
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