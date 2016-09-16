package org.malnatij.svplugin.views.actions.dialogs.validators;

import org.malnatij.svplugin.views.actions.StringValidator;

public class ManualZoomStringValidator
extends StringValidator{

	public String isValid(String newText) {
		
		try{
			/**
			 * just try to create a Double out of the representation, 
			 * if it fails, then reject
			 */
			new Double(newText);
		}catch(Exception e){
			
			return "Please, provide a well-formatted number." +
					" (A \"Double\" string represenation of it)";
		}
		
		return null;
	}

	
}
