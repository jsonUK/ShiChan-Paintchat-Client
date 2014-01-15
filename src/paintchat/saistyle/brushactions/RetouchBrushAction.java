package paintchat.saistyle.brushactions;

import java.awt.Color;

import paintchat.Mg;
import paintchat.saistyle.OverlayGraphics;
import paintchat_client.SaiMi;

public class RetouchBrushAction extends AbstractSelectBrushAction {
	
	public RetouchBrushAction(SaiMi mi, Mg mgDraw) {
		super(mi, mgDraw, Color.magenta);		
	}

	private int [] getMgPoints() {
		int [] movePoints = new int[4];
		movePoints[0] = pointToInt(selectRect.x, selectRect.y);
		movePoints[1] = pointToInt(selectRect.x + selectRect.width, selectRect.y + selectRect.height);
		return movePoints;
	}	
	
	private void applyRetouch(int pen) {
		try {
			if(isSelected) {							
				mgInfo.iPen = pen;
				OverlayGraphics g = saiMi.getOverlayGraphics();
				this.drawPermRect(g, false);		
				prepareRecord();
				mgDraw.dRetouch(getMgPoints());
				endRecord();
				isRecording = true; // let our parent class handle recording
				this.drawPermRect(g, true);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void applyFlipHorizontal() {
		applyRetouch(Mg.P_UD);
	}
	
	public void applyFlipVertical() {
		applyRetouch(Mg.P_LR);
	}
	
	public void applyErase() {
		applyRetouch(Mg.P_WHITE);
	}
	
	
	public void applyMerge() {
		applyRetouch(Mg.P_FUSION);
	}

	@Override
	public void mouseAction(int meID, int x, int y) {
		super.handleSelectMouseAction(meID, x, y);	
	}


	@Override
	protected void selectAction(int selectActionID) {
		// TODO Auto-generated method stub
		
	}

}
