// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/26/2009 5:37:40 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Tab.java

package syi.awt;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;

import paintchat.Mg;

// Referenced classes of package syi.awt:
//            LComponent, Awt

public class Tab extends LComponent
{

    public Tab(Container container, paintchat.Mg.Info info)
        throws Throwable
    {
        iDrag = -1;
        max = 0;
        try
        {
            mg = info.m;
            this.info = info;
            int i = sizeBar = (int)(16F * LComponent.Q);
            Dimension dimension = getSize();
            dimension.setSize(i * 4, 8 + i * 6);
            setDimension(dimension, dimension, dimension);
            Class class1 = Class.forName("cello.tablet.JTablet");
            tab = class1.newInstance();
            mGet = class1.getMethod("getPressure", null);
            mPoll = class1.getMethod("poll", null);
            mEx = class1.getMethod("getPressureExtent", null);
            setTitle("tablet");
            container.add(this, 0);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private int at(int i)
    {
        if(i > getSize().height / 2)
            i -= 5;
        return i / sizeBar;
    }
    
    /**
     * Shortcut for fast drawing
     * @param barId
     * @param sens
     * @param minMaxChange
     */
    private void dBar(int barId, int sens) {
    	Graphics g = getGraphics();
        g.translate(getGapX(), getGapY());
        dBar(barId, sens, g);
        g.dispose();
    }

    private void dBar(int barId, int sens, Graphics g)
    {
        Dimension dimension = getSize();
        int size = sizeBar;
        int height = barId * (dimension.height / 2) + size;
        float width = (float)(dimension.width - 6) / 255F;
        int barvalue = barId != 0 ? mg.iSS : mg.iSA;                        
        for(int bartype = 0; bartype < 2; bartype++)
        {            
        	int barlength = (int)((float)(barvalue >>> bartype * 8 & 0xff) * width);
            g.setColor(LComponent.clFrame);
            g.drawRect(2, height, (int)(width * 255F), size);            
            g.setColor(getForeground());
            g.fillRect(3, height + 1, barlength, size - 1);
            g.setColor(getBackground());
            g.fillRect(barlength + 3, height + 1, dimension.width - barlength - 7, size - 1);
            height += size;
        }              
        
        if(sens > 0) {
        	int sensStartX= (int)((float)(barvalue & 0xff) * width);        	
        	int sensPosX = (int)((((float)(barvalue >>> 8 & 0xff) * width) - (float)sensStartX) * (sens / 255F)) + sensStartX;
        	int sensPosY = (int)(barId * (dimension.height / 2) + size) + 1;
        	if(sensPosX > 0) {
        		g.setColor(new Color(204,192,233));
        		g.fillRect(3 + sensPosX, sensPosY, 2, size-2);
        	}
        }
    }

    private void drag(int mX)
    {
    	// ydrag 
    	// 0 = empty space, 
    	// 1 = start alpha, 
    	// 2 = end alpha, 
    	// 3 = space, 
    	// 4 = size start, 
    	// 5 = size end
        int ydrag = iDrag;
        if(ydrag <= 0 || ydrag == 3)
            return;
        boolean isSize = ydrag >= 3;
        if((iSOB & 1 << (isSize ? 1 : 0)) == 0)
            return;
        mX = (int)((255F / (float)getSize().width) * (float)mX);
        mX = mX > 0 ? mX < 255 ? mX : 255 : 0;
        if(isSize)
        {
        	// size = 4 or 5 so k = 0 (start) or 8 (end)
            int k = (ydrag - 4) * 8;
            mg.iSS = mg.iSS & 255 << 8 - k | mX << k;
        } else
        {
        	// alpha = 1 or 2 so l = 0 (start) or 8 (end)
            int l = (ydrag - 1) * 8;
            // if start, move first 4 bits, if end move 2nd 8 bits (0-255, or 256 * 0 - 255)
            mg.iSA = mg.iSA & 255 << 8 - l | mX << l;
        }

        repaint();
    }
    
    public boolean isSensAlpha() {
    	return (iSOB & 1) != 0;
    }
    
    public boolean isSensSize() {
    	return (iSOB & 2) != 0;
    }

    public void paint2(Graphics g)
    {
        try
        {
            int size = sizeBar;
            Dimension dimension = getSize();
            int width = dimension.width - 1;
            int ystart2nd = size * 3 + 4;
            for(int barId = 0; barId < 2; barId++)
            {
                boolean cbOn = (iSOB & barId + 1) != 0;
                int ystart = ystart2nd * barId;
                Awt.fillFrame(g, !cbOn, 0, ystart, width, ystart2nd);
                Awt.fillFrame(g, !cbOn, 0, ystart, size, size);
                g.setColor(getForeground());                
                g.drawString(STR[barId], 6, (ystart + size)-4);
                // draw line across to indicate disabled
                if(!cbOn) {
                	g.drawLine(0, ystart+size, size, ystart);
                }
                
                // (int)Math.sqrt(info.getPenMask()[mg.iPenM][mg.iSize].length) + "px"
                String v1 = "" + ((barId == 0)? ((float)(mg.iSA & 0xff) / 255F) : (int)Math.sqrt((info.getPenMask()[mg.iPenM][(int)(((float)(mg.iSS & 0xff) / 255F) * (info.getPMMax() -1))].length)));
                String v2 = "" + ((barId == 0)? ((float)(mg.iSA >>> 8 & 0xff) / 255F) : (int)Math.sqrt((info.getPenMask()[mg.iPenM][(int)(((float)(mg.iSS >>> 8 & 0xff) / 255F) * (info.getPMMax() -1))].length)));                
                if(v1.length() > 4) {
                	v1 = v1.substring(0, 4);
                }                
                if(v2.length() > 4) {
                	v2 = v2.substring(0, 4);
                }                
                
                String sensValues = v1 + " : " + v2;
                Rectangle2D fontbox = g.getFontMetrics().getStringBounds(sensValues, g);
                g.drawString(sensValues, (int)(width - (fontbox.getWidth() + 2)), (ystart + size) - 2);               
                
                dBar(barId, 0, g);
            }            
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    
    public void pMouse(MouseEvent mouseevent)
    {
        int mX = mouseevent.getX();
        int mY = mouseevent.getY();
        int size = sizeBar;
        switch(mouseevent.getID())
        {
        default:
            break;

        // click
        case 501: 
            if(iDrag >= 0)
                break;
            // get which widget is clicked on
            int widgetY = at(mY);
            iDrag = widgetY;
            if((widgetY == 0 || widgetY == 3) && mX <= size)
            {
            	// if select the checkbox
                iSOB ^= 1 << (widgetY != 0 ? 1 : 0);
                repaint();
            } else
            {
            	// drag the slider
                drag(mX);
            }
            break;

        // unclick
        case 502: 
            iDrag = -1;
            break;

        // drag    
        case 506: 
            drag(mX);
            break;
        }
    }

    public final boolean poll()
    {
        if(iSOB == 0)
            return false;
        try
        {
            if(((Boolean)mPoll.invoke(tab, null)).booleanValue())
            {
                mg.iSOB = iSOB;
                if(max <= 0)
                {
                    max = ((Integer)mEx.invoke(tab, null)).intValue();
                    if(max != 0)
                        mEx = null;
                }
                return true;
            }
        }
        catch(Throwable _ex) { }
        return false;
    }

    public final int strange()
    {
        try
        {
            if(poll()) {
                strange = (int)(((float)((Integer)mGet.invoke(tab, null)).intValue() / (float)max) * 255F);                
                if(iSOB != 0) {                    
	                if(isSensAlpha()) {
	                    dBar(0, strange);
	                }
	                if(isSensSize()) {
	                	dBar(1, strange);
	                }
                }
            }
            else
                strange = 0;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }        
        return strange;
    }

    private Mg mg;
    /**
     *  // 0 = empty space, 
    	// 1 = start alpha, 
    	// 2 = end alpha, 
    	// 3 = space, 
    	// 4 = size start, 
    	// 5 = size end
     */
    private int iDrag;
    private int sizeBar;
    private int max;
    private Mg.Info info;
    private int strange;
    private Object tab;
    private Method mGet;
    private Method mPoll;
    private Method mEx;
    private byte iSOB;
    private final String STR[] = {
        "A", "S"
    };
    private final Color TRACE_COLOR = new Color(198,191,229);
}
