package org.malnatij.svplugin.model.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.util.Log;

public class ClassHierarchyComputer
extends CoreModel {
	private ProjectRepresentation theProject;
	private ArrayList<ClassRepresentation> listOfClasses;
	// map representation of listOfClasses, just for quick access
	private HashMap<String, ClassRepresentation> mapOfClasses =
		new HashMap<String, ClassRepresentation>();
	// list of classes who have superclass
	private ArrayList<ClassRepresentation> listOfChildren =
		new ArrayList<ClassRepresentation>();
	private ArrayList<Filter> filters;
	private IProgressMonitor monitor;
	
	public ClassHierarchyComputer(ProjectRepresentation theProject,
			ArrayList<Filter> filters,
			IProgressMonitor monitor){
		this.theProject = theProject;
		this.filters = filters;
		this.monitor = monitor;
		Log.classHierarchyComputer("ClassHierarchyComputer has been created");
		run();
	}
	
	public void run(){
		this.setUp(filters);
	}
	
	/**
	 * Fill the list of classes
	 */
	private void setListOfClasses(){
		listOfClasses = theProject.getAllClasses();
		/*
		Log.printParentChildLinker("Set a list of " + listOfClasses.size() + " classes");
		for(int i = 0; i < listOfClasses.size(); i++){
			Log.printParentChildLinker("Class:(" + i + ") " + listOfClasses.get(i).getName());
		}
		*/
	}
	
	/**
	 * Fill the map of classes, for easier lookup
	 */
	private void setMapOfClasses(){
		int size = this.listOfClasses.size();
		for(int i = 0; i < size; i++){
			ClassRepresentation currentClass = listOfClasses.get(i);
			
			// now fill the map (ID = key, ClassRepresentation = value)
			String ID = currentClass.getUniqueID();
			
			// class already there
			if (mapOfClasses.containsKey(ID)) {
				// new class is incomplete
				if (!currentClass.isComplete()) {
					// do nothing
					Log.classHierarchyComputer("Duplicated but not complete: "
							+ ID + ", trashed");
				} else { // class is complete
					// override the previous one with the complete one
					mapOfClasses.put(ID, currentClass);
				}
			} else {
				// class was not there, add it
				mapOfClasses.put(ID, currentClass);
				Log.classHierarchyComputer("Added: " + ID + ", complete?: "
						+ currentClass.isComplete());
			}
			
		}
		Log.classHierarchyComputer("Set a map of " + mapOfClasses.size() +
				" classes");
	}
	
	private void assignSuperclass(ClassRepresentation child, ArrayList<Filter> filters){		
		String superClassID = child.getSuperclass().getUniqueID();
		
		// now let's find the real complete instance of that class
		if(mapOfClasses.containsKey(superClassID)){
			//Log.printParentChildLinker("Superclass found (" + superClassName + ")");
			// there is an internal, complete class
			ClassRepresentation parent = mapOfClasses.get(superClassID);
			// link the child and the parent
			child.setSuperClass(parent);
			// add the child to the list of children in the parent
			parent.addChild(child);
			Log.classHierarchyComputer("Assigned Superclass (" +
					superClassID + ") to " + child.getName());
		} else {
			// the superclass is external, delete that
			Log.classHierarchyComputer("NO SUPERCLASS FOUND (" +
					superClassID + ")");
		}
	}
	
	/**
	 * This method is responsible for linking REAL, CONCRETE superclasses
	 * to children (so far, they just had an incomplete superclass 
	 * representation)
	 * It will also contribute to set the list of children in every superclass
	 */
	private void linkEntities(ArrayList<Filter> filters){
		int size = listOfClasses.size();
		for(int i = 0; i < size; i++){
			ClassRepresentation currentClass = listOfClasses.get(i);
			if(currentClass.hasSuperClass(filters)){
				listOfChildren.add(currentClass);
			}
		}
		
		/*
		Log.printParentChildLinker("Found " + listOfChildren.size()
				+ " children");
		*/
		
		// now for every child, let's find the complete superclass(if any)
		int numberOfChildren = 1;
		if(listOfChildren.size() > numberOfChildren){
			numberOfChildren = listOfChildren.size();
		}
		
		int step = listOfClasses.size() / numberOfChildren;
		size = listOfChildren.size();
		for(int i = 0; i < size; i++){
			monitor.subTask("Creating hierarchy for class: " + listOfChildren.get(i).getName());
			assignSuperclass(listOfChildren.get(i), filters);
			monitor.worked(step);
		}
		
		Log.classHierarchyComputer("\nPROJECT after the LINKING:\n" + theProject.toString());
		Log.classHierarchyComputer("MaxLOC:" + theProject.getMaxLOC() +
				" MaxNOM:" + theProject.getMaxNOM());
	}
	
	/**
	 * Setup the whole environment that will be used while linking
	 * children with their superclasses
	 */
	private void setUp(ArrayList<Filter> filters){
		// set the list of all classes
		setListOfClasses();
		// set the map of all classes
		setMapOfClasses();
		// link children with parents and viceversa
		linkEntities(filters);
	}

}
