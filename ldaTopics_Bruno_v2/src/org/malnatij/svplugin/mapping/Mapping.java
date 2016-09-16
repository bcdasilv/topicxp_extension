package org.malnatij.svplugin.mapping;

import org.malnatij.svplugin.XRay;

/**
 * width and height mapping for every node, might be project-related or not
 * @author Jacopo Malnati
 *
 */
public abstract class Mapping extends XRay {

	/**
	 * @return a number that will be multiplied by the actual X of the node 
	 */
	public abstract double getXScaling();
	

	/**
	 * @return a number that will be multiplied by the actual Y of the node 
	 */
	public abstract double getYScaling();

}
