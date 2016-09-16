package org.malnatij.svplugin.model.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.malnatij.svplugin.util.Log;

public class TicksPredictor 
extends CoreModel {
	private static int classes = 0;
	
	public static int getNrOfClasses(IProject userSelectedProject){
		IResource[] content;
		try {
			content = userSelectedProject.members();
			for (int j = 0; j < content.length; j++){
				IResource currentContent = content[j];
				int currentType = currentContent.getType();
				switch(currentType){
				case IResource.FILE : fileHandler((IFile) currentContent);
				break;
				case IResource.FOLDER : folderHandler((IFolder) currentContent);
				break;
				default :
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	private static void fileHandler(IFile file){
		String fileExtension = file.getFileExtension();
		if(fileExtension != null && fileExtension.equalsIgnoreCase("java")){
			IJavaElement element = JavaCore.create(file);
			ICompilationUnit compilationUnit = (ICompilationUnit) element;
			IType[] types;
			try {
				types = compilationUnit.getAllTypes();
				classes += types.length;
			} catch (JavaModelException e) {
				Log.printError("Discarded (not available) file: " + file.getName());
			}
		}
	}
	
	private static void folderHandler(IFolder folder){
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
				default :
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

}
