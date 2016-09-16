package org.malnatij.svplugin.views.actions.menuitems.cascade;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectHierarchyPositionMenuItemProvider;

public class SelectHierarchyPositionCascadeProvider
extends MenuItemWithView{

	public SelectHierarchyPositionCascadeProvider(
			XRayWorkbenchView viewContainer, Menu menu) {
		super(viewContainer, menu);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem rootMenu = new MenuItem(menu, SWT.CASCADE);
		rootMenu.setText("Select by Hierarchy Position...");
		rootMenu.setImage(getImageDescriptor("leaf.gif").createImage());
		
		Menu subMenuRoot = new Menu(menu.getShell(), SWT.DROP_DOWN);
		rootMenu.setMenu(subMenuRoot);
		
		new SelectHierarchyPositionMenuItemProvider(viewContainer,
				subMenuRoot, "Select Roots", "root.gif",
				SelectHierarchyPositionMenuItemProvider.ROOT,
				true).
				getMenuItem();
		
		new SelectHierarchyPositionMenuItemProvider(viewContainer,
				subMenuRoot, "Select Branches", "branch.gif",
				SelectHierarchyPositionMenuItemProvider.BRANCH,
				true).
				getMenuItem();
		
		new SelectHierarchyPositionMenuItemProvider(viewContainer,
				subMenuRoot, "Select Leaves", "leaf.gif",
				SelectHierarchyPositionMenuItemProvider.LEAF,
				true).
				getMenuItem();
		
		new MenuItem(subMenuRoot, SWT.SEPARATOR);
		
		new SelectHierarchyPositionMenuItemProvider(viewContainer,
				subMenuRoot, "Deselect Roots", "noroot.gif",
				SelectHierarchyPositionMenuItemProvider.ROOT,
				false).
				getMenuItem();
		
		new SelectHierarchyPositionMenuItemProvider(viewContainer,
				subMenuRoot, "Deselect Branches", "nobranch.gif",
				SelectHierarchyPositionMenuItemProvider.BRANCH,
				false).
				getMenuItem();
		
		new SelectHierarchyPositionMenuItemProvider(viewContainer,
				subMenuRoot, "Deselect Leaves", "noleaf.gif",
				SelectHierarchyPositionMenuItemProvider.LEAF,
				false).
				getMenuItem();
		
		return rootMenu;
	}

}
