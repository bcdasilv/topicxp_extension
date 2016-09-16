package org.malnatij.svplugin.model;

/**
 * This is a contained entity, an entity that is contained inside another entity.
 * A package is contained inside a project, a class is contained inside a package
 * @author malnatij
 *
 */
public abstract class ContainedEntityRepresentation extends EntityRepresentation{
	/* an entity may belong to another entity (a class to a package,
	 * a package to a process) */ 
	protected EntityRepresentation container;
	
	public ContainedEntityRepresentation(String name,
			EntityRepresentation container) {
		super(name);
		this.container = container; 
	}
	
	/**
	 * @return the container entity of the current entity
	 */
	public EntityRepresentation getContainer(){
		return container;
	}
	
	public String toString(){
		String description = "";
		description += "[ContainedEntity: " + name + "]";
		description += "{container: " + container.getName() + "}";
		description += "\n";
		return description;
	}

}
