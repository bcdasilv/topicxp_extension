package org.malnatij.svplugin.filters;

import java.util.ArrayList;

import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.util.Log;


public class PackageContentFilter extends Filter{
	private ArrayList<PackageRepresentation> packagesContainer;
	private boolean notInPackage;
	
	/**
	 * @param packageContainer the package container
	 * @param notInPackage true if it matches classes outside the package, false otherwise
	 */
	public PackageContentFilter(
			ArrayList<PackageRepresentation> packageContainer,
			boolean notInPackage){
		this.packagesContainer = packageContainer;
		this.notInPackage = notInPackage;
	}

	@Override
	public boolean matches(EntityRepresentation entity) {

		if(entity instanceof ClassRepresentation){
			return matchesClass((ClassRepresentation)entity);
		} else if(entity instanceof PackageRepresentation){
			return matchesPackage((PackageRepresentation)entity);
		} else {
			Log.printError("Unknown entity to filter out");
			return false;
		}
		
	}
	
	private boolean matchesPackage(PackageRepresentation packageToMatch){
		if(notInPackage){
			return !matchesOnePackageforPackage(packageToMatch);
		} else {
			return matchesOnePackageforPackage(packageToMatch);
		}
		
	}
	
	private boolean matchesClass(ClassRepresentation classToMatch){
		if(notInPackage){
			return !matchesOnePackageForClass(classToMatch);
		} else {
			return matchesOnePackageForClass(classToMatch);
		}
	}
	
	private boolean matchesOnePackageforPackage(PackageRepresentation packageToMatch){
		for(int i = 0; i < packagesContainer.size(); i++){
			
			if(packageToMatch.getName().equals(packagesContainer.get(i).getName())){
				return true;
			}
		}
		return false;
	}
	
	private boolean matchesOnePackageForClass(ClassRepresentation classToMatch){
		for(int i = 0; i < packagesContainer.size(); i++){
		
			if(classToMatch.getPackageContainer().
					equals(packagesContainer.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		String description = "";
		
		if(notInPackage){
			description += "External to: ";
		} else {
			description += "Internal to: ";
		}
		
		for(int i = 0; i < packagesContainer.size(); i++){
			description += packagesContainer.get(i).getName() + " ";
		}
		
		return description;
	}

	
	

	
}
