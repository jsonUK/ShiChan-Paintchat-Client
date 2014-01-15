package paintchat.saistyle.brushactions;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import paintchat.Mg;
import paintchat.saistyle.OverlayGraphics;
import paintchat_client.SaiMi;

public class MoveBrushAction extends AbstractSelectBrushAction {

	protected boolean isCopy;
	protected boolean isMoving;
	
	public MoveBrushAction(SaiMi mi, Mg mgDraw) {
		super(mi, mgDraw, Color.cyan);
		// 
		isCopy = false;
		isMoving = false;
	}	
	
	protected void handleDragMoveMouseAction(int meID, int x, int y) {
		OverlayGraphics overlayG = saiMi.getOverlayGraphics();
		overlayG.setColor(Color.cyan);
		switch(meID) {
			case MouseEvent.MOUSE_PRESSED:
				if(isInSelectedArea(x, y)) {
					isMoving = true;
					// if not moved, then this is a new selection, copy image buffer
					// sets image
					captureImage();
					
					// remember the delta we move from
					firstPoint.setLocation(selectRect.x, selectRect.y);	
	        		lastPoint.setLocation(x, y);
	        		
	        		// clear last perm select rectangle, we are going to drag in the clear with no box
	        		drawPermRect(overlayG, false);
				}
				break;
			case MouseEvent.MOUSE_DRAGGED:
				if(isSelected) {
					// first clear the last spot we were before we update our points
					saiMi.m_paint(selectRect.x, selectRect.y, selectRect.width, selectRect.height);
					
            		selectRect.x = firstPoint.x + (x - lastPoint.x); // delta X from drag point
            		selectRect.y = firstPoint.y + (y - lastPoint.y);
            		
            		// draw image
            		drawImage();          		
            	}
				break;
			case MouseEvent.MOUSE_RELEASED:
				// repaint square
				if(isSelected) {					
					// do something? no, leave image there until we do another move or unselect
					// this way we imitate the 'floating' area, where the object doesnt exist until they 
					// either do something
					drawImage();
            		
            		drawPermRect(overlayG, true); // put draw rect down again
            		isModified = true;
            		isMoving = false;
				}
				break;
		}			
	}
	
	private void doMove() {
		try {
			if(isModified) {
				isModified = false;
				
				// remove image				
				saiMi.getOverlayGraphics().removeImage();
				
				int [] movePoints = new int[4];	// 4 because mg expects 4 even though we only use 3
				movePoints[0] = pointToInt(selectPointOrigin.x, selectPointOrigin.y);
				movePoints[1] = pointToInt(selectPointOrigin.x + selectRect.width, selectPointOrigin.y + selectRect.height);
				movePoints[2] = pointToInt(selectRect.x, selectRect.y);
				
				int lastHint = mgInfo.iHint;
				int lastLayerSrc = mgInfo.iLayerSrc;
				
				mgInfo.iHint = (isCopy? Mg.H_COPY : Mg.H_MOVE);
				mgInfo.iLayerSrc = originalLayer;
				
				mgDraw.set(mgInfo);
				mgDraw.dRetouch(movePoints);
				endRecord();
				
				mgInfo.iHint = lastHint;
				mgInfo.iLayerSrc = lastLayerSrc;				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Try to capture selected area as an image. Return if the selected
	 * image was captured or not 
	 * 
	 * @return
	 */
	private boolean captureImage() {
		if(!isSelected) {
			return false;
		}
		
		if(!isModified) {			
			originalLayer = mgInfo.iLayer;
			
			// create image, flush old for resources
			if(selectedImage != null) {
				selectedImage.flush();
			}
			selectedImage = mgDraw.getImage(mgInfo.iLayer, 
					selectRect.x, selectRect.y, 
					selectRect.width, selectRect.height);			
		}
		return true;
	}
	
	private void drawImage() {		
		OverlayGraphics overlayG = saiMi.getOverlayGraphics();		
		overlayG.drawImage2(selectedImage, selectRect.x, selectRect.y, selectRect.width, selectRect.height);
	}
	
	@Override
	public Cursor getCursor(int meID, int x, int y) {
		if(isInSelectedArea(x, y)) {
			return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		}
		return super.getCursor(meID, x, y);
	}

	@Override
	public void mouseAction(int meID, int x, int y) {
		if(isMoving || isInSelectedArea(x, y)) {
			handleDragMoveMouseAction(meID, x, y);
		}	
		else {
			handleSelectMouseAction(meID, x, y);
		}
	}

	public void setCopyMode(boolean copy) {
		isCopy = copy;
	}

	@Override
	protected void selectAction(int selectActionID) {
		switch(selectActionID) {
			case AbstractSelectBrushAction.SA_AREACHANGED:
			case AbstractSelectBrushAction.SA_UNSELECTED:
				doMove();					
		}
	}

}
