// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/30/2009 5:19:06 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ToolList.java

package paintchat.normal;

import java.awt.*;
import java.lang.reflect.Field;
import paintchat.Mg;
import paintchat.Res;

// Referenced classes of package paintchat.normal:
//            Tools

public class ToolList
{

    public ToolList(Res res1, ToolList atoollist[])
    {
        isSel = false;
        isSelOld = false;
        quality = 1;
        isDrag = false;
        isPaint = false;
        isTT = false;
        nowSelect = -1;
        iNow = 0;
        info = null;
        mgs = null;
        base = 0;
        res = res1;
        lists = atoollist;
    }

    public boolean contains(int i, int j)
    {
        return i >= X && j >= Y && i < X + width && j < Y + height;
    }

    private void dImage(Graphics g, Color color, int i, int j)
    {
        g.setColor(color);
        g.fillRect(2, i + 2, width - 4, height - 3);
        if(imIndex == 6)
        {
            g.setColor(new Color(info.iColorMask));
            g.fillRect(width - imW - 3, i + 3, imW, (height - 4) / 2);
            return;
        }
        if(image == null || j >= 7)
        {
            return;
        } else
        {
            int k = imIndex * imW;
            int l = j * imH;
            byte byte0 = 2;
            int i1 = i + 2;
            g.drawImage(image, byte0, i1, (byte0 + width) - 4, (i1 + height) - 3, k, l, k + imW, l + imH, color, null);
            return;
        }
    }

    public void drag(int i)
    {
        int j = Y;
        int k = (i - j) / height;
        int l = nowSelect;
        if(k == l)
            return;
        nowSelect = k;
        Graphics g = parent.primary();
        if(!isPaint)
        {
            isPaint = true;
            repaint();
        }
        int i1 = strings.length;
        if(l > 0 && l <= i1)
        {
            g.setColor(clFrame);
            g.drawRect(X, j + height * l, width - 1, height);
        }
        if(k > 0 && k <= i1)
        {
            g.setColor(clSel);
            g.drawRect(X, j + height * k, width - 1, height);
        }
    }

    public void getBounds(Rectangle rectangle)
    {
        rectangle.setBounds(X, Y, width, height);
    }

    public void init(Tools tools, Mg mg, ToolList toollist, String as[], boolean flag, Object obj)
    {
        info = mg;
        isField = flag;
        strings = as;
        parent = tools;
        listUndo = toollist;
        try
        {
            if(flag)
            {
                field = mg.getClass().getField((String)obj);
                length = as.length;
            } else
            {
                String as1[] = (String[])obj;
                mgs = new Mg[as1.length];
                for(int i = 0; i < as1.length; i++)
                {
                    Mg mg1 = new Mg();
                    mg1.set(as1[i]);
                    mgs[i] = mg1;
                }

                length = mgs.length;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public boolean intersects(Rectangle rectangle)
    {
        return rectangle.x + rectangle.width > X && rectangle.y + rectangle.height > Y && rectangle.x < X + width && rectangle.y < Y + height;
    }

    public void paint(Graphics g, Graphics g1)
    {
        try
        {
            if(g == null || g1 == null)
                return;
            if(isDrag)
            {
                int k = Y + height;
                int l = length;
                for(int i1 = 0; i1 < l; i1++)
                {
                    dImage(g, clB2, k, i1);
                    g.setColor(clText);
                    g.drawString(strings[i1], X + 3, k + base);
                    k += height;
                }

                k = Y + height;
                g.setColor(clFrame);
                for(int j1 = 0; j1 < l; j1++)
                {
                    g.drawRect(0, k, width - 1, height);
                    k += height;
                }

                k = Y + height;
                g.setColor(clB2);
                for(int k1 = 0; k1 < l; k1++)
                {
                    g.drawRect(1, k + 1, width - 3, height - 2);
                    k += height;
                }

            }
            int i = isField ? field.getInt(info) : iNow;
            if(isTT && i > 0)
                if(i < 12)
                    i = 1;
                else
                    i = 2;
            dImage(g1, clB, 0, i);
            g1.setColor(clFrame);
            g1.drawRect(0, 0, width - 1, height - 1);
            if(isSel)
            {
                g1.setColor(clSel);
                g1.drawRect(1, 1, width - 3, height - 2);
            } else
            {
                g1.setColor(clBL);
                g1.fillRect(1, 1, width - 3, 1);
                g1.fillRect(1, 2, 1, height - 2);
                g1.setColor(clBD);
                g1.fillRect(width - 2, 1, 1, height - 1);
                g1.fillRect(2, height - 1, width - 4, 1);
            }
            if(i >= 0 && i < strings.length)
            {
                String s = strings[i];
                if(isTT)
                {
                    int j = info.iTT;
                    if(j > 0)
                        s = s + (j != 1 ? j >= 12 ? res.res("t042" + (j - 12)) : (j - 1) * 10 + "%" : "5%");
                }
                g1.setColor(clText);
                g1.drawString(s, 3, base);
            }
            g.drawImage(parent.imBack, X, Y, X + width, Y + height, 0, 0, width, height, clB, null);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void press()
    {
        isDrag = true;
        isPaint = false;
        nowSelect = 0;
        isSelOld = isSel;
        if(!isField)
            sel();
    }

    public void release(int i)
    {
        isDrag = false;
        int j = Y;
        int k = (i - j) / height - 1;
        selItem(k, isSelOld && !isPaint || isField && !isPaint);
        if(isPaint)
            if(mgs == null)
            {
                int l = length * height + height;
                parent.primary().clearRect(X, j, width + 1, l + 1);
                parent.mPaint(X, j, width + 1, l + 1);
            } else
            {
                parent.mPaint(-1);
            }
        isPaint = false;
    }

    public void repaint()
    {
        paint(parent.primary(), parent.getBack());
    }

    public void sel()
    {
        int i = iNow;
        try
        {
            if(isField)
            {
                if(isTT)
                    i = iTT[i];
                field.setInt(info, i);
            } else
            if(!isSel)
            {
                unSel(this);
                upInfo();
                isSel = true;
            } else
            {
                upMg();
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void selItem(int i, boolean flag)
    {
        int j = length;
        if(flag)
            i = iNow + 1 < j ? iNow + 1 : 0;
        if(i < 0 || i >= length)
        {
            return;
        } else
        {
            iNow = i;
            upInfo();
            parent.mPaint(X, Y, width, height);
            this.parent.mgChange();
            return;
        }
    }

    public void setColor(Color color, Color color1, Color color2, Color color3, Color color4, Color color5, Color color6)
    {
        clB = color;
        clB2 = color1;
        clBL = color2;
        clBD = color3;
        clText = color4;
        clSel = color6;
        clFrame = color5;
    }

    public void setImage(Image image1, int i, int j, int k)
    {
        image = image1;
        imW = i;
        imH = j;
        imIndex = k;
        if(k == 4)
        {
            isTT = true;
            iTT = (new int[] {
                0, 1, 12
            });
        }
    }

    public void setLocation(int i, int j)
    {
        X = i;
        Y = j;
    }

    public void setSize(int i, int j, int k)
    {
        width = i;
        height = j;
        base = k;
    }

    public void setTT(int i)
    {
        if(i != 0)
            iTT[i > 12 ? 2 : 1] = i;
        info.iTT = i;
    }

    public void unSel(ToolList toollist)
    {
        int i = lists.length;
        for(int j = 0; j < i; j++)
        {
            ToolList toollist1 = lists[j];
            if(toollist1 != toollist && toollist1.isSel)
            {
                toollist1.upMg();
                toollist1.isSel = false;
            }
        }

    }

    private void upInfo()
    {
        if(isField)
        {
            sel();
            return;
        } else
        {
            int i = info.iColor;
            int j = info.iMask;
            int k = info.iColorMask;
            int l = info.iLayer;
            int i1 = info.iLayerSrc;
            int j1 = info.iTT;
            info.set(mgs[iNow]);
            info.iColor = i;
            info.iMask = j;
            info.iColorMask = k;
            info.iLayer = l;
            info.iLayerSrc = i1;
            info.iTT = j1;
            return;
        }
    }

    protected void upMg()
    {
        if(isField)
        {
            return;
        } else
        {
            mgs[iNow].set(info);
            return;
        }
    }

    private Res res;
    boolean isSel;
    private boolean isSelOld;
    private int quality;
    private boolean isDrag;
    private boolean isPaint;
    private boolean isField;
    private boolean isTT;
    private int iTT[];
    private int nowSelect;
    public int iNow;
    private Mg info;
    private Tools parent;
    private ToolList listUndo;
    private Mg mgs[];
    private String strings[];
    private int length;
    private Field field;
    private ToolList lists[];
    private Font font;
    private Color clText;
    private Color clB;
    private Color clB2;
    private Color clBL;
    private Color clBD;
    private Color clFrame;
    private Color clSel;
    private int base;
    private Image image;
    private int imW;
    private int imH;
    private int imIndex;
    private int X;
    private int Y;
    private int width;
    private int height;
}
