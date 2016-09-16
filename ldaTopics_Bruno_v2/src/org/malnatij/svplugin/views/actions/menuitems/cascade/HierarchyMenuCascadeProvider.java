package org.malnatij.svplugin.views.actions.menuitems.cascade;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectHierarchyMenuItemProvider;

public class HierarchyMenuCascadeProvider extends MenuItemWithView{

	public HierarchyMenuCascadeProvider(XRayWorkbenchView viewContainer,
			Menu menu) {
		super(viewContainer, menu);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem hierMenu = new MenuItem(menu, SWT.CASCADE);
		hierMenu.setText("Select by Hierarchy...");
		hierMenu.setImage(getImageDescriptor("parents.gif").createImage());
		
		Menu subMenuHier = new Menu(menu.getShell(), SWT.DROP_DOWN);
		hierMenu.setMenu(subMenuHier);
		
		new SelectHierarchyMenuItemProvider(viewContainer, subMenuHier,
				"Select Children", "children.gif",
				SelectHierarchyMenuItemProvider.CHILDREN, true).
				getMenuItem();
		
		new SelectHierarchyMenuItemProvider(viewContainer, subMenuHier,
				"Select Parents", "parents.gif",
				SelectHierarchyMenuItemProvider.PARENTS, true).
				getMenuItem();
		
		new SelectHierarchyMenuItemProvider(viewContainer, subMenuHier,
				"Select Siblings", "sibling.gif",
				SelectHierarchyMenuItemProvider.SIBLING, true).
				getMenuItem();
		
		new MenuItem(subMenuHier, SWT.SEPARATOR);
		
		new SelectHierarchyMenuItemProvider(viewContainer, subMenuHier,
				"Deselect Children", "nochildren.gif",
				SelectHierarchyMenuItemProvider.CHILDREN, false).
				getMenuItem();
		
		new SelectHierarchyMenuItemProvider(viewContainer, subMenuHier,
				"Deselect Parents", "noparents.gif",
				SelectHierarchyMenuItemProvider.PARENTS, false).
				getMenuItem();
		
		new SelectHierarchyMenuItemProvider(viewContainer, subMenuHier,
				"Deselect Siblings", "nosibling.gif",
				SelectHierarchyMenuItemProvider.SIBLING, false).
				getMenuItem();
		
		return hierMenu;
	}

}
