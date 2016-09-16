package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class SelectKindMenuItemProvider extends KindRelatedMenuItem{
	private boolean select;

	public SelectKindMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu, String menuName, String iconName,
			String kind, boolean select) {
		super(viewContainer, menu, menuName, iconName, kind);
		this.select = select;
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem selectKind = new MenuItem(menu, SWT.NONE);
		selectKind.setText(menuName);
		selectKind.setImage(getImageDescriptor(iconName).createImage());
		selectKind.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(select){
					viewContainer.selectClassKind(kind, viewContainer.getCurrentFilters());
				} else {
					viewContainer.deSelectClassKind(kind, viewContainer.getCurrentFilters());
				}
				

			}

		});
		
		if(viewContainer.getCurrentVisualizationDescription().
				equals(ViewFacade.PACKAGE_DEPENDENCY)){
			selectKind.setEnabled(false);
		}
		
		return selectKind;
	}

}
