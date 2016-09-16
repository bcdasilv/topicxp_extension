package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;
import org.malnatij.svplugin.views.actions.menuitems.providers.ZoomItemMenuProvider;

public class ZoomAction extends DropDownImageAction{

	public ZoomAction(final XRayWorkbenchView viewContainer) {
		super("Zoom In/Out/Fit", viewContainer);
		
		setImageDescriptor(getImageDescriptor("plus.gif"));

		createAndSetMenu();
	}

	@Override
	protected void createAndSetMenu() {
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new ZoomItemMenuProvider(viewContainer, menu, "Zoom In Current View",
						"plus.gif", KindRelatedMenuItem.ZOOM_IN).getMenuItem();
				
				new ZoomItemMenuProvider(viewContainer, menu, "Zoom Out Current View",
						"minus.gif", KindRelatedMenuItem.ZOOM_OUT).getMenuItem();
				
				new ZoomItemMenuProvider(viewContainer, menu, "Manual Zoom In/Out",
						"manual.gif", KindRelatedMenuItem.MANUAL_ZOOM).getMenuItem();
				
				new MenuItem(menu, SWT.SEPARATOR);
				
				new ZoomItemMenuProvider(viewContainer, menu, "Fit to the Current View",
						"fit.gif", KindRelatedMenuItem.FIT_SIZE).getMenuItem();
				
				new ZoomItemMenuProvider(viewContainer, menu, "Original size",
						"originalSize.gif", KindRelatedMenuItem.ORIGINAL_SIZE).getMenuItem();
				
				return menu;
			}
			public Menu getMenu(Menu parent) {
				return null;
			}
			public void dispose() {

			}
		});
	}

}
