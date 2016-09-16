package org.malnatij.svplugin.views.actions.menuitems.providers;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.filters.PackageContentFilter;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

public class PackageFilterMenuItemProvider extends KindRelatedMenuItem {
	private PackageRepresentation thePackage;

	public PackageFilterMenuItemProvider(XRayWorkbenchView viewContainer,
			Menu menu, String menuName, String iconName, String kind,
			PackageRepresentation thePackage) {
		super(viewContainer, menu, menuName, iconName, kind);
		this.thePackage = thePackage;
		
	}
	
	private String normalizedPackageName(String name){
		if(name.equals("")){
			name = "default package";
		}
		return name;
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem packFilter = new MenuItem(menu, SWT.NONE);
		
		packFilter.setText(normalizedPackageName(menuName));
		packFilter.setImage(getImageDescriptor(iconName).createImage());
		packFilter.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				viewContainer.getCurrentHandler().removePackageFilters();
				ArrayList<PackageRepresentation> packges =
					new ArrayList<PackageRepresentation>();
				packges.add(thePackage);
				
				if(kind.equals(KindRelatedMenuItem.PACKAGE_IN)){
					viewContainer.getCurrentHandler().
					addFilter(new PackageContentFilter(packges, true));
				} else if(kind.equals(KindRelatedMenuItem.PACKAGE_OUT)){
					viewContainer.getCurrentHandler().
					addFilter(new PackageContentFilter(packges, false));
				}
				
				viewContainer.getCurrentHandler().
				refreshView(viewContainer.getCurrentFilters());
			}

		});
		
		return packFilter;
	}

}
