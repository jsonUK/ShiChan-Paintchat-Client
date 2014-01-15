package paintchat.saistyle.brushactions;

import java.awt.Point;
import java.awt.event.MouseEvent;

import paintchat.Mg;
import paintchat.saistyle.OverlayGraphics;
import paintchat_client.SaiMi;

public class RectangleBrushAction extends AbstractBrushAction {        
	public RectangleBrushAction(SaiMi mi, Mg mgDraw) {
		super(mi, mgDraw);
		// TODO Auto-generated constructor stub
	}

	protected Point firstPoint = new Point();
	protected Point lastPoint = new Point();    	    	
	
	protected int getX() {
		return Math.max(Math.min(firstPoint.x, lastPoint.x), 0);
	}
	
	protected int getY() {
		return Math.max(Math.min(firstPoint.y, lastPoint.y), 0);
	}
	
	protected int getWidth() {
		// theres no way first point can be off the scale, only if we drag 2nd point off
		int lastX = Math.min(lastPoint.x, info.imW * info.scale);
		return Math.abs(firstPoint.x - lastX);
	}
	
	protected int getHeight() {
		int lastY = Math.min(lastPoint.y, info.imH * info.scale);
		return Math.abs(firstPoint.y - lastY);
	}
	
	protected void setUserPoints() {
		int topLeftX = getX();
		int topLeftY = getY();
		int bottomRightX = topLeftX != firstPoint.x? firstPoint.x : lastPoint.x;
		int bottomRightY = topLeftY != firstPoint.y? firstPoint.y : lastPoint.y;
		
		user.points[0] = topLeftX << 16 | topLeftY & 0xffff;
		user.points[1] = bottomRightX << 16 | bottomRightY & 0xffff;
	}

	@Override
	public void mouseAction(int meID, int x, int y, int pressure) {
		try {
			OverlayGraphics overlayG = saiMi.getOverlayGraphics();
			switch(meID)
	        {
	        default:
	            break;

	        case MouseEvent.MOUSE_PRESSED:
	        	prepareRecord();     	
	        	firstPoint.setLocation(x, y);
	        	lastPoint.setLocation(x, y);
	            break;

	        case MouseEvent.MOUSE_DRAGGED: 	        		        	
	            if(isRecording) {	            	
	            	overlayG.clearEchoXOR();
	            	//overlayG.drawRect(getX(), getY(), getWidth(), getHeight());
	            	lastPoint.setLocation(x,y);	// update last point then redraw
	            	overlayG.drawRect(getX(), getY(), getWidth(), getHeight());
	            }
	            break;

	        case MouseEvent.MOUSE_RELEASED: 
	        	if(isRecording) {
	        		overlayG.clearEchoXOR();
                    lastPoint.setLocation(x, y);
                    setUserPoints();                    
                    mgDraw.dRetouch(user.points);
                    endRecord();
                }                
	        	break;
	        }			
		}		
		catch(Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}
}
