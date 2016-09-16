package org.malnatij.svplugin.filters;

import org.malnatij.svplugin.model.EntityRepresentation;

/**
 * Null Filter, always return false (that is: this entity should not be filtered out)
 * This is a convenient filter, probably it won't last longer due to future refactoring
 */
public class NullFilter extends Filter {

	@Override
	public boolean matches(EntityRepresentation entity) {
		return false;
	}
	
	public String toString(){
		return "Null Filter";
	}

}
