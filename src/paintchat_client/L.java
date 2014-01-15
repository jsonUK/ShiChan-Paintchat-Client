// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 5/10/2009 12:42:26 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   L.java

package paintchat_client;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import paintchat.*;
import syi.awt.Awt;
import syi.awt.LComponent;

// Referenced classes of package paintchat_client:
//            Mi, Me

public class L extends LComponent
    implements ActionListener, ItemListener
{

    public L(Mi mi1, ToolBox toolbox, Res res1)
    {
        B = -1;
        mouse = -1;
        isASlide = false;
        popup = null;
        is_pre = true;
        is_DIm = false;
        tool = toolbox;
        bFont = Awt.getDefFont();
        bFont = new Font(bFont.getName(), 0, (int)((float)bFont.getSize() * 0.8F));
        FontMetrics fontmetrics = getFontMetrics(bFont);
        bH = fontmetrics.getHeight() + 6;
        base = bH - 2 - fontmetrics.getMaxDescent();
        int i = (int)(60F * LComponent.Q);
        sL = res1.res("Layer");
        strMenu = res1.res("MenuLayer");
        fontmetrics = getFontMetrics(bFont);
        i = Math.max(fontmetrics.stringWidth(sL + "00") + 4, i);
        i = Math.max(fontmetrics.stringWidth(strMenu) + 4, i);
        bW = i;
        i += bH + 100;
        mi = mi1;
        res = res1;
        setTitle(sL);
        super.isGUI = true;
        m = mi1.info.m;
        Dimension dimension = new Dimension(bW, bH);
        setDimension(new Dimension(dimension), dimension, new Dimension());
        setSize(getMaximumSize());
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        try
        {
            String s = actionevent.getActionCommand();
            int i = popup.getItemCount();
            int j;
            for(j = 0; j < i; j++)
                if(popup.getItem(j).getLabel().equals(s))
                    break;

            paintchat.Mg.Info info = mi.info;
            Mg mg1 = mg();
            setA(mg1);
            int k = mi.user.wait;
            if(popup.getName().charAt(0) == 'm')
            {
                switch(j)
                {
                case 0: // '\0'
                    mi.user.wait = -2;
                    mg1.dRetouch(new int[] {
                        1, info.L + 1
                    }, false);
                    mg1.dEnd();
                    mi.send(mg1);
                    mg1.iLayer = mg1.iLayerSrc + 1;
                    mg1.iLayerSrc = info.L - 1;
                    mg1.dRetouch(new int[] {
                        3
                    }, false);
                    mg1.dEnd();
                    mi.send(mg1);
                    mi.user.wait = k;
                    break;

                case 1: // '\001'
                    if(info.L <= 1 || !confirm(m.iLayer + res.res("DelLayerQ")))
                        return;
                    mg1.dRetouch(new int[] {
                        2
                    }, false);
                    mg1.dEnd();
                    mi.send(mg1);
                    m.iLayer = Math.min(m.iLayer, info.L - 1);
                    break;

                case 2: // '\002'
                    if(!confirm(res.res("CombineVQ")))
                        return;
                    mi.user.wait = -2;
                    mg1.iHint = 3;
                    mg1.iPen = 20;
                    mg1.iLayer = 0;
                    for(j = 1; j < info.L; j++)
                        if(mi.info.visit[j] != 0.0F)
                        {
                            mg1.iLayerSrc = j;
                            setA(mg1);
                            mg1.dRetouch(new int[] {
                                0, info.W << 16 | info.H
                            }, false);
                            mg1.dEnd();
                            mi.send(mg1);
                        }

                    mi.user.wait = k;
                    break;
                }
            } else
            {
                if(j == 0)
                {
                    mg1.dRetouch(new int[] {
                        3
                    }, false);
                } else
                {
                    mg1.iHint = 3;
                    mg1.iPen = 20;
                    mg1.dRetouch(new int[] {
                        0, info.W << 16 | info.H
                    }, false);
                }
                mg1.dEnd();
                mi.send(mg1);
            }
            setSize(getMaximumSize());
            mi.user.wait = k;
            mi.repaint();
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private int b(int i)
    {
        if(i < bH)
            return 0;
        else
            return Math.max(mi.info.L - (i / bH - 1), 1);
    }

    private boolean confirm(String s)
    {
        return Me.confirm(s, true);
    }

    private void dL(Graphics g, int i, int j)
    {
        getSize();
        int k = bW - 1;
        int l = bH - 2;
        Color color = m.iLayer != j ? LComponent.clFrame : Awt.cFSel;
        g.setColor(color);
        g.drawRect(0, i, k, l);
        g.setColor(Awt.cFore);
        g.setFont(bFont);
        g.drawString(sL + j, 2, i + base);
        g.setColor(color);
        g.drawRect(bW, i, 100, l);
        g.setColor(LComponent.clLBar);
        int i1 = (int)(100F * mi.info.visit[j]);
        g.fillRect(bW + 1, i + 1, i1 - 1, l - 1);
        g.setColor(Color.white);
        g.drawString(i1 + "%", bW + 3, i + base);
        int j1 = bW + 100;
        g.setColor(color);
        g.drawRect(j1 + 1, i, l - 2, l);
        g.setColor(Awt.cFore);
        if(mi.info.visit[j] == 0.0F)
        {
            g.drawLine(j1 + 2, i + 1, (j1 + l) - 2, (i + l) - 1);
            g.drawLine(1, i + 1, k - 1, (i + bH) - 3);
        } else
        {
            g.drawOval(j1 + 2, i + 2, l - 3, l - 3);
        }
    }

    public Dimension getMaximumSize()
    {
        Dimension dimension = super.getMaximumSize();
        if(mi != null)
            dimension.setSize(bW + 100 + bH, bH * (mi.info.L + 1));
        return dimension;
    }

    public void itemStateChanged(ItemEvent itemevent)
    {
        is_pre = !is_pre;
    }

    private Mg mg()
    {
        Mg mg1 = new Mg(mi.info, mi.user);
        mg1.iAlpha = 255;
        mg1.iHint = 14;
        mg1.iLayer = m.iLayer;
        mg1.iLayerSrc = m.iLayerSrc;
        return mg1;
    }

    private void p()
    {
        repaint();
        tool.up();
    }

    public void paint2(Graphics g)
    {
        try
        {
            Dimension dimension = getSize();
            int i = mi.info.L;
            int j = i - 1;
            int k = bH;
            g.setFont(bFont);
            g.clearRect(0, 0, dimension.width, dimension.height);
            for(; k < dimension.height; k += bH)
            {
                if(isASlide || j != mouse - 1)
                    dL(g, k, j);
                if(--j < 0)
                    break;
            }

            if(!isASlide && mouse > 0)
                dL(g, Y - YOFF, mouse - 1);
            Awt.drawFrame(g, mouse == 0, 0, 0, bW, bH - 2);
            g.setColor(Awt.cFore);
            g.drawString(strMenu, 3, bH - 6);
        }
        catch(Throwable _ex) { }
    }

    public void pMouse(MouseEvent mouseevent)
    {
        try
        {
            int y = Y = mouseevent.getY();
            int x = mouseevent.getX();
            paintchat.Mg.Info info = mi.info;
            boolean flag = Awt.isR(mouseevent);
            switch(mouseevent.getID())
            {
            default:
                break;

            case 501: 
                if(mouse >= 0)
                    break;
                int k = b(y);
                int l = k - 1;
                if(l >= 0)
                {
                    if(x > bW + 100 + 1)
                    {
                        int i1 = mi.user.wait;
                        mi.user.wait = -2;
                        if(flag)
                        {
                            for(int l1 = 0; l1 < info.L; l1++)
                                setAlpha(l1, l1 != l ? 0 : 255);

                        } else
                        {
                            setAlpha(l, info.visit[l] != 0.0F ? 0 : 255);
                        }
                        mi.user.wait = i1;
                        mi.repaint();
                        p();
                        break;
                    }
                    if(flag)
                        break;
                    isASlide = x >= bW;
                    mouse = k;
                    m.iLayer = m.iLayerSrc = l;
                    YOFF = y % bH;
                    if(isASlide)
                        setAlpha(l, (int)(((float)(x - bW) / 100F) * 255F));
                    else
                        p();
                } else
                {
                    m.iLayerSrc = m.iLayer;
                    popup(new String[] {
                        "AddLayer", "DelLayer", "CombineV"
                    }, x, y, true);
                }
                break;

            case 506: 
                if(mouse <= 0)
                    break;
                if(isASlide)
                {
                    setAlpha(m.iLayer, (int)(((float)(x - bW) / 100F) * 255F));
                } else
                {
                    m.iLayer = b(Y) - 1;
                    repaint();
                }
                break;

            case 503: 
                int j1 = b(y) - 1;
                if(!is_pre || j1 < 0 || x >= bW)
                {
                    if(is_DIm)
                    {
                        is_DIm = false;
                        repaint();
                    }
                    return;
                }
                is_DIm = true;
                Dimension dimension = getSize();
                int j2 = mi.info.W;
                int k2 = mi.info.H;
                Image image = getToolkit().createImage(new MemoryImageSource(j2, k2, new DirectColorModel(24, 0xff0000, 65280, 255), mi.info.getOffset()[j1], 0, j2));
                Graphics g = getGraphics();
                g.translate(getGapX(), getGapY());
                g.setClip(0, 0, dimension.width, dimension.height);
                g.drawImage(image, bW, 0, 100, (int)((100F / (float)j2) * (float)k2), null);
                g.dispose();
                image.flush();
                break;

            case 502: 
                if(flag)
                    break;
                if(isASlide)
                {
                    setAlpha(m.iLayer, (int)(((float)(x - bW) / 100F) * 255F));
                    mouse = -1;
                    isASlide = false;
                    break;
                }
                int k1 = mouse - 1;
                int i2 = b(Y) - 1;
                if(k1 >= 0 && i2 >= 0 && k1 != i2)
                {
                    m.iLayer = i2;
                    m.iLayerSrc = k1;
                    popup(new String[] {
                        res.res("Shift"), res.res("Combine")
                    }, x, y, false);
                }
                mouse = -1;
                repaint();
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private void popup(String as[], int i, int j, boolean flag)
    {
    	if(!ClientPermissions.isLayerEdit) {
  		  return;
  	  }
        if(popup == null)
        {
            popup = new PopupMenu();
            popup.addActionListener(this);
            add(popup);
        } else
        {
            popup.removeAll();
        }
        for(int k = 0; k < as.length; k++)
            popup.add(res.res(as[k]));

        if(flag)
        {
            popup.addSeparator();
            CheckboxMenuItem checkboxmenuitem = new CheckboxMenuItem(res.res("IsPreview"), is_pre);
            checkboxmenuitem.addItemListener(this);
            popup.add(checkboxmenuitem);
            popup.setName("m");
        } else
        {
            popup.setName("l");
        }
        popup.show(this, i, j);
    }

    private void setA(Mg mg1)
    {
        mg1.iAlpha2 = (int)(mi.info.visit[mg1.iLayer] * 255F) << 8 | (int)(mi.info.visit[mg1.iLayerSrc] * 255F);
    }

    public void setAlpha(int i, int j)
        throws Throwable
    {
        j = j > 0 ? j < 255 ? j : 255 : 0;
        mi.info.visit[i] = (float)j / 255F;
        mi.repaint();
        repaint();
    }

    private Mi mi;
    private ToolBox tool;
    private Res res;
    private Mg m;
    private int B;
    private Font bFont;
    private int bH;
    private int bW;
    private int base;
    private int mouse;
    private boolean isASlide;
    private int Y;
    private int YOFF;
    private String sL;
    private PopupMenu popup;
    private String strMenu;
    private boolean is_pre;
    private boolean is_DIm;
}