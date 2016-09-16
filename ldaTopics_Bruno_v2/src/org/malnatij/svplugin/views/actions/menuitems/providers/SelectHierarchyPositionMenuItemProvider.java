package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class SelectHierarchyPositionMenuItemProvider extends KindRelatedMenuItem{
	private boolean select;

	public SelectHierarchyPositionMenuItemProvider(
			XRayWorkbenchView viewContainer, Menu menu, String menuName,
			String iconName, String kind, boolean select) {
		super(viewContainer, menu, menuName, iconName, kind);
		this.select = select;
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem position = new MenuItem(menu, SWT.NONE);
		position.setText(menuName);
		position.setImage(getImageDescriptor(iconName).createImage());
		position.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(kind.equals(LEAF)){
					viewContainer.selectLeaves(viewContainer.getCurrentFilters(), select);
				} else if(kind.equals(ROOT)){
					viewContainer.selectRoots(viewContainer.getCurrentFilters(), select);
				} else if(kind.equals(BRANCH)){
					viewContainer.selectBranches(viewContainer.getCurrentFilters(), select);
				}

			}
			
		});
		
		if(viewContainer.getCurrentVisualizationDescription().
				equals(ViewFacade.PACKAGE_DEPENDENCY)){
			position.setEnabled(false);
		}
		
		return position;
	}

}
