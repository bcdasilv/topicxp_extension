package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectViewMenuItemProvider;

public class SelectViewAction extends DropDownImageAction{
	
	public SelectViewAction(final XRayWorkbenchView viewContainer) {
		super("Select Polymetric View", viewContainer);
		setImageDescriptor(getImageDescriptor("tree.gif"));

		createAndSetMenu();
	}
	
	protected void createAndSetMenu(){
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new SelectViewMenuItemProvider(viewContainer, menu,
						"System Complexity",
						"tree.gif",
						ViewFacade.SYSTEM_COMPLEXITY
						).getMenuItem();
				
				new SelectViewMenuItemProvider(viewContainer, menu,
						"Class Dependency",
						"classDependencyView.gif",
						ViewFacade.CLASS_DEPENDENCY
						).getMenuItem();
				
				new SelectViewMenuItemProvider(viewContainer, menu,
						"Package Dependency",
						"packageDependencyView.gif",
						ViewFacade.PACKAGE_DEPENDENCY
						).getMenuItem();
				
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
