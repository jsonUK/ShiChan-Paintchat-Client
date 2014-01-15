package paintchat.saistyle;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import paintchat.saistyle.components.SaiPanel;

public class BasicToolSelector extends SaiPanel {
	
	private static final Dimension TOOL_SIZE = new Dimension(20,20);
		
	public BasicToolSelector() {
		
	}
	
	public void init(Tools parent) {
		
	}
	
	@Override
	public void paint2(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	class ToolIcon extends Rectangle {
		
		public ToolIcon(int x, int y, int width, int height) {
			super(x,y,width,height);			
		}
		
		public void paint(Graphics g) {
			
		}
	}
}
