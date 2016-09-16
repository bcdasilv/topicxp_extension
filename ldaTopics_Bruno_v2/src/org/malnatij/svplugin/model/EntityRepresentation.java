package org.malnatij.svplugin.model;

import org.malnatij.svplugin.XRay;

/**
 * This class represents an entity that will be used by the plug-in.
 * It may be a class, a package a project..
 * @author Jacopo Malnati
 *
 */
public abstract class EntityRepresentation extends XRay{
	public static final String CONCRETE = "concrete";
	public static final String INTERFACE = "interface";
	public static final String ABSTRACT = "abstract"; 
	public static final String EXTERNAL = "external";
	
	// name of the entity (class name, package name, project name..)
	protected String name;
	
	// an entity can be hidden by the user in the visualization
	protected boolean isHidden = false;
	
	
	public EntityRepresentation(String name){
		this.name = name;
	}
	
	
	public String getName(){
		return name;
	}
	
	
	public String toString(){
		String description = "";
		description += "[Entity: " + name + "]";
		description += "\n";
		return description;
	}
	
	public void setHiddenEntity(boolean hidden){
		isHidden = hidden;
	}
	
	public boolean isHiddenEntity(){
		return isHidden;
	}

}
