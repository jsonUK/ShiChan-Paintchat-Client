// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 5/10/2009 12:34:58 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Mi.java

package paintchat_client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.naming.directory.InvalidAttributeIdentifierException;

import paintchat.Mg;
import paintchat.Res;
import paintchat.Mg.Info;
import paintchat.saistyle.OverlayGraphics;
import paintchat.saistyle.ToolTypeListener;
import paintchat.saistyle.brushactions.AbstractBrushAction;
import paintchat.saistyle.brushactions.EmptyBrushAction;
import paintchat.saistyle.brushactions.FreeLineBrushAction;
import paintchat.saistyle.brushactions.MoveBrushAction;
import paintchat.saistyle.brushactions.RectangleBrushAction;
import paintchat.saistyle.brushactions.RetouchBrushAction;
import paintchat.saistyle.brushactions.StraightLineBrushAction;

// Referenced classes of package paintchat_client:
//            Me, IMi

public class SaiMi extends Mi
    implements ActionListener, ToolTypeListener
{

    public SaiMi(IMi imi1, Res res1)
        throws Exception
    {
    	super(imi1, res1);
        isRight = false;
        isEnable = false;
        isIn = false;
        curCursor = null;
        isScroll = false;
        isDrag = false;
        poS = new Point();
        brushActionMap = new HashMap<Integer, AbstractBrushAction>();
        rS = new int[20];
        sizeBar = 20;
        imi = imi1;
        res = res1;
        super.isRepaint = false;
        super.isHide = false;
        super.isGUI = false;
        super.iGap = 2;
        Me.res = res1;
    }

    public boolean alert(String msg, boolean flag)
    {
        try
        {
            return Me.confirm(msg, flag);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
        return true;
    }

    /**
     * See if we are clicking in a corner or on the scroll bar
     * @param x
     * @param y
     * @return
     * @throws Throwable
     */
    private boolean b(int x, int y)
        throws Throwable
    {
        if(!in(x, y))
            return false;
        Dimension dimension = info.getSize();
        int w = dimension.width;
        int h = dimension.height;
        if(x > w || y > h)
        {
            if(x < sizeBar)
                scaleChange(isRight ? -1 : 1, false);
            else
            if(y < sizeBar)
            {
                // tab bar would haev gone here
            } else
            if(x > w && y > h)
            {
                scaleChange(isRight ? 1 : -1, false);
            } else
            {
                isIn = false;
                imi.scroll(true, 0, 0);
                isScroll = true;
                poS.setLocation(x, y);
            }
            isB = true;
            return true;
        } else
        {
            return false;
        }
    }

    private void cursor(int meID, int x, int y)
    {
        if(meID != MouseEvent.MOUSE_MOVED)
            return;
        Dimension dimension = info.getSize();
        int sBar = sizeBar;
        int w = dimension.width;
        int h = dimension.height;
        Cursor nextCursor;
        if(x > w || y >= h) {
            nextCursor = cursors[x >= sBar ? ((int) (y >= sBar ? ((int) (x <= w || y <= h ? 1 : 3)) : 0)) : 2];
        }
        else {             
        	nextCursor = currentBrushAction.getCursor(meID, x, y);            
        }                
        
        if(curCursor != nextCursor)
        {
            curCursor = nextCursor;
            setCursor(nextCursor);
        }
    }

    private boolean doMassClear()
        throws InterruptedException
    {
    	if(!ClientPermissions.isClear) {
    		return false;
    	}
    	
        if(!alert("kakunin_1", true))
        {
            return false;
        } else
        {
            mgDraw.set(mgInfo);
            mgDraw.dClear(true);
            mgDraw.dEnd();
            send(mgDraw);
            return true;
        }
    }
    
    /**
     * TODO: add more changes if we change tools 
     */
    public void mgChange() {
    	try {
    		updateCurrentBrush();
    	}
    	catch(Throwable t) {
    		t.printStackTrace();
    	}
    }


    public void drawScroll(Graphics g)
    {
        try
        {
            if(g == null)
                g = primary;
            if(g == null)
                return;
            float scale = info.scale;
            int sBar = sizeBar;
            int scaleX = info.scaleX;
            int scaleY = info.scaleY;
            int imgW = info.imW;
            int imgH = info.imH;
            Dimension dimension = info.getSize();
            int scaledW = (int)((float)dimension.width / scale);
            int scaledH = (int)((float)dimension.height / scale);
            if(scaleX + scaledW >= imgW)
            {
                scaleX = Math.max(0, imgW - scaledW);
                info.scaleX = scaleX;
            }
            if((scaleY + scaledH) - 1 >= imgH)
            {
                scaleY = Math.max(0, imgH - scaledH);
                info.scaleY = scaleY;
            }
            int j2 = dimension.width - sBar;
            int k2 = dimension.height - sBar;
            int l2 = Math.min((int)(((float)scaledW / (float)imgW) * (float)j2), j2);
            int i3 = Math.min((int)(((float)scaledH / (float)imgH) * (float)k2), k2);
            int j3 = (int)(((float)scaleX / (float)imgW) * (float)j2);
            int k3 = (int)(((float)scaleY / (float)imgH) * (float)k2);
            int ai[] = rS;
            g.setColor(cls[0]);
            for(int i = 0; i < 20; i += 4)
                g.drawRect(ai[i], ai[i + 1], ai[i + 2], ai[i + 3]);

            if(j3 > 0)
            {
                g.setColor(cls[2]);
                g.drawRect(ai[0] + 1, ai[1] + 1, j3 - 2, ai[3] - 2);
                g.setColor(cls[1]);
                g.fillRect(ai[0] + 2, ai[1] + 2, j3 - 2, ai[3] - 3);
            }
            g.setColor(cls[2]);
            g.drawRect(ai[0] + j3 + l2, ai[1] + 1, ai[2] - j3 - l2 - 1, ai[3] - 2);
            g.setColor(cls[1]);
            g.fillRect(ai[0] + 1 + j3 + l2, ai[1] + 2, ai[2] - j3 - l2 - 2, ai[3] - 3);
            g.setColor(cls[1]);
            if(k3 > 0)
            {
                g.setColor(cls[2]);
                g.drawRect(ai[4] + 1, ai[5] + 1, ai[6] - 2, k3 - 1);
                g.setColor(cls[1]);
                g.fillRect(ai[4] + 2, ai[5] + 2, ai[6] - 3, k3 - 1);
            }
            g.setColor(cls[2]);
            g.drawRect(ai[4] + 1, ai[5] + k3 + i3, ai[6] - 2, ai[7] - k3 - i3 - 1);
            g.setColor(cls[1]);
            g.fillRect(ai[4] + 2, ai[5] + k3 + i3, ai[6] - 3, ai[7] - k3 - i3 - 1);
            for(int j = 8; j < 20; j += 4)
            {
                for(int l3 = 0; l3 < 2; l3++)
                {
                    g.setColor(cls[2 - l3]);
                    if(l3 == 0)
                        g.drawRect(ai[j] + 1, ai[j + 1] + 1, ai[j + 2] - 2, ai[j + 3] - 2);
                    else
                        g.fillRect(ai[j] + 2, ai[j + 1] + 2, ai[j + 2] - 3, ai[j + 3] - 3);
                }

                g.setColor(cls[3]);
                int i4 = ai[j + 2] / 2;
                int k4 = ai[j + 3] / 2;
                if(j == 16)
                {
                    int i5 = ai[j] + i4 / 2;
                    int k5 = ai[j + 1] + k4 / 2;
                    k4 /= 2;
                    g.drawRect(i5, k5, i4, k4);
                    g.fillRect(i5, k5 + k4, 1, k4);
                } else
                {
                    g.fillRect(ai[j] + i4 / 2, ai[j + 1] + k4, i4 + 1, 1);
                    if(j == 8)
                        g.fillRect(ai[j] + i4, ai[j + 1] + k4 / 2, 1, k4);
                }
            }

            int j4 = ai[0] + j3;
            int l4 = ai[1] + 1;
            int j5 = ai[4] + 1;
            int l5 = ai[5] + k3;
            g.setColor(cls[0]);
            g.drawRect(j4, l4, l2, ai[3] - 2);
            g.drawRect(j5, l5, ai[6] - 2, i3 + 1);
            g.setColor(cls[3]);
            g.fillRect(j4 + 2, l4 + 2, l2 - 3, ai[3] - 5);
            g.fillRect(j5 + 2, l5 + 2, ai[6] - 5, i3 - 2);
            g.setColor(cls[4]);
            g.fillRect(j4 + 1, l4 + 1, l2 - 2, 1);
            g.fillRect(j4 + 1, l4 + 2, 1, ai[3] - 5);
            g.fillRect(j5 + 1, l5 + 1, ai[6] - 4, 1);
            g.fillRect(j5 + 1, l5 + 2, 1, i3 - 2);
            g.setColor(cls[5]);
            g.fillRect((j4 + l2) - 1, l4 + 1, 1, ai[3] - 4);
            g.fillRect(j4 + 1, (l4 + ai[3]) - 3, l2 - 1, 1);
            g.fillRect((j5 + ai[6]) - 3, l5 + 1, 1, i3 - 1);
            g.fillRect(j5 + 1, l5 + i3, ai[6] - 3, 1);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    
    public AbstractBrushAction getCurrentBrush() {
    	return currentBrushAction;
    }
    
    public final OverlayGraphics getOverlayGraphics() {
    	return primary2;
    }


    public void dScroll(int relaX, int relaY, int meID, int i, int j)
    {
        if(i != 0 || j != 0)
        {
            scroll(i, j);
            return;
        }
        
        
        if(meID == MouseEvent.MOUSE_RELEASED || meID == MouseEvent.MOUSE_DRAGGED)
        {
            scroll(relaX - poS.x, relaY - poS.y);
            poS.x = relaX;
            poS.y = relaY;
        } else
        if(meID == MouseEvent.MOUSE_PRESSED) {
        	poS.x = relaX;
            poS.y = relaY;
        }
    }
    
    private final void updateCurrentBrush() throws InvalidAttributeIdentifierException {
    	// update current tool
    	int toolType = info.getToolType();
    	// since both select and drag map to same brush, lets set our key to SELECT    	
    	AbstractBrushAction ba = brushActionMap.get(toolType);    	
    	    	
    	if(ba == null) {
			switch(toolType) {
				case Mg.TT_RETOUCH:
					ba = new RetouchBrushAction(this, mgDraw);
					break;
				case Mg.TT_DRAGMOVE:
					ba = new MoveBrushAction(this, mgDraw);
					break;
				case Mg.TT_PEN:
					ba = new FreeLineBrushAction(this, mgDraw);					
					break;
				case Mg.TT_SHAPE_STRAIGHTLINE:
					ba = new StraightLineBrushAction(this, mgDraw);
					break;
				case Mg.TT_SHAPE_RECTANGLE:
					ba = new RectangleBrushAction(this, mgDraw);
					break;
				case Mg.TT_SHAPE_OVAL:
					ba = new RectangleBrushAction(this, mgDraw);
					break;
				default:
					ba = new EmptyBrushAction(this, mgDraw);
			}
			if(ba == null) {	// should never get here, but leave it just incase
				throw new InvalidAttributeIdentifierException("Invalid ToolType: " + toolType);
			}
			
			brushActionMap.put(toolType, ba);
    	}    
    	if(currentBrushAction != null) {
    		currentBrushAction.abort();
    	}
    	currentBrushAction = ba;
    }

    private final boolean in(int x, int y)
    {
        Dimension dimension = getSize();
        return x >= 0 && y >= 0 && x < dimension.width && y < dimension.height;
    }

    public void init(Applet applet, Res res1, int i, int j, int k, int l, Cursor acursor[])
        throws IOException
    {
        String s = "color_";
        String s1 = s + "_frame_";
        cursors = acursor;
        cls = new Color[6];
        cls[0] = new Color(res1.getP(s + "frame", 0x505078));
        cls[1] = new Color(res1.getP(s + "icon", 0xccccff));
        cls[2] = new Color(res1.getP(s + "bar_hl", 0xffffff));
        cls[3] = new Color(res1.getP(s + "bar", 0x6f6fae));
        cls[4] = new Color(res1.getP(s1 + "hl", 0xeeeeff));
        cls[5] = new Color(res1.getP(s1 + "shadow", 0xaaaaaa));
        setBackground(Color.white);
        mgDraw = new Mg();
        user = mgDraw.newUser(this);
        paintchat.Mg.Info info1 = mgDraw.newInfo(applet, this, res1);
        info1.setSize(i, j, k);
        info1.setL(l);
        info = info1;
        mgInfo = info1.m;
        info.addToolTypeListener(this);
        enableEvents(57L);
    }

    public void m_paint(int startX, int startY, int width, int height)
    {
        Dimension dimension = info.getSize();
        if(mgDraw == null)
        {
            primary.clearRect(0, 0, dimension.width, dimension.height);
            return;
        }
        if(startX == 0 && startY == 0 && width == 0 && height == 0)
        {
            startX = startY = 0;
            width = dimension.width;
            height = dimension.height;
        } else
        {
            startX = Math.max(startX, 0);
            startY = Math.max(startY, 0);
            width = Math.min(width, dimension.width);
            height = Math.min(height, dimension.height);
        }
        mgDraw.m_paint(startX, startY, width, height);        
    }

    public void m_paint(Rectangle rectangle)
    {
        if(rectangle == null)
            m_paint(0, 0, 0, 0);
        else
            m_paint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public void paint2(Graphics g)
    {
        try
        {
            drawScroll(g);
            if(!super.isPaint)
                return;
            m_paint(g.getClipBounds());            
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    
    public void pMouse2(int meID, int relaX, int relaY, boolean rightClick, boolean altDown, boolean ctrlDown, int pressure, boolean fromLocal) {
    	try
        {   
    	//	PCDebug.println(relaX+"," +relaY+": "+ fromLocal);
    		
            //int scale = info.scale;
            boolean fMDown = meID == MouseEvent.MOUSE_PRESSED;	// mouse down
            boolean fIsRight = isRight;		// set last right click            
            
            // draw cursor if we are not dragging, but MOUSE_PRESSED
            if(fMDown)
            {
            	// set right click global and local
                fIsRight = isRight = rightClick;
                primary2.clearEchoXOR();
                isDrag = true;
                // if we clicked on a part of scrollbar or zoom button do nothing
                if(b(relaX, relaY)){                	
                    return;
                }
            }
            if(meID == MouseEvent.MOUSE_RELEASED)	// unclick
            {
            	// if we were not dragging, return and do nothing
                if(!isDrag)
                    return;
                // reset right click and drag
                isRight = false;
                isDrag = false;
                // set the flag to paint this onto canvas
                super.isPaint = true;
                // if we were scrolling repaint tools if needed
                if(isB) {
                	isB = false;
                	if(isScroll)
                    {
                        isScroll = false;
                        imi.scroll(false, 0, 0);
                        if(info.scale < 1)
                            m_paint(null);
                    }
                	return;
                }                
            }            
            // if we right clicked during a drag moment 
            if(isRight && isDrag)
            {
            	// if we had a click count more than 1, almost anything we do in art, clear
                if(currentBrushAction.isRecording())
                {
                    currentBrushAction.reset();
                    isRight = false;
                    isDrag = false;
                    super.isPaint = true;
                } else
                {
                    imi.setARGB(user.getPixel(relaX / info.scale + info.scaleX, relaY / info.scale + info.scaleY));
                }
                return;
            }

            // on mouse hover
            if(!isDrag)
            {
                cursor(meID, relaX, relaY);
                switch(meID)
                {
                default:
                    break;

                case MouseEvent.MOUSE_MOVED: 
                	currentBrushAction.drawCursor(meID, relaX, relaY);
                    break;
                case MouseEvent.MOUSE_EXITED: 
                	//currentBrushAction.drawCursor(meID, relaX, relaY);
                	primary2.clearEchoXOR();
                    break;
                }
            }
            // if we are changing the zoom/scroll and not campus, handle
            if(isB) {
	            if(isScroll) {
	                dScroll(relaX,relaY, meID, 0, 0);	                
	            }
	            return;
            }
            
            if(isEnable && ClientPermissions.isCanvas && !fIsRight) {
                currentBrushAction.mouseAction(meID, relaX, relaY, pressure);
            }
            if(meID == MouseEvent.MOUSE_RELEASED && isIn)
            {
                currentBrushAction.drawCursor(meID, relaX, relaY);
                isDrag = false;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    

    public void pMouse(MouseEvent me)
    {

    }

    protected void processComponentEvent(ComponentEvent componentevent)
    {
        try
        {
            super.processComponentEvent(componentevent);
            switch(componentevent.getID())
            {
            case ComponentEvent.COMPONENT_FIRST: // 'd'
            case ComponentEvent.COMPONENT_RESIZED: // 'e'
            case ComponentEvent.COMPONENT_SHOWN: // 'f'
                if(primary != null && isVisible() && super.isPaint)
                {
                    resetGraphics();
                    return;
                }
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    protected void processKeyEvent(KeyEvent keyevent)
    {
        try
        {
            boolean flag = keyevent.isControlDown() || keyevent.isShiftDown();
            boolean flag1 = keyevent.isAltDown();
            boolean flag2 = true;
            switch(keyevent.getID())
            {
            default:
                break;

            case 401: 
                switch(keyevent.getKeyCode())
                {
                case 32: // ' '
                    
                    break;

                case 39: // '\''
                    scroll(5, 0);
                    break;

                case 38: // '&'
                    scroll(0, -5);
                    break;

                case 40: // '('
                    scroll(0, 5);
                    break;

                case 37: // '%'
                    scroll(-5, 0);
                    break;

                case 107: // 'k'
                    if(info.addScale(1, false))
                        m_paint(null);
                    break;

                case 109: // 'm'
                    if(info.addScale(-1, false))
                        m_paint(null);
                    break;

                case 82: // 'R'
                case 89: // 'Y'
                    flag2 = false;
                    // fall through

                case 90: // 'Z'
                    if(flag1)
                        flag2 = false;
                    if(flag)
                        imi.undo(flag2);
                    break;

                case 66: // 'B'
                    imi.setARGB(mgInfo.iAlpha << 24 | mgInfo.iColor);
                    break;

                case 69: // 'E'
                    imi.setARGB(0xffffff);
                    break;
                }
                break;

            case 402: 
                switch(keyevent.getKeyCode())
                {
                case 32: // ' '
                    
                    break;
                }
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    
    // overridden
    public void repaint(long delay, int x, int y, int w, int h)
    {
        try
        {
            if(primary == null)
                return;
            int j1 = info.scale;
            x -= j1;
            y -= j1;
            w += j1 * 2;
            h += j1 * 2;
            primary.translate(-getGapX(), -getGapY());
            primary.setClip(x, y, w, h);
            paint(primary);
            Dimension dimension = getSize();
            primary.translate(getGapX(), getGapY());
            primary.setClip(0, 0, dimension.width, dimension.height);
        }
        catch(RuntimeException _ex) { }
    }    

    public synchronized void resetGraphics()
    {
        if(primary != null)
            primary.dispose();
        if(primary2 != null)
            primary2.dispose();
        Dimension dimension = getSize();
        int i = dimension.width - sizeBar;
        int j = dimension.height - sizeBar;
        primary = getGraphics();
        if(primary != null)
        {
            primary.translate(getGapX(), getGapY());
            Graphics g2 = primary.create(0, 0, i, j);            
            g2.setXORMode(Color.white);
            if(primary2 == null) {
            	primary2 = new OverlayGraphics(g2, info);
            }
            else {
            	g2.setColor(primary2.getColor());
            	primary2.setGraphics(g2);            	
            }
            info.setComponent(this, primary, i, j);
        }
        int ai[] = rS;
        int k = sizeBar;
        dimension = info.getSize();
        for(int l = 0; l < 20; l++)
            ai[l] = k;

        ai[1] = dimension.height;
        ai[2] = dimension.width - k;
        ai[4] = dimension.width;
        ai[7] = dimension.height - k;
        ai[8] = 0;
        ai[9] = dimension.height;
        ai[12] = dimension.width;
        ai[13] = dimension.height;
        ai[16] = dimension.width;
        ai[17] = 0;              
    }

    public void scaleChange(int newScale, boolean flagFalse)
    {
        if(info.addScale(newScale, flagFalse) && !super.isGUI)
        {
            float f = info.scale;
            int j = (int)((float)info.imW * f) + sizeBar;
            int k = (int)((float)info.imH * f) + sizeBar;
            Dimension dimension = getSize();
            int l = dimension.width;
            int i1 = dimension.height;
            if(j != dimension.width || k != dimension.height)
                setSize(j, k);
            dimension = getSize();
            if(dimension.width == l && dimension.height == i1) {
                repaint();
            }
            else {
                imi.changeSize();
            }
        }
    }
    
	public void toolTypeChanged(Info info, Object source) {
		try {
			updateCurrentBrush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void repaint() {
		super.repaint();
		if(primary2 != null) {
			primary2.repaint();
		}
	}
	
    public void scroll(int x, int y)
    {
        if(info == null)
            return;
        Dimension dimension = info.getSize();
        int imgW = info.imW;
        int imgH = info.imH;
        float scale = info.scale;
        int oldScaleX = info.scaleX;
        int oldScaleY = info.scaleY;
        float f1 = (float)x * ((float)imgW / (float)dimension.width);
        if(scale < 1.0F)
            f1 /= scale;
        if(f1 != 0.0F)
            f1 = f1 < 0.0F || f1 > 1.0F ? f1 > 0.0F || f1 < -1F ? f1 : -1F : 1.0F;
        int k1 = (int)f1;
        f1 = (float)y * ((float)imgH / (float)dimension.height);
        if(f1 != 0.0F)
            f1 = f1 < 0.0F || f1 > 1.0F ? f1 > 0.0F || f1 < -1F ? f1 : -1F : 1.0F;
        if(scale < 1.0F)
            f1 /= scale;
        int l1 = (int)f1;
        Graphics g = primary;
        info.scaleX = Math.max(oldScaleX + k1, 0);
        info.scaleY = Math.max(oldScaleY + l1, 0);
        drawScroll(g);
        poS.translate(x, y);
        int i2 = (int)((float)(info.scaleX - oldScaleX) * scale);
        int j2 = (int)((float)(info.scaleY - oldScaleY) * scale);
        k1 = dimension.width - Math.abs(i2);
        l1 = dimension.height - Math.abs(j2);
        try
        {
            g.copyArea(Math.max(i2, 0), Math.max(j2, 0), k1, l1, -i2, -j2);
            if(scale >= 1.0F)
            {
            	//System.out.println("i0");
            	Rectangle oldBounds = new Rectangle();
            	primary2.getClipBounds(oldBounds);
                if(i2 != 0) {
                    if(i2 > 0) {
                        m_paint(dimension.width - i2, 0, i2, dimension.height);                        
                        primary2.setClip(dimension.width - i2, 0, i2, dimension.height);
                        //System.out.println("i1");
                    }
                    else {
                        m_paint(0, 0, -i2, dimension.height);
                        primary2.setClip(0, 0, -i2, dimension.height);
                        //System.out.println("i2");
                    }
                    primary2.repaint();
                }
                if(j2 != 0) {
                    if(j2 > 0) {
                        m_paint(0, dimension.height - j2, dimension.width, j2);
                        primary2.setClip(0, dimension.height - j2, dimension.width, j2);
                        //System.out.println("i3");
                    }
                    else {
                        m_paint(0, 0, dimension.width, -j2);
                        primary2.setClip(0, 0, dimension.width, -j2);
                        //System.out.println("i4");
                    }
                    primary2.repaint();
                }                
                primary2.setClip(oldBounds);
            } 
            else {
                if(i2 != 0)
                    if(i2 > 0)
                        g.clearRect(dimension.width - i2, 0, i2, dimension.height);
                    else
                        g.clearRect(0, 0, -i2, dimension.height);
                if(j2 != 0)
                    if(i2 > 0)
                        g.clearRect(0, dimension.height - j2, dimension.width, j2);
                    else
                        g.clearRect(0, 0, dimension.width, -j2);
            }
            imi.scroll(true, i2, j2);           
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }


    public void send(String s)
    {
        try
        {
            Mg mg = new Mg(info, user);
            mg.set(s);
            mg.draw();
            send(mg);
        }
        catch(Throwable _ex) { }
    }

    public void send(Mg mg)
        throws InterruptedException
    {
        imi.send(mg);
    }

    public void setA()
    {
        mgDraw.iAlpha2 = (int)(info.visit[mgDraw.iLayer] * 255F) << 8 | (int)(info.visit[mgDraw.iLayerSrc] * 255F);
    }

    public void setSize(int w, int h)
    {
        if(info == null)
        {
            super.setSize(w, h);
        } else
        {
            int scaledW = info.imW * info.scale + sizeBar;
            int scaledH = info.imH * info.scale + sizeBar;
            super.setSize(Math.min(w, scaledW), Math.min(h, scaledH));
        }
    }    

    private IMi imi;
    private Res res;
    
    private boolean isRight;
    private boolean isScroll;
    private boolean isDrag;
    private boolean isIn;    
    private boolean isB;
    
    private Mg mgDraw;
    private Mg mgInfo;
    
    private AbstractBrushAction currentBrushAction;
    private HashMap <Integer, AbstractBrushAction> brushActionMap;
    
    /*public TextField text;    
    public boolean isEnable;
    public paintchat.Mg.Info info;   
    public paintchat.Mg.User user;
    */
    
//    private int ps[];
//    private int psCount;
    
//    private int oldX;
//    private int oldY;
    
    private Graphics primary;
    private OverlayGraphics primary2;
    
    
    private Cursor curCursor;
    private Cursor cursors[];
//    private Image imCopy;
    
    private Point poS;
    private int rS[];
    private int sizeBar;
    
    private Color cls[];


}