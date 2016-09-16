package org.malnatij.svplugin.model.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import org.eclipse.core.resources.IResource;

public interface IProjectAnalyzer {
	
	public void fileHandler(IFile file);
	
	public void unknownHandler(IResource unknownResource);
	
	public void folderHandler(IFolder folder);
	
	public void analyze();

}
