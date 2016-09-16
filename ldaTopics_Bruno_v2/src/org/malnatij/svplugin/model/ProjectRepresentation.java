package org.malnatij.svplugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.util.Log;

/**
 * This class represents the project that the plugin is analyzing
 * @author malnatij
 *
 */
public class ProjectRepresentation extends EntityRepresentation{
	// the packages inside the project: [name - PackageRepresentation]
	private HashMap<String, PackageRepresentation> packages =
		new HashMap<String, PackageRepresentation>();
	
	public ProjectRepresentation(String name){
		super(name);
		Log.printProjectRepresentation("A project representation has " +
				"been created: " + name);
	}
	
	public ClassRepresentation getClassRepresentationFor(String className,
			String packageName){
		PackageRepresentation thePackage = packages.get(packageName);
		ClassRepresentation theClass = null;
		if(thePackage != null){
			theClass = 
				thePackage.getClassRepresentation(className);
		}
		return theClass;
	}
	
	/**
	 * This method gets called by a class that wants to be associated to
	 * its package. If the package has already been created, it's returned,
	 * otherwise it's created and returned
	 * @param packageName the name of the required package
	 * @return the wanted package as PackageRepresentation
	 */
	public PackageRepresentation getPackage(String packageName){
		if(packages.containsKey(packageName)){
			return packages.get(packageName);
		} else {
			PackageRepresentation newPackage = new PackageRepresentation(packageName, this);
			packages.put(packageName, newPackage);
			return newPackage;
		}
	}
	
	/**
	 * This method returns a list of all the classes contained in the project
	 * @return all the classes in the project
	 */
	public ArrayList<ClassRepresentation> getAllClasses(){
		ArrayList<ClassRepresentation> allClasses =
			new ArrayList<ClassRepresentation>();
		
		Set<String> keySet = packages.keySet();
		Iterator<String> packageIterator = keySet.iterator();
		
		while(packageIterator.hasNext()){ // while there is a new package
			String currentPackage = packageIterator.next();
			// get the classes content of the package
			ArrayList<ClassRepresentation> packageContent =
				this.packages.get(currentPackage).getClasses();
			// append them to the global list
			for(int i = 0 ; i < packageContent.size(); i++){
				ClassRepresentation currentClass = packageContent.get(i);
				allClasses.add(currentClass);
			}
		}
		return allClasses;
	}
	
	public ArrayList<ClassRepresentation> getRootClasses(ArrayList<Filter> filters){
		ArrayList<ClassRepresentation> rootClasses =
			new ArrayList<ClassRepresentation>();
		
		// get the roots from all the packages
		Set<String> keySet = packages.keySet();
		Iterator<String> packageIterator = keySet.iterator();
		
		while(packageIterator.hasNext()){ // while there is a new package
			String currentPackage = packageIterator.next();
			// get the classes content of the package
			ArrayList<ClassRepresentation> rootsInPackage =
				this.packages.get(currentPackage).getRootsClasses(filters);
			// append them to the global list
			for(int i = 0 ; i < rootsInPackage.size(); i++){
				ClassRepresentation currentClass = rootsInPackage.get(i);
				rootClasses.add(currentClass);
			}
		}
		
		return rootClasses;
	}
	
	public int getMaxLOC(){
		int max = 0;
		
		Set<String> keySet = packages.keySet();
		Iterator<String> packageIterator = keySet.iterator();
		
		while(packageIterator.hasNext()){
			String currentPackageName = packageIterator.next();
			PackageRepresentation currentPackage = packages.get(currentPackageName);
			int currentMaxLOC = currentPackage.getMaxLOC();
			if(currentMaxLOC > max){
				max = currentMaxLOC;
			}
		}
		return max;
	}
	
	public int getMaxNOM(){
		int max = 0;
		
		Set<String> keySet = packages.keySet();
		Iterator<String> packageIterator = keySet.iterator();
		
		while(packageIterator.hasNext()){
			String currentPackageName = packageIterator.next();
			PackageRepresentation currentPackage = packages.get(currentPackageName);
			int currentMaxNOM = currentPackage.getMaxNOM();
			if(currentMaxNOM > max){
				max = currentMaxNOM;
			}
		}
		return max;
	}
	
	/**
	 * String representation
	 */
	public String toString(){
		if(name == null || name.equals("")){
			name = "(default package)";
		}
		
		String description = "";
		description += "\n[PROJECT REPRESENTATION: " + name + "] ";
		int size = getPackages().size();
		description += "#packages: " + size + "\n";
		
		Set<String> keySet = packages.keySet();
		Iterator<String> packageIterator = keySet.iterator();
		
		while(packageIterator.hasNext()){
			String currentPackage = packageIterator.next();
			description += "\n---> PACKAGE: " + currentPackage + "\n";
			
			ArrayList<ClassRepresentation> packageContent =
				packages.get(currentPackage).getClasses();
			
			for(int i = 0 ; i < packageContent.size(); i++){
				ClassRepresentation currentClass = packageContent.get(i);
				description += "Class(" + (i+1) + ")\n";
				description += currentClass.toString() + "\n";
			}
		}
		
		return description;
	}
	
	public String toShortString(){
		 String description = "["+ getName() + "]" +
		" P:" + getPackages().size() + 
		" C:" + getNumberOfClasses() +
		" M:" + getNumberOfmethods() +
		" L:" + getLinesOfCode();
		return description;
	}

	public int getNumberOfClasses() {
		return getAllClasses().size() - getExternalClassesNumber();
	}
	
	private int getExternalClassesNumber(){
		int nr = 0;
		ArrayList<ClassRepresentation> classes = getAllClasses();
		for(int i  = 0; i < classes.size(); i++){
			if(!classes.get(i).isComplete()){
				nr += 1;
			}
		}
		return nr;
	}

	public int getNumberOfmethods() {
		int methods = 0;
		ArrayList<ClassRepresentation> classes = getAllClasses();
		for (int i = 0; i < classes.size(); i++){
			methods += classes.get(i).getNumberOfMethods();
		}
		return methods;
	}

	public int getLinesOfCode() {
		int loc = 0;
		ArrayList<ClassRepresentation> classes = getAllClasses();
		for (int i = 0; i < classes.size(); i++){
			loc += classes.get(i).getLinesOfCode();
		}
		return loc;
	}
	
	public ArrayList<PackageRepresentation> getPackages(){
		ArrayList<PackageRepresentation> packageList = new ArrayList<PackageRepresentation>();
		Set<String> keySet = packages.keySet();
		Iterator<String> packageIterator = keySet.iterator();
		
		while(packageIterator.hasNext()){ // while there is a new package
			String currentPackageName = packageIterator.next();
			PackageRepresentation currentPackage = packages.get(currentPackageName);
			// add just not-empty packages [that basically are external packages]
			if(!currentPackage.isEmpty()){
				packageList.add(currentPackage);
			}
		}
		
		return packageList;
	}

}
