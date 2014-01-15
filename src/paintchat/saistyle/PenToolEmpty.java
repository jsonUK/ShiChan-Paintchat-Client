package paintchat.saistyle;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class PenToolEmpty extends Canvas {

	private boolean isHovered;
	
	public PenToolEmpty() {
		super();		
		isHovered = false;
		setBackground(Tools.C_BACKGROUND);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
		this.setSize(PenTool.PEN_TOOL_SIZE);
	}
	
	protected void processEvent(AWTEvent awtevent) {
		try {
			int id = awtevent.getID();
			if(awtevent instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) awtevent;
				switch(id) {					
					// mouse is ontop of us
					case MouseEvent.MOUSE_ENTERED:
						if(!isHovered) {
							isHovered = true;
							fastPaint();
						}
						break;
					case MouseEvent.MOUSE_PRESSED:
						
						break;										
					case MouseEvent.MOUSE_EXITED:
						if(isHovered) {
							isHovered = false;
							fastPaint();
						}
						break;
					case MouseEvent.MOUSE_RELEASED:
						
						break;
				};
			}					
			
		}
		catch(Exception e) {
			e.printStackTrace();			
		}
	}
	
	protected void fastPaint() {			
		Component parent = getParent();
		if(isVisible() && parent != null && parent.isVisible()) {
			Graphics2D g = (Graphics2D) getGraphics();
			if(g != null) {
				paint(g);
				g.dispose();
			}
		}
	}
	
	@Override
	public void paint(Graphics g) {		
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		// choose border
		Dimension d = getSize();
	
		g2.setColor(isHovered? Tools.C_ICON_BACKGROUND_SELECTED : getBackground());
		g2.drawRect(0, 0, d.width-1, d.height-1);	
	}
}
