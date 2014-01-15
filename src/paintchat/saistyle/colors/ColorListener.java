package paintchat.saistyle.colors;

public interface ColorListener {

	public abstract void colorChanged(int rgb, float [] hsb, ColorListener source); 
}
