package paintchat.saistyle.brushactions;


import java.awt.Cursor;

import paintchat.Mg;
import paintchat_client.SaiMi;

public abstract class AbstractBrushAction {
	protected Mg.Info info; 	// direct link to mg.info
	protected Mg.User user;
	protected Mg mgDraw;	// user info mg 
	protected Mg mgInfo;	// canvas mg that draws buffer ( i think )
	protected SaiMi saiMi;
	
	protected boolean isRecording;    	
	
	public AbstractBrushAction(SaiMi mi, Mg mgDraw) {
		this.saiMi = mi;
		this.info = mi.info;
		this.user = mi.user;
		this.mgInfo = mi.info.m;
		this.mgDraw = mgDraw;
		this.isRecording = false;
	}
	
	public abstract void mouseAction(int meID, int x, int y, int pressure);    	
	
	protected void prepareRecord() {
		isRecording = true;
		mgDraw.set(mgInfo);
	        if(mgDraw.iPen == Mg.P_FUSION)
	        	mgDraw.iLayerSrc = mgDraw.iLayer;
	        setA();    	   
	}
	
	public boolean isRecording() {
		return isRecording;
	}
	
	/**
	 * Abort this mouse
	 */
	public abstract void abort();
	
	protected void endRecord() throws InterruptedException {
        // if FUSION
        if(mgDraw.iPen == Mg.P_FUSION && mgDraw.iHint != Mg.H_CLEAR  && mgDraw.iHint != Mg.H_L)
        {
            int oldWait = user.wait;
            user.wait = -2;
            mgDraw.dEnd();	// finalize draw event
            int currentLayer = mgDraw.iLayer;
            for(int layerIndex = currentLayer + 1; layerIndex < info.L; layerIndex++)
                if(info.visit[layerIndex] != 0.0F)
                {
                	mgDraw.iLayerSrc = layerIndex;
                    setA();
                    mgDraw.draw();
                    saiMi.send(mgDraw);
                }

            for(int layerIndex = currentLayer - 1; layerIndex >= 0; layerIndex--)
                if(info.visit[layerIndex] != 0.0F)
                {
                    mgDraw.iLayerSrc = layerIndex;
                    setA();
                    mgDraw.draw();
                    saiMi.send(mgDraw);
                }

            user.wait = oldWait;
            saiMi.repaint();
        } else
        {
            mgDraw.dEnd();
            saiMi.send(mgDraw);
        }
        isRecording = false;
	}
	
	// set the alpha when we draw a layer i believe
	private void setA()
    {
		mgDraw.iAlpha2 = (int)(info.visit[mgDraw.iLayer] * 255F) << 8 | (int)(info.visit[mgDraw.iLayerSrc] * 255F);
    }
	
	/***
	 * Draw cursor: maybe need to show shadow of movement (by drawing last X and current X
	 * @param x
	 * @param y
	 */
	public void drawCursor(int meID, int x, int y) {}
	
	public Cursor getCursor(int meID, int x, int y) {
		return Cursor.getDefaultCursor();
	}
	
	public void reset(){
		if(isRecording)
        {
            isRecording = false;
            switch(mgInfo.iHint)
            {
            case 0: // '\0'
                mgDraw.reset();
                break;

            default:
                saiMi.m_paint(null);
                break;
            }
        }
	}
}
