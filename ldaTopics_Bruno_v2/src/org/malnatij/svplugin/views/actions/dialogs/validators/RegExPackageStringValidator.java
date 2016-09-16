package org.malnatij.svplugin.views.actions.dialogs.validators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.StringValidator;
import org.malnatij.svplugin.views.actions.dialogs.RegExPackageFilterDialog;

public class RegExPackageStringValidator extends StringValidator{
	private XRayWorkbenchView viewContainer;
	
	public RegExPackageStringValidator(XRayWorkbenchView viewContainer){
		this.viewContainer = viewContainer;
	}

	public String isValid(String newText) {
		if(isEmptyText(newText)){
			RegExPackageFilterDialog.getTable().removeAll();
			return "Insert a non empty Regular Expression, please.";
		}
		
		return checkAndUpdateTable(newText);
	}
	
	private String checkAndUpdateTable(String newText){
		try{
			RegExPackageFilterDialog.getTable().removeAll();
			ArrayList<String> names = getMatchingPackagesNames(newText);
			
			for(int i = 0; i < names.size(); i++){
				addTableItem(names.get(i));
			}
			
		} catch (Exception e){
			RegExPackageFilterDialog.getTable().removeAll();
			return "Invalid Regular Expression, see the Help panel.";
		}
		
		return null;
	}
	
	private ArrayList<String> getMatchingPackagesNames(String newText){
		ArrayList<String> names = new ArrayList<String>();
		
		String pattern = newText.toLowerCase();
		viewContainer.getCurrentHandler().removePackageFilters();
		
		ArrayList<PackageRepresentation> allPackages;
		try {
			allPackages = viewContainer.getModeledProject().getPackages();
			// fill the packages array with matching packages
			for(int i = 0; i < allPackages.size(); i++){
				PackageRepresentation currentPackage = allPackages.get(i);
				if(Pattern.matches(pattern, currentPackage.getName().toLowerCase())){
					names.add(currentPackage.getName());
				}
			}
		} catch (ModelNotPreviouslyScheduled e) {
			// should never happen
			e.printStackTrace();
		}	
		
		Collections.sort(names);
		
		return names;
	}
	
	private  void addTableItem(String itemName){
		TableItem item = 
			new TableItem(RegExPackageFilterDialog.getTable(), SWT.NONE);
		item.setText(itemName);
	}

}
