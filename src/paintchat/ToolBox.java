// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/30/2009 4:47:32 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ToolBox.java

package paintchat;

import java.applet.Applet;
import java.awt.Container;
import paintchat_client.Mi;
import syi.awt.LComponent;

// Referenced classes of package paintchat:
//            Res

public interface ToolBox
{

    public abstract String getC();

    public abstract LComponent[] getCs();

    public abstract int getW();

    public abstract void init(Container container, Applet applet, Res res, Res res1, Mi mi);

    public abstract void lift();

    public abstract void pack();

    public abstract void selPix(boolean flag);

    public abstract void setARGB(int i);

    public abstract void setC(String s);
    
    public abstract void mgChange();

    public abstract void setLineSize(int i);

    public abstract void up();
}
