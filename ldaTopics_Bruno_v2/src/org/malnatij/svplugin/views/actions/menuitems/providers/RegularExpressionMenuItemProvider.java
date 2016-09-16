package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.RegExFilterDialog;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class RegularExpressionMenuItemProvider extends MenuItemWithView{
	
	public RegularExpressionMenuItemProvider(XRayWorkbenchView viewContainer, Menu menu){
		super(viewContainer, menu);
		
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem regEx = new MenuItem(menu, SWT.NONE);
		regEx.setText("Regular Expression...");
		regEx.setImage(getImageDescriptor("regex.gif").createImage());
		regEx.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				new RegExFilterDialog(viewContainer);
			}
			
		});
		return regEx;
	}
}
