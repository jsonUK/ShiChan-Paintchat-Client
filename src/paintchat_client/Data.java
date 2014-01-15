// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 5/25/2009 10:52:44 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Data.java

package paintchat_client;

import java.applet.Applet;
import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Locale;
import java.util.zip.Inflater;
import paintchat.*;
import syi.awt.Awt;
import syi.awt.TextPanel;
import syi.util.ByteStream;
import syi.util.ThreadPool;

// Referenced classes of package paintchat_client:
//            Pl, Mi

public class Data
    implements Runnable
{

    public Data(Pl pl1)
    {
        isLive = true;
        isDestroy = false;
        isUpdate = true;
        isStopText = false;
        ID = 0;
        bWork = new ByteStream();
        bLine = new ByteStream();
        bMg = new ByteStream();
        mgOut = null;
        mgDraw = new Mg();
        bText = new ByteStream();
        tText = null;
        tLine = null;
        strName = null;
        EOF = new EOFException();
        pl = pl1;
    }

    public synchronized void destroy()
    {
        if(isDestroy)
            return;
        isDestroy = true;
        Thread thread = Thread.currentThread();
        try
        {
            if(tLine != null && tLine != thread)
            {
                tLine.interrupt();
                tLine = null;
            }
        }
        catch(Throwable _ex) { }
        try
        {
            if(tText != null && tText != thread)
            {
                w(new MgText((byte)2, strName));
                for(int i = 0; i < 5; i++)
                {
                    if(bText.size() == 0)
                        break;
                    Thread.sleep(1000L);
                }

                tText.interrupt();
                tText = null;
            }
        }
        catch(Throwable _ex) { }
        isLive = false;
    }

    private Socket getSocket()
        throws InterruptedException
    {
    	PCDebug.println("1");
        if(address == null)
        {
            InetAddress inetaddress = null;
            String s = config.getP(Constants.PARAM_SERVER_HOST, null);
            PCDebug.println("2");
            try
            {
                if(s != null) {
                	PCDebug.println("3");
                    inetaddress = InetAddress.getByName(s);
                    PCDebug.println("name: " + inetaddress);
                }
                else {
                	PCDebug.println("4");
	                String s1 = pl.applet.getCodeBase().getHost();
	                if(s1 == null || s1.length() <= 0)
	                    s1 = "localhost";
	                inetaddress = InetAddress.getByName(s1);
                }
            }
            catch(UnknownHostException _ex)
            {
            	_ex.printStackTrace();
                inetaddress = null;
            }
            if(inetaddress == null)
            {
                destroy();
                return null;
            }
            PCDebug.println("5");
            address = inetaddress;            
            Port = config.getP(Constants.PARAM_SERVER_PORT, -1);
        }
        while(isLive) 
        {
            for(int i = 0; i < 2; i++)
            {
                try
                {
                	PCDebug.println("attempting to connect to: " + address + " : " + Port);
                    return new Socket(address, Port);
                }
                catch(IOException _ex)
                {
                    Thread.currentThread();
                }
                Thread.sleep(3000L);
            }

            if(!mi.alert("reconnect", true))
                break;
        }
        destroy();
        return null;
    }

    public void init()
    {
        try
        {
            Applet applet = pl.applet;
            URL url = applet.getCodeBase();
            String s = p(Constants.PARAM_SERVER_RESOURCE_DIR, "./res");
            if(!s.endsWith("/"))
                s = s + '/';
            URL url1 = new URL(url, s);
            res = new Res(applet, url1, bWork);
            config = new Res(applet, url1, bWork);
            config.loadZip(p("res.zip", "res_normal.zip"));
            pl.iPG(30);
            try
            {
                String s1 = "param_utf8.txt";
                config.load(new String((byte[])config.getRes(s1), "UTF8"));
                config.remove(s1);
            }
            catch(IOException ioexception1)
            {
                mi.alert(ioexception1.getMessage(), false);
            }
            pl.iPG(45);
    /*        try
            {
                config.load(Awt.openStream(new URL(url, p("File_PaintChat_Infomation", p("server", ".paintchat")))));
            }
            catch(IOException ioexception2)
            {
                mi.alert(ioexception2.getMessage(), false);
            } */
            pl.iPG(50);
            res.loadResource(config, "res", Locale.getDefault().getLanguage());
            pl.iPG(55);
            MAX_KAIWA_BORDER = config.getP(Constants.PARAM_CLIENT_MAX_TEXT, 120);            
            imW = config.getP(Constants.PARAM_CLIENT_IMAGE_WIDTH, 0);
            imH = config.getP(Constants.PARAM_CLIENT_IMAGE_HEIGHT, 0);
            PCDebug.println("img: " + imW + ", " + imH);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public String p(String s, String s1)
    {
        try
        {
            String s2 = pl.applet.getParameter(s);
            if(s2 == null || s2.length() <= 0)
                return s1;
            else
                return s2;
        }
        catch(Throwable _ex)
        {
            return s1;
        }
    }

    private final int r(InputStream inputstream)
        throws IOException
    {
        int i = inputstream.read();
        if(i == -1)
            throw EOF;
        else
            return i;
    }

    private final int r2(InputStream inputstream)
        throws IOException
    {
        return (r(inputstream) << 8) + r(inputstream);
    }

    /**
     * Draw the log that is read into our buffer from rLine
     * @throws InterruptedException
     */
    private void rDraw()
        throws InterruptedException
    {
        Thread thread = Thread.currentThread();
        while(isLive) 
        {
        	// read the log if ther eis any and draw it
            while(bMg.size() > 0) 
            {
                int i;
                synchronized(bMg)
                {
                    i = mgDraw.set(bMg.getBuffer(), 0);
                    bMg.reset(i);
                }
                // on our first time connecting, show we loaded the log
                if(i == 2)
                {
                    pl.addTextInfo(res.res("log_complete"), true);
                    mgDraw.newUser(null).wait = 0;
                    mi.isEnable = true;
                    synchronized(bMg)
                    {
                        bMg.gc();
                    }
                } else
                {
                    mgDraw.draw();
                }
            }
            Thread.sleep(3000L);
        }
    }

    /**
     * Read in the log segments after have loaded the full log you join
     * @throws InterruptedException
     */
    private void rLine()
        throws InterruptedException
    {
        tLine = Thread.currentThread();
        while(ID == 0 && isLive) 
            Thread.sleep(3000L);
        if(!isLive)
            return;
        Socket socket = null;
        InputStream inputstream = null;
        OutputStream outputstream = null;
        Inflater inflater = new Inflater(false);
        ByteStream bytestream = new ByteStream();

        byte abyte0[] = new byte[128];
        try
        {
            socket = getSocket();
            if(socket == null)
                return;
            inputstream = socket.getInputStream();
            outputstream = socket.getOutputStream();
            sendServerID(outputstream,inputstream);
            abyte0[0] = 108;
            abyte0[1] = (byte)(ID >>> 8);
            abyte0[2] = (byte)(ID & 0xff);
            abyte0[3] = 1;
            outputstream.write(abyte0, 0, 4);
            outputstream.flush();
            r2(inputstream);
            long l = time();
            while(isLive) 
            {
                int inputLengthAvailable = inputstream.available();
                if(inputLengthAvailable >= 2)
                {
                    int inputLengthRead = r2(inputstream);
                    boolean bMaxSizeRead;
                    // why??? -(next 2 lines, if max, read more?)
                    if(bMaxSizeRead = inputLengthRead == 65535)
                        inputLengthRead = r2(inputstream);
                    if(inputLengthRead <= 0)
                    {
                        l = time();
                    } else
                    {
                        bytestream.reset();
                        bytestream.write(inputstream, inputLengthRead);
                        if(bMaxSizeRead)
                        {
                            inflater.reset();
                            inflater.setInput(bytestream.getBuffer(), 0, bytestream.size());
                            synchronized(bMg)
                            {
                                while(!inflater.needsInput()) 
                                {
                                    int bytesUncompressed = inflater.inflate(abyte0, 0, abyte0.length);
                                    if(bytesUncompressed > 0)
                                        bMg.write(abyte0, 0, bytesUncompressed);
                                }
                            }
                        } else
                        {
                            synchronized(bMg)
                            {
                                bytestream.writeTo(bMg);
                            }
                        }
                        l = time();
                    }
                } else
                if(bLine.size() >= 2)
                {
                    bytestream.reset();
                    int bLineSize;
                    synchronized(bLine)
                    {
                        bLineSize = bLine.size();
                        bytestream.w2(bLineSize);
                        bLine.writeTo(bytestream);
                    }
                    bytestream.writeTo(outputstream);
                    outputstream.flush();
                    synchronized(bLine)
                    {
                        bLine.reset(bLineSize);
                    }
                    l = time();
                } else
                if(time() - l >= 60000L)
                {
                    outputstream.write(0);
                    outputstream.write(0);
                    outputstream.flush();
                    l = time();
                } else
                if(inputstream.available() == 0)
                    Thread.sleep(2000L);
            }
        }
        catch(EOFException _ex) { }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
        try
        {
        	pl.addTextInfo("Lost Connection: Graphics (please restart)" , true);
            if(inputstream != null)
                inputstream.close();
            if(outputstream != null)
                outputstream.close();
            if(socket != null)
                socket.close();
        }
        catch(IOException _ex) { }
    }
    
    private void sendServerID(OutputStream out, InputStream in) throws IOException {
    	PrintWriter pw = new PrintWriter(out);
    	PCDebug.println("Sending server ID");
    	pw.println();    	// flush buffer
    	pw.println(serverId);
    	pw.flush();    	
    	int retCode = in.read();
    	if(retCode != 0) {
    		if(retCode == 1) {
    			pl.addTextInfo("invalid server ID: " , true);
    		}
    		
    		throw new IOException("Invalid paintchat server");
    	}
    	// if 0, then good to go
    }

    private void rText()
        throws InterruptedException
    {
        tText = Thread.currentThread();
        Socket socket = null;
        InputStream inputstream = null;
        OutputStream outputstream = null;
        ByteStream bytestream = new ByteStream();
        boolean flag = false;
        int soundIndex = 0;
        try
        {
            int k = 500;
            socket = getSocket();
            if(socket == null)
                return;
            outputstream = socket.getOutputStream();
            inputstream = socket.getInputStream();
            sendServerID(outputstream,inputstream);	// send server id to connect to
            outputstream.write(new byte[] {
                116, 0, 0, 1
            });
            outputstream.flush();
            if(ID == 0)
            {
                ID = r2(inputstream);
                System.out.println(ID);
                pl.addTextInfo(res.res("connect_complete"), true);
            }
            long l = time();
            while(isLive) 
            {
                int j = inputstream.available();
                if(j > 0)
                {
                    j = r2(inputstream);
                    if(j > 0)
                    {
                        bytestream.reset();
                        bytestream.write(inputstream, j);
                        int i1 = 0;
                        byte abyte0[] = bytestream.getBuffer();
                        int j1;
                        for(; i1 < j; i1 += j1 + 2)
                        {
                            j1 = (abyte0[i1] & 0xff) << 8 | abyte0[i1 + 1] & 0xff;
                            String s = new String(abyte0, i1 + 3, j1 - 1, "UTF8");
                            switch(abyte0[i1 + 2])
                            {
                            case MgText.M_TEXT: // '\0'
                                pl.addText(s, false);
                                soundIndex = Math.max(soundIndex, 1);
                                break;

                            case MgText.M_SCRIPT: // '\b'
                                pl.addSText(s);
                                soundIndex = Math.max(soundIndex, 1);
                                break;

                            case MgText.M_INFOMATION: // '\006'
                                pl.addTextInfo(s, false);
                                soundIndex = Math.max(soundIndex, 1);
                                break;
                                
                            case 12:	// custom admin text
                            	pl.addAdminText(s);
                            	soundIndex = Math.max(soundIndex, 1);
                                break;

                            case MgText.M_IN: // '\001'
                                pl.addInOut(s, true);
                                soundIndex = 2;
                                break;

                            case MgText.M_OUT: // '\002'
                                pl.addInOut(s, false);
                                soundIndex = 3;
                                break;
                            }
                            if(abyte0[i1 + 2] != 101)
                                flag = true;
                        }

                        k = 500;
                        l = time();
                    }
                } else
                {
                    if(flag)
                    {
                        pl.addText(null, false);
                        flag = false;
                        if(soundIndex != -1)
                        {
                            pl.dSound(soundIndex);
                            soundIndex = -1;
                        }
                    }
                    if(bText.size() > 0)
                    {
                        bytestream.reset();
                        synchronized(bText)
                        {
                            bText.writeTo(bytestream);
                        }
                        outputstream.write(bytestream.size() >>> 8);
                        outputstream.write(bytestream.size() & 0xff);
                        bytestream.writeTo(outputstream);
                        outputstream.flush();
                        synchronized(bText)
                        {
                            bText.reset(j);
                        }
                        k = 500;
                    }
                    if(k == 500)
                        l = time();
                    if(time() - l >= 30000L)
                        w(new MgText((byte)101, (String)null));
                    synchronized(tText)
                    {
                        isStopText = true;
                        tText.wait(k);
                        isStopText = false;
                    }
                    k = Math.min(k + 250, 4000);
                }
            }
        }
        catch(Throwable _ex) {
        	_ex.printStackTrace();
        }
        try
        {
        	pl.addTextInfo("Lost Connection: Text (please restart)" , true);
            if(socket != null)
                socket.close();
            if(inputstream != null)
                inputstream.close();
            if(outputstream != null)
                outputstream.close();
        }
        catch(IOException _ex) { }
    }

    public void run()
    {
        try
        {
            switch(Thread.currentThread().getName().charAt(0))
            {
            case 116: // 't'
                rText();
                break;

            case 108: // 'l'
                rLine();
                break;

            case 100: // 'd'
                rDraw();
                break;
            }
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public Thread run(Runnable runnable, char c)
    {
        return ThreadPool.poolStartThread(runnable, String.valueOf(c));
    }

    public void send(Mg mg)
    {
        synchronized(bLine)
        {
            mg.get(bLine, bWork, mgOut);
            if(mgOut == null)
                mgOut = new Mg();
            mgOut.set(mg);
        }
    }

    public void start()
    {
        Mi mi1 = mi;
        mgDraw.setInfo(mi1.info);
        mgDraw.newUser(mi1).wait = -1;
        w(new MgText((byte)1, strName));
        run(this, 't');
        run(this, 'l');
        run(this, 'd');
    }

    private final long time()
    {
        return System.currentTimeMillis();
    }
    
    public void setServerInfo(String name, String serverID) {
    	strName = name;
    	serverId = serverID;
    }
    
    public String getUserName() {
    	return strName;
    }

    public void w(MgText mgtext)
    {
        try
        {
            synchronized(bText)
            {
                mgtext.getData(bText);
            }
            if(isStopText && tText != null)
                synchronized(tText)
                {
                    tText.notify();
                }
        }
        catch(IOException _ex) { }
        catch(RuntimeException _ex) { }
    }

    public Pl pl;
    public Res res;
    public Res config;
    private boolean isLive;
    private boolean isDestroy;
    private boolean isUpdate;
    private boolean isStopText;
    private int Port;
    private InetAddress address;
    private int ID;
    private ByteStream bWork;
    private ByteStream bLine;
    private ByteStream bMg;
    private Mg mgOut;
    private Mg mgDraw;
    public Mi mi;
    private ByteStream bText;
    private TextPanel text;
    private Thread tText;
    private Thread tLine;
    private String strName;
    private String serverId;  
    public int imW;
    public int imH;
    public int MAX_KAIWA;
    public int MAX_KAIWA_BORDER;
    private EOFException EOF;
}