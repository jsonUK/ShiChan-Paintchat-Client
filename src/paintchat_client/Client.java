package paintchat_client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.KeyEvent;

import syi.util.ThreadPool;

public class Client extends Applet {

	private Pl pl;
	private TextField tField; 
	
	  public void destroy()
	  {
	    if (this.pl != null)
	      this.pl.destroy();
	  }

	  public void start()
	  {
	    try
	    {	    	
	    	ClientPermissions.loadParams(this);
	    	setLayout(new BorderLayout());	      
	    	this.pl = new Pl(this);
	    	add(this.pl, "Center");
	    	validate();
	     	      	    	     
	    	ThreadPool.poolStartThread(pl, 'i');
	    }
	    catch (Throwable localThrowable)
	    {
	      localThrowable.printStackTrace();
	    }
	  }
}
