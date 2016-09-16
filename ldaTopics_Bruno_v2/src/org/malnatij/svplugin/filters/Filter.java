package org.malnatij.svplugin.filters;

import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.model.EntityRepresentation;

/**
 * This is a filter, it matches a given entity depending on
 * the implementation of the filter subclass
 * @author Jacopo Malnati
 *
 */
public abstract class Filter
extends XRay{
	
	/**
	 * 
	 * @param entity and entity to filter
	 * @return true if the entity must be filtered OUT
	 * I.E.: if the filter is HIDDEN, then all hidden entities will
	 * match the HIDDEN FILTER.
	 */
	public abstract boolean matches(EntityRepresentation entity);

	
}
