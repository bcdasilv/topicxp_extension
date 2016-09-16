package org.malnatij.svplugin.views.actions;

import java.util.ArrayList;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.cascade.ColorMenuCascadeProvider;
import org.malnatij.svplugin.views.actions.menuitems.cascade.HierarchyMenuCascadeProvider;
import org.malnatij.svplugin.views.actions.menuitems.cascade.SelectByKindMenuCascadeProvider;
import org.malnatij.svplugin.views.actions.menuitems.cascade.SelectHierarchyPositionCascadeProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.InvertSelectionMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.RegularExpressionMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectColorMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectHighlightedMenuItemProvider;

public class SelectionAction
extends DropDownImageAction {

	public SelectionAction(XRayWorkbenchView viewContainer) {
		super("Selection", viewContainer);
		
		setImageDescriptor(getImageDescriptor("parents.gif"));
		
		createAndSetMenu();
	}

	@Override
	protected void createAndSetMenu() {
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new RegularExpressionMenuItemProvider(
						viewContainer, menu).getMenuItem();
				
				ArrayList<Color> allNodes = new ArrayList<Color>();
				allNodes.add(Colors.YELLOW);
				allNodes.add(Colors.GREEN);
				allNodes.add(Colors.ORANGE);
				allNodes.add(Colors.MAGENTA);
				allNodes.add(Colors.BLACK);
				allNodes.add(Colors.GRAY);
				
				new SelectColorMenuItemProvider(viewContainer,
						menu,
						allNodes,
						"Select All Nodes",
						"all.gif",
						true).
						getMenuItem();
				
				new InvertSelectionMenuItemProvider(
						viewContainer, menu).getMenuItem();
				
				new SelectHighlightedMenuItemProvider(
						viewContainer, menu).getMenuItem();
				
				new SelectByKindMenuCascadeProvider(
						viewContainer, menu).getMenuItem();
				
				new ColorMenuCascadeProvider(
						viewContainer, menu).getMenuItem();
				
				new HierarchyMenuCascadeProvider(
						viewContainer, menu).getMenuItem();
				
				new SelectHierarchyPositionCascadeProvider(
						viewContainer, menu).getMenuItem();
				
				return menu;
			}
			
			public Menu getMenu(Menu parent) {
				
				return null;
			}
			
			public void dispose() {

			}
		});
	}

}
