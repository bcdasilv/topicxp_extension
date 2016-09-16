package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;
import org.malnatij.svplugin.views.actions.menuitems.providers.ShowEdgesMenuItemProvider;

public class EdgesCreatorAction extends DropDownImageAction{
	
	public EdgesCreatorAction(final XRayWorkbenchView viewContainer){
		super("Show Incoming / Outgoing Dependency " +
				"Edges and Interfaces Edges", viewContainer);
		
		setImageDescriptor(getImageDescriptor("dependencies.gif"));

		createAndSetMenu();
	}

	@Override
	protected void createAndSetMenu() {
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new ShowEdgesMenuItemProvider(viewContainer, menu,
						"Outgoing Dependency Edges", "outgoingDependency.gif",
						KindRelatedMenuItem.OUTGOING_DEP).getMenuItem();
				
				new ShowEdgesMenuItemProvider(viewContainer, menu,
						"Incoming Dependency Edges", "incomingDependency.gif",
						KindRelatedMenuItem.INCOMING_DEP).getMenuItem();
				
				new ShowEdgesMenuItemProvider(viewContainer, menu,
						"Incoming / Outgoing Dependency Edges", "dependencies.gif",
						KindRelatedMenuItem.INCOMING_OUTGOING_DEP).getMenuItem();
				
				new MenuItem(menu, SWT.SEPARATOR);
				
				new ShowEdgesMenuItemProvider(viewContainer, menu,
						"Incoming or Outgoing Interface(s) Edges", "interface.gif",
						KindRelatedMenuItem.INTERFACE_LINK).getMenuItem();
				
				new MenuItem(menu, SWT.SEPARATOR);
				
				new ShowEdgesMenuItemProvider(viewContainer, menu,
						"Hide Given Edges..", "hideEdges.gif",
						KindRelatedMenuItem.HIDE_DEP).getMenuItem();
				
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
