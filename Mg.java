// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/26/2009 3:48:46 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Mg.java

package paintchat;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Hashtable;
import syi.awt.Awt;
import syi.util.ByteStream;

// Referenced classes of package paintchat:
//            Res, SRaster

public class Mg
{
    public class User
    {

        private void setup(Mg mg)
        {
            pV = Mg.b255[mg.info.bPen[mg.iPenM].length - 1];
            mg.getPM();
            count = 0;
            isF2 = false;
            isDirect = mg.iPen == 3 || mg.iHint == 9 || mg.isOver;
            if(mg.iTT >= 12)
            {
                pTT = info.getTT(mg.iTT);
                pTTW = (int)Math.sqrt(pTT.length);
            }
        }

        public void setIm(Mg mg)
        {
            if(mg.iHint == 8)
                return;
            if(pM != mg.iPenM || pA != mg.iAlpha || pS != mg.iSize)
            {
                int ai[] = mg.info.bPen[mg.iPenM][mg.iSize];
                int i = ai.length;
                if(p == null || p.length < i)
                    p = new int[i];
                float f = Mg.b255[mg.iAlpha];
                for(int j = 0; j < i; j++)
                {
                    float f1 = (float)ai[j] * f;
                    p[j] = f1 > 1.0F || f1 <= 0.0F ? (int)f1 : 1;
                }

                pW = (int)Math.sqrt(i);
                pM = mg.iPenM;
                pA = mg.iAlpha;
                pS = mg.iSize;
            }
        }

        public int getPixel(int i, int j)
        {
            int k = info.imW;
            if(i < 0 || j < 0 || i >= k || j >= info.imH)
            {
                return 0;
            } else
            {
                mkLPic(buffer, i, j, 1, 1, info.Q);
                return info.iOffs[info.m.iLayer][k * j + i] & 0xff000000 | buffer[0];
            }
        }

        public int[] getBuffer()
        {
            return buffer;
        }

        public long getRect()
        {
            return (long)dX << 48 | (long)dY << 32 | (long)(dW << 16) | (long)dH;
        }

        private Image image;
        private SRaster raster;
        private int buffer[];
        private int argb[];
        public int points[];
        private int ps2[];
        private int p[];
        private int pW;
        private int pM;
        private int pA;
        private int pS;
        private float pV;
        private float pTT[];
        private int pTTW;
        private boolean isDirect;
        public int wait;
        private int oX;
        private int oY;
        private int pX;
        private int pY;
        private int pX2;
        private int pY2;
        private float fX;
        private float fY;
        private boolean isF2;
        private int dX;
        private int dY;
        private int dW;
        private int dH;
        private int count;
        private int countMax;



        public User()
        {
            image = null;
            raster = null;
            buffer = new int[0x10000];
            argb = new int[4];
            points = new int[6];
            ps2 = null;
            p = null;
            pM = -1;
            pA = -1;
            pS = -1;
            pV = 1.0F;
            pTT = null;
            wait = 0;
            count = 0;
        }
    }

    public class Info
    {

        public void setSize(int i, int j, int k)
        {
            if(i * k != W || j * k != H)
                iOffs = null;
            imW = i;
            imH = j;
            W = i * k;
            H = j * k;
            Q = k;
            int l = W * H;
            if(iMOffs == null || iMOffs.length < l)
                iMOffs = new byte[l];
        }

        public void setIOffs(int ai[][])
        {
            L = ai.length;
            iOffs = ai;
            if(visit == null || visit.length != L)
                memset(visit = new float[L], 1.0F);
        }

        public void setComponent(Component component1, Graphics g1, int i, int j)
        {
            component = component1;
            vWidth = i;
            vHeight = j;
            g = g1;
        }

        public void setL(int i)
        {
            int j = iOffs != null ? iOffs.length : 0;
            Math.min(j, i);
            if(j != i)
            {
                float af[] = new float[i];
                int ai[][] = new int[i][];
                int k = W * H;
                for(int l = 0; l < i; l++)
                    if(l >= j)
                    {
                        af[l] = 1.0F;
                        memset(ai[l] = new int[k], 0xffffff);
                    } else
                    {
                        ai[l] = iOffs[l];
                        af[l] = visit[l];
                    }

                visit = af;
                iOffs = ai;
            }
            L = i;
        }

        public void delL(int i)
        {
            int j = iOffs.length;
            if(i >= j)
                return;
            int k = j - 1;
            float af[] = new float[k];
            int ai[][] = new int[k][];
            k = 0;
            for(int l = 0; l < j; l++)
                if(l != i)
                {
                    af[k] = visit[l];
                    ai[k++] = iOffs[l];
                }

            visit = af;
            iOffs = ai;
            L = j - 1;
        }

        public boolean addScale(int i, boolean flag)
        {
            if(flag)
            {
                if(i <= 0)
                {
                    scale = 1;
                    setQuality(1 - i);
                } else
                {
                    setQuality(1);
                    scale = i;
                }
                return true;
            }
            int j = scale + i;
            if(j > 32)
                return false;
            if(j <= 0)
            {
                scale = 1;
                setQuality((Q + 1) - j);
            } else
            if(Q >= 2)
            {
                setQuality(Q - 1);
            } else
            {
                setQuality(1);
                scale = j;
            }
            return true;
        }

        public void setQuality(int i)
        {
            Q = i;
            imW = W / Q;
            imH = H / Q;
        }

        public Dimension getSize()
        {
            vD.setSize(vWidth, vHeight);
            return vD;
        }

        private void center(Point point)
        {
            point.x = point.x / scale + scaleX;
            point.y = point.y / scale + scaleY;
        }

        public int[][][] getPenMask()
        {
            return bPen;
        }

        public int getPMMax()
        {
            return m.iHint != 8 ? bPen[m.iPenM].length : 255;
        }

        public int[][] getOffset()
        {
            return iOffs;
        }

        public float[] getTT(int i)
        {
            i -= 12;
            if(bTT[i] == null && bTT[i] == null)
            {
                if(dirTT != null)
                {
                    String s1 = dirTT;
                    dirTT = null;
                    try
                    {
                        cnf.loadZip(s1);
                    }
                    catch(IOException ioexception)
                    {
                        ioexception.printStackTrace();
                    }
                }
                int ai[] = loadIm("tt/" + i + ".gif", false);
                if(ai == null)
                    return null;
                int j = ai.length;
                float af[] = new float[j];
                for(int k = 0; k < j; k++)
                    af[k] = Mg.b255[ai[k]];

                bTT[i] = af;
            }
            return bTT[i];
        }

        private ByteStream workOut;
        private Res cnf;
        private String dirTT;
        public Graphics g;
        public String text;
        public String textOption;
        private int vWidth;
        private int vHeight;
        private Dimension vD;
        private Component component;
        public int Q;
        public int L;
        public float visit[];
        public int scale;
        public int scaleX;
        public int scaleY;
        private int iOffs[][];
        private byte iMOffs[];
        public int imH;
        public int imW;
        public int W;
        public int H;
        private int bPen[][][];
        private float bTT[][];
        public Mg m;













        public Info()
        {
            workOut = new ByteStream();
            dirTT = null;
            g = null;
            text = "";
            textOption = "";
            vD = new Dimension();
            component = null;
            Q = 1;
            scale = 1;
            scaleX = 0;
            scaleY = 0;
            bPen = new int[16][][];
            bTT = new float[14][];
            m = new Mg();
        }
    }


    public Mg()
    {
        iHint = 0;
        iPen = 0;
        iPenM = 0;
        iTT = 0;
        iColor = 0;
        iColorMask = 0;
        iAlpha = 255;
        iSA = 65280;
        iLayer = 0;
        iLayerSrc = 1;
        iMask = 0;
        iSize = 0;
        iSS = 65280;
        iCount = -8;
        isCount = true;
    }

    public Mg(Info info1, User user1)
    {
        iHint = 0;
        iPen = 0;
        iPenM = 0;
        iTT = 0;
        iColor = 0;
        iColorMask = 0;
        iAlpha = 255;
        iSA = 65280;
        iLayer = 0;
        iLayerSrc = 1;
        iMask = 0;
        iSize = 0;
        iSS = 65280;
        iCount = -8;
        isCount = true;
        info = info1;
        user = user1;
    }

    private final void addD(int i, int j, int k, int l)
    {
        setD(Math.min(i, user.dX), Math.min(j, user.dY), Math.max(k, user.dW), Math.max(l, user.dH));
    }

    private final void ch(int i, int j)
    {
        int ai[][] = info.getOffset();
        int ai1[] = ai[i];
        int ai2[] = ai[j];
        int k = info.W * info.H;
        for(int i1 = 0; i1 < k; i1++)
        {
            int l = ai2[i1];
            ai2[i1] = ai1[i1];
            ai1[i1] = l;
        }

    }

    private final void copy(int ai[][], int ai1[][])
    {
        for(int i = 0; i < ai1.length; i++)
            System.arraycopy(ai[i], 0, ai1[i], 0, ai1[i].length);

    }

    public final void dBuffer()
    {
        dBuffer(!user.isDirect, user.dX, user.dY, user.dW, user.dH);
    }

    private final void dBuffer(boolean flag, int i, int j, int k, int l)
    {
        try
        {
            int i1 = info.scale;
            int j1 = info.Q;
            int k1 = info.W;
            int l1 = info.H;
            int j2 = info.scaleX;
            int k2 = info.scaleY;
            boolean flag1 = i1 == 1;
            int ai[] = user.buffer;
            Color color = Color.white;
            Graphics g = info.g;
            if(g == null)
                return;
            i /= j1;
            j /= j1;
            k /= j1;
            l /= j1;
            i = i > j2 ? i : j2;
            j = j > k2 ? j : k2;
            int l2 = info.vWidth / i1 + j2;
            k = k <= l2 ? k : l2;
            k = k <= k1 ? k : k1;
            l2 = info.vHeight / i1 + k2;
            l = l <= l2 ? l : l2;
            l = l <= l1 ? l : l1;
            if(k <= i || l <= j)
                return;
            k1 = k - i;
            int i3 = k1 * i1;
            int j3 = (i - j2) * i1;
            int _tmp = i;
            int k3 = j;
            l2 = ai.length / (k1 * j1 * j1);
            do
            {
                int i2 = Math.min(l2, l - k3);
                if(i2 <= 0)
                    break;
                Image image = flag ? mkMPic(i, k3, k1, i2, j1) : mkLPic(null, i, k3, k1, i2, j1);
                if(flag1)
                    g.drawImage(image, j3, k3 - k2, color, null);
                else
                    g.drawImage(image, j3, (k3 - k2) * i1, i3, i2 * i1, color, null);
                k3 += i2;
            } while(true);
        }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
        }
    }

    private final void dBz(int ai[])
        throws InterruptedException
    {
        try
        {
            int i = ai[0];
            int k = 0;
            for(int l = 1; l < 4; l++)
            {
                k += Math.abs((ai[l] >> 16) - (i >> 16)) + Math.abs((short)ai[l] - (short)i);
                i = ai[l];
            }

            if(k <= 0)
                return;
            byte byte0 = -100;
            byte byte1 = -100;
            int k1 = -1000;
            int l1 = -1000;
            boolean flag = iPen == 1;
            Object _tmp = user.count;
            for(int j = k; j > 0; j--)
            {
                float f1 = (float)j / (float)k;
                float f = (float)Math.pow(1.0F - f1, 3D);
                float f2 = f * (float)(ai[3] >> 16);
                float f3 = f * (float)(short)ai[3];
                f = 3F * (1.0F - f1) * (1.0F - f1) * f1;
                f2 += f * (float)(ai[2] >> 16);
                f3 += f * (float)(short)ai[2];
                f = 3F * f1 * f1 * (1.0F - f1);
                f2 += f * (float)(ai[1] >> 16);
                f3 += f * (float)(short)ai[1];
                f = f1 * f1 * f1;
                f2 += f * (float)(ai[0] >> 16);
                f3 += f * (float)(short)ai[0];
                int i1 = (int)f2;
                int j1 = (int)f3;
                if(i1 != k1 || j1 != l1)
                {
                    Object _tmp1 = user.oX;
                    Object _tmp2 = user.oY;
                    if(flag)
                        dFLine2(i1, j1, iSize);
                    else
                        dFLine(i1, j1, iSize);
                    k1 = i1;
                    l1 = j1;
                }
            }

            Object _tmp3 = user.dX;
            Object _tmp4 = user.dY;
            Object _tmp5 = user.dW;
            Object _tmp6 = user.dH;
        }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
        }
    }

    public void dClear(boolean flag)
    {
        if(flag)
            getWork();
        int i = info.W;
        int j = info.H;
        int ai[][] = info.iOffs;
        int ai1[] = ai[0];
        int k = 0xffffff;
        int l = i * j;
        synchronized(ai)
        {
            for(int i1 = 0; i1 < i; i1++)
                ai1[i1] = k;

            for(int j1 = i; j1 < l; j1 += i)
                System.arraycopy(ai1, 0, ai1, j1, i);

            for(int k1 = 1; k1 < ai.length; k1++)
                System.arraycopy(ai1, 0, ai[k1], 0, ai1.length);

        }
        Object _tmp = user.isDirect;
        setD(0, 0, i, j);
        if(user.wait >= 0)
            dBuffer();
    }

    private void dCopy(int ai[])
    {
        int i = info.W;
        int j = info.H;
        int k = ai[0];
        int l = k >> 16;
        int i1 = (short)k;
        k = ai[1];
        int j1 = (k >> 16) - l;
        int k1 = (short)k - i1;
        k = ai[2];
        int l1 = k >> 16;
        short word0 = (short)k;
        if(l < 0)
        {
            j1 -= l;
            l = 0;
        }
        if(l + j1 > i)
            l = i - j1;
        if(i1 < 0)
        {
            k1 -= i1;
            i1 = 0;
        }
        if(i1 + k1 > j)
            i1 = j - k1;
        if(l1 < 0)
        {
            l -= l1;
            j1 += l1;
            l1 = 0;
        }
        if(word0 < 0)
        {
            i1 -= word0;
            k1 += word0;
            word0 = 0;
        }
        if(l1 + j1 >= i)
            j1 = i - l1;
        if(word0 + k1 >= j)
            k1 = j - word0;
        if(j1 <= 0 || k1 <= 0 || l1 >= i || word0 >= j)
            return;
        int ai1[] = j1 * k1 > user.buffer.length ? new int[j1 * k1] : user.buffer;
        int ai2[] = info.iOffs[iLayerSrc];
        for(int i2 = 0; i2 < k1; i2++)
            System.arraycopy(ai2, (i1 + i2) * i + l, ai1, j1 * i2, j1);

        ai2 = info.iOffs[iLayer];
        for(int j2 = 0; j2 < k1; j2++)
            System.arraycopy(ai1, j1 * j2, ai2, (word0 + j2) * i + l1, j1);

        setD(l1, word0, l1 + j1, word0 + k1);
    }

    public final void dEnd()
        throws InterruptedException
    {
        if(iHint == 0 && (user.pX != user.pX2 || user.pY != user.pY2))
            dFLine(user.pX, user.pY + 1, 1);
        if(!user.isDirect)
            dFlush();
        ByteStream bytestream = info.workOut;
        if(bytestream.size() > 0)
        {
            offset = bytestream.writeTo(offset, 0);
            iOffset = bytestream.size();
        }
        if(user.wait == -1)
            dBuffer();
    }

    private void dFill(int i, int j)
    {
        int k = info.W;
        int l = info.H;
        int[] _tmp = info.iOffs[iLayer];
        byte byte0 = (byte)iAlpha;
        byte abyte0[] = info.iMOffs;
        try
        {
            int ai[] = user.buffer;
            int i1 = 0;
            if(i < 0 || i >= k || j < 0 || j >= l)
                return;
            int l1 = pix(i, j);
            int i2 = iAlpha << 24 | iColor;
            if(l1 == i2)
                return;
            ai[i1++] = s(l1, i, j) << 16 | j;
            while(i1 > 0) 
            {
                int j1 = ai[--i1];
                i = j1 >>> 16;
                j = j1 & 0xffff;
                int k1 = k * j;
                boolean flag = false;
                boolean flag1 = false;
                do
                {
                    abyte0[k1 + i] = byte0;
                    if(j > 0 && pix(i, j - 1) == l1 && abyte0[(k1 - k) + i] == 0)
                    {
                        if(!flag)
                        {
                            flag = true;
                            ai[i1++] = s(l1, i, j - 1) << 16 | j - 1;
                        }
                    } else
                    {
                        flag = false;
                    }
                    if(j < l - 1 && pix(i, j + 1) == l1 && abyte0[k1 + k + i] == 0)
                    {
                        if(!flag1)
                        {
                            flag1 = true;
                            ai[i1++] = s(l1, i, j + 1) << 16 | j + 1;
                        }
                    } else
                    {
                        flag1 = false;
                    }
                    if(i <= 0 || pix(i - 1, j) != l1 || abyte0[(k1 + i) - 1] != 0)
                        break;
                    i--;
                } while(true);
            }
        }
        catch(RuntimeException runtimeexception)
        {
            System.out.println(runtimeexception);
        }
        setD(0, 0, k, l);
        t();
    }

    private void dFill(Object obj, int i, int j, int k, int l)
    {
        int i1 = iAlpha;
        int ai[];
        byte abyte0[];
        if(obj instanceof int[])
        {
            ai = (int[])obj;
            abyte0 = null;
        } else
        {
            ai = null;
            abyte0 = (byte[])obj;
        }
        int j1 = info.W;
        try
        {
            int j2 = k - i;
            for(; j < l; j++)
            {
                int k1;
                int l1 = k1 = j * j1 + i;
                int i2;
                for(i2 = 0; i2 < j2; i2++)
                {
                    if(abyte0 != null && abyte0[k1] != 0 || ai != null && ai[k1] != 0)
                        break;
                    k1++;
                }

                for(; i2 < j2; i2++)
                {
                    if(abyte0 != null && abyte0[k1] == 0 || ai != null && ai[k1] == 0)
                        break;
                    k1++;
                }

                int k2;
                for(k2 = i2; k2 < j2; k2++)
                    if(abyte0 != null && abyte0[l1 + k2] != 0 || ai != null && ai[l1 + k2] != 0)
                        break;

                if(k2 < j2)
                    for(; i2 < j2; i2++)
                    {
                        if(abyte0 != null && abyte0[k1] != 0 || ai != null && ai[k1] != 0)
                            break;
                        if(ai != null)
                            ai[k1] = i1;
                        if(abyte0 != null)
                            abyte0[k1] = (byte)i1;
                        k1++;
                    }

            }

        }
        catch(RuntimeException runtimeexception)
        {
            System.out.println(runtimeexception);
        }
    }

    private final void dFLine(int paramInt1, int paramInt2, int paramInt3)
    throws InterruptedException
  {
    int i = this.user.wait;    
    int j = this.user.pX;
    int k = this.user.pY;
    int l = this.user.pX2;
    int i1 = this.user.pY2;
    int i2 = j;
    int i3 = k;
    int i4 = paramInt1 - j;
    int i5 = paramInt2 - k;
    int i6 = Math.max(Math.abs(i4), Math.abs(i5));
    if (i6 <= 0)
      return;
    if (!(this.isCount))
      this.user.count = 0;
    int i9 = ss(paramInt3);
    int i10 = sa(paramInt3);
    float f1 = this.iSize;
    float f2 = this.iAlpha;
    float f3 = (i9 - f1) / i6;
    f3 = (f3 <= -1.0F) ? -1.0F : (f3 >= 1.0F) ? 1.0F : f3;
    float f4 = (i10 - f2) / i6;
    f4 = (f4 <= -1.0F) ? -1.0F : (f4 >= 1.0F) ? 1.0F : f4;
    float f5 = (i4 == 0) ? 0.0F : i4 / i6;
    float f6 = (i5 == 0) ? 0.0F : i5 / i6;
    float f7 = j;
    float f8 = k;
    for (int i11 = 0; i11 < i6; ++i11)
    {
      if ((l != i2) || (i1 != i3))
      {        
        int tmp289_288 = (this.user.count - 1);
        this.user.count =  tmp289_288;
        if (tmp289_288 < 0)
        {
          this.iSize = (int)f1;
          this.iAlpha = (int)f2;
          getPM();
          int i7 = i2 - (this.user.pW >>> 1);
          int i8 = i3 - (this.user.pW >>> 1);
          dPen(i7, i8, 1.0F);
          l = i2;
          i1 = i3;
          this.user.count =  this.user.countMax;
          if (i > 0)
          {
            dBuffer((this.user.isDirect) ? false : true, i7, i8, i7 + this.user.pW, i8 + this.user.pW);
            if (i > 1)
            {
              Thread.currentThread();
              Thread.sleep(i);
            }
          }
        }
      }
      i2 = (int)(f7 += f5);
      i3 = (int)(f8 += f6);
      f1 += f3;
      f2 += f4;
    }
    this.iAlpha = (int)(f2 - f4);
    this.iSize = (int)(f1 - f3);
    int tmp514_512 = l;
    this.user.pX = tmp514_512;
    this.user.pX2 = tmp514_512;
    int tmp531_529 = i1;
    this.user.pY = tmp531_529;
    this.user.pY2 = tmp531_529;
    i6 = (int)Math.sqrt(this.info.bPen[this.iPenM][Math.max(i9, (int)f1)].length) / 2 + this.info.Q;
    i4 = Math.min(j, paramInt1) - i6;
    i5 = Math.min(k, paramInt2) - i6;
    paramInt1 = Math.max(j, paramInt1) + i6;
    paramInt2 = Math.max(k, paramInt2) + i6;
    if (i == 0)
      dBuffer((this.user.isDirect) ? false : true, i4, i5, paramInt1, paramInt2);
    addD(i4, i5, paramInt1, paramInt2);
  }

    private final void dFLine2(int paramInt1, int paramInt2, int paramInt3)
    throws InterruptedException
  {
    try
    {
      if (!(this.user.isF2))
      {
        this.user.pX2 = paramInt1;
        this.user.pY2 = paramInt2;
        this.user.isF2 = true;
        return;
      }

      float f1 = this.user.fX;
      float f2 = this.user.fY;
      int i = this.user.pX2;
      int j = this.user.pY2;
      if (((int)f1 == i) && (i == paramInt1) && ((int)f2 == j) && (j == paramInt2))
        return;
      float f3 = (float)Math.sqrt((paramInt1 - f1) * (paramInt1 - f1) + (paramInt2 - f2) * (paramInt2 - f2));
      float f6 = f1;
      float f7 = f2;
      float f10 = f3 / 1.65F;
      int i1 = this.user.wait;
      int i6 = ss(paramInt3);
      int i7 = sa(paramInt3);
      float f11 = this.iSize;
      float f12 = this.iAlpha;
      if (!(this.isCount))
        this.user.count = 0;
      float f13 = (i6 - f11) / (f3 - 0.25F - f10) * 4.0F;
      f13 = (f13 <= -0.25F) ? -0.25F : (f13 >= 0.25F) ? 0.25F : f13;
      float f14 = (i7 - f12) / (f3 - 0.25F - f10) * 4.0F;
      f14 = (f14 <= -0.25F) ? -0.25F : (f14 >= 0.25F) ? 0.25F : f14;
      float f15 = f3 - 0.25F;
      while (f15 >= f10)
      {
        int tmp329_326 = this.user.count;
        this.user.count = tmp329_326 - 1;
        if (tmp329_326 <= 0)
        {
          int k;
          int l;
          this.user.count = this.user.countMax;
          float f5 = f15 / f3;
          float f4 = (1.0F - f5) * (1.0F - f5);
          f6 = f4 * paramInt1;
          f7 = f4 * paramInt2;
          f4 = 2.0F * f5 * (1.0F - f5);
          f6 += f4 * i;
          f7 += f4 * j;
          f4 = f5 * f5;
          f6 += f4 * f1;
          f7 += f4 * f2;
          this.iSize = (int)f11;
          this.iAlpha = (int)f12;
          getPM();
          int i2 = (k = (int)f6) - (this.user.pW >>> 1);
          int i3 = (l = (int)f7) - (this.user.pW >>> 1);
          if (this.isAnti)
          {
            int i4;
            int i5;
            float f8 = f6 - k;
            float f9 = f7 - l;
            if (f8 < 0.0F)
            {
              f8 = -f8;
              i4 = -1;
            }
            else
            {
              i4 = 1;
            }
            if (f9 < 0.0F)
            {
              f9 = -f9;
              i5 = -1;
            }
            else
            {
              i5 = 1;
            }
            if ((f8 != 1.0F) && (f9 != 1.0F))
              dPen(i2, i3, (1.0F - f8) * (1.0F - f9));
            if (f8 != 0.0F)
              dPen(i2 + i4, i3, f8 * (1.0F - f9));
            if (f9 != 0.0F)
              dPen(i2, i3 + i5, (1.0F - f8) * f9);
            if ((f8 != 0.0F) && (f9 != 0.0F))
              dPen(i2 + i4, i3 + i5, f8 * f9);
          }
          else
          {
            dPen(i2, i3, 1.0F);
          }
          if (i1 > 0)
          {
            dBuffer((this.user.isDirect) ? false : true, i2, i3, i2 + this.user.pW + 1, i3 + this.user.pW + 1);
            if (i1 > 1)
            {
              Thread.currentThread();
              Thread.sleep(i1);
            }
          }
        }
        f11 += f13;
        f12 += f14;
        f15 -= 0.25F;
      }
      this.iSize = i6;
      this.iAlpha = i7;
      this.user.fX = f6;
      this.user.fY = f7;
      int tmp827_826 = paramInt1;
      this.user.pX2 = tmp827_826;
      this.user.pX = tmp827_826;
      int tmp843_842 = paramInt2;
      this.user.pY2 = tmp843_842;
      this.user.pY = tmp843_842;
      int i2 = (int)Math.sqrt(this.info.bPen[this.iPenM][Math.max(i6, (int)f11)].length) / 2 + this.info.Q + 1;
      int i8 = Math.min(Math.min((int)f1, i), (int)f6) - i2;
      int i9 = Math.min(Math.min((int)f2, j), (int)f7) - i2;
      int i10 = Math.max(Math.max((int)f1, i), (int)f6) + i2;
      int i11 = Math.max(Math.max((int)f2, j), (int)f7) + i2;
      if (this.user.wait == 0)
        dBuffer((this.user.isDirect) ? false : true, i8, i9, i10, i11);
      addD(i8, i9, i10, i11);
    }
    catch (RuntimeException localRuntimeException)
    {
      localRuntimeException.printStackTrace();
    }
  }

    private final void dFlush()
    {
        int _tmp = info.Q;
        int i3 = info.W;
        int j3 = info.H;
        int k3 = user.dX > 0 ? user.dX : 0;
        int l3 = user.dY > 0 ? user.dY : 0;
        int i4 = user.dW < i3 ? user.dW : i3;
        int j4 = user.dH < j3 ? user.dH : j3;
        byte abyte0[] = info.iMOffs;
        int ai[] = info.iOffs[iLayer];
        switch(iPen)
        {
        case 17: // '\021'
            int i7 = (i4 - k3) / 2;
            for(; l3 < j4; l3++)
            {
                int i = l3 * i3 + k3;
                int l5 = (i + (i4 - k3)) - 1;
                for(int l1 = 0; l1 < i7; l1++)
                {
                    int k4 = ai[i];
                    ai[i] = ai[l5];
                    ai[l5] = k4;
                    abyte0[i] = abyte0[l5] = 0;
                    i++;
                    l5--;
                }

            }

            break;

        case 18: // '\022'
            int j7 = (j4 - l3) / 2;
            for(; k3 < i4; k3++)
            {
                int j = l3 * i3 + k3;
                int i6 = j + (j4 - l3 - 1) * i3;
                for(int i2 = 0; i2 < j7; i2++)
                {
                    int l4 = ai[j];
                    ai[j] = ai[i6];
                    ai[i6] = l4;
                    abyte0[j] = abyte0[i6] = 0;
                    j += i3;
                    i6 -= i3;
                }

            }

            break;

        case 19: // '\023'
            int j6 = i4 - k3;
            int k7 = j4 - l3;
            int k8 = l3 * i3 + k3;
            int k9 = j6 * k7;
            int ai2[] = new int[k9];
            for(int j10 = 0; j10 < k7; j10++)
                System.arraycopy(ai, k8 + i3 * j10, ai2, j6 * j10, j6);

            for(int k10 = 0; k10 < j6; k10++)
            {
                ai[k3 + k10] = 0xffffff;
                abyte0[k8 + k10] = 0;
            }

            for(int l10 = 1; l10 < k7; l10++)
            {
                System.arraycopy(ai, k8, ai, k8 + l10 * i3, j6);
                System.arraycopy(abyte0, k8, abyte0, k8 + l10 * i3, j6);
            }

            boolean flag = false;
            k9 = i3 * j3;
            for(int l11 = 0; l11 < k7; l11++)
            {
                int k = j6 * l11;
                int i5 = (k8 + k7) - l11;
                for(int j12 = 0; j12 < j6; j12++)
                {
                    int i13 = j12 + k3;
                    if(i13 <= i3 && i13 >= 0 && i5 < k9)
                        ai[i5] = ai2[k];
                    i5 += i3;
                    k++;
                }

            }

            addD(k3, l3, k3 + Math.max(j6, k7), l3 + j6);
            break;

        case 20: // '\024'
            if(iLayerSrc != iLayer)
            {
                int ai1[] = info.iOffs[iLayerSrc];
                int ai3[] = ai;
                int ai4[] = ai1;
                float f = b255[iAlpha2 >>> 8];
                float f1 = b255[iAlpha2 & 0xff];
                if(iLayer < iLayerSrc)
                {
                    ai3 = ai1;
                    ai4 = ai;
                    f = f1;
                    f1 = b255[iAlpha2 >>> 8];
                }
                for(; l3 < j4; l3++)
                {
                    int l = l3 * i3 + k3;
                    for(int j2 = k3; j2 < i4; j2++)
                    {
                        if(abyte0[l] != 0)
                        {
                            int l7 = (int)((float)(ai3[l] >>> 24) * f);
                            int k6 = (int)((float)(ai4[l] >>> 24) * b255[255 - l7] * f1);
                            int j5 = ai3[l];
                            int l8 = k6 + l7;
                            if(l8 != 0)
                            {
                                int l9 = ai4[l];
                                float f2 = (float)k6 / (float)l8;
                                j5 = f2 != 1.0F ? f2 != 0.0F ? ((j5 & 0xff0000) + (int)((float)((l9 & 0xff0000) - (j5 & 0xff0000)) * f2) & 0xff0000) + ((j5 & 0xff00) + (int)((float)((l9 & 0xff00) - (j5 & 0xff00)) * f2) & 0xff00) + ((j5 & 0xff) + (int)((float)((l9 & 0xff) - (j5 & 0xff)) * f2)) : j5 : l9;
                            } else
                            {
                                j5 = 0xffffff;
                            }
                            ai[l] = l8 << 24 | j5 & 0xffffff;
                            ai1[l] = 0xffffff;
                            abyte0[l] = 0;
                        }
                        l++;
                    }

                }

            }
            if(user.wait >= 0)
                dBuffer();
            break;

        case 9: // '\t'
            int k5 = iAlpha / 10 + 1;
            k3 = (k3 / k5) * k5;
            l3 = (l3 / k5) * k5;
            int ai5[] = user.argb;
            for(int l6 = l3; l6 < j4; l6 += k5)
            {
                for(int k2 = k3; k2 < i4; k2 += k5)
                {
                    int i10 = Math.min(k5, i3 - k2);
                    int i11 = Math.min(k5, j3 - l6);
                    for(int j11 = 0; j11 < 4; j11++)
                        ai5[j11] = 0;

                    int i12 = 0;
                    for(int i9 = 0; i9 < i11; i9++)
                    {
                        for(int i8 = 0; i8 < i10; i8++)
                        {
                            int k12 = pix(k2 + i8, l6 + i9);
                            int i1 = (l6 + i9) * i3 + k2 + i8;
                            for(int k11 = 0; k11 < 4; k11++)
                                ai5[k11] += k12 >>> k11 * 8 & 0xff;

                            i12++;
                        }

                    }

                    int l12 = ai5[3] << 24 | ai5[2] / i12 << 16 | ai5[1] / i12 << 8 | ai5[0] / i12;
                    for(int j9 = l6; j9 < l6 + i11; j9++)
                    {
                        int j1 = i3 * j9 + k2;
                        for(int j8 = 0; j8 < i10; j8++)
                        {
                            if(abyte0[j1] != 0)
                            {
                                abyte0[j1] = 0;
                                ai[j1] = l12;
                            }
                            j1++;
                        }

                    }

                }

            }

            if(user.wait >= 0)
                dBuffer(true, k3, l3, i4, j4);
            break;

        default:
            for(; l3 < j4; l3++)
            {
                int k1 = l3 * i3 + k3;
                for(int l2 = k3; l2 < i4; l2++)
                {
                    ai[k1] = getM(ai[k1], abyte0[k1] & 0xff, k1);
                    abyte0[k1] = 0;
                    k1++;
                }

            }

            break;
        }
    }

    private final boolean dNext()
        throws InterruptedException
    {
        if(iSeek >= iOffset)
            return false;
        int i = user.oX + rPo();
        int j = user.oY + rPo();
        int k = iSOB == 0 ? 0 : ru();
        Object _tmp = user.oX;
        Object _tmp1 = user.oY;
        if(iPen != 1)
            dFLine(i, j, k);
        else
            dFLine2(i, j, k);
        return true;
    }

    public final void dNext(int i, int j, int k)
        throws InterruptedException, IOException
    {
        int l = info.scale;
        int _tmp = user.pW;
        i = (i / l + info.scaleX) * info.Q;
        j = (j / l + info.scaleY) * info.Q;
        wPo(i - user.oX);
        wPo(j - user.oY);
        Object _tmp1 = user.oX;
        Object _tmp2 = user.oY;
        if(iSOB != 0)
            info.workOut.write(k);
        if(iPen == 1)
            dFLine2(i, j, k);
        else
            dFLine(i, j, k);
    }

    private final void dPen(int i, int j, float f)
    {
        if(iPen != 3)
        {
            dPenM(i, j, f);
            if(isOver)
                dFlush();
        } else
        {
            dPY(i, j);
        }
    }

    private final void dPenM(int i, int j, float f)
    {
        boolean flag = false;
        int _tmp = info.Q;
        int ai[] = getPM();
        int i2 = info.W;
        int j2 = user.pW;
        int k2 = j2 * Math.max(-j, 0) + Math.max(-i, 0);
        int l2 = Math.min(i + j2, i2);
        int i3 = Math.min(j + j2, info.H);
        if(l2 <= 0 || i3 <= 0)
            return;
        i = i > 0 ? i : 0;
        j = j > 0 ? j : 0;
        int ai1[] = info.iOffs[iLayer];
        byte abyte0[] = info.iMOffs;
        for(int l = j; l < i3; l++)
        {
            int i1 = i2 * l + i;
            int j1 = k2;
            k2 += j2;
            for(int k = i; k < l2; k++)
                if(isM(ai1[i1]))
                {
                    i1++;
                    j1++;
                } else
                {
                    int k1 = abyte0[i1] & 0xff;
                    int l1 = ai[j1++];
                    if(l1 == 0)
                        i1++;
                    else
                        switch(iPen)
                        {
                        case 1: // '\001'
                        case 20: // '\024'
                            abyte0[i1++] = (byte)(k1 + (int)((float)l1 * b255[255 - k1 >>> 1] * f));
                            break;

                        case 2: // '\002'
                        case 5: // '\005'
                        case 6: // '\006'
                        case 7: // '\007'
                            if((l1 = (int)((float)l1 * getTT(k, l))) != 0)
                                abyte0[i1] = (byte)Math.min(k1 + Math.max((int)((float)l1 * b255[255 - k1 >>> 2]), 1), 255);
                            i1++;
                            break;

                        default:
                            abyte0[i1++] = (byte)Math.max((int)((float)l1 * getTT(k, l)), k1);
                            break;
                        }
                }

        }

    }

    public void dPre(Graphics g, int ai[])
    {
        try
        {
            int k = ai[0];
            int l = ai[1];
            int i1 = ai[2];
            int j1 = ai[3];
            int i = ai[0];
            int k1 = 0;
            for(int l1 = 1; l1 < 4; l1++)
            {
                k1 += Math.abs((ai[l1] >> 16) - (i >> 16)) + Math.abs((short)ai[l1] - (short)i);
                i = ai[l1];
            }

            k1 /= 2;
            if(k1 <= 0)
                return;
            byte byte0 = -100;
            byte byte1 = -100;
            int k2 = k >> 16;
            int l2 = (short)k;
            for(int j = k1; j > 0; j--)
            {
                float f1 = (float)j / (float)k1;
                float f = (float)Math.pow(1.0F - f1, 3D);
                float f2 = f * (float)(j1 >> 16);
                float f3 = f * (float)(short)j1;
                f = 3F * (1.0F - f1) * (1.0F - f1) * f1;
                f2 += f * (float)(i1 >> 16);
                f3 += f * (float)(short)i1;
                f = 3F * f1 * f1 * (1.0F - f1);
                f2 += f * (float)(l >> 16);
                f3 += f * (float)(short)l;
                f = f1 * f1 * f1;
                f2 += f * (float)(k >> 16);
                f3 += f * (float)(short)k;
                int i2 = (int)f2;
                int j2 = (int)f3;
                if(i2 != k2 || j2 != l2)
                {
                    g.fillRect(i2, j2, 1, 1);
                    k2 = i2;
                    l2 = j2;
                }
            }

        }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
        }
    }

    private final void dPY(int i, int j)
    {
        boolean flag = false;
        int ai[] = getPM();
        int j2 = info.W;
        int k2 = user.pW;
        int l2 = k2 * Math.max(-j, 0) + Math.max(-i, 0);
        int i3 = l2;
        int j3 = Math.min(i + k2, j2);
        int k3 = Math.min(j + k2, info.H);
        i = i > 0 ? i : 0;
        j = j > 0 ? j : 0;
        if(j3 - i <= 0 || k3 - j <= 0)
            return;
        int ai1[] = info.iOffs[iLayer];
        int l3 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        for(int l6 = j; l6 < k3; l6++)
        {
            int k = j2 * l6 + i;
            int j1 = i3;
            i3 += k2;
            for(int j7 = i; j7 < j3; j7++)
            {
                int l1;
                int j6;
                if((l1 = ai[j1++]) == 0 || isM(j6 = ai1[k++]))
                {
                    k++;
                } else
                {
                    j5 += j6 >>> 24;
                    k5 += j6 >>> 16 & 0xff;
                    l5 += j6 >>> 8 & 0xff;
                    i6 += j6 & 0xff;
                    l3++;
                }
            }

        }

        if(l3 == 0)
            return;
        j5 /= l3;
        k5 /= l3;
        l5 /= l3;
        i6 /= l3;
        if(iAlpha > 0)
        {
            float f1 = b255[iAlpha] / 3F;
            int k7 = iColor >>> 16 & 0xff;
            int i8 = iColor >>> 8 & 0xff;
            int j8 = iColor & 0xff;
            j5 = Math.max((int)((float)j5 + (float)(255 - j5) * f1), 1);
            int l = (int)((float)(k7 - k5) * f1);
            k5 += l == 0 ? ((int) (k7 <= k5 ? ((int) (k7 >= k5 ? 0 : -1)) : 1)) : l;
            l = (int)((float)(i8 - l5) * f1);
            l5 += l == 0 ? ((int) (i8 <= l5 ? ((int) (i8 >= l5 ? 0 : -1)) : 1)) : l;
            l = (int)((float)(j8 - i6) * f1);
            i6 += l == 0 ? ((int) (j8 <= i6 ? ((int) (j8 >= i6 ? 0 : -1)) : 1)) : l;
        }
        i3 = l2;
        for(int i7 = j; i7 < k3; i7++)
        {
            int i1 = j2 * i7 + i;
            int k1 = i3;
            i3 += k2;
            for(int l7 = i; l7 < j3; l7++)
            {
                int i2 = ai[k1++];
                int k6 = ai1[i1];
                float f;
                if(i2 == 0 || isM(k6) || (f = getTT(l7, i7) * b255[i2]) == 0.0F)
                {
                    i1++;
                } else
                {
                    int j4 = k6 >>> 24;
                    int k4 = k6 >>> 16 & 0xff;
                    int i5 = k6 >>> 8 & 0xff;
                    int l4 = k6 & 0xff;
                    int i4 = (int)((float)(j5 - j4) * f);
                    j4 += i4 == 0 ? ((int) (j5 <= j4 ? ((int) (j5 >= j4 ? 0 : -1)) : 1)) : i4;
                    i4 = (int)((float)(k5 - k4) * f);
                    k4 += i4 == 0 ? ((int) (k5 <= k4 ? ((int) (k5 >= k4 ? 0 : -1)) : 1)) : i4;
                    i4 = (int)((float)(l5 - i5) * f);
                    i5 += i4 == 0 ? ((int) (l5 <= i5 ? ((int) (l5 >= i5 ? 0 : -1)) : 1)) : i4;
                    i4 = (int)((float)(i6 - l4) * f);
                    l4 += i4 == 0 ? ((int) (i6 <= l4 ? ((int) (i6 >= l4 ? 0 : -1)) : 1)) : i4;
                    ai1[i1++] = (j4 << 24) + (k4 << 16) + (i5 << 8) + l4;
                }
            }

        }

    }

    public final void draw()
        throws InterruptedException
    {
        try
        {
            if(info == null)
                return;
            iSeek = 0;
            switch(iHint)
            {
            case 0: // '\0'
            case 1: // '\001'
                dStart();
                while(dNext()) ;
                break;

            case 10: // '\n'
                dClear(false);
                break;

            default:
                dRetouch(null);
                break;
            }
        }
        catch(InterruptedException _ex) { }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
        dEnd();
    }

    private void dRect(int i, int j, int k, int l)
    {
        int i1 = info.W;
        int j1 = info.H;
        byte abyte0[] = info.iMOffs;
        int ai[] = info.iOffs[iLayer];
        byte byte0 = (byte)iAlpha;
        if(i < 0)
            i = 0;
        if(j < 0)
            j = 0;
        if(k > i1)
            k = i1;
        if(l > j1)
            l = j1;
        if(i >= k || j >= l || byte0 == 0)
            return;
        setD(i, j, k, l);
label0:
        switch(iHint)
        {
        default:
            break;

        case 3: // '\003'
            for(int l2 = j; l2 < l; l2++)
            {
                int k1 = l2 * i1 + i;
                for(int j2 = i; j2 < k; j2++)
                {
                    if(!isM(ai[k1]))
                        abyte0[k1] = byte0;
                    k1++;
                }

            }

            break;

        case 4: // '\004'
            int i3 = i;
            int k3 = j;
            int i4 = k;
            int k4 = l;
            for(int i5 = 0; i5 < iSize + 1; i5++)
            {
                int l1 = i1 * k3 + i3;
                int i2 = i1 * (k4 - 1) + i3;
                for(int k2 = i3; k2 < i4; k2++)
                {
                    if(!isM(ai[l1]))
                        abyte0[l1] = byte0;
                    if(!isM(ai[i2]))
                        abyte0[i2] = byte0;
                    l1++;
                    i2++;
                }

                l1 = i1 * k3 + i3;
                i2 = (i1 * k3 + i4) - 1;
                for(int k5 = k3; k5 < k4; k5++)
                {
                    if(!isM(ai[l1]))
                        abyte0[l1] = byte0;
                    if(!isM(ai[i2]))
                        abyte0[i2] = byte0;
                    l1 += i1;
                    i2 += i1;
                }

                i3++;
                i4--;
                k3++;
                k4--;
                if(i4 <= i3 || k4 <= k3)
                    break label0;
            }

            break;

        case 5: // '\005'
        case 6: // '\006'
            int j3 = k - i - 1;
            int l3 = l - j - 1;
            int j5 = j3 / 2;
            int l5 = l3 / 2;
            for(float f = 0.0F; f < 8F; f = (float)((double)f + 0.001D))
            {
                int j4 = i + j5 + (int)Math.round(Math.cos(f) * (double)j5);
                int l4 = j + l5 + (int)Math.round(Math.sin(f) * (double)l5);
                abyte0[i1 * l4 + j4] = byte0;
            }

            if(iHint == 5 && j5 > 0 && l5 > 0)
                dFill(abyte0, i, j, k, l);
            break;
        }
        t();
    }

    public void dRetouch(int ai[])
        throws InterruptedException
    {
        dRetouch(ai, true);
    }

    public void dRetouch(int ai[], boolean flag)
        throws InterruptedException
    {
        try
        {
            boolean flag1 = ai != null;
            byte byte0 = 4;
            user.setup(this);
            setD(0, 0, 0, 0);
            int ai1[] = user.points;
            if(flag1)
            {
                if(flag)
                {
                    int i = info.scale;
                    int l = info.Q;
                    int k2 = info.scaleX;
                    int l2 = info.scaleY;
                    int i3 = iHint != 2 ? 0 : user.pW / 2;
                    for(int k3 = 0; k3 < 4; k3++)
                    {
                        int i1 = ai[k3] >> 16;
                        int k1 = (short)ai[k3];
                        i1 = (i1 / i + k2) * l - i3;
                        k1 = (k1 / i + l2) * l - i3;
                        ai1[k3] = i1 << 16 | k1 & 0xffff;
                    }

                } else
                {
                    System.arraycopy(ai, 0, ai1, 0, ai.length);
                }
            } else
            {
                int j = 0;
                while(iSeek < iOffset) 
                {
                    ai1[j++] = (r2() & 0xffff) << 16 | r2() & 0xffff;
                    if(iHint == 8)
                        break;
                }
            }
            int k = ai1[0] >> 16;
            short word0 = (short)ai1[0];
            switch(iHint)
            {
            case 2: // '\002'
                int j1 = user.wait;
                user.wait = -2;
                dStart(k, word0, 0, false, false);
                dBz(ai1);
                user.wait = j1;
                break;

            case 8: // '\b'
                String s1 = info.text;
                String s2 = info.textOption;
                if(!flag1)
                {
                    String s3 = new String(offset, iSeek, iOffset - iSeek, "UTF8");
                    int j3 = s3.indexOf('\0');
                    s1 = s3.substring(j3 + 1);
                    s2 = s3.substring(0, j3);
                }
                dText(s1, s2, k, word0);
                byte0 = 1;
                break;

            case 9: // '\t'
                dCopy(ai1);
                byte0 = 3;
                break;

            case 7: // '\007'
                dFill(k, word0);
                byte0 = 1;
                break;

            case 14: // '\016'
                int _tmp = info.W * info.H;
                switch(word0)
                {
                default:
                    break;

                case 0: // '\0'
                    ch(iLayerSrc, iLayer);
                    break;

                case 1: // '\001'
                    info.setL(ai1[1]);
                    break;

                case 2: // '\002'
                    info.delL(iLayerSrc);
                    break;

                case 3: // '\003'
                    if(iLayer > iLayerSrc)
                    {
                        for(int l1 = iLayerSrc; l1 < iLayer; l1++)
                            ch(l1, l1 + 1);

                    }
                    if(iLayer >= iLayerSrc)
                        break;
                    for(int i2 = iLayerSrc; i2 > iLayer; i2--)
                        ch(i2, i2 - 1);

                    break;

                case 4: // '\004'
                    int ai2[][] = info.getOffset();
                    System.arraycopy(ai2[iLayerSrc], 0, ai2[iLayer], 0, ai2[0].length);
                    break;

                case 5: // '\005'
                    info.visit[iLayerSrc] = (float)iAlpha / 255F;
                    break;
                }
                setD(0, 0, info.W, info.H);
                break;

            default:
                dRect(k, word0, ai1[1] >> 16, (short)ai1[1]);
                byte0 = 2;
                break;
            }
            if(flag1)
            {
                ByteStream bytestream = getWork();
                for(int j2 = 0; j2 < byte0; j2++)
                    bytestream.w(ai1[j2], 4);

                if(iHint == 8)
                    bytestream.write((info.textOption + '\0' + info.text).getBytes("UTF8"));
            }
            if(user.wait >= 0)
            {
                dFlush();
                dBuffer();
                setD(0, 0, 0, 0);
                Object _tmp1 = user.isDirect;
            } else
            {
                Object _tmp2 = user.isDirect;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private void dStart()
    {
      try
      {
        int i = r2();
        int j = r2();
        int tmp23_22 = i;
        this.user.oX = tmp23_22;
        int tmp27_23 = tmp23_22;
        this.user.pX = tmp27_23;
        this.user.fX = tmp27_23;
        int tmp48_47 = j;
        this.user.oY = tmp48_47;
        int tmp52_48 = tmp48_47;
        this.user.pY = tmp52_48;
        this.user.fY = tmp52_48;
        this.user.pY2 = -1000;
        this.user.pX2 = -1000;
        this.user.setup(this);
        if (this.iSOB != 0)
        {
          int k = ru();
          this.iSize = ss(k);
          this.iAlpha = sa(k);
        }
        setD(i, j, i + this.user.pW, j + this.user.pW);
      }
      catch (RuntimeException localRuntimeException)
      {
        localRuntimeException.printStackTrace();
      }
    }

    public void dStart(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
    {
      try
      {
    	this.user.setup(this);
        this.iSize = ss(paramInt3);
        this.iAlpha = sa(paramInt3);
        this.user.setup(this);
        if (paramBoolean2)
        {
          int i = this.info.scale;
          paramInt1 = (paramInt1 / i + this.info.scaleX) * this.info.Q;
          paramInt2 = (paramInt2 / i + this.info.scaleY) * this.info.Q;
        }
        if (paramBoolean1)
        {
          ByteStream localByteStream = getWork();
          localByteStream.w(paramInt1, 2);
          localByteStream.w(paramInt2, 2);
          if (this.iSOB != 0)
            localByteStream.write(paramInt3);
        }
        
        this.user.pY2 = -1000;
        this.user.pX2 = -1000;
        int tmp161_160 = paramInt1;
        this.user.oX = tmp161_160;
        int tmp165_161 = tmp161_160;
        this.user.pX = tmp165_161;
        this.user.fX = tmp165_161;
        int tmp186_185 = paramInt2;
        this.user.oY = tmp186_185;
        int tmp190_186 = tmp186_185;
        this.user.pY = tmp190_186;
        this.user.fY = tmp190_186;
        int j = this.user.pW / 2;
        setD(paramInt1 - j, paramInt2 - j, paramInt1 + j, paramInt2 + j);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }

    private void dText(String s1, String s2, int i, int j)
    {
        try
        {
            int k = info.W;
            int l = info.H;
            int ai[] = info.iOffs[iLayer];
            byte abyte0[] = info.iMOffs;
            float f = b255[iAlpha];
            if(f == 0.0F)
                return;
            Font font = new Font("sanssefit", 0, iSize);
            FontMetrics fontmetrics = info.component.getFontMetrics(font);
            if(s1 == null || s1.length() <= 0)
                return;
            int i1 = fontmetrics.stringWidth(s1) + s1.length();
            int j1 = fontmetrics.getMaxAscent() + fontmetrics.getMaxDescent() + 2;
            int k1 = j1 - fontmetrics.getMaxDescent();
            setD(i, j, i + i1, j + j1);
            Image image = info.component.createImage(i1, j1);
            Graphics g = image.getGraphics();
            g.setFont(font);
            g.setColor(Color.black);
            g.fillRect(0, 0, i1, j1);
            g.setColor(Color.blue);
            g.drawString(s1, fontmetrics.getLeading(), k1);
            g.dispose();
            g = null;
            font = null;
            fontmetrics = null;
            PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, i1, j1, true);
            pixelgrabber.grabPixels();
            int ai1[] = (int[])pixelgrabber.getPixels();
            pixelgrabber = null;
            image.flush();
            image = null;
            boolean flag = false;
            int j2 = Math.min(k - i, i1);
            int k2 = Math.min(l - j, j1);
            for(int l2 = 0; l2 < k2; l2++)
            {
                int l1 = l2 * i1;
                int i2 = (l2 + j) * k + i;
                for(int i3 = 0; i3 < j2; i3++)
                {
                    if(!isM(ai[i2]))
                        abyte0[i2] = (byte)(int)((float)(ai1[l1] & 0xff) * f);
                    l1++;
                    i2++;
                }

            }

            setD(i, j, i + i1, j + j1);
            t();
        }
        catch(InterruptedException _ex) { }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
        }
    }

    public final void get(OutputStream outputstream, ByteStream bytestream, Mg mg)
    {
        try
        {
            bytestream.reset();
            int i = 0;
            boolean flag = false;
            int j = getFlag(mg);
            int k = j >>> 8 & 0xff;
            int l = j & 0xff;
            bytestream.write(j >>> 16);
            bytestream.write(k);
            bytestream.write(l);
            if((k & 1) != 0)
            {
                i = iHint;
                flag = true;
            }
            if((k & 2) != 0)
            {
                if(flag)
                    bytestream.write(i << 4 | iPenM);
                else
                    i = iPenM;
                flag = !flag;
            }
            if((k & 4) != 0)
            {
                if(flag)
                    bytestream.write(i << 4 | iMask);
                else
                    i = iMask;
                flag = !flag;
            }
            if(flag)
                bytestream.write(i << 4);
            if((k & 8) != 0)
                bytestream.write(iPen);
            if((k & 0x10) != 0)
                bytestream.write(iTT);
            if((k & 0x20) != 0)
                bytestream.write(iLayer);
            if((k & 0x40) != 0)
                bytestream.write(iLayerSrc);
            if((l & 1) != 0)
                bytestream.write(iAlpha);
            if((l & 2) != 0)
                bytestream.w(iColor, 3);
            if((l & 4) != 0)
                bytestream.w(iColorMask, 3);
            if((l & 8) != 0)
                bytestream.write(iSize);
            if((l & 0x10) != 0)
                bytestream.write(iCount);
            if((l & 0x20) != 0)
                bytestream.w(iSA, 2);
            if((l & 0x40) != 0)
                bytestream.w(iSS, 2);
            if(iPen == 20)
                bytestream.w2(iAlpha2);
            if(offset != null && iOffset > 0)
                bytestream.write(offset, 0, iOffset);
            outputstream.write(bytestream.size() >>> 8);
            outputstream.write(bytestream.size() & 0xff);
            bytestream.writeTo(outputstream);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
        }
    }

    private final int getFlag(Mg mg)
    {
        int j = 0;
        if(isAllL)
            j |= 1;
        if(isAFix)
            j |= 2;
        if(isAnti)
            j |= 0x10;
        if(isCount)
            j |= 8;
        if(isOver)
            j |= 4;
        j |= iSOB << 6;
        int i = j << 16;
        if(mg == null)
            return i | 0xffff;
        j = 0;
        if(iHint != mg.iHint)
            j |= 1;
        if(iPenM != mg.iPenM)
            j |= 2;
        if(iMask != mg.iMask)
            j |= 4;
        if(iPen != mg.iPen)
            j |= 8;
        if(iTT != mg.iTT)
            j |= 0x10;
        if(iLayer != mg.iLayer)
            j |= 0x20;
        if(iLayerSrc != mg.iLayerSrc)
            j |= 0x40;
        i |= j << 8;
        j = 0;
        if(iAlpha != mg.iAlpha)
            j |= 1;
        if(iColor != mg.iColor)
            j |= 2;
        if(iColorMask != mg.iColorMask)
            j |= 4;
        if(iSize != mg.iSize)
            j |= 8;
        if(iCount != mg.iCount)
            j |= 0x10;
        if(iSA != mg.iSA)
            j |= 0x20;
        if(iSS != mg.iSS)
            j |= 0x40;
        return i | j;
    }

    public Image getImage(int i, int j, int k, int l, int i1)
    {
        j = Math.round(j / info.scale) + info.scaleX;
        k = Math.round(k / info.scale) + info.scaleY;
        l /= info.scale;
        i1 /= info.scale;
        int j1 = info.Q;
        if(j1 <= 1)
        {
            return info.component.createImage(new MemoryImageSource(l, i1, info.iOffs[i], k * info.W + j, info.W));
        } else
        {
            Image image = info.component.createImage(new MemoryImageSource(l * j1, i1 * j1, info.iOffs[i], k * j1 * info.W + j * j1, info.W));
            Image image1 = image.getScaledInstance(l, i1, 2);
            image.flush();
            return image1;
        }
    }

    private final int getM(int i, int j, int k)
    {
        if(j == 0)
            return i;
        float f = b255[j];
        int l = i >>> 24;
        int j1 = i >>> 16 & 0xff;
        int k1 = i >>> 8 & 0xff;
        int l1 = i & 0xff;
        int k2 = iColor;
        switch(iPen)
        {
        default:
            int i2 = isAFix ? l : l + (int)((float)(255 - l) * f);
            f = b255[Math.min((int)((float)j * (255F / (float)i2)), 255)];
            return (i2 << 24) + (j1 + (int)((float)((k2 >>> 16 & 0xff) - j1) * f) << 16) + (k1 + (int)((float)((k2 >>> 8 & 0xff) - k1) * f) << 8) + (l1 + (int)((float)((k2 & 0xff) - l1) * f));

        case 4: // '\004'
        case 5: // '\005'
            int j2 = l - (int)((float)l * f);
            return j2 != 0 ? j2 << 24 | i & 0xffffff : 0xffffff;

        case 6: // '\006'
            return (l << 24) + (Math.min(j1 + (int)((float)j1 * f), 255) << 16) + (Math.min(k1 + (int)((float)k1 * f), 255) << 8) + Math.min(l1 + (int)((float)l1 * f), 255);

        case 7: // '\007'
            return (l << 24) + (Math.max(j1 - (int)((float)(255 - j1) * f), 0) << 16) + (Math.max(k1 - (int)((float)(255 - k1) * f), 0) << 8) + Math.max(l1 - (int)((float)(255 - l1) * f), 0);

        case 8: // '\b'
            int ai[] = user.argb;
            int ai1[] = info.iOffs[iLayer];
            int k3 = info.W;
            for(int l3 = 0; l3 < 4; l3++)
                ai[l3] = 0;

            int l2 = k % k3;
            k += l2 != 0 ? l2 != k3 - 1 ? 0 : -1 : 1;
            k += k >= k3 ? k <= k3 * (info.H - 1) ? 0 : -k3 : k3;
            for(int i4 = -1; i4 < 2; i4++)
            {
                for(int k4 = -1; k4 < 2; k4++)
                {
                    int i3 = ai1[k + k4 + i4 * k3];
                    int i1 = i3 >>> 24;
                    for(int l4 = 0; l4 < 4; l4++)
                        ai[l4] += i3 >>> (l4 << 3) & 0xff;

                }

            }

            for(int j4 = 0; j4 < 4; j4++)
            {
                int j3 = i >>> (j4 << 3) & 0xff;
                ai[j4] = j3 + (int)((float)(ai[j4] / 9 - j3) * f);
            }

            return ai[3] << 24 | ai[2] << 16 | ai[1] << 8 | ai[0];

        case 9: // '\t'
        case 20: // '\024'
            if(j == 0)
                return i;
            else
                return j << 24 | 0xff0000;

        case 10: // '\n'
            return j << 24 | k2;
        }
    }

    public final byte[] getOffset()
    {
        return offset;
    }

    private final int[] getPM()
    {
        if(iHint == 8)
            return null;
        int ai[] = user.p;
        if(user.pM != iPenM || user.pA != iAlpha || user.pS != iSize)
        {
            int ai1[][] = info.bPen[iPenM];
            int ai2[] = ai1[iSize];
            int i = ai2.length;
            if(ai == null || ai.length < i)
                ai = new int[i];
            float f = b255[iAlpha];
            for(int j = 0; j < i; j++)
                ai[j] = (int)((float)ai2[j] * f);

            Object _tmp = user.pW;
            Object _tmp1 = user.pM;
            Object _tmp2 = user.pA;
            Object _tmp3 = user.pS;
            Object _tmp4 = user.p;
            Object _tmp5 = user.countMax;
            Object _tmp6 = user.count;
        }
        return ai;
    }

    private final float getTT(int i, int j)
    {
        if(iTT == 0)
            return 1.0F;
        if(iTT < 12)
        {
            return (float)(isTone(iTT - 1, i, j) ? 0 : 1);
        } else
        {
            int k = user.pTTW;
            return user.pTT[(j % k) * k + i % k];
        }
    }

    private final ByteStream getWork()
    {
        info.workOut.reset();
        return info.workOut;
    }

    private final boolean isM(int i)
    {
        if(iMask == 0)
        {
            return false;
        } else
        {
            i &= 0xffffff;
            return iMask != 1 ? iMask != 2 ? false : iColorMask != i : iColorMask == i;
        }
    }

    public static final boolean isTone(int i, int j, int k)
    {
        switch(i)
        {
        default:
            break;

        case 10: // '\n'
            if((j + 3) % 4 == 0 && (k + 2) % 4 == 0)
                return true;
            break;

        case 9: // '\t'
            if((j + 1) % 4 == 0 && (k + 2) % 4 == 0)
                break;
            // fall through

        case 8: // '\b'
            if(j % 2 != 0 && (k + 1) % 2 != 0)
                return true;
            break;

        case 7: // '\007'
            if((j + 2) % 4 == 0 && (k + 3) % 4 == 0)
                break;
            // fall through

        case 6: // '\006'
            if(j % 4 == 0 && (k + 1) % 4 == 0)
                break;
            // fall through

        case 5: // '\005'
            if((j + 1) % 2 != (k + 1) % 2)
                return true;
            break;

        case 4: // '\004'
            if((j + 1) % 4 == 0 && (k + 3) % 4 == 0)
                break;
            // fall through

        case 3: // '\003'
            if(j % 2 != 0 || k % 2 != 0)
                return true;
            break;

        case 2: // '\002'
            if((j + 2) % 4 == 0 && (k + 4) % 4 == 0)
                break;
            // fall through

        case 1: // '\001'
            if((j + 2) % 4 == 0 && (k + 2) % 4 == 0)
                break;
            // fall through

        case 0: // '\0'
            if(j % 4 != 0 || k % 4 != 0)
                return true;
            break;
        }
        return false;
    }

    private int[] loadIm(Object obj, boolean flag)
    {
        try
        {
            Component component = info.component;
            Image image = component.getToolkit().createImage((byte[])info.cnf.getRes(obj));
            info.cnf.remove(obj);
            Awt.wait(image);
            PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, image.getWidth(null), image.getHeight(null), true);
            pixelgrabber.grabPixels();
            int ai[] = (int[])pixelgrabber.getPixels();
            int i = ai.length;
            image.flush();
            image = null;
            if(flag)
            {
                for(int j = 0; j < i; j++)
                    ai[j] = ai[j] & 0xff ^ 0xff;

            } else
            {
                for(int k = 0; k < i; k++)
                    ai[k] &= 0xff;

            }
            return ai;
        }
        catch(RuntimeException _ex) { }
        catch(InterruptedException _ex) { }
        return null;
    }

    public final void m_paint(int i, int j, int k, int l)
    {
        int i1 = info.scale;
        int j1 = info.Q;
        i = (i / i1 + info.scaleX) * j1;
        j = (j / i1 + info.scaleY) * j1;
        k = (k / i1) * j1;
        l = (l / i1) * j1;
        dBuffer(false, i, j, i + k, j + l);
    }

    public final void memset(float af[], float f)
    {
        int i = af.length;
        for(int j = 0; j < i; j++)
            af[j] = f;

    }

    public final void memset(int ai[], int i)
    {
        int j = ai.length;
        for(int k = 0; k < j; k++)
            ai[k] = i;

    }

    public final Image mkLPic(int ai[], int i, int j, int k, int l, int i1)
    {
        i *= i1;
        j *= i1;
        k *= i1;
        l *= i1;
        int k1 = info.L;
        int _tmp = i1 * i1;
        float af[] = info.visit;
        int k2 = 0;
        int i3 = j + l;
        int j3 = info.W;
        int ai1[][] = info.iOffs;
        int _tmp1 = j3 * info.H;
        boolean flag = ai == null;
        float[] _tmp2 = b255;
        if(flag)
            ai = user.buffer;
        for(; j < i3; j++)
        {
            int j2 = j3 * j + i;
            for(int l2 = j2 + k; j2 < l2; j2++)
            {
                int i2 = 0xffffff;
                for(int j1 = 0; j1 < k1; j1++)
                {
                    int l1 = ai1[j1][j2];
                    float f = b255[l1 >>> 24] * af[j1];
                    i2 = f != 1.0F ? f != 0.0F ? ((i2 & 0xff0000) + (int)((float)((l1 & 0xff0000) - (i2 & 0xff0000)) * f) & 0xff0000) + ((i2 & 0xff00) + (int)((float)((l1 & 0xff00) - (i2 & 0xff00)) * f) & 0xff00) + ((i2 & 0xff) + (int)((float)((l1 & 0xff) - (i2 & 0xff)) * f)) : i2 : l1;
                }

                ai[k2++] = i2;
            }

        }

        if(flag)
            user.raster.newPixels(user.image, k, l, i1);
        else
            user.raster.scale(ai, k, l, i1);
        return user.image;
    }

    private final Image mkMPic(int i, int j, int k, int l, int i1)
    {
        i *= i1;
        j *= i1;
        k *= i1;
        l *= i1;
        int k1 = info.L;
        int l1 = iLayer;
        int _tmp = i1 * i1;
        float af[] = info.visit;
        int i3 = 0;
        int k3 = j + l;
        int l3 = info.W;
        int ai[][] = info.iOffs;
        int _tmp1 = l3 * info.H;
        int ai1[] = user.buffer;
        float[] _tmp2 = b255;
        byte abyte0[] = info.iMOffs;
        for(; j < k3; j++)
        {
            int l2 = l3 * j + i;
            for(int j3 = l2 + k; l2 < j3; l2++)
            {
                int k2 = abyte0[l2] & 0xff;
                int j2 = 0xffffff;
                for(int j1 = 0; j1 < k1; j1++)
                {
                    int i2 = j1 == l1 ? getM(ai[j1][l2], k2, l2) : ai[j1][l2];
                    float f = b255[i2 >>> 24] * af[j1];
                    j2 = f != 1.0F ? f != 0.0F ? ((j2 & 0xff0000) + (int)((float)((i2 & 0xff0000) - (j2 & 0xff0000)) * f) & 0xff0000) + ((j2 & 0xff00) + (int)((float)((i2 & 0xff00) - (j2 & 0xff00)) * f) & 0xff00) + ((j2 & 0xff) + (int)((float)((i2 & 0xff) - (j2 & 0xff)) * f)) : j2 : i2;
                }

                ai1[i3++] = j2;
            }

        }

        user.raster.newPixels(user.image, k, l, i1);
        return user.image;
    }

    public Info newInfo(Applet applet, Component component, Res res)
    {
        if(info != null)
            return info;
        info = new Info();
        Object _tmp = info.cnf;
        Object _tmp1 = info.component;
        Info info1 = info;
        Mg mg = info.m;
        for(int i = 1; i < 256; i++)
            b255[i] = (float)i / 255F;

        b255[0] = 0.0F;
        int ai[][][] = info.bPen;
        boolean flag = false;
        int j3 = 1;
        char c = '\377';
        mg.iAlpha = 255;
        for(int k3 = 0; k3 < 2; k3++)
        {
            set(mg);
            int ai1[][] = new int[23][];
            for(int j = 0; j < 23; j++)
            {
                int ai2[];
                ai1[j] = ai2 = new int[j3 * j3];
                if(j3 <= 7)
                {
                    int k1 = j3 * j3;
                    for(int l = 0; l < k1; l++)
                        ai2[l] = c;

                    if(j3 >= 3)
                        ai2[0] = ai2[j3 - 1] = ai2[j3 * (j3 - 1)] = ai2[k1 - 1] = 0;
                } else
                {
                    int i3 = j3 / 2;
                    int l2 = (int)(6.2831853071795862D * (double)i3);
                    int l1 = l2 * 4;
                    for(int i1 = 0; i1 < l1; i1++)
                    {
                        int j2 = Math.min(i3 + (int)Math.round((double)i3 * Math.cos(i1)), j3 - 1);
                        int k2 = Math.min(i3 + (int)Math.round((double)i3 * Math.sin(i1)), j3 - 1);
                        ai2[k2 * j3 + j2] = c;
                    }

                    info1.W = info1.H = j3;
                    dFill(ai2, 0, 0, j3, j3);
                }
                j3 += j >= 6 ? j >= 18 ? 4 : 2 : 1;
            }

            ai[k3] = ai1;
            mg.iAlpha = 110;
            c = 'P';
            j3 = 1;
        }

        set(((Mg) (null)));
        mg.set(((Mg) (null)));
        if(res != null)
        {
            for(int k = 0; k < 16; k++)
            {
                int i2;
                for(i2 = 0; res.get("pm" + k + '/' + i2 + ".gif") != null; i2++);
                if(i2 > 0)
                {
                    ai[k] = new int[i2][];
                    for(int j1 = 0; j1 < i2; j1++)
                        ai[k][j1] = loadIm("pm" + k + '/' + j1 + ".gif", true);

                }
            }

            info.bTT = new float[res.getP("tt_size", 31)][];
        }
        String s1 = applet.getParameter("tt.zip");
        if(s1 != null && s1.length() > 0)
            info.dirTT = s1;
        return info;
    }

    public User newUser(Component component)
    {
        if(user == null)
        {
            user = new User();
            if(color_model == null)
                color_model = new DirectColorModel(24, 0xff0000, 65280, 255);
            Object _tmp = user.raster;
            Object _tmp1 = user.image;
        }
        return user;
    }

    public final int pix(int i, int j)
    {
        if(!isAllL)
            return info.iOffs[iLayer][j * info.W + i];
        int k = info.L;
        int i1 = 0;
        int k1 = 0xffffff;
        int i2 = info.W * j + i;
        for(int j2 = 0; j2 < k; j2++)
        {
            int l1 = info.iOffs[j2][i2];
            float f = b255[l1 >>> 24];
            if(f != 0.0F)
            {
                if(f == 1.0F)
                {
                    k1 = l1;
                    i1 = 255;
                }
                i1 = (int)((float)i1 + (float)(255 - i1) * f);
                int l = 0;
                for(int k2 = 16; k2 >= 0; k2 -= 8)
                {
                    int j1 = k1 >>> k2 & 0xff;
                    l |= j1 + (int)((float)((l1 >>> k2 & 0xff) - j1) * f) << k2;
                }

                k1 = l;
            }
        }

        return i1 << 24 | k1;
    }

    private final byte r()
    {
        return offset[iSeek++];
    }

    private final int r(byte abyte0[], int i, int j)
    {
        int k = 0;
        for(int l = j - 1; l >= 0; l--)
            k |= (abyte0[i++] & 0xff) << l * 8;

        return k;
    }

    private final short r2()
    {
        return (short)((ru() << 8) + ru());
    }

    public void reset()
    {
        byte abyte0[] = info.iMOffs;
        int j = info.W;
        int k = Math.max(user.dX, 0);
        int l = Math.max(user.dY, 0);
        int i1 = Math.min(user.dW, j);
        int j1 = Math.min(user.dH, info.H);
        for(int l1 = l; l1 < j1; l1++)
        {
            int i = k + l1 * j;
            for(int k1 = k; k1 < i1; k1++)
                abyte0[i++] = 0;

        }

        dBuffer(false, k, l, i1, j1);
        setD(0, 0, 0, 0);
    }

    private final int rPo()
    {
        byte byte0 = r();
        return byte0 == -128 ? r2() : byte0;
    }

    private final int ru()
    {
        return r() & 0xff;
    }

    private final int s(int i, int j, int k)
    {
        byte abyte0[] = info.iMOffs;
        int l = info.W - 1;
        for(int i1 = (l + 1) * k + j; j < l && pix(j + 1, k) == i && abyte0[i1 + 1] == 0; j++)
            i1++;

        return j;
    }

    private final int sa(int i)
    {
        if((iSOB & 1) == 0)
        {
            return iAlpha;
        } else
        {
            int j = iSA & 0xff;
            return j + (int)(b255[(iSA >>> 8) - j] * (float)i);
        }
    }

    public final int set(byte abyte0[], int i)
    {
        int j = (abyte0[i++] & 0xff) << 8 | abyte0[i++] & 0xff;
        int k = i;
        if(j <= 2)
            return j + 2;
        try
        {
            int l = 0;
            boolean flag = false;
            int i1 = abyte0[i++] & 0xff;
            int j1 = abyte0[i++] & 0xff;
            int k1 = abyte0[i++] & 0xff;
            isAllL = (i1 & 1) != 0;
            isAFix = (i1 & 2) != 0;
            isOver = (i1 & 4) != 0;
            isCount = (i1 & 8) != 0;
            isAnti = (i1 & 0x10) != 0;
            iSOB = i1 >>> 6;
            if((j1 & 1) != 0)
            {
                l = abyte0[i++] & 0xff;
                flag = true;
                iHint = l >>> 4;
            }
            if((j1 & 2) != 0)
            {
                if(!flag)
                {
                    l = abyte0[i++] & 0xff;
                    iPenM = l >>> 4;
                } else
                {
                    iPenM = l & 0xf;
                }
                flag = !flag;
            }
            if((j1 & 4) != 0)
            {
                if(!flag)
                {
                    l = abyte0[i++] & 0xff;
                    iMask = l >>> 4;
                } else
                {
                    iMask = l & 0xf;
                }
                flag = !flag;
            }
            if((j1 & 8) != 0)
                iPen = abyte0[i++] & 0xff;
            if((j1 & 0x10) != 0)
                iTT = abyte0[i++] & 0xff;
            if((j1 & 0x20) != 0)
                iLayer = abyte0[i++] & 0xff;
            if((j1 & 0x40) != 0)
                iLayerSrc = abyte0[i++] & 0xff;
            if((k1 & 1) != 0)
                iAlpha = abyte0[i++] & 0xff;
            if((k1 & 2) != 0)
            {
                iColor = r(abyte0, i, 3);
                i += 3;
            }
            if((k1 & 4) != 0)
            {
                iColorMask = r(abyte0, i, 3);
                i += 3;
            }
            if((k1 & 8) != 0)
                iSize = abyte0[i++] & 0xff;
            if((k1 & 0x10) != 0)
                iCount = abyte0[i++];
            if((k1 & 0x20) != 0)
            {
                iSA = r(abyte0, i, 2);
                i += 2;
            }
            if((k1 & 0x40) != 0)
            {
                iSS = r(abyte0, i, 2);
                i += 2;
            }
            if(iPen == 20)
            {
                iAlpha2 = r(abyte0, i, 2);
                i += 2;
            }
            k = j - (i - k);
            if(k > 0)
            {
                if(offset == null || offset.length < k)
                    offset = new byte[k];
                iOffset = k;
                System.arraycopy(abyte0, i, offset, 0, k);
            } else
            {
                iOffset = 0;
            }
        }
        catch(RuntimeException runtimeexception)
        {
            runtimeexception.printStackTrace();
            iOffset = 0;
        }
        return j + 2;
    }

    public final void set(String s1)
    {
        try
        {
            if(s1 == null || s1.length() == 0)
                return;
            Field afield[] = getClass().getDeclaredFields();
            int i = s1.indexOf('@');
            if(i < 0)
                i = s1.length();
            int k;
            for(int j = 0; j < i; j = k + 1)
            {
                k = s1.indexOf('=', j);
                if(k == -1)
                    break;
                String s2 = s1.substring(j, k);
                j = k + 1;
                k = s1.indexOf(';', j);
                if(k < 0)
                    k = i;
                try
                {
                    for(int l = 0; l < afield.length; l++)
                    {
                        Field field = afield[l];
                        if(!field.getName().equals(s2))
                            continue;
                        String s3 = s1.substring(j, k);
                        Class class1 = field.getType();
                        if(class1.equals(Integer.TYPE))
                            field.setInt(this, Integer.parseInt(s3));
                        else
                        if(class1.equals(Boolean.TYPE))
                            field.setBoolean(this, Integer.parseInt(s3) != 0);
                        else
                            field.set(this, s3);
                        break;
                    }

                }
                catch(NumberFormatException _ex) { }
                catch(IllegalAccessException _ex) { }
            }

            if(i != s1.length())
            {
                ByteStream bytestream = getWork();
                for(int i1 = i + 1; i1 < s1.length(); i1 += 2)
                    bytestream.write(Character.digit(s1.charAt(i1), 16) << 4 | Character.digit(s1.charAt(i1 + 1), 16));

                offset = bytestream.toByteArray();
                iOffset = offset.length;
            }
        }
        catch(Throwable _ex) { }
    }

    public final void set(Mg mg)
    {
        if(mg == null)
            mg = mgDef;
        iHint = mg.iHint;
        iPen = mg.iPen;
        iPenM = mg.iPenM;
        iTT = mg.iTT;
        iMask = mg.iMask;
        iSize = mg.iSize;
        iSS = mg.iSS;
        iCount = mg.iCount;
        isOver = mg.isOver;
        isCount = mg.isCount;
        isAFix = mg.isAFix;
        isAnti = mg.isAnti;
        isAllL = mg.isAllL;
        iAlpha = mg.iAlpha;
        iAlpha2 = mg.iAlpha2;
        iSA = mg.iSA;
        iColor = mg.iColor;
        iColorMask = mg.iColorMask;
        iLayer = mg.iLayer;
        iLayerSrc = mg.iLayerSrc;
        iSOB = mg.iSOB;
        iOffset = 0;
    }

    private final void setD(int i, int j, int k, int l)
    {
        Object _tmp = user.dX;
        Object _tmp1 = user.dY;
        Object _tmp2 = user.dW;
        Object _tmp3 = user.dH;
    }

    public void setInfo(Info info1)
    {
        info = info1;
    }

    public void setUser(User user1)
    {
        user = user1;
    }

    private final int ss(int i)
    {
        if((iSOB & 2) == 0)
        {
            return iSize;
        } else
        {
            int j = iSS & 0xff;
            return (int)(((float)j + b255[(iSS >>> 8) - j] * (float)i) * user.pV);
        }
    }

    private final void t()
    {
        if(iTT == 0)
            return;
        byte abyte0[] = info.iMOffs;
        int k = info.W;
        int l = user.dX;
        int i1 = user.dY;
        int j1 = user.dW;
        int k1 = user.dH;
        for(int l1 = i1; l1 < k1; l1++)
        {
            int j = k * l1 + l;
            for(int i = l; i < j1; i++)
                abyte0[j] = (byte)(int)((float)(abyte0[j++] & 0xff) * getTT(i, l1));

        }

    }

    private final void wPo(int i)
        throws IOException
    {
        ByteStream bytestream = info.workOut;
        if(i > 127 || i < -127)
        {
            bytestream.write(-128);
            bytestream.w(i, 2);
        } else
        {
            bytestream.write(i);
        }
    }

    private Info info;
    private User user;
    public int iHint;
    public int iPen;
    public int iPenM;
    public int iTT;
    public int iColor;
    public int iColorMask;
    public int iAlpha;
    public int iAlpha2;
    public int iSA;
    public int iLayer;
    public int iLayerSrc;
    public int iMask;
    public int iSize;
    public int iSS;
    public int iCount;
    public int iSOB;
    public boolean isAFix;
    public boolean isOver;
    public boolean isCount;
    public boolean isAnti;
    public boolean isAllL;
    private int iSeek;
    private int iOffset;
    private byte offset[];
    public static final int H_FLINE = 0;
    public static final int H_LINE = 1;
    public static final int H_BEZI = 2;
    public static final int H_RECT = 3;
    public static final int H_FRECT = 4;
    public static final int H_OVAL = 5;
    public static final int H_FOVAL = 6;
    public static final int H_FILL = 7;
    public static final int H_TEXT = 8;
    public static final int H_COPY = 9;
    public static final int H_CLEAR = 10;
    public static final int H_L = 14;
    public static final int P_SOLID = 0;
    public static final int P_PEN = 1;
    public static final int P_SUISAI = 2;
    public static final int P_SUISAI2 = 3;
    public static final int P_WHITE = 4;
    public static final int P_SWHITE = 5;
    public static final int P_LIGHT = 6;
    public static final int P_DARK = 7;
    public static final int P_BOKASHI = 8;
    public static final int P_MOSAIC = 9;
    public static final int P_FILL = 10;
    public static final int P_LR = 17;
    public static final int P_UD = 18;
    public static final int P_R = 19;
    public static final int P_FUSION = 20;
    public static final int PM_PEN = 0;
    public static final int PM_SUISAI = 1;
    public static final int PM_SOLID = 2;
    public static final int PM_KUREYON = 3;
    public static final int M_N = 0;
    public static final int M_M = 1;
    public static final int M_R = 2;
    public static final int M_ADD = 3;
    public static final int M_SUB = 4;
    private static final int F1O = 4;
    private static final int F1C = 8;
    private static final int F1A = 16;
    private static final int F1S = 32;
    private static final int F2H = 1;
    private static final int F2PM = 2;
    private static final int F2M = 4;
    private static final int F2P = 8;
    private static final int F2T = 16;
    private static final int F2L = 32;
    private static final int F2LS = 64;
    private static final int F3A = 1;
    private static final int F3C = 2;
    private static final int F3CM = 4;
    private static final int F3S = 8;
    private static final int F3E = 16;
    private static final int F3SA = 32;
    private static final int F3SS = 64;
    private static final int DEF_COUNT = -8;
    private static final String ENCODE = "UTF8";
    private static float b255[] = new float[256];
    private static ColorModel color_model = null;
    private static final Mg mgDef = new Mg();





}
