package paintchat.saistyle.components;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Label;

import paintchat.saistyle.Tools;

public class SaiLabel extends Label {

	public SaiLabel(String text) {
		super(text);
		setFont(Tools.F_LABEL_FONT);
		
		FontMetrics fm = getFontMetrics(Tools.F_LABEL_FONT);
		Dimension size = new Dimension(fm.stringWidth(text) + 6, fm.getHeight() + 4);
		setSize(size);
		setPreferredSize(size);
	}
}
