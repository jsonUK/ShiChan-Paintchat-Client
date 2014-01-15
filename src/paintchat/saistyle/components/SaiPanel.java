package paintchat.saistyle.components;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;

public abstract class SaiPanel extends Panel {

	public void fastPaint() {
		Container parent = getParent();
			if(isVisible() && parent != null && parent.isVisible()) {
			Graphics2D g = (Graphics2D) getGraphics();
			if(g != null) {
				paint2(g);
				g.dispose();
			}
		}
	}
	
	public void setSizes(Dimension d) {
		setSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
	}
	
	public abstract void paint2(Graphics2D g);
	
	public void paint(Graphics g) {
		super.paint(g);
		paint2((Graphics2D)g);
	}
}
