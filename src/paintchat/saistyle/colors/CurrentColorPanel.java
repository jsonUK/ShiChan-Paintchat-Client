package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import paintchat.saistyle.IconFactory;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiCanvas;
import paintchat.saistyle.components.ToolTip;

public class CurrentColorPanel extends SaiCanvas implements ColorListener, MouseListener {
	
	private static final Dimension COLOR_SQUARE_SIZE = new Dimension(28,28);
	private static final int INSET = 2;
		
	private Rectangle primaryColorSq;
	private Rectangle secondaryColorSq;
	private ColorToolManager manager;
	private Color secondaryColor;
	
	private BufferedImage image;
	
	public CurrentColorPanel(ColorToolManager mgr) {		
		manager = mgr;
		primaryColorSq = new Rectangle(0,0,COLOR_SQUARE_SIZE.width, COLOR_SQUARE_SIZE.height);
		secondaryColorSq = new Rectangle((int)(COLOR_SQUARE_SIZE.width * 0.6D),(int) (COLOR_SQUARE_SIZE.height *0.6D), COLOR_SQUARE_SIZE.width, COLOR_SQUARE_SIZE.height);		
		secondaryColor = Color.WHITE;
		
		image = IconFactory.createIcon("change", null, null, false, Tools.C_BACKGROUND, 15, 15, 0);
		
		Rectangle union = primaryColorSq.union(secondaryColorSq);
		
		setSize(new Dimension(union.width+1, union.height+1));
		setFont(Tools.F_TOOL);
		addMouseListener(this);
		
		new ToolTip("Currentjypg Color", this);
	}				
	
	public void colorChanged(int rgb, float[] hsb, ColorListener source) {
//		repaint();
		fastPaint();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.drawImage(image, null, getWidth()-15, 0);
		//g.setColor(new Color(getRGB()));
		//g.fillRect(0, 0, getWidth(), getHeight());
		// draw border lines
		g.setColor(Tools.C_LINE);
		g2.drawRect(primaryColorSq.x, primaryColorSq.y, (int)primaryColorSq.getMaxX(), (int)primaryColorSq.getMaxY());
		g2.drawLine(secondaryColorSq.x, (int)primaryColorSq.getMaxY(), (int)secondaryColorSq.x, (int)secondaryColorSq.getMaxY()); // draw left side of border line
		g2.drawLine((int)primaryColorSq.getMaxX(), secondaryColorSq.y, (int)secondaryColorSq.getMaxX(), secondaryColorSq.y); // draw top side of border line
		g2.drawLine((int)secondaryColorSq.getMaxX(), secondaryColorSq.y, (int)secondaryColorSq.getMaxX(), (int)secondaryColorSq.getMaxY()); // draw right side of border line
		g2.drawLine(secondaryColorSq.x, (int)secondaryColorSq.getMaxY(), (int)secondaryColorSq.getMaxX(), (int)secondaryColorSq.getMaxY()); // draw bottom side of border line
		
		
		// draw primary square
		g2.setColor(manager.getColor());
		g2.fillRect(primaryColorSq.x + INSET, 
				primaryColorSq.y + INSET, 
				primaryColorSq.width - (INSET +1 ), 
				primaryColorSq.height - (INSET +1));
		// draw 2ndary fill
		g2.setColor(secondaryColor);
		g2.fillRect(secondaryColorSq.x + INSET,		// bottom half
				(int)primaryColorSq.getMaxY() + 1, 
				secondaryColorSq.width - (INSET + 1), 
				(int) (secondaryColorSq.getMaxY() - primaryColorSq.getMaxY() - (INSET)));
		g2.fillRect((int)primaryColorSq.getMaxX() + 1, 	// top right quarter (what is left)
				secondaryColorSq.y + INSET, 
				(int) (secondaryColorSq.getMaxX() - primaryColorSq.getMaxX() - (INSET)), 
				(int)(primaryColorSq.getMaxY() - secondaryColorSq.y - 1 ));
		
		
	}
	
	private boolean isWithinSwitchIcon(int x, int y) {
		int maxX = (int)primaryColorSq.getMaxX();
		int topY = secondaryColorSq.y;
		return (x > primaryColorSq.getMaxX() && y < secondaryColorSq.y); 	
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		if(isWithinSwitchIcon(e.getX(), e.getY())) {
			swapColor();
		}
	}
	
	private void swapColor() {
		Color old = secondaryColor;
		secondaryColor = manager.getColor();
		manager.fireColorEvent(old, this);		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
