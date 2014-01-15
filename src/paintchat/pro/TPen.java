// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/29/2009 9:19:08 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TPen.java

package paintchat.pro;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.util.Hashtable;
import paintchat.Mg;
import paintchat.Res;
import syi.awt.Awt;
import syi.awt.LComponent;

// Referenced classes of package paintchat.pro:
//            Tools

public class TPen extends LComponent
    implements Runnable
{

    public TPen(Tools tools1, paintchat.Mg.Info info1, Res res, TPen tpen, LComponent alcomponent[])
    {
        iType = 0;
        isRun = false;
        image = null;
        images = null;
        isDrag = false;
        selButton = 0;
        selPen = 0;
        imW = 0;
        imH = 0;
        selItem = -1;
        mgs = null;
        sizeTT = 0;
        tools = tools1;
        info = info1;
        mg = info.m;
        config = res;
        tPen = tpen;
        cs = alcomponent;
    }

    private int getIndex(int i, int j, int k)
    {
        Dimension dimension = getSize();
        int l = imW;
        int i1 = imH;
        if(iType != 2)
        {
            l += 3;
            i1 += 3;
        }
        i -= k;
        int j1 = (dimension.width - k) / l;
        return (j / i1) * j1 + Math.min(i / l, j1);
    }

    public void init(int i)
    {
        iType = i;
        Res res = config;
        i++;
        int j = 30;
        int k = 30;
        String s = String.valueOf(i);
        int l;
        for(l = 0; res.containsKey(String.valueOf('t') + s + l); l++);
        if(l != 0)
        {
            mgs = new Mg[l];
            for(int i1 = 0; i1 < l; i1++)
            {
                Mg mg1 = new Mg();
                mg1.set(res.get(String.valueOf('t') + s + i1));
                mgs[i1] = mg1;
            }

            for(int j1 = l - 1; j1 >= 0; j1--)
                if(mgs[j1].iPen == 4 || mgs[j1].iPen == 5)
                    selWhite = j1;

            String s1 = "res/" + i + ".gif";
            image = getToolkit().createImage((byte[])res.getRes(s1));
            Awt.wait(image);
            res.remove(s1);
            j = (int)((float)image.getWidth(null) * LComponent.Q);
            k = (int)((float)(image.getHeight(null) / l) * LComponent.Q);
            if(LComponent.Q != 1.0F)
                image = Awt.toMin(image, j, k * l);
            imW = j;
            imH = k;
            imCount = l;
        } else
        {
            imCount = 0;
            imW = 20;
            imH = 20;
        }
        j += 3;
        k += 3;
        selItem = -1;
        setItem(0, null);
        setDimension(new Dimension(j + 1, k + 1), new Dimension(j + 1, k * l + 1), new Dimension(j * l + 1, k * l + 1));
    }

    public void initHint()
    {
        try
        {
            String s = "res/3.gif";
            iType = 3;
            imCount = 7;
            int i = imCount;
            image = getToolkit().createImage((byte[])config.getRes(s));
            Awt.wait(image);
            config.remove(s);
            int j = image.getWidth(null);
            int k = image.getHeight(null);
            if(LComponent.Q != 1.0F)
            {
                j = (int)((float)j * LComponent.Q);
                k = ((int)((float)k * LComponent.Q) / i) * i;
                image = Awt.toMin(image, j, k);
            }
            k /= i;
            imW = j;
            imH = k;
            j += 3;
            k += 3;
            setDimension(new Dimension(j + 1, k + 1), new Dimension(j + 1, k * i + 1), new Dimension(j * i + 1, k * i + 1));
        }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
        }
    }

    public void initTT()
    {
        iType = 2;
        Res res = config;
        getToolkit();
        cmDef = new DirectColorModel(24, 65280, 65280, 255);
        imW = imH = (int)(34F * LComponent.Q);
        try
        {
            String s = "tt_size";
            images = new Image[Integer.parseInt(res.get(s))];
            res.remove(s);
            int i = imW * 5 + 1;
            int j = ((images.length + 12) / 5 + 1) * imW + 1;
            setDimension(new Dimension(imW + 1, imW + 1), new Dimension(i, j), new Dimension(i * 2, j * 2));
        }
        catch(RuntimeException _ex) { }
    }

    private void mouseH(MouseEvent mouseevent)
    {
        if(mouseevent.getID() != 501)
            return;
        int i = getIndex(mouseevent.getX(), mouseevent.getY(), 0);
        if(i >= 7)
        {
            return;
        } else
        {
            mg.iHint = i;
            repaint();
            return;
        }
    }

    private void mousePen(MouseEvent mouseevent)
    {
        if(mouseevent.getID() == 501)
        {
            int i = getIndex(mouseevent.getX(), mouseevent.getY(), 0);
            if(i >= imCount)
                return;
            setItem(i, null);
        }
    }

    private void mouseTT(MouseEvent mouseevent)
    {
        if(mouseevent.getID() == 501)
        {
            getSize();
            int i = getIndex(mouseevent.getX(), mouseevent.getY(), 0);
            if(i >= images.length + 12)
                return;
            mg.iTT = i;
            repaint();
        }
    }

    public void paint2(Graphics g)
    {
        switch(iType)
        {
        case 2: // '\002'
            paintTT(g);
            break;

        case 3: // '\003'
            selItem = mg.iHint;
            // fall through

        default:
            paintPen(g);
            break;
        }
    }

    private void paintPen(Graphics g)
    {
        if(image == null)
            return;
        int i = 0;
        int j = 0;
        int k = imW;
        int l = imH;
        int i1 = imW + 3;
        int j1 = imH + 3;
        Dimension dimension = getSize();
        for(int k1 = 0; k1 < imCount; k1++)
        {
            g.setColor(selItem != k1 ? Awt.cF : Awt.cFSel);
            g.drawRect(i + 1, j + 1, k + 1, l + 1);
            g.drawImage(image, i + 2, j + 2, i + k + 2, j + l + 2, 0, k1 * l, k, (k1 + 1) * l, null);
            if(selItem == k1)
            {
                g.setColor(Color.black);
                g.fillRect(i + 2, j + 2, k, 1);
                g.fillRect(i + 2, j + 3, 1, l - 1);
            }
            i = i + i1 * 2 < dimension.width ? i + i1 : 0;
            j = i != 0 ? j : j + j1;
            if(j + j1 >= dimension.height)
                break;
        }

    }

    private void paintTT(Graphics g)
    {
        if(images == null)
            return;
        if(!isRun)
        {
            Thread thread = new Thread(this);
            thread.setPriority(1);
            thread.setDaemon(true);
            thread.start();
            isRun = true;
        }
        int i = images.length + 11;
        int j = 0;
        int k = 0;
        int l = imW;
        int i1 = imH;
        int j1 = l - 3;
        int ai[] = tools.iBuffer;
        Dimension dimension = getSize();
        getToolkit();
        int i2 = getBackground().getRGB();
        for(int j2 = -1; j2 < i; j2++)
        {
            g.setColor(j2 + 1 != mg.iTT ? Awt.cF : Awt.cFSel);
            g.drawRect(j + 1, k + 1, l - 2, i1 - 2);
            if(j2 == -1)
            {
                g.setColor(Color.blue);
                g.fillRect(j + 2, k + 2, l - 3, i1 - 3);
            } else
            if(j2 < 11)
            {
                synchronized(ai)
                {
                    int k2 = 0;
                    int l2 = j2;
                    for(int l1 = 0; l1 < j1; l1++)
                    {
                        for(int k1 = 0; k1 < j1; k1++)
                            ai[k2++] = Mg.isTone(l2, k1, l1) ? i2 : 0xff0000ff;

                    }

                    g.drawImage(tools.mkImage(j1, j1), j + 2, k + 2, getBackground(), null);
                }
            } else
            {
                Image image1 = images[j2 - 11];
                if(image1 == null)
                {
                    g.setColor(Color.blue);
                    g.fillRect(j + 2, k + 2, l - 3, i1 - 3);
                } else
                {
                    g.drawImage(image1, j + 2, k + 2, getBackground(), null);
                }
            }
            j += l;
            if(j + l < dimension.width)
                continue;
            j = 0;
            k += i1;
            if(k + i1 >= dimension.height)
                break;
        }

    }

    public void pMouse(MouseEvent mouseevent)
    {
        switch(iType)
        {
        default:
            mousePen(mouseevent);
            break;

        case 2: // '\002'
            mouseTT(mouseevent);
            break;

        case 3: // '\003'
            mouseH(mouseevent);
            break;
        }
    }

    public void run()
    {
        try
        {
            int i = imW;
            int j = imH;
            for(int k = 0; k < images.length; k++)
                if(images[k] == null)
                {
                    float af[] = info.getTT(k + 12);
                    int ai[] = new int[af.length];
                    for(int l = 0; l < ai.length; l++)
                        ai[l] = (int)((1.0F - af[l]) * 255F) << 8 | 0xff;

                    int i1 = (int)Math.sqrt(ai.length);
                    images[k] = Awt.toMin(createImage(new MemoryImageSource(i1, i1, cmDef, ai, 0, i1)), i - 3, j - 3);
                    if(k % 5 == 2)
                        repaint();
                }

            repaint();
        }
        catch(Throwable _ex) { }
    }

    public void setItem(int i, Mg mg1)
    {
        if(iType == 1)
        {
            tPen.setItem(-1, mg1);
        } else
        {
            if(selItem >= 0 && selItem < imCount)
                mgs[selItem].set(mg);
            if(i >= 0)
            {
                int j = mgs[i].iPen;
                if(j == 4 || j == 5)
                    selWhite = i;
                else
                    selPen = i;
            }
        }
        selItem = i;
        if(i >= 0 || mg1 != null)
        {            
            mg1 = (mg1 == null ? mgs[i] : mg1);
            // copy over colors
            mg1.setColorAndLayer(mg);
            mg.set(mg1);
            if(tPen != null)
                tPen.repaint();
            for(int k1 = 0; k1 < cs.length; k1++) {
                if(cs[k1] != null)
                    cs[k1].repaint();
            }
            tools.mgChange();

        } else
        {
            repaint();
        }
    }

    public void undo(boolean flag)
    {
        if(flag)
        {
            if(selWhite != selItem)
                setItem(selWhite, null);
        } else
        if(selPen != selItem)
            setItem(selPen, null);
    }

    private Tools tools;
    private int iType;
    private boolean isRun;
    private LComponent cs[];
    private TPen tPen;
    private paintchat.Mg.Info info;
    private Mg mg;
    private Res config;
    private Image image;
    private Image images[];
    private boolean isDrag;
    private int selButton;
    private int selWhite;
    private int selPen;
    private int imW;
    private int imH;
    private int imCount;
    private int selItem;
    private Mg mgs[];
    private ColorModel cmDef;
    private int sizeTT;
}
