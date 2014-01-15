package paintchat.saistyle.brushactions;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import paintchat.Mg;
import paintchat.saistyle.OverlayGraphics;
import paintchat_client.SaiMi;

public class StraightLineBrushAction extends AbstractBrushAction {

	public StraightLineBrushAction(SaiMi mi, Mg mgDraw) {
		super(mi, mgDraw);
		// TODO Auto-generated constructor stub
	}

	private Point firstPoint = new Point();
	private Point lastPoint = new Point();
	
	@Override
	public void mouseAction(int meID, int x, int y, int pressure) {
		try
        {
			OverlayGraphics overlayG = saiMi.getOverlayGraphics();
            switch(meID)
            {
            default:
                break;

            case MouseEvent.MOUSE_PRESSED: 
                prepareRecord();
                firstPoint.setLocation(x, y);
                lastPoint.setLocation(x, y);
                overlayG.setColor(new Color(mgDraw.iColor));
                overlayG.drawLine(x, y, x, y);
                break;

            case MouseEvent.MOUSE_DRAGGED: 
                if(isRecording)
                {
                	overlayG.clearEchoXOR();
                	//overlayG.drawLine(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y);
                	overlayG.drawLine(firstPoint.x, firstPoint.y, x, y);
                    lastPoint.setLocation(x,y);
                }
                break;

            case MouseEvent.MOUSE_RELEASED: 
            	overlayG.clearEchoXOR();
                if(!isRecording)
                    break;
                int oldIPen = mgDraw.iPen;
                mgDraw.iPen = Mg.P_SOLID;
                mgDraw.dStart(firstPoint.x, firstPoint.y, -1, true, true);	                
                mgDraw.dNext(x, y, -1);
                endRecord();
                mgDraw.iPen = oldIPen;
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}
	
}