// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/28/2009 3:17:24 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Res.java

package paintchat;

import java.applet.Applet;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import syi.awt.Awt;
import syi.util.ByteStream;

public class Res extends Hashtable
{

    public Res()
    {
        this(null, null, null);
    }

    public Res(Applet applet1, Object obj, ByteStream bytestream)
    {
        resBase = obj;
        applet = applet1;
        work = bytestream;
    }

    public final String get(String s)
    {
        return get(s, "");
    }

    public final String get(String s, String s1)
    {
        if(s == null)
            return s1;
        Object o = super.get(s); 
        if(o == null)
            return s1;
        else
            return o.toString();
    }

    public final boolean getBool(String s)
    {
        return getBool(s, false);
    }

    public final boolean getBool(String s, boolean flag)
    {
        try
        {
            s = get(s);
            if(s == null || s.length() <= 0)
                return flag;
            char c = s.charAt(0);
            switch(c)
            {
            case 49: // '1'
            case 111: // 'o'
            case 116: // 't'
            case 121: // 'y'
                return true;

            case 48: // '0'
            case 102: // 'f'
            case 110: // 'n'
            case 120: // 'x'
                return false;
            }
        }
        catch(RuntimeException _ex) { }
        return flag;
    }

    public ByteStream getBuffer()
    {
        if(work == null)
            work = new ByteStream();
        else
            work.reset();
        return work;
    }

    public final int getInt(String s)
    {
        try
        {
            return getInt(s, 0);
        }
        catch(Exception _ex)
        {
            return 0;
        }
    }

    public final int getInt(String s, int i)
    {
        try
        {
            String s1 = get(s);
            if(s1 != null && s1.length() > 0)
                return parseInt(s1);
        }
        catch(Throwable _ex) { }
        return i;
    }

    public final int getP(String s, int i)
    {
        String s1 = p(s);
        if(s1 != null)
            put(s, s1);
        return getInt(s, i);
    }

    public String getP(String s, String s1)
    {
        String s2 = p(s);
        return s2 != null && s2.length() > 0 ? s2 : get(s, s1);
    }

    public boolean getP(String s, boolean flag)
    {
        String s1 = p(s);
        if(s1 != null)
            put(s, s1);
        return getBool(s, flag);
    }

    public final Object getRes(Object obj)
    {
        try
        {
            Object obj1 = get(obj);
            if(obj1 == null)
            {
                ByteStream bytestream = getBuffer();
                bytestream.write(Awt.openStream((resBase instanceof String) ? new URL(applet.getCodeBase(), (String)resBase + (String)obj) : new URL((URL)resBase, (String)obj)));
                return bytestream.toByteArray();
            } else
            {
                return obj1;
            }
        }
        catch(IOException _ex)
        {
            return null;
        }
    }

    public boolean load(InputStream inputstream)
    {
        return load(((Reader) (new InputStreamReader(inputstream))));
    }

    public boolean load(Reader reader)
    {
        try
        {
            Object obj = (reader instanceof StringReader) ? ((Object) (reader)) : ((Object) (new BufferedReader(reader, 512)));
            CharArrayWriter chararraywriter = new CharArrayWriter();
            String s1 = null;
            try
            {
                do
                {
                    String s;
                    do
                        s = readLine(((Reader) (obj)));
                    while(s == null);
                    int i = s.indexOf('=');
                    if(i > 0)
                    {
                        if(s1 != null)
                        {
                            put(s1, chararraywriter.toString());
                            s1 = null;
                        }
                        s1 = s.substring(0, i).trim();
                        chararraywriter.reset();
                        if(i + 1 < s.length())
                            chararraywriter.write(s.substring(i + 1));
                    } else
                    if(s1 != null)
                    {
                        chararraywriter.write(10);
                        chararraywriter.write(s);
                    }
                } while(true);
            }
            catch(EOFException _ex) { }
            if(s1 != null && chararraywriter.size() > 0)
                put(s1, chararraywriter.toString());
            ((Reader) (obj)).close();
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
            return false;
        }
        return true;
    }

    public void load(String s)
    {
        if(s == null || s.length() <= 0)
        {
            return;
        } else
        {
            load(((Reader) (new StringReader(s))));
            return;
        }
    }

    public void loadResource(Res res1, String s, String s1)
    {
        boolean _tmp = s1 != null && s1.equals("ja");
        String s2 = s + (s1 == null || s1.length() == 0 ? "" : '_' + s1) + ".txt";
        for(int i = 0; i < 2; i++)
        {
            try
            {
                byte abyte0[] = (byte[])res1.getRes(s2);
                if(abyte0 != null)
                {
                    ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
                    load(new InputStreamReader(bytearrayinputstream, "UTF8"));
                    break;
                }
            }
            catch(RuntimeException _ex) { }
            catch(UnsupportedEncodingException _ex) { }
            s2 = s + ".txt";
        }

    }

    public void loadZip(InputStream inputstream)
        throws IOException
    {
        ByteStream bytestream = getBuffer();
        ZipInputStream zipinputstream = new ZipInputStream(inputstream);
        ZipEntry zipentry;
        while((zipentry = zipinputstream.getNextEntry()) != null) 
        {
            bytestream.reset();
            bytestream.write(zipinputstream);
            r(bytestream, zipentry.getName());
        }
        zipinputstream.close();
    }

    public void loadZip(String s)
        throws IOException
    {
        try
        {
            InputStream inputstream = getClass().getResourceAsStream('/' + s);
            if(inputstream != null)
            {
                loadZip(inputstream);
                return;
            }
        }
        catch(Throwable _ex) { }
        loadZip(Awt.openStream(new URL(applet.getCodeBase(), s)));
    }

    private String p(String s)
    {
        return applet.getParameter(s);
    }

    public static final int parseInt(String s)
    {
        int i = s.length();
        if(i <= 0)
            return 0;
        byte byte0 = 0;
        if(s.charAt(0) == '0')
            byte0 = 2;
        else
        if(s.charAt(0) == '#')
            byte0 = 1;
        if(byte0 != 0)
        {
            int j = 0;
            i -= byte0;
            for(int k = 0; k < i; k++)
                j |= Character.digit(s.charAt(k + byte0), 16) << (i - 1 - k) * 4;

            return j;
        } else
        {
            return Integer.parseInt(s);
        }
    }

    private void r(ByteStream bytestream, String s)
        throws IOException
    {
        String s1 = s.toLowerCase();
        if(s1.endsWith("zip"))
            loadZip(new ByteArrayInputStream(bytestream.toByteArray()));
        else
            put(s, bytestream.toByteArray());
    }

    private final String readLine(Reader reader)
        throws EOFException, IOException
    {
        int i = reader.read();
        if(i == -1)
            throw new EOFException();
        if(i == 13 || i == 10)
            return null;
        if(i == 35)
        {
            do
                i = reader.read();
            while(i != 13 && i != 10 && i != -1);
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((char)i);
        while((i = reader.read()) != -1) 
        {
            if(i == 13 || i == 10)
                break;
            stringbuffer.append((char)i);
        }
        return stringbuffer.toString();
    }

    public final String res(String s)
    {
        return getP(s, s);
    }

    private Object resBase;
    private Applet applet;
    private ByteStream work;
    private static final String EMPTY = "";
}
