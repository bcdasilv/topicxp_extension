package org.malnatij.svplugin.mapping;

import org.malnatij.svplugin.model.ProjectRepresentation;

/**
 * Mapping project-dependent
 * 
 * @author Jacopo Malnati
 *
 */
public abstract class ProjectDependentMapping extends Mapping{
	
	protected ProjectRepresentation theProject;
	
	
	public ProjectDependentMapping(ProjectRepresentation theProject){
		
		this.theProject = theProject;
	}

}
