package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class UntagMenuItemProvider extends MenuItemWithView{

	public UntagMenuItemProvider(XRayWorkbenchView viewContainer, Menu menu) {
		super(viewContainer, menu);
		
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem black = new MenuItem(menu, SWT.NONE);
		black.setText("No tag (Black)");
		black.setImage(getImageDescriptor("black.gif").createImage());
		black.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.defaultColorForSelectedNodes();
			}
		});
		
		return black;
	}

}
