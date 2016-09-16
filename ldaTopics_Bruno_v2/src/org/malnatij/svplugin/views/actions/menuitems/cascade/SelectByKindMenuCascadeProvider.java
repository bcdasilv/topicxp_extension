package org.malnatij.svplugin.views.actions.menuitems.cascade;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectKindMenuItemProvider;

public class SelectByKindMenuCascadeProvider 
extends MenuItemWithView{

	
	
	public SelectByKindMenuCascadeProvider(XRayWorkbenchView viewContainer,
			Menu menu) {
		super(viewContainer, menu);
		
	}

	public MenuItem getMenuItem(){
		MenuItem kindMenu = new MenuItem(menu, SWT.CASCADE);
		kindMenu.setText("Select by Kind...");
		kindMenu.setImage(getImageDescriptor("selectConcrete.gif").createImage());
		
		Menu subMenuKind = new Menu(menu.getShell(), SWT.DROP_DOWN);
		kindMenu.setMenu(subMenuKind);
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Select Concrete Classes", "selectConcrete.gif",
				EntityRepresentation.CONCRETE, true).getMenuItem();
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Select Abstract Classes", "selectAbstract.gif",
				EntityRepresentation.ABSTRACT, true).getMenuItem();
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Select Interfaces", "selectInterface.gif",
				EntityRepresentation.INTERFACE, true).getMenuItem();
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Select External Classes", "selectExternal.gif",
				EntityRepresentation.EXTERNAL, true).getMenuItem();
		
		new MenuItem(subMenuKind, SWT.SEPARATOR);
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Deselect Concrete Classes", "deselectConcrete.gif",
				EntityRepresentation.CONCRETE, false).getMenuItem();
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Deselect Abstract Classes", "deselectAbstract.gif",
				EntityRepresentation.ABSTRACT, false).getMenuItem();
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Deselect Interfaces", "deselectInterface.gif",
				EntityRepresentation.INTERFACE, false).getMenuItem();
		
		new SelectKindMenuItemProvider(viewContainer, subMenuKind,
				"Deselect External Classes", "deselectExternal.gif",
				EntityRepresentation.EXTERNAL, false).getMenuItem();
		
		return kindMenu;
	}

}
