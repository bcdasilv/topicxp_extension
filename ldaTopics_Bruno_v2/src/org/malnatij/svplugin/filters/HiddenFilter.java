package org.malnatij.svplugin.filters;

import org.malnatij.svplugin.model.EntityRepresentation;

/**
 * An entity can be hidden, therefore we don't need to show it
 * to the user.
 * 
 * This is the default filter for every visualization
 * @author Jacopo Malnati
 *
 */
public class HiddenFilter
extends Filter{

	@Override
	public boolean matches(EntityRepresentation entity) {
		
		return entity.isHiddenEntity();
	}
	
	public String toString(){
		
		return "Hidden entities";
	}

	

}
