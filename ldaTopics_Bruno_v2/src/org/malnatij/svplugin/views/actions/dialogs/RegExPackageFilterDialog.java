package org.malnatij.svplugin.views.actions.dialogs;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.malnatij.svplugin.filters.PackageContentFilter;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.validators.RegExPackageStringValidator;

public class RegExPackageFilterDialog extends ExtendendInputDialog {
	private static Table table;

	public RegExPackageFilterDialog(XRayWorkbenchView viewContainer) {
		super(Display.getCurrent().getActiveShell(),
				"Enter a Regular Expression",
				"Enter a Regular Expression",
				"",
				new RegExPackageStringValidator(viewContainer),
				viewContainer);
		
		open();
	}
	
	protected Control createDialogArea(Composite parent) {
		   Composite container = getSuperControl(parent);
		   
		   table = new Table (container, SWT.VIRTUAL | SWT.BORDER);
		   table.setItemCount(0);
		   table.setLayoutData(new GridData(600, 300));
		   
		   return container;
	}
	
	public static Table getTable(){
		return table;
	}
	
	public void okPressed(){
		String pattern = getValue().toLowerCase();
		viewContainer.getCurrentHandler().removePackageFilters();
		ArrayList<PackageRepresentation> packges =
			new ArrayList<PackageRepresentation>();
		
		ArrayList<PackageRepresentation> allPackages;
		try {
			allPackages = viewContainer.getModeledProject().getPackages();
			// fill the packages array with matching packages
			for(int i = 0; i < allPackages.size(); i++){
				PackageRepresentation currentPackage = allPackages.get(i);
				if(Pattern.matches(pattern, currentPackage.getName().toLowerCase())){
					packges.add(currentPackage);
				}
			}
			
			viewContainer.getCurrentHandler().
			addFilter(new PackageContentFilter(packges, true));
			
			viewContainer.getCurrentHandler().
			refreshView(viewContainer.getCurrentFilters());
		} catch (ModelNotPreviouslyScheduled e) {
			Log.printError("ModelNotInitializedException in okPressed()");
			e.printStackTrace();
		}
		
		close();
	}
}
