package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.HideEdgesDialog;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;
import org.malnatij.svplugin.views.actions.menuitems.providers.CutMenuItemProvider;

public class HideNodeAction extends DropDownImageAction{

	public HideNodeAction(XRayWorkbenchView viewContainer) {
		super("Hide/Show Nodes", viewContainer);
		
		setImageDescriptor(getImageDescriptor("cut.gif"));
		
		createAndSetMenu();
	}

	@Override
	protected void createAndSetMenu() {
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new CutMenuItemProvider(viewContainer, menu, "Hide Selected Nodes",
						"cut.gif", KindRelatedMenuItem.HIDE).getMenuItem();
				
				new MenuItem(menu, SWT.SEPARATOR);
				
				new CutMenuItemProvider(viewContainer, menu, "Show Hidden Nodes",
						"tree.gif", KindRelatedMenuItem.SHOW_HIDDEN).getMenuItem();
				
				return menu;
			}
			
			public Menu getMenu(Menu parent) {
				return null;
			}
			
			public void dispose() {

			}
		});
		
	}
	
	
	public void run() {
		new HideEdgesDialog(viewContainer,
				viewContainer.getMaxDependencyWeigth());
	}

}
