package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class SelectHighlightedMenuItemProvider 
extends MenuItemWithView {

	public SelectHighlightedMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu) {
		super(viewContainer, menu);
		
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem selHigh = new MenuItem(menu, SWT.NONE);
		selHigh.setText("Select Highlighted Nodes");
		selHigh.setImage(getImageDescriptor("selectHighligth.gif").createImage());
		selHigh.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.selectHighlightedNodes(viewContainer.getCurrentFilters());
			}
			
		});
		
		return selHigh;
	}

}
