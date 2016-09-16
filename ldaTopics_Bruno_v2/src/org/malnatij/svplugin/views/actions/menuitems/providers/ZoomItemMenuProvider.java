package org.malnatij.svplugin.views.actions.menuitems.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.ManualZoomDialog;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class ZoomItemMenuProvider extends KindRelatedMenuItem{

	public ZoomItemMenuProvider(XRayWorkbenchView viewContainer, Menu menu,
			String menuName, String iconName, String kind) {
		super(viewContainer, menu, menuName, iconName, kind);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem zoom = new MenuItem(menu, SWT.NONE);
		zoom.setText(menuName);
		zoom.setImage(getImageDescriptor(iconName).createImage());
		zoom.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if(kind.equals(KindRelatedMenuItem.ZOOM_IN)){
					viewContainer.zoomIn(viewContainer.getCurrentFilters());
				} else if(kind.equals(KindRelatedMenuItem.ZOOM_OUT)){
					viewContainer.zoomOut(viewContainer.getCurrentFilters());
				} else if(kind.equals(KindRelatedMenuItem.FIT_SIZE)){
					viewContainer.fitSize(viewContainer.getCurrentFilters());
				} else if(kind.equals(KindRelatedMenuItem.ORIGINAL_SIZE)){
					viewContainer.originalSize(viewContainer.getCurrentFilters());
				} else if(kind.equals(KindRelatedMenuItem.MANUAL_ZOOM)){
					new ManualZoomDialog(viewContainer);
				}
				
			}

		});
		return zoom;
	}

}
