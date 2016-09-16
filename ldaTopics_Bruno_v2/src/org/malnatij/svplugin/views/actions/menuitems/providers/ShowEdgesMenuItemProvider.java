package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.HideEdgesDialog;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class ShowEdgesMenuItemProvider extends KindRelatedMenuItem{

	public ShowEdgesMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu, String menuName, String iconName, String kind) {
		super(viewContainer, menu, menuName, iconName, kind);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem showEdges = new MenuItem(menu, SWT.NONE);
		showEdges.setText(menuName);
		showEdges.setImage(getImageDescriptor(iconName).createImage());
		showEdges.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(kind.equals(INTERFACE_LINK)){
					viewContainer.handleInterfaceLinkRequest(
							viewContainer.getCurrentFilters());
				} else if(kind.equals(OUTGOING_DEP)){
					viewContainer.handleOutgoingDependencyLinkRequest(
							viewContainer.getCurrentFilters());
				} else if(kind.equals(INCOMING_DEP)){
					viewContainer.handleIncomingDependencyLinkRequest(
							viewContainer.getCurrentFilters());
				} else if(kind.equals(INCOMING_OUTGOING_DEP)){
					viewContainer.handleOutgoingDependencyLinkRequest(
							viewContainer.getCurrentFilters());
					viewContainer.handleIncomingDependencyLinkRequest(
							viewContainer.getCurrentFilters());
				} else if(kind.equals(HIDE_DEP)){
					new HideEdgesDialog(viewContainer,
							viewContainer.getMaxDependencyWeigth());
				}
				
			}
		});
		
		if(!viewContainer.getCurrentVisualizationDescription().
				equals(ViewFacade.SYSTEM_COMPLEXITY)){
			if(!kind.equals(HIDE_DEP)){
				showEdges.setEnabled(false);
			}
		}
		
		return showEdges;
	}
}