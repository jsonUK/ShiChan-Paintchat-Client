package paintchat.saistyle.brushpref;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import paintchat.Mg;
import paintchat.saistyle.IconFactory;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiCheckbox;
import paintchat.saistyle.components.SaiToolBar;

public class ShapeTypeTool extends SaiToolBar {
	
	public static final int ST_LINE = 0;
	public static final int ST_RECT = 1;
	public static final int ST_RECT_FILL = 2;
	public static final int ST_OVAL = 3;
	public static final int ST_OVAL_FILL = 4;
			
	private static Dimension ICON_SIZE = new Dimension(18, 16);
	
	
	public ShapeTypeTool() {
		super(ICON_SIZE);
		
		addTool("line", ST_LINE);
		addTool("rect", ST_RECT);
		addTool("rect_filled", ST_RECT_FILL);
		addTool("oval", ST_OVAL);
		addTool("oval_filled", ST_OVAL_FILL);
	}	
	
	public void setMgHint(int mgHint) {
		setSelectedIndex(convertMgHintToShape(mgHint));
	}
	
	public int getMgHint() {
		return convertShapeToMgHint(getSelectedIndex()); 
	}
	
	/**
	 * 	public static final int H_FLINE = 0;
	public static final int H_LINE = 1;
	public static final int H_BEZI = 2;
	public static final int H_RECT = 3;
	public static final int H_FRECT = 4;
	public static final int H_OVAL = 5;
	public static final int H_FOVAL = 6;
	public static final int H_FILL = 7;
	public static final int H_TEXT = 8;
	public static final int H_COPY = 9;
	public static final int H_CLEAR = 10;
	 * 
	 * @param mgHint
	 * @return
	 */
	private static int convertMgHintToShape(int mgHint) {
		switch(mgHint) {
			//case Mg.H_FLINE:
			//	return ST_FREE;
			// kind of lame that RECT = filled rect and FRECT is framerect here..
			case Mg.H_LINE:
				return ST_LINE;
			case Mg.H_FRECT:
				return ST_RECT;
			case Mg.H_RECT:
				return ST_RECT_FILL;
			case Mg.H_FOVAL:
				return ST_OVAL;
			case Mg.H_OVAL:
				return ST_OVAL_FILL;
		}	
		return -1;
	}

	private static int convertShapeToMgHint(int shapeID) {
		switch(shapeID) {
			//case ST_FREE:
			//	return Mg.H_FLINE;
			case ST_LINE:
				return Mg.H_LINE;
			case ST_RECT:
				return Mg.H_FRECT;
			case ST_RECT_FILL:
				return Mg.H_RECT;
			case ST_OVAL:
				return Mg.H_FOVAL;
			case ST_OVAL_FILL:
				return Mg.H_OVAL;
		}				
		return -1;
	}
}
