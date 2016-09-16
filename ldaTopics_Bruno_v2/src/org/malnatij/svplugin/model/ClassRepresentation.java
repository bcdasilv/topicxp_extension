package org.malnatij.svplugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.malnatij.svplugin.filters.Filter;

/**
 * This class represents a "class entity" found in the project that the
 * plugin is analyzing. It holds information about the name and type
 * of class
 * @author malnatij
 *
 */
public class ClassRepresentation 
extends ContainedEntityRepresentation
implements UniquelyIdentifiableObject{
	// every class may implement several INTERFACES
	private ArrayList<ClassRepresentation> interfaces = 
		new ArrayList<ClassRepresentation>();
	
	// every class may extend one class, setting that class as SUPERCLASS
	private ClassRepresentation superClass = null;
	/* Every class may have a set of children. This information is stored
	 * in the children, but in order to speed up the drawing process
	 * this information is also kept by the parent class
	 */
	private ArrayList<ClassRepresentation> children = 
		new ArrayList<ClassRepresentation>();
	
	// every class has a certain size
	private int linesOfCode = -1;
	// every class has a number of methods
	private int numberOfMethods = -1;
	// every class has a number of fields
	private int numberOfFields = -1;
	
	/* A class may be internal to the project (and be complete) or not 
	 * (if it's not internal/complete, it is a superclass of an internal one)
	 */
	private boolean isComplete = false;
	
	// a flag denotes that the class is an abstract class, an interface etc..
	private int flag = -1;
	
	// relpath of the file containing this class
	private String relPath = "";
	
	// a class may be an inner class
	private boolean isInnerClass = false;
	
	// every class may use methods declared in other classes
	// [ClassRepresentation -> int(weight)]
	private HashMap<ClassRepresentation, Integer> usedClasses =
		new HashMap<ClassRepresentation, Integer>();
	
	/**
	 * This constructor is used ONLY while creating a ClassRepresentation
	 * for a superclass (that may or not may be internal to the project)
	 * that will be substituted, during the next step, with its concrete,
	 * complete ClassRepresentation (if internal)
	 */
	public ClassRepresentation(String name, int flag, PackageRepresentation classPackage){
		super(name, classPackage);
		this.flag = flag;
		this.isComplete = false;
		classPackage.addClass(this);
	}
	
	public ClassRepresentation(String name, PackageRepresentation thePackage,
			ArrayList<ClassRepresentation> interfaces, ClassRepresentation superClass,
			int linesOfCode, int nrOfMethods, int nrOfFields, int flag,
			String relPath, boolean isInner){
		super(name, thePackage);
		
		this.interfaces = interfaces;
		this.superClass = superClass;
		this.linesOfCode = linesOfCode;
		this.numberOfMethods = nrOfMethods;
		this.numberOfFields = nrOfFields;
		this.flag = flag;
		this.isComplete = true;
		this.relPath = relPath;
		this.isInnerClass = isInner;
	}
	
	/**
	 * While building a ClassRepresentation, it may be the case that its
	 * superclass has not been discovered yet, therefore an incomplete
	 * class is built with just the name of the class.
	 * This method checks if the current instance of the ClassRepresentation
	 * is complete or not (if not, it has just the name)
	 * @return true is the class is complete
	 */
	public boolean isComplete(){
		return isComplete;
	}
	
	/**
	 * Does this class have a declared superclass?
	 * @return true if this class extends another class
	 */
	public boolean hasSuperClass(ArrayList<Filter> filters){
		if(superClass != null){
			// it has a superclass, let's filter it out
			if(!filtersOut(superClass, filters)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add a child to this class
	 * @param child the child that has this class as superclass
	 */
	public void addChild(ClassRepresentation child){
		children.add(child);
	}
	
	
	public PackageRepresentation getPackageContainer(){
		return (PackageRepresentation) this.container;
	}
	
	public String getSuperClassName(ArrayList<Filter> filters){
		if (hasSuperClass(filters)){
			return superClass.getName();
		} else {
			return null;
		}
		
	}
	
	public void setSuperClass(ClassRepresentation superClass){
		this.superClass = superClass;
	}
	
	private boolean filtersOut(ClassRepresentation classToFilter,
			ArrayList<Filter> filters){
		for(int i = 0; i < filters.size(); i++){
			if(filters.get(i).matches(classToFilter)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasChildren(ArrayList<Filter> filters){
		int childrenCount = children.size();
		if(childrenCount > 0){
			// if there are children, let's see if at least one will not be filtered out
			for(int i = 0; i < childrenCount; i++){
				// check if it fails to filter out at least one children
				if(!filtersOut(children.get(i), filters)){
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<ClassRepresentation> getChildren(ArrayList<Filter> filters){
		ArrayList<ClassRepresentation> filteredChildren =
			new ArrayList<ClassRepresentation>();
		
		for(int i = 0; i < children.size(); i++){
			ClassRepresentation currentChild = children.get(i);
			if(!filtersOut(currentChild, filters)){
				filteredChildren.add(currentChild);
			}
		}
		
		return filteredChildren;
	}
	
	private String getStringForFlags(int flag){
		String result = Flags.toString(flag);
		if(result.equals("")){
			return result;
		}
		return result + " ";
	}
	
	/**
	 * String representation of this object
	 */
	public String toString(){
		String description = "";
		description += "[" + getStringForFlags(flag) + this.getUniqueID() + "]";
		if (this.isComplete()){
			if(superClass != null){
				description += " -> SuperClass " + this.superClass.getName();
				if(this.superClass.isComplete()){
					description += "(INTERNAL)\n";
				} else {
					description += "(EXTERNAL)\n";
				}
			} else {
				description += "\n";
			}
			description += "#methods:" + this.numberOfMethods + " #fields:" + this.numberOfFields +
				" #loc:" + this.linesOfCode;
			
			int nrOfInterfaces = this.interfaces.size();
			if(nrOfInterfaces > 0){
				description += " #interfaces: " + nrOfInterfaces + "\n";
				for(int i = 0; i < interfaces.size(); i++){
					description += "implements: " + interfaces.get(i).getUniqueID() + "\n";
				}
			}
			
			if(isInnerClass()){
				description += " (Internal)";
			}
				
			int nrChildren = children.size();
			if(nrChildren > 0){
				description += "#children: " + nrChildren;
			}
			
			description += "\nFile: " + relPath;
			
			if(usedClasses.size() > 0){
				description += "\nUses:\n";
				
				Set<ClassRepresentation> keySet = usedClasses.keySet();
				Iterator<ClassRepresentation> classIterator = keySet.iterator();
				
				int i = 0;
				while(classIterator.hasNext()){
					i++;
					ClassRepresentation currentClass = classIterator.next();
					int weigth = usedClasses.get(currentClass);
					description += "\t" + currentClass.getName() + "(" + weigth + ")";
					if(i < usedClasses.size()){
						description += "\n";
					}
				}

			}
		}
		
		return description;
	}
	
	public boolean isInnerClass(){
		return isInnerClass;
	}
	
	public boolean isInterface(){
		return Flags.isInterface(flag);
	}
	
	public boolean hasInterfaces(){
		return interfaces.size() > 0;
	}

	/**
	 * In a system complexity view, if a class doesn't have a superclass,
	 * it's a root
	 * @return true if the class would be a root in a system complexity view
	 */
	public boolean isRoot(ArrayList<Filter> filters) {
		return !hasSuperClass(filters);
	}

	public int getLinesOfCode() {
		return linesOfCode;
	}

	public int getNumberOfFields() {
		return numberOfFields;
	}

	public int getNumberOfMethods() {
		return numberOfMethods;
	}
	
	public int getFlag(){
		return flag;
	}
	
	public ClassRepresentation getSuperclass(){
		return superClass;
	}
	
	public String getRelpath(){
		return relPath;
	}
	
	public ArrayList<ClassRepresentation> getImplementedClass(){
		// TODO check for filters after calling this method
		return interfaces;
	}
	
	public boolean hasDependencies(){
		// TODO check for filters after calling this method
		return usedClasses.size() > 0;
	}
	
	public void addUsedClass(ClassRepresentation usedClass){
		if(!usedClass.getName().equals(this.getName())){
			if(usedClasses.containsKey(usedClass)){
				// new use
				int previousWeigth = usedClasses.get(usedClass);
				usedClasses.put(usedClass, previousWeigth + 1);
			} else {
				// first use
				usedClasses.put(usedClass, 1);
			}
			
			// now add to the package of this class, the usage of the package of the other class
			((PackageRepresentation)container).addUsedPackage(usedClass.getPackageContainer());
		}
	}

	public HashMap<ClassRepresentation, Integer> getUsedClasses() {
		// TODO check for filters after calling this method
		return usedClasses;
	}
	
	public boolean equals(ClassRepresentation otherClass){
		return this.getName().equals(otherClass.getName()) &&
		this.getPackageContainer().getName().equals(
				otherClass.getPackageContainer().getName());
		
	}
	
	public boolean dependsOn(ClassRepresentation theClass){
		return usedClasses.get(theClass) != null;
	}
	
	public int getDependencyWeightFor(ClassRepresentation theClass){
		if(dependsOn(theClass)){
			return usedClasses.get(theClass);
		} else {
			return 0;
		}
	}

	public boolean is(String kind) {
		if(kind.equals(EntityRepresentation.ABSTRACT)){
			if(isComplete && Flags.isAbstract(flag)){
				return true;
			}
		} else if(kind.equals(EntityRepresentation.INTERFACE)){
			if(Flags.isInterface(flag)){
				return true;
			}
		} else if(kind.equals(EntityRepresentation.EXTERNAL)){
			if(!isComplete){
				return true;
			}
		} else if(kind.equals(EntityRepresentation.CONCRETE)){
			if(isComplete &&
					!Flags.isAbstract(flag) &&
					!Flags.isInterface(flag)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns package.class that uniquely identifies a class within the system
	 */
	public String getUniqueID() {
		if(getPackageContainer().getName().equals("")){
			return getName();
		}
		return getPackageContainer().getName() + "." + getName();
	}
	
}
