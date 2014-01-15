package paintchat.saistyle;

import java.awt.event.MouseEvent;

import paintchat.PCDebug;
import paintchat_client.SaiMi;
import cello.jtablet.DriverStatus;
import cello.jtablet.TabletDevice;
import cello.jtablet.TabletManager;
import cello.jtablet.event.TabletEvent;
import cello.jtablet.event.TabletListener;
import cello.jtablet.installer.JTabletExtension;
/**
 * TODO LIST:
 * - Move tool, not like copy.
 * - fix floating
 *  
 *  - option panel PEN = special, look at other pen modes (watercolor)
 *  - Draw user name next to user's art on add
 *  - create tool hotkeys (select target tool) and global hotkeys (pen size change, zoom)
 *  - add a window focus SELECTION highlight (between chat and canvas)
 * @author rift
 *
 */
public class SaiTablet implements TabletListener {
	/// Note for some reason the mouse alignment and jpen are off by +2 pixels both directions
	private SaiMi mi;	
	private Tools tools;
	private boolean isStylus;
	
	private boolean driversDetected;


	public SaiTablet(SaiMi mi, Tools t) {
		isStylus = false;			
		tools = t;
		try {	
			TabletManager manager = TabletManager.getDefaultManager();
			if(manager.getDriverStatus().getState() == DriverStatus.State.LOADED) {
				System.out.println("jtablet okay? :" + JTabletExtension.checkCompatibility(mi, "1.2.0"));
				manager.addTabletListener(mi, this);
				driversDetected = true;				
			}
			else {
				driversDetected = false;
			}
		}
		catch(Exception e) {			
			driversDetected = false;	
		}
		this.mi = (SaiMi)mi;
	}
	
	/**
	 * Determine if the drivers were successfully loaded
	 * @return
	 */
	public boolean hasDrivers() {		
		return driversDetected;	
	}
	
	/**
	 * Helper function to convert float version into the 255 value we love so much
	 * @param sens
	 * @return
	 */
	private final int toPressure(float sens) {		
		//int p = (!isStylus)? 255 :(int)(255F * sens);
		//PCDebug.println("c:" + isStylus + "  p:"+p + "   s:"+sens);
		int p = (int)(255F * sens);
		return p;
	}
	
	private final void toSaiMi(int meID, TabletEvent event) {		
		event.translatePoint(-mi.getGapX(), -mi.getGapY());
		mi.pMouse2(meID, event.getX(), event.getY(), event.getButton() == TabletEvent.BUTTON3, event.isAltDown(), event.isControlDown(), toPressure(event.getPressure()), false);
	}
	
	public void cursorDragged(TabletEvent event) {
		toSaiMi(MouseEvent.MOUSE_DRAGGED, event);
	}

	public void cursorMoved(TabletEvent event) {		
		toSaiMi(MouseEvent.MOUSE_MOVED, event);
	}

	public void cursorPressed(TabletEvent event) {
		// TODO Auto-generated method stub
		TabletDevice.Type stylusType = event.getDevice().getType();
		if(stylusType == TabletDevice.Type.STYLUS) {
			isStylus = true;
		}
		else if(stylusType == TabletDevice.Type.ERASER) {
			isStylus = true;
		}		
		// UNKNOWN or MOUSE
		else {
			isStylus = false;
		}
		
		PCDebug.println("1:" + (event.getButton() == TabletEvent.BUTTON1) + " 2:" + (event.getButton() == TabletEvent.BUTTON2) + " 3:" + (event.getButton() == TabletEvent.BUTTON3));
		PCDebug.println(stylusType);
		
		toSaiMi(MouseEvent.MOUSE_PRESSED, event);
	}

	public void cursorReleased(TabletEvent event) {
		toSaiMi(MouseEvent.MOUSE_RELEASED, event);
	}

	public void cursorScrolled(TabletEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void levelChanged(TabletEvent event) {
		toSaiMi(MouseEvent.MOUSE_DRAGGED, event);
	}

	public void cursorEntered(TabletEvent event) {
		toSaiMi(MouseEvent.MOUSE_ENTERED, event);
	}

	public void cursorExited(TabletEvent event) {		
		toSaiMi(MouseEvent.MOUSE_EXITED, event);
	}

	public void cursorGestured(TabletEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
