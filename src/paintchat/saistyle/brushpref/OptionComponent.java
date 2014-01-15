package paintchat.saistyle.brushpref;

import java.awt.Panel;

import paintchat.Mg.Info;
import paintchat.saistyle.Tools;

public abstract class OptionComponent extends Panel {

	Info info;
	OptionPanel parent;
	Tools tools;
	
	public OptionComponent(Tools tools) {
		this.tools = tools;
		this.info = tools.getInfo();
	}
	
	public abstract void loadMg();
	
	protected void setParent(OptionPanel op) {
		this.parent = op;
	}
	
	protected void updateOptions() {
		this.parent.updateOptions();
	}
}
