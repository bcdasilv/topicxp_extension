package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class NormalizeBorderMenuItemProvider extends MenuItemWithView{

	public NormalizeBorderMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu) {
		super(viewContainer, menu);
		
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem normalizeAllBorders = new MenuItem(menu, SWT.NONE);
		normalizeAllBorders.setText("Remove all Tags");
		normalizeAllBorders.setImage(
				getImageDescriptor("defaultBorder.gif").createImage());
		normalizeAllBorders.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.normalizeAllBorders(viewContainer.getCurrentFilters());
			}
		});
		
		return normalizeAllBorders;
	}

}
