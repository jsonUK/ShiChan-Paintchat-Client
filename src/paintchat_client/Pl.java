// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 5/25/2009 10:10:36 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Pl.java

package paintchat_client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import paintchat.Constants;
import paintchat.Mg;
import paintchat.MgText;
import paintchat.PCDebug;
import paintchat.Res;
import paintchat.ToolBox;
import syi.awt.Awt;
import syi.awt.LButton;
import syi.awt.LComponent;
import syi.awt.TextPanel;
import syi.util.ThreadPool;

// Referenced classes of package paintchat_client:
//            IMi, Data, Mi

public class Pl extends Panel
    implements Runnable, ActionListener, IMi, KeyListener
{

    public Pl(Applet applet1)
    {
        super(null);
        isStart = false;
        isStartChat = false;
        iScrollType = 0;
        tool = null;
        dPack = new Dimension();
        dSize = null;
        dMax = new Dimension();
        iGap = 5;
        iCenter = 70;
        iCenterOld = -1;
        sounds = null;
        iPG = 10;
        applet = applet1;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        try
        {
            Object obj = actionevent.getSource();
            LButton lbutton = (LButton)tPanelB.getComponent(2);
            if((obj instanceof LButton) && obj != lbutton)
            {
                if(tPanelB.getComponent(0) == obj)
                    f(tPanel, true);
                else
                    f(this, false);
            } else
            {
                typed(obj != lbutton);
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    protected void addInOut(String s, boolean flag)
    {
        if(flag)
        {
            tList.addText(s);
            s = s + res.res("entered");
        } else
        {
            tList.remove(s);
            s = s + res.res("leaved");
        }
        addTextInfo(s, false);
    }
    
    protected void addUserKick(String s) {
//    	
    }

    protected void addSText(String s)
    {
        tText.decode(s);
    }

    protected void addText(String s, boolean flag)
    {
        if(s == null)
            tText.repaint();
        else
            tText.addText(s, flag);
    }

    protected void addTextInfo(String s, boolean flag)
    {
        Color color = tText.getForeground();
        tText.setForeground(Color.red);
        addText("PaintChat>" + s, false);
        tText.setForeground(color);
        if(flag)
            addText(null, flag);
    }
    
    protected void addCmdInfo(String cmdResult) {
    	Color color = tText.getForeground();
        tText.setForeground(new Color(150,88,88));
        addText(cmdResult, false);
        tText.setForeground(color);
    }
    
    protected void addAdminText(String s) {
    	Color color = tText.getForeground();
        tText.setForeground(new Color(31,105,85));
        addText(s, false);
        tText.setForeground(color);
        
    }

    public void changeSize()
    {
    }

    public void destroy()
    {
        try
        {
            if(dd != null)
                dd.destroy();
            dd = null;
            tool = null;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    protected void dSound(int i)
    {
        try
        {
            if(!isStartChat || sounds == null || sounds[i] == null)
                return;
            sounds[i].play();
        }
        catch(RuntimeException _ex)
        {
            sounds = null;
        }
    }

    private void f(Component component, boolean flag)
    {
        try
        {
            boolean flag1 = false;
            Object obj = flag ? ((Object) (this)) : ((Object) (applet));
            Component acomponent[] = ((Container) (obj)).getComponents();
            for(int i = 0; i < acomponent.length; i++)
            {
                if(acomponent[i] != component)
                    continue;
                flag1 = true;
                break;
            }

            obj = component.getParent();
            ((Container) (obj)).remove(component);
            if(flag1)
            {
                if(flag)
                    iCenter = 100;
                pack();
                Object obj1 = flag ? ((Object) (new Dialog(Awt.getPFrame()))) : ((Object) (new Frame("PaintChatClient v3.56b")));
                ((Container) (obj1)).setLayout(new BorderLayout());
                ((Container) (obj1)).add(component, "Center");
                ((Window) (obj1)).pack();
                ((Window) (obj1)).show();
            } else
            {
                ((Window)obj).dispose();
                if(flag)
                {
                    iCenter = 70;
                    add(component);
                } else
                {
                    applet.add(component, "Center");
                    applet.validate();
                }
                pack();
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public Dimension getSize()
    {
        if(dSize == null)
            dSize = super.getSize();
        return dSize;
    }

    public void iPG(int i)
    {
        iPG = i;
        if(isStart)
            return;
        try
        {
            Graphics g = getGraphics();
            if(g == null)
                return;
            String s = String.valueOf(iPG) + '%';
            FontMetrics fontmetrics = g.getFontMetrics();
            int j = fontmetrics.getHeight() + 2;
            g.setColor(getBackground());
            g.fillRect(5, 5 + j, fontmetrics.stringWidth(s) + 15, j + 10);
            g.setColor(getForeground());
            g.drawString("PaintChatClient v3.56b", 10, 10 + j);
            g.drawString(s, 10, 10 + j * 2);
            g.dispose();
        }
        catch(Throwable _ex) { }
    }

    public void keyPressed(KeyEvent keyevent)
    {
        try
        {
            boolean flag = keyevent.isAltDown() || keyevent.isControlDown();
            int i = keyevent.getKeyCode();
            if(flag)
            {
                if(i == 38)
                {
                    keyevent.consume();
                    iCenter = Math.max(iCenter - 4, 0);
                    pack();
                }
                if(i == 40)
                {
                    keyevent.consume();
                    iCenter = Math.min(iCenter + 4, 100);
                    pack();
                }
                if(i == 83)
                {
                    keyevent.consume();
                    typed(true);
                }
            } else
            {
                switch(i)
                {
                case 117: // 'u'
                    f(this, false);
                    break;

                default:
                    dSound(0);
                    break;

                case 10: // '\n'
                    break;
                }
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public void keyTyped(KeyEvent keyevent)
    {
    }

    private Cursor loadCursor(String s, int i)
    {
        try
        {
            if(s != null && s.length() > 0)
            {
                boolean flag = s.equals("none");
                int j;
                int k;
                int l;
                int i1;
                Image image;
                if(!flag)
                {
                    image = getToolkit().createImage((byte[])dd.config.getRes(s));
                    if(image == null)
                        return Cursor.getPredefinedCursor(i);
                    Awt.wait(image);
                    l = image.getWidth(null);
                    i1 = image.getHeight(null);
                    j = s.indexOf('x');
                    if(j == -1)
                        j = j != -1 ? Integer.parseInt(s.substring(j + 1, s.indexOf('x', j + 1))) : l / 2 - 1;
                    k = s.indexOf('y');
                    if(k == -1)
                        k = k != -1 ? Integer.parseInt(s.substring(k + 1, s.indexOf('y', k + 1))) : i1 / 2 - 1;
                } else
                {
                    j = k = 7;
                    l = i1 = 16;
                    image = null;
                }
                try
                {
                    if(image == null)
                    {
                        IndexColorModel indexcolormodel = new IndexColorModel(8, 2, new byte[2], new byte[2], new byte[2], 0);
                        image = createImage(new MemoryImageSource(l, i1, indexcolormodel, new byte[l * i1], 0, l));
                    }
                    Toolkit toolkit = getToolkit();
                    toolkit.getClass();
                    Method method = java.awt.Toolkit.class.getMethod("createCustomCursor", new Class[] {
                        java.awt.Image.class, java.awt.Point.class, java.lang.String.class
                    });
                    Method method1 = java.awt.Toolkit.class.getMethod("getBestCursorSize", new Class[] {
                        Integer.TYPE, Integer.TYPE
                    });
                    Dimension dimension = (Dimension)method1.invoke(toolkit, new Object[] {
                        new Integer(l), new Integer(i1)
                    });
                    if(dimension.width != 0 && dimension.height != 0)
                        return (Cursor)method.invoke(toolkit, new Object[] {
                            image, new Point((int)(((float)l / (float)dimension.width) * (float)j), (int)(((float)i1 / (float)dimension.height) * (float)k)), "custum"
                        });
                }
                catch(NoSuchMethodException _ex)
                {
                    if(image == null)
                        image = createImage(new MemoryImageSource(l, i1, new int[l * i1], 0, l));
                    return (Cursor)Class.forName("com.ms.awt.CursorX").getConstructors()[0].newInstance(new Object[] {
                        image, new Integer(j), new Integer(k)
                    });
                }
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
        return Cursor.getPredefinedCursor(i);
    }

    private void loadSound()
    {
        if(sounds != null)
        {
            sounds = null;
            return;
        }
        sounds = new AudioClip[4];
        String as[] = {
            "tp.au", "talk.au", "in.au", "out.au"
        };
        for(int i = 0; i < 4; i++)
            try
            {
                String s = dd.config.res(as[i]);
                if(s != null && s.length() > 0 && s.charAt(0) != '_')
                    sounds[i] = applet.getAudioClip(new URL(applet.getCodeBase(), s));
            }
            catch(IOException _ex)
            {
                sounds[i] = null;
            }

    }
      

    private void mkTextPanel()
    {        
        String s = "Center";
        String s1 = "East";
        String s2 = "West";
        Panel panel = new Panel(new BorderLayout());
        tField = new TextField();
        tField.addActionListener(this);
        panel.add(tField, s);
        tLabel = new Label(dd.res.res("input"));
        panel.add(tLabel, s2);
        String as[] = {
            "F", "FAll", "enter"
        };
        Panel panel1 = new Panel(new FlowLayout(0, 2, 1));
        tPanelB = panel1;
        for(int i = 0; i < 3; i++)
        {
            LButton lbutton = new LButton(res.res(as[i]));
            lbutton.addActionListener(this);
            lbutton.setName(String.valueOf(i));
            panel1.add(lbutton);
        }
        
        String [] strSizes = new String[]{"S","M","L"};
        for(int i=0; i < 3; i++) {
            LButton lbutton = new LButton(strSizes[i]);
            lbutton.addActionListener(SIZE_BUTTON_LISTENER);
            lbutton.setName(strSizes[i]);
            panel1.add(lbutton);
        }

        panel.add(panel1, s1);
        Color color = getBackground();
        Color color1 = getForeground();
        tText = new TextPanel(applet, 100, color, color1, tField);
        tText.isView = false;
        tList = new TextPanel(applet, 20, color, color1, tField);
        tPanel = new Panel(new BorderLayout());
        tPanel.add(tText, s);
        tPanel.add(tList, s1);
        tPanel.add(panel, "South");
        Awt.getDef(tPanel);
        Awt.setDef(tPanel, false);
    }
    
    private final ActionListener SIZE_BUTTON_LISTENER = new ActionListener() {

		public void actionPerformed(ActionEvent ae) {
			LButton src = (LButton) ae.getSource();
			Graphics g = getGraphics();			
			// show 1 line
			
			if(src.getName().equals("S")) {
				iCenter = 90;			
			}
			if(src.getName().equals("M")) {
				iCenter = 70;
			}
			if(src.getName().equals("L")) {
				iCenter = 50;
			}
			if(iCenter != iCenterOld) {
				pack();
			}
		}    	
    };   

    private void pack()
    {
        if(tool == null || mi == null)
        {
            return;
        } else
        {
            dSize = super.getSize();
            ThreadPool.poolStartThread(this, 'p');
            return;
        }
    }

    public void paint(Graphics g)
    {
        if(!isStart)
            iPG(iPG);
    }

    protected void processEvent(AWTEvent awtevent)
    {
        int i = awtevent.getID();
        if(i == 101 || i == 102)
            pack();
    }

    public void repaint(long l, int i, int j, int k, int i1)
    {
        repaint(((Component) (this)), i, j, k, i1);
    }

    private void repaint(Component component, int i, int j, int k, int l)
    {
        if(component instanceof Container)
        {
            Component acomponent[] = ((Container)component).getComponents();
            for(int i1 = 0; i1 < acomponent.length; i1++)
            {
                Point point1 = acomponent[i1].getLocation();
                repaint(acomponent[i1], i - point1.x, j - point1.y, k, l);
            }

        } else
        {
            Point point = component.getLocation();
            int j1 = i - point.x;
            int k1 = j - point.y;
            if(j1 + k <= 0 || k1 + l <= 0)
                return;
            component.repaint(j1, k1, k, l);
        }
    }

    private void rInit()
    {
    	PCDebug.println("starting");
        String s = "cursor_";
        try
        {        	
        	PCDebug.println("app:" + applet.getParent().getParent());
            getSize();
            dd = new Data(this);
            mgText = new MgText();
            iPG(60);
            dd.init();
            res = dd.res;
            Res res1 = dd.config;
            PCDebug.println("z" + res1);
            int layerCount = res1.getP(Constants.PARAM_CLIENT_LAYER_COUNT, 2);
            int quality = res1.getP("quality", 1);
            try
            {
                Awt.cBk = Awt.cC = new Color(res1.getP("color_bk", 0xcfcfff));
                Awt.cFore = new Color(res1.getP("color_text", 0x505078));
                Awt.cFSel = new Color(res1.getP("color_icon_select", 0xee3333));
                Awt.getDef(this);
                Awt.setPFrame((Frame)Awt.getParent(this));
            }
            catch(Throwable _ex) { }
            iPG(75);
            Cursor acursor[] = new Cursor[4];
            int i = 0;
            int ai[] = {
                i, 13, i, i
            };
            for(int j = 0; j < 4; j++)
                acursor[j] = loadCursor(applet.getParameter(s + (j + 1)), ai[j]);

            iPG(80);
            // TODO temp for testing
            mi = new Mi(this, res);//new SaiMi(this, res);
            mi.init(applet, dd.config, dd.imW, dd.imH, quality, layerCount, acursor);
            dd.mi = mi;
            iPG(90);
            mkTextPanel();
            String toolName = res1.getP(Constants.PARAM_CLIENT_TOOLS_PACKAGE, "normal");
            enableEvents(9L);
            tField.addKeyListener(this);
            isStart = true;            
            try
            {
                getClass();
                tool = (ToolBox)Class.forName("paintchat." + toolName + ".Tools").newInstance();
                tool.init(this, applet, dd.config, res, mi);
            }
            catch(Throwable throwable1)
            {
                throwable1.printStackTrace();
            }
            add(mi);
            add(tPanel);
            tField.requestFocus();
            iPG(100);
            mi.resetGraphics();
            pack();
            PCDebug.println("finishing");
            initCustomParams();
            mi.mgChange();
            mi.isEnable = true;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
    
    /**
     * initialize private paramters
     */
    private void initCustomParams() {
    	// set username
    	String nick = applet.getParameter(Constants.PARAM_CLIENT_NICK);
    	if(nick == null || nick.length() == 0) {
    		addTextInfo("Invalid username" , true);
    		return;
    	}
	  	if(nick.length() > 12) { 
	  		nick = nick.substring(0, 12); 
		}	          
        
        // get server id
        String serverid = applet.getParameter(Constants.PARAM_SERVER_ID);
        if(serverid == null || nick.length() == 0) {
        	addTextInfo("Invalid server_id" , true);
    		return;
        }               
        
        
        // initialize connection
        dd.setServerInfo(nick, serverid);
        dd.run(this, 's');
    }

    /**
     * Pack in the tools and recenter our scroller. we do this when our drawing size changes
     */
    private synchronized void rPack()
    {
        Dimension dimension = getSize();
        if(dPack != null && dimension.equals(dPack) && iCenter == iCenterOld || !isVisible())
            return;
        int i = tool.getW();
        setVisible(false);
        iCenterOld = iCenter;
        dPack.setSize(dimension);
        int miHeight = (int)((float)dimension.height * ((float)iCenter / 100F));
        Dimension dimension1 = dMax;
        dimension1.setSize(dimension.width - mi.getGapW() - tool.getW(), miHeight - mi.getGapH());
        if(mi != null && !((LComponent) (mi)).isGUI)
        {
            mi.setDimension(null, dimension1, dimension1);
            int k = i >= 0 ? i : 0;
            if(mi.getLocation().x != k)
                mi.setLocation(k, 0);
        } else
        {
            mi.getMaximumSize().setSize(dimension1);
            mi.inParent();
        }
        if(tPanel != null && tPanel.getParent() == this)
        {
            int l = 1;
            int i1 = l + i;
            tPanel.setBounds(i1, miHeight + l, dimension.width - l * 2 - i1, dimension.height - (miHeight + l));
            validate();
        }
        if(tool != null)
            tool.pack();        
        setVisible(true);
        tField.requestFocus();
    }

    private void rStart()
    {
        isStartChat = true;
        dd.start();
        ((LButton)tPanelB.getComponent(2)).setText(res.res("leave"));
        tText.isView = true;
        mi.resetGraphics();
        if(dd.config.getP("Client_Sound", false))
            loadSound();
    }

    public void run()
    {
        try
        {
            switch(Thread.currentThread().getName().charAt(0))
            {
            case 105: // 'i'
                rInit();
                break;

            case 115: // 's'
                rStart();
                break;

            case 112: // 'p'
                rPack();
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void scroll(boolean flag, int i, int j)
    {
        LComponent alcomponent[] = tool.getCs();
        int k = alcomponent.length;
        Point point1 = mi.getLocation();
        int k1 = point1.x + mi.getGapX();
        int l1 = point1.y + mi.getGapY();
        Dimension dimension1 = mi.getSizeW();
        for(int i2 = 0; i2 < k; i2++)
        {
            LComponent lcomponent = alcomponent[i2];
            Point point = lcomponent.getLocation();
            Dimension dimension = lcomponent.getSizeW();
            if((point.x + dimension.width > point1.x && point.y + dimension.height > point1.y && point.x < point1.x + dimension1.width && point.y < point1.y + dimension1.height || lcomponent.isEscape) && lcomponent.isVisible())
                if(iScrollType == 0 && false)
                {
                    int l = point.x - k1;
                    int i1 = point.y - l1;
                    int j1 = dimension.width;
                    int _tmp = dimension.height;
                    if(i > 0)
                        mi.m_paint(l - i, i1, i, dimension.height);
                    if(i < 0)
                        mi.m_paint(l + j1, i1, -i, dimension.height);
                    j1 += Math.abs(i);
                    if(j < 0)
                        mi.m_paint(l - i, i1 + dimension.height, j1, -j);
                    if(j > 0)
                        mi.m_paint(l - i, i1 - j, j1, j);
                } else
                {
                    boolean flag1 = lcomponent.isEscape;
                    lcomponent.escape(flag);
                    if(!flag1)
                        mi.m_paint(point.x - point1.x, point.y - point1.y, dimension.width, dimension.height);
                }
        }

    }

    public void send(Mg mg)
    {
        dd.send(mg);
    }

    public void setARGB(int i)
    {
        i &= 0xffffff;
        tool.selPix(mi.info.m.iLayer != 0 && i == 0xffffff);
        if(mi.info.m.iPen != 4 && mi.info.m.iPen != 5)
            tool.setARGB(mi.info.m.iAlpha << 24 | i);
    }

    private void setDefComponent(Container container)
    {
        try
        {
            if(container == null)
                return;
            Color color = container.getForeground();
            Color color1 = container.getBackground();
            Component acomponent[] = container.getComponents();
            if(acomponent != null)
            {
                for(int i = 0; i < acomponent.length; i++)
                {
                    Component component = acomponent[i];
                    component.setBackground(color1);
                    component.setForeground(color);
                    if(component instanceof Container)
                        setDefComponent((Container)component);
                }

            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void setLineSize(int i)
    {
        tool.setLineSize(i);
    }
    
    public void cmdSetCanvasSize(String cmd) {
    	try {
    		String [] split = cmd.split(" ", 2);
    		if(split.length == 1) {
    			addCmdInfo("Proper use: " + CMD_RESIZE + " 80");
    			return;
    		}
    		int canvasSize = Integer.parseInt(split[1]);
    		if(canvasSize < 0) {    		
    			canvasSize = 0;
    		}
    		// hardcode 95 so they can at least see their textbox
    		if(canvasSize > 95){
    			canvasSize = 95;
    		}
    		addCmdInfo("Setting Canvas size to: " + canvasSize);
    		iCenter = canvasSize;
    		pack();
    	}
    	catch(Exception e) {
    		addCmdInfo("Canvas size must be a number from 0-95");
    	}
    }
    
    private void processCmd(String cmd) {
    	if(cmd.startsWith(CMD_RESIZE)) {
    		cmdSetCanvasSize(cmd);
    	}
    	else {
    		addCmdInfo("Invalid Command: " + cmd);
    	}
    }

    private void typed(boolean dontExit)
    {
        try
        {
            if(!dontExit && isStartChat)
            {
                applet.getAppletContext().showDocument(new URL(applet.getDocumentBase(), dd.config.getP(Constants.PARAM_EXIT_URL, "../index.html")));
                return;
            }
            String s = tField.getText();
            if(s == null || s.length() <= 0)
                return;
            tField.setText("");
            
            if(s.startsWith("/")) {
            	processCmd(s);
            	return;
            }
            
            if(s.length() > 256)
            {
                mi.alert("longer talk it", false);
                return;
            }
            if(mi.info.m.iHint == 8)
            {
                mi.addText(s);
            } else
            {
                mgText.setData((byte)0, s);
                dd.w(mgText);
                s = dd.getUserName() + '>' + s;
                tText.addText(s, true);
                dSound(1);
            }
        
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void undo(boolean flag)
    {
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    private static final String CMD_RESIZE = "/size";
    
    private static final String STR_VERSION = "PaintChatClient v3.56b";
    private static final String STR_INFO = "PaintChat";
    protected Applet applet;
    private boolean isStart;
    private boolean isStartChat;
    private int iScrollType;
    private Data dd;
    private Res res;
    private Mi mi;
    private ToolBox tool;
    private Panel tPanel;
    private Panel tPanelB;
    private TextPanel tText;
    private TextField tField;
    private TextPanel tList;
    private Label tLabel;
    private MgText mgText;
    private Dimension dPack;
    private Dimension dSize;
    private Dimension dMax;
    private int iGap;
    private int iCenter;
    private int iCenterOld;
    private Color clInfo;
    private AudioClip sounds[];
    private int iPG;
}