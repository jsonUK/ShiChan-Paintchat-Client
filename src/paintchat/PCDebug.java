package paintchat;

public class PCDebug {

	private static final boolean PRINT_DEBUG = true;
	
	public static void println(Object o) {
		if(PRINT_DEBUG) {
			System.out.println(o);
		}
	}
}
