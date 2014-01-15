package paintchat.saistyle.brushactions;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import paintchat.Mg;
import paintchat.saistyle.IconFactory;
import paintchat.saistyle.OverlayGraphics;
import paintchat_client.SaiMi;

public class FreeLineBrushAction extends AbstractBrushAction {

	public FreeLineBrushAction(SaiMi mi, Mg mgDraw) {
		super(mi, mgDraw);
		// TODO Auto-generated constructor stub
	}

	private Color cursorColor = Color.black;
	private Point lastPoint = new Point();
	private Cursor cachedCursor = null;
	
	@Override
	public void mouseAction(int meID, int x, int y, int pressure) {
		try
        {				
            switch(meID)
            {
            default:
                break;

            case MouseEvent.MOUSE_PRESSED: 
                prepareRecord();
                mgDraw.dStart(x, y, pressure, true, true);
                break;

            case MouseEvent.MOUSE_DRAGGED: 
                if(isRecording && validScaledMovement(x, y))
                {
                    mgDraw.dNext(x, y, pressure);
                }
                break;

            case MouseEvent.MOUSE_RELEASED: 
                if(isRecording)
                {
                    endRecord();
                }
                break;
            }
            
            lastPoint.x = x;
            lastPoint.y = y;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
	}
	
	public void drawCursor(int meID, int x, int y) {
        try
        {        	
            int penSize = info.getPenMask()[mgInfo.iPenM][mgInfo.iSize].length;
            int penSizeScaled = ((int)Math.sqrt(penSize) * info.scale) / info.Q;
           // System.out.println("S: " + info.scale + ", Q: " + info.Q + ", PS: " + penSizeScaled);
            OverlayGraphics overlayG = saiMi.getOverlayGraphics();
            Color color = cursorColor;
            color = (color.getRGB() & 0xffffff) == mgInfo.iColor >>> 1 ? color : new Color(mgInfo.iColor >>> 1);
			cursorColor = color;
            overlayG.setColor(mgInfo.iPen != 4 && mgInfo.iPen != 5 ? color.getRGB() != 0xffffff ? color : Color.red : Color.cyan);
            overlayG.clearEchoXOR();
            if(penSize <= 1)
            {
                overlayG.fillRect(x, y, penSizeScaled, penSizeScaled);
            } else
            {
                int penRadius = penSizeScaled >>> 1;	             
                overlayG.drawOval(x - penRadius, y - penRadius, penSizeScaled, penSizeScaled);
            }
        }
        catch(RuntimeException _ex) { }        
	}
	
	@Override
	public Cursor getCursor(int meID, int x, int y) {
		if(cachedCursor == null) {
			cachedCursor = IconFactory.createBrushCursor("cursor_arrow", new Point(-1,-1));			
		}
		return cachedCursor;
	}
	
	private boolean validScaledMovement(int curX, int curY) {
		return Math.max(Math.abs(curX - lastPoint.x), Math.abs(curY - lastPoint.y)) >= info.scale;
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}		
}