package paintchat.saistyle.components;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class ToolTip extends Canvas {

	protected String tip;
	protected Component owner;
	
	private Container mainContainer;
	private LayoutManager mainLayout;
	
	private boolean shown;
	private volatile boolean isOver;
	
	private final int VERTICAL_OFFSET = 30;
	private final int HORIZONTAL_ENLARGE = 6;
	
	private final int delay;	
	
    public ToolTip(String tip, Component owner) {
    	this(tip, owner, 0);
    }
    
    public ToolTip(String tip, Component owner, int delay) {
    	isOver = false;
    	this.delay = delay; 
    	this.tip = tip;
		this.owner = owner;
    	owner.addMouseListener(new MAdapter());
   		setBackground(new Color(255,255,220));
    }


	public void paint(Graphics g) {
		g.setFont(owner.getFont());
		g.drawRect(0,0,getSize().width -1, getSize().height -1);
		g.drawString(tip, 3, getSize().height - 4);
	}

	private synchronized void addToolTip() {
		if(!shown) {
			mainContainer.setLayout(null);
			
			FontMetrics fm = getFontMetrics(owner.getFont());    		
			setSize(fm.stringWidth(tip) + HORIZONTAL_ENLARGE, fm.getHeight() + 4);
	
			setLocation((owner.getLocationOnScreen().x - mainContainer.getLocationOnScreen().x) , 
						(owner.getLocationOnScreen().y - mainContainer.getLocationOnScreen().y + VERTICAL_OFFSET));
	
			// correction, whole tool tip must be visible 
			if (mainContainer.getSize().width < ( getLocation().x + getSize().width )) {
				setLocation(mainContainer.getSize().width - getSize().width, getLocation().y);
			}
			mainContainer.add(this, 0);
			mainContainer.validate();
			repaint();
		}
		shown = true;
	}

	
	private synchronized void removeToolTip() {
		if (shown) {
			mainContainer.remove(0);
			mainContainer.setLayout(mainLayout);
			mainContainer.validate();
		}
		shown = false;
	}

	private void findMainContainer() {
		Container parent = owner.getParent();
		while (true) {
			if ((parent instanceof Applet) || (parent instanceof Frame)) {
				mainContainer = parent;
				break;				
			} else {
				parent = parent.getParent();
			}
		}		
		mainLayout = mainContainer.getLayout();
	}

    class MAdapter extends MouseAdapter {
    	public void mouseEntered(MouseEvent me) {
    		isOver = true;
    		if(delay > 0) {
    			try {
    				Thread.sleep(delay);
    			}
    			catch(Exception e) {}    			
    		}
    		if(isOver) {
			    findMainContainer();
	    		addToolTip();
    		}
    	}
	    public void mouseExited(MouseEvent me) {
	    	isOver = false;
	    	removeToolTip();
		}
		public void mousePressed(MouseEvent me) {
			isOver = false;
	    	removeToolTip();
		}
	}
}
