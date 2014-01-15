package paintchat_client;

import java.applet.Applet;

public class ClientPermissions {

	public static boolean isLayerEdit = false;
	public static boolean isFill = false;
	public static boolean isClear = true;	
	public static boolean isCanvas = true;
	
	public static void loadParams(Applet applet) {
		isLayerEdit = getBool(applet, "rg_layer_edit", false);		
		isFill = getBool(applet, "rg_fill", false);
		isClear = getBool(applet, "rg_clear", true);
		isCanvas = getBool(applet, "rg_canvas", true);
	}
	
	private static boolean getBool(Applet app, String param, boolean def) {
		String val = app.getParameter(param);
		if(val == null) {
			return def;
		}
		try{
			return Boolean.parseBoolean(val);
		}
		catch(Exception e) {
			return def;
		}
	}
}
