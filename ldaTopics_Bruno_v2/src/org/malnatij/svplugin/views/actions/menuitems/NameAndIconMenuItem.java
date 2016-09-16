package org.malnatij.svplugin.views.actions.menuitems;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public abstract class NameAndIconMenuItem extends MenuItemWithView{
	protected String menuName;
	
	protected String iconName;
	
	public NameAndIconMenuItem(XRayWorkbenchView viewContainer, Menu menu,
			String menuName, String iconName) {
		super(viewContainer, menu);
		
		this.menuName = menuName;
		this.iconName = iconName;
	}

	public abstract MenuItem getMenuItem();
	

}
