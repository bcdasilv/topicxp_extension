package org.malnatij.svplugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.util.Log;

/**
 * This is a package representation, it holds a set of classes
 * @author malnatij
 *
 */
public class PackageRepresentation extends ContainedEntityRepresentation{
	// the classes contained in this package
	private HashMap<String, ClassRepresentation> classes = 
		new HashMap<String, ClassRepresentation>();
	// dependencies
	private HashMap<PackageRepresentation, Integer> usedPackages =
		new HashMap<PackageRepresentation, Integer>();

	public PackageRepresentation(String name, ProjectRepresentation theProject) {
		super(name, theProject);
		Log.printPackageRepresentation("A new package has been created: " + this);
	}
	
	public void addClass(ClassRepresentation newClass){
		String className = newClass.getName();
		ClassRepresentation wantedClass = classes.get(className);
		
		if(wantedClass != null){
			if(newClass.isComplete()){
				Log.printPackageRepresentation(
						"Adding to pack a concrete instead an incomplete: " 
						+ newClass.getName());
				classes.put(className, newClass);
				return;
			} else {
				// do not overwrite
				return;
			}
		} else {
			Log.printPackageRepresentation("Adding to pack " +
					newClass.getName() + " complete?: " + newClass.isComplete());
			classes.put(className, newClass);
		}
		
	}
	
	public ArrayList<ClassRepresentation> getClasses(){
		Iterator<String> iterator = classes.keySet().iterator();
		ArrayList<ClassRepresentation> listOfClasses = new ArrayList<ClassRepresentation>();
		while(iterator.hasNext()){
			listOfClasses.add(classes.get(iterator.next()));
		}
		return listOfClasses;
	}

	public ArrayList<ClassRepresentation> getRootsClasses(ArrayList<Filter> filters) {
		ArrayList<ClassRepresentation> rootClasses = 
			new ArrayList<ClassRepresentation>();
		
		ArrayList<ClassRepresentation> listOfClasses = getClasses();
		
		int size = listOfClasses.size();
		for(int i = 0; i < size; i++){
			ClassRepresentation currentClass = listOfClasses.get(i);
			if(currentClass.isRoot(filters)){
				rootClasses.add(currentClass);
			}
		}
		
		return rootClasses;
	}
	
	public int getMaxLOC(){
		int max = 0;
		ArrayList<ClassRepresentation> listOfClasses = getClasses();
		
		int size = listOfClasses.size();
		for(int i = 0; i < size; i++){
			int currentLOC = listOfClasses.get(i).getLinesOfCode();
			if (currentLOC > max){
				max = currentLOC;
			}
		}
		return max;
	}
	
	public int getMaxNOM(){
		int max = 0;
		ArrayList<ClassRepresentation> listOfClasses = getClasses();
		
		int size = listOfClasses.size();
		for(int i = 0; i < size; i++){
			int currentNOM = listOfClasses.get(i).getNumberOfMethods();
			if (currentNOM > max){
				max = currentNOM;
			}
		}
		return max;
	}
	
	public String toString(){
		String description = "";
		description += "[Package: " + name + "] ";
		description += "#class:" + classes.size() + "\n";
		
		ArrayList<ClassRepresentation> listOfClasses = getClasses();
		
		int size = listOfClasses.size();
		
		if(size > 25){
			for(int i = 0; i < 25; i++){
				description += "\t" + listOfClasses.get(i).getName() + "\n";
			}
			description += "\t" + "... (" + (size-10) + " more)\n";
		} else {
			for(int i = 0; i < size; i++){
				description += "\t" + listOfClasses.get(i).getName() + "\n";
			}
		}
		
		
		description += " {belonging to: " + container.getName() + "}";
		return description;
	}

	public ClassRepresentation getClassRepresentation(String className) {
		return classes.get(className);
		
		/*
		for(int i = 0 ; i < classes.size(); i++){
			ClassRepresentation currentClass = classes.get(i);
			if(currentClass.getName().equals(className)){
				return currentClass;
			}
		}
		System.out.println("package: [" + this.name + "], class not found: [" + className + "]");
		return null;
		*/
	}

	public HashMap<PackageRepresentation, Integer> getUsedPackages() {
		return usedPackages;
	}
	
	public void addUsedPackage(PackageRepresentation thePackage){
		if(!this.equals(thePackage)){
			if(usedPackages.containsKey(thePackage)){
				int previousWeight = usedPackages.get(thePackage);
				usedPackages.put(thePackage, previousWeight + 1);
			} else {
				usedPackages.put(thePackage, 1);
			}
		}
	}

	public boolean isEmpty() {
		if(classes.size() == 0){
			return true;
		} else {
			int externalClasses = 0;
			ArrayList<ClassRepresentation> listOfClasses = getClasses();
			
			int size = listOfClasses.size();
			for(int i = 0; i < size; i++){
				if(!listOfClasses.get(i).isComplete()){
					externalClasses++;
				}
			}
			if(externalClasses == classes.size()){
				return true;
			}
		}
		return false;
	}


}
