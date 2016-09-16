package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class CutMenuItemProvider extends KindRelatedMenuItem{

	public CutMenuItemProvider(XRayWorkbenchView viewContainer, Menu menu,
			String menuName, String iconName, String kind) {
		super(viewContainer, menu, menuName, iconName, kind);
		
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem cut = new MenuItem(menu, SWT.NONE);
		cut.setText(menuName);
		cut.setImage(getImageDescriptor(iconName).createImage());
		cut.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(kind.equals(HIDE)){
					viewContainer.hideSelectedNode(viewContainer.getCurrentFilters());
				} else if(kind.equals(SHOW_HIDDEN)){
					viewContainer.showHiddenNodes(viewContainer.getCurrentFilters());
				}
			}
			
		});
		
		return cut;
	}

}
