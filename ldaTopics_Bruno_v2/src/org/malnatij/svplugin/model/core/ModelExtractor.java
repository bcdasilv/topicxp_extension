package org.malnatij.svplugin.model.core;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Document;

import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.util.Log;

public class ModelExtractor
extends CoreModel
implements IProjectAnalyzer{
	private ProjectRepresentation theProject;
	private int nrOfAnalyzedClasses = -1;
	private IProject userSelectedProject;
	private IProgressMonitor monitor;
	
	public ModelExtractor(IProject wantedProject, IProgressMonitor monitor){
		this.userSelectedProject = wantedProject;
		this.monitor = monitor;
		analyze();
	}
	
	public ProjectRepresentation getProject(){
		return theProject;
	}

	public void analyze(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		Log.printProjectScanner("Found " + projects.length + 
				" project(s) in the workspace");
		
		IProject currentProject = null;
		
		// if the user manually selected a project..
		if(userSelectedProject != null){
			Log.printProjectScanner("user selected project");
			projectHandler(userSelectedProject);
			
			for(int i = 0; i < projects.length; i++){
				// getting the currentProject, useful for the parser later on
				if(projects[i].getName().equals(userSelectedProject.getName())){
					currentProject = projects[i];
				}
			}
		}
		
		if(nrOfAnalyzedClasses > -1){
			Log.printProjectScanner(nrOfAnalyzedClasses + " classes in the project");
		}
		
		if (currentProject != null){
			// parse
			new DependencyBuilder(currentProject, theProject, monitor);
		} else {
			Log.printError("null project while starting to parse");
		}
		
	}

	public void projectHandler(IProject project){
		String projectName = project.getName();
		Log.printProjectScanner("Project: " + projectName + " (open project)");

		this.theProject = new ProjectRepresentation(projectName);
		try {
			IResource[] content = project.members();
			for (int j = 0; j < content.length; j++){
				IResource currentContent = content[j];
				int currentType = currentContent.getType();
				switch(currentType){
				case IResource.FILE : fileHandler((IFile) currentContent);
				break;
				case IResource.FOLDER : folderHandler((IFolder) currentContent);
				break;
				default : unknownHandler(currentContent);
				}
			}

			/* now that we have complete information about the project,
			 * we can start with the 2nd step (linking child->superclasses)
			 */
			new ClassHierarchyComputer(this.theProject,
					new ArrayList<Filter>(), monitor); // empty filter

		} catch (CoreException e) {
			// Resource /CURRENT is not open. (it has been closed in the meanwhile)
			//e.printStackTrace();
		}
		Log.printProjectScanner("\n");
	}

	public void unknownHandler(IResource unknownResource){
		Log.printProjectScanner("[Unknown] " + unknownResource.getName());
	}
	
	private String normalizeClassNameFromGenerics(String className){
		if(className != null){
			StringTokenizer toc = new StringTokenizer(className, "<");
			String classNormalizedName = "unknown";
			if(toc.hasMoreElements()){
				classNormalizedName = toc.nextToken();
			} else {
				Log.printError("Error in normalizeClassNameFromGenerics for " + className);
			}
			return classNormalizedName.trim();
		} else {
			return className;
		}
		
	}
	
	private String getRelativePath(ICompilationUnit compilationUnit){
		try {
			return compilationUnit.getCorrespondingResource().getFullPath().toOSString();
		} catch (JavaModelException e) {
			//e.printStackTrace();
			Log.printError("unable to retrieve path for :" + compilationUnit.getElementName());
			return "";
		}
	}
	
	private boolean isInnerClass(String currentClassName, String fileName){
		return !currentClassName.equalsIgnoreCase(fileName);
	}
	
	private ClassRepresentation retrieveSuperclassOf(IType type){
		ClassRepresentation intermediateSuperClass =  null;
		
		String currentsSuperClassName;
		try {
			currentsSuperClassName = normalizeClassNameFromGenerics(type.getSuperclassName());
		} catch (JavaModelException e1) {
			//e1.printStackTrace();
			Log.printError("Unable to determine the superclass of:" + type.getElementName());
			return null;
		}
		
		String superClassPackageName = "";
		if (currentsSuperClassName != null) {				
			ITypeHierarchy supertypeHierarchy;
			try {
				supertypeHierarchy = type.newSupertypeHierarchy(null);
			} catch (JavaModelException e1) {
				//e1.printStackTrace();
				Log.printError("Unable to retrieve the supertype hierarchy of the superclass:"
								+ currentsSuperClassName);
				return null;
			}

			IType superClass = null;
			try{
				superClass = supertypeHierarchy.getAllSuperclasses(type)[0];
			}catch(Exception e){
				Log.printError("Unable to get the superclass for: " 
						+ type.getElementName() + " should be: " + currentsSuperClassName);
				//e.printStackTrace();
				return null;
			}
			
			int superFlag = getFlagsForType(superClass);
			superClassPackageName = getPackageNameForType(superClass);

			intermediateSuperClass = new ClassRepresentation(
					currentsSuperClassName, superFlag, theProject
							.getPackage(superClassPackageName));

			Log.printProjectScanner("  (Superclass: " + superClassPackageName
					+ "." + currentsSuperClassName + ")");
		}
		
		return intermediateSuperClass;
	}
	
	private int getNOMforType(IType type){
		try {
			return type.getMethods().length;
		} catch (JavaModelException e) {
			//e.printStackTrace();
			Log.printError("Unable to get the methods of: " + type.getElementName());
			return -1;
		}
	}
	
	private String getPackageNameForType(IType type){
		return type.getPackageFragment().getElementName();
	}
	
	private int getNOFforType(IType type){
		try {
			return type.getFields().length;
		} catch (JavaModelException e) {
			//e.printStackTrace();
			Log.printError("Unable to get the fields of: " + type.getElementName());
			return -1;
		}
	}
	
	private int getFlagsForType(IType type){
		try {
			return type.getFlags();
		} catch (JavaModelException e) {
			//e.printStackTrace();
			Log.printError("Unable to get flags of: " + type.getElementName());
			return -1;
		}
	}
	
	private int getLOCforType(IType type){
		try {
			return new Document(type.getSource()).getNumberOfLines();
		} catch (JavaModelException e) {
			//e.printStackTrace();
			Log.printError("Unable to compute the LOC of: " + type.getElementName());
			return -1;
		}
	}
	
	private void handleType(IType type, String fileName, String relPath){
		// type (class name defined)
		String currentClassName =
			normalizeClassNameFromGenerics(type.getElementName());
				
		monitor.subTask("Extracting the model for class: " + currentClassName);
		
		Log.printProjectScanner("->Class: " + currentClassName);
		
		// is the class an inner class?
		boolean inner = isInnerClass(currentClassName, fileName);
		
		// get the superclass
		ClassRepresentation intermediateSuperClass =  retrieveSuperclassOf(type);
		
		// flags
		int flag = getFlagsForType(type);
		if(flag == -1) return;
		
		// interfaces
		ArrayList<ClassRepresentation> interfacesList = getInterfacesForType(type);
		
		// packages
		String packageName = getPackageNameForType(type);
		Log.printProjectScanner("  Package: " + packageName);
		
		// set and get the package
		PackageRepresentation classPackage = theProject.getPackage(packageName);
		
		// methods
		int numberOfMethods = getNOMforType(type);
		if(numberOfMethods == -1) return;
		
		// fields
		int numberOfFields = getNOFforType(type);
		if(numberOfFields == -1) return;
	
		// lines of code
		int linesOfCode = getLOCforType(type);
		if(linesOfCode == -1) return;
		Log.printProjectScanner("  Lines of code: " + linesOfCode);
		
		// finally I can create a ClassRepresentation with the information
		ClassRepresentation currentClassRep = new ClassRepresentation(
				currentClassName, classPackage, interfacesList,
				intermediateSuperClass,
				linesOfCode, numberOfMethods, numberOfFields, flag,
				relPath, inner);
		
		// then add the class to its package
		classPackage.addClass(currentClassRep);
		nrOfAnalyzedClasses++;
		monitor.worked(1);
	}
	
	private void handleTypesOf(ICompilationUnit compilationUnit, String fileName, String relPath){
		IType[] types;
		try {
			types = compilationUnit.getAllTypes();
		} catch(Exception e){
			Log.printError("Unable to determine the types for: " + compilationUnit.getElementName());
			return;
		}

		// handle each type (that is: each java class inside the compilation unit)
		for (int i = 0; i < types.length; i++) {
			handleType(types[i], fileName, relPath);
		}
	}

	public void fileHandler(IFile file) {
		String fileExtension = file.getFileExtension();
		if (fileExtension != null && fileExtension.equalsIgnoreCase("java")) {
			String fileNameAndExtension = file.getName();
			Log.printProjectScanner("\n[JavaFile] " + fileNameAndExtension);
			String fileName = file.getName().substring(0,fileNameAndExtension.length() - 5);
			IJavaElement element = JavaCore.create(file);
			ICompilationUnit compilationUnit = (ICompilationUnit) element;

			// relative path
			String relPath = getRelativePath(compilationUnit);
			if(relPath.equals("")) return;

			// handle each type in the compilation unit
			handleTypesOf(compilationUnit, fileName, relPath);
		}
	}
	
	private ArrayList<ClassRepresentation> getInterfacesForType(IType type){
		// list of interfaces that will be populated
		ArrayList<ClassRepresentation> interfacesList = new ArrayList<ClassRepresentation>();
			String[] interfaces;
			try {
				interfaces = type.getSuperInterfaceNames();
				for(int a = 0; a < interfaces.length; a++){
					
					ITypeHierarchy superInterfaceHierarchy = type.newSupertypeHierarchy(null);
					String interfacePackageName = "";
					try{
						IType[] supertypes= superInterfaceHierarchy.getAllInterfaces();
						// find the right interface
						for(int i = 0; i < supertypes.length; i++){
							interfacePackageName = "";
							if(interfaces[a].equals(supertypes[i].getElementName())){
								interfacePackageName =
									supertypes[i].getPackageFragment().getElementName();
								break;
							}
						}
					} catch (Exception e){
						Log.printError("Unable to retrieve the " +
								"package or flags of interface: " + interfaces[a]);
					}
					interfacesList.add(new ClassRepresentation(interfaces[a], Flags.AccInterface,
							theProject.getPackage(interfacePackageName)));
				}
			} catch (JavaModelException e) {
				Log.printError("Unable to retrieve the " +
						"interfaces of " + type.getElementName());
			}
		return interfacesList;
	}

	public void folderHandler(IFolder folder){
		//System.out.println("[Folder] " + folder.getName());
		IResource[] folderContent;
		try {
			folderContent = folder.members();
			for(int i = 0; i < folderContent.length; i++){
				IResource currentResource = folderContent[i];
				int currentType = currentResource.getType();
				switch(currentType){
				case IResource.FILE : fileHandler((IFile) currentResource);
				break;
				case IResource.FOLDER : folderHandler((IFolder) currentResource);
				break;
				default : unknownHandler(currentResource);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
	
}