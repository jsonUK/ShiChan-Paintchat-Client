package paintchat.saistyle.brushactions;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import paintchat.Mg;
import paintchat.saistyle.OverlayGraphics;
import paintchat_client.SaiMi;


public abstract class AbstractSelectBrushAction extends RectangleBrushAction {

	protected final static int SA_AREACHANGED = 0;
	protected final static int SA_UNSELECTED = 1;
	
	protected boolean isSelected;
	protected boolean isModified;
	protected int originalLayer;
	protected Image selectedImage;
	protected Rectangle selectRect;
	protected Point selectPointOrigin;
	protected SelectArea selectArea;
	protected Color selectColor;
	
	public AbstractSelectBrushAction(SaiMi mi, Mg mgDraw, Color selectColor) {
		super(mi, mgDraw);
		this.selectColor = selectColor;
		selectArea = new SelectArea();	
		// these are points and rects handled by the mose even whether to update the native mappings to scaled
		selectRect = selectArea.scaledRect;
		selectPointOrigin = selectArea.scaledOrigPoint;
		originalLayer = 0;
		isSelected = false;
		isModified = false;			
	}

	@Override
	public final void mouseAction(int meID, int x, int y, int pressure) {
		try
        {
			selectArea.update();
			mouseAction(meID, x, y);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
	}
	
	public abstract void mouseAction(int meID, int x, int y);
	
	protected abstract void selectAction(int selectActionID);
	
	protected void handleSelectMouseAction(int meID, int x, int y) {
		OverlayGraphics overlayG = saiMi.getOverlayGraphics();
		overlayG.setColor(selectColor);
		overlayG.clearEchoXOR();
		switch(meID)
        {
        default:
            break;

        case MouseEvent.MOUSE_PRESSED:        	
        	
        	// remove the old perm Rectangle
        	drawPermRect(overlayG, false);
        	
        	// the owner should know if we change the selected area incase they are doing something
        	if(isSelected) {
        		selectAction(SA_AREACHANGED);
        	}
        	
        	// if selected and we clicked inside our rectangle, lets allow the user to move it, keep the selected flag
        	if(isInSelectedArea(x,y)) {
        		// we are misusing first/last point, DO NOT CALL getX(), Y() WIDTH() HEIGHT(), they will not work
        		firstPoint.setLocation(selectRect.x, selectRect.y);	
        		lastPoint.setLocation(x, y);
        		// clear last perm select rectangle
        		
        		// now add the fast memory one
        		overlayG.drawRect(selectRect.x, selectRect.y, selectRect.width, selectRect.height);
        		break;
        	}        	      
        	
        	// start selection drag
    		isSelected = false;
    		isModified = false;
        	isRecording = true;	        	
        	firstPoint.setLocation(x, y);
        	lastPoint.setLocation(x, y);
            break;

        case MouseEvent.MOUSE_DRAGGED: 	        		        	
            if(isRecording) {	             	
            	// if we are enlarging our selection rectangle
            	if(!isSelected) {	            	
	            	//overlayG.drawRect(getX(), getY(), getWidth(), getHeight());
	            	lastPoint.setLocation(x,y);	// update last point then redraw
	            	overlayG.drawRect(getX(), getY(), getWidth(), getHeight());
            	}
            	// we are moving the selected area
            	else {
            		selectRect.x = firstPoint.x + (x - lastPoint.x); // delta X from drag point
            		selectRect.y = firstPoint.y + (y - lastPoint.y);
            		overlayG.drawRect(selectRect.x, selectRect.y, selectRect.width, selectRect.height);
            	}
            }
            break;

        case MouseEvent.MOUSE_RELEASED: 
            if(isRecording) {
            	if(!isSelected) {
		        	// if distance between two points less than a few pixels, unselect
		        	int dx = (firstPoint.x - lastPoint.x);
		        	int dy = (firstPoint.y - lastPoint.y);
		        	double distance = Math.sqrt(dx*dx + dy*dy);
		        	if(distance < 5) {
		        		// unselect and return                		
		        		isRecording = false;
		        		return;
		        	}
		        	
		        	// otherwise mark as selected - note: perm call we need to call this again to remove
		        	selectRect.setRect(getX(), getY(), getWidth(), getHeight());		        	
		        	isSelected = true;
            	}
            	// redraw updated perm rectangle
            	drawPermRect(overlayG, true); 
            	selectPointOrigin.setLocation(selectRect.x, selectRect.y);
            }                
        }
	}	
		
	public void unselect() {
		if(isSelected) {
			selectAction(SA_UNSELECTED);
			// remove drawing area
			drawPermRect(saiMi.getOverlayGraphics(), false);
			
			isSelected = false;
			isRecording = false;
			isModified = false;
		}
	}
		
	
	protected int pointToInt(int x, int y) {
		return  x << 16 | y & 0xffff;
	}
	
	protected boolean isInSelectedArea(int x, int y) {
		if(!isSelected) {
			return false;
		}
		selectArea.update();
		return isSelected && selectRect.contains(x, y);	
	}	
	
	public void abort() {
		unselect();
	}

	protected void drawPermRect(OverlayGraphics g, boolean draw) {
		
		if(draw) {		
			g.drawRect2(selectRect.x, selectRect.y, selectRect.width, selectRect.height, OverlayGraphics.STORE_PERM1);
		}		
		else {
			g.clearEchoXOR(OverlayGraphics.STORE_PERM1);
		}
	}
	
	@Override
	public Cursor getCursor(int meID, int x, int y) {
		if(isInSelectedArea(x, y)) {
			return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);					
		}
		return null;
	}
	
	private class SelectArea {
		private int lastQ, lastScale;
		private Rectangle scaledRect = new Rectangle();		
		private Point scaledOrigPoint = new Point();
		
				
		public void update() {			
			if(lastQ != info.Q || lastScale != info.scale) {
				info.translatePoint(scaledOrigPoint, lastQ, lastScale);
				info.translateRect(scaledRect, lastQ, lastScale);		
				lastQ = info.Q;
				lastScale = info.scale;
			}
		}		
	}
}
