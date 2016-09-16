package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class SelectViewMenuItemProvider extends KindRelatedMenuItem{

	public SelectViewMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu, String menuName, String iconName, String kind) {
		super(viewContainer, menu, menuName, iconName, kind);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem showView = new MenuItem(menu, SWT.NONE);
		showView.setText(menuName);
		showView.setImage(getImageDescriptor(iconName).createImage());
		showView.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(kind.equals(ViewFacade.SYSTEM_COMPLEXITY)){
					viewContainer.showSystemComplexity();
				} else if(kind.equals(ViewFacade.CLASS_DEPENDENCY)){
					viewContainer.showClassDependency();
				} else {
					viewContainer.showPackageDependency();
				}
				
			}
		});
		
		checkEnabled(showView, kind);
		
		return showView;
	}
	
	private void checkEnabled(MenuItem showView, String kind){
		if(viewContainer.getCurrentVisualizationDescription().
				equals(kind)){
			showView.setEnabled(false);
		}
	}

}
