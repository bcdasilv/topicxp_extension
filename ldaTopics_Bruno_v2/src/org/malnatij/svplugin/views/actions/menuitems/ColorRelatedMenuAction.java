package org.malnatij.svplugin.views.actions.menuitems;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Menu;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public abstract class ColorRelatedMenuAction extends NameAndIconMenuItem{
	protected ArrayList<Color> color;
	
	protected boolean select;
	
	
	public ColorRelatedMenuAction(XRayWorkbenchView viewContainer, Menu menu,
			String menuName, String iconName, ArrayList<Color> color, boolean select) {
		
		super(viewContainer, menu, menuName, iconName);
	
		this.color = color;
		this.select = select;
	}
	
	
}
