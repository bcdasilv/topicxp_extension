package org.malnatij.svplugin.views.actions;

import java.util.ArrayList;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;
import org.malnatij.svplugin.views.actions.menuitems.providers.AllPackagesMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.PackageFilterMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.RegularExpressionPackageMenuItemProvider;

public class PackageFilterAction extends DropDownImageAction {

	public PackageFilterAction(XRayWorkbenchView viewContainer) {
		super("Show/Hide Package Content", viewContainer);
		
		setImageDescriptor(getImageDescriptor("filterIn.gif"));

		createAndSetMenu();
	}

	@Override
	protected void createAndSetMenu() {
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new AllPackagesMenuItemProvider(viewContainer, menu).getMenuItem();
				
				new RegularExpressionPackageMenuItemProvider(viewContainer, menu).getMenuItem();
				
				MenuItem listlFilterIn = new MenuItem(menu, SWT.CASCADE);
				listlFilterIn.setText("Single Package Content...");
				listlFilterIn.setImage(getImageDescriptor("filterIn.gif").createImage());
				
				if(viewContainer.getCurrentVisualizationDescription().
						equals(ViewFacade.PACKAGE_DEPENDENCY)){
					listlFilterIn.setEnabled(false);
				}
				
				Menu subMenuFilterIn = new Menu(parent.getShell(), SWT.DROP_DOWN);
				listlFilterIn.setMenu(subMenuFilterIn);
				
				ProjectRepresentation theProject;
				try {
					theProject = viewContainer.getModeledProject();
					ArrayList<PackageRepresentation> packages = theProject.getPackages();
					
					for(int i = 0; i < packages.size(); i++){
						new PackageFilterMenuItemProvider(viewContainer, subMenuFilterIn,
								packages.get(i).getName(), "packageIn.gif",
								KindRelatedMenuItem.PACKAGE_IN, packages.get(i)).getMenuItem();
					}
					
					MenuItem listlFilterOut = new MenuItem(menu, SWT.CASCADE);
					listlFilterOut.setText("Single Package Excluded...");
					listlFilterOut.setImage(getImageDescriptor("filterOut.gif").createImage());
					
					Menu subMenuFilterOut = new Menu(parent.getShell(), SWT.DROP_DOWN);
					listlFilterOut.setMenu(subMenuFilterOut);
					
					for(int i = 0; i < packages.size(); i++){
						new PackageFilterMenuItemProvider(viewContainer, subMenuFilterOut,
								packages.get(i).getName(), "packageOut.gif",
								KindRelatedMenuItem.PACKAGE_OUT, packages.get(i)).getMenuItem();
					}
					
				} catch (ModelNotPreviouslyScheduled e) {
					Log.printError("ModelNotPreviouslyScheduled in createAndSetMenu()");
					e.printStackTrace();
				}
			
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
