package org.malnatij.svplugin.mapping;

/**
 * Null mapping, no restrictions on the size of every node
 * @author Jacopo Malnati
 *
 */
public class NullMapping
extends Mapping {

	public double getXScaling() {
		
		return 1;
	}

	public double getYScaling() {
	
		return 1;
	}

}
