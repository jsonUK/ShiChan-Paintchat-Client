// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/26/2009 2:01:08 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LComponent.java

package syi.awt;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

// Referenced classes of package syi.awt:
//            Awt

public abstract class LComponent extends Canvas
{

    public LComponent()
    {
        title = null;
        isUpDown = false;
        isGUI = true;
        isHide = true;
        isEscape = false;
        isPaint = true;
        isRepaint = true;
        rEscape = null;
        dSize = null;
        dVisit = null;
        dS = null;
        dL = null;
        pLocation = null;
        isMove = false;
        isResize = false;
        oldX = 0;
        oldY = 0;
        countResize = 0;
        if(Q == 0.0F)
            setup();
        iGap = Math.max((int)(4F * Q), 1);
        iBSize = Math.max((int)(12F * Q), 7);
        isRepaint = !isWin;
        setFont(Awt.getDefFont());
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
    }    

    public void escape(boolean flag)
    {
        if(isEscape == flag)
            return;
        isEscape = flag;
        if(flag)
        {
            rEscape = new Rectangle(getBounds());
            setBounds(0, 0, 1, 1);
        } else
        if(rEscape != null)
        {
            setBounds(rEscape);
            rEscape = null;
        }
    }

    private Cursor getCur(int i, int j)
    {
        byte byte0;
        switch(inCorner(i, j))
        {
        case 1: // '\001'
            byte0 = 6;
            break;

        case 2: // '\002'
            byte0 = 7;
            break;

        case 3: // '\003'
            byte0 = 4;
            break;

        case 4: // '\004'
            byte0 = 5;
            break;

        default:
            byte0 = 0;
            break;
        }
        return Cursor.getPredefinedCursor(byte0);
    }

    public int getGapH()
    {
        return getGapY() + iGap;
    }

    public int getGapW()
    {
        return iGap * 2;
    }

    public int getGapX()
    {
        return iGap;
    }

    public int getGapY()
    {
        return iGap + (isGUI ? iBSize : 0);
    }

    public Point getLocation()
    {
        if(pLocation == null)
            pLocation = super.getLocation();
        return pLocation;
    }

    public Dimension getMaximumSize()
    {
        return dL != null ? dL : getSize();
    }

    public Dimension getMinimumSize()
    {
        return dS != null ? dS : getSize();
    }

    public Dimension getPreferredSize()
    {
        return getSizeW();
    }

    public Dimension getSize()
    {
        if(dVisit == null)
            dVisit = new Dimension();
        dVisit.setSize(getSizeW());
        dVisit.width -= getGapW();
        dVisit.height -= getGapH();
        return dVisit;
    }

    public Dimension getSizeW()
    {
        if(dSize == null)
            dSize = super.getSize();
        return dSize;
    }

    private int inCorner(int i, int j)
    {
        Dimension dimension = getSizeW();
        byte byte0 = 0;
        int k = iGap;
        if(j <= k)
        {
            if(i <= k)
                byte0 = 1;
            if(i >= dimension.width - k)
                byte0 = 2;
        } else
        if(j >= dimension.height - k)
        {
            if(i <= k)
                byte0 = 3;
            if(i >= dimension.width - k)
                byte0 = 4;
        }
        return byte0;
    }

    public void inParent()
    {
        Container container = getParent();
        if(container == null)
            return;
        Point point = getLocation();
        Dimension dimension = container.getSize();
        Dimension dimension1 = getSizeW();
        int i = point.x;
        int j = point.y;
        int k = dimension1.width;
        int l = dimension1.height;
        i = i > 0 ? i + k < dimension.width ? i : dimension.width - k : 0;
        i = i > 0 ? i : 0;
        j = j > 0 ? j + l < dimension.height ? j : dimension.height - l : 0;
        j = j > 0 ? j : 0;
        if(i != point.x || j != point.y)
            setLocation(i, j);
        k = Math.min(k, dimension.width);
        l = Math.min(l, dimension.height);
        if(k != dimension1.width || l != dimension1.height)
            setSize(k - getGapW(), l - getGapH());
    }

    public void paint(Graphics g)
    {
        if(!isVisible())
            return;
        Dimension dimension = getSizeW();
        if(!isPaint && isMove)
        {
            g.drawRect(0, 0, dimension.width - 1, dimension.height - 1);
            return;
        }
        int i = iBSize;
        int j = iGap;
        int _tmp = j * 2;
        int k = dimension.width;
        int l = dimension.height;
        g.setColor(clFrame);
        g.drawRect(0, 0, k - 1, l - 1);
        if(isGUI)
        {
            g.fillRect(1, i, k - 2, 1);
            g.fillRect(k - i - 1, 1, 1, i - 1);
            g.setColor(clLBar);
            g.fillRect(1, 1, k - 2, 1);
            g.setColor(clBar);
            g.fillRect(1, 2, k - 2 - iBSize, iBSize - 2);
            g.drawLine((k - i) + 1, 2, k - 2, i - 1);
            g.drawLine((k - i) + 1, i - 1, k - 2, 1);
            if(title != null && title.length() > 0)
            {
                g.setClip(1, 1, k - 1 - iBSize, iBSize - 1);
                g.setFont(fontBar);
                g.setColor(clBarT);
                g.drawString(title, j, i - 1);
                g.setClip(0, 0, dimension.width, dimension.height);
            }
        }
        int i1 = getGapX();
        int j1 = getGapY();
        g.translate(i1, j1);
        try
        {
            paint2(g);
        }
        catch(Throwable _ex) { }
        g.translate(-i1, -j1);
    }

    public abstract void paint2(Graphics g);

    public abstract void pMouse(MouseEvent mouseevent);
    
    public void setCursor(Cursor c) {
    	super.setCursor(c);
    }

    protected void processEvent(AWTEvent awtevent)
    {
        try
        {
            int i = awtevent.getID();
            Dimension dimension = getSizeW();
            Point point = getLocation();
            switch(i)
            {
            case 101: // 'e'
                dimension.setSize(super.getSize());
                int j = dimension.width;
                int k = dimension.height;
                inParent();
                if(isRepaint)
                    getParent().repaint(0L, point.x, point.y, j, k);
                break;

            case 100: // 'd'
                point.setLocation(super.getLocation());
                int i1 = point.x;
                int k1 = point.y;
                inParent();
                if(isRepaint)
                    getParent().repaint(0L, i1, k1, dimension.width, dimension.height);
                break;
            }
            if(awtevent instanceof MouseEvent)
            {            	
                MouseEvent mouseevent = (MouseEvent)awtevent;
                mouseevent.consume();
                int l = mouseevent.getX();
                int j1 = mouseevent.getY();
                if(isGUI)
                {
                    Dimension dimension1 = getSizeW();
                    Dimension dimension2 = getSize();
                    int _tmp = iGap * 2;
                    boolean flag = false;
                    switch(mouseevent.getID())
                    {
                    case MouseEvent.MOUSE_MOVED: 
                        if(!getCursor().equals(getCur(l, j1)))
                            setCursor(getCur(l, j1));
                        if(isUpDown)
                        {
                            Container container = getParent();
                            if(container.getComponent(0) != this)
                            {
                                container.remove(this);
                                container.add(this, 0);
                            }
                        }
                        break;

                    case MouseEvent.MOUSE_PRESSED: 
                        oldX = l;
                        oldY = j1;
                        int l1 = inCorner(l, j1);
                        if(l1 != 0)
                        {
                            isMove = true;
                            isResize = true;
                            isPaint = false;
                            countResize = 0;
                            return;
                        }
                        if(j1 <= iBSize)
                        {
                            if(l >= dimension1.width - iBSize)
                            {
                                if(isHide)
                                    setVisible(false);
                                return;
                            }
                            if(mouseevent.getClickCount() % 2 == 0)
                            {
                                Dimension dimension3 = getMaximumSize();
                                if(dimension2.equals(dimension3))
                                {
                                    Dimension dimension5 = getMinimumSize();
                                    setSize((dimension3.width + dimension5.width) / 2, (dimension3.height + dimension5.height) / 2);
                                } else
                                {
                                    setSize(dimension3);
                                }
                                return;
                            } else
                            {
                                isMove = true;
                                isResize = false;
                                isPaint = false;
                                return;
                            }
                        }
                        break;

                    case MouseEvent.MOUSE_DRAGGED: 
                        if(isMove && ++countResize >= 4)
                        {
                            flag = true;
                            countResize = 0;
                        }
                        break;

                    case MouseEvent.MOUSE_RELEASED: 
                        if(isMove)
                        {
                            isMove = false;
                            flag = true;
                            isPaint = true;
                        }
                        break;
                    }
                    if(flag)
                    {
                        Point point1 = getLocation();
                        if(isResize)
                        {
                            Dimension dimension4 = dL;
                            Dimension dimension6 = dS;
                            int i2 = dimension2.width + (l - oldX);
                            int j2 = dimension2.height + (j1 - oldY);
                            setSize(i2 >= dimension6.width ? i2 <= dimension4.width ? i2 : dimension4.width : dimension6.width, j2 >= dimension6.height ? j2 <= dimension4.height ? j2 : dimension4.height : dimension6.height);
                            oldX = l;
                            oldY = j1;
                        } else
                        {
                            Point point2 = getLocation();
                            Dimension dimension7 = getParent().getSize();
                            l = (point2.x + l) - oldX;
                            l = l > 0 ? l + dimension1.width < dimension7.width ? l : dimension7.width - dimension1.width : 0;
                            j1 = (point2.y + j1) - oldY;
                            j1 = j1 > 0 ? j1 + dimension1.height < dimension7.height ? j1 : dimension7.height - dimension1.height : 0;
                            setLocation(l, j1);
                        }
                        if(isPaint)
                            repaint();
                    }
                }
                l = getGapX();
                j1 = getGapY();
                if(!isMove)
                {
                    mouseevent.translatePoint(-l, -j1);
                    pMouse(mouseevent);
                    mouseevent.translatePoint(l, j1);
                }
            }
            super.processEvent(awtevent);
        }
        catch(Throwable _ex) { }
    }

    public void setDimension(Dimension dimension, Dimension dimension1, Dimension dimension2)
    {
        dimension1 = dimension1 != null ? dimension1 : getSize();
        dS = dimension != null ? dimension : dS != null ? dS : new Dimension();
        dL = dimension2 != null ? dimension2 : dL != null ? dL : new Dimension(9999, 9999);
        setSize(dimension1.width, dimension1.height);
    }

    public void setLocation(int i, int j)
    {
        getLocation().setLocation(i, j);
        super.setLocation(i, j);
    }

    public void setSize(int i, int j)
    {
        Dimension dimension = getMaximumSize();
        Dimension dimension1 = getMinimumSize();
        i = i <= dimension.width ? i >= dimension1.width ? i : dimension1.width : dimension.width;
        j = j <= dimension.height ? j >= dimension1.height ? j : dimension1.height : dimension.height;
        Dimension dimension2 = getSize();
        if(dimension2.width == i && dimension2.height == j)
        {
            return;
        } else
        {
            i += getGapW();
            j += getGapH();
            getSizeW().setSize(i, j);
            super.setSize(i, j);
            return;
        }
    }

    public void setSize(Dimension dimension)
    {
        setSize(dimension.width, dimension.height);
    }

    public void setTitle(String s)
    {
        title = s;
    }

    private void setup()
    {
        isWin = Awt.isWin();
        clFrame = Color.black;
        clBar = new Color(0x5555ff);
        clLBar = clBar.brighter();
        clBarT = Color.white;
        Q = Awt.q();
        fontBar = new Font("sansserif", 0, (int)(10F * Q));
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    protected static boolean isWin;
    private String title;
    public boolean isUpDown;
    public boolean isGUI;
    public boolean isHide;
    public boolean isEscape;
    protected boolean isPaint;
    protected boolean isRepaint;
    private Rectangle rEscape;
    private Dimension dSize;
    private Dimension dVisit;
    private Dimension dS;
    private Dimension dL;
    private Point pLocation;
    private boolean isMove;
    private boolean isResize;
    private int oldX;
    private int oldY;
    private int countResize;
    protected static Color clFrame;
    protected static Color clLBar;
    protected static Color clBar;
    protected static Color clBarT;
    protected int iBSize;
    protected int iGap;
    private static Font fontBar = null;
    public static float Q;

}
