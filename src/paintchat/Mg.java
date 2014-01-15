// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/26/2009 3:48:46 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Mg.java

package paintchat;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;

import nanoxml.XMLElement;
import paintchat.saistyle.BrushFactory;
import paintchat.saistyle.SaiUtil;
import paintchat.saistyle.ToolTypeListener;
import syi.awt.Awt;
import syi.util.ByteStream;

// Referenced classes of package paintchat:
//            Res, SRaster

public class Mg {
	public class User {

		private void setup(Mg mg) {
			pV = Mg.b255[mg.info.bPen[mg.iPenM].length - 1];
			mg.getPM();
			count = 0;
			isF2 = false;
			isDirect = mg.iPen == P_SUISAI2 || mg.iHint == H_COPY || mg.isOver;
			if (mg.iTT >= 12) {
				pTT = info.getTT(mg.iTT);
				pTTW = (int) Math.sqrt(pTT.length);
			}
		}

		public void setIm(Mg mg) {
			if (mg.iHint == 8)
				return;
			if (pM != mg.iPenM || pA != mg.iAlpha || pS != mg.iSize) {
				int ai[] = mg.info.bPen[mg.iPenM][mg.iSize];
				int i = ai.length;
				if (p == null || p.length < i)
					p = new int[i];
				float f = Mg.b255[mg.iAlpha];
				for (int j = 0; j < i; j++) {
					float f1 = (float) ai[j] * f;
					p[j] = f1 > 1.0F || f1 <= 0.0F ? (int) f1 : 1;
				}

				pW = (int) Math.sqrt(i);
				pM = mg.iPenM;
				pA = mg.iAlpha;
				pS = mg.iSize;
			}
		}

		public int getPixel(int i, int j) {
			int k = info.imW;
			if (i < 0 || j < 0 || i >= k || j >= info.imH) {
				return 0;
			}
			else {
				mkLPic(buffer, i, j, 1, 1, info.Q);
				return info.iOffs[info.m.iLayer][k * j + i] & 0xff000000 | buffer[0];
			}
		}

		public int[] getBuffer() {
			return buffer;
		}

		public long getRect() {
			return (long) dX << 48 | (long) dY << 32 | (long) (dW << 16) | (long) dH;
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
		/** first x */
		private float fX;
		private float fY;
		/** is first */
		private boolean isF2;
		private int dX;
		private int dY;
		private int dW;
		private int dH;
		private int count;
		private int countMax;

		public User() {
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

	public class Info {

		/**
		 * Set canvas size bytes i believe
		 * @param x
		 * @param y
		 * @param sensitivity
		 * @throws InterruptedException
		 */
		public void setSize(int i, int j, int k) {
			if (i * k != W || j * k != H)
				iOffs = null;
			imW = i;
			imH = j;
			W = i * k;
			H = j * k;
			Q = k;
			int l = W * H;
			if (iMOffs == null || iMOffs.length < l)
				iMOffs = new byte[l];
		}

		public void setIOffs(int ai[][]) {
			L = ai.length;
			iOffs = ai;
			if (visit == null || visit.length != L)
				memset(visit = new float[L], 1.0F);
		}

		public void setComponent(Component component1, Graphics g1, int i, int j) {
			component = component1;
			vWidth = i;
			vHeight = j;
			g = g1;
		}
		
		private final int toNativeX(int x, int q, int scale) {			
			return ((x/scale + info.scaleX) * q);			
		}
		
		private final int toNativeY(int y, int q, int scale) {
			return ((y/scale + info.scaleY) * q);
		}
		
		private final int toScaledX(int nativeX) {
			return ((nativeX / info.Q) - info.scaleX) * info.scale;
		}
		
		private final int toScaledY(int nativeY) {
			return ((nativeY / info.Q) - info.scaleY) * info.scale;
		}
		
		public final int translateX(int x, int oldQ, int oldScale) {
			return toScaledX(toNativeX(x, oldQ, oldScale));
		}
		
		public final int translateY(int y, int oldQ, int oldScale) {
			return toScaledY(toNativeY(y, oldQ, oldScale));
		}		
		
		public final int translateSize(int len, int oldQ, int oldScale) {
			return (len * oldQ * info.scale) / (oldScale * info.Q);			
		}
		
		public final void translatePoint(Point scaledPoint, int q, int scale) {
			scaledPoint.setLocation(translateX(scaledPoint.x, q, scale), translateY(scaledPoint.y, q, scale));			
		}		
				
		public final void translateRect(Rectangle scaledRect, int q, int scale) {
			scaledRect.setBounds(translateX(scaledRect.x, q, scale), translateY(scaledRect.y, q, scale), 
					translateSize(scaledRect.width, q, scale),	
					translateSize(scaledRect.height, q, scale));
		}				
		
		
		public int getToolType() {
			return toolType;	
		}
		
		public void setToolType(int type, Object source) {
			toolType = type;
			if(toolTypeListeners != null) {
				for(ToolTypeListener listener: toolTypeListeners) {
					if(listener != source) {
						listener.toolTypeChanged(this, source);
					}
				}
			}
		}
		
		public void addToolTypeListener(ToolTypeListener listener) {
			if(toolTypeListeners == null) {
				toolTypeListeners = new LinkedList<ToolTypeListener>();
			}
			toolTypeListeners.add(listener);
		}

		public void setL(int i) {
			int j = iOffs != null ? iOffs.length : 0;
			Math.min(j, i);
			if (j != i) {
				float af[] = new float[i];
				int ai[][] = new int[i][];
				int k = W * H;
				for (int l = 0; l < i; l++)
					if (l >= j) {
						af[l] = 1.0F;
						memset(ai[l] = new int[k], 0xffffff);	// TODO Out of Memory exception idling pchat
					}
					else {
						ai[l] = iOffs[l];
						af[l] = visit[l];
					}

				visit = af;
				iOffs = ai;
			}
			L = i;
		}

		public void delL(int i) {
			int j = iOffs.length;
			if (i >= j)
				return;
			int k = j - 1;
			float af[] = new float[k];
			int ai[][] = new int[k][];
			k = 0;
			for (int l = 0; l < j; l++)
				if (l != i) {
					af[k] = visit[l];
					ai[k++] = iOffs[l];
				}

			visit = af;
			iOffs = ai;
			L = j - 1;
		}

		public boolean addScale(int newScale, boolean flag) {
			if (flag) {
				if (newScale <= 0) {
					scale = 1;
					setQuality(1 - newScale);
				}
				else {
					setQuality(1);
					scale = newScale;
				}
				return true;
			}
			int tempScale = scale + newScale;
			if (tempScale > 32)
				return false;
			if (tempScale <= 0) {
				scale = 1;
				setQuality((Q + 1) - tempScale);
			}
			else if (Q >= 2) {
				setQuality(Q - 1);
			}
			else {
				setQuality(1);
				scale = tempScale;
			}
			return true;
		}

		public void setQuality(int i) {
			Q = i;
			imW = W / Q;
			imH = H / Q;
		}

		public Dimension getSize() {
			vD.setSize(vWidth, vHeight);
			return vD;
		}

		private void center(Point point) {
			point.x = point.x / scale + scaleX;
			point.y = point.y / scale + scaleY;
		}
		
		/**
		 * Get the float value of this pen size
		 * @return
		 */
		public float getPenSize() {
			return getPenSize(m.iSize);
		}
		
		/**
		 * 
		 * @param size
		 * @return
		 */
		public float getPenSize(int size) {
			return bPenSize[m.iPenM][size];
		}
		
		public int getPenSizeIndex() {
			return m.iSize;
		}
		
		/**
		 * Set current mg used by user
		 *
		 * @param m
		 */
		public void setMg(Mg mg) {
			// note that our m never changes, we do shallow copying, but we should keep track of last mg we loaded from
			// save old m
			if(lastMg != null) {
				lastMg.set(m);
			}
			// transfer over the color before we copy everything over (so we keep color and layers)
			mg.setColorAndLayer(m);
			// copy m over
			m.set(mg);
			// do a fix to sync iSize with sens size
			syncSensValues();
			// remember last
			lastMg = mg;
			
			// sync values
			syncSensValues();
		}
		
		public void syncSensValues() {
			m.setSensitivity((int)(m.iSize / (getPenSizeIndexLength()-1) * 255F) , false, true);
			m.setSensitivity(m.iAlpha, false, false);
		}
		
		public int getPenSizeIndexLength() {
			return getPenMask()[m.iPenM].length;
		}		

		public int[][][] getPenMask() {
			return bPen;
		}

		public int getPMMax() {
			return m.iHint != 8 ? bPen[m.iPenM].length : 255;
		}

		public int[][] getOffset() {
			return iOffs;
		}

		public float[] getTT(int i) {
			i -= 12;
			if (bTT[i] == null && bTT[i] == null) {
				if (dirTT != null) {
					String s1 = dirTT;
					dirTT = null;
					try {
						cnf.loadZip(s1);
					}
					catch (IOException ioexception) {
						ioexception.printStackTrace();
					}
				}
				int ai[] = loadIm("tt/" + i + ".gif", false);
				if (ai == null)
					return null;
				int j = ai.length;
				float af[] = new float[j];
				for (int k = 0; k < j; k++)
					af[k] = Mg.b255[ai[k]];

				bTT[i] = af;
			}
			return bTT[i];
		}

		private LinkedList<ToolTypeListener> toolTypeListeners;
		private int toolType;
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
		/**
		 * bPen = [MODE] [SIZE] [MASKbits]
		 */
		private int bPen[][][];
		private float bPenSize [][];
		private float bTT[][];
		public Mg m;
		private Mg lastMg;

		public Info() {
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
			bPenSize = new float[16][];
			bPen = new int[16][][];
			bTT = new float[14][];
			m = new Mg();
		}
	}

	public Mg() {
		iHint = 0;
		iPen = 0;
		iPenM = 0;
		iTT = 0;
		iColor = 0;
		iColorMask = 0;
		iAlpha = 255;
		iSA = 65280; // 0xFF00
		iLayer = 0;
		iLayerSrc = 1;
		iMask = 0;
		iSize = 0;
		iSS = 65280; // 0xFF00
		iCount = -8;
		isCount = true;
	}

	public Mg(Info info1, User user1) {
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

	private final void addD(int i, int j, int k, int l) {
		setD(Math.min(i, user.dX), Math.min(j, user.dY), Math.max(k, user.dW), Math.max(l, user.dH));
	}

	private final void ch(int i, int j) {
		int ai[][] = info.getOffset();
		int ai1[] = ai[i];
		int ai2[] = ai[j];
		int k = info.W * info.H;
		for (int i1 = 0; i1 < k; i1++) {
			int l = ai2[i1];
			ai2[i1] = ai1[i1];
			ai1[i1] = l;
		}

	}

	private final void copy(int ai[][], int ai1[][]) {
		for (int i = 0; i < ai1.length; i++)
			System.arraycopy(ai[i], 0, ai1[i], 0, ai1[i].length);

	}

	public final void dBuffer() {
		dBuffer(!user.isDirect, user.dX, user.dY, user.dW, user.dH);
	}

	private final void dBuffer(boolean isMe, int x, int y, int x2, int y2) {
		try {
			int i1 = info.scale;
			int j1 = info.Q;
			int k1 = info.W;
			int l1 = info.H;
			int j2 = info.scaleX;
			int k2 = info.scaleY;
			boolean naturalScale = i1 == 1;
			int ai[] = user.buffer;
			Color color = Color.white;
			Graphics g = info.g;
			if (g == null)
				return;
			x /= j1;
			y /= j1;
			x2 /= j1;
			y2 /= j1;
			x = x > j2 ? x : j2;
			y = y > k2 ? y : k2;
			int l2 = info.vWidth / i1 + j2;
			x2 = x2 <= l2 ? x2 : l2;
			x2 = x2 <= k1 ? x2 : k1;
			l2 = info.vHeight / i1 + k2;
			y2 = y2 <= l2 ? y2 : l2;
			y2 = y2 <= l1 ? y2 : l1;
			if (x2 <= x || y2 <= y)
				return;
			k1 = x2 - x;
			int i3 = k1 * i1;
			int j3 = (x - j2) * i1;
			int _tmp = x;
			int k3 = y;
			l2 = ai.length / (k1 * j1 * j1);
			do {
				int i2 = Math.min(l2, y2 - k3);
				if (i2 <= 0)
					break;
				Image image = isMe ? mkMPic(x, k3, k1, i2, j1) : mkLPic(null, x, k3, k1, i2, j1);
				if (naturalScale)
					g.drawImage(image, j3, k3 - k2, color, null);
				else
					g.drawImage(image, j3, (k3 - k2) * i1, i3, i2 * i1, color, null);
				k3 += i2;
			} while (true);
		}
		catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
		}
	}

	private final void dBz(int ai[]) throws InterruptedException {
		try {
			int i = ai[0];
			int k = 0;
			for (int l = 1; l < 4; l++) {
				k += Math.abs((ai[l] >> 16) - (i >> 16)) + Math.abs((short) ai[l] - (short) i);
				i = ai[l];
			}

			if (k <= 0)
				return;
			byte byte0 = -100;
			byte byte1 = -100;
			int k1 = -1000;
			int l1 = -1000;
			boolean flag = iPen == 1;
			user.count = user.count - 1;
			for (int j = k; j > 0; j--) {
				float f1 = (float) j / (float) k;
				float f = (float) Math.pow(1.0F - f1, 3D);
				float f2 = f * (float) (ai[3] >> 16);
				float f3 = f * (float) (short) ai[3];
				f = 3F * (1.0F - f1) * (1.0F - f1) * f1;
				f2 += f * (float) (ai[2] >> 16);
				f3 += f * (float) (short) ai[2];
				f = 3F * f1 * f1 * (1.0F - f1);
				f2 += f * (float) (ai[1] >> 16);
				f3 += f * (float) (short) ai[1];
				f = f1 * f1 * f1;
				f2 += f * (float) (ai[0] >> 16);
				f3 += f * (float) (short) ai[0];
				int i1 = (int) f2;
				int j1 = (int) f3;
				if (i1 != k1 || j1 != l1) {
					user.oX = i1;
					user.oY = j1;
					if (flag)
						dFLine2(i1, j1, iSize);
					else
						dFLine(i1, j1, iSize);
					k1 = i1;
					l1 = j1;
				}
			}

			user.dX = user.dX - 1;
			user.dY = user.dY - 1;
			user.dW = user.dW + 2;
			user.dH = user.dH + 2;
		}
		catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
		}
	}

	public void dClear(boolean flag) {
		if (flag)
			getWork();
		int i = info.W;
		int j = info.H;
		int ai[][] = info.iOffs;
		int ai1[] = ai[0];
		int k = 0xffffff;
		int l = i * j;
		synchronized (ai) {
			for (int i1 = 0; i1 < i; i1++)
				ai1[i1] = k;

			for (int j1 = i; j1 < l; j1 += i)
				System.arraycopy(ai1, 0, ai1, j1, i);

			for (int k1 = 1; k1 < ai.length; k1++)
				System.arraycopy(ai1, 0, ai[k1], 0, ai1.length);

		}
		user.isDirect = true;
		setD(0, 0, i, j);
		if (user.wait >= 0)
			dBuffer();
	}

	/**
	 * int Points:
	 * [1] = x1 | y1
	 * [2] = w | h
	 * [3] = x2 | y2
	 * @param points
	 * @param copy
	 */
	private void dMove(int points[], boolean copy) {
		int iw = info.W;
		int ih = info.H;
		int k = points[0];
		int x1 = k >> 16;
		int y1 = (short) k;
		k = points[1];
		int w = (k >> 16) - x1;
		int h = (short) k - y1;
		k = points[2];
		int x2 = k >> 16;
		short y2 = (short) k;
		if (x1 < 0) {
			w -= x1;
			x1 = 0;
		}
		if (x1 + w > iw)
			x1 = iw - w;
		if (y1 < 0) {
			h -= y1;
			y1 = 0;
		}
		if (y1 + h > ih)
			y1 = ih - h;
		if (x2 < 0) {
			x1 -= x2;
			w += x2;
			x2 = 0;
		}
		if (y2 < 0) {
			y1 -= y2;
			h += y2;
			y2 = 0;
		}
		if (x2 + w >= iw)
			w = iw - x2;
		if (y2 + h >= ih)
			h = ih - y2;
		if (w <= 0 || h <= 0 || x2 >= iw || y2 >= ih)
			return;
		int buffer[] = w * h > user.buffer.length ? new int[w * h] : user.buffer;
		int offsets[] = info.iOffs[iLayerSrc];
		// copy into buffer
		for (int hDelta = 0; hDelta < h; hDelta++) 
			System.arraycopy(offsets, (y1 + hDelta) * iw + x1, buffer, w * hDelta, w);
		
		if(!copy) {			
			for (int hDelta = 0; hDelta < h; hDelta++) {
				for(int wDelta = 0; wDelta < w; wDelta++) {
					offsets[(y1 + hDelta) * iw + x1 + wDelta] = 0;
				}
			}
				//System.arraycopy(offsets, (y1 + hDelta) * iw + x1, buffer, w * hDelta, w); 
		}

		// paste into destination
		offsets = info.iOffs[iLayer];
		for (int hDelta = 0; hDelta < h; hDelta++)
			System.arraycopy(buffer, w * hDelta, offsets, (y2 + hDelta) * iw + x2, w);

		setD(x2, y2, x2 + w, y2 + h);
		if(!copy) {
			// redraw this area
			dFlush();
			dBuffer();
			// now set the next draw area
			setD(x1, y1, x1 + w, y1 + h);
		}
	}

	public final void dEnd() throws InterruptedException {
		// draw single point when user didnt drag at all
		if (iHint == H_FLINE && (user.pX != user.pX2 || user.pY != user.pY2))
			dFLine(user.pX, user.pY + 1, 1);
		if (!user.isDirect)
			dFlush();
		ByteStream bytestream = info.workOut;
		if (bytestream.size() > 0) {
			offset = bytestream.writeTo(offset, 0);
			iOffset = bytestream.size();
		}
		if (user.wait == -1)
			dBuffer();
	}

	private void dFill(int i, int j) {
		int k = info.W;
		int l = info.H;
		// int[] _tmp = info.iOffs[iLayer];
		byte byte0 = (byte) iAlpha;
		byte abyte0[] = info.iMOffs;
		try {
			int ai[] = user.buffer;
			int i1 = 0;
			if (i < 0 || i >= k || j < 0 || j >= l)
				return;
			int l1 = pix(i, j);
			int i2 = iAlpha << 24 | iColor;
			if (l1 == i2)
				return;
			ai[i1++] = s(l1, i, j) << 16 | j;
			while (i1 > 0) {
				int j1 = ai[--i1];
				i = j1 >>> 16;
				j = j1 & 0xffff;
				int k1 = k * j;
				boolean flag = false;
				boolean flag1 = false;
				do {
					abyte0[k1 + i] = byte0;
					if (j > 0 && pix(i, j - 1) == l1 && abyte0[(k1 - k) + i] == 0) {
						if (!flag) {
							flag = true;
							ai[i1++] = s(l1, i, j - 1) << 16 | j - 1;
						}
					}
					else {
						flag = false;
					}
					if (j < l - 1 && pix(i, j + 1) == l1 && abyte0[k1 + k + i] == 0) {
						if (!flag1) {
							flag1 = true;
							ai[i1++] = s(l1, i, j + 1) << 16 | j + 1;
						}
					}
					else {
						flag1 = false;
					}
					if (i <= 0 || pix(i - 1, j) != l1 || abyte0[(k1 + i) - 1] != 0)
						break;
					i--;
				} while (true);
			}
		}
		catch (RuntimeException runtimeexception) {
			PCDebug.println(runtimeexception);
		}
		setD(0, 0, k, l);
		t();
	}

	private void dFill(Object obj, int startX, int startY, int endX, int endY) {
		int alpha = iAlpha;
		int ai[];
		byte abyte0[];
		if (obj instanceof int[]) {
			ai = (int[]) obj;
			abyte0 = null;
		}
		else {
			ai = null;
			abyte0 = (byte[]) obj;
		}
		int infoWidth = info.W;
		try {
			int widthLength = endX - startX;
			for (; startY < endY; startY++) {
				int arrayIndex;
				int arrayIndexStart = arrayIndex = startY * infoWidth + startX;
				int i;

				for (i = 0; i < widthLength; i++) {
					if (abyte0 != null && abyte0[arrayIndex] != 0 || ai != null && ai[arrayIndex] != 0)
						break;
					arrayIndex++;
				}

				for (; i < widthLength; i++) {
					if (abyte0 != null && abyte0[arrayIndex] == 0 || ai != null && ai[arrayIndex] == 0)
						break;
					arrayIndex++;
				}

				int k2;
				for (k2 = i; k2 < widthLength; k2++)
					if (abyte0 != null && abyte0[arrayIndexStart + k2] != 0 || ai != null
							&& ai[arrayIndexStart + k2] != 0)
						break;

				if (k2 < widthLength)
					for (; i < widthLength; i++) {
						if (abyte0 != null && abyte0[arrayIndex] != 0 || ai != null && ai[arrayIndex] != 0)
							break;
						if (ai != null)
							ai[arrayIndex] = alpha;
						if (abyte0 != null)
							abyte0[arrayIndex] = (byte) alpha;
						arrayIndex++;
					}

			}

		}
		catch (RuntimeException runtimeexception) {
			PCDebug.println(runtimeexception);
		}
	}

	/*
	 * private final void dFLine(int x, int y, int sensitivity) throws
	 * InterruptedException { int wait = this.user.wait; int userPX =
	 * this.user.pX; int userPY = this.user.pY; int userPX2 = this.user.pX2; int
	 * userPY2 = this.user.pY2; int tempUserPX = userPX; int tempUserPY =
	 * userPY; int pointFromLeft = x - userPX; int pointFromTop = y - userPY;
	 * int i6 = Math.max(Math.abs(pointFromLeft), Math.abs(pointFromTop)); if
	 * (i6 <= 0) return; if (!(this.isCount)) this.user.count = 0; int sensSize
	 * = ss(sensitivity); int sensAlpha = sa(sensitivity); float size =
	 * this.iSize; float alpha = this.iAlpha; float f3 = (sensSize - size) / i6;
	 * f3 = (f3 <= -1.0F) ? -1.0F : (f3 >= 1.0F) ? 1.0F : f3; float f4 =
	 * (sensAlpha - alpha) / i6; f4 = (f4 <= -1.0F) ? -1.0F : (f4 >= 1.0F) ?
	 * 1.0F : f4; float f5 = (pointFromLeft == 0) ? 0.0F : pointFromLeft / i6;
	 * float f6 = (pointFromTop == 0) ? 0.0F : pointFromTop / i6; float f7 =
	 * userPX; float f8 = userPY; for (int i11 = 0; i11 < i6; ++i11) { if
	 * ((userPX2 != tempUserPX) || (userPY2 != tempUserPY)) { int tmp289_288 =
	 * (this.user.count - 1); this.user.count = tmp289_288; if (tmp289_288 < 0)
	 * { this.iSize = (int)size; this.iAlpha = (int)alpha; getPM(); int i7 =
	 * tempUserPX - (this.user.pW >>> 1); int i8 = tempUserPY - (this.user.pW
	 * >>> 1); PCDebug.println("("+i7 +
	 * ",\t"+i8+")\t("+tempUserPX+",\t"+tempUserPY+")"); dPen(i7, i8, 1.0F);
	 * userPX2 = tempUserPX; userPY2 = tempUserPY; this.user.count =
	 * this.user.countMax; if (wait > 0) { dBuffer((this.user.isDirect) ? false
	 * : true, i7, i8, i7 + this.user.pW, i8 + this.user.pW); if (wait > 1) {
	 * Thread.currentThread(); Thread.sleep(wait); } } } } tempUserPX = (int)(f7
	 * += f5); tempUserPY = (int)(f8 += f6); size += f3; alpha += f4; }
	 * this.iAlpha = (int)(alpha - f4); this.iSize = (int)(size - f3); int
	 * tmp514_512 = userPX2; this.user.pX = tmp514_512; this.user.pX2 =
	 * tmp514_512; int tmp531_529 = userPY2; this.user.pY = tmp531_529;
	 * this.user.pY2 = tmp531_529; i6 =
	 * (int)Math.sqrt(this.info.bPen[this.iPenM][Math.max(sensSize,
	 * (int)size)].length) / 2 + this.info.Q; pointFromLeft = Math.min(userPX,
	 * x) - i6; pointFromTop = Math.min(userPY, y) - i6; x = Math.max(userPX, x)
	 * + i6; y = Math.max(userPY, y) + i6; if (wait == 0)
	 * dBuffer((this.user.isDirect) ? false : true, pointFromLeft, pointFromTop,
	 * x, y); addD(pointFromLeft, pointFromTop, x, y); }
	 */
	private final void dFLine(int x, int y, int sensitivity) throws InterruptedException {
		int userWait = user.wait;
		int userpX = user.pX;
		int userpY = user.pY;
		int userpX2 = user.pX2;
		int userpY2 = user.pY2;
		int int9 = userpX;
		int int10 = userpY;
		int int11 = x - userpX;
		int int12 = y - userpY;
		int int13 = Math.max(Math.abs(int11), Math.abs(int12));

		if (int13 > 0) {
			int sensSize;
			int sensAlpha;
			float float18;
			float float19;
			float float20;
			float float21;
			float float22;
			float float23;
			float float24;
			float float25;
			int int26;

			if (!isCount)
				user.count = 0;
			sensSize = ss(sensitivity);
			sensAlpha = sa(sensitivity);
			float18 = (float) iSize;
			float19 = (float) iAlpha;
			float20 = ((float) sensSize - float18) / (float) int13;
			float20 = (float20 >= 1.0F) ? 1.0F : (float20 <= -1.0F) ? -1.0F : float20;
			float21 = ((float) sensAlpha - float19) / (float) int13;
			float21 = (float21 >= 1.0F) ? 1.0F : (float21 <= -1.0F) ? -1.0F : float21;
			float22 = (int11 == 0) ? 0.0F : (float) int11 / (float) int13;
			float23 = (int12 == 0) ? 0.0F : (float) int12 / (float) int13;
			float24 = (float) userpX;
			float25 = (float) userpY;
			for (int26 = 0; int26 < int13; ++int26) {
				int temp_int27 = user.count - 1;

				user.count = temp_int27;
				if ((userpX2 != int9 || userpY2 != int10) && temp_int27 < 0) {
					int int14;
					int int15;

					iSize = (int) float18;
					iAlpha = (int) float19;
					getPM();
					int14 = int9 - (user.pW >>> 1);
					int15 = int10 - (user.pW >>> 1);
					dPen(int14, int15, 1.0F);
					userpX2 = int9;
					userpY2 = int10;
					user.count = user.countMax;
					if (userWait > 0) {
						dBuffer((user.isDirect) ? false : true, int14, int15, int14 + user.pW, int15 + user.pW);
						if (userWait > 1) {
							Thread.currentThread();
							Thread.sleep((long) userWait);
						}
					}
				}
				int9 = (int) (float24 += float22);
				int10 = (int) (float25 += float23);
				float18 += float20;
				float19 += float21;
			}
			iAlpha = (int) (float19 - float21);
			iSize = (int) (float18 - float20);
			user.pX = userpX2;
			user.pX2 = userpX2;
			user.pY = userpY2;
			user.pY2 = userpY2;

			int13 = (int) Math.sqrt((double) this.info.bPen[iPenM][Math.max(sensSize, (int) float18)].length) / 2
					+ info.Q;
			int11 = Math.min(userpX, x) - int13;
			int12 = Math.min(userpY, y) - int13;
			x = Math.max(userpX, x) + int13;
			y = Math.max(userpY, y) + int13;
			if (userWait == 0)
				dBuffer((user.isDirect) ? false : true, int11, int12, x, y);
			addD(int11, int12, x, y);
		}
	}

	private final void dFLine2(int x, int y, int sens) throws InterruptedException {
		try {
			if (!user.isF2) {
				user.pX2 = x;
				user.pY2 = y;
				user.isF2 = true;
				return;
			}
			else {
				float userfX = user.fX;
				float userfY = user.fY;
				int userpX2 = user.pX2;
				int userpY2 = user.pY2;

				if ((int) userfX == userpX2 && userpX2 == x && (int) userfY == userpY2 && userpY2 == y)
					return;
				else {
					float distance = (float) Math
							.sqrt((double) (((float) x - userfX) * ((float) x - userfX) + ((float) y - userfY)
									* ((float) y - userfY)));
					float userfX2 = userfX;
					float userfY2 = userfY;
					float float17 = distance / 1.6499999761581421F;
					int int18 = user.wait;
					int sensSize = ss(sens);
					int sensAlpha = sa(sens);
					float origSize = (float) iSize;
					float origAlpha = (float) iAlpha;
					int startDrawXPixel;
					float deltaSize;
					float deltaAlpha;
					float deltaDistance;
					int int30;
					int int31;
					int int32;
					int int34;

					if (!isCount)
						user.count = 0;
					deltaSize = ((float) sensSize - origSize) / ((distance - 0.25F - float17) * 4.0F);
					deltaSize = (deltaSize >= 0.25F) ? 0.25F : (deltaSize <= -0.25F) ? -0.25F : deltaSize;
					deltaAlpha = ((float) sensAlpha - origAlpha) / ((distance - 0.25F - float17) * 4.0F);
					deltaAlpha = (deltaAlpha >= 0.25F) ? 0.25F : (deltaAlpha <= -0.25F) ? -0.25F : deltaAlpha;
					// PCDebug.println("d="+distance+",x="+x+",px="+userpX2+",fx="+userfX+"pw="+user.pW);
					int testint = 0;
					for (deltaDistance = distance - 0.25F; deltaDistance >= float17; deltaDistance -= 0.25F) {
						int oldCount = user.count;

						user.count = oldCount - 1;
						if (oldCount <= 0) {
							int int9;
							int int10;
							float float11;
							float calculatedDistance;
							int startDrawYPixel;

							user.count = user.countMax;
							calculatedDistance = deltaDistance / distance;
							float11 = (1.0F - calculatedDistance) * (1.0F - calculatedDistance);
							userfX2 = float11 * (float) x;
							userfY2 = float11 * (float) y;
							float11 = 2.0F * calculatedDistance * (1.0F - calculatedDistance);
							userfX2 += float11 * (float) userpX2;
							userfY2 += float11 * (float) userpY2;
							float11 = calculatedDistance * calculatedDistance;
							userfX2 += float11 * userfX;
							userfY2 += float11 * userfY;
							iSize = (int) origSize;
							iAlpha = (int) origAlpha;
							getPM();
							startDrawXPixel = (int9 = (int) userfX2) - (user.pW >>> 1);
							startDrawYPixel = (int10 = (int) userfY2) - (user.pW >>> 1);
							if (isAnti) {
								float xPixelOffset = userfX2 - (float) int9;
								float yPixelOffset = userfY2 - (float) int10;
								// PCDebug.println("1="+xPixelOffset+",2="+yPixelOffset);
								int int21;
								int int22;

								if (xPixelOffset < 0.0F) {
									xPixelOffset = -xPixelOffset;
									int21 = -1;
								}
								else
									int21 = 1;
								if (yPixelOffset < 0.0F) {
									yPixelOffset = -yPixelOffset;
									int22 = -1;
								}
								else
									int22 = 1;
								if (xPixelOffset != 1.0F && yPixelOffset != 1.0F) {
									dPen(startDrawXPixel, startDrawYPixel, (1.0F - xPixelOffset) * (1.0F - yPixelOffset));
									System.out.println("a" + testint + " " + startDrawXPixel + "," +  startDrawYPixel + "," + (1.0F - xPixelOffset) * (1.0F - yPixelOffset));
								}
								if (xPixelOffset != 0.0F) {
									dPen(startDrawXPixel + int21, startDrawYPixel, xPixelOffset * (1.0F - yPixelOffset));
									System.out.println("b" + testint + " " + (startDrawXPixel + int21) + "," +  startDrawYPixel + "," + (xPixelOffset * (1.0F - yPixelOffset)));
								}
								if (yPixelOffset != 0.0F) {
									dPen(startDrawXPixel, startDrawYPixel + int22, (1.0F - xPixelOffset) * yPixelOffset);
									System.out.println("c" + testint + " " + startDrawXPixel + "," +  (startDrawYPixel + int22) + "," + ((1.0F - xPixelOffset) * yPixelOffset));
								}
								if (xPixelOffset != 0.0F && yPixelOffset != 0.0F) {
									dPen(startDrawXPixel + int21, startDrawYPixel + int22, xPixelOffset * yPixelOffset);
									System.out.println("d" + testint + " " + (startDrawXPixel + int21) + "," +  (startDrawYPixel + int22) + "," + (xPixelOffset * yPixelOffset));
								}
								testint++;
							}
							else
								dPen(startDrawXPixel, startDrawYPixel, 1.0F);
							if (int18 > 0) {
								dBuffer((user.isDirect) ? false : true, startDrawXPixel, startDrawYPixel,
										startDrawXPixel + user.pW + 1, startDrawYPixel + user.pW + 1);
								if (int18 > 1) {
									Thread.currentThread();
									Thread.sleep((long) int18);
								}
							}
						}
						origSize += deltaSize;
						origAlpha += deltaAlpha;
					}
					iSize = sensSize;
					iAlpha = sensAlpha;
					user.fX = userfX2;
					user.fY = userfY2;
					user.pX = user.pX2 = x;
					user.pY = user.pY2 = y;
					startDrawXPixel = (int) Math
							.sqrt((double) info.bPen[iPenM][Math.max(sensSize, (int) origSize)].length)
							/ 2 + info.Q + 1;
					int34 = Math.min(Math.min((int) userfX, userpX2), (int) userfX2) - startDrawXPixel;
					int30 = Math.min(Math.min((int) userfY, userpY2), (int) userfY2) - startDrawXPixel;
					int31 = Math.max(Math.max((int) userfX, userpX2), (int) userfX2) + startDrawXPixel;
					int32 = Math.max(Math.max((int) userfY, userpY2), (int) userfY2) + startDrawXPixel;
					if (user.wait == 0)
						dBuffer((user.isDirect) ? false : true, int34, int30, int31, int32);
					addD(int34, int30, int31, int32);
				}
			}
		}
		catch (RuntimeException RuntimeException35) {
			((Throwable) RuntimeException35).printStackTrace();
		}
	}

	private final void dFLine3(int x, int y, int sens) throws InterruptedException {
		try {			
			if (!user.isF2) {
				user.pX2 = x;
				user.pY2 = y;
				user.isF2 = true;
				return;
			}
			else {
				float userfX = user.fX;
				float userfY = user.fY;
				int userpX2 = user.pX2;
				int userpY2 = user.pY2;

				if ((int) userfX == userpX2 && userpX2 == x && (int) userfY == userpY2 && userpY2 == y)
					return;
				else {
					float distance = (float) Math.sqrt((double) (((float) x - userfX) * ((float) x - userfX) + ((float) y - userfY) * ((float) y - userfY)));
					float userfX2 = userfX;
					float userfY2 = userfY;
					float float17 = distance / 1.6499999761581421F;
					int int18 = user.wait;
					int sensSize = ss(sens);
					int sensAlpha = sa(sens);
					float origSize = (float) iSize;
					float origAlpha = (float) iAlpha;
					int startDrawXPixel;
					float deltaSize;
					float deltaAlpha;
					float deltaDistance;
					int int30;
					int int31;
					int int32;
					int int34;

					if (!isCount)
						user.count = 0;
					deltaSize = ((float) sensSize - origSize) / ((distance - 0.25F - float17) * 4.0F);
					deltaSize = (deltaSize >= 0.25F) ? 0.25F : (deltaSize <= -0.25F) ? -0.25F : deltaSize;
					deltaAlpha = ((float) sensAlpha - origAlpha) / ((distance - 0.25F - float17) * 4.0F);
					deltaAlpha = (deltaAlpha >= 0.25F) ? 0.25F : (deltaAlpha <= -0.25F) ? -0.25F : deltaAlpha;
					// PCDebug.println("d="+distance+",x="+x+",px="+userpX2+",fx="+userfX+"pw="+user.pW);
					int testint = 0;
					for (deltaDistance = distance - 0.25F; deltaDistance >= float17; deltaDistance -= 0.25F) {
						testint++;
					}
					//System.out.println("xy(" +x+","+y+") pxy(" + user.pX + "," + user.pY + ") fxy(" + user.fX +"," + user.fY + ") d: " + distance + " cap: " + float17 + ", i: " + testint + ", c: " + user.count);
					for (deltaDistance = distance - 0.25F; deltaDistance >= float17; deltaDistance -= 0.25F) {

						user.count--;
						if (user.count < 0) {
							int int9;
							int int10;
							float float11;
							float calculatedDistance;
							int startDrawYPixel;

							user.count = user.countMax;
							calculatedDistance = deltaDistance / distance;
							float11 = (1.0F - calculatedDistance) * (1.0F - calculatedDistance);
							userfX2 = float11 * (float) x;
							userfY2 = float11 * (float) y;
							float11 = 2.0F * calculatedDistance * (1.0F - calculatedDistance);
							userfX2 += float11 * (float) userpX2;
							userfY2 += float11 * (float) userpY2;
							float11 = calculatedDistance * calculatedDistance;
							userfX2 += float11 * userfX;
							userfY2 += float11 * userfY;
							iSize = (int) origSize;
							iAlpha = (int) origAlpha;
							getPM();
							int penRadius = (user.pW >>> 1);
							startDrawXPixel = (int9 = (int) userfX2) - penRadius;
							startDrawYPixel = (int10 = (int) userfY2) - penRadius;
							penRadius = Math.max(1, penRadius);							
							
							if (isAnti ) {
								float xPixelOffset = userfX2 - (float) int9;
								float yPixelOffset = userfY2 - (float) int10;
								// PCDebug.println("1="+xPixelOffset+",2="+yPixelOffset);
								int int21;
								int int22;

								// when user draws off canvas (left)
								if (xPixelOffset < 0.0F) {
									xPixelOffset = -xPixelOffset;
									int21 = -1;
								}
								else
									int21 = 1;
								// when user draws off canvas (top)								
								if (yPixelOffset < 0.0F) {
									yPixelOffset = -yPixelOffset;
									int22 = -1;
								}
								else
									int22 = 1;
								if (xPixelOffset != 1.0F && yPixelOffset != 1.0F) {
									dPen(startDrawXPixel, startDrawYPixel, (1.0F - xPixelOffset) * (1.0F - yPixelOffset));
									//System.out.println("a" + testint + " " + startDrawXPixel + "," +  startDrawYPixel + "," + (1.0F - xPixelOffset) * (1.0F - yPixelOffset));
								}
								if (xPixelOffset != 0.0F) {
									dPen(startDrawXPixel + int21, startDrawYPixel, xPixelOffset * (1.0F - yPixelOffset));
									//System.out.println("b" + testint + " " + (startDrawXPixel + int21) + "," +  startDrawYPixel + "," + (xPixelOffset * (1.0F - yPixelOffset)));
								}
								if (yPixelOffset != 0.0F) {
									dPen(startDrawXPixel, startDrawYPixel + int22, (1.0F - xPixelOffset) * yPixelOffset);
									//System.out.println("c" + testint + " " + startDrawXPixel + "," +  (startDrawYPixel + int22) + "," + ((1.0F - xPixelOffset) * yPixelOffset));
								}
								if (xPixelOffset != 0.0F && yPixelOffset != 0.0F) {
									dPen(startDrawXPixel + int21, startDrawYPixel + int22, xPixelOffset * yPixelOffset);
									//System.out.println("d" + testint + " " + (startDrawXPixel + int21) + "," +  (startDrawYPixel + int22) + "," + (xPixelOffset * yPixelOffset));
								}								
							}
							else
								dPen(startDrawXPixel, startDrawYPixel, 1.0F);
							if (int18 > 0) {
								dBuffer((user.isDirect) ? false : true, startDrawXPixel, startDrawYPixel,
										startDrawXPixel + user.pW + 1, startDrawYPixel + user.pW + 1);
								if (int18 > 1) {
									Thread.currentThread();
									Thread.sleep((long) int18);
								}
							}
						}
						origSize += deltaSize;
						origAlpha += deltaAlpha;
						//System.out.println("a: " + origAlpha + ", s: " + origSize);
					}
					iSize = sensSize;
					iAlpha = sensAlpha;
					user.fX = userfX2;
					user.fY = userfY2;
					user.pX = user.pX2 = x;
					user.pY = user.pY2 = y;
					startDrawXPixel = (int) Math
							.sqrt((double) info.bPen[iPenM][Math.max(sensSize, (int) origSize)].length)
							/ 2 + info.Q + 1;
					int34 = Math.min(Math.min((int) userfX, userpX2), (int) userfX2) - startDrawXPixel;
					int30 = Math.min(Math.min((int) userfY, userpY2), (int) userfY2) - startDrawXPixel;
					int31 = Math.max(Math.max((int) userfX, userpX2), (int) userfX2) + startDrawXPixel;
					int32 = Math.max(Math.max((int) userfY, userpY2), (int) userfY2) + startDrawXPixel;
					if (user.wait == 0)
						dBuffer((user.isDirect) ? false : true, int34, int30, int31, int32);
					addD(int34, int30, int31, int32);
				}
			}
		}
		catch (RuntimeException RuntimeException35) {
			((Throwable) RuntimeException35).printStackTrace();
		}
	}
	
	/*
	 * private final void dFLine2(int x, int y, int sensitivity) throws
	 * InterruptedException { try { if (!(this.user.isF2)) { this.user.pX2 = x;
	 * this.user.pY2 = y; this.user.isF2 = true; return; }
	 * 
	 * float userFX = this.user.fX; float userFY = this.user.fY; int userPX2 =
	 * this.user.pX2; int userPY2 = this.user.pY2; if (((int)userFX == userPX2)
	 * && (userPX2 == x) && ((int)userFY == userPY2) && (userPY2 == y)) return;
	 * float distanceDelta = (float)Math.sqrt((x - userFX) * (x - userFX) + (y -
	 * userFY) * (y - userFY)); float userFXBak = userFX; float userFYBak =
	 * userFY; float f10 = distanceDelta / 1.65F; int usreWait = this.user.wait;
	 * int sensSize = ss(sensitivity); int sensAlpha = sa(sensitivity); float
	 * curSize = this.iSize; float curAlpha = this.iAlpha; if (!(this.isCount))
	 * this.user.count = 0; float f13 = (sensSize - curSize) / (distanceDelta -
	 * 0.25F - f10) * 4.0F; f13 = (f13 <= -0.25F) ? -0.25F : (f13 >= 0.25F) ?
	 * 0.25F : f13; float f14 = (sensAlpha - curAlpha) / (distanceDelta - 0.25F
	 * - f10) * 4.0F; f14 = (f14 <= -0.25F) ? -0.25F : (f14 >= 0.25F) ? 0.25F :
	 * f14; float f15 = distanceDelta - 0.25F; while (f15 >= f10) { int
	 * tmp329_326 = this.user.count; this.user.count = tmp329_326 - 1; if
	 * (tmp329_326 <= 0) { int k; int l; this.user.count = this.user.countMax;
	 * float f5 = f15 / distanceDelta; float f4 = (1.0F - f5) * (1.0F - f5);
	 * userFXBak = f4 * x; userFYBak = f4 * y; f4 = 2.0F * f5 * (1.0F - f5);
	 * userFXBak += f4 * userPX2; userFYBak += f4 * userPY2; f4 = f5 * f5;
	 * userFXBak += f4 * userFX; userFYBak += f4 * userFY; this.iSize = curSize
	 * < 0? 0 : curSize >= info.bPen[iPenM].length? info.bPen[iPenM].length - 1
	 * : (int) curSize; this.iAlpha = curAlpha < 0? 0 : curAlpha >= b255.length?
	 * b255.length - 1 : (int) curAlpha; getPM(); int i2 = (k = (int)userFXBak)
	 * - (this.user.pW >>> 1); int i3 = (l = (int)userFYBak) - (this.user.pW >>>
	 * 1); if (this.isAnti) { int i4; int i5; float f8 = userFXBak - k; float f9
	 * = userFYBak - l; if (f8 < 0.0F) { f8 = -f8; i4 = -1; } else { i4 = 1; }
	 * if (f9 < 0.0F) { f9 = -f9; i5 = -1; } else { i5 = 1; } if ((f8 != 1.0F)
	 * && (f9 != 1.0F)) dPen(i2, i3, (1.0F - f8) * (1.0F - f9)); if (f8 != 0.0F)
	 * dPen(i2 + i4, i3, f8 * (1.0F - f9)); if (f9 != 0.0F) dPen(i2, i3 + i5,
	 * (1.0F - f8) * f9); if ((f8 != 0.0F) && (f9 != 0.0F)) dPen(i2 + i4, i3 +
	 * i5, f8 * f9); } else { dPen(i2, i3, 1.0F); } if (usreWait > 0) {
	 * dBuffer((this.user.isDirect) ? false : true, i2, i3, i2 + this.user.pW +
	 * 1, i3 + this.user.pW + 1); if (usreWait > 1) { Thread.currentThread();
	 * Thread.sleep(usreWait); } } } curSize += f13; curAlpha += f14; f15 -=
	 * 0.25F; } this.iSize = sensSize; this.iAlpha = sensAlpha; this.user.fX =
	 * userFXBak; this.user.fY = userFYBak; int tmp827_826 = x; this.user.pX2 =
	 * tmp827_826; this.user.pX = tmp827_826; int tmp843_842 = y; this.user.pY2
	 * = tmp843_842; this.user.pY = tmp843_842; int i2 =
	 * (int)Math.sqrt(this.info
	 * .bPen[this.iPenM][Math.min(info.bPen[this.iPenM].length - 1,
	 * Math.max(sensSize, (int)curSize))].length) / 2 + this.info.Q + 1; int i8
	 * = Math.min(Math.min((int)userFX, userPX2), (int)userFXBak) - i2; int i9 =
	 * Math.min(Math.min((int)userFY, userPY2), (int)userFYBak) - i2; int i10 =
	 * Math.max(Math.max((int)userFX, userPX2), (int)userFXBak) + i2; int i11 =
	 * Math.max(Math.max((int)userFY, userPY2), (int)userFYBak) + i2; if
	 * (this.user.wait == 0) dBuffer((this.user.isDirect) ? false : true, i8,
	 * i9, i10, i11); addD(i8, i9, i10, i11); } catch (RuntimeException
	 * localRuntimeException) { localRuntimeException.printStackTrace(); } }
	 */
	private final void dFlush() {
		int _tmp = info.Q;
		int iW = info.W;
		int iH = info.H;
		int uX1 = user.dX > 0 ? user.dX : 0;
		int uY1 = user.dY > 0 ? user.dY : 0;
		int uX2 = user.dW < iW ? user.dW : iW;
		int uY2 = user.dH < iH ? user.dH : iH;
		byte iMoffs[] = info.iMOffs;
		int ioffsL[] = info.iOffs[iLayer];
		switch (iPen) {
			case P_LR: // '\021'
				int uW = (uX2 - uX1) / 2;
				for (; uY1 < uY2; uY1++) {
					int curX = uY1 * iW + uX1;
					int curW = (curX + (uX2 - uX1)) - 1;
					for (int cnt = 0; cnt < uW; cnt++) {
						int pixelOffset = ioffsL[curX];
						ioffsL[curX] = ioffsL[curW];
						ioffsL[curW] = pixelOffset;
						iMoffs[curX] = iMoffs[curW] = 0;
						curX++;
						curW--;
					}

				}

				break;

			case P_UD: // '\022'
				int uH = (uY2 - uY1) / 2;
				for (; uX1 < uX2; uX1++) {
					int curY = uY1 * iW + uX1;
					int curH = curY + (uY2 - uY1 - 1) * iW;
					for (int i2 = 0; i2 < uH; i2++) {
						int l4 = ioffsL[curY];
						ioffsL[curY] = ioffsL[curH];
						ioffsL[curH] = l4;
						iMoffs[curY] = iMoffs[curH] = 0;
						curY += iW;
						curH -= iW;
					}

				}

				break;

			case P_R: // '\023'
				int j6 = uX2 - uX1;
				int k7 = uY2 - uY1;
				int k8 = uY1 * iW + uX1;
				int k9 = j6 * k7;
				int ai2[] = new int[k9];
				for (int j10 = 0; j10 < k7; j10++)
					System.arraycopy(ioffsL, k8 + iW * j10, ai2, j6 * j10, j6);

				for (int k10 = 0; k10 < j6; k10++) {
					ioffsL[uX1 + k10] = 0xffffff;
					iMoffs[k8 + k10] = 0;
				}

				for (int l10 = 1; l10 < k7; l10++) {
					System.arraycopy(ioffsL, k8, ioffsL, k8 + l10 * iW, j6);
					System.arraycopy(iMoffs, k8, iMoffs, k8 + l10 * iW, j6);
				}

				boolean flag = false;
				k9 = iW * iH;
				for (int l11 = 0; l11 < k7; l11++) {
					int k = j6 * l11;
					int i5 = (k8 + k7) - l11;
					for (int j12 = 0; j12 < j6; j12++) {
						int i13 = j12 + uX1;
						if (i13 <= iW && i13 >= 0 && i5 < k9)
							ioffsL[i5] = ai2[k];
						i5 += iW;
						k++;
					}

				}

				addD(uX1, uY1, uX1 + Math.max(j6, k7), uY1 + j6);
				break;

			case P_FUSION: // '\024'
				if (iLayerSrc != iLayer) {
					int ai1[] = info.iOffs[iLayerSrc];
					int ai3[] = ioffsL;
					int ai4[] = ai1;
					float f = b255[iAlpha2 >>> 8];
					float f1 = b255[iAlpha2 & 0xff];
					if (iLayer < iLayerSrc) {
						ai3 = ai1;
						ai4 = ioffsL;
						f = f1;
						f1 = b255[iAlpha2 >>> 8];
					}
					for (; uY1 < uY2; uY1++) {
						int l = uY1 * iW + uX1;
						for (int j2 = uX1; j2 < uX2; j2++) {
							if (iMoffs[l] != 0) {
								int l7 = (int) ((float) (ai3[l] >>> 24) * f);
								int k6 = (int) ((float) (ai4[l] >>> 24) * b255[255 - l7] * f1);
								int j5 = ai3[l];
								int l8 = k6 + l7;
								if (l8 != 0) {
									int l9 = ai4[l];
									float f2 = (float) k6 / (float) l8;
									j5 = f2 != 1.0F ? f2 != 0.0F ? ((j5 & 0xff0000)
											+ (int) ((float) ((l9 & 0xff0000) - (j5 & 0xff0000)) * f2) & 0xff0000)
											+ ((j5 & 0xff00) + (int) ((float) ((l9 & 0xff00) - (j5 & 0xff00)) * f2) & 0xff00)
											+ ((j5 & 0xff) + (int) ((float) ((l9 & 0xff) - (j5 & 0xff)) * f2))
											: j5
											: l9;
								}
								else {
									j5 = 0xffffff;
								}
								ioffsL[l] = l8 << 24 | j5 & 0xffffff;
								ai1[l] = 0xffffff;
								iMoffs[l] = 0;
							}
							l++;
						}

					}

				}
				if (user.wait >= 0)
					dBuffer();
				break;

			case P_MOSAIC: // '\t'
				int k5 = iAlpha / 10 + 1;
				uX1 = (uX1 / k5) * k5;
				uY1 = (uY1 / k5) * k5;
				int ai5[] = user.argb;
				for (int l6 = uY1; l6 < uY2; l6 += k5) {
					for (int k2 = uX1; k2 < uX2; k2 += k5) {
						int i10 = Math.min(k5, iW - k2);
						int i11 = Math.min(k5, iH - l6);
						for (int j11 = 0; j11 < 4; j11++)
							ai5[j11] = 0;

						int i12 = 0;
						for (int i9 = 0; i9 < i11; i9++) {
							for (int i8 = 0; i8 < i10; i8++) {
								int k12 = pix(k2 + i8, l6 + i9);
								int i1 = (l6 + i9) * iW + k2 + i8;
								for (int k11 = 0; k11 < 4; k11++)
									ai5[k11] += k12 >>> k11 * 8 & 0xff;

								i12++;
							}

						}

						int l12 = ai5[3] << 24 | ai5[2] / i12 << 16 | ai5[1] / i12 << 8 | ai5[0] / i12;
						for (int j9 = l6; j9 < l6 + i11; j9++) {
							int j1 = iW * j9 + k2;
							for (int j8 = 0; j8 < i10; j8++) {
								if (iMoffs[j1] != 0) {
									iMoffs[j1] = 0;
									ioffsL[j1] = l12;
								}
								j1++;
							}

						}

					}

				}

				if (user.wait >= 0)
					dBuffer(true, uX1, uY1, uX2, uY2);
				break;

			default:
				for (; uY1 < uY2; uY1++) {
					int k1 = uY1 * iW + uX1;
					for (int l2 = uX1; l2 < uX2; l2++) {
						ioffsL[k1] = getM(ioffsL[k1], iMoffs[k1] & 0xff, k1);
						iMoffs[k1] = 0;
						k1++;
					}

				}

				break;
		}
	}

	private final boolean dNext() throws InterruptedException {
		if (iSeek >= iOffset)
			return false;
		int i = user.oX + rPo();
		int j = user.oY + rPo();
		int k = iSOB == 0 ? 0 : ru();
		user.oX = i;
		user.oY = j;
		if (iPen != 1)
			dFLine(i, j, k);
		else
			//dFLine2(i, j, k);
			dFLine3(i, j, k);
		return true;
	}

	public final void dNext(int x, int y, int sens) throws InterruptedException, IOException {
		int l = info.scale;
		// int _tmp = user.pW;
		x = (x / l + info.scaleX) * info.Q;
		y = (y / l + info.scaleY) * info.Q;
		wPo(x - user.oX);
		wPo(y - user.oY);
		user.oX = x;
		user.oY = y;
		if (iSOB != 0)
			info.workOut.write(sens);
		if (iPen == 1)
			//dFLine2(x, y, sens); // goodline
			dFLine3(x, y, sens);
		else
			dFLine(x, y, sens); // badline
	}

	private final void dPen(int x, int y, float alpha) {
		if (iPen != 3) {
			dPenM(x, y, alpha);
			if (isOver)
				dFlush();
		}
		else {
			dPY(x, y);
		}
	}
	
	private final void dPenM(int x, int y, float alpha) {
		boolean flag = false;
		int _tmp = info.Q;
		int penPixelArray[] = getPM();
		int infoW = info.W;
		int userPW = user.pW;
		int alphaMax = user.pA;
		int k2 = userPW * Math.max(-y, 0) + Math.max(-x, 0);
		int capX = Math.min(x + userPW, infoW);
		int capY = Math.min(y + userPW, info.H);
		if (capX <= 0 || capY <= 0)
			return;
		x = x > 0 ? x : 0;
		y = y > 0 ? y : 0;
		int ioffsLayer[] = info.iOffs[iLayer];
		byte mioffs[] = info.iMOffs;
		// PCDebug.println("x="+x+"capx="+capX+",pw="+userPW);
		for (int ypxl = y; ypxl < capY; ypxl++) {
			int ioffsIndex = infoW * ypxl + x;
			int j1 = k2;
			k2 += userPW;
			for (int k = x; k < capX; k++)
				if (isM(ioffsLayer[ioffsIndex])) {
					ioffsIndex++;
					j1++;
				}
				else {
					int currentMOffs = mioffs[ioffsIndex] & 0xff;
					int penPixelValue = penPixelArray[j1++];
					// PCDebug.println("pp="+penPixelValue);
					// PCDebug.println("pixelPoint" + mousePoint);
					if (penPixelValue == 0)
						ioffsIndex++;
					else
						switch (iPen) {
							case P_PEN: // '\001'
							case P_FUSION: // '\024'								
								
								// TODO, smooth out number of times pen brush needs to touch canvas								
								if(j1 == (user.pW * user.pW)/2) {
					//				System.out.println("ppvl: " + penPixelValue + ", fpvl: " + b255[penPixelValue] + ", b: " + (alphaMax * b255[penPixelValue]));
								}
								
								mioffs[ioffsIndex++] = (byte) Math.max(currentMOffs, Math.min(alphaMax * alpha, 
										(currentMOffs + (int) ((float) penPixelValue * b255[255 - currentMOffs >>> 1] * alpha))));
								
								/*mioffs[ioffsIndex++] = (byte) Math.max(currentMOffs, Math.min(alphaMax * b255[penPixelValue], Math.min(255, 
										(currentMOffs + (int) ((float) penPixelValue * b255[255 - currentMOffs >>> 1] * alpha)))));
									*/
								//mioffs[ioffsIndex++] = (byte) (currentMOffs + (int) ((float) penPixelValue * b255[255 - currentMOffs >>> 1] * alpha));
								break;

							case P_SUISAI: // '\002'
							case P_SWHITE: // '\005'
							case P_LIGHT: // '\006'
							case P_DARK: // '\007'
								if ((penPixelValue = (int) ((float) penPixelValue * getTT(k, ypxl))) != 0)
									mioffs[ioffsIndex] = (byte) Math.min(currentMOffs
											+ Math.max((int) ((float) penPixelValue * b255[255 - currentMOffs >>> 2]),
													1), 255);
								ioffsIndex++;
								break;

							default:
								mioffs[ioffsIndex++] = (byte) Math.max((int) ((float) penPixelValue * getTT(k, ypxl)),
										currentMOffs);
								break;
						}
				}

		}

	}

	public void dPre(Graphics g, int ai[]) {
		try {
			int k = ai[0];
			int l = ai[1];
			int i1 = ai[2];
			int j1 = ai[3];
			int i = ai[0];
			int k1 = 0;
			for (int l1 = 1; l1 < 4; l1++) {
				k1 += Math.abs((ai[l1] >> 16) - (i >> 16)) + Math.abs((short) ai[l1] - (short) i);
				i = ai[l1];
			}

			k1 /= 2;
			if (k1 <= 0)
				return;
			byte byte0 = -100;
			byte byte1 = -100;
			int k2 = k >> 16;
			int l2 = (short) k;
			for (int j = k1; j > 0; j--) {
				float f1 = (float) j / (float) k1;
				float f = (float) Math.pow(1.0F - f1, 3D);
				float f2 = f * (float) (j1 >> 16);
				float f3 = f * (float) (short) j1;
				f = 3F * (1.0F - f1) * (1.0F - f1) * f1;
				f2 += f * (float) (i1 >> 16);
				f3 += f * (float) (short) i1;
				f = 3F * f1 * f1 * (1.0F - f1);
				f2 += f * (float) (l >> 16);
				f3 += f * (float) (short) l;
				f = f1 * f1 * f1;
				f2 += f * (float) (k >> 16);
				f3 += f * (float) (short) k;
				int i2 = (int) f2;
				int j2 = (int) f3;
				if (i2 != k2 || j2 != l2) {
					g.fillRect(i2, j2, 1, 1);
					k2 = i2;
					l2 = j2;
				}
			}

		}
		catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
		}
	}

	private final void dPY(int i, int j) {
		boolean flag = false;
		int penMask[] = getPM();
		int width = info.W;
		int penWidth = user.pW;
		int l2 = penWidth * Math.max(-j, 0) + Math.max(-i, 0);
		int i3 = l2;
		int pointX = Math.min(i + penWidth, width);
		int pointY = Math.min(j + penWidth, info.H);
		i = i > 0 ? i : 0;
		j = j > 0 ? j : 0;
		if (pointX - i <= 0 || pointY - j <= 0)
			return;
		int layerOffsets[] = info.iOffs[iLayer];
		int l3 = 0;
		int nAlpha = 0;
		int nRed = 0;
		int nGreen = 0;
		int nBlue = 0;
		for (int l6 = j; l6 < pointY; l6++) {
			int k = width * l6 + i;
			int j1 = i3;
			i3 += penWidth;
			for (int j7 = i; j7 < pointX; j7++) {
				int j6;
				// if penmask = 0 or mask
				if ((penMask[j1++]) == 0 || isM(j6 = layerOffsets[k++])) {
					k++;
				}
				else {
					nAlpha += j6 >>> 24;
					nRed += j6 >>> 16 & 0xff;
					nGreen += j6 >>> 8 & 0xff;
					nBlue += j6 & 0xff;
					l3++;
				}
			}

		}

		if (l3 == 0)
			return;
		nAlpha /= l3;
		nRed /= l3;
		nGreen /= l3;
		nBlue /= l3;
		if (iAlpha > 0) {
			float f1 = b255[iAlpha] / 3F;
			int k7 = iColor >>> 16 & 0xff;
			int i8 = iColor >>> 8 & 0xff;
			int j8 = iColor & 0xff;
			nAlpha = Math.max((int) ((float) nAlpha + (float) (255 - nAlpha) * f1), 1);
			int l = (int) ((float) (k7 - nRed) * f1);
			nRed += l == 0 ? ((int) (k7 <= nRed ? ((int) (k7 >= nRed ? 0 : -1)) : 1)) : l;
			l = (int) ((float) (i8 - nGreen) * f1);
			nGreen += l == 0 ? ((int) (i8 <= nGreen ? ((int) (i8 >= nGreen ? 0 : -1)) : 1)) : l;
			l = (int) ((float) (j8 - nBlue) * f1);
			nBlue += l == 0 ? ((int) (j8 <= nBlue ? ((int) (j8 >= nBlue ? 0 : -1)) : 1)) : l;
		}
		i3 = l2;
		for (int i7 = j; i7 < pointY; i7++) {
			int i1 = width * i7 + i;
			int k1 = i3;
			i3 += penWidth;
			for (int l7 = i; l7 < pointX; l7++) {
				int i2 = penMask[k1++];
				int k6 = layerOffsets[i1];
				float f;
				if (i2 == 0 || isM(k6) || (f = getTT(l7, i7) * b255[i2]) == 0.0F) {
					i1++;
				}
				else {
					int j4 = k6 >>> 24;
					int k4 = k6 >>> 16 & 0xff;
					int i5 = k6 >>> 8 & 0xff;
					int l4 = k6 & 0xff;
					int i4 = (int) ((float) (nAlpha - j4) * f);
					j4 += i4 == 0 ? ((int) (nAlpha <= j4 ? ((int) (nAlpha >= j4 ? 0 : -1)) : 1)) : i4;
					i4 = (int) ((float) (nRed - k4) * f);
					k4 += i4 == 0 ? ((int) (nRed <= k4 ? ((int) (nRed >= k4 ? 0 : -1)) : 1)) : i4;
					i4 = (int) ((float) (nGreen - i5) * f);
					i5 += i4 == 0 ? ((int) (nGreen <= i5 ? ((int) (nGreen >= i5 ? 0 : -1)) : 1)) : i4;
					i4 = (int) ((float) (nBlue - l4) * f);
					l4 += i4 == 0 ? ((int) (nBlue <= l4 ? ((int) (nBlue >= l4 ? 0 : -1)) : 1)) : i4;
					layerOffsets[i1++] = (j4 << 24) + (k4 << 16) + (i5 << 8) + l4;
				}
			}
		}
	}

	public final void draw() throws InterruptedException {
		try {
			if (info == null)
				return;
			iSeek = 0;
			switch (iHint) {
				case 0: // '\0'
				case 1: // '\001'
					dStart();
					while (dNext())
						;
					break;

				case 10: // '\n'
					dClear(false);
					break;

				default:
					dRetouch(null);
					break;
			}
		}
		catch (InterruptedException _ex) {
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		dEnd();
	}

	private void dRect(int i, int j, int k, int l) {
		int i1 = info.W;
		int j1 = info.H;
		byte abyte0[] = info.iMOffs;
		int ai[] = info.iOffs[iLayer];
		byte byte0 = (byte) iAlpha;
		if (i < 0)
			i = 0;
		if (j < 0)
			j = 0;
		if (k > i1)
			k = i1;
		if (l > j1)
			l = j1;
		if (i >= k || j >= l || byte0 == 0)
			return;
		setD(i, j, k, l);
		label0: switch (iHint) {
			default:
				break;

			case H_RECT: // '\003'
				for (int l2 = j; l2 < l; l2++) {
					int k1 = l2 * i1 + i;
					for (int j2 = i; j2 < k; j2++) {
						if (!isM(ai[k1]))
							abyte0[k1] = byte0;
						k1++;
					}

				}

				break;

			case H_FRECT: // '\004'
				int i3 = i;
				int k3 = j;
				int i4 = k;
				int k4 = l;
				for (int i5 = 0; i5 < iSize + 1; i5++) {
					int l1 = i1 * k3 + i3;
					int i2 = i1 * (k4 - 1) + i3;
					for (int k2 = i3; k2 < i4; k2++) {
						if (!isM(ai[l1]))
							abyte0[l1] = byte0;
						if (!isM(ai[i2]))
							abyte0[i2] = byte0;
						l1++;
						i2++;
					}

					l1 = i1 * k3 + i3;
					i2 = (i1 * k3 + i4) - 1;
					for (int k5 = k3; k5 < k4; k5++) {
						if (!isM(ai[l1]))
							abyte0[l1] = byte0;
						if (!isM(ai[i2]))
							abyte0[i2] = byte0;
						l1 += i1;
						i2 += i1;
					}

					i3++;
					i4--;
					k3++;
					k4--;
					if (i4 <= i3 || k4 <= k3)
						break label0;
				}

				break;

			case H_OVAL: // '\005'
			case H_FOVAL: // '\006'
				int j3 = k - i - 1;
				int l3 = l - j - 1;
				int j5 = j3 / 2;
				int l5 = l3 / 2;
				for (float f = 0.0F; f < 8F; f = (float) ((double) f + 0.001D)) {
					int j4 = i + j5 + (int) Math.round(Math.cos(f) * (double) j5);
					int l4 = j + l5 + (int) Math.round(Math.sin(f) * (double) l5);
					abyte0[i1 * l4 + j4] = byte0;
				}

				if (iHint == H_OVAL && j5 > 0 && l5 > 0)
					dFill(abyte0, i, j, k, l);
				break;
		}
		t();
	}

	public void dRetouch(int ai[]) throws InterruptedException {
		dRetouch(ai, true);
	}

	public void dRetouch(int paramPoints[], boolean flag) throws InterruptedException {
		try {
			boolean hasParamPoints = paramPoints != null;
			byte pointsTouched = 4;
			user.setup(this);
			setD(0, 0, 0, 0);
			int userPoints[] = user.points;
			if (hasParamPoints) {
				if (flag) {
					int scale = info.scale;
					int quality = info.Q;
					int scaleX = info.scaleX;
					int scaleY = info.scaleY;
					int zero = iHint != H_BEZI ? 0 : user.pW / 2;
					for (int k3 = 0; k3 < 4; k3++) {
						int scaledX = paramPoints[k3] >> 16;
						int scaledY = (short) paramPoints[k3];
						scaledX = (scaledX / scale + scaleX) * quality - zero;
						scaledY = (scaledY / scale + scaleY) * quality - zero;
						userPoints[k3] = scaledX << 16 | scaledY & 0xffff;
					}

				}
				else {
					System.arraycopy(paramPoints, 0, userPoints, 0, paramPoints.length);
				}
			}
			else {
				int j = 0;
				while (iSeek < iOffset) {
					userPoints[j++] = (r2() & 0xffff) << 16 | r2() & 0xffff;
					if (iHint == H_TEXT)
						break;
				}
			}
			int x = userPoints[0] >> 16;
			short y = (short) userPoints[0];
			switch (iHint) {
				case H_BEZI: // '\002'
					int j1 = user.wait;
					user.wait = -2;
					dStart(x, y, 0, false, false);
					dBz(userPoints);
					user.wait = j1;
					break;

				case H_TEXT: // '\b'
					String s1 = info.text;
					String s2 = info.textOption;
					if (!hasParamPoints) {
						String s3 = new String(offset, iSeek, iOffset - iSeek, "UTF8");
						int j3 = s3.indexOf('\0');
						s1 = s3.substring(j3 + 1);
						s2 = s3.substring(0, j3);
					}
					dText(s1, s2, x, y);
					pointsTouched = 1;
					break;

				case H_COPY: // '\t'
					dMove(userPoints, true);
					pointsTouched = 3;
					break;
					
				case H_MOVE:
					dMove(userPoints, false);
					pointsTouched = 3;
					break;

				case H_FILL: // '\007'
					dFill(x, y);
					pointsTouched = 1;
					break;

				case H_L: // '\016'
					int _tmp = info.W * info.H;
					switch (y) {
						default:
							break;

						case 0: // '\0'
							ch(iLayerSrc, iLayer);
							break;

						case 1: // '\001'
							info.setL(userPoints[1]);
							break;

						case 2: // '\002'
							info.delL(iLayerSrc);
							break;

						case 3: // '\003'
							if (iLayer > iLayerSrc) {
								for (int l1 = iLayerSrc; l1 < iLayer; l1++)
									ch(l1, l1 + 1);

							}
							if (iLayer >= iLayerSrc)
								break;
							for (int i2 = iLayerSrc; i2 > iLayer; i2--)
								ch(i2, i2 - 1);

							break;

						case 4: // '\004'
							int ai2[][] = info.getOffset();
							System.arraycopy(ai2[iLayerSrc], 0, ai2[iLayer], 0, ai2[0].length);
							break;

						case 5: // '\005'
							info.visit[iLayerSrc] = (float) iAlpha / 255F;
							break;
					}
					setD(0, 0, info.W, info.H);
					break;

				default:
					dRect(x, y, userPoints[1] >> 16, (short) userPoints[1]);
					pointsTouched = 2;
					break;
			}
			if (hasParamPoints) {
				ByteStream bytestream = getWork();
				for (int j2 = 0; j2 < pointsTouched; j2++)
					bytestream.w(userPoints[j2], 4);

				if (iHint == 8)
					bytestream.write((info.textOption + '\0' + info.text).getBytes("UTF8"));
			}
			if (user.wait >= 0) {
				dFlush();
				dBuffer();
				setD(0, 0, 0, 0);
				user.isDirect = true;
			}
			else {
				user.isDirect = false;
			}
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void dStart() {
		try {
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
			if (this.iSOB != 0) {
				int k = ru();
				this.iSize = ss(k);
				this.iAlpha = sa(k);
			}
			setD(i, j, i + this.user.pW, j + this.user.pW);
		}
		catch (RuntimeException localRuntimeException) {
			localRuntimeException.printStackTrace();
		}
	}

	public void dStart(int x, int y, int pSens, boolean writeOut, boolean needsScaling) {
		try {
			this.user.setup(this);
			this.iSize = ss(pSens);
			this.iAlpha = sa(pSens);
			this.user.setup(this);
			if (needsScaling) {
				int i = this.info.scale;
				x = (x / i + this.info.scaleX) * this.info.Q;
				y = (y / i + this.info.scaleY) * this.info.Q;
			}
			if (writeOut) {
				ByteStream localByteStream = getWork();
				localByteStream.w(x, 2);
				localByteStream.w(y, 2);
				if (this.iSOB != 0)
					localByteStream.write(pSens);
			}

			this.user.pY2 = -1000;
			this.user.pX2 = -1000;

			this.user.oX = x;
			this.user.pX = x;
			this.user.fX = x;
			this.user.oY = y;
			this.user.pY = y;
			this.user.fY = y;
			int pRadius = this.user.pW / 2;
			setD(x - pRadius, y - pRadius, x + pRadius, y + pRadius);
		}
		catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	private void dText(String s1, String s2, int i, int j) {
		try {
			int k = info.W;
			int l = info.H;
			int ai[] = info.iOffs[iLayer];
			byte abyte0[] = info.iMOffs;
			float f = b255[iAlpha];
			if (f == 0.0F)
				return;
			Font font = new Font("sanssefit", 0, iSize);
			FontMetrics fontmetrics = info.component.getFontMetrics(font);
			if (s1 == null || s1.length() <= 0)
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
			int ai1[] = (int[]) pixelgrabber.getPixels();
			pixelgrabber = null;
			image.flush();
			image = null;
			boolean flag = false;
			int j2 = Math.min(k - i, i1);
			int k2 = Math.min(l - j, j1);
			for (int l2 = 0; l2 < k2; l2++) {
				int l1 = l2 * i1;
				int i2 = (l2 + j) * k + i;
				for (int i3 = 0; i3 < j2; i3++) {
					if (!isM(ai[i2]))
						abyte0[i2] = (byte) (int) ((float) (ai1[l1] & 0xff) * f);
					l1++;
					i2++;
				}

			}

			setD(i, j, i + i1, j + j1);
			t();
		}
		catch (InterruptedException _ex) {
		}
		catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
		}
	}

	public final void get(OutputStream outputstream, ByteStream bytestream, Mg mg) {
		try {
			bytestream.reset();
			int i = 0;
			boolean flag = false;
			int j = getFlag(mg);
			int k = j >>> 8 & 0xff;
			int l = j & 0xff;
			bytestream.write(j >>> 16);
			bytestream.write(k);
			bytestream.write(l);
			if ((k & 1) != 0) {
				i = iHint;
				flag = true;
			}
			if ((k & 2) != 0) {
				if (flag)
					bytestream.write(i << 4 | iPenM);
				else
					i = iPenM;
				flag = !flag;
			}
			if ((k & 4) != 0) {
				if (flag)
					bytestream.write(i << 4 | iMask);
				else
					i = iMask;
				flag = !flag;
			}
			if (flag)
				bytestream.write(i << 4);
			if ((k & 8) != 0)
				bytestream.write(iPen);
			if ((k & 0x10) != 0)
				bytestream.write(iTT);
			if ((k & 0x20) != 0)
				bytestream.write(iLayer);
			if ((k & 0x40) != 0)
				bytestream.write(iLayerSrc);
			if ((l & 1) != 0)
				bytestream.write(iAlpha);
			if ((l & 2) != 0)
				bytestream.w(iColor, 3);
			if ((l & 4) != 0)
				bytestream.w(iColorMask, 3);
			if ((l & 8) != 0)
				bytestream.write(iSize);
			if ((l & 0x10) != 0)
				bytestream.write(iCount);
			if ((l & 0x20) != 0)
				bytestream.w(iSA, 2);
			if ((l & 0x40) != 0)
				bytestream.w(iSS, 2);
			if (iPen == 20)
				bytestream.w2(iAlpha2);
			if (offset != null && iOffset > 0)
				bytestream.write(offset, 0, iOffset);
			outputstream.write(bytestream.size() >>> 8);
			outputstream.write(bytestream.size() & 0xff);
			bytestream.writeTo(outputstream);
		}
		catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
		catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
		}
	}

	private final int getFlag(Mg mg) {
		int j = 0;
		if (isAllL)
			j |= 1;
		if (isAFix)
			j |= 2;
		if (isAnti)
			j |= 0x10;
		if (isCount)
			j |= 8;
		if (isOver)
			j |= 4;
		j |= iSOB << 6;
		int i = j << 16;
		if (mg == null)
			return i | 0xffff;
		j = 0;
		if (iHint != mg.iHint)
			j |= 1;
		if (iPenM != mg.iPenM)
			j |= 2;
		if (iMask != mg.iMask)
			j |= 4;
		if (iPen != mg.iPen)
			j |= 8;
		if (iTT != mg.iTT)
			j |= 0x10;
		if (iLayer != mg.iLayer)
			j |= 0x20;
		if (iLayerSrc != mg.iLayerSrc)
			j |= 0x40;
		i |= j << 8;
		j = 0;
		if (iAlpha != mg.iAlpha)
			j |= 1;
		if (iColor != mg.iColor)
			j |= 2;
		if (iColorMask != mg.iColorMask)
			j |= 4;
		if (iSize != mg.iSize)
			j |= 8;
		if (iCount != mg.iCount)
			j |= 0x10;
		if (iSA != mg.iSA)
			j |= 0x20;
		if (iSS != mg.iSS)
			j |= 0x40;
		return i | j;
	}

	public Image getImage(int layerIndex, int x, int y, int width, int height) {
		x = Math.round(x / info.scale) + info.scaleX;
		y = Math.round(y / info.scale) + info.scaleY;
		width /= info.scale;
		height /= info.scale;
		int j1 = info.Q;
		if (j1 <= 1) {
			return info.component.createImage(new MemoryImageSource(width, height, info.iOffs[layerIndex], y * info.W + x, info.W));
		}
		else {
			Image image = info.component.createImage(new MemoryImageSource(width * j1, height * j1, info.iOffs[layerIndex], y * j1
					* info.W + x * j1, info.W));
			Image image1 = image.getScaledInstance(width, height, 2);
			image.flush();
			return image1;
		}
	}

	private final int getM(int origColor, int mAlpha, int ioffIndex) {
		if (mAlpha == 0)
			return origColor;
		float fAlpha = b255[mAlpha];
		int origA = origColor >>> 24;
		int origR = origColor >>> 16 & 0xff;
		int origG = origColor >>> 8 & 0xff;
		int origB = origColor & 0xff;
		int curColor = iColor;
		switch (iPen) {
			default:
				if (mAlpha == 0) {
					return origColor;
				}
				// old
				// int i2 = isAFix ? origA : origA + (int) ((float) (255 -
				// origA) * fAlpha);
				// fAlpha = b255[Math.min((int) ((float) mAlpha * (255F /
				// (float) i2)), 255)];

				// float float6 = b255[Math.min( (int) ((float) int3 *
				// b255d[int5]), 255 )];
				int i2 = origA + (int) ((float) mAlpha * this.b255[255 - origA]);
				fAlpha = b255[Math.min((int) ((float) mAlpha * b255d[i2]), 255)];
				return (i2 << 24) + (origR + (int) ((float) ((curColor >>> 16 & 0xff) - origR) * fAlpha) << 16)
						+ (origG + (int) ((float) ((curColor >>> 8 & 0xff) - origG) * fAlpha) << 8)
						+ (origB + (int) ((float) ((curColor & 0xff) - origB) * fAlpha));

			case P_WHITE: // '\004'
			case P_SWHITE: // '\005'
				int j2 = origA - (int) ((float) origA * fAlpha);
				return j2 != 0 ? j2 << 24 | origColor & 0xffffff : 0xffffff;

			case P_LIGHT: // '\006'
				return (origA << 24) + (Math.min(origR + (int) ((float) origR * fAlpha), 255) << 16)
						+ (Math.min(origG + (int) ((float) origG * fAlpha), 255) << 8)
						+ Math.min(origB + (int) ((float) origB * fAlpha), 255);

			case P_DARK: // '\007'
				return (origA << 24) + (Math.max(origR - (int) ((float) (255 - origR) * fAlpha), 0) << 16)
						+ (Math.max(origG - (int) ((float) (255 - origG) * fAlpha), 0) << 8)
						+ Math.max(origB - (int) ((float) (255 - origB) * fAlpha), 0);

			case P_BOKASHI: // '\b'
				int ai[] = user.argb;
				int ai1[] = info.iOffs[iLayer];
				int k3 = info.W;
				for (int l3 = 0; l3 < 4; l3++)
					ai[l3] = 0;

				int l2 = ioffIndex % k3;
				ioffIndex += l2 != 0 ? l2 != k3 - 1 ? 0 : -1 : 1;
				ioffIndex += ioffIndex >= k3 ? ioffIndex <= k3 * (info.H - 1) ? 0 : -k3 : k3;
				for (int i4 = -1; i4 < 2; i4++) {
					for (int k4 = -1; k4 < 2; k4++) {
						int i3 = ai1[ioffIndex + k4 + i4 * k3];
						int i1 = i3 >>> 24;
						for (int l4 = 0; l4 < 4; l4++)
							ai[l4] += i3 >>> (l4 << 3) & 0xff;

					}

				}

				for (int j4 = 0; j4 < 4; j4++) {
					int j3 = origColor >>> (j4 << 3) & 0xff;
					ai[j4] = j3 + (int) ((float) (ai[j4] / 9 - j3) * fAlpha);
				}

				return ai[3] << 24 | ai[2] << 16 | ai[1] << 8 | ai[0];

			case P_MOSAIC: // '\t'
			case P_FUSION: // '\024'
				if (mAlpha == 0)
					return origColor;
				else
					return mAlpha << 24 | 0xff0000;

			case P_FILL: // '\n'
				return mAlpha << 24 | curColor;
		}
	}

	public final byte[] getOffset() {
		return offset;
	}

	private final int[] getPM() {
		if (iHint == H_TEXT)
			return null;
		int ai[] = user.p;
		if (user.pM != iPenM || user.pA != iAlpha || user.pS != iSize) {
			int ai1[][] = info.bPen[iPenM];
			int ai2[] = ai1[iSize];
			int i = ai2.length;
			if (ai == null || ai.length < i)
				ai = new int[i];
			float f = b255[iAlpha];
			for (int j = 0; j < i; j++)
				ai[j] = (int) ((float) ai2[j] * f);

			user.pW = (int) Math.sqrt(i);
			user.pM = this.iPenM;
			user.pA = this.iAlpha;
			user.pS = this.iSize;
			user.p = ai;
			user.countMax = (this.iCount >= 0) ? this.iCount : (int) (user.pW
					/ (float) Math.sqrt(ai1[(ai1.length - 1)].length) * -this.iCount);
			user.count = Math.min(user.count, user.countMax);
		}
		return ai;
	}

	private final float getTT(int i, int j) {
		if (iTT == 0)
			return 1.0F;
		if (iTT < 12) {
			return (float) (isTone(iTT - 1, i, j) ? 0 : 1);
		}
		else {
			int k = user.pTTW;
			return user.pTT[(j % k) * k + i % k];
		}
	}

	private final ByteStream getWork() {
		info.workOut.reset();
		return info.workOut;
	}

	private final boolean isM(int i) {
		if (iMask == 0) {
			return false;
		}
		else {
			i &= 0xffffff;
			return iMask != 1 ? iMask != 2 ? false : iColorMask != i : iColorMask == i;
		}
	}

	public static final boolean isTone(int i, int j, int k) {
		switch (i) {
			default:
				break;

			case 10: // '\n'
				if ((j + 3) % 4 == 0 && (k + 2) % 4 == 0)
					return true;
				break;

			case 9: // '\t'
				if ((j + 1) % 4 == 0 && (k + 2) % 4 == 0)
					break;
				// fall through

			case 8: // '\b'
				if (j % 2 != 0 && (k + 1) % 2 != 0)
					return true;
				break;

			case 7: // '\007'
				if ((j + 2) % 4 == 0 && (k + 3) % 4 == 0)
					break;
				// fall through

			case 6: // '\006'
				if (j % 4 == 0 && (k + 1) % 4 == 0)
					break;
				// fall through

			case 5: // '\005'
				if ((j + 1) % 2 != (k + 1) % 2)
					return true;
				break;

			case 4: // '\004'
				if ((j + 1) % 4 == 0 && (k + 3) % 4 == 0)
					break;
				// fall through

			case 3: // '\003'
				if (j % 2 != 0 || k % 2 != 0)
					return true;
				break;

			case 2: // '\002'
				if ((j + 2) % 4 == 0 && (k + 4) % 4 == 0)
					break;
				// fall through

			case 1: // '\001'
				if ((j + 2) % 4 == 0 && (k + 2) % 4 == 0)
					break;
				// fall through

			case 0: // '\0'
				if (j % 4 != 0 || k % 4 != 0)
					return true;
				break;
		}
		return false;
	}

	private int[] loadIm(Object obj, boolean flag) {
		try {
			Component component = info.component;
			Image image = component.getToolkit().createImage((byte[]) info.cnf.getRes(obj));
			info.cnf.remove(obj);
			Awt.wait(image);
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, image.getWidth(null), image.getHeight(null), true);
			pixelgrabber.grabPixels();
			int ai[] = (int[]) pixelgrabber.getPixels();
			int i = ai.length;
			image.flush();
			image = null;
			if (flag) {
				for (int j = 0; j < i; j++)
					ai[j] = ai[j] & 0xff ^ 0xff;

			}
			else {
				for (int k = 0; k < i; k++)
					ai[k] &= 0xff;

			}
			return ai;
		}
		catch (RuntimeException _ex) {
		}
		catch (InterruptedException _ex) {
		}
		return null;
	}

	public final void m_paint(int x, int y, int w, int h) {
		int i1 = info.scale;
		int j1 = info.Q;
		x = (x / i1 + info.scaleX) * j1;
		y = (y / i1 + info.scaleY) * j1;
		w = (w / i1) * j1;
		h = (h / i1) * j1;
		dBuffer(false, x, y, x + w, y + h);
	}

	public final void memset(float af[], float f) {
		int i = af.length;
		for (int j = 0; j < i; j++)
			af[j] = f;

	}

	public final void memset(int ai[], int i) {
		int j = ai.length;
		for (int k = 0; k < j; k++)
			ai[k] = i;

	}

	public final Image mkLPic(int buffer[], int x, int y, int w, int h, int scale) {
		x *= scale;
		y *= scale;
		w *= scale;
		h *= scale;
		int infoL = info.L;
		int _tmp = scale * scale;
		float infoVisit[] = info.visit;
		int k2 = 0;
		int yrange = y + h;
		int infoW = info.W;
		int infoIOffs[][] = info.iOffs;
		int _tmp1 = infoW * info.H;
		boolean emptyBuffer = buffer == null;
		float[] _tmp2 = b255;
		if (emptyBuffer)
			buffer = user.buffer;
		for (; y < yrange; y++) {
			int offsIndex = infoW * y + x;
			for (int offsIndexMaxWidth = offsIndex + w; offsIndex < offsIndexMaxWidth; offsIndex++) {
				int fint = 0xffffff;
				for (int layerIndex = 0; layerIndex < infoL; layerIndex++) {
					int l1 = infoIOffs[layerIndex][offsIndex];
					float f = b255[l1 >>> 24] * infoVisit[layerIndex];
					fint = f != 1.0F ? f != 0.0F ? ((fint & 0xff0000)
							+ (int) ((float) ((l1 & 0xff0000) - (fint & 0xff0000)) * f) & 0xff0000)
							+ ((fint & 0xff00) + (int) ((float) ((l1 & 0xff00) - (fint & 0xff00)) * f) & 0xff00)
							+ ((fint & 0xff) + (int) ((float) ((l1 & 0xff) - (fint & 0xff)) * f)) : fint : l1;
				}

				buffer[k2++] = fint;
			}

		}

		if (emptyBuffer)
			user.raster.newPixels(user.image, w, h, scale);
		else
			user.raster.scale(buffer, w, h, scale);
		return user.image;
	}

	private final Image mkMPic(int x, int y, int w, int h, int scale) {
		x *= scale;
		y *= scale;
		w *= scale;
		h *= scale;
		int infoL = info.L;
		int layerCurrent = iLayer;
		int _tmp = scale * scale;
		float infoVisit[] = info.visit;
		int i3 = 0;
		int yrange = y + h;
		int infoW = info.W;
		int infoIOffs[][] = info.iOffs;
		int _tmp1 = infoW * info.H;
		int userBuffer[] = user.buffer;
		float[] _tmp2 = b255;
		byte infoIMOffs[] = info.iMOffs;
		for (; y < yrange; y++) {
			int l2 = infoW * y + x;
			for (int j3 = l2 + w; l2 < j3; l2++) {
				int k2 = infoIMOffs[l2] & 0xff;
				int color = 0xffffff;
				for (int layerIndex = 0; layerIndex < infoL; layerIndex++) {
					int pixel = layerIndex == layerCurrent ? getM(infoIOffs[layerIndex][l2], k2, l2)
							: infoIOffs[layerIndex][l2];
					float alpha = b255[pixel >>> 24] * infoVisit[layerIndex];
					color = alpha != 1.0F ? alpha != 0.0F ? ((color & 0xff0000)
							+ (int) ((float) ((pixel & 0xff0000) - (color & 0xff0000)) * alpha) & 0xff0000)
							+ ((color & 0xff00) + (int) ((float) ((pixel & 0xff00) - (color & 0xff00)) * alpha) & 0xff00)
							+ ((color & 0xff) + (int) ((float) ((pixel & 0xff) - (color & 0xff)) * alpha))
							: color
							: pixel;
				}

				userBuffer[i3++] = color;
			}

		}

		user.raster.newPixels(user.image, w, h, scale);
		return user.image;
	}

	/*
	 * public Info newInfo(Applet Applet1, Component Component2, Res Res3) { if(
	 * info == null ) { Info Info4; M M5; int int6; int int7; int int8; int
	 * int9; float float10; int[][][] int_3darray11; int[][] int_2darray12;
	 * int[] int_1darray13; int int15; int int17; int int18; int int19; String
	 * String28;
	 * 
	 * info = new Info(); Info.access$7( info, Res3 ); Info.access$8( info,
	 * Component2 ); Info4 = info; M5 = info.m; float10 = 3.1415927410125732F;
	 * for( int6 = 1; int6 < 256; ++int6 ) { b255[int6] = (float) int6 / 255.0F;
	 * b255d[int6] = 255.0F / (float) int6; } b255[0] = 0.0F; b255d[0] = 0.0F;
	 * int_3darray11 = bPen; int15 = 0; int17 = 1; int19 = 255; M5.iAlpha = 255;
	 * set( M5 ); int_2darray12 = new int[23][]; for( int6 = 0; int6 < 23;
	 * ++int6 ) { int8 = int17 * int17; if( int17 <= 6 ) { int_2darray12[int6] =
	 * int_1darray13 = new int[int8]; int18 = int17; for( int7 = 0; int7 < int8;
	 * ++int7 ) int_1darray13[int7] = (int7 < int17 || int8 - int7 < int17 ||
	 * int7 % int17 == 0 || int7 % int17 == int17 - 1) ? int19 : M5.iAlpha; if(
	 * int17 >= 3 ) int_1darray13[0] = int_1darray13[int17 - 1] =
	 * int_1darray13[int17 * (int17 - 1)] = int_1darray13[int8 - 1] = 0; } else
	 * { int int16;
	 * 
	 * int18 = int17 + 1; int_2darray12[int6] = int_1darray13 = new int[int18 *
	 * int18]; int16 = (int17 - 1) / 2; int9 = (int) ((float) Math.round( 2.0F *
	 * float10 * (float) int16 ) * 3.0F); for( int7 = 0; int7 < int9; ++int7 ) {
	 * int int14 = Math.min( int16 + (int) Math.round( (double) int16 *
	 * Math.cos( (double) int7 ) ), int17 );
	 * 
	 * int15 = Math.min( int16 + (int) Math.round( (double) int16 * Math.sin(
	 * (double) int7 ) ), int17 ); int_1darray13[int15 * int18 + int14] = int19;
	 * } Info4.W = Info4.H = int18; dFill( int_1darray13, 0, 0, int18, int18 );
	 * } int17 += (int6 <= 7) ? 1 : (int6 < 18) ? 2 : 4; } int_3darray11[0] =
	 * int_2darray12; M5.iAlpha = 255; int_2darray12 = new int[32][];
	 * int_2darray12[0] = new int[] { 128 }; int_2darray12[1] = new int[] { 255
	 * }; int_2darray12[2] = new int[] { 0, 128, 0, 128, 255, 128, 0, 128, 0 };
	 * int_2darray12[3] = new int[] { 128, 174, 128, 174, 255, 174, 128, 174,
	 * 128 }; int_2darray12[4] = new int[] { 174, 255, 174, 255, 255, 255, 174,
	 * 255, 174 }; int_2darray12[5] = new int[9]; memset( int_2darray12[5], 255
	 * ); int_2darray12[6] = new int[] { 0, 128, 128, 0, 128, 255, 255, 128,
	 * 128, 255, 255, 128, 0, 128, 128, 0 }; int_1darray13 = int_2darray12[7] =
	 * new int[16]; memset( int_1darray13, 255 ); int_1darray13[0] =
	 * int_1darray13[3] = int_1darray13[15] = int_1darray13[12] = 128; memset(
	 * int_2darray12[8] = new int[16], 255 ); int17 = 3; for( int6 = 9; int6 <
	 * 32; ++int6 ) { float float24;
	 * 
	 * int18 = int17 + 3; float24 = (float) int17 / 2.0F; int_2darray12[int6] =
	 * int_1darray13 = new int[int18 * int18]; int9 = (int) ((float) Math.round(
	 * 2.0F * float10 * float24 ) * (float) (2 + int6 / 16)) + int6 * 2; for(
	 * int7 = 0; int7 < int9; ++int7 ) { float float20; int int25 = (int)
	 * (float20 = float24 + 1.5F + float24 * (float) Math.cos( (double) int7 ));
	 * float float21; int int26 = (int) (float21 = float24 + 1.5F + float24 *
	 * (float) Math.sin( (double) int7 )); float float22 = float20 - (float)
	 * int25; float float23 = float21 - (float) int26; int int27 = int26 * int18
	 * + int25;
	 * 
	 * int_1darray13[int27] += (int) ((1.0F - float22) * 255.0F);
	 * int_1darray13[int27 + 1] += (int) (float22 * 255.0F); int_1darray13[int27
	 * + int18] += (int) ((1.0F - float23) * 255.0F); int_1darray13[int27 +
	 * int18 + 1] += (int) (float23 * 255.0F); } int8 = int18 * int18; for( int7
	 * = 0; int7 < int8; ++int7 ) int_1darray13[int7] = Math.min(
	 * int_1darray13[int7], 255 ); int17 += 2; Info4.W = Info4.H = int18; dFill(
	 * int_1darray13, 0, 0, int18, int18 ); } int_3darray11[1] = int_2darray12;
	 * set( null ); M5.set( null ); if( Res3 != null ) { for( int6 = 0; int6 <
	 * 16; ++int6 ) { for( int9 = 0; Res3.get( "pm" + int6 + '/' + int9 + ".gif"
	 * ) != null; ++int9 ) ; if( int9 > 0 ) { int_3darray11[int6] = new
	 * int[int9][]; for( int7 = 0; int7 < int9; ++int7 )
	 * int_3darray11[int6][int7] = loadIm( "pm" + int6 + '/' + int7 + ".gif",
	 * true ); } } Info.access$9( info, new float[Res3.getP( "tt_size", 31 )][]
	 * ); } String28 = Applet1.getParameter( "tt.zip" ); if( String28 != null &&
	 * String28.length() > 0 ) Info.access$10( info, String28 ); } return info;
	 * }
	 */

	public Info newInfo(Applet applet, Component component, Res res) {
		if (info != null)
			return info;
		info = new Info();
		info.cnf = res;
		info.component = component;
		Info info1 = info;
		Mg mg = info.m;
		for (int i = 1; i < 256; i++) {
			b255[i] = (float) i / 255F;
			b255d[i] = 255.0F / (float) i;
		}

		b255[0] = 0.0F;
		int bPenA[][][] = info.bPen;
		boolean flag = false;

		int brushLength = 1;
		int pixelStrength = 255;
		mg.iAlpha = 255;
		for (int penModeIndex = 0; penModeIndex < 2; penModeIndex++) {
			set(mg);
			int penModeBytes[][] = new int[23][];
			for (int pSizeIndex = 0; pSizeIndex < 23; pSizeIndex++) {
				int brushPixels[];
				penModeBytes[pSizeIndex] = brushPixels = new int[brushLength * brushLength];
				// if brush length <= 7, then fill in entire area with pixels
				if (brushLength <= 7) {
					int brushArea = brushLength * brushLength;
					for (int l = 0; l < brushArea; l++)
						brushPixels[l] = pixelStrength;

					// if pixels greater than 3, set four corners 0
					if (brushLength >= 3)
						brushPixels[0] = brushPixels[brushLength - 1] = brushPixels[brushLength * (brushLength - 1)] = brushPixels[brushArea - 1] = 0;
				}
				else {
					int radius = brushLength / 2;
					int circumference = (int) (6.2831853071795862D * (double) radius);
					int l1 = circumference * 4;
					for (int i1 = 0; i1 < l1; i1++) {
						int xPoint = Math.min(radius + (int) Math.round((double) radius * Math.cos(i1)),
								brushLength - 1);
						int yPoint = Math.min(radius + (int) Math.round((double) radius * Math.sin(i1)),
								brushLength - 1);
						brushPixels[yPoint * brushLength + xPoint] = pixelStrength;
					}

					info1.W = info1.H = brushLength;
					dFill(brushPixels, 0, 0, brushLength, brushLength);
				}
				// brushlength = (
				brushLength += pSizeIndex >= 6 ? pSizeIndex >= 18 ? 4 : 2 : 1;
			}

			bPenA[penModeIndex] = penModeBytes;
			mg.iAlpha = 110;
			pixelStrength = 80;
			brushLength = 1;
		}

		// THE FOLLOWING SETS INDEXES 2, 3 FOR PENS OF AIRBRUSH AND TEXTURE MODES
		set(((Mg) (null)));
		mg.set(((Mg) (null)));
		if (res != null) {
			for (int penModeIndex2 = 0; penModeIndex2 < 16; penModeIndex2++) {
				int i2;
				// count number of gifs in package
				for (i2 = 0; res.containsKey("pm" + penModeIndex2 + '/' + i2 + ".gif"); i2++) {
					;
				}
				// load bytes
				if (i2 > 0) {
					PCDebug.println("found pm: " + penModeIndex2);
					bPenA[penModeIndex2] = new int[i2][];
					for (int j1 = 0; j1 < i2; j1++)
						bPenA[penModeIndex2][j1] = loadIm("pm" + penModeIndex2 + '/' + j1 + ".gif", true);

				}
			}

			info.bTT = new float[res.getP("tt_size", 31)][];
		}
		
		// create indexes for brushes 4-7
		BrushFactory fac = new BrushFactory();
		createBrushMode(PM_HARDEST, 0.05, fac);
		createBrushMode(PM_HARD, 0.33D, fac);
		createBrushMode(PM_SOFT, 0.66D, fac);
		createBrushMode(PM_SOFTEST, 1.0D, fac);
		
		// create the existing sizes for index 0-3
		
		for(int i=0; i < 4; i++) {
			float [] sizes = new float[bPenA[i].length];
			for(int j=0; j < bPenA[i].length; j++) {
				sizes[j] = (float) Math.sqrt(bPenA[i][j].length);
			}
			info.bPenSize[i] = sizes;
		}
		
		String s1 = applet.getParameter("tt.zip");
		if (s1 != null && s1.length() > 0)
			info.dirTT = s1;
		return info;
	}
	
	private void createBrushMode(int penModeIndex, double softness, BrushFactory fac) {
		int brushCount = 40;
		int[][] brushMode = new int[brushCount][];
		float [] sizes = new float[brushCount];
		for(int i=0; i < brushCount; i++) {
			double brushSize = (i + 1) * 0.25;
			brushMode[i] = fac.createBrush(brushSize, softness);
			sizes[i] = (float)brushSize;
		}
		
		brushMode[brushCount-1] = fac.createBrush(40, softness);
		sizes[brushCount-1] = 40;
		
		info.bPen[penModeIndex] = brushMode;
		info.bPenSize[penModeIndex] = sizes;
	}
	
	

	public User newUser(Component component) {
		if (user == null) {
			user = new User();
			if (color_model == null)
				color_model = new DirectColorModel(24, 0xff0000, 65280, 255);
			// User.access$raster(this.user, new SRaster(color_model,
			// User.access$buffer(this.user), 128, 128));
			// User.access$image(this.user,
			// paramComponent.createImage(User.access$raster(this.user)));
			user.raster = new SRaster(color_model, user.buffer, 128, 128);
			user.image = component.createImage(user.raster);
		}
		return user;
	}

	public final int pix(int i, int j) {
		if (!isAllL)
			return info.iOffs[iLayer][j * info.W + i];
		int k = info.L;
		int i1 = 0;
		int k1 = 0xffffff;
		int i2 = info.W * j + i;
		for (int j2 = 0; j2 < k; j2++) {
			int l1 = info.iOffs[j2][i2];
			float f = b255[l1 >>> 24];
			if (f != 0.0F) {
				if (f == 1.0F) {
					k1 = l1;
					i1 = 255;
				}
				i1 = (int) ((float) i1 + (float) (255 - i1) * f);
				int l = 0;
				for (int k2 = 16; k2 >= 0; k2 -= 8) {
					int j1 = k1 >>> k2 & 0xff;
					l |= j1 + (int) ((float) ((l1 >>> k2 & 0xff) - j1) * f) << k2;
				}

				k1 = l;
			}
		}

		return i1 << 24 | k1;
	}

	private final byte r() {
		return offset[iSeek++];
	}

	private final int r(byte abyte0[], int i, int j) {
		int k = 0;
		for (int l = j - 1; l >= 0; l--)
			k |= (abyte0[i++] & 0xff) << l * 8;

		return k;
	}

	private final short r2() {
		return (short) ((ru() << 8) + ru());
	}

	public void reset() {
		byte abyte0[] = info.iMOffs;
		int j = info.W;
		int k = Math.max(user.dX, 0);
		int l = Math.max(user.dY, 0);
		int i1 = Math.min(user.dW, j);
		int j1 = Math.min(user.dH, info.H);
		for (int l1 = l; l1 < j1; l1++) {
			int i = k + l1 * j;
			for (int k1 = k; k1 < i1; k1++)
				abyte0[i++] = 0;

		}

		dBuffer(false, k, l, i1, j1);
		setD(0, 0, 0, 0);
	}

	private final int rPo() {
		byte byte0 = r();
		return byte0 == -128 ? r2() : byte0;
	}

	private final int ru() {
		return r() & 0xff;
	}

	private final int s(int i, int j, int k) {
		byte abyte0[] = info.iMOffs;
		int l = info.W - 1;
		for (int i1 = (l + 1) * k + j; j < l && pix(j + 1, k) == i && abyte0[i1 + 1] == 0; j++)
			i1++;

		return j;
	}

	private final int sa(int i) {
		if ((iSOB & 1) == 0) {
			return iAlpha;
		}
		else {
			int min = iSA & 0xff;
			int max = Math.max(iSA >>> 8 & 0xff, min);
			return min + (int) (b255[max - min] * (float) i);
		}
	}

	public final int set(byte buffer[], int i) {
		int j = (buffer[i++] & 0xff) << 8 | buffer[i++] & 0xff;
		int k = i;
		if (j <= 2)
			return j + 2;
		try {
			int l = 0;
			boolean flag = false;
			int i1 = buffer[i++] & 0xff;
			int j1 = buffer[i++] & 0xff;
			int k1 = buffer[i++] & 0xff;
			isAllL = (i1 & 1) != 0;
			isAFix = (i1 & 2) != 0;
			isOver = (i1 & 4) != 0;
			isCount = (i1 & 8) != 0;
			isAnti = (i1 & 0x10) != 0;
			iSOB = i1 >>> 6;
			if ((j1 & 1) != 0) {
				l = buffer[i++] & 0xff;
				flag = true;
				iHint = l >>> 4;
			}
			if ((j1 & 2) != 0) {
				if (!flag) {
					l = buffer[i++] & 0xff;
					iPenM = l >>> 4;
				}
				else {
					iPenM = l & 0xf;
				}
				flag = !flag;
			}
			if ((j1 & 4) != 0) {
				if (!flag) {
					l = buffer[i++] & 0xff;
					iMask = l >>> 4;
				}
				else {
					iMask = l & 0xf;
				}
				flag = !flag;
			}
			if ((j1 & 8) != 0)
				iPen = buffer[i++] & 0xff;
			if ((j1 & 0x10) != 0)
				iTT = buffer[i++] & 0xff;
			if ((j1 & 0x20) != 0)
				iLayer = buffer[i++] & 0xff;
			if ((j1 & 0x40) != 0)
				iLayerSrc = buffer[i++] & 0xff;
			if ((k1 & 1) != 0)
				iAlpha = buffer[i++] & 0xff;
			if ((k1 & 2) != 0) {
				iColor = r(buffer, i, 3);
				i += 3;
			}
			if ((k1 & 4) != 0) {
				iColorMask = r(buffer, i, 3);
				i += 3;
			}
			if ((k1 & 8) != 0)
				iSize = buffer[i++] & 0xff;
			if ((k1 & 0x10) != 0)
				iCount = buffer[i++];
			if ((k1 & 0x20) != 0) {
				iSA = r(buffer, i, 2);
				i += 2;
			}
			if ((k1 & 0x40) != 0) {
				iSS = r(buffer, i, 2);
				i += 2;
			}
			if (iPen == 20) {
				iAlpha2 = r(buffer, i, 2);
				i += 2;
			}
			k = j - (i - k);
			if (k > 0) {
				if (offset == null || offset.length < k)
					offset = new byte[k];
				iOffset = k;
				System.arraycopy(buffer, i, offset, 0, k);
			}
			else {
				iOffset = 0;
			}
		}
		catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
			iOffset = 0;
		}
		return j + 2;
	}

	public final void set(String s1) {
		try {
			if (s1 == null || s1.length() == 0)
				return;
			Field afield[] = getClass().getDeclaredFields();
			int i = s1.indexOf('@');
			if (i < 0)
				i = s1.length();
			int k;
			for (int j = 0; j < i; j = k + 1) {
				k = s1.indexOf('=', j);
				if (k == -1)
					break;
				String s2 = s1.substring(j, k);
				j = k + 1;
				k = s1.indexOf(';', j);
				if (k < 0)
					k = i;
				try {
					for (int l = 0; l < afield.length; l++) {
						Field field = afield[l];
						if (!field.getName().equals(s2))
							continue;
						String s3 = s1.substring(j, k);
						Class class1 = field.getType();
						if (class1.equals(Integer.TYPE))
							field.setInt(this, Integer.parseInt(s3));
						else if (class1.equals(Boolean.TYPE))
							field.setBoolean(this, Integer.parseInt(s3) != 0);
						else
							field.set(this, s3);
						break;
					}

				}
				catch (NumberFormatException _ex) {
				}
				catch (IllegalAccessException _ex) {
				}
			}

			if (i != s1.length()) {
				ByteStream bytestream = getWork();
				for (int i1 = i + 1; i1 < s1.length(); i1 += 2)
					bytestream.write(Character.digit(s1.charAt(i1), 16) << 4 | Character.digit(s1.charAt(i1 + 1), 16));

				offset = bytestream.toByteArray();
				iOffset = offset.length;
			}
		}
		catch (Throwable _ex) {
		}
	}

	public final void set(XMLElement e) {
		iHint = e.getIntAttribute("IHINT", iHint);
		iPen = e.getIntAttribute("IPEN", iPen);
		iPenM = e.getIntAttribute("IPENM", iPenM);
		iTT = e.getIntAttribute("ITT", iTT);
		iMask = e.getIntAttribute("IMASK", iMask);
		iSize = e.getIntAttribute("ISIZE", iSize);
		iSS = e.getIntAttribute("ISS", iSS);
		iCount = e.getIntAttribute("ICOUNT", iCount);
		isOver = e.getBooleanAttribute("ISOVER", "1", "0", isOver);
		isCount = e.getBooleanAttribute("ISCOUNT", "1", "0", isCount);
		isAFix = e.getBooleanAttribute("ISAFIX", "1", "0", isAFix);
		isAnti = e.getBooleanAttribute("ISANTI", "1", "0", isAnti);
		isAllL = e.getBooleanAttribute("ISALLL", "1", "0", isAllL);
		iAlpha = e.getIntAttribute("IALPHA", iAlpha);
		iAlpha2 = e.getIntAttribute("IALPHA2", iAlpha2);
		iSA = e.getIntAttribute("ISA", iSA);
		iColor = e.getIntAttribute("ICOLOR", iColor);
		iColorMask = e.getIntAttribute("ICOLORMASK", iColorMask);
		iLayer = e.getIntAttribute("ILAYER", iLayer);
		iLayerSrc = e.getIntAttribute("ILAYERSRC", iLayerSrc);
		iSOB = e.getIntAttribute("ISOB", iSOB);
		iOffset = 0;
	}

	public final XMLElement getXML() {
		XMLElement xml = new XMLElement();

		xml.setName("tool");
		xml.setIntAttribute("IHINT", iHint);
		xml.setIntAttribute("IPEN", iPen);
		xml.setIntAttribute("IPENM", iPenM);
		xml.setIntAttribute("ITT", iTT);
		xml.setIntAttribute("IMASK", iMask);
		xml.setIntAttribute("ISIZE", iSize);
		xml.setIntAttribute("ISS", iSS);
		xml.setIntAttribute("ICOUNT", iCount);
		xml.setIntAttribute("ISOVER", isOver ? 1 : 0);
		xml.setIntAttribute("ISCOUNT", isCount ? 1 : 0);
		xml.setIntAttribute("ISAFIX", isAFix ? 1 : 0);
		xml.setIntAttribute("ISANTI", isAnti ? 1 : 0);
		xml.setIntAttribute("ISALLL", isAllL ? 1 : 0);
		xml.setIntAttribute("IALPHA", iAlpha);
		xml.setIntAttribute("IALPHA2", iAlpha2);
		xml.setIntAttribute("ISA", iSA);
		xml.setIntAttribute("ICOLOR", iColor);
		xml.setIntAttribute("ICOLORMASK", iColorMask);
		xml.setIntAttribute("ILAYER", iLayer);
		xml.setIntAttribute("ILAYERSRC", iLayerSrc);
		xml.setIntAttribute("ISOB", iSOB);

		return xml;
	}
	
	public final void setColorAndLayer(Mg mg) {
		iColor = mg.iColor;
        iColorMask = mg.iColorMask;
        iMask = mg.iMask;
        iLayer = mg.iLayer;
	}

	public final void set(Mg mg) {
		if (mg == null)
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

	private final void setD(int dX, int dY, int dW, int dH) {
		user.dX = dX;
		user.dY = dY;
		user.dW = dW;
		user.dH = dH;
	}

	public void setInfo(Info info1) {
		info = info1;
	}

	public void setUser(User user1) {
		user = user1;
	}
	

	/**
	 * Val should be between 0 - 255 <br>
	 * if not min, it will change the max <br>
	 * if not size, it will change alpha
	 * @param val
	 * @param min
	 * @param size
	 */
	public void setSensitivity(int val, boolean min, boolean size) {
		val = Math.min(255, Math.max(0, val));
		int shift = min? 0 : 8;
		if(size) {			
			iSS = iSS & 255 << 8 - shift | val << shift;
		}
		else {
			iSA = iSA & 255 << 8 - shift | val << shift;
		}
	}
	
	/**
	 * Get the sensitivty value
	 * @param min
	 * @param size
	 * @return
	 */
	public int getSensitivity(boolean min, boolean size) {
		int sens = size? iSS : iSA;
		return min? sens & 0xff : sens >>> 8 & 0xff;		
	}
	
	public boolean hasSensitivity(boolean size) {
		return (iSOB & (size? SB_SIZE : SB_ALPHA)) != 0;
	}
	
	public void enableSensitivity(boolean size, boolean enabled) {
		// note to self: fuck my life, this shit is ridiculous tedious
		iSOB = iSOB & 1 << (size? 0 : 1) | (enabled? 1 : 0) << (size? 1 : 0);
	}			
	
	private final int ss(int i) {
		if ((iSOB & 2) == 0) {
			return iSize;
		}
		else {
			int min = iSS & 0xff;
			int max = Math.max(iSS >>> 8 & 0xff, min);
			return (int) (((float) min + b255[max - min] * (float) i) * user.pV);
		}
	}

	private final void t() {
		if (iTT == 0)
			return;
		byte abyte0[] = info.iMOffs;
		int k = info.W;
		int l = user.dX;
		int i1 = user.dY;
		int j1 = user.dW;
		int k1 = user.dH;
		for (int l1 = i1; l1 < k1; l1++) {
			int j = k * l1 + l;
			for (int i = l; i < j1; i++)
				abyte0[j] = (byte) (int) ((float) (abyte0[j++] & 0xff) * getTT(i, l1));

		}

	}

	private final void wPo(int i) throws IOException {
		ByteStream bytestream = info.workOut;
		if (i > 127 || i < -127) {
			bytestream.write(-128);
			bytestream.w(i, 2);
		}
		else {
			bytestream.write(i);
		}
	}
	
	public static int toToolType(int hint) {
		int tip = TT_PEN;
		switch(hint) {
			case Mg.H_LINE:
				tip = Mg.TT_SHAPE_STRAIGHTLINE;
				break;
			case Mg.H_RECT:
			case Mg.H_FRECT:
				tip = Mg.TT_SHAPE_RECTANGLE;
				break;
			case Mg.H_OVAL:
			case Mg.H_FOVAL:
				tip = Mg.TT_SHAPE_OVAL;
				break;					
		}
		return tip;
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
	public static final int SB_ALPHA = 0x1;
	public static final int SB_SIZE = 0x2;
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
	public static final int H_MOVE = 11;
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
	public static final int PM_NORMAL = 0;
	public static final int PM_WATERCOLOR = 1;
	public static final int PM_AIRBRUSH = 2;
	public static final int PM_EPEN = 3;
	public static final int PM_SOFTEST = 4;
	public static final int PM_SOFT = 5;
	public static final int PM_HARD = 6;
	public static final int PM_HARDEST = 7;
	public static final int M_N = 0;
	public static final int M_M = 1;
	public static final int M_R = 2;
	public static final int M_ADD = 3;
	public static final int M_SUB = 4;
	private static final int F1O = 4;
	private static final int F1C = 8;
	private static final int F1A = 0x10;
	private static final int F1S = 0x20;
	private static final int F2H = 1;
	private static final int F2PM = 2;
	private static final int F2M = 4;
	private static final int F2P = 8;
	private static final int F2T = 0x10;
	private static final int F2L = 0x20;
	private static final int F2LS = 0x40;
	private static final int F3A = 0x01;
	private static final int F3C = 0x02;
	private static final int F3CM = 0x04;
	private static final int F3S = 0x08;
	private static final int F3E = 0x10;
	private static final int F3SA = 0x20;
	private static final int F3SS = 0x40;
	private static final int DEF_COUNT = -8;
	private static final String ENCODE = "UTF8";
	private static float b255[] = new float[256];
	private static float[] b255d = new float[256];
	private static ColorModel color_model = null;
	private static final Mg mgDef = new Mg();
	public static final int TT_PEN = 0;
	public static final int TT_RETOUCH = 1;
	public static final int TT_DRAGMOVE = 2;
	public static final int TT_EYEDROPPER = 3;
	public static final int TT_SHAPE_STRAIGHTLINE = 4;
	public static final int TT_SHAPE_RECTANGLE = 5;
	public static final int TT_SHAPE_OVAL = 6;
	

}
