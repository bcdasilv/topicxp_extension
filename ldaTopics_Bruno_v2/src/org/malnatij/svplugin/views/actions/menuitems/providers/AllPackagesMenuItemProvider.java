package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class AllPackagesMenuItemProvider extends MenuItemWithView {

	public AllPackagesMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu) {
		super(viewContainer, menu);
		
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem allPack = new MenuItem(menu, SWT.NONE);
		allPack.setText("All Packages' Content");
		allPack.setImage(getImageDescriptor("packageIn.gif").createImage());
		allPack.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.getCurrentHandler().setDefaultHiddenFilter();
				
				viewContainer.getCurrentHandler().
				refreshView(viewContainer.getCurrentFilters());
			}
			
		});
		return allPack;
	}

}
