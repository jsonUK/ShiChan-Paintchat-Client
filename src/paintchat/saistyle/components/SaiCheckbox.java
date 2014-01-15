package paintchat.saistyle.components;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public abstract class SaiCheckbox extends SaiCanvas implements MouseListener {	
		
	private boolean isChecked;
	private boolean isHovered;
	
	private BufferedImage [] imageState;
	
	private static final int MASK_CHECK = 0x2;
	private static final int MASK_HOVER = 0x1;
	
	// note C = checked, H = hover
	protected static final int STATE_C0H0 = 0;
	protected static final int STATE_C0H1 = 1;
	protected static final int STATE_C1H0 = 2;
	protected static final int STATE_C1H1 = 3;	
	
	private int drawState;
	
	private LinkedList<SaiCheckboxListener> listeners;
	
	public SaiCheckbox(Dimension d) {
		this(d, false);
	}
	
	/**
	 * 
	 * @param d
	 * @param init Useful to call when creating anonymous instances, otherwise always use false and call with super constructor
	 */
	public SaiCheckbox(Dimension d, boolean init) {
		super();

		drawState = 0;		
		
		imageState = new BufferedImage[4];				
		listeners = new LinkedList<SaiCheckboxListener>();		
		
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
		this.setSize(d);
		
		if(init) {
			init();
		}
	}
	
	/**
	 * This should be called by the super constructor to initialize the state
	 */
	protected void init() {
		initImageStates(imageState);
	}

	/**
	 * Create and initialize the buffered images ARray
	 * @param images
	 */
	protected abstract void initImageStates(BufferedImage [] imageState);
	
	/**
	 * Fire listeners
	 */
	private void fireCheckListeners() {
		for(SaiCheckboxListener listener : listeners) {
			listener.actionChecked(this);
		}
	}
	
	public void addCheckboxListener(SaiCheckboxListener l) {
		listeners.add(l);
	}
	
	public void removeCheckboxListener(SaiCheckboxListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Is this checked?
	 * @return
	 */
	public boolean isChecked() {
		return isChecked;
	}
	
	/**
	 * Set checked state and return old state
	 * @param checked
	 * @return
	 */
	public boolean setChecked(boolean checked) {
		boolean oldState = isChecked;
		if(checked != isChecked) {
			isChecked = checked;
			fastPaint();
		}
		return oldState;
	}
	
	protected void processEvent(AWTEvent awtevent) {
		try {
			int id = awtevent.getID();
			if(awtevent instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) awtevent;
				switch(id) {					
					// mouse is ontop of us
					case MouseEvent.MOUSE_ENTERED:
						mouseEntered(me);
						break;
					case MouseEvent.MOUSE_PRESSED:
						mousePressed(me);
						break;										
					case MouseEvent.MOUSE_EXITED:
						mouseExited(me);
						break;
					case MouseEvent.MOUSE_RELEASED:
						mouseReleased(me);
						break;
				};
			}					
			
		}
		catch(Exception e) {
			e.printStackTrace();			
		}
	}

	@Override
	protected void fastPaint() {	
		// can update state here
		drawState = (isChecked? MASK_CHECK : 0) | (isHovered? MASK_HOVER : 0);
		super.fastPaint();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		// choose border
		Dimension d = getSize();
		g2.drawImage(imageState[drawState], null, 0, 0);
	}


	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseEntered(MouseEvent e) {
		if(!isHovered) {
			isHovered = true;							
			fastPaint();
		}		
	}


	public void mouseExited(MouseEvent e) {
		if(isHovered) {
			isHovered = false;
			fastPaint();
		}
	}


	public void mousePressed(MouseEvent e) {
		isChecked = !isChecked;
		fastPaint();
		fireCheckListeners();
	}


	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}	
}
