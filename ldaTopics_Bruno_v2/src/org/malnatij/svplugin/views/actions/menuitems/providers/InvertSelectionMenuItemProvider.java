package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;

public class InvertSelectionMenuItemProvider
extends MenuItemWithView{
	
	public InvertSelectionMenuItemProvider(XRayWorkbenchView viewContainer, Menu menu){
		super(viewContainer, menu);
		
	}

	
	public MenuItem getMenuItem(){
		
		MenuItem invert = new MenuItem(menu, SWT.NONE);
		invert.setText("Invert Selection");
		invert.setImage(getImageDescriptor("invertSelection.gif").createImage());
		invert.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.invertSelection(viewContainer.getCurrentFilters());
			}
			
		});
		
		return invert;
	}

}
