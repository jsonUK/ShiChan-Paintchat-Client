// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/30/2009 4:47:31 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Tools.java

package paintchat.normal;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ColorModel;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.EventObject;
import java.util.Hashtable;
import paintchat.*;
import paintchat_client.L;
import paintchat_client.Mi;
import syi.awt.Awt;
import syi.awt.LComponent;

// Referenced classes of package paintchat.normal:
//            ToolList

public class Tools extends LComponent
    implements ToolBox, ActionListener
{

    public Tools()
    {
        primary = null;
        list = new ToolList[T_COLORS];
        rPaint = new Rectangle();
        rects = null;
        fit_w = -1;
        fit_h = -1;
        nowButton = -1;
        nowColor = -1;
        isRGB = true;
        fhsb = new float[3];
        imBack = null;
        setTitle("tools");
        super.isHide = false;
        super.iGap = 2;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        Menu menu1 = (Menu)actionevent.getSource();
        int j = 0;
        int k = menu1.getItemCount();
        for(int l = 0; l < k; l++)
        {
            if(!menu1.getItem(l).getLabel().equals(s))
                continue;
            j = l;
            break;
        }

        switch(Integer.parseInt(menu1.getLabel()))
        {
        case 1: // '\001'
            j += 11;
            // fall through

        case 0: // '\0'
            list[4].setTT(j + 1);
            repaint();
            break;

        case 2: // '\002'
            mg.iPenM = j;
            setLineSize(null, mg.iSize);
            repaint();
            break;
        }
    }

    public Graphics getBack()
    {
        if(imBack == null)
            try
            {
                imBack = createImage(60, 60);
                back = imBack.getGraphics();
                back.setFont(fontDef);
            }
            catch(RuntimeException _ex)
            {
                imBack = null;
                back = null;
            }
        return back;
    }

    public String getC()
    {
        try
        {
            int ai[] = COLORS != null ? COLORS : DEFC;
            StringBuffer stringbuffer = new StringBuffer();
            for(int j = 0; j < ai.length; j++)
            {
                if(j != 0)
                    stringbuffer.append('\n');
                stringbuffer.append("#" + Integer.toHexString(0xff000000 | ai[j] & 0xffffff).substring(2).toUpperCase());
            }

            return stringbuffer.toString();
        }
        catch(Throwable _ex)
        {
            return null;
        }
    }

    public LComponent[] getCs()
    {
        if(L != null)
            return (new LComponent[] {
                this, L, tPicPanel
            });
        else
            return (new LComponent[] {
                this, tPicPanel
            });
    }

    private int getRGB()
    {
        if(!isRGB)
            return Color.HSBtoRGB((float)(iColor >>> 16 & 0xff) / 256F, (float)(iColor >>> 8 & 0xff) / 255F, (float)(iColor & 0xff) / 255F) & 0xffffff;
        else
            return iColor & 0xffffff;
    }

    public int getW()
    {
        return getSizeW().width;
    }

    private int i(String s, int j)
    {
        try
        {
            String s1 = applet.getParameter(s);
            if(s1 != null && s1.length() > 0)
                return Res.parseInt(s1);
        }
        catch(Throwable _ex) { }
        return j;
    }

    public boolean i(String s, boolean flag)
    {
        String s1 = applet.getParameter(s);
        if(s1 == null || s1.length() <= 0)
            return flag;
        char c = s1.charAt(0);
        switch(c)
        {
        case 49: // '1'
        case 111: // 'o'
        case 116: // 't'
        case 121: // 'y'
            return true;
        }
        return false;
    }

    public void init(Container container, Applet applet1, Res res1, Res res2, Mi mi1)
    {
        applet = applet1;
        res = res2;
        config = res1;
        info = mi1.info;
        mi = mi1;
        mg = info.m;
        iBuffer = mi.user.getBuffer();
        sCMode(false);
        setBackground(new Color(i("tool_color_back", 0x9999bb)));
        clB = new Color(i("tool_color_button", 0xffe8dfae));
        clB2 = new Color(i("tool_color_button2", 0xf8daaa));
        clB2D = clB2.darker();
        clFrame = new Color(i("tool_color_frame", 0));
        clText = new Color(i("tool_color_text", 0x773333));
        clBar = new Color(i("tool_color_bar", 0xddddff));
        clSel = new Color(i("tool_color_select", 0xee3333));
        container.getSize();
        setDimension(new Dimension(WIDTH, 205), new Dimension(WIDTH, 410), new Dimension(WIDTH, 580));
        list[0].sel();
        container.add(this);
        
        // tpic panel addition
        tPicPanel = new TPicNormal(this);        
        tPicPanel.setLocation(60, 0);        
        tPicPanel.setBackground(applet1.getBackground());
        tPicPanel.setForeground(applet1.getForeground());
        tPicPanel.setVisible(false);
        
        if(res1.getP("tool_layer", true))
        {
            L = new L(mi1, this, res);
            L.setLocation(60, tPicPanel.getSizeW().height);
            L.setVisible(false);                                
        }
        
        // theres some event thrown which screws up how these are laid out if there is not delay
        try {
        	Thread.sleep(100);
        }
        catch(Exception e) {}
        
        // java optimizes and breaks if we put these before we add to container
        if(L != null) {
        	container.add(L);
        }
        container.add(tPicPanel);             
                 
    }

    private int isClick(int j, int k)
    {
        if(rects == null)
            return -1;
        int l = rects.length;
        int i1 = list.length;
        for(int j1 = 0; j1 < i1; j1++)
            if(list[j1].contains(j, k))
                return j1;

        for(int k1 = 0; k1 < l; k1++)
        {
            Rectangle rectangle = rects[k1];
            if(rectangle != null && rectangle.contains(j, k))
                return k1 + i1;
        }

        return -1;
    }

    public void lift()
    {
        int _tmp = nowButton;
        list[0].unSel(null);
        nowButton = -1;
        repaint();
    }
    
    protected Image mkImage(int i, int j)
    {
        if(raster == null)
        {
            raster = new SRaster(ColorModel.getRGBdefault(), iBuffer, i, j);
            image = applet.createImage(raster);
        } else
        {
            raster.newPixels(image, iBuffer, i, j);
        }
        return image;
    }

    private void makeList()
    {
        Image image = null;
        int j = 0;
        int k = 0;
        Color color = clB.brighter();
        Color color1 = clB.darker();
        try
        {
            String s1 = "res/s.gif";
            Image image1 = getToolkit().createImage((byte[])config.getRes(s1));
            Awt.wait(image1);
            image = image1;
            config.remove(s1);
            j = image1.getWidth(null) / 6;
            k = image1.getHeight(null) / T_COLORS;
        }
        catch(RuntimeException _ex) { }
        for(int l = 0; l < list.length; l++)
        {
            ToolList toollist = new ToolList(res, list);
            String s = String.valueOf(l);
            int i1;
            for(i1 = 0; res.containsKey("t0" + s + i1); i1++);
            String as1[] = new String[i1];
            Object obj;
            boolean flag;
            if(l < 4)
            {
                String as[] = new String[i1];
                for(int j1 = 0; j1 < i1; j1++)
                {
                    String s2 = "t0" + s + j1;
                    as[j1] = config.getP(s2, null);
                    as1[j1] = res.res(s2);
                    config.remove(s2);
                    res.remove(s2);
                }

                obj = as;
                flag = false;
            } else
            {
                for(int k1 = 0; k1 < i1; k1++)
                {
                    String s3 = "t0" + s + k1;
                    as1[k1] = res.res(s3);
                    res.remove(s3);
                }

                obj = l != 4 ? ((Object) (l != 5 ? "iMask" : "iHint")) : "iTT";
                flag = true;
            }
            toollist.init(this, mg, null, as1, flag, obj);
            toollist.setImage(image, j, k, l);
            toollist.setColor(clB, clB2, color, color1, clText, clFrame, clSel);
            list[l] = toollist;
        }

    }

    private void menu(int j, int k, int l)
    {
        if(popup == null)
        {
            popup = new PopupMenu(String.valueOf(l));
            popup.addActionListener(this);
        } else
        {
            remove(popup);
            popup.removeAll();
            popup.setLabel(String.valueOf(l));
        }
label0:
        switch(l)
        {
        default:
            break;

        case 0: // '\0'
            for(int i1 = 0; i1 < 11; i1++)
            {
                String s = String.valueOf(i1 != 0 ? i1 * 10 : 5) + '%';
                if(mg.iTT - 1 == i1)
                    popup.add(new CheckboxMenuItem(s, true));
                else
                    popup.add(s);
            }

            break;

        case 1: // '\001'
            int j1 = config.getInt("tt_size");
            for(int k1 = 0; k1 < j1; k1++)
            {
                String s1 = res.res("t042" + k1);
                if(mg.iTT - 12 == k1)
                    popup.add(new CheckboxMenuItem(s1, true));
                else
                    popup.add(s1);
            }

            break;

        case 2: // '\002'
            for(int l1 = 0; l1 < 16; l1++)
            {
            	if(!res.containsKey("penm_" + l1)){
            		break label0;
            	}
            	
                String s2 = (String)res.get("penm_" + l1);
                if(mg.iPenM == l1)
                    popup.add(new CheckboxMenuItem(s2, true));
                else
                    popup.add(s2);
            }

            break;
        }
        add(popup);
        popup.show(this, j, k);
        System.out.println(popup.getItemCount());
    }

    public void mPaint(int j)
    {
        Rectangle rectangle;
        if(j == -1)
        {
            rectangle = rPaint;
            rectangle.setSize(getSize());
            rectangle.setLocation(0, 0);
        } else
        if(j < list.length)
        {
            rectangle = rPaint;
            list[j].getBounds(rectangle);
        } else
        {
            rectangle = rects[j - list.length];
        }
        mPaint(primary(), rectangle);
    }

    public void mPaint(int j, int k, int l, int i1)
    {
        Rectangle rectangle = rPaint;
        rectangle.setBounds(j, k, l, i1);
        mPaint(primary(), rectangle);
    }

    private void mPaint(Graphics g, Rectangle rectangle)
    {
        if(rects == null || g == null)
            return;
        Graphics g1 = getBack();
        if(g1 == null)
            return;
        if(rectangle == null)
        {
            rectangle = g.getClipBounds();
            if(rectangle == null || rectangle.isEmpty())
                rectangle = new Rectangle(getSize());
        }
        if(rectangle.isEmpty())
            return;
        int i1 = list.length;
        Dimension dimension = getSize();
        for(int j = 0; j < i1; j++)
            if(list[j].intersects(rectangle))
                list[j].paint(g, g1);

        int j1 = isRGB ? 1 : 0;
        for(int k = 0; k < rects.length; k++)
        {
            Rectangle rectangle1 = rects[k];
            int l = k + list.length;
            if(rectangle1.intersects(rectangle))
            {
                if(k < 14)
                {
                    Color color = new Color(COLORS[k]);
                    g1.setColor(k != nowColor ? color.brighter() : color.darker());
                    g1.drawRect(1, 1, rectangle1.width - 2, rectangle1.height - 2);
                    g1.setColor(color);
                    g1.fillRect(2, 2, rectangle1.width - 3, rectangle1.height - 3);
                    g1.setColor(nowColor != k ? clFrame : clSel);
                } else
                {
                    switch(l)
                    {
                    default:
                        break;

                    case T_R: // '\025'
                    case T_G: // '\026'
                    case T_B: // '\027'
                    case T_A: // '\030'
                        int k1 = rectangle1.height;
                        int _tmp = l != T_A ? (T_B - l) * 8 : 24;
                        int l1 = l != T_A ? iColor >>> (T_B - l) * 8 & 0xff : mg.iAlpha;
                        int i2 = (int)(((float)(dimension.width - 10 - 2) / 255F) * (float)l1);
                        g1.setColor(clB2);
                        g1.fillRect(0, 0, 5, k1 - 1);
                        g1.fillRect(rectangle1.width - 5, 1, 5, k1 - 1);
                        g1.setColor(clFrame);
                        g1.fillRect(5, 1, 1, k1 - 1);
                        g1.fillRect(rectangle1.width - 5 - 1, 1, 1, k1 - 1);
                        if(i2 > 0)
                        {
                            g1.setColor(clRGB[j1][l - T_R]);
                            g1.fillRect(6, 1, i2, rectangle1.height - 2);
                        }
                        int k2 = rectangle1.width - 10 -   - 2;
                        if(k2 > 0)
                        {
                            g1.setColor(clBar);
                            g1.fillRect(i2 + 5 + 1, 1, k2, rectangle1.height - 2);
                            g1.setColor(clERGB[j1][l - T_R]);
                            g1.fillRect(i2 + 5 + 1, 1, 1, rectangle1.height - 2);
                        }
                        g1.setColor(clText);
                        g1.drawString(String.valueOf(clValue[j1][l - T_R]) + l1, 8, rectangle1.height - 2);
                        break;

                    case T_LAYER: // '\032'
                        g1.setColor(clBar);
                        g1.fillRect(1, 1, rectangle1.width - 1, rectangle1.height - 2);
                        g1.setColor(clText);
                        g1.drawString("layer " + mg.iLayer, 2, rectangle1.height - 2);
                        if(info.visit[mg.iLayer] == 0.0F)
                        {
                            g1.setColor(Color.red);
                            g1.drawLine(1, 1, rectangle1.width - 3, rectangle1.height - 3);
                        }
                        break;
                    case T_COLOR_PICKER:
                    	int deltaWidth = (rectangle1.width -2) / 8;  
                    	for(int i=0; i < 8; i++) {                    		
                    		g1.setColor(Color.getHSBColor(((float)i)/8F, 1, 1));
                    		g1.fillRect(1 + (i * deltaWidth), 1, rectangle1.width -2, rectangle1.height-2);
                    	}
                    	if(tPicPanel.isVisible()) {
                    		g1.setColor(getForeground());
                    		g1.drawRect(0, 0, rectangle1.width, rectangle1.height);
                    	}
                    	break;

                    case T_LINE_SIZE: // '\031'
                        boolean flag = 8 == mg.iHint;
                        Color color1 = new Color(getRGB());
                        int j2 = flag ? 255 : info.getPMMax();
                        int l2 = rectangle1.width - 10;
                        int i3 = rectangle1.height - 2;
                        g1.setColor(clB2);
                        g1.fillRect(1, 1, rectangle1.width - 2, i3);
                        info.getPenMask();
                        if(mg.iSize >= j2)
                            break;
                        g1.setColor(color1);
                        g1.fillRect(1, 1, l2, (int)(((float)(mg.iSize + 1) / (float)j2) * (float)i3));
                        g1.setColor(clText);
                        g1.drawString(flag ? mg.iSize + "pt" : (int)Math.sqrt(info.getPenMask()[mg.iPenM][mg.iSize].length) + "px", 4, i3 - 1);
                        g1.setColor(clFrame);
                        g1.fillRect(l2, 1, 1, i3);
                        g1.fillRect(l2 + 1, i3 / 2, 8, 1);
                        g1.setColor(color1);
                        for(int j3 = 3; j3 >= 1; j3--)
                        {
                            g1.fillRect(rectangle1.width - 5 - j3, j3 + 2, j3 << 1, 1);
                            g1.fillRect(rectangle1.width - 5 - j3, i3 - 2 - j3, j3 << 1, 1);
                        }

                        g1.fillRect(rectangle1.width - 6, 5, 2, 8);
                        g1.fillRect(rectangle1.width - 6, i3 - 11, 2, 8);
                        break;
                    }
                    g1.setColor(nowButton != l ? clFrame : clSel);
                }
                g1.drawRect(0, 0, rectangle1.width - 1, rectangle1.height - 1);
                g.drawImage(imBack, rectangle1.x, rectangle1.y, rectangle1.x + rectangle1.width, rectangle1.y + rectangle1.height, 0, 0, rectangle1.width, rectangle1.height, Color.white, null);
            }
        }

    }

    private void mPress(MouseEvent mouseevent)
    {
        int x = mouseevent.getX();
        int y = mouseevent.getY();
        int clicked = isClick(x, y);
        if(clicked < 0 || clicked >= MAX_TOOL)
            return;
        boolean altOrCtrl = (mouseevent.getModifiers() & 4) != 0 || mouseevent.isControlDown() || mouseevent.isAltDown();
        nowButton = clicked;
        if(clicked < list.length)
        {
            if(altOrCtrl)
            {
                nowButton = -1;
                if(clicked == 4)
                {
                    int i1 = list[clicked].iNow;
                    if(i1 > 0)
                        menu(x, y, i1 - 1);
                }
                if(clicked == 6)
                {
                    mg.iColorMask = mg.iColor;
                    mPaint(6);
                }
            } else
            if(clicked == 5 && (mg.iHint == 8 || mg.iHint == 9 || mg.iHint == 10))
                nowButton = -1;
            else
                list[clicked].press();
        } else
        {
            switch(clicked)
            {
            case T_R: // '\025'
            case T_G: // '\026'
            case T_B: // '\027'
            case T_A: // '\030'
                int j1 = x > 5 ? ((int) (x < rects[T_R - list.length].width - 5 ? 0 : 1)) : -1;
                if(j1 != 0)
                {
                    nowButton = -1;
                    if(altOrCtrl)
                        j1 *= 5;
                } else
                if(altOrCtrl)
                {
                    sCMode(isRGB);
                    nowButton = -1;
                    break;
                }
                setRGB(clicked, j1, x, y);
                break;

            case T_LAYER: // '\032'
                int k1 = mg.iLayer;
                if(altOrCtrl)
                {
                    info.visit[k1] = info.visit[k1] != 0.0F ? 0 : 1;
                    mi.repaint();
                } else
                if(L != null && !L.isVisible())
                    L.setVisible(true);
                else
                if(++mg.iLayer >= info.L)
                    mg.iLayer = 0;
                L.repaint();
                break;

            case T_LINE_SIZE: // '\031'
                if(altOrCtrl)
                {
                    nowButton = -1;
                    menu(x, y, 2);
                    break;
                }
                Rectangle rectangle = rects[T_LINE_SIZE - list.length];
                if(x >= (rectangle.x + rectangle.width) - 10)
                {
                    setLineSize(null, mg.iSize + (((rectangle.y + rectangle.height) - y) / 2 < 10 ? 1 : -1));
                    nowButton = -1;
                } else
                {
                    setLineSize(new Point(x, y), 0);
                }
                break;
                
            case T_COLOR_PICKER:
            	tPicPanel.setVisible(!tPicPanel.isVisible());
            	break;

            default:
                nowColor = clicked - list.length;
                if(altOrCtrl)
                {
                	// pop up color picker
                    COLORS[nowColor] = mg.iColor;
                	//tPicPanel.setVisible(true);                	
                    break;
                }
                if(mouseevent.isShiftDown())
                    COLORS[nowColor] = DEFC[nowColor];
                mg.iColor = COLORS[nowColor];
                toColor(mg.iColor);
                tPicPanel.setColor(mg.iColor);
                selPix(false);
                break;
            }
            mPaint(-1);
        }
    }

    public void setMask(Component component, int i, int j, int k, boolean flag)
    {
        if(flag)
        {
        	// TODO do masking popup etc
            //setField(component, "iMask", "mask_", j, k);
        } else
        {
            mg.iColorMask = i & 0xffffff;
            tPicPanel.repaint();
        }
    }
    
    private void mRelease(int j, int k)
    {
        if(nowButton == -1)
            return;
        if(nowButton < T_COLORS)
        {
            list[nowButton].release(k);
            mPaint(-1);
        }
    }

    public void pack()
    {
        try
        {
            Container container = getParent();
            if(container == null)
                return;
            Dimension dimension = container.getSize();
            Dimension dimension1 = getMaximumSize();
            setSize(Math.min(dimension1.width, dimension.width), Math.min(dimension1.height, dimension.height));
            if(L != null) {
            	L.inParent();
            }
            tPicPanel.inParent();
        }
        catch(Throwable _ex) { }
    }

    public void paint2(Graphics g)
    {
        try
        {
            g.setFont(fontDef);
            mPaint(g, g.getClipBounds());
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    
    public void repaint() {
    	super.repaint();
    	tPicPanel.repaint();
    }

    public void pMouse(MouseEvent mouseevent)
    {
        try
        {
            if(rects == null)
                return;
            int j = mouseevent.getX();
            int k = mouseevent.getY();
            Dimension dimension = getSize();
            switch(mouseevent.getID())
            {
            case 501: 
                if(k >= 0 && k < dimension.height)
                    mPress(mouseevent);
                return;

            case 502: 
                mRelease(j, k);
                return;
            }
            if(mouseevent.getID() != 506)
                return;
            switch(nowButton)
            {
            default:
                if(nowButton < T_COLORS)
                    list[nowButton].drag(k);
                break;

            case -1: 
                break;

            case T_R: // '\025'
            case T_G: // '\026'
            case T_B: // '\027'
            case T_A: // '\030'
                setRGB(nowButton, 0, j, k);
                break;

            case T_LINE_SIZE: // '\031'
                setLineSize(mouseevent.getPoint(), 0);
                mPaint(T_LINE_SIZE);
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public Graphics primary()
    {
        if(primary == null)
            try
            {
                primary = getGraphics();
                if(primary != null)
                {
                    primary.setFont(fontDef);
                    primary.translate(getGapX(), getGapY());
                }
            }
            catch(RuntimeException _ex)
            {
                primary = null;
            }
        return primary;
    }

    protected void processEvent(AWTEvent awtevent)
    {
        int j = awtevent.getID();
        if((j == 102 || j == 101 || j == 100) && primary != null)
        {
            primary.dispose();
            primary = null;
        }
        super.processEvent(awtevent);
    }

    private void sCMode(boolean flag)
    {
        isRGB = !flag;
        toColor(mg.iColor);
    }

    public void selPix(boolean flag)
    {
        int j = 0;
        byte byte0 = 0;
        for(; j < list.length; j++)
            if(list[j].isSel)
                break;

        boolean flag1 = false;
        if(flag)
        {
            if(j != 3)
            {
                flag1 = true;
                byte0 = 3;
            }
        } else
        if(j == 3)
        {
            flag1 = true;
            byte0 = 0;
        }
        if(flag1)
        {
            list[byte0].sel();
            mPaint(-1);
        }
    }

    public void setARGB(int newColor)
    {    	
    	int old = mg.iAlpha << 24 | mg.iColor;
        mg.iAlpha = newColor >>> 24;        
        mg.iColor = newColor & 0xffffff;
        if(old != newColor) {
	        toColor(newColor);
	        mPaint(T_R);
	        mPaint(T_G);
	        mPaint(T_B);
	        
	        mPaint(T_LINE_SIZE);
	        mPaint(-1);
	        tPicPanel.setColor(newColor);
        }
    }

    public void setC(String s)
    {
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new StringReader(s));
            int j = 0;
            while((s = bufferedreader.readLine()) != null && s.length() > 0) 
                DEFC[j++] = Integer.decode(s).intValue();
            System.arraycopy(DEFC, 0, COLORS, 0, COLORS.length);
            repaint();
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void setLineSize(int j)
    {
        setLineSize(null, j);
        mPaint(T_LINE_SIZE);
    }

    public void setLineSize(Point point, int j)
    {
        int _tmp = mg.iSize;
        int k = info.getPMMax();
        Rectangle rectangle = rects[T_LINE_SIZE - list.length];
        int _tmp1 = rectangle.height;
        if(point != null)
            j = (int)(((float)(point.y - rectangle.y) / (float)rectangle.height) * (float)k);
        j = j > 0 ? j < k ? j : k - 1 : 0;
        mg.iSize = j;
    }

    private void setRGB(int rgbSliderIndex, int k, int x, int y)
    {
        int sliderByte = rgbSliderIndex != T_A ? ((2 - rgbSliderIndex) + T_R) * 8 : 24;
        int newColor = mg.iAlpha << 24 | iColor;        
        int sliderVal;
        
        if(k != 0)
        {
            sliderVal = newColor >>> sliderByte & 0xff;
            sliderVal += k;
        } else
        {
            Rectangle rectangle = rects[T_R - list.length];
            sliderVal = (int)((float)(x - rectangle.x - 4) * 5.3F);
        }
        sliderVal = sliderVal > 0 ? sliderVal < 255 ? sliderVal : 255 : 0;
        int i2 = 255 << sliderByte;
        i2 = ~i2;

        newColor = newColor & i2 | sliderVal << sliderByte;
        iColor = newColor & 0xFFFFFF;
        this.mg.iColor = getRGB();
        this.mg.iAlpha = Math.max(newColor >>> 24, 1);
        this.iColor = newColor & 0xFFFFFF;
        mPaint(rgbSliderIndex);
        if(nowColor >= 0)
        {
            COLORS[nowColor] = mg.iColor;
            mPaint(list.length + nowColor);
        }
        mPaint(T_LINE_SIZE);
        tPicPanel.setColor(newColor);
    }
    
    public void setRGB(int i)
    {
        setARGB(mg.iAlpha << 24 | i & 0xffffff);
    }

    public void setSize(int j, int k)
    {
        if(applet == null)
        {
            super.setSize(j, k);
            return;
        }
        if(j == fit_w && k == fit_h)
            return;
        synchronized(this)
        {
            fit_w = j;
            fit_h = k;
            if(list[0] == null)
                makeList();
            if(rects == null)
            {
                rects = new Rectangle[MAX_TOOL - list.length];
                for(int l = 0; l < rects.length; l++)
                    rects[l] = new Rectangle();

            }
            Rectangle arectangle[] = rects;
            float f = (float)j / 55F;
            float f1 = (float)k / 410F;
            int k1 = (int)(55F * f);
            int l1 = (int)(20F * f1);
            fontDef = new Font("sansserif", 0, (int)Math.min(Math.max((float)(l1 - 4) / 1.25F, 4F), k1 / 5));
            FontMetrics fontmetrics = getFontMetrics(fontDef);
            int i2 = l1 - fontmetrics.getMaxDescent();
            if(back != null)
                back.setFont(fontDef);
            int j2 = 0;
            int k2 = 0;
            for(int i1 = 0; i1 < list.length; i1++)
            {
                list[i1].setLocation(0, k2);
                list[i1].setSize(k1, l1, i2);
                k2 += l1;
            }

            j2 = (k1 - 1) / 2;
            int j1;
            for(j1 = 0; j1 < 14; j1++)
            {
                Rectangle rectangle = arectangle[j1];
                rectangle.setBounds(j1 % 2 != 1 ? 0 : j2 + 1, k2, j1 % 2 != 1 ? j2 : k1 - j2 - 1, l1);
                if(j1 % 2 == 1)
                    k2 += l1 + 1;
            }

            j2 = (int)(15F * f1);
            for(; j1 < 18; j1++)
            {
                Rectangle rectangle1 = arectangle[j1];
                rectangle1.setBounds(0, k2, k1, j2);
                k2 += j2 + 1;
            }

            j2 = (int)(32F * f1);
            Rectangle rectangle2 = arectangle[j1++];
            rectangle2.setBounds(0, k2, k1, j2);
            k2 += j2 + 1;
            j2 = (int)(20F * f1);
            rectangle2 = arectangle[j1++];
            rectangle2.setBounds(0, k2, k1, j2);
            k2 += j2 + 1;
            // reuse layer tip
            arectangle[j1++].setBounds(0,k2,k1,j2);
            k2+= j2+1;
            super.setSize(j, k);
        }
    }

    private void toColor(int j)
    {
        if(!isRGB)
        {
            Color.RGBtoHSB(j >>> 16 & 0xff, j >>> 8 & 0xff, j & 0xff, fhsb);
            iColor = mg.iAlpha << 24 | (int)(fhsb[0] * 255F) << 16 | (int)(fhsb[1] * 255F) << 8 | (int)(fhsb[2] * 255F);
        } else
        {
            iColor = mg.iAlpha << 24 | j;
        }
    }

    public void up()
    {
        repaint();
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    private Applet applet;
    private Mi mi;
    private L L;
    protected paintchat.Mg.Info info;
    private Mg mg;
    private Res res;
    private Res config;
    private Graphics primary;
    private Font fontDef;
    private ToolList list[];
    private Rectangle rPaint;
    private Rectangle rects[];
    private int fit_w;
    private int fit_h;
    private int nowButton;
    private int nowColor;
    private Color clFrame;
    private Color clB;
    private Color clText;
    private Color clBar;
    private Color clB2;
    private Color clB2D;
    private Color clSel;
    private PopupMenu popup;
    private static int DEFC[] = {
        0, 0xffffff, 0xb47575, 0x888888, 0xfa9696, 0xc096c0, 0xffb6ff, 0x8080ff, 0x25c7c9, 0xe7e58d, 
        0xe7962d, 0x99cb7b, 0xfcece2, 0xf9ddcf
    };
    private static int COLORS[];
    private static Color clRGB[][];
    private static Color clERGB[][];
    private char clValue[][] = {
        {
            'H', 'S', 'B', 'A'
        }, {
            'R', 'G', 'B', 'A'
        }
    };
    private boolean isRGB;
    private float fhsb[];
    private int iColor;
    
    protected int iBuffer[];
    private Image image;
    private SRaster raster;
    private TPicNormal tPicPanel;
    protected Image imBack;
    private Graphics back;
    private static final byte MAX_TOOL = 28;
    private static final byte T_COLORS = 7;
    private static final int T_R = 21;
    private static final int T_G = 22;
    private static final int T_B = 23;
    private static final int T_A = 24;
    private static final int T_LINE_SIZE = 25;
    private static final int T_LAYER = 26;
    private static final int T_COLOR_PICKER = 27;
    public static final int WIDTH = 55;

    static 
    {
        COLORS = new int[14];
        System.arraycopy(DEFC, 0, COLORS, 0, 14);
        clRGB = (new Color[][] {
            new Color[] {
                Color.magenta, Color.cyan, Color.white, Color.lightGray
            }, new Color[] {
                new Color(0xfa9696), new Color(0x82f238), new Color(0x8080ff), Color.lightGray
            }
        });
        clERGB = new Color[2][4];
        for(int j = 0; j < 2; j++)
        {
            for(int k = 0; k < 4; k++)
                clERGB[j][k] = clRGB[j][k].darker();

        }

    }

	public void mgChange() {
		mi.mgChange();		
	}
}
