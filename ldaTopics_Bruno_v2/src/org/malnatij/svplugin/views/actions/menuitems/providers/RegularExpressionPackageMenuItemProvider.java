package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.RegExPackageFilterDialog;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class RegularExpressionPackageMenuItemProvider extends MenuItemWithView {

	public RegularExpressionPackageMenuItemProvider(
			XRayWorkbenchView viewContainer, Menu menu) {
		super(viewContainer, menu);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem regEx = new MenuItem(menu, SWT.NONE);
		regEx.setText("Packages Content as Regular Expression...");
		regEx.setImage(getImageDescriptor("regex.gif").createImage());
		regEx.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				new RegExPackageFilterDialog(viewContainer);
			}
			
		});
		return regEx;
	}

}
