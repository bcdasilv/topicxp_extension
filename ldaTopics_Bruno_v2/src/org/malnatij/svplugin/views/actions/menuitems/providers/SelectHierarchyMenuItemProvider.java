package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class SelectHierarchyMenuItemProvider extends KindRelatedMenuItem{
	private boolean select;

	public SelectHierarchyMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu, String menuName, String iconName, String kind,
			boolean select) {
		super(viewContainer, menu, menuName, iconName, kind);
		this.select = select;
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem selectHierarchy = new MenuItem(menu, SWT.NONE);
		selectHierarchy.setText(menuName);
		selectHierarchy.setImage(getImageDescriptor(iconName).createImage());
		selectHierarchy.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.selectNodeOfHierarchy(
						kind, viewContainer.getCurrentFilters(), select);
			}
			
		});
		
		if(viewContainer.getCurrentVisualizationDescription().
				equals(ViewFacade.PACKAGE_DEPENDENCY)){
			selectHierarchy.setEnabled(false);
		}
		
		return selectHierarchy;
	}

}
