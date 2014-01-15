package paintchat.saistyle.components;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;

public class SaiCanvas extends Canvas {

	protected void fastPaint() {
		Component parent = getParent();
		if(isVisible() && parent != null && parent.isVisible()) {
			Graphics g = getGraphics();
			if(g != null) {
				paint(g);
				g.dispose();
			}
		}
	}
}
