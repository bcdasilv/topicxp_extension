package org.malnatij.svplugin.views.actions.menuitems.providers;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.ColorRelatedMenuAction;

public class SelectColorMenuItemProvider extends ColorRelatedMenuAction{
	
	public SelectColorMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu, ArrayList<Color> color, String menuName, String iconName,
			boolean select) {
		super(viewContainer, menu, menuName, iconName, color, select);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem selectColored = new MenuItem(menu, SWT.NONE);
		selectColored.setText(menuName);
		selectColored.setImage(getImageDescriptor(iconName).createImage());
		selectColored.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(select){
					viewContainer.
					selectNodesTagged(color, viewContainer.getCurrentFilters());
				} else {
					viewContainer.
					deSelectNodesTagged(color, viewContainer.getCurrentFilters());
				}
				
			}
			
		});
		return selectColored;
	}

}
