package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import paintchat.saistyle.IconFactory;
import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiCheckbox;

public class ColorToggleCheckbox extends SaiCheckbox {

	public static final Dimension COLOR_TOGGLE_CHECKBOX_SIZE = new Dimension(21,21);
	private String iconName;	
	
	public ColorToggleCheckbox(String iconName) {
		super(COLOR_TOGGLE_CHECKBOX_SIZE);
		this.iconName = iconName;
		init();
	}
	
	@Override
	protected void initImageStates(BufferedImage[] imageState) {
		Dimension d = COLOR_TOGGLE_CHECKBOX_SIZE;
		imageState[STATE_C0H0] = IconFactory.createIcon(iconName, null, null, true, Tools.C_BACKGROUND, d.width, d.height, 1);
		imageState[STATE_C0H1] = IconFactory.createIcon(iconName, null, Tools.C_ICON_HOVER_BORDER_1, false, Tools.C_BACKGROUND, d.width, d.height, 1);
		imageState[STATE_C1H0] = IconFactory.createIcon(iconName, null, Tools.C_ICON_HOVER_BORDER_1, false, Tools.C_ICON_BACKGROUND_SELECTED, d.width, d.height, 1);
		imageState[STATE_C1H1] = IconFactory.createIcon(iconName, null, Tools.C_ICON_HOVER_BORDER_2, false, Tools.C_ICON_BACKGROUND_SELECTED_HOVER, d.width, d.height, 1);
	}

}
