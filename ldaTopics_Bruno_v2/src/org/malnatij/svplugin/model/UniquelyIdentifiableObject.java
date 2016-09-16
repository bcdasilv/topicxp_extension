package org.malnatij.svplugin.model;

public interface UniquelyIdentifiableObject {
	
	/**
	 * @return a unique string, for each different object
	 */
	public String getUniqueID();

}
