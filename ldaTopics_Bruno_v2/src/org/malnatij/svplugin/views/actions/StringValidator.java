package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.dialogs.IInputValidator;
import org.malnatij.svplugin.XRay;

/**
 * This is a generic string validator
 * @author Jacopo Malnati
 *
 */
public abstract class StringValidator
extends XRay
implements IInputValidator{
	
	
	/**
	 * Just a simple function to check if the input is empty
	 * @param text the string to check
	 * @return true if the string doesn't have any char
	 */
	protected boolean isEmptyText(String text){
		return text.trim() // remove white spaces
		.length() == 0; // check the length
	}

	
}
