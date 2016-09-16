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

public class ColorTagMenuItemProvider extends ColorRelatedMenuAction{

	public ColorTagMenuItemProvider(XRayWorkbenchView viewContainer, Menu menu,
			String menuName, String iconName, ArrayList<Color> color, boolean tag) {
		super(viewContainer, menu, menuName, iconName, color, tag);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem colored = new MenuItem(menu, SWT.NONE);
		colored.setText(menuName);
		colored.setImage(getImageDescriptor(iconName).createImage());
		colored.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(select){
					viewContainer.colorizeSelectedNodes(color.get(0));
				} else {
					viewContainer.normalizeBorderColoredOf(color.get(0),
							viewContainer.getCurrentFilters());
				}
				
			}
		});
		
		return colored;
	}

}
