// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 5/10/2009 12:34:58 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Mi.java

package paintchat_client;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import paintchat.Mg;
import paintchat.PCDebug;
import paintchat.Res;
import syi.awt.Awt;
import syi.awt.LComponent;

// Referenced classes of package paintchat_client:
//            Me, IMi

public class SaiMiBackup extends Mi
    implements ActionListener
{

    public SaiMiBackup(IMi imi1, Res res1)
        throws Exception
    {
    	super(imi1, res1);
        isRight = false;
        isEnable = false;
        ps = new int[5];
        psCount = -1;
        oldX = 0;
        oldY = 0;
        isIn = false;
        nowCurIndex = -1;
        imCopy = null;
        isSpace = false;
        isScroll = false;
        isDrag = false;
        poS = new Point();
        rS = new int[20];
        sizeBar = 20;
        cPre = Color.black;
        imi = imi1;
        res = res1;
        super.isRepaint = false;
        super.isHide = false;
        super.isGUI = false;
        super.iGap = 2;
        Me.res = res1;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(text != null)
        {
            addText(actionevent.getActionCommand());
            if(isText)
                text.setVisible(false);
        }
    }
    
    public void addText(String text)
    {
        try
        {
            info.text = text;
            mgInfo.iHint = 8;
            m.set(mgInfo);
            m.dRetouch(ps);
            dEnd();
        }
        catch(InterruptedException _ex) { }
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
        int cursor;
        if(x > w || y >= h)
            cursor = x >= sBar ? ((int) (y >= sBar ? ((int) (x <= w || y <= h ? 1 : 3)) : 0)) : 2;
        else
            cursor = 0;
        if(nowCurIndex != cursor)
        {
            nowCurIndex = cursor;
            setCursor(cursors[cursor]);
        }
    }

    private void dClear()
        throws InterruptedException
    {
        if(!alert("kakunin_1", true))
        {
            return;
        } else
        {
            m.set(mgInfo);
            m.dClear(true);
            dEnd();
            return;
        }
    }

    private void dCopy(int meID, int x, int y)
        throws InterruptedException
    {
        if(psCount <= 1)
        {
            if(meID == MouseEvent.MOUSE_RELEASED)
            {
                if(psCount <= 0)
                    return;
                psCount = 2;
                p(1, x, y);
                if(!transRect())
                {
                    psCount = -1;
                } else
                {
                    ps[0] = user.points[0];
                    ps[1] = user.points[1];
                    ps[2] = ps[0];
                }
                ps[4] = mgInfo.iLayer;
            } else
            {
                dRect(meID, x, y);
            }
            return;
        }
        int startX = ps[2] >> 16;
        int startY = (short)ps[2];
        int width = (ps[1] >> 16) - (ps[0] >> 16);
        int height = (short)ps[1] - (short)ps[0];
        switch(meID)
        {
        default:
            break;

        case MouseEvent.MOUSE_PRESSED: 
            if(imCopy != null)
                imCopy.flush();
            imCopy = m.getImage(ps[4], startX, startY, width, height);
            p(3, x, y);
            break;

        case MouseEvent.MOUSE_DRAGGED: 
            m_paint(startX, startY, width, height);
            startX += x - (ps[3] >> 16);
            startY += y - (short)ps[3];
            p(2, startX, startY);
            p(3, x, y);
            primary2.setPaintMode();
            primary2.drawImage(imCopy, startX, startY, width, height, Color.white, null);
            primary2.setXORMode(Color.white);
            break;

        case MouseEvent.MOUSE_RELEASED: 
            startX += x - (ps[3] >> 16);
            startY += y - (short)ps[3];
            p(2, startX, startY);
            m.set(mgInfo);
            m.iLayerSrc = ps[4];
            m.dRetouch(ps);
            dEnd();
            psCount = -1;
            break;
        }
    }

    private void dEnd()
        throws InterruptedException
    {
        Mg mg = m;
        // if 
        if(mg.iHint != Mg.H_CLEAR && mg.iPen == Mg.P_FUSION && mg.iHint != Mg.H_L)
        {
            int i = user.wait;
            user.wait = -2;
            mg.dEnd();	// finalize draw event
            int layerIndex = mg.iLayer;
            for(int k = layerIndex + 1; k < info.L; k++)
                if(info.visit[k] != 0.0F)
                {
                    mg.iLayerSrc = k;
                    setA();
                    mg.draw();
                    send(mg);
                }

            for(int l = layerIndex - 1; l >= 0; l--)
                if(info.visit[l] != 0.0F)
                {
                    mg.iLayerSrc = l;
                    setA();
                    mg.draw();
                    send(mg);
                }

            user.wait = i;
            repaint();
        } else
        {
            mg.dEnd();
            send(mg);
        }
    }

    private void dFLine(int mId, int x, int y, int pressure)
    {
        try
        {
            switch(mId)
            {
            default:
                break;

            case MouseEvent.MOUSE_PRESSED: 
                setM();
                m.dStart(x, y, pressure, true, true);
                oldX = 0;
                oldY = 0;
                psCount = 1;
                p(0, x, y);
                break;

            case MouseEvent.MOUSE_DRAGGED: 
                if(psCount >= 0 && isOKPo(x, y))
                {
                    psCount = 0;
                    m.dNext(x, y, pressure);
                    p(psCount, x, y);
                    psCount++;
                }
                break;

            case MouseEvent.MOUSE_RELEASED: 
                if(psCount >= 0)
                {
                    dEnd();
                    psCount = -1;
                }
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private void dLine(int meID, int x, int y)
    {
        try
        {
            switch(meID)
            {
            default:
                break;

            case MouseEvent.MOUSE_PRESSED: 
                setM();
                for(int l = 0; l < 4; l++)
                    p(l, x, y);

                psCount = 0;
                primary2.setColor(new Color(m.iColor));
                primary2.drawLine(x, y, x, y);
                break;

            case MouseEvent.MOUSE_DRAGGED: 
                if(psCount >= 0)
                {
                    primary2.drawLine(ps[0] >> 16, (short)ps[0], ps[1] >> 16, (short)ps[1]);
                    primary2.drawLine(ps[0] >> 16, (short)ps[0], x, y);
                    p(1, x, y);
                }
                break;

            case MouseEvent.MOUSE_RELEASED: 
                if(psCount < 0)
                    break;
                m.dStart(ps[0] >> 16, (short)ps[0], -1, true, true);
                if(m.iPen == 1)
                {
                    for(int i1 = 0; i1 < 5; i1++)
                        m.dNext(x, y, -1);

                }
                m.dNext(x, y, -1);
                dEnd();
                psCount = -1;
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    
    /**
     * TODO: add more changes if we change tools 
     */
    public void mgChange() {
    	// do nothing
    }

    private final void dPre(int x, int y, boolean useOldCoord)
    {
        if(mgInfo == null || mgInfo.iHint == 8 || primary2 == null)
            return;
        try
        {
            int penSize = info.getPenMask()[mgInfo.iPenM][mgInfo.iSize].length;
            int penSizeScaled = ((int)Math.sqrt(penSize) * info.scale) / info.Q;
            Graphics g = primary2;
            Color color = cPre;
            color = (color.getRGB() & 0xffffff) == mgInfo.iColor >>> 1 ? color : new Color(mgInfo.iColor >>> 1);
            cPre = color;
            g.setColor(mgInfo.iPen != 4 && mgInfo.iPen != 5 ? color.getRGB() != 0xffffff ? color : Color.red : Color.cyan);
            if(penSize <= 1)
            {
                if(useOldCoord)
                    g.fillRect(oldX, oldY, penSizeScaled, penSizeScaled);
                g.fillRect(x, y, penSizeScaled, penSizeScaled);
            } else
            {
                int penRadius = penSizeScaled >>> 1;
                if(useOldCoord)
                    g.drawOval(oldX - penRadius, oldY - penRadius, penSizeScaled, penSizeScaled);
                g.drawOval(x - penRadius, y - penRadius, penSizeScaled, penSizeScaled);
            }
            oldX = x;
            oldY = y;
        }
        catch(RuntimeException _ex) { }
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

    private void dRect(int meID, int x, int y)
    {
        try
        {
            switch(meID)
            {
            default:
                break;

            case MouseEvent.MOUSE_PRESSED: 
                setM();
                p(0, x, y);
                user.points[1] = user.points[0] = 0;
                psCount = 1;
                break;

            case MouseEvent.MOUSE_DRAGGED: 
                if(psCount == 1)
                {
                    int firstPoints = user.points[0];
                    int secondPoints = user.points[1];
                    int firstX = firstPoints >> 16;
                    short word0 = (short)firstPoints;
                    primary2.drawRect(firstX, word0, (secondPoints >> 16) - firstX - 1, (short)secondPoints - word0 - 1);
                    p(1, x, y);
                    transRect();
                    firstPoints = user.points[0];
                    secondPoints = user.points[1];
                    firstX = firstPoints >> 16;
                    word0 = (short)firstPoints;
                    primary2.drawRect(firstX, word0, (secondPoints >> 16) - firstX - 1, (short)secondPoints - word0 - 1);
                }
                break;

            case MouseEvent.MOUSE_RELEASED: 
                if(psCount > 0)
                {
                    p(1, x, y);
                    if(transRect())
                    {
                        m.dRetouch(user.points);
                        dEnd();
                    }
                }
                psCount = -1;
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
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

    public void dText(int i, int j, int k)
    {
        switch(i)
        {
        default:
            break;

        case MouseEvent.MOUSE_PRESSED: 
            m_paint(null);
            break;

        case MouseEvent.MOUSE_RELEASED: 
            if(text == null)
            {
                text = new TextField(16);
                text.addActionListener(this);
                isText = true;
                getParent().add(text, 0);
            }
            if(!isText)
            {
                primary.setColor(new Color(mgInfo.iColor));
                primary.fillRect(j - 1, k - 1, mgInfo.iSize + 1, 1);
                primary.fillRect(j - 1, k, 1, mgInfo.iSize);
            } else
            {
                text.setFont(new Font("sanssefit", 0, mgInfo.iSize));
                text.setSize(text.getPreferredSize());
                Point point = getLocation();
                text.setLocation(j + point.x, k + point.y);
                text.setVisible(true);
            }
            p(0, j, k);
            break;
        }
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
        m = new Mg();
        user = m.newUser(this);
        paintchat.Mg.Info info1 = m.newInfo(applet, this, res1);
        info1.setSize(i, j, k);
        info1.setL(l);
        info = info1;
        mgInfo = info1.m;
        enableEvents(57L);
    }

    private final boolean isOKPo(int x, int y)
    {
        int lastPoint = ps[psCount - 1];
        return Math.max(Math.abs(x - (lastPoint >> 16)), Math.abs(y - (short)lastPoint)) >= info.scale;
    }

    public void m_paint(int startX, int startY, int width, int height)
    {
        Dimension dimension = info.getSize();
        if(m == null)
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
        m.m_paint(startX, startY, width, height);
    }

    public void m_paint(Rectangle rectangle)
    {
        if(rectangle == null)
            m_paint(0, 0, 0, 0);
        else
            m_paint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    private final void p(int psIndex, int x, int y)
    {
        ps[psIndex] = x << 16 | y & 0xffff;
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
    
    public void pMouse3(int meID, int relaX, int relaY, boolean rightClick, boolean altDown, boolean ctrlDown, int pressure, boolean fromLocal) {
    	try
        {   
    	//	PCDebug.println(relaX+"," +relaY+": "+ fromLocal);
    		
            //int scale = info.scale;
            boolean fMDown = meID == MouseEvent.MOUSE_PRESSED;	// mouse down
            boolean fMDrag = meID == MouseEvent.MOUSE_DRAGGED;	// mouse drag
            boolean fIsRight = isRight;		// set last right click            
            
            // draw cursor if we are not dragging, but MOUSE_PRESSED
            if(fMDown)
            {
            	// set right click global and local
                fIsRight = isRight = rightClick;
                if(!isDrag)
                    dPre(oldX, oldY, false);
                isDrag = true;
                // if we clicked on a part of scrollbar or zoom button do nothing
                if(b(relaX, relaY))
                    return;
                // 
                if(isText && text != null && text.isVisible())
                    text.setVisible(false);
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
                if(isScroll)
                {
                    isScroll = false;
                    imi.scroll(false, 0, 0);
                    if(info.scale < 1)
                        m_paint(null);
                    return;
                }                
            }
            // if we right clicked during a drag moment 
            if(isRight && isDrag)
            {
            	// if we had a click count more than 1, almost anything we do in art, clear
                if(psCount >= 0)
                {
                    reset();
                    isRight = false;
                    isDrag = false;
                    super.isPaint = true;
                } else
                {
                    imi.setARGB(user.getPixel(relaX / info.scale + info.scaleX, relaY / info.scale + info.scaleY));
                }
                return;
            }
            
            if(!isDrag)
            {
                cursor(meID, relaX, relaY);
                switch(meID)
                {
                default:
                    break;

                case MouseEvent.MOUSE_ENTERED: 
                    //getS();
                    break;

                case MouseEvent.MOUSE_MOVED: 
                    dPre(relaX, relaY, isIn);
                    isIn = true;
                    break;

                case MouseEvent.MOUSE_EXITED: 
                    if(isIn)
                    {
                        isIn = false;
                        dPre(oldX, oldY, false);
                    }
                    break;
                }
            }
            if(isSpace || isScroll)
            {
                dScroll(relaX,relaY, meID, 0, 0);
                return;
            }
            if(isEnable && ClientPermissions.isCanvas && !fIsRight)
                switch(mgInfo.iHint)
                {
            	case Mg.H_FLINE: // '\0'
                    dFLine(meID, relaX, relaY, pressure);
                    break;

                case Mg.H_LINE: // '\001'
                    dLine(meID, relaX, relaY);
                    break;

                case Mg.H_TEXT: // '\b'
                    dText(meID, relaX, relaY);
                    break;

                case Mg.H_COPY: // '\t'
                    dCopy(meID, relaX, relaY);
                    break;

                case Mg.H_CLEAR: // '\n'
                    if(ClientPermissions.isClear && fMDown)
                        dClear();
                    break;

                case Mg.H_FILL: // '\007'
                    if(ClientPermissions.isFill && fMDown)
                    {
                        m.set(info.m);
                        p(0, relaX, relaY);
                        p(1, relaX + 1024, relaY + 1024);
                        transRect();
                        m.dRetouch(user.points);
                        dEnd();
                    }
                    break;

                default:
                    dRect(meID, relaX, relaY);
                    break;
                }
            if(meID == MouseEvent.MOUSE_RELEASED && isIn)
            {
                dPre(relaX, relaY, false);
                isDrag = false;
            }
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
            boolean fMDrag = meID == MouseEvent.MOUSE_DRAGGED;	// mouse drag
            boolean fIsRight = isRight;		// set last right click
            
            // ok to remove
            if(altDown && ctrlDown)
            {
                if(psCount >= 0)
                    reset();
                if(fMDown)
                {
                    poS.y = relaY;
                    poS.x = mgInfo.iSize;
                }
                if(fMDrag)
                    imi.setLineSize((relaY - poS.y) / 4 + poS.x);
                return;
            }
            
            // draw cursor if we are not dragging, but MOUSE_PRESSED
            if(fMDown)
            {
            	// set right click global and local
                fIsRight = isRight = rightClick;
                if(!isDrag)
                    dPre(oldX, oldY, false);
                isDrag = true;
                // if we clicked on a part of scrollbar or zoom button do nothing
                if(b(relaX, relaY))
                    return;
                // 
                if(isText && text != null && text.isVisible())
                    text.setVisible(false);
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
                if(isScroll)
                {
                    isScroll = false;
                    imi.scroll(false, 0, 0);
                    if(info.scale < 1)
                        m_paint(null);
                    return;
                }                
            }
            // if we right clicked during a drag moment 
            if(isRight && isDrag)
            {
            	// if we had a click count more than 1, almost anything we do in art, clear
                if(psCount >= 0)
                {
                    reset();
                    isRight = false;
                    isDrag = false;
                    super.isPaint = true;
                } else
                {
                    imi.setARGB(user.getPixel(relaX / info.scale + info.scaleX, relaY / info.scale + info.scaleY));
                }
                return;
            }
            
            if(!isDrag)
            {
                cursor(meID, relaX, relaY);
                switch(meID)
                {
                default:
                    break;

                case MouseEvent.MOUSE_ENTERED: 
                    //getS();
                    break;

                case MouseEvent.MOUSE_MOVED: 
                    dPre(relaX, relaY, isIn);
                    isIn = true;
                    break;

                case MouseEvent.MOUSE_EXITED: 
                    if(isIn)
                    {
                        isIn = false;
                        dPre(oldX, oldY, false);
                    }
                    break;
                }
            }
            if(isSpace || isScroll)
            {
                dScroll(relaX,relaY, meID, 0, 0);
                return;
            }
            if(isEnable && ClientPermissions.isCanvas && !fIsRight)
                switch(mgInfo.iHint)
                {
            	case Mg.H_FLINE: // '\0'
                    dFLine(meID, relaX, relaY, pressure);
                    break;

                case Mg.H_LINE: // '\001'
                    dLine(meID, relaX, relaY);
                    break;

                case Mg.H_TEXT: // '\b'
                    dText(meID, relaX, relaY);
                    break;

                case Mg.H_COPY: // '\t'
                    dCopy(meID, relaX, relaY);
                    break;

                case Mg.H_CLEAR: // '\n'
                    if(ClientPermissions.isClear && fMDown)
                        dClear();
                    break;

                case Mg.H_FILL: // '\007'
                    if(ClientPermissions.isFill && fMDown)
                    {
                        m.set(info.m);
                        p(0, relaX, relaY);
                        p(1, relaX + 1024, relaY + 1024);
                        transRect();
                        m.dRetouch(user.points);
                        dEnd();
                    }
                    break;

                default:
                    dRect(meID, relaX, relaY);
                    break;
                }
            if(meID == MouseEvent.MOUSE_RELEASED && isIn)
            {
                dPre(relaX, relaY, false);
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
            case 100: // 'd'
            case 101: // 'e'
            case 102: // 'f'
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
                    isSpace = true;
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
                    isSpace = false;
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

    /**
     * Clear current action
     */
    public void reset()
    {
        if(psCount >= 0)
        {
            psCount = -1;
            switch(mgInfo.iHint)
            {
            case Mg.H_FLINE: // '\0'
                m.reset();
                break;

            default:
                m_paint(null);
                break;
            }
        }
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
            primary2 = primary.create(0, 0, i, j);
            primary2.setXORMode(Color.white);
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
            if(dimension.width == l && dimension.height == i1)
                repaint();
            else
                imi.changeSize();
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
        int scaleX = info.scaleX;
        int scaleY = info.scaleY;
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
        info.scaleX = Math.max(scaleX + k1, 0);
        info.scaleY = Math.max(scaleY + l1, 0);
        drawScroll(g);
        poS.translate(x, y);
        int i2 = (int)((float)(info.scaleX - scaleX) * scale);
        int j2 = (int)((float)(info.scaleY - scaleY) * scale);
        k1 = dimension.width - Math.abs(i2);
        l1 = dimension.height - Math.abs(j2);
        try
        {
            g.copyArea(Math.max(i2, 0), Math.max(j2, 0), k1, l1, -i2, -j2);
            if(scale >= 1.0F)
            {
                if(i2 != 0)
                    if(i2 > 0)
                        m_paint(dimension.width - i2, 0, i2, dimension.height);
                    else
                        m_paint(0, 0, -i2, dimension.height);
                if(j2 != 0)
                    if(j2 > 0)
                        m_paint(0, dimension.height - j2, dimension.width, j2);
                    else
                        m_paint(0, 0, dimension.width, -j2);
            } else
            {
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

    private void send()
    {
        imi.send(m);
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
        m.iAlpha2 = (int)(info.visit[m.iLayer] * 255F) << 8 | (int)(info.visit[m.iLayerSrc] * 255F);
    }

    private final void setM()
    {
        m.set(mgInfo);
        if(m.iPen == Mg.P_FUSION)
            m.iLayerSrc = m.iLayer;
        setA();
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

    public boolean transRect()
    {
        int firstPoint = ps[0];
        int x = firstPoint >> 16;
        short y = (short)firstPoint;
        int secondPoint = ps[1];
        int x2 = secondPoint >> 16;
        short y2 = (short)secondPoint;
        int l = Math.max(Math.min(x, x2), 0);
        int i1 = Math.min(Math.max(x, x2), info.imW * info.scale);
        int j1 = Math.max(Math.min(y, y2), 0);
        int k1 = Math.min(Math.max(y, y2), info.imH * info.scale);
        if(i1 - l < info.scale || k1 - j1 < info.scale)
        {
            return false;
        } else
        {
            user.points[0] = l << 16 | j1 & 0xffff;
            user.points[1] = i1 << 16 | k1 & 0xffff;
            return true;
        }
    }
    
    private static abstract class AbstractBrushAction {
    	
    	protected boolean rightClickUsed;
    	
    	protected Mg.Info info; 	// direct link to mg.info
    	protected Mg.User user;
    	protected Mg mgDraw;	// user info mg 
    	protected Mg mgInfo;	// canvas mg that draws buffer ( i think )
    	protected SaiMi saiMi;
    	protected boolean isRecording;    	
    	
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
    	
    	protected void endRecord() throws InterruptedException {
            // if 
            if(mgDraw.iHint != Mg.H_CLEAR && mgDraw.iPen == Mg.P_FUSION && mgDraw.iHint != Mg.H_L)
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
    
    private static class FreeLineBrushAction extends AbstractBrushAction {

    	private int lastX, lastY;
    	
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
	            
	            lastX = x;
				lastY = y;
	        }
	        catch(Throwable throwable)
	        {
	            throwable.printStackTrace();
	        }
		}
		
		private boolean validScaledMovement(int curX, int curY) {
			return Math.max(Math.abs(curX - lastX), Math.abs(curY - lastY)) >= info.scale;
		}		
    }
    

    private IMi imi;
    private Res res;
    
    private boolean isRight;
    private boolean isText;
    private boolean isSpace;
    private boolean isScroll;
    private boolean isDrag;
    private boolean isIn;    
    
    private Mg m;
    private Mg mgInfo;
    
    /*public TextField text;    
    public boolean isEnable;
    public paintchat.Mg.Info info;   
    public paintchat.Mg.User user;
    */
    
    private int ps[];
    private int psCount;
    
    private int oldX;
    private int oldY;
    
    private Graphics primary;
    private Graphics primary2;
    
    
    private int nowCurIndex;
    private Cursor cursors[];
    private Image imCopy;
    
    private Point poS;
    private int rS[];
    private int sizeBar;
    
    private Color cls[];
    private Color cPre;
}