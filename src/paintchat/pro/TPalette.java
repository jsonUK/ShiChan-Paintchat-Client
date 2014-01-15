// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/30/2009 11:35:15 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TPalette.java

package paintchat.pro;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.StringReader;
import paintchat.Mg;
import paintchat.Res;
import syi.awt.Awt;
import syi.awt.LComponent;

// Referenced classes of package paintchat.pro:
//            Tools

public class TPalette extends LComponent
{

    public TPalette()
    {
        lenColor = 255;
        iDrag = -1;
        sizePalette = 20;
        selPalette = 0;
        oldColor = 0;
        isRGB = 1;
        fhsb = new float[3];
        if(clRGB == null)
            clRGB = (new Color[][] {
                new Color[] {
                    Color.magenta, Color.cyan, Color.white, Color.lightGray
                }, new Color[] {
                    new Color(0xfa9696), new Color(0x82f238), new Color(0x8080ff), Color.lightGray
                }
            });
    }

    public void changeRH()
    {
        int i = getRGB();
        isRGB = isRGB != 0 ? 0 : 1;
        setColor(i);
    }

    public String getC()
    {
        try
        {
            StringBuffer stringbuffer = new StringBuffer();
            for(int j = 0; j < cls.length; j++)
            {
                if(j != 0)
                    stringbuffer.append('\n');
                int i = cls[j].getRGB();
                stringbuffer.append("#" + Integer.toHexString(0xff000000 | i & 0xffffff).substring(2).toUpperCase());
            }

            return stringbuffer.toString();
        }
        catch(Throwable _ex)
        {
            return null;
        }
    }

    private int getRGB()
    {
        return isRGB != 1 ? Color.HSBtoRGB((float)(iColor >>> 16 & 0xff) / 255F, (float)(iColor >>> 8 & 0xff) / 255F, (float)(iColor & 0xff) / 255F) & 0xffffff : iColor;
    }

    public void init(Tools tools1, paintchat.Mg.Info info1, Res res1)
    {
        info = info1;
        mg = info1.m;
        res = res1;
        tools = tools1;
        if(cls == null)
        {
            cls = new Color[sizePalette];
            for(int i = 0; i < sizePalette; i++)
                cls[i] = new Color(i < clsDef.length ? clsDef[i] : 0xffffff);

        }
        setDimension(new Dimension((int)(42F * LComponent.Q), (int)(42F * LComponent.Q)), new Dimension((int)(112F * LComponent.Q), (int)(202F * LComponent.Q)), new Dimension((int)(300F * LComponent.Q), (int)(300F * LComponent.Q)));
    }

    public void paint2(Graphics g)
    {
        try
        {            
            Dimension dimension = getSize();
            int j = Math.min((dimension.height - 1) / 10, 64);
            int k = (int)((float)j * 1.5F);
            byte byte0 = ((byte)(j > 12 ? 2 : 0));
            int l = 0;
            int _tmp = cls.length;
            int i1 = 0;
            int j1 = 0;
            for(int k1 = 0; k1 < cls.length; k1++)
            {
                g.setColor(cls[l++]);
                g.fillRect(i1 + 1, j1 + 1, k - 1 - byte0, j - 1 - byte0);
                g.setColor(Awt.cF);
                g.drawRect(i1, j1, k - byte0, j - byte0);
                if(selPalette == k1)
                {
                    g.setColor(Awt.cFSel);
                    g.drawRect(i1 + 1, j1 + 1, k - byte0 - 2, j - byte0 - 2);
                }
                if(i1 == 0)
                {
                    i1 += k;
                } else
                {
                    i1 = 0;
                    j1 += j;
                }
            }

            int l1 = k * 2;
            j1 = pBar(g, l1, 0, j);
            g.setColor(getBackground());
            g.fillRect(i1 + l1, j1, dimension.width - l1, dimension.height - j1);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private int pBar(Graphics g, int i, int j, int k)
    {
        Dimension dimension = getSize();
        int l = dimension.width - i - 1;
        int _tmp = dimension.height - j;
        Color color = getBackground();
        Color color1 = Awt.cFore;
        boolean flag = mg.iHint == 8;
        int i1 = flag ? 255 : info.getPenMask()[mg.iPenM].length;
        int j1 = Math.min(k * 6, i1 * 8 + 1);
        int k1 = mg.iSize;
        k1 = k1 > 0 ? k1 < i1 ? k1 : i1 - 1 : 0;
        mg.iSize = k1;
        String s = flag ? mg.iSize + "pt" : (int)Math.sqrt(info.getPenMask()[mg.iPenM][mg.iSize].length) + "px";
        g.setColor(Awt.cF);
        g.drawRect(i, j, l, j1);
        int l1 = (int)((float)j1 * ((float)(k1 + 1) / (float)i1));
        g.setColor(cls[selPalette]);
        g.fillRect(i + 1, j + 1, l - 1, l1 - 1);
        g.setColor(color);
        g.fillRect(i + 1, j + 1 + l1, l - 1, j1 - l1 - 1);
        g.setColor(color1);
        g.setFont(Awt.getDefFont());
        g.setXORMode(color);
        g.drawString(s, i + 2, (j + j1) - 2);
        g.setPaintMode();
        if(clFont == null || clFont.getSize() != Math.max(k - 2, 1))
            clFont = new Font("sansserif", 0, Math.max(k - 2, 1));
        g.setFont(clFont);
        int j2 = iColor << 8 | mg.iAlpha;
        int k2 = 24;
        j += j1;
        for(int l2 = 0; l2 < 4; l2++)
        {
            g.setColor(Awt.cF);
            g.drawRect(i, j + 1, l, k - 2);
            g.setColor(Color.white);
            g.fillRect(i + 1, j + 2, l - 1, 1);
            g.fillRect(i + 1, j + 3, 1, k - 4);
            int i2 = (int)((float)(l - 2) * ((float)(j2 >>> k2 & 0xff) / 255F));
            g.setColor(clRGB[isRGB][l2]);
            g.fillRect(i + 2, j + 3, i2, k - 4);
            g.setColor(Color.gray);
            g.fillRect(i + 1 + i2, j + 3, 1, k - 4);
            g.setColor(color);
            g.fillRect(i + 2 + i2, j + 3, l - i2 - 2, k - 4);
            g.setColor(color1);
            g.drawString(String.valueOf(clValue[isRGB][l2]) + (j2 >>> k2 & 0xff), i + 2, (j + k) - 2);
            j += k;
            k2 -= 8;
        }

        return j;
    }

    public void pMouse(MouseEvent mouseevent)
    {
        int i = mouseevent.getID();
        int j = mouseevent.getX();
        int k = mouseevent.getY();
        Dimension dimension = getSize();
        int l = (dimension.height - 1) / 10;
        int i1 = (int)((float)l * 1.5F);
        int j1 = i1 * 2;
        boolean flag = Awt.isR(mouseevent);
        boolean flag1 = mg.iHint == 8;
        int k1 = flag1 ? 255 : info.getPenMask()[mg.iPenM].length;
        int l1 = Math.min(l * 6, k1 * 8 + 1);
        if(j <= j1 && i == 501)
        {
            iDrag = -1;
            int i2 = Math.min((k / l) * 2 + j / i1, 19);
            selPalette = i2;
            int j2 = flag ? getRGB() : cls[i2].getRGB();
            if(mouseevent.isShiftDown() && i2 < clsDef.length)
                j2 = clsDef[i2];
            tools.setRGB(j2);
            return;
        }
        boolean flag2 = false;
        switch(i)
        {
        case 501: 
            if(k < l1)
            {
                if(flag)
                {
                    tools.setField(this, "iPenM", "penm_", j, k);
                    return;
                }
                iDrag = 0;
            } else
            {
                if(flag)
                {
                    changeRH();
                    return;
                }
                iDrag = (k - l1) / l;
                iDrag = (iDrag > 0 ? iDrag < 3 ? iDrag : 3 : 0) + 1;
            }
            flag2 = true;
            break;

        case 502: 
            iDrag = -1;
            break;

        case 506: 
            if(iDrag >= 0)
                flag2 = true;
            break;
        }
        if(flag2)
            if(iDrag == 0)
            {
                setLineSize((int)(((float)k / (float)l1) * (float)k1));
            } else
            {
                int k2 = (int)(((float)(j - j1) / (float)(dimension.width - j1)) * 255F);
                int l2 = 24 - 8 * (iDrag - 1);
                int i3 = iColor << 8 | mg.iAlpha;
                i3 = i3 & (-1 ^ 255 << l2) | Math.max(Math.min(k2, 255), 0) << l2;
                iColor = i3 >>> 8;
                mg.iAlpha = Math.max(i3 & 0xff, 1);
                i3 = iColor;
                tools.setRGB(getRGB());
                boolean flag3 = iColor == i3;
                iColor = i3;
                if(flag3)
                    repaint();
            }
    }

    public void setC(String s)
    {
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new StringReader(s));
            int i = 0;
            while((s = bufferedreader.readLine()) != null) 
            {
                if(i < cls.length)
                    cls[i] = Color.decode(s);
                if(i < clsDef.length)
                    clsDef[i++] = cls[i].getRGB() & 0xffffff;
            }
            repaint();
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void setColor(int i)
    {
        i &= 0xffffff;
        boolean flag = getRGB() != i;
        if(isRGB == 1)
        {
            iColor = i;
        } else
        {
            Color.RGBtoHSB(i >>> 16, i >>> 8 & 0xff, i & 0xff, fhsb);
            iColor = (int)(fhsb[0] * 255F) << 16 | (int)(fhsb[1] * 255F) << 8 | (int)(fhsb[2] * 255F);
        }
        if((cls[selPalette].getRGB() & 0xffffff) != i)
        {
            cls[selPalette] = new Color(mg.iColor);
            flag = true;
        }
        if(flag)
            repaint();
    }

    public void setLineSize(int i)
    {
        int j = mg.iHint != 8 ? info.getPenMask()[mg.iPenM].length : 255;
        int k = mg.iSize;
        mg.iSize = Math.min(Math.max(0, i), j);
        if(k != mg.iSize)
            repaint();
    }

    private int lenColor;
    private int iDrag;
    private paintchat.Mg.Info info;
    private Mg mg;
    private Tools tools;
    private Res res;
    private int sizePalette;
    private int selPalette;
    private int oldColor;
    private Color cls[];
    private int isRGB;
    private float fhsb[];
    private int iColor;
    private static Font clFont = null;
    private static final char clValue[][] = {
        {
            'H', 'S', 'B', 'A'
        }, {
            'R', 'G', 'B', 'A'
        }
    };
    private static Color clRGB[][] = null;
    private static int clsDef[] = {
        0, 0xffffff, 0xb47575, 0x888888, 0xfa9696, 0xc096c0, 0xffb6ff, 0x8080ff, 0x25c7c9, 0xe7e58d, 
        0xe7962d, 0x99cb7b, 0xfcece2, 0xf9ddcf
    };

}
